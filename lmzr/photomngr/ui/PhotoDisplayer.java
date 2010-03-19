package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.photomngr.ui.action.ActionChangeOriginality;
import lmzr.photomngr.ui.action.ActionChangePrivacy;
import lmzr.photomngr.ui.action.ActionChangeQuality;
import lmzr.photomngr.ui.action.ActionCreateCopiesForPrinting;
import lmzr.photomngr.ui.action.ActionDisplayEditor;
import lmzr.photomngr.ui.action.ActionDisplayGPSDatabase;
import lmzr.photomngr.ui.action.ActionEditLocations;
import lmzr.photomngr.ui.action.ActionEditSubjects;
import lmzr.photomngr.ui.action.ActionExportLocations;
import lmzr.photomngr.ui.action.ActionExportSubjects;
import lmzr.photomngr.ui.action.ActionDisplayFilter;
import lmzr.photomngr.ui.action.ActionFullScreen;
import lmzr.photomngr.ui.action.ActionNextPhoto;
import lmzr.photomngr.ui.action.ActionPreviousPhoto;
import lmzr.photomngr.ui.action.ActionQuit;
import lmzr.photomngr.ui.action.ActionResetNumberOfCopies;
import lmzr.photomngr.ui.action.ActionSave;

/**
 * display a photo is an independent window
 */
