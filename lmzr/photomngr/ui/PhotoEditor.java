package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.ui.action.ActionClose;
import lmzr.photomngr.ui.celleditor.AuthorCellEditor;
import lmzr.photomngr.ui.celleditor.CopiesCellEditor;
import lmzr.photomngr.ui.celleditor.PhotoTraitCellEditor;
import lmzr.photomngr.ui.cellrenderer.LocationCellRenderer;
import lmzr.photomngr.ui.cellrenderer.SubjectCellRenderer;

public class PhotoEditor extends JFrame
                         implements ListSelectionListener, TableModelListener {

    final private ListSelectionManager a_selection;
    final private PhotoList a_photoList;
    final private JTextField a_location;
    final private SubjectCellRenderer a_subject;
    final private PhotoTraitCellEditor a_quality;
    final private PhotoTraitCellEditor a_originality;
    final private PhotoTraitCellEditor a_privacy;
    final private AuthorCellEditor a_author;
    final private JTextField a_panorama;
    final private JTextField a_panoramaFirst;
    final private CopiesCellEditor a_copies;
    private boolean a_isAdjusting;


   /**
     * @param photoList
     * @param selection
     */
    public PhotoEditor(final PhotoList photoList,
    		           final ListSelectionManager selection) {
        super();
        
        a_isAdjusting = false;

        a_photoList = photoList;
        a_selection = selection;
        
        a_photoList.addTableModelListener(this);
        a_selection.addListener(this);

	    final JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menubar.add(menuFile);
		final ActionClose a_actionClose = new ActionClose("Close", KeyEvent.VK_UNDEFINED, null,"Close the window",this);
		final JMenuItem itemClose = new JMenuItem(a_actionClose);
		menuFile.add(itemClose);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel);

        final JLabel locationLabel = new JLabel("location");
        locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(locationLabel);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_location = new LocationCellRenderer();
        panel.add(a_location);
        a_location.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_location.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(final ActionEvent e) {
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
                public void actionPerformed(final ActionEvent e) {
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
                    public void actionPerformed(final ActionEvent e) {
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
                    public void actionPerformed(final ActionEvent e) {
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
                    public void actionPerformed(final ActionEvent e) {
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
                    public void actionPerformed(final ActionEvent e) {
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
                    public void actionPerformed(final ActionEvent e) {
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
                    public void actionPerformed(final ActionEvent e) {
                    	if (a_isAdjusting) return;
                        a_photoList.setValueAt(a_copies.getSelectedItem(),
                        		               a_selection.getSelection()[0],
                                               PhotoList.PARAM_COPIES);}});
        
        update();

    }
 
    /**
     * 
     */
    private void update() {
        
    	final int selection[] = a_selection.getSelection();
        
    	a_isAdjusting = true;
    	
        
        
        if ( selection.length!=1 ) {
            // zero or more than one image is selected
            // -> empty the text fields and disable all the fields (except previous and next if at least one 
            setEnabledAll(false);
            a_location.setText(" ");
            a_subject.setText(" ");
            a_panorama.setText(" ");
            a_panoramaFirst.setText(" ");
            a_copies.setSelectedItem(new Integer(0));
            a_author.setSelectedItem("");
        	a_isAdjusting = false;
            return;
        }
        
        setEnabledAll(true);
            
        final Photo photo = a_photoList.getPhoto(selection[0]);
        final String location = photo.getIndexData().getLocation().toLongString();
        a_location.setText(location);
        final String subject = photo.getIndexData().getSubject().toString();
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
        update();
    }
    
    /**
     * enable or disable all the fields
     * @param b
     */
    private void setEnabledAll(final boolean b) {
        a_location.setEnabled(b);
        a_quality.setEnabled(b);
        a_originality.setEnabled(b);
        a_privacy.setEnabled(b);
        a_author.setEnabled(b);
        a_panorama.setEnabled(b);
        a_panoramaFirst.setEnabled(b);
        a_copies.setEnabled(b);
    }

}
