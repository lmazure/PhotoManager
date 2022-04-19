package lmzr.photomngr.ui.cellrenderer;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;

/**
 * @author Laurent Mazuré
 */
public class GPSDataDeleteCellRenderer extends JButton
                                           implements TableCellRenderer {



    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row,
                                                   final int column) {

        final GPSRecord record = (GPSRecord) value;
        setText("delete");
        setEnabled( record.getGPSData() != null );

        return this;
    }

}