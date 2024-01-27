package lmzr.photomngr.ui.filter;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import lmzr.photomngr.data.filter.FilterOnPhotoTrait;
import lmzr.photomngr.data.phototrait.PhotoTrait;

/**
 *
 */
public class TraitDisplayComponentFilterUI extends ComponentFilterUI {

    final private JCheckBox a_check[];

    /**
     * @param label
     * @param filter
     */
    public TraitDisplayComponentFilterUI(final String label,
                                         final FilterOnPhotoTrait filter) {
        super(label, filter);

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        final PhotoTrait[] traits = filter.getTraits();
        final boolean[] values = filter.getValues();

        a_check = new JCheckBox[filter.getTraits().length];
        for (int i=0; i<traits.length; i++) {
            a_check[i] = new JCheckBox(traits[i].toString());
            a_check[i].setSelected(values[i]);
            getPane().add(a_check[i]);
        }
    }

    /**
     * @return null if the field is not enabled, the values of the filter otherwise
     */
    public boolean[] getValues() {
        final boolean[] values = new boolean[a_check.length];
        for (int i=0; i<a_check.length; i++) {
            values[i] = a_check[i].isSelected();
        }
        return values;
    }
}
