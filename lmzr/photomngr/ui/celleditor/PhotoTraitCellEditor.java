package lmzr.photomngr.ui.celleditor;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import lmzr.photomngr.data.phototrait.PhotoTrait;

/**
 * @author Laurent Mazuré
 */
public class PhotoTraitCellEditor extends ComboBoxCellEditor {
    
    
    /**
     * @param traits
     */
    public PhotoTraitCellEditor(final PhotoTrait traits[]) {
        super();
        for (int i=0; i<traits.length; i++) addItem(traits[i]);
    }
    
    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    @Override
	public boolean isCellEditable(final EventObject event) {
        if (event == null) {
            // the cell is programmatically edited
            return true;
        } else if ( event instanceof MouseEvent ) {
            final MouseEvent e = (MouseEvent)event;
            if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK ) return false;
            if ( e.getClickCount()!=2 ) return false;
            return true;
        } else if  ( event instanceof KeyEvent ) {
           	final KeyEvent e = (KeyEvent)event;
        	if ( (e.getModifiers() & InputEvent.CTRL_MASK) != 0 ) return false;
            return true;
        } else {
            return false;
        }
    }

}