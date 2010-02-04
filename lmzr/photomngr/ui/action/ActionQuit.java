package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;

/**
 * Action to quit
 */
public class ActionQuit extends PhotoManagerAction {

	final PhotoList a_list;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param list 
	 */
	public ActionQuit(final String text,
	                  final int mnemonic,
	                  final KeyStroke accelerator,
	                  final String tooltipText,
	                  final PhotoList list) {
        super(text, mnemonic, accelerator, tooltipText);
        a_list = list;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
	    controlledExit();
	}
	
    /**
     * 
     */
    public void controlledExit() {
	    if (a_list.isSaved()) {
			System.exit(0);
	    } else {
		    final int a = JOptionPane.showConfirmDialog(null,"Do you really want to exit without saving?","Exit",JOptionPane.OK_CANCEL_OPTION);
	        if ( a == JOptionPane.OK_OPTION ) {
				System.exit(0);		            
	        }
	    }
    }

}