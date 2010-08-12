package lmzr.photomngr.ui.filter;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import lmzr.photomngr.data.filter.FilterOnCopies;

/**
 * 
 */
public class CopiesComponentFilterUI extends ComponentFilterUI {

    final private JFormattedTextField a_min;
    final private JFormattedTextField a_max;
    
    /**
     * @param label
     * @param filter
     */
    public CopiesComponentFilterUI(final String label,
                                   final FilterOnCopies filter) {
        super(label);
        
		//setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        //setLayout(new GridLayout(2,2));
        
		setFilterEnabled(filter.isEnabled());
        
		getPane().add(new JLabel("min"));
		a_min = new JFormattedTextField(NumberFormat.getIntegerInstance());
		getPane().add(a_min);
		a_min.setValue(filter.getMin());

		getPane().add(new JLabel("max"));
		a_max = new JFormattedTextField(NumberFormat.getIntegerInstance());
		getPane().add(a_max);
		a_max.setValue(filter.getMax());
    }

    /**
     * @return min value
     */
    public long getMin() {
        if (isFilterEnabled()) {
            return (Long)a_min.getValue();        
        }
        return 0;
    }

    /**
     * @return max value
     */
    public long getMax() {
        if (isFilterEnabled()) {
            return (Long)a_max.getValue();        
        }
        return Long.MAX_VALUE;
    }
}
