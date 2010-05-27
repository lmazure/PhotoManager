package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.action.ActionClose;

public class PhotoGeometryEditor extends JFrame {

    final private ListSelectionManager a_selection;
    final private PhotoList a_photoList;
    final private JButton a_rotateLeft;
    final private JButton a_rotateRight;

    /**
     * @param photoList
     * @param selection
     */
    public PhotoGeometryEditor(final PhotoList photoList,
    		                   final ListSelectionManager selection) {
        super();
        

        a_photoList = photoList;
        a_selection = selection;

	    final JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		final JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menubar.add(menuFile);
		final ActionClose a_actionClose = new ActionClose("Close", KeyEvent.VK_UNDEFINED, null,"Close the window",this);
		final JMenuItem itemClose = new JMenuItem(a_actionClose);
		menuFile.add(itemClose);

        final JPanel display = new JPanel();
        display.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(display);
        a_rotateLeft = new JButton("rotate left");
        a_rotateRight = new JButton("rotate right");
        display.add(a_rotateLeft);
        a_rotateLeft.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(final ActionEvent e) {
                    	for (int i=0; i<a_selection.getSelection().length; i++) {
                    	    float r = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ROTATION))).floatValue();
                    	    a_photoList.setValueAt(new Float(r-90.),
 			                                       a_selection.getSelection()[i],
			                                       PhotoList.PARAM_ROTATION);};}});
        display.add(a_rotateRight);
        a_rotateRight.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(final ActionEvent e) {
                    	for (int i=0; i<a_selection.getSelection().length; i++) {
                    	    float r = ((Float)(a_photoList.getValueAt(a_selection.getSelection()[i],PhotoList.PARAM_ROTATION))).floatValue();
                    	    a_photoList.setValueAt(new Float(r+90.),
 			                                       a_selection.getSelection()[i],
			                                       PhotoList.PARAM_ROTATION);};}});
        display.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}
