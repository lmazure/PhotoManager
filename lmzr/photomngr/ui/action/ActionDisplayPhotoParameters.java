package lmzr.photomngr.ui.action;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.PhotoParametersDisplay;

public class ActionDisplayPhotoParameters extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param photoList
	 * @param selection
	 */
	public ActionDisplayPhotoParameters(final String text,
			                            final int mnemonic,
			                            final KeyStroke accelerator,
		                                final String tooltipText,
			                            final PhotoList photoList,
			                            final ListSelectionManager selection) {
        super(text, mnemonic, accelerator, tooltipText);
		a_photoList = photoList;
        a_selection = selection;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent e) {

        final PhotoParametersDisplay a_PhotoParametersDisplay = new PhotoParametersDisplay(a_photoList, a_selection);
        a_PhotoParametersDisplay.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        a_PhotoParametersDisplay.setBounds(new Rectangle(0,0,900,400));
        a_PhotoParametersDisplay.setVisible(true);

	}
}
