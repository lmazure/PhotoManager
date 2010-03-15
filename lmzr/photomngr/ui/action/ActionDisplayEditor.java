package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import lmzr.photomngr.ui.PhotoEditorComponent;

/**
 * Action to display the display the editor
 */
public class ActionDisplayEditor extends PhotoManagerAction {
	
	final private PhotoEditorComponent a_editor;
	
		/**
		 * @param text
		 * @param mnemonic
		 * @param accelerator
		 * @param tooltipText
		 * @param editor 
		 */
		public ActionDisplayEditor(final String text,
                          final int mnemonic,
  		                  final KeyStroke accelerator,
		                  final String tooltipText,
		                  final PhotoEditorComponent editor) {
	        super(text, mnemonic, accelerator, tooltipText);
	        a_editor = editor;
		}
	
	
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
		    a_editor.setVisible(!a_editor.isVisible());
		}
	}
