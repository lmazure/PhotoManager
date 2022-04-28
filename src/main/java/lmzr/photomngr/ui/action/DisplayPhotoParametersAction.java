package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.PhotoParametersDisplay;

/**
 * @author Laurent Mazuré
 */
public class DisplayPhotoParametersAction extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;
    private PhotoParametersDisplay a_PhotoParametersDisplay;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param photoList
     * @param selection
     */
    public DisplayPhotoParametersAction(final String text,
                                        final int mnemonic,
                                        final KeyStroke accelerator,
                                        final String tooltipText,
                                        final PhotoList photoList,
                                        final ListSelectionManager selection) {
        super(text, mnemonic, accelerator, tooltipText);
        this.a_photoList = photoList;
        this.a_selection = selection;
        this.a_PhotoParametersDisplay = null;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if (this.a_PhotoParametersDisplay==null)
            this.a_PhotoParametersDisplay = new PhotoParametersDisplay(this.a_photoList, this.a_selection);

        this.a_PhotoParametersDisplay.setVisible(true);

    }
}
