package lmzr.photomngr.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * Action to copy
 */
public class ActionCopy extends PhotoManagerAction implements ClipboardOwner {

	final private JTable a_table;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param table 
	 */
	public ActionCopy(final String text,
                      final int mnemonic,
                      final KeyStroke accelerator,
                      final String tooltipText,
                      final JTable table) {
		super(text, mnemonic, accelerator, tooltipText);
		
		a_table = table;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {

		final ListSelectionModel selection = a_table.getSelectionModel();
		if ( selection.getMinSelectionIndex() == -1 ) return;

		int selectedColumn = a_table.getSelectedColumn();
		if ( selectedColumn == -1) return;

		final StringSelection fieldContent = new StringSelection(a_table.getValueAt(selection.getMinSelectionIndex(),selectedColumn).toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fieldContent, this);
	}
	
	/**
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(@SuppressWarnings("unused") Clipboard clipboard,
			                  @SuppressWarnings("unused") Transferable contents) {
	}
}
