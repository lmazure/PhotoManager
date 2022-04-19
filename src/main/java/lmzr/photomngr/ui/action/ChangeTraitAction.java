package lmzr.photomngr.ui.action;

import java.awt.event.ActionEvent;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.phototrait.PhotoTrait;

/**
 * Action to change a trait of the selected photos
 * 
 * @author Laurent Mazuré
 */

public class ChangeTraitAction extends PhotoManagerAction {

	final private PhotoList a_photoList;
	final private PhotoTrait a_value;
	final private int a_parameter;
	final private ListSelectionManager a_selection;

	/**
	 * @param value
	 * @param name
	 * @param parameter
	 * @param photoList 
	 * @param selection 
	 */
	public ChangeTraitAction(final PhotoTrait value,
			                 final String name,
			                 final int parameter,
			                 final PhotoList photoList,
		 	     			 final ListSelectionManager selection) {
		super(value.toString(), 0, null, "set " + name + " to " + value.toString());
		a_value = value;
		a_parameter = parameter;
		a_photoList = photoList;
		a_selection = selection;
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final int select[] = a_selection.getSelection();
		for (int i=0; i<select.length; i++) {
			a_photoList.setValueAt(a_value,select[i],a_parameter);
		}
	}
}