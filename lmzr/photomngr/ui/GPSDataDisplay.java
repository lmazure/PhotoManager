package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.action.ActionQuit;
import lmzr.photomngr.ui.action.ActionSave;

public class GPSDataDisplay extends JFrame {
	
	final private GPSTreeTable a_treeTable;
	final private JMenuBar a_menubar;
	
	GPSDataDisplay(final GPSDatabase database) {
		
		super();
		
        a_menubar = new JMenuBar();
		setJMenuBar(a_menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		a_menubar.add(menuFile);
		ActionSave a_actionSave = new ActionSave("Save", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",null /*filteredList*/); //TODO gérer le dernier argument
		final JMenuItem itemSave = new JMenuItem(a_actionSave);
		menuFile.add(itemSave);
		final ActionQuit actionQuit = new ActionQuit("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",null /*filteredList*/);  //TODO gérer le dernier argument
		final JMenuItem itemQuit = new JMenuItem(actionQuit);
		menuFile.add(itemQuit);

		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		a_treeTable = new GPSTreeTable(database);
		final JScrollPane scrollPane = new JScrollPane(a_treeTable);
		pane.add(scrollPane, BorderLayout.CENTER);
	}

}
