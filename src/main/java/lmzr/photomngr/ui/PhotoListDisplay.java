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
import lmzr.photomngr.ui.action.CopyAction;
import lmzr.photomngr.ui.action.CopyFromNextAction;
import lmzr.photomngr.ui.action.CopyFromPreviousAction;
import lmzr.photomngr.ui.action.CutAction;
import lmzr.photomngr.ui.action.PasteAction;
import lmzr.photomngr.ui.action.QuitAction;
import lmzr.photomngr.ui.action.RenameFolderAction;
import lmzr.photomngr.ui.action.SaveAction;


/**
 * @author Laurent
 */
public class PhotoListDisplay extends JFrame
                              implements ListSelectionListener, SaveListener {

    final private JMenuBar a_menubar;
    final private PhotoListTable a_table;
    final private PhotoList a_list;
    final private ListSelectionManager a_selection;
    final private SaveAction a_actionSave;
    final private RenameFolderAction a_actionRenameFolder;
    final private CopyAction a_actionCopy;
    final private CutAction a_actionCut;
    final private PasteAction a_actionPaste;
    final private CopyFromNextAction a_actionCopyFromNext;
    final private CopyFromPreviousAction a_actionCopyFromPrevious;


    /**
     * @param photoList
     * @param filteredPhotoList
     * @param GPSDatabase
     * @param scheduler
     */
    public PhotoListDisplay(final PhotoList photoList,
                            final PhotoList filteredPhotoList,
                              final GPSDatabase GPSDatabase,
                              final Scheduler scheduler) {
        super();

        this.a_list = photoList;

        this.a_table = new PhotoListTable(photoList, filteredPhotoList,this);

        // listen to change of the selection column(s)
        this.a_selection = new ListSelectionManager(photoList,getLineSelectionListModel());
        this.a_selection.addListener(this);

        // listen to change of the selection row(s)
        getColumnSelectionListModel().addListSelectionListener(this);

        this.a_menubar = new JMenuBar();
        setJMenuBar(this.a_menubar);

        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        this.a_menubar.add(menuFile);
        this.a_actionSave = new SaveAction("Save photo data", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",this.a_list);
        final JMenuItem itemSave = new JMenuItem(this.a_actionSave);
        menuFile.add(itemSave);
        final QuitAction a_actionQuit = new QuitAction("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",photoList,GPSDatabase,scheduler);
        final JMenuItem itemQuit = new JMenuItem(a_actionQuit);
        menuFile.add(itemQuit);

        final JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic(KeyEvent.VK_E);
        this.a_menubar.add(menuEdit);
        this.a_actionCopy = new CopyAction("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),"Copy",this.a_table);
        final JMenuItem itemCopy = new JMenuItem(this.a_actionCopy);
        menuEdit.add(itemCopy);
        this.a_actionCut = new CutAction("Cut", KeyEvent.VK_U, KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK),"Cut",this.a_table);
        final JMenuItem itemCut = new JMenuItem(this.a_actionCut);
        menuEdit.add(itemCut);
        this.a_actionPaste = new PasteAction("Paste", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),"Paste",this.a_table);
        final JMenuItem itemPaste = new JMenuItem(this.a_actionPaste);
        menuEdit.add(itemPaste);
        this.a_actionCopyFromPrevious = new CopyFromPreviousAction("Copy parameter from previous", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",this.a_table);
        final JMenuItem itemCopyFromPrevious = new JMenuItem(this.a_actionCopyFromPrevious);
        menuEdit.add(itemCopyFromPrevious);
        this.a_actionCopyFromNext = new CopyFromNextAction("Copy parameter from next", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",this.a_table);
        final JMenuItem itemCopyFromNext = new JMenuItem(this.a_actionCopyFromNext);
        menuEdit.add(itemCopyFromNext);
        this.a_actionRenameFolder = new RenameFolderAction("Rename folder", KeyEvent.CHAR_UNDEFINED, null,"Rename the folder",this,this.a_list,this.a_selection);
        final JMenuItem itemRenameFolder = new JMenuItem(this.a_actionRenameFolder);
        menuEdit.add(itemRenameFolder);

        final Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        final JScrollPane scrollPane = new JScrollPane(this.a_table);
        pane.add(scrollPane, BorderLayout.CENTER);

        this.a_table.getInputMap().put((KeyStroke)this.a_actionCopy.getValue(Action.ACCELERATOR_KEY),this.a_actionCopy.getValue(Action.NAME));
        this.a_table.getActionMap().put(this.a_actionCopy.getValue(Action.NAME),this.a_actionCopy);
        this.a_table.getInputMap().put((KeyStroke)this.a_actionCut.getValue(Action.ACCELERATOR_KEY),this.a_actionCut.getValue(Action.NAME));
        this.a_table.getActionMap().put(this.a_actionCut.getValue(Action.NAME),this.a_actionCut);
        this.a_table.getInputMap().put((KeyStroke)this.a_actionPaste.getValue(Action.ACCELERATOR_KEY),this.a_actionPaste.getValue(Action.NAME));
        this.a_table.getActionMap().put(this.a_actionPaste.getValue(Action.NAME),this.a_actionPaste);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        new WindowClosingListener(this,
                new WindowClosingListener.Callback() { @Override public void windowClosing() { a_actionQuit.controlledExit(); }});

        saveChanged(new SaveEvent(photoList,this.a_list.isSaved()));
        photoList.addSaveListener(this);
    }

    /**
     * @return selection list model
     */
    public ListSelectionModel getLineSelectionListModel() {
        return this.a_table.getSelectionModel();
    }

    /**
     * @return selection list model
     */
    public ListSelectionModel getColumnSelectionListModel() {
        return this.a_table.getColumnModel().getSelectionModel();
    }

    /**
     * @see lmzr.photomngr.data.SaveListener#saveChanged(lmzr.photomngr.data.SaveEvent)
     */
    @Override
    public void saveChanged(final SaveEvent e) {

        final boolean photoListIsSaved = e.isSaved();

        setTitle("photo list - [photo data is " + (e.isSaved() ? "saved]" : "modified]") );

        this.a_actionSave.setEnabled(!photoListIsSaved);
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e) {

        final int select[] = this.a_selection.getSelection();

        this.a_actionRenameFolder.setEnabled( select.length == 1 );

        this.a_actionCopy.setEnabled(this.a_table.getSelectedColumn()!=-1);

        // ne marche pas car il faut aussi �couter la s�lection de la colonne
        boolean editable;

        if ( select.length==0 ||
             this.a_table.getSelectedColumn()==-1 ) {
            editable = false;
        } else {
            final int col = this.a_table.convertColumnIndexToModel(this.a_table.getSelectedColumn());
            editable = true;
            for (int i=0; i<select.length; i++) {
                editable &= this.a_list.isCellEditable(select[i],col);
            }
        }

        this.a_actionPaste.setEnabled(editable);
        this.a_actionCopyFromNext.setEnabled( editable && (select[select.length-1]!=this.a_list.getRowCount()-1));
        this.a_actionCopyFromPrevious.setEnabled( editable && (select[0]!=0));
    }
}
