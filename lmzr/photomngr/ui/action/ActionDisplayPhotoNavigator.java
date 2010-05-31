package lmzr.photomngr.ui.action;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.PhotoNavigator;

public class ActionDisplayPhotoNavigator extends PhotoManagerAction {

	   final private PhotoList a_photoList;
	   final private GPSDatabase a_GPSDatabase;
	   final private ListSelectionManager a_selection;

		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param photoList
		 * @param selection
		 */
		public ActionDisplayPhotoNavigator(final String text,
				                           final int mnemonic,
				                           final KeyStroke accelerator,
				                           final String tooltipText,
				                           final PhotoList photoList,
				                           final GPSDatabase GPSDatabase,
				                           final ListSelectionManager selection) {
	        super(text, mnemonic, accelerator, tooltipText);
			a_photoList = photoList;
			a_GPSDatabase = GPSDatabase;
	        a_selection = selection;
		}


		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {

	        final PhotoNavigator a_PhotoNavigator = new PhotoNavigator(a_photoList, a_GPSDatabase, a_selection);
	        a_PhotoNavigator.setBounds(new Rectangle(0,0,300,250));
	        a_PhotoNavigator.setVisible(true);
		}
}
