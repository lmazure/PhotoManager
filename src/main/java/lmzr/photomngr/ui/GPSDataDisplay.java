package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.ui.action.CloseAction;
import lmzr.photomngr.ui.action.CopyAction;
import lmzr.photomngr.ui.action.CopyFromNextAction;
import lmzr.photomngr.ui.action.CopyFromPreviousAction;
import lmzr.photomngr.ui.action.CutAction;
import lmzr.photomngr.ui.action.PasteAction;
import lmzr.photomngr.ui.action.SaveAction;

/**
 * @author Laurent Mazuré
 *
 */
public class GPSDataDisplay extends JFrame
                            implements SaveListener {

    final private GPSTreeTable a_treeTable;
    final private JMenuBar a_menubar;
    final private GPSDatabase a_GPSdatabase;

    /**
     * @param GPSDatabase
     */
    public GPSDataDisplay(final GPSDatabase GPSDatabase) {

        super();

        a_GPSdatabase = GPSDatabase;

        a_treeTable = new GPSTreeTable(a_GPSdatabase);
        a_treeTable.setRootVisible(true);

        a_menubar = new JMenuBar();
        setJMenuBar(a_menubar);

        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        a_menubar.add(menuFile);
        final SaveAction a_actionSave = new SaveAction("Save GPS data", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",a_GPSdatabase);
        final JMenuItem itemSave = new JMenuItem(a_actionSave);
        menuFile.add(itemSave);
        final CloseAction a_actionClose = new CloseAction("Close", KeyEvent.VK_UNDEFINED, null,"Close the window",this);
        final JMenuItem itemClose = new JMenuItem(a_actionClose);
        menuFile.add(itemClose);

        final JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic(KeyEvent.VK_E);
        a_menubar.add(menuEdit);
        final CopyAction actionCopy = new CopyAction("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),"Copy",a_treeTable);
        final JMenuItem itemCopy = new JMenuItem(actionCopy);
        menuEdit.add(itemCopy);
        final CutAction actionCut = new CutAction("Cut", KeyEvent.VK_U, KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK),"Cut",a_treeTable);
        final JMenuItem itemCut = new JMenuItem(actionCut);
        menuEdit.add(itemCut);
        final PasteAction actionPaste = new PasteAction("Paste", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),"Paste",a_treeTable);
        final JMenuItem itemPaste = new JMenuItem(actionPaste);
        menuEdit.add(itemPaste);
        final CopyFromPreviousAction actionCopyFromPrevious = new CopyFromPreviousAction("Copy parameter from previous", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",a_treeTable);
        final JMenuItem itemCopyFromPrevious = new JMenuItem(actionCopyFromPrevious);
        menuEdit.add(itemCopyFromPrevious);
        final CopyFromNextAction actionCopyFromNext = new CopyFromNextAction("Copy parameter from next", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK),"Copy the parameter from the previous photo",a_treeTable);
        final JMenuItem itemCopyFromNext = new JMenuItem(actionCopyFromNext);
        menuEdit.add(itemCopyFromNext);

        final Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        final JScrollPane scrollPane = new JScrollPane(a_treeTable);
        pane.add(scrollPane, BorderLayout.CENTER);

        a_treeTable.getInputMap().put((KeyStroke)actionCopy.getValue(Action.ACCELERATOR_KEY),actionCopy.getValue(Action.NAME));
        a_treeTable.getActionMap().put(actionCopy.getValue(Action.NAME),actionCopy);
        a_treeTable.getInputMap().put((KeyStroke)actionCut.getValue(Action.ACCELERATOR_KEY),actionCut.getValue(Action.NAME));
        a_treeTable.getActionMap().put(actionCut.getValue(Action.NAME),actionCut);
        a_treeTable.getInputMap().put((KeyStroke)actionPaste.getValue(Action.ACCELERATOR_KEY),actionPaste.getValue(Action.NAME));
        a_treeTable.getActionMap().put(actionPaste.getValue(Action.NAME),actionPaste);

        a_GPSdatabase.addSaveListener(this);

        saveChanged(new SaveEvent(a_GPSdatabase, a_GPSdatabase.isSaved()));

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        setBounds(new Rectangle(0,0,900,400));
    }

    /**
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {

        super.dispose();

        a_GPSdatabase.removeSaveListener(this);
    }

    /**
     * @see lmzr.photomngr.data.SaveListener#saveChanged(lmzr.photomngr.data.SaveEvent)
     */
    @Override
    public void saveChanged(final SaveEvent e) {
        setTitle("GPS data - [GPS data is " + (e.isSaved() ? "saved]" : "modified]") );
    }
}
