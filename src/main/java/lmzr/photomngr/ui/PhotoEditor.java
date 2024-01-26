package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.Dimension;
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
        a_isAdjusting = false;

        a_photoList = photoList;
        a_selection = selection;

        a_photoList.addTableModelListener(this);
        a_selection.addListener(this);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        //TODO half of this class simply does not work... to be finished

        a_location = new LocationCellEditor(photoList.getLocationFactory(),this);
        a_location.setBorder(BorderFactory.createTitledBorder("location"));
        a_location.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_location.getPreferredSize().height));
        panel.add(a_location);
        a_location.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_location.addTextFocusListener (
                new FocusListener() {
                    @Override
                    public void focusLost(final FocusEvent e) {
                        if (a_isAdjusting) {
                            return;
                        }
                        a_photoList.setValueAt(a_location.getText(),
                                               a_selection.getSelection()[0],
                                               PhotoList.PARAM_LOCATION);}
                    @Override
                    public void focusGained(final FocusEvent e) {
                        // do noting
                    }
                });
        a_subject = new SubjectCellEditor(photoList, this);
        a_subject.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_subject.getPreferredSize().height));
        a_subject.setBorder(BorderFactory.createTitledBorder("subject"));
        a_subject.addTextFocusListener (
                new FocusListener() {
                    @Override
                    public void focusLost(final FocusEvent e) {
                        if (a_isAdjusting) {
                            return;
                        }
                        a_photoList.setValueAt(a_subject.getText(),
                                               a_selection.getSelection()[0],
                                               PhotoList.PARAM_SUBJECT);}
                    @Override
                    public void focusGained(final FocusEvent e) {
                        // do noting
                    }
                });
        panel.add(a_subject);
        a_subject.setAlignmentX(Component.LEFT_ALIGNMENT);

        a_quality = new PhotoTraitCellEditor(PhotoQuality.getTraits());
        a_quality.setBorder(BorderFactory.createTitledBorder("quality"));
        a_quality.setMaximumSize(a_quality.getPreferredSize());
        a_quality.addActionListener(
            e -> {
                if (a_isAdjusting) {
                    return;
                }
                a_photoList.setValueAt(a_quality.getSelectedItem(),
                                       a_selection.getSelection()[0],
                                       PhotoList.PARAM_QUALITY);});
        panel.add(a_quality);
        a_quality.setAlignmentX(Component.LEFT_ALIGNMENT);

        a_originality = new PhotoTraitCellEditor(PhotoOriginality.getTraits());
        a_originality.setBorder(BorderFactory.createTitledBorder("originality"));
        a_originality.setMaximumSize(a_originality.getPreferredSize());
        a_originality.addActionListener(
                e -> {
                    if (a_isAdjusting) {
                        return;
                    }
                    a_photoList.setValueAt(a_originality.getSelectedItem(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_ORIGINALITY);});
        panel.add(a_originality);
        a_originality.setAlignmentX(Component.LEFT_ALIGNMENT);

        a_privacy = new PhotoTraitCellEditor(PhotoPrivacy.getTraits());
        a_privacy.setBorder(BorderFactory.createTitledBorder("privacy"));
        a_privacy.setMaximumSize(a_privacy.getPreferredSize());
        a_privacy.addActionListener(
                e -> {
                    if (a_isAdjusting) {
                        return;
                    }
                    a_photoList.setValueAt(a_privacy.getSelectedItem(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_PRIVACY);});
        panel.add(a_privacy);
        a_privacy.setAlignmentX(Component.LEFT_ALIGNMENT);

        a_author = new AuthorCellEditor(photoList.getAuthorFactory());
        a_author.setBorder(BorderFactory.createTitledBorder("author"));
        a_author.setMaximumSize(a_author.getPreferredSize());
        panel.add(a_author);
        a_author.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_author.addActionListener(
                e -> {
                    if (a_isAdjusting) {
                        return;
                    }
                    a_photoList.setValueAt(a_author.getSelectedItem(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_AUTHOR);});

        a_panorama = new JTextField();
        a_panorama.setBorder(BorderFactory.createTitledBorder("panorama"));
        a_panorama.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_panorama.getPreferredSize().height));
        panel.add(a_panorama);
        a_panorama.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_panorama.addActionListener(
                e -> {
                    if (a_isAdjusting) {
                        return;
                    }
                    a_photoList.setValueAt(a_panorama.getText(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_PANORAMA);});
        a_panoramaFirst = new JTextField();
        a_panoramaFirst.setBorder(BorderFactory.createTitledBorder("panorama first"));
        a_panoramaFirst.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_panoramaFirst.getPreferredSize().height));
        panel.add(a_panoramaFirst);
        a_panoramaFirst.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_panoramaFirst.addActionListener(
                e -> {
                    if (a_isAdjusting) {
                        return;
                    }
                    a_photoList.setValueAt(a_panoramaFirst.getText(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_PANORAMA_FIRST);});

        a_copies = new CopiesCellEditor();
        a_copies.setBorder(BorderFactory.createTitledBorder("copies"));
        a_copies.setMaximumSize(a_copies.getPreferredSize());
        panel.add(a_copies);
        a_copies.setAlignmentX(Component.LEFT_ALIGNMENT);
        a_copies.addActionListener(
                e -> {
                    if (a_isAdjusting) {
                        return;
                    }
                    a_photoList.setValueAt(a_copies.getSelectedItem(),
                                           a_selection.getSelection()[0],
                                           PhotoList.PARAM_COPIES);});

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        pack();

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
            a_subject.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_subject.getPreferredSize().height));
            a_panorama.setText(" ");
            a_panoramaFirst.setText(" ");
            a_copies.setSelectedItem(Integer.valueOf(0));
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
        a_subject.setMaximumSize(new Dimension(Integer.MAX_VALUE,a_subject.getPreferredSize().height));
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
        a_copies.setSelectedItem(Integer.valueOf(copies));

        a_isAdjusting = false;
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
        a_location.setEnabled(b);
        a_quality.setEnabled(b);
        a_originality.setEnabled(b);
        a_privacy.setEnabled(b);
        a_author.setEnabled(b);
        a_panorama.setEnabled(b);
        a_panoramaFirst.setEnabled(b);
        a_copies.setEnabled(b);
    }

    /**
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {

        super.dispose();

        a_photoList.removeTableModelListener(this);
        a_selection.removeListener(this);
    }
}
