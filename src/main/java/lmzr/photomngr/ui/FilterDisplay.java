package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import lmzr.photomngr.data.DataFormat;
import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilterOnAuthor;
import lmzr.photomngr.data.filter.FilterOnCopies;
import lmzr.photomngr.data.filter.FilterOnFormat;
import lmzr.photomngr.data.filter.FilterOnHierarchicalCompoundString;
import lmzr.photomngr.data.filter.FilterOnPhotoTrait;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.filter.PhotoListFilter;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.ui.filter.AuthorComponentFilterUI;
import lmzr.photomngr.ui.filter.CopiesComponentFilterUI;
import lmzr.photomngr.ui.filter.FormatComponentFilterUI;
import lmzr.photomngr.ui.filter.HierachicalCompoundStringComponentFilterUI;
import lmzr.photomngr.ui.filter.TraitDisplayComponentFilterUI;

/**
 * @author Laurent Mazuré
 */
public class FilterDisplay extends JDialog {

    final private TraitDisplayComponentFilterUI a_quality;
    final private TraitDisplayComponentFilterUI a_originality;
    final private TraitDisplayComponentFilterUI a_privacy;
    final private HierachicalCompoundStringComponentFilterUI a_location;
    final private HierachicalCompoundStringComponentFilterUI a_subject1;
    final private HierachicalCompoundStringComponentFilterUI a_subject2;
    final private HierachicalCompoundStringComponentFilterUI a_subject3;
    final private HierachicalCompoundStringComponentFilterUI a_subject4;
    final private FormatComponentFilterUI a_format;
    final private AuthorComponentFilterUI a_author;
    final private CopiesComponentFilterUI a_copies;

