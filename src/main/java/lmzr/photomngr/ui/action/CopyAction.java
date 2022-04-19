package lmzr.photomngr.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import lmzr.photomngr.data.phototrait.PhotoTrait;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundString;

/**
 * Action to copy
 * 
 * @author Laurent Mazuré
 */
public class CopyAction extends PhotoManagerAction implements ClipboardOwner {

	final private JTable a_table;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param table 
	 */
	public CopyAction(final String text,
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
	@Override
	public void actionPerformed(final ActionEvent e) {

		final ListSelectionModel selection = a_table.getSelectionModel();
		if ( selection.getMinSelectionIndex() == -1 ) return;

		final int selectedColumn = a_table.getSelectedColumn();
		if ( selectedColumn == -1) return;

		Object value = a_table.getValueAt(selection.getMinSelectionIndex(),selectedColumn);
		String str;
		if (value instanceof String) {
			str = (String)value;
		} else if (value instanceof HierarchicalCompoundString) {
			str = ((HierarchicalCompoundString)value).toString();
		} else if (value instanceof MultiHierarchicalCompoundString) {
			str = ((MultiHierarchicalCompoundString)value).toString();
		} else if (value instanceof PhotoTrait) {
			str = ((PhotoTrait)value).toString();
		} else if (value instanceof Integer) {
			str = NumberFormat.getInstance().format((Integer)value);
		} else if (value instanceof Float) {
			str = NumberFormat.getInstance().format((Float)value);
		} else {
			throw new IllegalArgumentException("Unsupported type");
		}
		final StringSelection fieldContent = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fieldContent, this);
	}
	
	/**
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(final Clipboard clipboard,
			                  final Transferable contents) {
	}
}
