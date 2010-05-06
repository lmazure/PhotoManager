package lmzr.photomngr.ui;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowClosingListener implements WindowListener {

	public interface Callback {
		void windowClosing();
	}
	
	private Callback a_callback;
	
	public WindowClosingListener(final Window window,
			                     final Callback callback) {
		window.addWindowListener(this);
		a_callback = callback;
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		a_callback.windowClosing();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