public class PhotoDisplayer extends JFrame
                            implements SaveListener {

    final private FilteredPhotoList a_photoList;
    final private GPSDatabase a_GPSDatabase;
	final private JMenuBar a_menubar;
	private boolean a_isFullScreen;
	private Rectangle a_bounds;
	private final ActionQuit a_actionQuit;

    /**
     * @param scheduler 
     * @param photoList
     * @param GPSDatabase
     * @param subsampler
     * @param selection
     * @throws HeadlessException
     */
    public PhotoDisplayer(final Scheduler scheduler,
    		              final FilteredPhotoList photoList,
    		              final GPSDatabase GPSDatabase,
    		              final SubsampledImageCachedManager subsampler,
                          final ListSelectionManager selection) throws HeadlessException {
        
        super();
        
        a_photoList = photoList;
        a_photoList.addSaveListener(this);
        a_GPSDatabase = GPSDatabase;
        a_GPSDatabase.addSaveListener(this);
        
        saveChanged(new SaveEvent(photoList,photoList.isSaved()));
        
    	final PhotoDisplayerComponent displayer = new PhotoDisplayerComponent(scheduler, photoList, subsampler, selection);
        getContentPane().add(displayer,BorderLayout.CENTER);
    	final PhotoEditorComponent editor = new PhotoEditorComponent(photoList, a_GPSDatabase, selection);
        getContentPane().add(editor,BorderLayout.EAST);
        
        addComponentListener(new ComponentAdapter() {
            @Override
			public void componentResized(@SuppressWarnings("unused") final ComponentEvent e) {
                repaint();
            }
        });

        a_menubar = new JMenuBar();
		setJMenuBar(a_menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		a_menubar.add(menuFile);
		final ActionSave actionSave = new ActionSave("Save photo data", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",a_photoList);
		final JMenuItem itemSave = new JMenuItem(actionSave);
		menuFile.add(itemSave);
		final ActionDisplayGPSDatabase actionDisplayGPSDatabase = new ActionDisplayGPSDatabase("Display GPS database", KeyEvent.VK_UNDEFINED, null,"Display GPS database",a_GPSDatabase);
		final JMenuItem itemDisplayGPSDatabase = new JMenuItem(actionDisplayGPSDatabase);
		menuFile.add(itemDisplayGPSDatabase);
		final ActionExportSubjects actionExportSubjects = new ActionExportSubjects("Export subjects", KeyEvent.CHAR_UNDEFINED, null,"Export the list of subjects",this,a_photoList);
		final JMenuItem itemExportSubjects = new JMenuItem(actionExportSubjects);
		menuFile.add(itemExportSubjects);
		final ActionExportLocations actionExportLocations = new ActionExportLocations("Export locations", KeyEvent.CHAR_UNDEFINED, null,"Export the list of locations",this,a_photoList);
		final JMenuItem itemExportLocations = new JMenuItem(actionExportLocations);
		menuFile.add(itemExportLocations);
		final ActionCreateCopiesForPrinting actionCreateCopiesForPrinting = new ActionCreateCopiesForPrinting("Create copies for printing", KeyEvent.CHAR_UNDEFINED, null,"Copies the file for a printing",this,a_photoList);
		final JMenuItem itemCreateCopiesForPrinting = new JMenuItem(actionCreateCopiesForPrinting);
		menuFile.add(itemCreateCopiesForPrinting);
		a_actionQuit = new ActionQuit("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",a_photoList,a_GPSDatabase);
		final JMenuItem itemQuit = new JMenuItem(a_actionQuit);
		menuFile.add(itemQuit);

		final JMenu menuView = new JMenu("View");
		menuView.setMnemonic(KeyEvent.VK_V);
		a_menubar.add(menuView);
		final ActionNextPhoto actionNextPhoto = new ActionNextPhoto("Next photo", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),"Display next photo",selection);
		final JMenuItem itemNextPhoto = new JMenuItem(actionNextPhoto);
		menuView.add(itemNextPhoto);
		final ActionPreviousPhoto actionPreviousPhoto = new ActionPreviousPhoto("Previous photo", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK),"Display previous photo",selection);
		final JMenuItem itemPreviousPhoto = new JMenuItem(actionPreviousPhoto);
		menuView.add(itemPreviousPhoto);
		final ActionFullScreen actionFullScreen = new ActionFullScreen("Full screen", KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK),"Full screen mode on/off",this);
		final JMenuItem itemFullScreen = new JMenuItem(actionFullScreen);
		menuView.add(itemFullScreen);

		final JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		a_menubar.add(menuEdit);
		final ActionDisplayEditor actionEdit = new ActionDisplayEditor("Edit photo parameters", KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK),"Edit the photo parameters",editor);
		final JMenuItem itemEdit = new JMenuItem(actionEdit);
		menuEdit.add(itemEdit);
		final JMenu submenuEditQuality = new JMenu("Set Quality");
		for (int i=0; i<PhotoQuality.getTraits().length; i++) {
		    final PhotoQuality v = (PhotoQuality)(PhotoQuality.getTraits()[i]);
		    submenuEditQuality.add(new JMenuItem(new ActionChangeQuality(v,a_photoList,selection)));
		}
		menuEdit.add(submenuEditQuality);
		final JMenu submenuEditOriginality = new JMenu("Set Originality");
		for (int i=0; i<PhotoOriginality.getTraits().length; i++) {
		    final PhotoOriginality v = (PhotoOriginality)(PhotoOriginality.getTraits()[i]);
		    submenuEditOriginality.add(new JMenuItem(new ActionChangeOriginality(v,a_photoList,selection)));
		}
		menuEdit.add(submenuEditOriginality);
		final JMenu submenuEditPrivacy = new JMenu("Set Privacy");
		for (int i=0; i<PhotoPrivacy.getTraits().length; i++) {
		    final PhotoPrivacy v = (PhotoPrivacy)(PhotoPrivacy.getTraits()[i]);
		    submenuEditPrivacy.add(new JMenuItem(new ActionChangePrivacy(v,a_photoList,selection)));
		}
		menuEdit.add(submenuEditPrivacy);
		final ActionResetNumberOfCopies actionResetNumberOfCopies = new ActionResetNumberOfCopies("Reset numbers of copîes", KeyEvent.CHAR_UNDEFINED, null,"Set all numbers of copies to zero",a_photoList);
		final JMenuItem itemResetNumberOfCopies = new JMenuItem(actionResetNumberOfCopies);
		menuEdit.add(itemResetNumberOfCopies);
		final ActionEditSubjects actionEditSubjects = new ActionEditSubjects("Edit subjects", KeyEvent.CHAR_UNDEFINED, null,"Display the subject editor",this,a_photoList);
		final JMenuItem itemEditSubjects = new JMenuItem(actionEditSubjects);
		menuEdit.add(itemEditSubjects);
		final ActionEditLocations actionEditLocations = new ActionEditLocations("Edit locations", KeyEvent.CHAR_UNDEFINED, null,"Display the location editor",this,a_photoList,a_GPSDatabase);
		final JMenuItem itemEditLocations = new JMenuItem(actionEditLocations);
		menuEdit.add(itemEditLocations);

		final JMenu filterView = new JMenu("Filter");
		a_menubar.add(filterView);
		final ActionDisplayFilter actionFilter = new ActionDisplayFilter("Filter list", 0, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK),"Filter the list of photos",this,a_photoList,selection);
		final JMenuItem itemFilter = new JMenuItem(actionFilter);
		filterView.add(itemFilter);
		
        a_isFullScreen = false;
    }

	/**
	 * @param isFullScreen
	 */
    public void setFullScreen(final boolean isFullScreen) {
	    if ( isFullScreen == a_isFullScreen ) return;
        dispose();
        setUndecorated(isFullScreen);
        a_menubar.setVisible(!isFullScreen);
        if (isFullScreen) {
            a_bounds = getBounds();
            setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } else {
            setBounds(a_bounds);
        }
        setVisible(true);
        a_isFullScreen = isFullScreen;
	}
	
	/**
	 * @return full screen mode
	 */
	public boolean getFullScreen() {
	    return a_isFullScreen;
	}
	
    /**
     * @see lmzr.photomngr.data.SaveListener#saveChanged(lmzr.photomngr.data.SaveEvent)
     */
    public void saveChanged(@SuppressWarnings("unused") final SaveEvent e) {
        setTitle("photo display - [photo data is " +
        		 (a_photoList.isSaved() ? "saved]" : "modified]") +
        		 " [GPS data is " +
        		 (a_GPSDatabase.isSaved() ? "saved]" : "modified]") );

    }

    /**
     * 
     */
    public void controlledExit() {
    	a_actionQuit.controlledExit(); // TODO fix this dirty hack
    }
}