package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.photomngr.ui.mapdisplayer.GeoportailMapURICreator;
import lmzr.photomngr.ui.mapdisplayer.MapURICreator;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent Mazuré
 *
 */
public class GPSDataDisplayMapCellEditor extends JButton
                                         implements TableCellEditor {
	
	final static MapURICreator s_geoportailMapURICreator = new GeoportailMapURICreator();

	/**
     * 
     */
    public GPSDataDisplayMapCellEditor() {
        super();        
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(final JTable table,
    		                                     final Object value,
                                                 final boolean isSelected, 
                                                 final int row,
                                                 final int column) {
    	
	    final GPSRecord record = (GPSRecord) value;
	    final HierarchicalCompoundString location = record.getLocation();
	    final GPSData GPSData = record.getGPSData();

	    if ( GPSData != null) {
			try {
				Desktop.getDesktop().browse(s_geoportailMapURICreator.createMapURIFromGPSData(record));
			} catch (final HeadlessException ex) {
				System.err.println("failed to start a browser to display the map of "+location.toString());
				ex.printStackTrace();
			} catch (final UnsupportedOperationException ex) {
				System.err.println("failed to start a browser to display the map of "+location.toString());
				ex.printStackTrace();
			} catch (final IOException ex) {
				System.err.println("failed to start a browser to display the map of "+location.toString());
				ex.printStackTrace();
			}
	    }
	    
	    setText("map " + record.getLocation().toShortString());
	    setEnabled( ( record.getGPSData() != null) &&  record.getGPSData().isComplete() );

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
    public boolean isCellEditable(final EventObject anEvent) {
        return true;
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(final EventObject anEvent) {
        return true;
    }

    /**
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void addCellEditorListener(final CellEditorListener l) {
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(final CellEditorListener l) {
    }
}
