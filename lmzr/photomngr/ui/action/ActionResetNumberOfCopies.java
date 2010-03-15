package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;

/**
 * Action to reset the number of copies
 */
public class ActionResetNumberOfCopies extends PhotoManagerAction {
	
    final private FilteredPhotoList a_photoList;

		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param photoList 
		 */
		public ActionResetNumberOfCopies(final String text,
				                         final int mnemonic,
				                         final KeyStroke accelerator,
				                         final String tooltipText,
				                		 final  FilteredPhotoList photoList) {
	        super(text, mnemonic, accelerator, tooltipText);
	        a_photoList = photoList;

		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			for (int i=0; i<a_photoList.getRowCount(); i++) {
				if ( ((Integer)a_photoList.getValueAt(i, PhotoList.PARAM_COPIES)).intValue() > 0 ) {
					a_photoList.setValueAt(new Integer(0),i, PhotoList.PARAM_COPIES);
				}
			}
		}
	}