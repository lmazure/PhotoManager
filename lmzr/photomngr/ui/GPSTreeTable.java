package lmzr.photomngr.ui;

import lmzr.photomngr.data.GPS.GPSDatabase;

import org.jdesktop.swingx.JXTreeTable;

public class GPSTreeTable extends JXTreeTable {

	GPSTreeTable(final GPSDatabase database) {
		super(database);
	}
}
