package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.ui.FilterDisplay;
import lmzr.photomngr.ui.PhotoDisplayer;

/**
 * Action to display the filter
 *
 * @author Laurent Mazuré
 */
public class DisplayFilterAction extends PhotoManagerAction {

    final private PhotoDisplayer a_photoDisplayer;
    final private FilteredPhotoList a_photoList;
    final private ListSelectionManager a_selection;
    private FilterDisplay a_filter;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param photoDisplayer
     * @param photoList
     * @param selection
     */
    public DisplayFilterAction(final String text,
                               final int mnemonic,
                               final KeyStroke accelerator,
                               final String tooltipText,
                               final PhotoDisplayer photoDisplayer,
                               final FilteredPhotoList photoList,
                               final ListSelectionManager selection) {
        super(text, mnemonic, accelerator, tooltipText);
        this.a_photoDisplayer = photoDisplayer;
        this.a_photoList = photoList;
        this.a_selection = selection;
        this.a_filter = null;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if ( this.a_filter == null)
            this.a_filter = new FilterDisplay(this.a_photoDisplayer,this.a_photoList,this.a_selection);

        this.a_filter.setVisible(true);
    }
}