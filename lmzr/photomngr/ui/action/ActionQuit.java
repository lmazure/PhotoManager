package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;

/**
 * Action to quit
 */
public class ActionQuit extends PhotoManagerAction {

	final private PhotoList a_photoList;
	final private GPSDatabase a_GPSDatabase;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param list 
	 * @param GPSDatabase 
	 */
	public ActionQuit(final String text,
	                  final int mnemonic,
	                  final KeyStroke accelerator,
	                  final String tooltipText,
	                  final PhotoList list,
	                  final GPSDatabase GPSDatabase) {
        super(text, mnemonic, accelerator, tooltipText);
        a_photoList = list;
        a_GPSDatabase = GPSDatabase;
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
	    if ( a_photoList.isSaved() && a_GPSDatabase.isSaved() ) {
			System.exit(0);
	    } else {
	    	String message = "Do you really want to exit without saving ";
	    	if ( !a_photoList.isSaved() && !a_GPSDatabase.isSaved() ) {
	    		message += "the photo data and the GPS data?";
	    	} else if ( !a_photoList.isSaved() ) {
	    		message += "the photo data?";
	    	} else {
	    		message += "the GPS data?";
	    	}
		    final int a = JOptionPane.showConfirmDialog(null,message,"Exit",JOptionPane.OK_CANCEL_OPTION);
	        if ( a == JOptionPane.OK_OPTION ) {
				System.exit(0);		            
	        }
	    }
    }

}