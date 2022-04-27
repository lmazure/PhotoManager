package lmzr.photomngr.ui;

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
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.imagecomputation.ImageComputationManager;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.photomngr.ui.action.ChangeOriginalityAction;
import lmzr.photomngr.ui.action.ChangePrivacyAction;
import lmzr.photomngr.ui.action.ChangeQualityAction;
import lmzr.photomngr.ui.action.CreateCopiesForPrintingAction;
import lmzr.photomngr.ui.action.DisplayGPSAreasInGoogleMapsAction;
import lmzr.photomngr.ui.action.DisplayGPSDatabaseAction;
import lmzr.photomngr.ui.action.DisplayPhotoEditorAction;
import lmzr.photomngr.ui.action.DisplayPhotoGeometryEditorAction;
import lmzr.photomngr.ui.action.DisplayPhotoNavigatorAction;
import lmzr.photomngr.ui.action.DisplayPhotoParametersAction;
import lmzr.photomngr.ui.action.EditLocationsAction;
import lmzr.photomngr.ui.action.EditSubjectsAction;
import lmzr.photomngr.ui.action.ExportLocationsAction;
import lmzr.photomngr.ui.action.ExportSubjectsAction;
import lmzr.photomngr.ui.action.DisplayFilterAction;
import lmzr.photomngr.ui.action.FullScreenAction;
import lmzr.photomngr.ui.action.NextPhotoAction;
import lmzr.photomngr.ui.action.PreviousPhotoAction;
import lmzr.photomngr.ui.action.QuitAction;
import lmzr.photomngr.ui.action.ResetNumberOfCopiesAction;
import lmzr.photomngr.ui.action.RotateAction;
import lmzr.photomngr.ui.action.SaveAction;

/**
 * JFrame used to display the photo(s)
 *
 * @author Laurent Mazuré
 */
