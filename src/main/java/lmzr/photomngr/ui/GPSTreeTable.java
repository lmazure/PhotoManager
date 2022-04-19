package lmzr.photomngr.ui;

import javax.swing.table.TableColumnModel;

import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.celleditor.GPSDataDeleteCellEditor;
import lmzr.photomngr.ui.celleditor.GPSDataDisplayMapCellEditor;
import lmzr.photomngr.ui.cellrenderer.GPSDataDeleteCellRenderer;
import lmzr.photomngr.ui.cellrenderer.GPSDataDisplayMapCellRenderer;

import org.jdesktop.swingx.JXTreeTable;

/**
 * @author Laurent Mazuré
 */
public class GPSTreeTable extends JXTreeTable {

	/**
	 * @param database
	 */
	GPSTreeTable(final GPSDatabase database) {
		
		super(database);
		
		final TableColumnModel colModel = getColumnModel();
		
		colModel.getColumn(GPSDatabase.PARAM_GPS_DATA_FOR_MAPPING).setCellRenderer(new GPSDataDisplayMapCellRenderer());
		colModel.getColumn(GPSDatabase.PARAM_GPS_DATA_FOR_MAPPING).setCellEditor(new GPSDataDisplayMapCellEditor());
		colModel.getColumn(GPSDatabase.PARAM_GPS_DATA_FOR_DELETING).setCellRenderer(new GPSDataDeleteCellRenderer());
		colModel.getColumn(GPSDatabase.PARAM_GPS_DATA_FOR_DELETING).setCellEditor(new GPSDataDeleteCellEditor());

		getColumnModel().getColumn(GPSDatabase.PARAM_LOCATION).setPreferredWidth(500);
		getColumnModel().getColumn(GPSDatabase.PARAM_LATITUDE_MIN).setPreferredWidth(120);
		getColumnModel().getColumn(GPSDatabase.PARAM_LATITUDE_MAX).setPreferredWidth(120);
		getColumnModel().getColumn(GPSDatabase.PARAM_LONGITUDE_MIN).setPreferredWidth(120);
		getColumnModel().getColumn(GPSDatabase.PARAM_LONGITUDE_MAX).setPreferredWidth(120);
		getColumnModel().getColumn(GPSDatabase.PARAM_GPS_DATA_FOR_MAPPING).setPreferredWidth(200);
		getColumnModel().getColumn(GPSDatabase.PARAM_GPS_DATA_FOR_DELETING).setPreferredWidth(200);
	}
}
