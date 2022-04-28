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
        this.a_value = value;
        this.a_parameter = parameter;
        this.a_photoList = photoList;
        this.a_selection = selection;
    }


    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final int select[] = this.a_selection.getSelection();
        for (int i=0; i<select.length; i++) {
            this.a_photoList.setValueAt(this.a_value,select[i],this.a_parameter);
        }
    }
}