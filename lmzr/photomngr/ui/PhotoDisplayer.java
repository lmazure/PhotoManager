package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.PhotoListMetaDataEvent;
import lmzr.photomngr.data.PhotoListMetaDataListener;
import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.filter.FilteredPhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.data.phototrait.PhotoTrait;
import lmzr.photomngr.exporter.Exporter;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.ui.action.ActionQuit;
import lmzr.photomngr.ui.action.ActionSave;
import lmzr.photomngr.ui.action.PhotoManagerAction;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * display a photo is an independent window
 */
public class PhotoDisplayer extends JFrame
                            implements SaveListener {

    final private FilteredPhotoList a_photoList;
    final private ListSelectionManager a_selection;
	final private JMenuBar a_menubar;
	final private PhotoEditorComponent a_editor;
	final private PhotoDisplayerComponent a_displayer;
	private boolean a_isFullScreen;
	private Rectangle a_bounds;
	final ActionQuit a_actionQuit;
    


	/**
	 * Action to display the next photo
	 */
	private class ActionNextPhoto extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionNextPhoto(final String text,
		                       final int mnemonic,
		                       final KeyStroke accelerator,
		                       final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			a_selection.next(1);
		}
	}

	/**
	 * Action to display the previous photo
	 */
	private class ActionPreviousPhoto extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionPreviousPhoto(final String text,
		                           final int mnemonic,
  		                           final KeyStroke accelerator,
		                           final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			a_selection.previous(1);
		}
	}

	/**
	 * Action to display the display full screen
	 */
	private class ActionFullScreen extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionFullScreen(final String text,
		                        final int mnemonic,
  		                        final KeyStroke accelerator,
		                        final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    setFullScreen(!getFullScreen());
		}
	}

	/**
	 * Action to display the display the editor
	 */
	private class ActionEdit extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionEdit(final String text,
                          final int mnemonic,
  		                  final KeyStroke accelerator,
		                  final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    a_editor.setVisible(!a_editor.isVisible());
		}
	}

	/**
	 * Action to display the filter
	 */
	private class ActionFilter extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionFilter(final String text,
                            final int mnemonic,
  		                    final KeyStroke accelerator,
		                    final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    final FilterDisplay filter = new FilterDisplay(PhotoDisplayer.this,a_photoList,a_selection);
		    filter.setVisible(true);
		}
	}

	/**
	 * Action to export the list of subjects
	 */
	private abstract class ActionExport extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionExport(final String text,
                            final int mnemonic,
  		                    final KeyStroke accelerator,
		                    final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
		private void dump(final BufferedWriter out,
				          final HierarchicalCompoundString string) {
			try {
				out.write(string.toLongString());
				out.write("\n");
			} catch (IOException e1) {
		        System.err.println("failed to export subjects");
		        e1.printStackTrace();
		        JOptionPane.showMessageDialog(PhotoDisplayer.this,
		        		                      "Failed to export subjects\n"+e1.toString(),
		        		                      "Save error",
		        		                      JOptionPane.ERROR_MESSAGE);
			}
			for (HierarchicalCompoundString s: string.getChildren()) dump(out,s);
		}

		/**
		 * @param root
		 */
		protected void dumpRoot(final HierarchicalCompoundString root) {
		    final JFileChooser fc = new JFileChooser((File)null);
			fc.showSaveDialog(PhotoDisplayer.this);
			final File f = fc.getSelectedFile();
			if ( f == null ) return;
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(f));
				for (HierarchicalCompoundString s: root.getChildren()) dump(out,s);
		        out.close();
			} catch (final IOException e1) {
		        System.err.println("failed to export subjects");
		        e1.printStackTrace();
		        JOptionPane.showMessageDialog(PhotoDisplayer.this,
		        		                      "Failed to export subjects\n"+e1.toString(),
		        		                      "Save error",
		        		                      JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Action to export the list of subjects
	 */
	private class ActionExportSubjects extends ActionExport {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionExportSubjects(final String text,
                                    final int mnemonic,
  		                            final KeyStroke accelerator,
		                            final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    dumpRoot(a_photoList.getSubjectFactory().getRootAsHierarchicalCompoundString());
		}
	}

	/**
	 * Action to export the list of locations
	 */
	private class ActionExportLocations extends ActionExport {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionExportLocations(final String text,
				                     final int mnemonic,
  		                             final KeyStroke accelerator,
		                             final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    dumpRoot(a_photoList.getLocationFactory().getRootAsHierarchicalCompoundString());
		}
	}
	
	/**
	 * Action to edit the list of subjects
	 */
	private class ActionEditSubjects extends PhotoManagerAction implements MapTranslationPerformer {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionEditSubjects(final String text,
                                  final int mnemonic,
  		                          final KeyStroke accelerator,
		                          final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    final SubjectBatchEditor editor = new SubjectBatchEditor(PhotoDisplayer.this,a_photoList.getSubjectFactory().getRootAsHierarchicalCompoundString(),this);
		    editor.setVisible(true);
		}
		
		/**
		 * @see lmzr.photomngr.ui.MapTranslationPerformer#performMapTranslation(java.util.Map)
		 */
		public void performMapTranslation(Map<String,String> map) {
			a_photoList.performSubjectMapTranslation(map);
		}
	}


	/**
	 * Action to edit the list of locations
	 */
	private class ActionEditLocations extends PhotoManagerAction implements MapTranslationPerformer {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionEditLocations(final String text,
                                   final int mnemonic,
  		                           final KeyStroke accelerator,
		                           final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    final SubjectBatchEditor editor = new SubjectBatchEditor(PhotoDisplayer.this,a_photoList.getLocationFactory().getRootAsHierarchicalCompoundString(),this);
		    editor.setVisible(true);
		}
		
		/**
		 * @see lmzr.photomngr.ui.MapTranslationPerformer#performMapTranslation(java.util.Map)
		 */
		public void performMapTranslation(Map<String,String> map) {
			a_photoList.performLocationMapTranslation(map);
		}
	}

	
	/**
	 * Action to change a trait of the selected photos
	 */
	private class ActionChangeTrait extends PhotoManagerAction {
	
	    final PhotoTrait a_value;
	    final int a_indexInPhotoList;
	    
		/**
		 * @param value
		 * @param name
		 * @param indexInPhotoList
		 */
		public ActionChangeTrait(final PhotoTrait value,
		                         final String name,
		                         final int indexInPhotoList) {
	        super(value.toString(), 0, null, "set " + name + " to "+value.toString());
	        a_value = value;
	        a_indexInPhotoList = indexInPhotoList;
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			final int select[] = a_selection.getSelection();
			for (int i=0; i<select.length; i++) {
				a_photoList.setValueAt(a_value,select[i],a_indexInPhotoList);
			}
		}
	}
	
	/**
	 * Action to display the quality of the selected photos
	 */
	private class ActionChangeQuality extends ActionChangeTrait {
	
		/**
		 * @param value
		 */
		public ActionChangeQuality(final PhotoQuality value) {
	        super(value,"quality",PhotoList.PARAM_QUALITY);
		}	
	}
	
	/**
	 * Action to display the originality of the selected photos
	 */
	private class ActionChangeOriginality extends ActionChangeTrait {
	
		/**
		 * @param value
		 */
		public ActionChangeOriginality(final PhotoOriginality value) {
	        super(value,"originality",PhotoList.PARAM_ORIGINALITY);
		}	
	}

	/**
	 * Action to display the privacy of the selected photos
	 */
	private class ActionChangePrivacy extends ActionChangeTrait {
	
		/**
		 * @param value
		 */
		public ActionChangePrivacy(final PhotoPrivacy value) {
	        super(value,"privacy",PhotoList.PARAM_PRIVACY);
		}	
	}

	/**
	 * Action to create exportable copies
	 */
	private class ActionCreateCopiesForPrinting extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionCreateCopiesForPrinting(final String text,
		                                     final int mnemonic,
		                                     final KeyStroke accelerator,
		                                     final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {

			final Exporter exporter = new Exporter(PhotoDisplayer.this);
			exporter.export(a_photoList);

		}
	}

	/**
	 * Action to reset the number of copies
	 */
	private class ActionResetNumberOfCopies extends PhotoManagerAction {
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 */
		public ActionResetNumberOfCopies(final String text,
				                         final int mnemonic,
				                         final KeyStroke accelerator,
				                         final String tooltipText) {
	        super(text, mnemonic, accelerator, tooltipText);
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
			for (int i=0; i<a_photoList.getRowCount(); i++) {
				if ( ((Integer)a_photoList.getValueAt(i, PhotoList.PARAM_COPIES)).intValue() > 0 ) {
					a_photoList.setValueAt(new Integer(0),i, PhotoList.PARAM_COPIES);
				}
			}
		}
	}

    /**
     * @param photoList
     * @param GPSDatabase
     * @param subsampler SubsampledImageCachedManager used to manage cached images
     * @param selection
     * @throws HeadlessException
     */
    public PhotoDisplayer(final FilteredPhotoList photoList,
    		              final GPSDatabase GPSDatabase,
    		              final SubsampledImageCachedManager subsampler,
                          final ListSelectionManager selection) throws HeadlessException {
        
        super();
        saveChanged(new SaveEvent(photoList,photoList.isSaved()));
        
        a_photoList = photoList;
        a_photoList.addSaveListener(this);
        a_selection = selection;
        a_displayer = new PhotoDisplayerComponent(photoList, subsampler, a_selection);
        getContentPane().add(a_displayer,BorderLayout.CENTER);
        a_editor = new PhotoEditorComponent(photoList, GPSDatabase, a_selection);
        getContentPane().add(a_editor,BorderLayout.EAST);
        
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
		final ActionSave actionSave = new ActionSave("Save", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),"Save",photoList);
		final JMenuItem itemSave = new JMenuItem(actionSave);
		menuFile.add(itemSave);
		final ActionExportSubjects actionExportSubjects = new ActionExportSubjects("Export subjects", KeyEvent.CHAR_UNDEFINED, null,"Export the list of subjects");
		final JMenuItem itemExportSubjects = new JMenuItem(actionExportSubjects);
		menuFile.add(itemExportSubjects);
		final ActionExportLocations actionExportLocations = new ActionExportLocations("Export locations", KeyEvent.CHAR_UNDEFINED, null,"Export the list of locations");
		final JMenuItem itemExportLocations = new JMenuItem(actionExportLocations);
		menuFile.add(itemExportLocations);
		final ActionCreateCopiesForPrinting actionCreateCopiesForPrinting = new ActionCreateCopiesForPrinting("Create copies for printing", KeyEvent.CHAR_UNDEFINED, null,"Copies the file for a printing");
		final JMenuItem itemCreateCopiesForPrinting = new JMenuItem(actionCreateCopiesForPrinting);
		menuFile.add(itemCreateCopiesForPrinting);
		a_actionQuit = new ActionQuit("Quit", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),"Exit",photoList);
		final JMenuItem itemQuit = new JMenuItem(a_actionQuit);
		menuFile.add(itemQuit);

		final JMenu menuView = new JMenu("View");
		menuView.setMnemonic(KeyEvent.VK_V);
		a_menubar.add(menuView);
		final ActionNextPhoto actionNextPhoto = new ActionNextPhoto("Next photo", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),"Display next photo");
		final JMenuItem itemNextPhoto = new JMenuItem(actionNextPhoto);
		menuView.add(itemNextPhoto);
		final ActionPreviousPhoto actionPreviousPhoto = new ActionPreviousPhoto("Previous photo", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK),"Display previous photo");
		final JMenuItem itemPreviousPhoto = new JMenuItem(actionPreviousPhoto);
		menuView.add(itemPreviousPhoto);
		final ActionFullScreen actionFullScreen = new ActionFullScreen("Full screen", KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK),"Full screen mode on/off");
		final JMenuItem itemFullScreen = new JMenuItem(actionFullScreen);
		menuView.add(itemFullScreen);

		final JMenu menuEdit = new JMenu("Edit");
		menuEdit.setMnemonic(KeyEvent.VK_E);
		a_menubar.add(menuEdit);
		final ActionEdit actionEdit = new ActionEdit("Edit photo parameters", KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK),"Edit the photo parameters");
		final JMenuItem itemEdit = new JMenuItem(actionEdit);
		menuEdit.add(itemEdit);
		final JMenu submenuEditQuality = new JMenu("Set Quality");
		for (int i=0; i<PhotoQuality.getTraits().length; i++) {
		    final PhotoQuality v = (PhotoQuality)(PhotoQuality.getTraits()[i]);
		    submenuEditQuality.add(new JMenuItem(new ActionChangeQuality(v)));
		}
		menuEdit.add(submenuEditQuality);
		final JMenu submenuEditOriginality = new JMenu("Set Originality");
		for (int i=0; i<PhotoOriginality.getTraits().length; i++) {
		    final PhotoOriginality v = (PhotoOriginality)(PhotoOriginality.getTraits()[i]);
		    submenuEditOriginality.add(new JMenuItem(new ActionChangeOriginality(v)));
		}
		menuEdit.add(submenuEditOriginality);
		final JMenu submenuEditPrivacy = new JMenu("Set Privacy");
		for (int i=0; i<PhotoPrivacy.getTraits().length; i++) {
		    final PhotoPrivacy v = (PhotoPrivacy)(PhotoPrivacy.getTraits()[i]);
		    submenuEditPrivacy.add(new JMenuItem(new ActionChangePrivacy(v)));
		}
		menuEdit.add(submenuEditPrivacy);
		final ActionResetNumberOfCopies actionResetNumberOfCopies = new ActionResetNumberOfCopies("Reset numbers of copîes", KeyEvent.CHAR_UNDEFINED, null,"Set all numbers of copies to zero");
		final JMenuItem itemResetNumberOfCopies = new JMenuItem(actionResetNumberOfCopies);
		menuEdit.add(itemResetNumberOfCopies);
		final ActionEditSubjects actionEditSubjects = new ActionEditSubjects("Edit subjects", KeyEvent.CHAR_UNDEFINED, null,"Display the subject editor");
		final JMenuItem itemEditSubjects = new JMenuItem(actionEditSubjects);
		menuEdit.add(itemEditSubjects);
		final ActionEditLocations actionEditLocations = new ActionEditLocations("Edit locations", KeyEvent.CHAR_UNDEFINED, null,"Display the location editor");
		final JMenuItem itemEditLocations = new JMenuItem(actionEditLocations);
		menuEdit.add(itemEditLocations);

		final JMenu filterView = new JMenu("Filter");
		a_menubar.add(filterView);
		final ActionFilter actionFilter = new ActionFilter("Filter list", 0, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK),"Filter the list of photos");
		final JMenuItem itemFilter = new JMenuItem(actionFilter);
		filterView.add(itemFilter);
		
        a_isFullScreen = false;
    }

	/**
	 * @param isFullScreen
	 */
	private void setFullScreen(final boolean isFullScreen) {
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
	private boolean getFullScreen() {
	    return a_isFullScreen;
	}
	
    /**
     * @see lmzr.photomngr.data.SaveListener#saveChanged(lmzr.photomngr.data.SaveEvent)
     */
    public void saveChanged(final SaveEvent e) {
        setTitle("photo display - [photo data is " + (e.isSaved() ? "saved]" : "modified]") );

    }

    /**
     * 
     */
    public void controlledExit() {
    	a_actionQuit.controlledExit();
    }
}