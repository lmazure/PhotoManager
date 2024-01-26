package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
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
import lmzr.photomngr.ui.mapdisplayer.MapURICreator;
import lmzr.photomngr.ui.mapdisplayer.MapURICreatorFactory;
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
        a_photoList = photoList;
        a_GPSDatabase = GPSDatabase;
        a_selection = selection;
        a_previousSelection = null;
        a_photoList.addTableModelListener(this);
        a_selection.addListener(this);
        a_photoList.addMetaListener(this);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        a_folder = new JComboBox<>();
        a_folder.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_folder.getPreferredSize().height));
        updateFolderList();
        panel.add(a_folder);
        a_folder.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_folderIsDisabled = false;
        a_folder.addActionListener(
                e -> {
                    if (a_folderIsDisabled) {
                        return;
                    }
                    for (int i=0; i<a_photoList.getRowCount(); i++) {
                        final String f = a_photoList.getPhoto(i).getFolder();
                        if (f.equals(a_folder.getSelectedItem())) {
                            final int v [] = new int[1];
                            v[0] = i;
                            a_selection.setSelection(v);
                            return;
                        }}});

        final JPanel navigator = new JPanel();
        a_file = new JLabel();
        a_nextPhoto = new JButton("next");
        a_nextPhoto.addActionListener(
                e -> a_selection.next(1));
        a_previousPhoto = new JButton("prev.");
        a_previousPhoto.addActionListener(
                e -> a_selection.previous(1));
        navigator.add(a_file);
        navigator.add(a_previousPhoto);
        navigator.add(a_nextPhoto);
        panel.add(navigator);
        navigator.setAlignmentX(Component.LEFT_ALIGNMENT);
        navigator.setMaximumSize(new Dimension(Integer.MAX_VALUE,navigator.getPreferredSize().height));

        a_dateTime = new JLabel();
        a_dateTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(a_dateTime);
        a_dateTime.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JPanel viewerButtons = new JPanel();

        final Player[] players = PlayerFactory.getPlayers();
        a_play = new JButton[players.length];
        for (int i=0; i<players.length; i++) {
            a_play[i] = new JButton(new StartPlayerAction( players[i].getName(),
                                                           KeyEvent.CHAR_UNDEFINED,
                                                           null,
                                                           "start "+players[i].getName(),
                                                           this,
                                                           players[i]));
            viewerButtons.add(a_play[i]);
            a_play[i].setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        viewerButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewerButtons.setBorder(BorderFactory.createTitledBorder("external viewers"));
        viewerButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE,viewerButtons.getPreferredSize().height));

        panel.add(viewerButtons);

        final JPanel mapFull = new JPanel();
        mapFull.setLayout(new BoxLayout(mapFull, BoxLayout.Y_AXIS));

        a_map = new JLabel();
        mapFull.add(a_map);

        final JPanel mapButtons = new JPanel();
        final MapURICreator[] mapURICreators = MapURICreatorFactory.getMapDisplayers();
        a_mapDisplayers = new JButton[mapURICreators.length];
        for (int i=0; i<mapURICreators.length; i++) {
            final String siteName = mapURICreators[i].getName();
            a_mapDisplayers[i] = new JButton(new DisplayMapAction(siteName,
                                                                  KeyEvent.CHAR_UNDEFINED,
                                                                  null,
                                                                  "display " + siteName + " map",
                                                                  a_GPSDatabase,
                                                                  a_photoList,
                                                                  a_selection,
                                                                  mapURICreators[i]));
            mapButtons.add(a_mapDisplayers[i]);
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

        final int selection[] = a_selection.getSelection();
        if (Arrays.equals(a_previousSelection,selection)) {
            return;
        }
        a_previousSelection = selection;

        // buttons to start external player
        for (final JButton element : a_play) {
            final StartPlayerAction action = (StartPlayerAction)element.getAction();
            action.setEnabled( (selection.length==1) &&
                               action.getPlayer().isFormatSupported(a_photoList.getPhoto(selection[0]).getFormat()) );
        }

        // name of current photo
        if ( selection.length == 1 ) {
            a_file.setText(a_photoList.getPhoto(selection[0]).getFilename());
        } else {
            a_file.setText(" ");
        }

        // buttons next/previous
        if ( selection.length == 0 ) {
            // no image selected
            // -> disable the next and previous buttons
            a_nextPhoto.setEnabled(false);
            a_previousPhoto.setEnabled(false);
        } else {
            // at least one selected image
            // -> enable the next and previous buttons
            a_nextPhoto.setEnabled(true);
            a_previousPhoto.setEnabled(true);
        }

        // folder list
        if ( selection.length == 0 ) {
            a_folder.setVisible(false);
        } else {
            final String firstFolder = a_photoList.getPhoto(selection[0]).getFolder();
            boolean allInSameFolder = true;
            for (int i=1;
                 (i<a_selection.getSelection().length) && allInSameFolder;
                 i++) {
                final String folder = a_photoList.getPhoto(selection[i]).getFolder();
                allInSameFolder &= folder.equals(firstFolder);
            }
            if (allInSameFolder) {
                a_folderIsDisabled = true;
                a_folder.setSelectedItem(firstFolder);
                a_folderIsDisabled = false;
                a_folder.setVisible(true);
            } else {
                a_folder.setVisible(false);
            }
        }


        if ( selection.length!=1 ) {
            // zero or more than one image is selected
            // -> empty the text fields and disable all the fields (except previous and next if at least one
            a_map.setText("");
            for ( final JButton b: a_mapDisplayers) {
                b.setEnabled(false);
            }
            a_dateTime.setText("");
            pack();
            return;
        }


        final Photo photo = a_photoList.getPhoto(selection[0]);
        final Date date = photo.getHeaderData().getDate();
        if (date != null ) {
            final String d = s_dateFormat.format(date);
            final String t = s_timeFormat.format(date);
            a_dateTime.setText(d+" - "+t);
        } else {
            a_dateTime.setText(" ");
        }
        //TODO the code below is stupid and broken
        final String location = photo.getIndexData().getLocation().toLongString();
        final GPSRecord gps = a_GPSDatabase.getGPSData(photo.getIndexData().getLocation());
        if (location!=null) {
            if ( gps != null && gps.getGPSData().isComplete() ) {
                a_map.setText("map "+gps.getLocation().toString());
                for ( final JButton b: a_mapDisplayers) {
                    b.setEnabled(true);
                }
            } else {
                a_map.setText("no map for "+location);
                for ( final JButton b: a_mapDisplayers) {
                    b.setEnabled(false);
                }
            }
        } else {
            a_map.setText("map");
            for ( final JButton b: a_mapDisplayers) {
                b.setEnabled(false);
            }
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
        a_folderIsDisabled = true;
        a_folder.removeAllItems();
        final Vector<String> folderList = new Vector<>();
        for (int i=0; i<a_photoList.getRowCount(); i++) {
            if ( !folderList.contains(a_photoList.getPhoto(i).getFolder())) {
                folderList.add(a_photoList.getPhoto(i).getFolder());
            }
        }
        for (String f : folderList) {
            a_folder.addItem(f);
        }
        a_folderIsDisabled = false;
    }

    /**
     * @see lmzr.photomngr.data.PhotoListMetaDataListener#photoListMetaDataChanged(lmzr.photomngr.data.PhotoListMetaDataEvent)
     */
    @Override
    public void photoListMetaDataChanged(final PhotoListMetaDataEvent e) {
        if (e.getChange()==PhotoListMetaDataEvent.FILTER_HAS_CHANGED) {
            updateFolderList();
        }
    }

    /**
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {

        super.dispose();

        a_photoList.removeTableModelListener(this);
        a_selection.removeListener(this);
        a_photoList.removeMetaListener(this);
    }

    /**
     * @see lmzr.photomngr.data.PhotoProvider#getPhoto()
     * @return photo currently displayed
     */
    @Override
    public Photo getPhoto()
    {
        return a_photoList.getPhoto(a_selection.getSelection()[0]);
    }
}
