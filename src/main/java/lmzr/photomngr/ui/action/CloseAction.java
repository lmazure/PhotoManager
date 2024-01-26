package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Action to close a JFrame
 *
 * @author Laurent Mazuré
 */
public class CloseAction extends PhotoManagerAction {

    private final JFrame a_frame;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param frame
     */
    public CloseAction(final String text,
                        final int mnemonic,
                       final KeyStroke accelerator,
                       final String tooltipText,
                       final JFrame frame) {
        super(text, mnemonic, accelerator, tooltipText);
        a_frame = frame;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        a_frame.dispose();
    }
}