    /**
     * @param frame
     * @param list
     * @param selection
     */
    public FilterDisplay(final JFrame frame,
                         final FilteredPhotoList list,
                         final ListSelectionManager selection) {
        super(frame,true);

        final PhotoListFilter filter = list.getFilter();

        final Container pane = getContentPane();

        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        this.a_quality = new TraitDisplayComponentFilterUI("quality",filter.getFilterOnQuality());
        p.add(this.a_quality);
        this.a_quality.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_originality = new TraitDisplayComponentFilterUI("originality",filter.getFilterOnOriginality());
        p.add(this.a_originality);
        this.a_originality.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_privacy = new TraitDisplayComponentFilterUI("privacy",filter.getFilterOnPrivacy());
        p.add(this.a_privacy);
        this.a_privacy.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_location = new HierachicalCompoundStringComponentFilterUI("location",list.getLocationFactory(),filter.getFilterOnLocation());
        p.add(this.a_location);
        this.a_location.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_subject1 = new HierachicalCompoundStringComponentFilterUI("subject 1",list.getSubjectFactory(),filter.getFilterOnSubject1());
        p.add(this.a_subject1);
        this.a_subject1.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_subject2 = new HierachicalCompoundStringComponentFilterUI("subject 2",list.getSubjectFactory(),filter.getFilterOnSubject2());
        p.add(this.a_subject2);
        this.a_subject2.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_subject3 = new HierachicalCompoundStringComponentFilterUI("subject 3",list.getSubjectFactory(),filter.getFilterOnSubject3());
        p.add(this.a_subject3);
        this.a_subject3.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_subject4 = new HierachicalCompoundStringComponentFilterUI("subject 4",list.getSubjectFactory(),filter.getFilterOnSubject4());
        p.add(this.a_subject4);
        this.a_subject4.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_format = new FormatComponentFilterUI("format",filter.getFilterOnFormat());
        p.add(this.a_format);
        this.a_format.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_author = new AuthorComponentFilterUI("author",list.getAuthorFactory(),filter.getFilterOnAuthor());
        p.add(this.a_author);
        this.a_author.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.a_copies = new CopiesComponentFilterUI("copies",filter.getFilterOnCopies());
        p.add(this.a_copies);
        this.a_copies.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pane.add(new JScrollPane(p),BorderLayout.CENTER);

        final JPanel buttonsPane = new JPanel(new GridLayout(1,2));
        add(buttonsPane,BorderLayout.SOUTH);
        final JButton bOk = new JButton("OK");
        final JButton bCancel = new JButton("Cancel");
        buttonsPane.add(bOk);
        buttonsPane.add(bCancel);
        getRootPane().setDefaultButton(bOk);
        bOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final FilterOnPhotoTrait filterOnOriginality = new FilterOnPhotoTrait(FilterDisplay.this.a_originality.isActive(),PhotoOriginality.getTraits(),FilterDisplay.this.a_originality.getValues(),PhotoList.PARAM_ORIGINALITY);
                    final FilterOnPhotoTrait filterOnPrivacy = new FilterOnPhotoTrait(FilterDisplay.this.a_privacy.isActive(),PhotoPrivacy.getTraits(),FilterDisplay.this.a_privacy.getValues(),PhotoList.PARAM_PRIVACY);
                    final FilterOnPhotoTrait filterOnQuality = new FilterOnPhotoTrait(FilterDisplay.this.a_quality.isActive(),PhotoQuality.getTraits(),FilterDisplay.this.a_quality.getValues(),PhotoList.PARAM_QUALITY);
                    final FilterOnHierarchicalCompoundString filterOnLocation = new FilterOnHierarchicalCompoundString(FilterDisplay.this.a_location.isActive(),FilterDisplay.this.a_location.getValues(),PhotoList.PARAM_LOCATION);
                    final FilterOnHierarchicalCompoundString filterOnSubject1 = new FilterOnHierarchicalCompoundString(FilterDisplay.this.a_subject1.isActive(),FilterDisplay.this.a_subject1.getValues(),PhotoList.PARAM_SUBJECT);
                    final FilterOnHierarchicalCompoundString filterOnSubject2 = new FilterOnHierarchicalCompoundString(FilterDisplay.this.a_subject2.isActive(),FilterDisplay.this.a_subject2.getValues(),PhotoList.PARAM_SUBJECT);
                    final FilterOnHierarchicalCompoundString filterOnSubject3 = new FilterOnHierarchicalCompoundString(FilterDisplay.this.a_subject3.isActive(),FilterDisplay.this.a_subject3.getValues(),PhotoList.PARAM_SUBJECT);
                    final FilterOnHierarchicalCompoundString filterOnSubject4 = new FilterOnHierarchicalCompoundString(FilterDisplay.this.a_subject4.isActive(),FilterDisplay.this.a_subject4.getValues(),PhotoList.PARAM_SUBJECT);
                    final FilterOnFormat filterOnFormat = new FilterOnFormat(FilterDisplay.this.a_format.isActive(),DataFormat.getAllFormats(),FilterDisplay.this.a_format.getValues());
                    final FilterOnAuthor filterOnAuthor = new FilterOnAuthor(FilterDisplay.this.a_author.isActive(),FilterDisplay.this.a_author.getValues());
                    final FilterOnCopies filterOnCopies = new FilterOnCopies(FilterDisplay.this.a_copies.isActive(),FilterDisplay.this.a_copies.getMin(),FilterDisplay.this.a_copies.getMax());
                    final PhotoListFilter f = new PhotoListFilter(filterOnOriginality,
                                                                  filterOnPrivacy,
                                                                  filterOnQuality,
                                                                  filterOnLocation,
                                                                  filterOnSubject1,
                                                                  filterOnSubject2,
                                                                  filterOnSubject3,
                                                                  filterOnSubject4,
                                                                  filterOnFormat,
                                                                  filterOnAuthor,
                                                                  filterOnCopies);
                    list.setFilter(f,selection);
                    close();
                }
        });
        bCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        close();
                    }
        });
        pack();
        setSize(650,700);
    }

    /**
     *
     */
    private void close() {
        setVisible(false);
    }

}
