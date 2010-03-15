package lmzr.photomngr.ui;

import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.celleditor.GPSDataCellEditor;
import lmzr.photomngr.ui.cellrenderer.GPSDataCellRenderer;

import org.jdesktop.swingx.JXTreeTable;

public class GPSTreeTable extends JXTreeTable {

	GPSTreeTable(final GPSDatabase database) {
		
		super(database);
		
		getColumnModel().getColumn(GPSDatabase.PARAM_GPS_DATA).setCellRenderer(new GPSDataCellRenderer());
		getColumnModel().getColumn(GPSDatabase.PARAM_GPS_DATA).setCellEditor(new GPSDataCellEditor());

	}
}
