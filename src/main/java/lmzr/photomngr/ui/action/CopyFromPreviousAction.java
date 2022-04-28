package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * Action to copy a parameter from the previous image
 *
 * @author Laurent Mazuré
 */
public class CopyFromPreviousAction extends PhotoManagerAction {

    final private JTable a_table;

    /**
     * @param text
     * @param mnemonic
     * @param accelerator
     * @param tooltipText
     * @param table
     */
    public CopyFromPreviousAction(final String text,
                                  final int mnemonic,
                                  final KeyStroke accelerator,
                                  final String tooltipText,
                                  final JTable table) {
        super(text, mnemonic, accelerator, tooltipText);

        this.a_table = table;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        final ListSelectionModel selection = this.a_table.getSelectionModel();
        if ( selection.getMinSelectionIndex() == -1 ) return;

        if (selection.getMinSelectionIndex()==0) return;

        int selectedColumn = this.a_table.getSelectedColumn();
        if ( selectedColumn == -1) return;

        final Object value = this.a_table.getValueAt(selection.getMinSelectionIndex()-1,selectedColumn);

        for (int i=selection.getMinSelectionIndex(); i<=selection.getMaxSelectionIndex(); i++) {
            if ( selection.isSelectedIndex(i) &&  this.a_table.isCellEditable(i,selectedColumn) ) {
                this.a_table.setValueAt(value,i,selectedColumn);
            }
        }
    }
}
