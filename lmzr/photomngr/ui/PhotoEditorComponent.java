package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.PhotoListMetaDataEvent;
import lmzr.photomngr.data.PhotoListMetaDataListener;
import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.ui.action.PhotoManagerAction;
import lmzr.photomngr.ui.celleditor.AuthorCellEditor;
import lmzr.photomngr.ui.celleditor.CopiesCellEditor;
import lmzr.photomngr.ui.celleditor.PhotoTraitCellEditor;
import lmzr.photomngr.ui.cellrenderer.LocationCellRenderer;
import lmzr.photomngr.ui.cellrenderer.SubjectCellRenderer;
import lmzr.photomngr.ui.player.Player;
import lmzr.photomngr.ui.player.Player_QuickTime;
import lmzr.photomngr.ui.player.Player_VideoLAN;
import lmzr.photomngr.ui.player.Player_WindowsMediaPlayer;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent Mazuré
 */
public class PhotoEditorComponent extends JPanel
                                  implements ListSelectionListener, TableModelListener, PhotoListMetaDataListener {
    
    final private static DateFormat s_dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
    final private static DateFormat s_timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

    final private ListSelectionManager a_selection;
    final private PhotoList a_photoList;
    final private GPSDatabase a_GPSDatabase;
    final private JComboBox a_folder;
    final private JButton a_nextPhoto;
    final private JButton a_previousPhoto;
    private boolean a_folderIsDisabled;
    final private JLabel a_file;
    final private JLabel a_date;
    final private JLabel a_time;
    final private JButton a_map;
    final private JButton a_rotateLeft;
    final private JButton a_rotateRight;
    final private JTextField a_location;
    final private SubjectCellRenderer a_subject;
    final private PhotoTraitCellEditor a_quality;
    final private PhotoTraitCellEditor a_originality;
    final private PhotoTraitCellEditor a_privacy;
    final private AuthorCellEditor a_author;
    final private JTextField a_panorama;
    final private JTextField a_panoramaFirst;
    final private CopiesCellEditor a_copies;
    final private JTextArea a_parameters;
    final static private Player a_players[] = new Player[] { new Player_VideoLAN(), new Player_WindowsMediaPlayer(), new Player_QuickTime() }; 
    private JButton a_play[];
    private boolean a_isAdjusting;
    int a_previousSelection[];

	/**
	 * Action to paste
	 */
	private class ActionStartPlayer extends PhotoManagerAction {
	
		final private Player a_player;
		
		/**
		 * @param player
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionStartPlayer(final Player player,
				                 final String text,
				                 final int mnemonic,
				                 final KeyStroke accelerator,
				                 final String tooltipText) {
			super(text, mnemonic, accelerator, tooltipText);
			a_player = player;
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			
        	try {
            	final String[] commandLine = { a_player.getExecutable().getAbsolutePath(),
            			                       a_photoList.getPhoto(a_selection.getSelection()[0]).getFullPath() };
			    Runtime.getRuntime().exec(commandLine);
    		} catch (final IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
		}
		
		/**
		 * @return return the player launched by the action
		 */
		public Player getPlayer() {
			return a_player;
		}
	}
	
    /**
     * @param photoList
     * @param GPSDatabase
     * @param selection
     */
    public PhotoEditorComponent(final PhotoList photoList,
    		                    final GPSDatabase GPSDatabase,
                                final ListSelectionManager selection) {
        super();
        a_isAdjusting = false;
        a_photoList = photoList;
        a_GPSDatabase = GPSDatabase;
        a_photoList.addMetaListener(this);
        a_selection = selection;
        a_previousSelection = null;
        photoList.addTableModelListener(this);
        a_selection.addListener(this);
        
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		add(panel);
        a_folder = new JComboBox();
        updateFolderList();
        panel.add(a_folder);
        a_folder.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_folderIsDisabled = false;
        a_folder.addActionListener(
                new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                        if (a_folderIsDisabled) return;
                        for (int i=0; i<a_photoList.getRowCount(); i++) {
                            final String f = a_photoList.getPhoto(i).getFolder();
                            if (f.equals(a_folder.getSelectedItem())) {
                            	final int v [] = new int[1];
                            	v[0] = i;
                                a_selection.setSelection(v);
                                return;
                            }}}});
        
        final JPanel navigator = new JPanel();
        a_file = new JLabel();
        a_nextPhoto = new JButton("next");
        a_nextPhoto.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) { a_selection.next(1);}});
        a_previousPhoto = new JButton("prev.");
        a_previousPhoto.addActionListener(
                new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) { a_selection.previous(1);}});
        navigator.add(a_file);
        navigator.add(a_previousPhoto);
        navigator.add(a_nextPhoto);
        panel.add(navigator);
        navigator.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        final JPanel datetime =new JPanel();
        datetime.setLayout(new BoxLayout(datetime, BoxLayout.Y_AXIS));
        a_date = new JLabel();
        datetime.add(a_date);
        a_date.setAlignmentX(Component.CENTER_ALIGNMENT);
        a_time = new JLabel();
        datetime.add(a_time);
        a_time.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(datetime);
        datetime.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_play = new JButton[a_players.length];
        for (int i=0; i<a_players.length; i++) {
	        a_play[i] = new JButton(new ActionStartPlayer( a_players[i],
	        		                                       a_players[i].getName(),
	        		                                       KeyEvent.CHAR_UNDEFINED,
	        		                                       null,
	        		                                       "start "+a_players[i].getName()));
	        panel.add(a_play[i]);
	        a_play[i].setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        a_map = new JButton("map");
        a_map.addActionListener(
                new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	final GPSRecord gpsRecord = GPSDatabase.getGPSData(((HierarchicalCompoundString)(a_photoList.getValueAt(a_selection.getSelection()[0],PhotoList.PARAM_LOCATION))));
                    	if (gpsRecord==null) return;
                    	final GPSData gps = gpsRecord.getGPSData();
                    	if (!gps.isComplete()) return;
                    	try {
	                    	final String[] commandLine = { "C:\\Program Files\\Mozilla Firefox\\firefox.exe", 
	                    			                       "http://maps.google.com/maps?q="+
	                    			                 + gps.getLatitudeAsDouble()
	                    			                 + "+"
	                    			                 + gps.getLongitudeAsDouble()
	                    			                 + "+("
	                    			                 + URLEncoder.encode(gpsRecord.getLocation().toLongString().replace(">","/"),"UTF-8")
	                    			                 +")&ll="
	                    			                 + gps.getLatitudeAsDouble()
	                    			                 + ","
	                    			                 + gps.getLongitudeAsDouble()
	                    			                 + "&spn="
	                    			                 + gps.getLatitudeRangeAsDouble()
	                    			                 + ","
	                    			                 + gps.getLongitudeRangeAsDouble()
	                    			                 + "&t=h&hl=fr" };
						    Runtime.getRuntime().exec(commandLine);
                		} catch (final IOException e1) {
                			// TODO Auto-generated catch block
                			e1.printStackTrace();
                		}}});
        panel.add(a_map);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(new JSeparator());
        
        final JPanel display = new JPanel();
        a_rotateLeft = new JButton("rotate left");
        a_rotateRight = new JButton("rotate right");
        display.add(a_rotateLeft);
        a_rotateLeft.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	for (int i=0; i<a_selection.getSelection().length; i++) {
                    	    float r = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ROTATION))).floatValue();
                    	    a_photoList.setValueAt(new Float(r-90.),
 			                                       a_selection.getSelection()[i],
			                                       PhotoList.PARAM_ROTATION);};}});
        display.add(a_rotateRight);
        a_rotateRight.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	for (int i=0; i<a_selection.getSelection().length; i++) {
                    	    float r = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ROTATION))).floatValue();
                    	    a_photoList.setValueAt(new Float(r+90.),
 			                                       a_selection.getSelection()[i],
			                                       PhotoList.PARAM_ROTATION);};}});
        panel.add(display);
        display.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(new JSeparator());
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        
        final JLabel locationLabel = new JLabel("location");
        locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(locationLabel);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_location = new LocationCellRenderer();
        panel.add(a_location);
        a_location.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_location.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                    	a_photoList.setValueAt(a_location.getText(),
                    			               a_selection.getSelection()[0],
                    			               PhotoList.PARAM_LOCATION);}});
        final JLabel subjectLabel = new JLabel("subject");
        subjectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subjectLabel);
        subjectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_subject = new SubjectCellRenderer();
        /*
        a_subject.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(final ActionEvent e) {
                    	if (a_isAdjusting) return;
                    	a_photoList.setValueAt(a_location.getText(),
                    			               a_selection.getSelection()[0],
                    			               PhotoList.PARAM_SUBJECT);}});
        */
        panel.add(a_subject);
        a_subject.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JLabel qualityLabel = new JLabel("quality");
        qualityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(qualityLabel);
        qualityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_quality = new PhotoTraitCellEditor(PhotoQuality.getTraits());
        a_quality.addActionListener(
            new ActionListener() {
                public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                	if (a_isAdjusting) return;
                    a_photoList.setValueAt(a_quality.getSelectedItem(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_QUALITY);}});
        panel.add(a_quality);
        a_quality.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JLabel originalityLabel = new JLabel("originality");
        originalityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(originalityLabel);
        originalityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_originality = new PhotoTraitCellEditor(PhotoOriginality.getTraits());
        a_originality.addActionListener(
                new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                        a_photoList.setValueAt(a_originality.getSelectedItem(),
                        		               a_selection.getSelection()[0],
                                               PhotoList.PARAM_ORIGINALITY);}});
        panel.add(a_originality);
        a_originality.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JLabel privacyLabel = new JLabel("privacy");
        privacyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(privacyLabel);
        privacyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_privacy = new PhotoTraitCellEditor(PhotoPrivacy.getTraits());
        a_privacy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                        a_photoList.setValueAt(a_privacy.getSelectedItem(),
                        		               a_selection.getSelection()[0],
                                               PhotoList.PARAM_PRIVACY);}});
        panel.add(a_privacy);
        a_privacy.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JLabel authorLabel = new JLabel("author");
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(authorLabel);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_author = new AuthorCellEditor(photoList.getAuthorFactory());
        panel.add(a_author);
        a_author.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_author.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                    	a_photoList.setValueAt(a_author.getSelectedItem(),
                    			               a_selection.getSelection()[0],
                    			               PhotoList.PARAM_AUTHOR);}});
        final JLabel panoramaLabel = new JLabel("panorama");
        panoramaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(panoramaLabel);
        panoramaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_panorama = new JTextField();
        panel.add(a_panorama);
        a_panorama.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_panorama.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                    	a_photoList.setValueAt(a_panorama.getText(),
                    			               a_selection.getSelection()[0],
                    			               PhotoList.PARAM_PANORAMA);}});
        final JLabel panoramaFirstLabel = new JLabel("panorama first");
        panoramaFirstLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(panoramaFirstLabel);
        panoramaFirstLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_panoramaFirst = new JTextField();
        panel.add(a_panoramaFirst);
        a_panoramaFirst.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_panoramaFirst.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                    	a_photoList.setValueAt(a_panoramaFirst.getText(),
                    			               a_selection.getSelection()[0],
                    			               PhotoList.PARAM_PANORAMA_FIRST);}});
        final JLabel copiesLabel = new JLabel("copies");
        copiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(copiesLabel);
        copiesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_copies = new CopiesCellEditor();
        panel.add(a_copies);
        a_copies.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_copies.addActionListener(
                new ActionListener() {
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
                    	if (a_isAdjusting) return;
                        a_photoList.setValueAt(a_copies.getSelectedItem(),
                        		               a_selection.getSelection()[0],
                                               PhotoList.PARAM_COPIES);}});
        final JLabel parametersLabel = new JLabel("shot parameters");
        parametersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(parametersLabel);
        parametersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_parameters = new JTextArea();      
        a_parameters.setEditable(false);
        panel.add(a_parameters);
        a_parameters.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * 
     */
    private void update() {
        
    	final int selection[] = a_selection.getSelection();
    	if (Arrays.equals(a_previousSelection,selection)) return;
        a_previousSelection = selection;        
        
    	a_isAdjusting = true;
    	
    	// buttons to start external player
    	for (int i=0; i<a_play.length; i++) {
    		final ActionStartPlayer action = (ActionStartPlayer)a_play[i].getAction();
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
            setEnabledAll(false);
            a_date.setText(" ");
            a_time.setText(" ");            
            a_location.setText(" ");
            a_subject.setText(" ");
            //a_copy aussi
            //a_author.setText(" ");
            a_panorama.setText(" ");
            a_panoramaFirst.setText(" ");
            a_parameters.setText(" ");
        	a_isAdjusting = false;
            return;
        }
        
        setEnabledAll(true);
            
        final Photo photo = a_photoList.getPhoto(selection[0]);
        final Date date = photo.getHeaderData().getDate();
        if (date != null ) {
            final String d = s_dateFormat.format(date); 
            a_date.setText(d);
            final String t = s_timeFormat.format(date); 
            a_time.setText(t);
        } else {
            a_date.setText(" ");
            a_time.setText(" ");            
        }
        String location = photo.getIndexData().getLocation().toLongString();
        a_location.setText(location);
    	final GPSRecord gps = a_GPSDatabase.getGPSData(photo.getIndexData().getLocation());
    	if (location!=null) {
	    	if ( gps != null && gps.getGPSData().isComplete() ) {
	        	a_map.setEnabled(true);
	        	a_map.setText("map "+gps.getLocation().toString());
	    	} else {
	        	a_map.setEnabled(false);    		
	        	a_map.setText("map "+location);
	    	}
    	} else {
        	a_map.setEnabled(false);    		
        	a_map.setText("map");
    	}
        String subject = photo.getIndexData().getSubject().toString();
        a_subject.setText(subject);
        final PhotoQuality quality = photo.getIndexData().getQuality();
        a_quality.setSelectedItem(quality);
        final PhotoOriginality originality = photo.getIndexData().getOriginality();
        a_originality.setSelectedItem(originality);
        final PhotoPrivacy privacy = photo.getIndexData().getPrivacy();
        a_privacy.setSelectedItem(privacy);
        a_author.setSelectedItem(photo.getIndexData().getAuthor());
        final String panorama = photo.getIndexData().getPanorama();
        a_panorama.setText(panorama);
        final String panoramaFirst = photo.getIndexData().getPanoramaFirst();
        a_panoramaFirst.setText(panoramaFirst);
        final int copies = photo.getIndexData().getCopies();
        a_copies.setSelectedItem(new Integer(copies));
        String parameters = "";
        if ( photo.getHeaderData().getWidth()!=0) parameters = parameters + "Width: " + photo.getHeaderData().getWidth() + "\n";
        if ( photo.getHeaderData().getHeight()!=0) parameters = parameters + "Height: " + photo.getHeaderData().getHeight() + "\n";
        if ( photo.getHeaderData().getManufacturer()!=null) parameters = parameters + "Manufacturer: " + photo.getHeaderData().getManufacturer() + "\n";
        if ( photo.getHeaderData().getModel()!=null) parameters = parameters + "Model: " + photo.getHeaderData().getModel() + "\n";
        if ( photo.getHeaderData().getExposureTime()!=null) parameters = parameters + "Exposure Time: " + photo.getHeaderData().getExposureTime() + "\n";
        if ( photo.getHeaderData().getApertureValue()!=null) parameters = parameters + "Aperture Value: " + photo.getHeaderData().getApertureValue() + "\n";
        if ( photo.getHeaderData().getShutterSpeed()!=null) parameters = parameters + "Shutter Speed: " + photo.getHeaderData().getShutterSpeed() + "\n";
        if ( photo.getHeaderData().getFlash()!=null) parameters = parameters + "Flash: " + photo.getHeaderData().getFlash() + "\n";
        if ( photo.getHeaderData().getFocalLength()!=null) parameters = parameters + "Focal Length: " + photo.getHeaderData().getFocalLength() + "\n";
        if ( photo.getHeaderData().getCanonSelfTimerDelay()!=null) parameters = parameters + "Canon Self Timer Delay: " + photo.getHeaderData().getCanonSelfTimerDelay() + "\n";
        if ( photo.getHeaderData().getCanonFlashMode()!=null) parameters = parameters + "Canon Flash Mode: " + photo.getHeaderData().getCanonFlashMode() + "\n";
        if ( photo.getHeaderData().getCanonContinuousDriveMode()!=null) parameters = parameters + "Canon Continuous Drive Mode: " + photo.getHeaderData().getCanonContinuousDriveMode() + "\n";
        if ( photo.getHeaderData().getCanonFocusMode()!=null) parameters = parameters + "Canon Focus Mode: " + photo.getHeaderData().getCanonFocusMode() + "\n";
        if ( photo.getHeaderData().getCanonISO()!=null) parameters = parameters + "Canon ISO: " + photo.getHeaderData().getCanonISO() + "\n";
        if ( photo.getHeaderData().getCanonSubjectDistance()!=null) parameters = parameters + "Canon Subject Distance: " + photo.getHeaderData().getCanonSubjectDistance() + "\n";
        if (parameters.length()>0) parameters = parameters.substring(0,parameters.length()-1);
        a_parameters.setText(parameters);
        
    	a_isAdjusting = false;
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
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
    public void tableChanged(final TableModelEvent e) {
    	if ( e.getType()==TableModelEvent.INSERT || e.getType()==TableModelEvent.DELETE ) {
    		updateFolderList();
    	}
        update();
    }
    
    /**
     * enable or disable all the fields
     * @param b
     */
    private void setEnabledAll(final boolean b) {
        a_date.setEnabled(b);
        a_time.setEnabled(b);            
        a_location.setEnabled(b);
        a_quality.setEnabled(b);
        a_originality.setEnabled(b);
        a_privacy.setEnabled(b);
        a_author.setEnabled(b);
        a_panorama.setEnabled(b);
        a_panoramaFirst.setEnabled(b);
        a_copies.setEnabled(b);
        a_parameters.setEnabled(b);
    }

    /**
     * 
     */
    private void updateFolderList() {
    	a_folderIsDisabled = true;
    	a_folder.removeAllItems();
    	final Vector<String> folderList = new Vector<String>();
    	for (int i=0; i<a_photoList.getRowCount(); i++) {
    		if ( !folderList.contains(a_photoList.getPhoto(i).getFolder()))
    			folderList.add(a_photoList.getPhoto(i).getFolder());
    	}
    	for (Iterator<String> i=folderList.iterator(); i.hasNext(); ) {
    		final String f = i.next();
    		a_folder.addItem(f);
    	}
    	a_folderIsDisabled = false;
    }
    
	/**
	 * @see lmzr.photomngr.data.PhotoListMetaDataListener#photoListMetaDataChanged(lmzr.photomngr.data.PhotoListMetaDataEvent)
	 */
	public void photoListMetaDataChanged(final PhotoListMetaDataEvent e) {
		if (e.getChange()==PhotoListMetaDataEvent.FILTER_HAS_CHANGED) updateFolderList();
	}
}
