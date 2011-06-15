package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;


import javax.swing.KeyStroke;

import lmzr.photomngr.ui.PhotoDisplayer;

/**
 * Action to display the display full screen
 * 
 * @author Laurent Mazuré
 */
public class FullScreenAction extends PhotoManagerAction {

	private final PhotoDisplayer a_photoDisplayer;
	
	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param photoDisplayer 
	 */
	public FullScreenAction(final String text,
		         	        final int mnemonic,
		         	        final KeyStroke accelerator,
		         	        final String tooltipText,
		         	        final PhotoDisplayer photoDisplayer) {
		super(text, mnemonic, accelerator, tooltipText);
		a_photoDisplayer = photoDisplayer;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent e) {
		a_photoDisplayer.setFullScreen(!a_photoDisplayer.getFullScreen());
	}
}