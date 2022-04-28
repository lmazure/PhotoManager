package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.PhotoListMetaDataEvent;
import lmzr.photomngr.data.PhotoListMetaDataListener;
import lmzr.photomngr.data.PhotoProvider;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.photomngr.ui.action.DisplayMapAction;
import lmzr.photomngr.ui.action.StartPlayerAction;
import lmzr.photomngr.ui.mapdisplayer.MapURICreatorFactory;
import lmzr.photomngr.ui.mapdisplayer.MapURICreator;
import lmzr.photomngr.ui.player.Player;
import lmzr.photomngr.ui.player.PlayerFactory;

/**
 * @author Laurent Mazuré
 *
 */
public class PhotoNavigator extends JFrame
                            implements ListSelectionListener, TableModelListener, PhotoListMetaDataListener, PhotoProvider {

    final private static DateFormat s_dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
    final private static DateFormat s_timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

    final private ListSelectionManager a_selection;
    final private PhotoList a_photoList;
    final private GPSDatabase a_GPSDatabase;
    final private JComboBox<String> a_folder;
    final private JButton a_nextPhoto;
    final private JButton a_previousPhoto;
    private boolean a_folderIsDisabled;
    final private JLabel a_file;
    final private JLabel a_dateTime;
    final private JLabel a_map;
    final private JButton[] a_mapDisplayers;
    final private JButton[] a_play;
    private int[] a_previousSelection;


    /**
     * @param photoList
     * @param GPSDatabase
     * @param selection
     */
    public PhotoNavigator(final PhotoList photoList,
                          final GPSDatabase GPSDatabase,
                          final ListSelectionManager selection) {
        super();
        this.a_photoList = photoList;
        this.a_GPSDatabase = GPSDatabase;
        this.a_selection = selection;
        this.a_previousSelection = null;
        this.a_photoList.addTableModelListener(this);
        this.a_selection.addListener(this);
        this.a_photoList.addMetaListener(this);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        this.a_folder = new JComboBox<>();
        this.a_folder.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_folder.getPreferredSize().height));
        updateFolderList();
        panel.add(this.a_folder);
        this.a_folder.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.a_folderIsDisabled = false;
        this.a_folder.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoNavigator.this.a_folderIsDisabled) return;
                        for (int i=0; i<PhotoNavigator.this.a_photoList.getRowCount(); i++) {
                            final String f = PhotoNavigator.this.a_photoList.getPhoto(i).getFolder();
                            if (f.equals(PhotoNavigator.this.a_folder.getSelectedItem())) {
                                final int v [] = new int[1];
                                v[0] = i;
                                PhotoNavigator.this.a_selection.setSelection(v);
                                return;
                            }}}});

        final JPanel navigator = new JPanel();
        this.a_file = new JLabel();
        this.a_nextPhoto = new JButton("next");
        this.a_nextPhoto.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) { PhotoNavigator.this.a_selection.next(1);}});
        this.a_previousPhoto = new JButton("prev.");
        this.a_previousPhoto.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed( final ActionEvent e) { PhotoNavigator.this.a_selection.previous(1);}});
        navigator.add(this.a_file);
        navigator.add(this.a_previousPhoto);
        navigator.add(this.a_nextPhoto);
        panel.add(navigator);
        navigator.setAlignmentX(Component.LEFT_ALIGNMENT);
        navigator.setMaximumSize(new Dimension(Integer.MAX_VALUE,navigator.getPreferredSize().height));

        this.a_dateTime = new JLabel();
        this.a_dateTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(this.a_dateTime);
        this.a_dateTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JPanel viewerButtons = new JPanel();

        final PlayerFactory playerFactory = new PlayerFactory();
        final Player[] players = playerFactory.getPlayers();
        this.a_play = new JButton[players.length];
        for (int i=0; i<players.length; i++) {
            this.a_play[i] = new JButton(new StartPlayerAction( players[i].getName(),
                                                           KeyEvent.CHAR_UNDEFINED,
                                                           null,
                                                           "start "+players[i].getName(),
                                                           this,
                                                           players[i]));
            viewerButtons.add(this.a_play[i]);
            this.a_play[i].setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        viewerButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewerButtons.setBorder(BorderFactory.createTitledBorder("external viewers"));
        viewerButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE,viewerButtons.getPreferredSize().height));

        panel.add(viewerButtons);

        final JPanel mapFull = new JPanel();
        mapFull.setLayout(new BoxLayout(mapFull, BoxLayout.Y_AXIS));

        this.a_map = new JLabel();
        mapFull.add(this.a_map);

        final JPanel mapButtons = new JPanel();
        final MapURICreatorFactory mapURICreatorFactory = new MapURICreatorFactory();
        final MapURICreator[] mapURICreators = mapURICreatorFactory.getMapDisplayers();
        this.a_mapDisplayers = new JButton[mapURICreators.length];
        for (int i=0; i<mapURICreators.length; i++) {
            final String siteName = mapURICreators[i].getName();
            this.a_mapDisplayers[i] = new JButton(new DisplayMapAction(siteName,
                                                                  KeyEvent.CHAR_UNDEFINED,
                                                                  null,
                                                                  "display " + siteName + " map",
                                                                  this.a_GPSDatabase,
                                                                  this.a_photoList,
                                                                  this.a_selection,
                                                                  mapURICreators[i]));
            mapButtons.add(this.a_mapDisplayers[i]);
        }
        mapButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        mapButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE,mapButtons.getPreferredSize().height));

        mapFull.add(mapButtons);
        mapButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        mapFull.setBorder(BorderFactory.createTitledBorder("map"));
        panel.add(mapFull);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        update();

        pack();
    }

    /**
     *
     */
    private void update() {

        final int selection[] = this.a_selection.getSelection();
        if (Arrays.equals(this.a_previousSelection,selection)) return;
        this.a_previousSelection = selection;

        // buttons to start external player
        for (int i=0; i<this.a_play.length; i++) {
            final StartPlayerAction action = (StartPlayerAction)this.a_play[i].getAction();
            action.setEnabled( (selection.length==1) &&
                               action.getPlayer().isFormatSupported(this.a_photoList.getPhoto(selection[0]).getFormat()) );
        }

        // name of current photo
        if ( selection.length == 1 ) {
            this.a_file.setText(this.a_photoList.getPhoto(selection[0]).getFilename());
        } else {
            this.a_file.setText(" ");
        }

        // buttons next/previous
        if ( selection.length == 0 ) {
            // no image selected
            // -> disable the next and previous buttons
            this.a_nextPhoto.setEnabled(false);
            this.a_previousPhoto.setEnabled(false);
        } else {
            // at least one selected image
            // -> enable the next and previous buttons
            this.a_nextPhoto.setEnabled(true);
            this.a_previousPhoto.setEnabled(true);
        }

        // folder list
        if ( selection.length == 0 ) {
            this.a_folder.setVisible(false);
        } else {
            final String firstFolder = this.a_photoList.getPhoto(selection[0]).getFolder();
            boolean allInSameFolder = true;
            for (int i=1;
                 (i<this.a_selection.getSelection().length) && allInSameFolder;
                 i++) {
                final String folder = this.a_photoList.getPhoto(selection[i]).getFolder();
                allInSameFolder &= folder.equals(firstFolder);
            }
            if (allInSameFolder) {
                this.a_folderIsDisabled = true;
                this.a_folder.setSelectedItem(firstFolder);
                this.a_folderIsDisabled = false;
                this.a_folder.setVisible(true);
            } else {
                this.a_folder.setVisible(false);
            }
        }


        if ( selection.length!=1 ) {
            // zero or more than one image is selected
            // -> empty the text fields and disable all the fields (except previous and next if at least one
            this.a_map.setText("");
            for ( JButton b: this.a_mapDisplayers) b.setEnabled(false);
            this.a_dateTime.setText("");
            pack();
            return;
        }


        final Photo photo = this.a_photoList.getPhoto(selection[0]);
        final Date date = photo.getHeaderData().getDate();
        if (date != null ) {
            final String d = s_dateFormat.format(date);
            final String t = s_timeFormat.format(date);
            this.a_dateTime.setText(d+" - "+t);
        } else {
            this.a_dateTime.setText(" ");
        }
        //TODO the code below is stupid and broken
        final String location = photo.getIndexData().getLocation().toLongString();
        final GPSRecord gps = this.a_GPSDatabase.getGPSData(photo.getIndexData().getLocation());
        if (location!=null) {
            if ( gps != null && gps.getGPSData().isComplete() ) {
                this.a_map.setText("map "+gps.getLocation().toString());
                for ( JButton b: this.a_mapDisplayers) b.setEnabled(true);
            } else {
                this.a_map.setText("no map for "+location);
                for ( JButton b: this.a_mapDisplayers) b.setEnabled(false);
            }
        } else {
            this.a_map.setText("map");
            for ( JButton b: this.a_mapDisplayers) b.setEnabled(false);
        }

        pack();
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e) {

        if (e.getValueIsAdjusting()) {
            // event compression -> only the last one is taken into account
            return;
        }

        update();
    }

    /**
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    @Override
    public void tableChanged(final TableModelEvent e) {
        if ( e.getType()==TableModelEvent.INSERT || e.getType()==TableModelEvent.DELETE ) {
            updateFolderList();
        }
        update();
    }

    /**
     *
     */
    private void updateFolderList() {
        this.a_folderIsDisabled = true;
        this.a_folder.removeAllItems();
        final Vector<String> folderList = new Vector<>();
        for (int i=0; i<this.a_photoList.getRowCount(); i++) {
            if ( !folderList.contains(this.a_photoList.getPhoto(i).getFolder()))
                folderList.add(this.a_photoList.getPhoto(i).getFolder());
        }
        for (Iterator<String> i=folderList.iterator(); i.hasNext(); ) {
            final String f = i.next();
            this.a_folder.addItem(f);
        }
        this.a_folderIsDisabled = false;
    }

    /**
     * @see lmzr.photomngr.data.PhotoListMetaDataListener#photoListMetaDataChanged(lmzr.photomngr.data.PhotoListMetaDataEvent)
     */
    @Override
    public void photoListMetaDataChanged(final PhotoListMetaDataEvent e) {
        if (e.getChange()==PhotoListMetaDataEvent.FILTER_HAS_CHANGED) updateFolderList();
    }

    /**
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {

        super.dispose();

        this.a_photoList.removeTableModelListener(this);
        this.a_selection.removeListener(this);
        this.a_photoList.removeMetaListener(this);
    }

    /**
     * @see lmzr.photomngr.data.PhotoProvider#getPhoto()
     * @return photo currently displayed
     */
    @Override
    public Photo getPhoto()
    {
        return this.a_photoList.getPhoto(this.a_selection.getSelection()[0]);
    }
}
