package lmzr.photomngr.ui.action;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * Base class for actions
 *
 * @author Laurent Mazuré
 */
public abstract class PhotoManagerAction extends AbstractAction {

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     */
     public PhotoManagerAction(final String text,
                               final int mnemonic,
                               final KeyStroke accelerator,
                               final String tooltipText) {
        super(text);
        if (mnemonic!=KeyEvent.CHAR_UNDEFINED) putValue(MNEMONIC_KEY, Integer.valueOf(mnemonic));
        if (accelerator!=null) putValue(ACCELERATOR_KEY, accelerator);
        if (tooltipText!=null) putValue(SHORT_DESCRIPTION, tooltipText);
    }
}