package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.filter.FilteredPhotoList;

/**
 * Action to export the list of subjects
 */
public class ActionExportLocations extends ActionExport {
	
    final private FilteredPhotoList a_photoList;

		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param frame 
		 * @param photoList 
		 */
		public ActionExportLocations(final String text,
                                    final int mnemonic,
  		                            final KeyStroke accelerator,
		                            final String tooltipText,
		                			final JFrame frame,
		                			final  FilteredPhotoList photoList) {
	        super(text, mnemonic, accelerator, tooltipText, frame);
	        a_photoList = photoList;
		}
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    dumpRoot(a_photoList.getLocationFactory().getRootAsHierarchicalCompoundString());
		}
	}