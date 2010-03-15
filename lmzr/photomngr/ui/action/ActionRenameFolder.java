package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.PhotoListDisplay;

/**
 * Action to rename a folder
 */
public class ActionRenameFolder extends PhotoManagerAction {
	
	final private PhotoListDisplay a_photoListDisplay;
    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;

		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param photoListDisplay 
		 * @param photoList 
		 * @param selection 
		 */
		public ActionRenameFolder(final String text,
                                  final int mnemonic,
                                  final KeyStroke accelerator,
      		                      final String tooltipText,
      		                      final PhotoListDisplay photoListDisplay,
      		                      final PhotoList photoList,
      		                      final ListSelectionManager selection) {
    	        super(text, mnemonic, accelerator, tooltipText);
    	        a_photoListDisplay = photoListDisplay;
    	        a_photoList = photoList;
    	        a_selection = selection;
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e) {
			
			if (!a_photoList.isSaved()) {
		        JOptionPane.showMessageDialog(a_photoListDisplay,
	                                          "Cannot rename a file if the current data is not saved",
	                                          "Rename error",
	                                          JOptionPane.ERROR_MESSAGE);
				return;
			}
			
		    final Photo photo = a_photoList.getPhoto(a_selection.getSelection()[0]);
			final String oldFolderName = photo.getFolder();
			final String newFolderName = JOptionPane.showInputDialog(a_photoListDisplay,
					                                                 "new folder name?",
					                                                 oldFolderName);
		    if (newFolderName == null) return;  // user clicked cancel

		    // rename the directory
		    final File photoFile = new File(photo.getFullPath());
		    final File oldFolderFile = photoFile.getParentFile();
		    final File newFolderFile = new File(oldFolderFile.getParentFile(),newFolderName);
		    if (!oldFolderFile.renameTo(newFolderFile)) {
		        JOptionPane.showMessageDialog(a_photoListDisplay,
                        "Failed to rename \""+oldFolderFile+"\" into \""+newFolderFile+"\"",
                        "Rename error",
                        JOptionPane.ERROR_MESSAGE);
		        return;
		    }
		    
		    // update the data recorded in the list
		    for (int i=0; i<a_photoList.getRowCount(); i++) {
		    	if ( ((String)a_photoList.getValueAt(i, PhotoList.PARAM_FOLDER)).equals(oldFolderName) ) {
		    		a_photoList.setValueAt(newFolderName, i, PhotoList.PARAM_FOLDER);
		    	}
		    }

		    // save
			a_photoListDisplay.a_actionSave.actionPerformed(e); //TODO ce hack est vraiment très sale!!!
		}
	}
	