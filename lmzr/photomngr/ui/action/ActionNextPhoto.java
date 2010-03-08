package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;

/**
 * Action to display the next photo
 */
public class ActionNextPhoto extends PhotoManagerAction {

	final private ListSelectionManager a_selection;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 */
	public ActionNextPhoto(final String text,
			final int mnemonic,
			final KeyStroke accelerator,
			final String tooltipText,
			final ListSelectionManager selection) {
		super(text, mnemonic, accelerator, tooltipText);
		a_selection = selection;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		a_selection.next(1);
	}
}