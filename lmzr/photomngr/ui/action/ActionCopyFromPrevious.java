package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * Action to copy a parameter from the previous image
 */
public class ActionCopyFromPrevious extends PhotoManagerAction {

	final private JTable a_table;

	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param table 
	 */
	public ActionCopyFromPrevious(final String text,
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
		
		final ListSelectionModel selection = a_table.getSelectionModel();
		if ( selection.getMinSelectionIndex() == -1 ) return;

		if (selection.getMinSelectionIndex()==0) return;

		int selectedColumn = a_table.getSelectedColumn();
		if ( selectedColumn == -1) return;
		
        final Object value = a_table.getValueAt(selection.getMinSelectionIndex()-1,selectedColumn);
        
        for (int i=selection.getMinSelectionIndex(); i<=selection.getMaxSelectionIndex(); i++) {
        	if ( selection.isSelectedIndex(i) &&  a_table.isCellEditable(i,selectedColumn) ) {
        		a_table.setValueAt(value,i,selectedColumn);
        	}
        }
	}
}
