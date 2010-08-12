package lmzr.photomngr.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * Action to paste
 */
public class PasteAction extends PhotoManagerAction {

	final private JTable a_table;
	
	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param table 
	 */
	public PasteAction(final String text,
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
	public void actionPerformed(final ActionEvent e) {
		
		final Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);			
		String text = null;
		
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				text = (String)t.getTransferData(DataFlavor.stringFlavor);
			} else {
				return;
			}
		} catch (final UnsupportedFlavorException e1) {
			e1.printStackTrace();
			return;
		} catch (final IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		final ListSelectionModel selection = a_table.getSelectionModel();
		if ( selection.getMinSelectionIndex() == -1 ) return;
		
		int selectedColumn = a_table.getSelectedColumn();
		if ( selectedColumn == -1) return;

        for (int i=selection.getMinSelectionIndex(); i<=selection.getMaxSelectionIndex(); i++) {
        	if ( selection.isSelectedIndex(i) &&  a_table.isCellEditable(i,selectedColumn) ) {
        		a_table.setValueAt(text,i,selectedColumn);
        	}
        }
	}
}
	