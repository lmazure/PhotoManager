package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTable;

import lmzr.photomngr.data.AuthorFactory;

/**
 *
 */
public class AuthorCellEditor extends ComboBoxCellEditor {

    private final AuthorFactory a_factory;

    /**
     * @param factory
     *
     */
    public AuthorCellEditor(final AuthorFactory factory) {
        a_factory = factory;
        setEditable(true);
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    @Override
    public boolean isCellEditable(final EventObject event) {
        if (event == null) {
            // the cell is programmatically edited
            return true;
        }
        if ( event instanceof final MouseEvent e ) {
            if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK ) {
                return false;
            }
            if ( e.getClickCount()!=2 ) {
                return false;
            }
            return true;
        } else if  ( event instanceof final KeyEvent e ) {
               if ( (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 ) {
                return false;
            }
            return true;
        } else {
            return false;
        }
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
        removeAllItems();
        final String authors[] = a_factory.getAuthors();
        for (final String author : authors) {
            addItem(author);
        }
        return this;
    }
}
