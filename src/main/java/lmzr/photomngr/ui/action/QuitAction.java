package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.scheduler.Scheduler;

/**
 * Action to quit
 *
 * @author Laurent Mazuré
 */
public class QuitAction extends PhotoManagerAction {

    final private PhotoList a_photoList;
    final private GPSDatabase a_GPSDatabase;
    final private Scheduler a_scheduler;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param list
     * @param GPSDatabase
     * @param scheduler
     */
    public QuitAction(final String text,
                      final int mnemonic,
                      final KeyStroke accelerator,
                      final String tooltipText,
                      final PhotoList list,
                      final GPSDatabase GPSDatabase,
                      final Scheduler scheduler) {
        super(text, mnemonic, accelerator, tooltipText);
        a_photoList = list;
        a_GPSDatabase = GPSDatabase;
        a_scheduler = scheduler;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        controlledExit();
    }

    /**
     *
     */
    public void controlledExit() {
        if ( a_photoList.isSaved() && a_GPSDatabase.isSaved() ) {
            a_scheduler.submitIO("exit",
                    new Runnable() { @Override public void run() { System.exit(0); } });
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
                a_scheduler.submitIO("exit",
                        new Runnable() { @Override public void run() { System.exit(0); } });
            }
        }
    }

}