package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;

/**
 * Action to save all the data
 */
public class ActionSave extends PhotoManagerAction {

	final PhotoList a_list;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param list 
	 */
	public ActionSave(final String text,
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
	    try {
	        a_list.save();
	    } catch (IOException e1) {
	        System.err.println("failed to save data");
	        e1.printStackTrace();
	        JOptionPane.showMessageDialog(null,
	        		                      "Failed to save data\n"+e1.toString(),
	        		                      "Save error",
	        		                      JOptionPane.ERROR_MESSAGE);
	    }
	}
}
