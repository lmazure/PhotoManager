package lmzr.photomngr.ui.filter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import lmzr.photomngr.data.filter.FilterBase;

/**
 *
 */
public class ComponentFilterUI extends JPanel {

	final private JPanel a_pane; 
    final private JCheckBox a_title;
    
    /**
     * @param label
     * @param filter 
     */
    public ComponentFilterUI(final String label,
    		                 final FilterBase filter) {
        super();
        
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		a_title = new JCheckBox(label);
        add(a_title);
        
        a_pane = new JPanel();
        a_pane.setLayout(new BoxLayout(a_pane,BoxLayout.Y_AXIS));
        a_pane.setBorder(BorderFactory.createLineBorder(Color.black));
                
        add(a_pane);
        
        final ActionListener listener = new ActionListener() {
        	@Override
			public void actionPerformed(final ActionEvent e) {
        		final boolean b = a_title.isSelected();
        		a_pane.setVisible(b);
        		filter.setEnabled(b);}};
        a_title.addActionListener(listener);
        
        setFilterEnabled(filter.isEnabled());
    }

    /**
     * @return the pane
     */
    JPanel getPane() {
    	return a_pane;
    }

    /**
     * @param isFilterEnabled
     */
    void setFilterEnabled(final boolean isFilterEnabled) {
		a_title.setSelected(isFilterEnabled);
        a_pane.setVisible(isFilterEnabled);    	
    }
    
    /**
     * @return true if the filer is active, false otherwise
     */
    public boolean isActive() {
    	return a_title.isSelected();
    }
}

