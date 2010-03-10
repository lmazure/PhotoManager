package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.SaveableModel;

/**
 * Action to save all the data
 */
public class ActionSave extends PhotoManagerAction {

	private final SaveableModel a_model;

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
	                  final SaveableModel list) {
        super(text, mnemonic, accelerator, tooltipText);
        a_model = list;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
	    try {
	        a_model.save();
	    } catch (final IOException e1) {
	        System.err.println("failed to save data");
	        e1.printStackTrace();
	        JOptionPane.showMessageDialog(null,
	        		                      "Failed to save data\n"+e1.toString(),
	        		                      "Save error",
	        		                      JOptionPane.ERROR_MESSAGE);
	    }
	}
}
