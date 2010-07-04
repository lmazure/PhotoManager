package lmzr.photomngr.ui.filter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 */
public class FilterComponent extends JPanel {

	final private JPanel a_pane; 
    final private JCheckBox a_title;
    
    /**
     * @param label
     */
    public FilterComponent(final String label) {
        super();
        
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		a_title = new JCheckBox(label);
        add(a_title);
        
        a_pane = new JPanel();
        a_pane.setLayout(new BoxLayout(a_pane,BoxLayout.Y_AXIS));
        a_pane.setBorder(BorderFactory.createLineBorder(Color.black));
                
        add(a_pane);
        
        a_title.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { a_pane.setVisible(isFilterEnabled());}});
    }

    /**
     * @return the pane
     */
    JPanel getPane() {
    	return a_pane;
    }
    
    /**
     * @return indicates if the filter is enabled
     */
    boolean isFilterEnabled() {
    	return a_title.isSelected();
    }
    
    /**
     * @param isFilterEnabled
     */
    void setFilterEnabled(final boolean isFilterEnabled) {
		a_title.setSelected(isFilterEnabled);
        a_pane.setVisible(isFilterEnabled);    	
    }
}

