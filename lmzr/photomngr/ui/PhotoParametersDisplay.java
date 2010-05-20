package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.action.ActionClose;

public class PhotoParametersDisplay extends JFrame {
	
	final PhotoParametersTableModel a_tableModel;
	
	public PhotoParametersDisplay(final PhotoList photoList,
                                  final ListSelectionManager selection) {
		super();

	    final JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menubar.add(menuFile);
		final ActionClose a_actionClose = new ActionClose("Close", KeyEvent.VK_UNDEFINED, null,"Close the window",this);
		final JMenuItem itemClose = new JMenuItem(a_actionClose);
		menuFile.add(itemClose);
		
		a_tableModel = new PhotoParametersTableModel(photoList,selection);
		final JTable table = new JTable(a_tableModel);

		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		final JScrollPane scrollPane = new JScrollPane(table);
		pane.add(scrollPane, BorderLayout.CENTER);
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
