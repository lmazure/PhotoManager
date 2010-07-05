package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.photomngr.ui.action.ActionCopy;
import lmzr.photomngr.ui.action.ActionCopyFromNext;
import lmzr.photomngr.ui.action.ActionCopyFromPrevious;
import lmzr.photomngr.ui.action.ActionPaste;
import lmzr.photomngr.ui.action.ActionQuit;
import lmzr.photomngr.ui.action.ActionRenameFolder;
import lmzr.photomngr.ui.action.ActionSave;


/**
 * @author Laurent
 */
public class PhotoListDisplay extends JFrame
                              implements ListSelectionListener, SaveListener {

	final private JMenuBar a_menubar;
	final private PhotoListTable a_table;
	final private PhotoList a_list;
	final private ListSelectionManager a_selection;
	final public ActionSave a_actionSave; //TODO méchant hack dans ActionRenameFolder, à nettoyer asap
	final private ActionRenameFolder a_actionRenameFolder;
	final private ActionCopy a_actionCopy;
	final private ActionPaste a_actionPaste;
	final private ActionCopyFromNext a_actionCopyFromNext;
	final private ActionCopyFromPrevious a_actionCopyFromPrevious;


    /**
     * @param unfilteredList
     * @param filteredList
     * @param GPSDatabase
     * @param scheduler
     */
    public PhotoListDisplay(final PhotoList unfilteredList,
    		                final PhotoList filteredList,
      		                final GPSDatabase GPSDatabase,
      		                final Scheduler scheduler) {
	    super();
	    
	    a_list = filteredList;

		a_table = new PhotoListTable(unfilteredList,filteredList);

	    saveChanged(new SaveEvent(filteredList,a_list.isSaved()));
        filteredList.addSaveListener(this);

		// listen to change of the selection column(s)
		a_selection = new ListSelectionManager(filteredList,getLineSelectionListModel());
		a_selection.addListener(this);
		
		// listen to change of the selection row(s)
		getColumnSelectionListModel().addListSelectionListener(this);
		
        a_menubar = new JMenuBar();
		setJMenuBar(a_menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		a_menubar.add(menuFile);
		a_actionSave = new ActionSave("Save photo data", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",filteredList);
		final JMenuItem itemSave = new JMenuItem(a_actionSave);
		menuFile.add(itemSave);
		final ActionQuit a_actionQuit = new ActionQuit("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",filteredList,GPSDatabase,scheduler);
		final JMenuItem itemQuit = new JMenuItem(a_actionQuit);
		menuFile.add(itemQuit);

		final JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		a_menubar.add(menuEdit);
		a_actionCopy = new ActionCopy("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),"Copy",a_table);
		final JMenuItem itemCopy = new JMenuItem(a_actionCopy);
		menuEdit.add(itemCopy);
		a_actionPaste = new ActionPaste("Paste", KeyEvent.VK_V, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),"Paste",a_table);
		final JMenuItem itemPaste = new JMenuItem(a_actionPaste);
		menuEdit.add(itemPaste);
		a_actionCopyFromPrevious = new ActionCopyFromPrevious("Copy parameter from previous", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",a_table);
		final JMenuItem itemCopyFromPrevious = new JMenuItem(a_actionCopyFromPrevious);
		menuEdit.add(itemCopyFromPrevious);
		a_actionCopyFromNext = new ActionCopyFromNext("Copy parameter from next", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",a_table);
		final JMenuItem itemCopyFromNext = new JMenuItem(a_actionCopyFromNext);
		menuEdit.add(itemCopyFromNext);
		a_actionRenameFolder = new ActionRenameFolder("Rename folder", KeyEvent.CHAR_UNDEFINED, null,"Rename the folder",this,a_list,a_selection);
		final JMenuItem itemRenameFolder = new JMenuItem(a_actionRenameFolder);
		menuEdit.add(itemRenameFolder);

		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		final JScrollPane scrollPane = new JScrollPane(a_table);
		pane.add(scrollPane, BorderLayout.CENTER);

		a_table.getInputMap().put((KeyStroke)a_actionCopy.getValue(Action.ACCELERATOR_KEY),a_actionCopy.getValue(Action.NAME));
		a_table.getActionMap().put(a_actionCopy.getValue(Action.NAME),a_actionCopy);
		a_table.getInputMap().put((KeyStroke)a_actionPaste.getValue(Action.ACCELERATOR_KEY),a_actionPaste.getValue(Action.NAME));
		a_table.getActionMap().put(a_actionPaste.getValue(Action.NAME),a_actionPaste);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		new WindowClosingListener(this,
                new WindowClosingListener.Callback() { @Override public void windowClosing() { a_actionQuit.controlledExit(); }});
}
    
    /**
     * @return selection list model
     */
    public ListSelectionModel getLineSelectionListModel() {
        return a_table.getSelectionModel();
    }

    /**
     * @return selection list model
     */
    public ListSelectionModel getColumnSelectionListModel() {
        return a_table.getColumnModel().getSelectionModel();
    }

    /**
     * @see lmzr.photomngr.data.SaveListener#saveChanged(lmzr.photomngr.data.SaveEvent)
     */
    public void saveChanged(final SaveEvent e) {
        setTitle("photo list - [photo data is " + (e.isSaved() ? "saved]" : "modified]") );
    }
    
    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(final ListSelectionEvent e) {

		final int select[] = a_selection.getSelection();

		a_actionRenameFolder.setEnabled( select.length == 1 );
    
		a_actionCopy.setEnabled(a_table.getSelectedColumn()!=-1);
		
		// ne marche pas car il faut aussi écouter la sélection de la colonne
    	boolean editable;
    	
		if ( select.length==0 ||
			 a_table.getSelectedColumn()==-1 ) {
			editable = false;
		} else {
			final int col = a_table.convertColumnIndexToModel(a_table.getSelectedColumn());
			editable = true;
			for (int i=0; i<select.length; i++) {
				editable &= a_list.isCellEditable(select[i],col);
			}
		}
		
		a_actionPaste.setEnabled(editable);
		a_actionCopyFromNext.setEnabled( editable && (select[select.length-1]!=a_list.getRowCount()-1));
		a_actionCopyFromPrevious.setEnabled( editable && (select[0]!=0));
    }
}
