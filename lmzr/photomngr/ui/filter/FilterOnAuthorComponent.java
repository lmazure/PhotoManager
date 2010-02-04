package lmzr.photomngr.ui.filter;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import lmzr.photomngr.data.filter.FilterOnAuthor;

/**
 * 
 */
public class FilterOnAuthorComponent extends FilterComponent {

    final private JCheckBox a_check[];
    
    /**
     * @param label
     * @param filter
     */
    public FilterOnAuthorComponent(final String label,
                                   final FilterOnAuthor filter) {
        super(label);
        
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		setFilterEnabled(filter.isEnabled());
        
        a_check = new JCheckBox[filter.getAuthors().length];
        for (int i=0; i<filter.getAuthors().length; i++) {
            a_check[i] = new JCheckBox(filter.getAuthors()[i]);
            a_check[i].setSelected((filter==null)?true:filter.getValues()[i]);
            getPane().add(a_check[i]);
        }        
    }

    /**
     * @return null if the field is not enabled, the values of the filter otherwise
     */
    public boolean[] getValues() {
        final boolean[] values = new boolean[a_check.length];
        if (isFilterEnabled()) {
            for (int i=0; i<a_check.length; i++) values[i] = a_check[i].isSelected();        
        } else {
            for (int i=0; i<a_check.length; i++) values[i] = true;        
        }
        return values;
    }
}
