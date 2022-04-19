package lmzr.photomngr.ui.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.util.string.HierarchicalCompoundString;

/**
 * Common code to export the list of subjects and export the list of locations
 * 
 * @author Laurent Mazuré
 */

public abstract class ExportAction extends PhotoManagerAction {

	final private JFrame a_frame;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param frame 
	 */
	public ExportAction(final String text,
			            final int mnemonic,
			            final KeyStroke accelerator,
			            final String tooltipText,
			            final JFrame frame) {
		super(text, mnemonic, accelerator, tooltipText);
		a_frame = frame;
	}

	private void dump(final BufferedWriter out,
			          final HierarchicalCompoundString string) {
		try {
			out.write(string.toLongString());
			out.write("\n");
		} catch (IOException e1) {
			System.err.println("failed to export subjects");
			e1.printStackTrace();
			JOptionPane.showMessageDialog(a_frame,
					"Failed to export subjects\n"+e1.toString(),
					"Save error",
					JOptionPane.ERROR_MESSAGE);
		}
		for (HierarchicalCompoundString s: string.getChildren()) dump(out,s);
	}

	/**
	 * @param root
	 */
	protected void dumpRoot(final HierarchicalCompoundString root) {
		final JFileChooser fc = new JFileChooser((File)null);
		fc.showSaveDialog(a_frame);
		final File f = fc.getSelectedFile();
		if ( f == null ) return;
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(f));
			for (HierarchicalCompoundString s: root.getChildren()) dump(out,s);
			out.close();
		} catch (final IOException e1) {
			System.err.println("failed to export subjects");
			e1.printStackTrace();
			JOptionPane.showMessageDialog(a_frame,
					"Failed to export subjects\n"+e1.toString(),
					"Save error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
