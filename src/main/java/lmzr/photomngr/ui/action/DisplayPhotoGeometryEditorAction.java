package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.PhotoGeometryEditor;

/**
 * @author Laurent Mazuré
 */
public class DisplayPhotoGeometryEditorAction extends PhotoManagerAction {

       final private PhotoList a_photoList;
       final private ListSelectionManager a_selection;
       private PhotoGeometryEditor a_PhotoGeometryEditor;

        /**
         * @param text
         * @param mnemonic
         * @param accelerator
         * @param tooltipText
         * @param photoList
         * @param selection
         */
        public DisplayPhotoGeometryEditorAction(final String text,
                                                final int mnemonic,
                                                final KeyStroke accelerator,
                                                final String tooltipText,
                                                final PhotoList photoList,
                                                final ListSelectionManager selection) {
            super(text, mnemonic, accelerator, tooltipText);
            this.a_photoList = photoList;
            this.a_selection = selection;
            this.a_PhotoGeometryEditor = null;
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e) {

            if (this.a_PhotoGeometryEditor==null)
                this.a_PhotoGeometryEditor = new PhotoGeometryEditor(this.a_photoList, this.a_selection);

            this.a_PhotoGeometryEditor.setVisible(true);
        }

}
