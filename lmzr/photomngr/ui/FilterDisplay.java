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
import lmzr.photomngr.ui.filter.FilterOnAuthorComponent;
import lmzr.photomngr.ui.filter.FilterOnCopiesComponent;
import lmzr.photomngr.ui.filter.FilterOnFormatComponent;
import lmzr.photomngr.ui.filter.FilterOnHierachicalCompoundStringComponent;
import lmzr.photomngr.ui.filter.FilterOnTraitDisplayComponent;

/**
 * 
 */
public class FilterDisplay extends JDialog {

    final private FilterOnTraitDisplayComponent a_quality;
    final private FilterOnTraitDisplayComponent a_originality;
    final private FilterOnTraitDisplayComponent a_privacy;
    final private FilterOnHierachicalCompoundStringComponent a_location;
    final private FilterOnHierachicalCompoundStringComponent a_subject1;
    final private FilterOnHierachicalCompoundStringComponent a_subject2;
    final private FilterOnHierachicalCompoundStringComponent a_subject3;
    final private FilterOnHierachicalCompoundStringComponent a_subject4;
    final private FilterOnFormatComponent a_format;
    final private FilterOnAuthorComponent a_author;
    final private FilterOnCopiesComponent a_copies;
    
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
        
        a_quality = new FilterOnTraitDisplayComponent("quality",filter.getFilterOnQuality());
        p.add(a_quality);
        a_quality.setAlignmentX(Component.LEFT_ALIGNMENT);

        a_originality = new FilterOnTraitDisplayComponent("originality",filter.getFilterOnOriginality());
        p.add(a_originality);
        a_originality.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_privacy = new FilterOnTraitDisplayComponent("privacy",filter.getFilterOnPrivacy());
        p.add(a_privacy);
        a_privacy.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_location = new FilterOnHierachicalCompoundStringComponent("location",list.getLocationFactory(),filter.getFilterOnLocation());
        p.add(a_location);
        a_location.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_subject1 = new FilterOnHierachicalCompoundStringComponent("subject 1",list.getSubjectFactory(),filter.getFilterOnSubject1());
        p.add(a_subject1);
        a_subject1.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_subject2 = new FilterOnHierachicalCompoundStringComponent("subject 2",list.getSubjectFactory(),filter.getFilterOnSubject2());
        p.add(a_subject2);
        a_subject2.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_subject3 = new FilterOnHierachicalCompoundStringComponent("subject 3",list.getSubjectFactory(),filter.getFilterOnSubject3());
        p.add(a_subject3);
        a_subject3.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_subject4 = new FilterOnHierachicalCompoundStringComponent("subject 4",list.getSubjectFactory(),filter.getFilterOnSubject4());
        p.add(a_subject4);
        a_subject4.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_format = new FilterOnFormatComponent("format",filter.getFilterOnFormat());
        p.add(a_format);
        a_format.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_author = new FilterOnAuthorComponent("author",list.getAuthorFactory(),filter.getFilterOnAuthor());
        p.add(a_author);
        a_author.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        a_copies = new FilterOnCopiesComponent("copies",filter.getFilterOnCopies());
        p.add(a_copies);
        a_copies.setAlignmentX(Component.LEFT_ALIGNMENT);
        
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
				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				    final FilterOnPhotoTrait filterOnOriginality = new FilterOnPhotoTrait(PhotoOriginality.getTraits(),a_originality.getValues(),PhotoList.PARAM_ORIGINALITY);
				    final FilterOnPhotoTrait filterOnPrivacy = new FilterOnPhotoTrait(PhotoPrivacy.getTraits(),a_privacy.getValues(),PhotoList.PARAM_PRIVACY);
				    final FilterOnPhotoTrait filterOnQuality = new FilterOnPhotoTrait(PhotoQuality.getTraits(),a_quality.getValues(),PhotoList.PARAM_QUALITY);
				    final FilterOnHierarchicalCompoundString filterOnLocation = new FilterOnHierarchicalCompoundString(a_location.getValues(),PhotoList.PARAM_LOCATION);
				    final FilterOnHierarchicalCompoundString filterOnSubject1 = new FilterOnHierarchicalCompoundString(a_subject1.getValues(),PhotoList.PARAM_SUBJECT);
				    final FilterOnHierarchicalCompoundString filterOnSubject2 = new FilterOnHierarchicalCompoundString(a_subject2.getValues(),PhotoList.PARAM_SUBJECT);
				    final FilterOnHierarchicalCompoundString filterOnSubject3 = new FilterOnHierarchicalCompoundString(a_subject3.getValues(),PhotoList.PARAM_SUBJECT);
				    final FilterOnHierarchicalCompoundString filterOnSubject4 = new FilterOnHierarchicalCompoundString(a_subject4.getValues(),PhotoList.PARAM_SUBJECT);
				    final FilterOnFormat filterOnFormat = new FilterOnFormat(DataFormat.getAllFormats(),a_format.getValues());  
				    final FilterOnAuthor filterOnAuthor = new FilterOnAuthor(a_author.getValues());  
				    final FilterOnCopies filterOnCopies = new FilterOnCopies(a_copies.getMin(),a_copies.getMax());  
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
					public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
						close();
					}
		});
		pack();
        setSize(250,700);
    }
    
	/**
	 * 
	 */
	private void close() {
		setVisible(false);
		dispose();		
	}

}
