package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

/**
 * @author Laurent Mazur�
 */
public class TextFieldForFloatCellEditor extends JTextField implements TableCellEditor {

   final private Vector<CellEditorListener> a_listenerList;

	/**
	 * 
	 */
	TextFieldForFloatCellEditor() {
		super();
        a_listenerList = new Vector<CellEditorListener>();
	}
	
	/**
     * @param listener
     */
    @Override
	public void addCellEditorListener(final CellEditorListener listener) {
        a_listenerList.add(listener);
    } 
    
    /**
     * @param listener
     */
    @Override
	public void removeCellEditorListener(final CellEditorListener listener) {
        a_listenerList.remove(listener);
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
    
    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    @Override
	public void cancelCellEditing() {
        fireEditingCanceled();
    } 
    
    /**
     * @return x
     */
    @Override
	public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    } 
    
    /**
     * @param event
     * @return x
     */
    @Override
	public boolean isCellEditable(final EventObject event) {
        if (event == null) {
            // the cell is programaticaly edited
            return true;
        } else if ( event instanceof MouseEvent ) {
            final MouseEvent e = (MouseEvent)event;
            if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK ) return false;
            if ( e.getClickCount()!=2 ) return false;
            return true;
        } else if  ( event instanceof KeyEvent ) {
           	final KeyEvent e = (KeyEvent)event;
        	if ( (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 ) return false;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * @param event
     * @return x
     */
    @Override
	public boolean shouldSelectCell(final EventObject event) {
        return true;
    } 
        
    /**
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param column
     * @return x
     */
    @Override
	public Component getTableCellEditorComponent(final JTable table,
                                                 final Object value, 
                                                 final boolean isSelected,
                                                 final int row,
                                                 final int column) {
    	
        final NumberFormat format = NumberFormat.getInstance();
    	final String str = format.format(value);
    	setText(str);
        return this;
    }
    
    /**
     * @return selected element
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
	public Object getCellEditorValue() {
        return getText(); 
    }   

}
