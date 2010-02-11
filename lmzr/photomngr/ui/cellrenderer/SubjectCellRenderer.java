package lmzr.photomngr.ui.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellRenderer;

/**
 * @author Laurent
 */
public class SubjectCellRenderer extends JTextArea
                                 implements TableCellRenderer {

    final private Vector<CellEditorListener> a_listenerList;

    /**
     * 
     */
    public SubjectCellRenderer() {
        super();
        a_listenerList = new Vector<CellEditorListener>();
    }
    
    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row,
                                                   final int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
          } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
          }
        setText(value.toString());
        final int preferredheight = getPreferredSize().height;
        if ( table.getRowHeight(row) < preferredheight ) {
        	table.setRowHeight(row,preferredheight);
        }
        if (hasFocus) {
        	Border border = null;
        	if (isSelected) {
        		border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
        	}
        	if (border == null) {
        		border = UIManager.getBorder("Table.focusCellHighlightBorder");
        	}
        	setBorder(border);

        	if (!isSelected && table.isCellEditable(row, column)) {
        		Color col;
        		col = UIManager.getColor("Table.focusCellForeground");
        		if (col != null) {
        			super.setForeground(col);
        		}
        		col = UIManager.getColor("Table.focusCellBackground");
        		if (col != null) {
        			super.setBackground(col);
        		}
        	}
        } else {
        	setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        return this;
    }

    /**
     * 
     */
    protected void fireEditingStopped() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = a_listenerList.size()-1; i>=0; i--) a_listenerList.get(i).editingStopped(e);
    } 

    /**
     * 
     */
    protected void fireEditingCanceled() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = a_listenerList.size()-1; i>=0; i--) a_listenerList.get(i).editingCanceled(e);
    }    
}
