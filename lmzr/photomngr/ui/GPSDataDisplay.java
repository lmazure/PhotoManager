package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import lmzr.photomngr.data.GPS.GPSDatabase;

public class GPSDataDisplay extends JFrame {
	
	final private GPSTreeTable a_treeTable;
	
	GPSDataDisplay(final GPSDatabase database) {
		
		super();
		
		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		a_treeTable = new GPSTreeTable(database);
		final JScrollPane scrollPane = new JScrollPane(a_treeTable);
		pane.add(scrollPane, BorderLayout.CENTER);
	}

}
