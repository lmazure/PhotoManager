package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class PhotoParametersDisplay extends JFrame {
	
	final PhotoParametersTableModel a_tableModel;
	
	/**
	 * @param photoList
	 * @param selection
	 */
	public PhotoParametersDisplay(final PhotoList photoList,
                                  final ListSelectionManager selection) {
		super();

	
		a_tableModel = new PhotoParametersTableModel(photoList,selection);
		final JTable table = new JTable(a_tableModel);

		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		final JScrollPane scrollPane = new JScrollPane(table);
		pane.add(scrollPane, BorderLayout.CENTER);
		
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        pack();
	}
	
	/**
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		a_tableModel.dispose();
		super.dispose();
	}
}
