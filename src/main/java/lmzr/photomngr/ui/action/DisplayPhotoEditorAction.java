package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.PhotoEditor;

/**
 * @author Laurent Mazuré
 */
public class DisplayPhotoEditorAction extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;
    private PhotoEditor a_PhotoEditor;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param photoList
     * @param selection
     */
    public DisplayPhotoEditorAction(final String text,
                                    final int mnemonic,
                                    final KeyStroke accelerator,
                                    final String tooltipText,
                                    final PhotoList photoList,
                                    final ListSelectionManager selection) {
        super(text, mnemonic, accelerator, tooltipText);
        this.a_photoList = photoList;
        this.a_selection = selection;
        this.a_PhotoEditor = null;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if (this.a_PhotoEditor==null)
            this.a_PhotoEditor = new PhotoEditor(this.a_photoList, this.a_selection);

        this.a_PhotoEditor.setVisible(true);
    }
}
