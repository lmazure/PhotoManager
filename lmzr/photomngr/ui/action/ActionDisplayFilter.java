package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.ui.FilterDisplay;
import lmzr.photomngr.ui.PhotoDisplayer;

/**
 * Action to display the filter
 */
public class ActionDisplayFilter extends PhotoManagerAction {

	final private PhotoDisplayer a_photoDisplayer;
    final private FilteredPhotoList a_photoList;
    final private ListSelectionManager a_selection;

		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param photoDisplayer 
		 * @param photoList 
		 * @param selection 
		 */
		public ActionDisplayFilter(final String text,
                            final int mnemonic,
  		                    final KeyStroke accelerator,
		                    final String tooltipText,
		                	final PhotoDisplayer photoDisplayer,
	                        final FilteredPhotoList photoList,
                         	final ListSelectionManager selection) {
	        super(text, mnemonic, accelerator, tooltipText);
	        a_photoDisplayer = photoDisplayer;
	        a_photoList = photoList;
	        a_selection = selection;
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    final FilterDisplay filter = new FilterDisplay(a_photoDisplayer,a_photoList,a_selection);
		    filter.setVisible(true);
		}
	}