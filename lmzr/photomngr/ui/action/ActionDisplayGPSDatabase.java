package lmzr.photomngr.ui.action;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.GPSDataDisplay;

/**
 * @author Laurent
 *
 */
public class ActionDisplayGPSDatabase extends PhotoManagerAction {

	private final GPSDatabase a_GPSDatabase;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param GPSDatabase 
	 */
	public ActionDisplayGPSDatabase(final String text,
 	                                final int mnemonic,
	                                final KeyStroke accelerator,
	                                final String tooltipText,
	                                final GPSDatabase GPSDatabase) {
        super(text, mnemonic, accelerator, tooltipText);
        a_GPSDatabase = GPSDatabase;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {

        final GPSDataDisplay a_GPSDisplay = new GPSDataDisplay(a_GPSDatabase);
        a_GPSDisplay.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        a_GPSDisplay.setBounds(new Rectangle(0,0,900,400));
        a_GPSDisplay.setVisible(true);

	}
}