package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
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
import lmzr.photomngr.ui.celleditor.AuthorCellEditor;
import lmzr.photomngr.ui.celleditor.CopiesCellEditor;
import lmzr.photomngr.ui.celleditor.LocationCellEditor;
import lmzr.photomngr.ui.celleditor.PhotoTraitCellEditor;
import lmzr.photomngr.ui.celleditor.SubjectCellEditor;

/**
 * @author Laurent Mazuré
 *
 */
public class PhotoEditor extends JFrame
                         implements ListSelectionListener, TableModelListener {

    final private ListSelectionManager a_selection;
    final private PhotoList a_photoList;
    final private LocationCellEditor a_location;
    final private SubjectCellEditor a_subject;
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

        this.a_isAdjusting = false;

        this.a_photoList = photoList;
        this.a_selection = selection;

        this.a_photoList.addTableModelListener(this);
        this.a_selection.addListener(this);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        //TODO half of this class simply does not work... to be finished

        this.a_location = new LocationCellEditor(photoList.getLocationFactory(),this);
        this.a_location.setBorder(BorderFactory.createTitledBorder("location"));
        this.a_location.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_location.getPreferredSize().height));
        panel.add(this.a_location);
        this.a_location.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.a_location.addTextFocusListener (
                new FocusListener() {
                    @Override
                    public void focusLost(final FocusEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_location.getText(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_LOCATION);}
                    @Override
                    public void focusGained(final FocusEvent e) {
                    	// do noting
                    }
                });
        this.a_subject = new SubjectCellEditor(photoList, this);
        this.a_subject.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_subject.getPreferredSize().height));
        this.a_subject.setBorder(BorderFactory.createTitledBorder("subject"));
        this.a_subject.addTextFocusListener (
                new FocusListener() {
                    @Override
                    public void focusLost(final FocusEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_subject.getText(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_SUBJECT);}
                    @Override
                    public void focusGained(final FocusEvent e) {
                    	// do noting
                    }
                });
        panel.add(this.a_subject);
        this.a_subject.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_quality = new PhotoTraitCellEditor(PhotoQuality.getTraits());
        this.a_quality.setBorder(BorderFactory.createTitledBorder("quality"));
        this.a_quality.setMaximumSize(this.a_quality.getPreferredSize());
        this.a_quality.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (PhotoEditor.this.a_isAdjusting) return;
                    PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_quality.getSelectedItem(),
                                           PhotoEditor.this.a_selection.getSelection()[0],
                                           PhotoList.PARAM_QUALITY);}});
        panel.add(this.a_quality);
        this.a_quality.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_originality = new PhotoTraitCellEditor(PhotoOriginality.getTraits());
        this.a_originality.setBorder(BorderFactory.createTitledBorder("originality"));
        this.a_originality.setMaximumSize(this.a_originality.getPreferredSize());
        this.a_originality.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_originality.getSelectedItem(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_ORIGINALITY);}});
        panel.add(this.a_originality);
        this.a_originality.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_privacy = new PhotoTraitCellEditor(PhotoPrivacy.getTraits());
        this.a_privacy.setBorder(BorderFactory.createTitledBorder("privacy"));
        this.a_privacy.setMaximumSize(this.a_privacy.getPreferredSize());
        this.a_privacy.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_privacy.getSelectedItem(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_PRIVACY);}});
        panel.add(this.a_privacy);
        this.a_privacy.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_author = new AuthorCellEditor(photoList.getAuthorFactory());
        this.a_author.setBorder(BorderFactory.createTitledBorder("author"));
        this.a_author.setMaximumSize(this.a_author.getPreferredSize());
        panel.add(this.a_author);
        this.a_author.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.a_author.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_author.getSelectedItem(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_AUTHOR);}});

        this.a_panorama = new JTextField();
        this.a_panorama.setBorder(BorderFactory.createTitledBorder("panorama"));
        this.a_panorama.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_panorama.getPreferredSize().height));
        panel.add(this.a_panorama);
        this.a_panorama.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.a_panorama.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_panorama.getText(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_PANORAMA);}});
        this.a_panoramaFirst = new JTextField();
        this.a_panoramaFirst.setBorder(BorderFactory.createTitledBorder("panorama first"));
        this.a_panoramaFirst.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_panoramaFirst.getPreferredSize().height));
        panel.add(this.a_panoramaFirst);
        this.a_panoramaFirst.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.a_panoramaFirst.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_panoramaFirst.getText(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_PANORAMA_FIRST);}});

        this.a_copies = new CopiesCellEditor();
        this.a_copies.setBorder(BorderFactory.createTitledBorder("copies"));
        this.a_copies.setMaximumSize(this.a_copies.getPreferredSize());
        panel.add(this.a_copies);
        this.a_copies.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.a_copies.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (PhotoEditor.this.a_isAdjusting) return;
                        PhotoEditor.this.a_photoList.setValueAt(PhotoEditor.this.a_copies.getSelectedItem(),
                                               PhotoEditor.this.a_selection.getSelection()[0],
                                               PhotoList.PARAM_COPIES);}});

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        pack();

        update();
    }

    /**
     *
     */
    private void update() {

        final int selection[] = this.a_selection.getSelection();

        this.a_isAdjusting = true;



        if ( selection.length!=1 ) {
            // zero or more than one image is selected
            // -> empty the text fields and disable all the fields (except previous and next if at least one
            setEnabledAll(false);
            this.a_location.setText(" ");
            this.a_subject.setText(" ");
            this.a_subject.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_subject.getPreferredSize().height));
            this.a_panorama.setText(" ");
            this.a_panoramaFirst.setText(" ");
            this.a_copies.setSelectedItem(Integer.valueOf(0));
            this.a_author.setSelectedItem("");
            this.a_isAdjusting = false;
            return;
        }

        setEnabledAll(true);

        final Photo photo = this.a_photoList.getPhoto(selection[0]);
        final String location = photo.getIndexData().getLocation().toLongString();
        this.a_location.setText(location);
        final String subject = photo.getIndexData().getSubject().toString();
        this.a_subject.setText(subject);
        this.a_subject.setMaximumSize(new Dimension(Integer.MAX_VALUE,this.a_subject.getPreferredSize().height));
        final PhotoQuality quality = photo.getIndexData().getQuality();
        this.a_quality.setSelectedItem(quality);
        final PhotoOriginality originality = photo.getIndexData().getOriginality();
        this.a_originality.setSelectedItem(originality);
        final PhotoPrivacy privacy = photo.getIndexData().getPrivacy();
        this.a_privacy.setSelectedItem(privacy);
        this.a_author.setSelectedItem(photo.getIndexData().getAuthor());
        final String panorama = photo.getIndexData().getPanorama();
        this.a_panorama.setText(panorama);
        final String panoramaFirst = photo.getIndexData().getPanoramaFirst();
        this.a_panoramaFirst.setText(panoramaFirst);
        final int copies = photo.getIndexData().getCopies();
        this.a_copies.setSelectedItem(Integer.valueOf(copies));

        this.a_isAdjusting = false;
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
        update();
    }

    /**
     * enable or disable all the fields
     * @param b
     */
    private void setEnabledAll(final boolean b) {
        this.a_location.setEnabled(b);
        this.a_quality.setEnabled(b);
        this.a_originality.setEnabled(b);
        this.a_privacy.setEnabled(b);
        this.a_author.setEnabled(b);
        this.a_panorama.setEnabled(b);
        this.a_panoramaFirst.setEnabled(b);
        this.a_copies.setEnabled(b);
    }

    /**
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {

        super.dispose();

        this.a_photoList.removeTableModelListener(this);
        this.a_selection.removeListener(this);
    }
}
