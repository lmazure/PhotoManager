package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.JXTreeTable;

import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author lmazure
 *
 */
public class GPSDataDeleteCellEditor extends JButton
                                     implements TableCellEditor {
    
    /**
     * 
     */
    public GPSDataDeleteCellEditor() {
        super();        
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(final JTable table,
                                                 final Object value,
                                                 @SuppressWarnings("unused") final boolean isSelected, 
                                                 @SuppressWarnings("unused") final int row,
                                                 @SuppressWarnings("unused") final int column) {
        
        final JXTreeTable treeTable = (JXTreeTable) table;
        final GPSDatabase database = (GPSDatabase) treeTable.getTreeTableModel();
        final GPSRecord record = (GPSRecord) value;
        final HierarchicalCompoundString location = record.getLocation();

        database.setValueAt(null, location, GPSDatabase.PARAM_LATITUDE_MIN);
        database.setValueAt(null, location, GPSDatabase.PARAM_LATITUDE_MAX);
        database.setValueAt(null, location, GPSDatabase.PARAM_LONGITUDE_MIN);
        database.setValueAt(null, location, GPSDatabase.PARAM_LONGITUDE_MAX);
        
        setText("delete");
        setEnabled(false);

        return this;
    }

    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {
    }

    /**
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {        
        return true;
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return null;
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(@SuppressWarnings("unused") final EventObject anEvent) {
        return true;
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(@SuppressWarnings("unused") final EventObject anEvent) {
        return true;
    }

    /**
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void addCellEditorListener(@SuppressWarnings("unused") final CellEditorListener l) {
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(@SuppressWarnings("unused") final CellEditorListener l) {
    }
    
    /**
     * @param e
     */
    protected void fireCellEditing(final ChangeEvent e){
        
    }

}