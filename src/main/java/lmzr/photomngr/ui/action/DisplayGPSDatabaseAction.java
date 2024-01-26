package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.GPSDataDisplay;

/**
 * @author Laurent Mazuré
 */
public class DisplayGPSDatabaseAction extends PhotoManagerAction {

    private final GPSDatabase a_GPSDatabase;
    private GPSDataDisplay a_GPSDisplay;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param GPSDatabase
     */
    public DisplayGPSDatabaseAction(final String text,
                                     final int mnemonic,
                                    final KeyStroke accelerator,
                                    final String tooltipText,
                                    final GPSDatabase GPSDatabase) {
        super(text, mnemonic, accelerator, tooltipText);
        a_GPSDatabase = GPSDatabase;
        a_GPSDisplay = null;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if ( a_GPSDisplay == null ) {
            a_GPSDisplay = new GPSDataDisplay(a_GPSDatabase);
        }

        a_GPSDisplay.setVisible(true);

    }
}