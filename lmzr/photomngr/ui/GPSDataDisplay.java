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
import lmzr.photomngr.ui.action.ActionCopy;
import lmzr.photomngr.ui.action.ActionCopyFromNext;
import lmzr.photomngr.ui.action.ActionCopyFromPrevious;
import lmzr.photomngr.ui.action.ActionPaste;
import lmzr.photomngr.ui.action.ActionQuit;
import lmzr.photomngr.ui.action.ActionSave;

public class GPSDataDisplay extends JFrame {
	
	final private GPSTreeTable a_treeTable;
	final private JMenuBar a_menubar;
	
	GPSDataDisplay(final GPSDatabase database) {
		
		super();

		a_treeTable = new GPSTreeTable(database);
		a_treeTable.setRootVisible(true);

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

		final JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		a_menubar.add(menuEdit);
		ActionCopy actionCopy = new ActionCopy("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),"Copy",a_treeTable);
		final JMenuItem itemCopy = new JMenuItem(actionCopy);
		menuEdit.add(itemCopy);
		ActionPaste actionPaste = new ActionPaste("Paste", KeyEvent.VK_V, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),"Paste",a_treeTable);
		final JMenuItem itemPaste = new JMenuItem(actionPaste);
		menuEdit.add(itemPaste);
		ActionCopyFromPrevious actionCopyFromPrevious = new ActionCopyFromPrevious("Copy parameter from previous", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",a_treeTable);
		final JMenuItem itemCopyFromPrevious = new JMenuItem(actionCopyFromPrevious);
		menuEdit.add(itemCopyFromPrevious);
		ActionCopyFromNext actionCopyFromNext = new ActionCopyFromNext("Copy parameter from next", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",a_treeTable);
		final JMenuItem itemCopyFromNext = new JMenuItem(actionCopyFromNext);
		menuEdit.add(itemCopyFromNext);

		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		final JScrollPane scrollPane = new JScrollPane(a_treeTable);
		pane.add(scrollPane, BorderLayout.CENTER);
	}

}
