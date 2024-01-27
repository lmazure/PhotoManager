
package lmzr.photomngr.ui.celleditor;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * @author Laurent Mazuré
 */
public class CopiesCellEditor extends ComboBoxCellEditor {

    /**
     *
     */
    public CopiesCellEditor() {
        setEditable(true);
        for (int i=0; i<=5; i++) {
            addItem(Integer.valueOf(i));
        }
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

}
