package lmzr.photomngr.ui.cellrenderer;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author lmazure
 *
 */
public class GPSDataDisplayMapCellRenderer extends JButton
                                           implements TableCellRenderer {



    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(@SuppressWarnings("unused") final JTable table,
                                                   final Object value,
                                                   @SuppressWarnings("unused") final boolean isSelected,
                                                   @SuppressWarnings("unused") final boolean hasFocus,
                                                   @SuppressWarnings("unused") final int row,
                                                   @SuppressWarnings("unused") final int column) {

        final GPSRecord record = (GPSRecord) value;
        setText("map " + record.getLocation().toShortString());
        setEnabled( ( record.getGPSData() != null) &&  record.getGPSData().isComplete() );

        return this;
    }

}