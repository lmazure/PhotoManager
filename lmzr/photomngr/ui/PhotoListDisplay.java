package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.PhotoListMetaDataEvent;
import lmzr.photomngr.data.PhotoListMetaDataListener;
import lmzr.photomngr.ui.action.ActionQuit;
import lmzr.photomngr.ui.action.ActionSave;
import lmzr.photomngr.ui.action.PhotoManagerAction;


/**
 * @author Laurent
 */
public class PhotoListDisplay extends JFrame
                              implements ListSelectionListener, PhotoListMetaDataListener {

	final private JMenuBar a_menubar;
	final private PhotoListTable a_table;
	final PhotoList a_list;
	final ListSelectionManager a_selection;
	final private ActionSave a_actionSave;
	final private ActionRenameFolder a_actionRenameFolder;
	final private ActionCopy a_actionCopy;
	final private ActionPaste a_actionPaste;
	final private ActionCopyFromNext a_actionCopyFromNext;
	final private ActionCopyFromPrevious a_actionCopyFromPrevious;
	
	/**
	 * Action to paste
	 */
	private class ActionPaste extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionPaste(final String text,
                           final int mnemonic,
                           final KeyStroke accelerator,
                           final String tooltipText) {
			super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			
			final Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);			
			String text = null;
			
			try {
				if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					text = (String)t.getTransferData(DataFlavor.stringFlavor);
				}
			} catch (final UnsupportedFlavorException e1) {
				e1.printStackTrace();
				return;
			} catch (final IOException e1) {
				e1.printStackTrace();
				return;
			}
			
			final int select[] = a_selection.getSelection();
			if (select.length==0) return;

			if ( a_table.getSelectedColumn() == -1) return;
			int col = a_table.convertColumnIndexToModel(a_table.getSelectedColumn());
			
			for (int i=0; i<select.length; i++) {
				if ( a_list.isCellEditable(select[i],col)) a_list.setValueAt(text,select[i],col);
			}
		}
	}
	
	/**
	 * Action to copy
	 */
	private class ActionCopy extends PhotoManagerAction implements ClipboardOwner {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionCopy(final String text,
                          final int mnemonic,
                          final KeyStroke accelerator,
                          final String tooltipText) {
			super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {

			final int select[] = a_selection.getSelection();
			if (select.length!=1) return;

			if ( a_table.getSelectedColumn() == -1) return;
			int col = a_table.convertColumnIndexToModel(a_table.getSelectedColumn());

			final StringSelection fieldContent = new StringSelection(a_list.getValueAt(select[0],col).toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fieldContent, this);
		}
		
		/**
		 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
		 */
		public void lostOwnership(@SuppressWarnings("unused") Clipboard clipboard,
				                  @SuppressWarnings("unused") Transferable contents) {
		}
	}
	
	/**
	 * Action to copy a parameter from the previous image
	 */
	private class ActionCopyFromPrevious extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionCopyFromPrevious(final String text,
                                      final int mnemonic,
                                      final KeyStroke accelerator,
                                      final String tooltipText) {
			super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			
			final int select[] = a_selection.getSelection();
			if (select.length==0) return;
			if (select[0]==0) return;

			if ( a_table.getSelectedColumn() == -1) return;
			final int col = a_table.convertColumnIndexToModel(a_table.getSelectedColumn());
			
            final Object value = a_list.getValueAt(select[0]-1,col);
			for (int i=0; i<select.length; i++) {
				if ( a_list.isCellEditable(select[i],col)) a_list.setValueAt(value,select[i],col);
			}

		}
	}
	
	/**
	 * Action to copy a parameter from the next image
	 */
	private class ActionCopyFromNext extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionCopyFromNext(final String text,
                                  final int mnemonic,
                                  final KeyStroke accelerator,
                                  final String tooltipText) {
			super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			
			final int select[] = a_selection.getSelection();
			if (select.length==0) return;
			if (select[select.length-1]==a_list.getRowCount()-1) return;

			if ( a_table.getSelectedColumn() == -1) return;
			final int col = a_table.convertColumnIndexToModel(a_table.getSelectedColumn());
			
			final Object value = a_list.getValueAt(select[select.length-1]+1,col);
			
			for (int i=0; i<select.length; i++) {
				if ( a_list.isCellEditable(select[i],col)) a_list.setValueAt(value,select[i],col);
			}
		}
	}
	

	/**
	 * Action to rename a folder
	 */
	private class ActionRenameFolder extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionRenameFolder(final String text,
                                  final int mnemonic,
                                  final KeyStroke accelerator,
                                  final String tooltipText) {
			super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			
			if (!a_list.isSaved()) {
		        JOptionPane.showMessageDialog(PhotoListDisplay.this,
	                                          "Cannot rename a file if the current data is not saved",
	                                          "Rename error",
	                                          JOptionPane.ERROR_MESSAGE);
				return;
			}
			
		    final Photo photo = a_list.getPhoto(a_selection.getSelection()[0]);
			final String oldFolderName = photo.getFolder();
			final String newFolderName = JOptionPane.showInputDialog(PhotoListDisplay.this,
					                                                 "new folder name?",
					                                                 oldFolderName);
		    if (newFolderName == null) return;  // user clicked cancel

		    // rename the directory
		    final File photoFile = new File(photo.getFullPath());
		    final File oldFolderFile = photoFile.getParentFile();
		    final File newFolderFile = new File(oldFolderFile.getParentFile(),newFolderName);
		    if (!oldFolderFile.renameTo(newFolderFile)) {
		        JOptionPane.showMessageDialog(PhotoListDisplay.this,
                        "Failed to rename \""+oldFolderFile+"\" into \""+newFolderFile+"\"",
                        "Rename error",
                        JOptionPane.ERROR_MESSAGE);
		        return;
		    }
		    
		    // update the data recorded in the list
		    for (int i=0; i<a_list.getRowCount(); i++) {
		    	if ( ((String)a_list.getValueAt(i, PhotoList.PARAM_FOLDER)).equals(oldFolderName) ) {
		    		a_list.setValueAt(newFolderName, i, PhotoList.PARAM_FOLDER);
		    	}
		    }

		    // save
			a_actionSave.actionPerformed(e);
		}
	}
	

    /**
     * @param unfilteredList
     * @param filteredList
     */
    public PhotoListDisplay(final PhotoList unfilteredList,
    		                final PhotoList filteredList) {
	    super();
	    
	    a_list = filteredList;
	    
	    photoListMetaDataChanged(new PhotoListMetaDataEvent(filteredList,PhotoListMetaDataEvent.PHOTOLIST_IS_SAVED));
        filteredList.addMetaListener(this);

        a_menubar = new JMenuBar();
		setJMenuBar(a_menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		a_menubar.add(menuFile);
		a_actionSave = new ActionSave("Save", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",filteredList);
		final JMenuItem itemSave = new JMenuItem(a_actionSave);
		menuFile.add(itemSave);
		final ActionQuit actionQuit = new ActionQuit("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",filteredList);
		final JMenuItem itemQuit = new JMenuItem(actionQuit);
		menuFile.add(itemQuit);


		final JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		a_menubar.add(menuEdit);
		a_actionCopy = new ActionCopy("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),"Copy");
		final JMenuItem itemCopy = new JMenuItem(a_actionCopy);
		menuEdit.add(itemCopy);
		a_actionPaste = new ActionPaste("Paste", KeyEvent.VK_V, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),"Paste");
		final JMenuItem itemPaste = new JMenuItem(a_actionPaste);
		menuEdit.add(itemPaste);
		a_actionCopyFromPrevious = new ActionCopyFromPrevious("Copy parameter from previous", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo");
		final JMenuItem itemCopyFromPrevious = new JMenuItem(a_actionCopyFromPrevious);
		menuEdit.add(itemCopyFromPrevious);
		a_actionCopyFromNext = new ActionCopyFromNext("Copy parameter from next", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo");
		final JMenuItem itemCopyFromNext = new JMenuItem(a_actionCopyFromNext);
		menuEdit.add(itemCopyFromNext);
		a_actionRenameFolder = new ActionRenameFolder("Rename folder", KeyEvent.CHAR_UNDEFINED, null,"Rename the folder");
		final JMenuItem itemRenameFolder = new JMenuItem(a_actionRenameFolder);
		menuEdit.add(itemRenameFolder);

		final Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		a_table = new PhotoListTable(unfilteredList,filteredList);
		final JScrollPane scrollPane = new JScrollPane(a_table);
		pane.add(scrollPane, BorderLayout.CENTER);

		a_table.getInputMap().put((KeyStroke)a_actionCopy.getValue(Action.ACCELERATOR_KEY),a_actionCopy.getValue(Action.NAME));
		a_table.getActionMap().put(a_actionCopy.getValue(Action.NAME),a_actionCopy);
		a_table.getInputMap().put((KeyStroke)a_actionPaste.getValue(Action.ACCELERATOR_KEY),a_actionPaste.getValue(Action.NAME));
		a_table.getActionMap().put(a_actionPaste.getValue(Action.NAME),a_actionPaste);

		// listen to change of the selection column(s)
		a_selection = new ListSelectionManager(filteredList,getLineSelectionListModel());
		a_selection.addListener(this);
		
		// listen to change of the selection row(s)
		getColumnSelectionListModel().addListSelectionListener(this);
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
     * @see lmzr.photomngr.data.PhotoListMetaDataListener#photoListMetaDataChanged(lmzr.photomngr.data.PhotoListMetaDataEvent)
     */
    public void photoListMetaDataChanged(final PhotoListMetaDataEvent e) {
    	if (e.getChange()==PhotoListMetaDataEvent.PHOTOLIST_IS_SAVED) {
            final PhotoList list = (PhotoList)e.getSource();
            setTitle("photo list " + (list.isSaved() ? "[saved]" : "[modified]") );
        }
    }
    
    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(@SuppressWarnings("unused") final ListSelectionEvent e) {

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
