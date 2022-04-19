package lmzr.photomngr.ui.filter;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import lmzr.photomngr.data.filter.FilterOnFormat;

/**
 * 
 */
public class FormatComponentFilterUI extends ComponentFilterUI {

    final private JCheckBox a_check[];
    
    /**
     * @param label
     * @param filter
     */
    public FormatComponentFilterUI(final String label,
                                   final FilterOnFormat filter) {
        super(label, filter);
        
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        a_check = new JCheckBox[filter.getFormats().length];
        for (int i=0; i<filter.getFormats().length; i++) {
            a_check[i] = new JCheckBox(filter.getFormats()[i].toString());
            a_check[i].setSelected((filter==null)?true:filter.getValues()[i]);
            getPane().add(a_check[i]);
        }        
    }

    /**
     * @return null if the field is not enabled, the values of the filter otherwise
     */
    public boolean[] getValues() {
        final boolean[] values = new boolean[a_check.length];
        for (int i=0; i<a_check.length; i++) values[i] = a_check[i].isSelected();        
        return values;
    }
}
