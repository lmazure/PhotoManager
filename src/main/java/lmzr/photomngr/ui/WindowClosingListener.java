package lmzr.photomngr.ui;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Laurent Mazuré
 *
 */
public class WindowClosingListener implements WindowListener {

    /**
     *
     */
    public interface Callback {
        /**
         *
         */
        void windowClosing();
    }

    private Callback a_callback;

    /**
     * @param window
     * @param callback
     */
    public WindowClosingListener(final Window window,
                                 final Callback callback) {
        window.addWindowListener(this);
        this.a_callback = callback;
    }

    /**
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(final WindowEvent arg0) {
    	// do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(final WindowEvent arg0) {
    	// do noting
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(final WindowEvent arg0) {
        this.a_callback.windowClosing();
    }

    /**
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(final WindowEvent arg0) {
    	// do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(final WindowEvent arg0) {
    	// do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(final WindowEvent arg0) {
    	// do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(final WindowEvent arg0) {
    	// do nothing
    }
}
