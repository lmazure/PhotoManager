package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.PhotoGeometryEditor;

/**
 * @author Laurent Mazur�
 */
public class DisplayPhotoGeometryEditorAction extends PhotoManagerAction {

	   final private PhotoList a_photoList;
	   final private ListSelectionManager a_selection;
	   PhotoGeometryEditor a_PhotoGeometryEditor;
	   
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
			a_photoList = photoList;
	        a_selection = selection;
	        a_PhotoGeometryEditor = null;
		}


		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e) {

			if (a_PhotoGeometryEditor==null)
				a_PhotoGeometryEditor = new PhotoGeometryEditor(a_photoList, a_selection);
			
	        a_PhotoGeometryEditor.setVisible(true);
		}

}
