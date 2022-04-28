package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;

/**
 * Action to reset the number of copies
 *
 * @author Laurent Mazuré
 */
public class ResetNumberOfCopiesAction extends PhotoManagerAction {

    final private FilteredPhotoList a_photoList;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param photoList
     */
    public ResetNumberOfCopiesAction(final String text,
                                     final int mnemonic,
                                     final KeyStroke accelerator,
                                     final String tooltipText,
                                     final  FilteredPhotoList photoList) {
        super(text, mnemonic, accelerator, tooltipText);
        this.a_photoList = photoList;

    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        for (int i=0; i<this.a_photoList.getRowCount(); i++) {
            if ( ((Integer)this.a_photoList.getValueAt(i, PhotoList.PARAM_COPIES)).intValue() > 0 ) {
                this.a_photoList.setValueAt(Integer.valueOf(0),i, PhotoList.PARAM_COPIES);
            }
        }
    }
}