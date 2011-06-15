package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.exporter.Exporter;

/**
 * Action to create exportable copies
 * @author Laurent Mazuré
 */
public class CreateCopiesForPrintingAction extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private JFrame a_frame;

		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param frame 
		 * @param photoList 
		 */
		public CreateCopiesForPrintingAction(final String text,
		                                     final int mnemonic,
		                                     final KeyStroke accelerator,
		                                     final String tooltipText,
		                                     final JFrame frame,
					                	     final  FilteredPhotoList photoList) {
	        super(text, mnemonic, accelerator, tooltipText);
	        a_frame = frame;
	        a_photoList = photoList;
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {

			final Exporter exporter = new Exporter(a_frame);
			exporter.export(a_photoList);
		}
	}