public class PhotoDisplayer extends JFrame
                            implements SaveListener, ListSelectionListener {

    final private FilteredPhotoList a_photoList;
    final private ImageComputationManager a_computationManager;
    final private ListSelectionManager a_selection;
    final private ChangeQualityAction[] a_changeQualityActions;
    final private ChangeOriginalityAction[] a_changeOriginalityActions;
    final private ChangePrivacyAction[] a_changePrivacyActions;
    final private GPSDatabase a_GPSDatabase;
    final private JMenuBar a_menubar;
    private boolean a_isFullScreen;
    private Rectangle a_bounds;
    private final SaveAction a_actionSave;
    private final DisplayGPSAreasInGoogleMapsAction a_actionDisplayGPSAreasInGoogleMaps;

    /**
     * @param scheduler
     * @param photoList
     * @param GPSDatabase
     * @param subsampler
     * @param selection
     * @param cacheDirectory
     * @throws HeadlessException
     */
    public PhotoDisplayer(final Scheduler scheduler,
                          final FilteredPhotoList photoList,
                          final GPSDatabase GPSDatabase,
                          final SubsampledImageCachedManager subsampler,
                          final ListSelectionManager selection,
                          final String cacheDirectory) throws HeadlessException {

        super();

        a_photoList = photoList;
        a_computationManager = new ImageComputationManager(scheduler, subsampler);
        a_photoList.addSaveListener(this);
        a_GPSDatabase = GPSDatabase;
        a_GPSDatabase.addSaveListener(this);
        a_selection = selection;

        // listen to change of the selection column(s)
        a_selection.addListener(this);

        final PhotoDisplayerComponent displayer = new PhotoDisplayerComponent(scheduler, photoList, subsampler, a_selection, a_computationManager);
        getContentPane().add(displayer);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                repaint();
            }
        });

        a_menubar = new JMenuBar();
        setJMenuBar(a_menubar);

        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        a_menubar.add(menuFile);
        a_actionSave = new SaveAction("Save photo data", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",a_photoList);
        final JMenuItem itemSave = new JMenuItem(a_actionSave);
        menuFile.add(itemSave);
        final DisplayPhotoNavigatorAction actionDisplayPhotoNavigator = new DisplayPhotoNavigatorAction("Display photo navigator", KeyEvent.VK_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_F2,0), "Display photo navigator",a_photoList,a_GPSDatabase,a_selection);
        final JMenuItem itemDisplayPhotoNavigator = new JMenuItem(actionDisplayPhotoNavigator);
        menuFile.add(itemDisplayPhotoNavigator);
        final DisplayPhotoGeometryEditorAction actionDisplayPhotoGeometryEditor = new DisplayPhotoGeometryEditorAction("Display photo geometry editor", KeyEvent.VK_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_F3,0),"Display photo geometry editor",a_photoList,a_selection);
        final JMenuItem itemDisplayPhotoGeometryEditor = new JMenuItem(actionDisplayPhotoGeometryEditor);
        menuFile.add(itemDisplayPhotoGeometryEditor);
        final DisplayPhotoEditorAction actionDisplayPhotoEditor = new DisplayPhotoEditorAction("Display photo editor", KeyEvent.VK_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_F4,0),"Display photo editor",a_photoList,a_selection);
        final JMenuItem itemDisplayPhotoEditor = new JMenuItem(actionDisplayPhotoEditor);
        menuFile.add(itemDisplayPhotoEditor);
        final DisplayPhotoParametersAction actionDisplayPhotoParameters = new DisplayPhotoParametersAction("Display photo parameters", KeyEvent.VK_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_F5,0),"Display photo parameters",a_photoList,a_selection);
        final JMenuItem itemDisplayPhotoParameters = new JMenuItem(actionDisplayPhotoParameters);
        menuFile.add(itemDisplayPhotoParameters);
        final DisplayGPSDatabaseAction actionDisplayGPSDatabase = new DisplayGPSDatabaseAction("Display GPS database", KeyEvent.VK_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_F6,0),"Display GPS database",a_GPSDatabase);
        final JMenuItem itemDisplayGPSDatabase = new JMenuItem(actionDisplayGPSDatabase);
        menuFile.add(itemDisplayGPSDatabase);
        final DisplayFilterAction actionFilter = new DisplayFilterAction("Filter list", KeyEvent.VK_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),"Filter the list of photos",this,a_photoList,a_selection);
        final JMenuItem itemFilter = new JMenuItem(actionFilter);
        menuFile.add(itemFilter);
        final ExportSubjectsAction actionExportSubjects = new ExportSubjectsAction("Export subjects", KeyEvent.CHAR_UNDEFINED, null,"Export the list of subjects",this,a_photoList);
        final JMenuItem itemExportSubjects = new JMenuItem(actionExportSubjects);
        menuFile.add(itemExportSubjects);
        final ExportLocationsAction actionExportLocations = new ExportLocationsAction("Export locations", KeyEvent.CHAR_UNDEFINED, null,"Export the list of locations",this,a_photoList);
        final JMenuItem itemExportLocations = new JMenuItem(actionExportLocations);
        menuFile.add(itemExportLocations);
        final CreateCopiesForPrintingAction actionCreateCopiesForPrinting = new CreateCopiesForPrintingAction("Create copies for printing", KeyEvent.CHAR_UNDEFINED, null,"Copies the file for a printing",this,a_photoList);
        final JMenuItem itemCreateCopiesForPrinting = new JMenuItem(actionCreateCopiesForPrinting);
        menuFile.add(itemCreateCopiesForPrinting);
        final QuitAction a_actionQuit = new QuitAction("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",a_photoList,a_GPSDatabase,scheduler);
        final JMenuItem itemQuit = new JMenuItem(a_actionQuit);
        menuFile.add(itemQuit);

        final JMenu menuView = new JMenu("View");
        menuView.setMnemonic(KeyEvent.VK_V);
        a_menubar.add(menuView);
        final NextPhotoAction actionNextPhoto = new NextPhotoAction("Next photo", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),"Display next photo",a_selection);
        final JMenuItem itemNextPhoto = new JMenuItem(actionNextPhoto);
        menuView.add(itemNextPhoto);
        final PreviousPhotoAction actionPreviousPhoto = new PreviousPhotoAction("Previous photo", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK),"Display previous photo",a_selection);
        final JMenuItem itemPreviousPhoto = new JMenuItem(actionPreviousPhoto);
        menuView.add(itemPreviousPhoto);
        final FullScreenAction actionFullScreen = new FullScreenAction("Full screen", KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK),"Full screen mode on/off",this);
        final JMenuItem itemFullScreen = new JMenuItem(actionFullScreen);
        menuView.add(itemFullScreen);

        final JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic(KeyEvent.VK_E);
        a_menubar.add(menuEdit);
        final JMenu submenuEditQuality = new JMenu("Set Quality");
        a_changeQualityActions = new ChangeQualityAction[PhotoQuality.getTraits().length];
        for (int i=0; i<PhotoQuality.getTraits().length; i++) {
            final PhotoQuality v = (PhotoQuality)(PhotoQuality.getTraits()[i]);
            a_changeQualityActions[i] = new ChangeQualityAction(v,a_photoList,a_selection);
            submenuEditQuality.add(new JMenuItem(a_changeQualityActions[i]));
        }
        menuEdit.add(submenuEditQuality);
        final JMenu submenuEditOriginality = new JMenu("Set Originality");
        a_changeOriginalityActions = new ChangeOriginalityAction[PhotoOriginality.getTraits().length];
        for (int i=0; i<PhotoOriginality.getTraits().length; i++) {
            final PhotoOriginality v = (PhotoOriginality)(PhotoOriginality.getTraits()[i]);
            a_changeOriginalityActions[i] = new ChangeOriginalityAction(v,a_photoList,a_selection);
            submenuEditOriginality.add(a_changeOriginalityActions[i]);
        }
        menuEdit.add(submenuEditOriginality);
        final JMenu submenuEditPrivacy = new JMenu("Set Privacy");
        a_changePrivacyActions = new ChangePrivacyAction[PhotoPrivacy.getTraits().length];
        for (int i=0; i<PhotoPrivacy.getTraits().length; i++) {
            final PhotoPrivacy v = (PhotoPrivacy)(PhotoPrivacy.getTraits()[i]);
            a_changePrivacyActions[i] = new ChangePrivacyAction(v,a_photoList,a_selection);
            submenuEditPrivacy.add(a_changePrivacyActions[i]);
        }
        menuEdit.add(submenuEditPrivacy);
        final ResetNumberOfCopiesAction actionResetNumberOfCopies = new ResetNumberOfCopiesAction("Reset numbers of copies", KeyEvent.CHAR_UNDEFINED, null,"Set all numbers of copies to zero",a_photoList);
        final JMenuItem itemResetNumberOfCopies = new JMenuItem(actionResetNumberOfCopies);
        menuEdit.add(itemResetNumberOfCopies);
        final RotateAction actionRotateClockwize = new RotateAction("Rotate clockwise", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK), null, a_photoList,a_selection, 90f);
        final JMenuItem itemRotateClockwize = new JMenuItem(actionRotateClockwize);
        menuEdit.add(itemRotateClockwize);
        final RotateAction actionRotateCounterClockwize = new RotateAction("Rotate anticlockwise", KeyEvent.CHAR_UNDEFINED, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK), null, a_photoList,a_selection, -90f);
        final JMenuItem itemRotateCounterClockwize = new JMenuItem(actionRotateCounterClockwize);
        menuEdit.add(itemRotateCounterClockwize);
        final EditSubjectsAction actionEditSubjects = new EditSubjectsAction("Edit subjects", KeyEvent.CHAR_UNDEFINED, null,"Display the subject editor",this,a_photoList,a_selection);
        final JMenuItem itemEditSubjects = new JMenuItem(actionEditSubjects);
        menuEdit.add(itemEditSubjects);
        final EditLocationsAction actionEditLocations = new EditLocationsAction("Edit locations", KeyEvent.CHAR_UNDEFINED, null,"Display the location editor",this,a_photoList,a_selection,a_GPSDatabase);
        final JMenuItem itemEditLocations = new JMenuItem(actionEditLocations);
        menuEdit.add(itemEditLocations);
        a_actionDisplayGPSAreasInGoogleMaps = new DisplayGPSAreasInGoogleMapsAction("Check GPS Area", KeyEvent.CHAR_UNDEFINED, null,"Display Google Maps with GPS Areas",a_GPSDatabase,a_photoList,a_selection,cacheDirectory);
        final JMenuItem itemDisplayGPSAreasInGoogleMaps = new JMenuItem(a_actionDisplayGPSAreasInGoogleMaps);
        menuEdit.add(itemDisplayGPSAreasInGoogleMaps);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        new WindowClosingListener(this,
                                  new WindowClosingListener.Callback() { @Override public void windowClosing() { a_actionQuit.controlledExit(); }});

        a_isFullScreen = false;

        saveChanged(new SaveEvent(photoList,photoList.isSaved()));
    }

    /**
     * @param isFullScreen
     */
    public void setFullScreen(final boolean isFullScreen) {

        dispose();

        if (isFullScreen) {
            a_bounds = getBounds();
            setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            setUndecorated(true);
        } else {
            setBounds(a_bounds);
            setUndecorated(false);
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
    @Override
    public void saveChanged(final SaveEvent e) {

        final boolean photoListIsSaved = a_photoList.isSaved();
        final boolean GPSDatabaseIsSaved = a_GPSDatabase.isSaved();

        setTitle("photo display - [photo data is " +
                 (photoListIsSaved ? "saved]" : "modified]") +
                 " [GPS data is " +
                 (GPSDatabaseIsSaved ? "saved]" : "modified]") );

        a_actionSave.setEnabled(!photoListIsSaved);
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {

        int[] selection = a_selection.getSelection();

        a_actionDisplayGPSAreasInGoogleMaps.setEnabled(selection.length==1);

        for (int i=0; i<a_changeQualityActions.length; i++) {
            a_changeQualityActions[i].setEnabled(selection.length>0);
        }

        for (int i=0; i<a_changeOriginalityActions.length; i++) {
            a_changeOriginalityActions[i].setEnabled(selection.length>0);
        }

        for (int i=0; i<a_changePrivacyActions.length; i++) {
            a_changePrivacyActions[i].setEnabled(selection.length>0);
        }
    }
}