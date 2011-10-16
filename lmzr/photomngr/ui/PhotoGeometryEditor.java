package lmzr.photomngr.ui;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.action.ResetGeometryAction;
import lmzr.photomngr.ui.action.RotateAction;
import lmzr.photomngr.ui.action.ZoomAction;

/**
 * @author Laurent Mazuré
 */
public class PhotoGeometryEditor extends JFrame {

    final private ListSelectionManager a_selection;
    final private PhotoList a_photoList;
    final private JButton a_rotateLeft90;
    final private JButton a_rotateRight90;
    final private JButton a_rotateLeft10;
    final private JButton a_rotateRight10;
    final private JButton a_rotateLeft1;
    final private JButton a_rotateRight1;
    final private JButton a_rotateLeft01;
    final private JButton a_rotateRight01;
    final private JButton a_multiplyZoom15;
    final private JButton a_divideZoom15;
    final private JButton a_multiplyZoom11;
    final private JButton a_divideZoom11;
    final private JButton a_multiplyZoom101;
    final private JButton a_divideZoom101;
    final private JButton a_resetGeometry;

    /**
     * @param photoList
     * @param selection
     */
    public PhotoGeometryEditor(final PhotoList photoList,
    		                   final ListSelectionManager selection) {
        super();
        

        a_photoList = photoList;
        a_selection = selection;

        final JPanel display = new JPanel();
        display.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(display);

        a_rotateLeft90 = new JButton("↺ 90°");
        display.add(a_rotateLeft90);
        a_rotateLeft90.addActionListener(new RotateAction(-90.0f, a_photoList, a_selection));
        a_rotateRight90 = new JButton("↻ 90°");
        display.add(a_rotateRight90);
        a_rotateRight90.addActionListener(new RotateAction(90.0f, a_photoList, a_selection));
        
        a_rotateLeft10 = new JButton("↺ 10°");
        display.add(a_rotateLeft10);
        a_rotateLeft10.addActionListener(new RotateAction(-10.0f, a_photoList, a_selection));
        a_rotateRight10 = new JButton("↻ 10°");
        display.add(a_rotateRight10);
        a_rotateRight10.addActionListener(new RotateAction(10.0f, a_photoList, a_selection));
        
        a_rotateLeft1 = new JButton("↺ 1°");
        display.add(a_rotateLeft1);
        a_rotateLeft1.addActionListener(new RotateAction(-1.0f, a_photoList, a_selection));
        a_rotateRight1 = new JButton("↻ 1°");
        display.add(a_rotateRight1);
        a_rotateRight1.addActionListener(new RotateAction(1.0f, a_photoList, a_selection));

        a_rotateLeft01 = new JButton("↺ 0.1°");
        display.add(a_rotateLeft01);
        a_rotateLeft01.addActionListener(new RotateAction(-0.1f, a_photoList, a_selection));
        a_rotateRight01 = new JButton("↻ 0.1°");
        display.add(a_rotateRight01);
        a_rotateRight01.addActionListener(new RotateAction(0.1f, a_photoList, a_selection));

        a_multiplyZoom15 = new JButton("x 1.5");
        display.add(a_multiplyZoom15);
        a_multiplyZoom15.addActionListener(new ZoomAction(1.5f, a_photoList, a_selection));
        a_divideZoom15 = new JButton("/ 1.5");
        display.add(a_divideZoom15);
        a_divideZoom15.addActionListener(new ZoomAction(1.0f/1.5f, a_photoList, a_selection));

        a_multiplyZoom11 = new JButton("x 1.1");
        display.add(a_multiplyZoom11);
        a_multiplyZoom11.addActionListener(new ZoomAction(1.1f, a_photoList, a_selection));
        a_divideZoom11 = new JButton("/ 1.1");
        display.add(a_divideZoom11);
        a_divideZoom11.addActionListener(new ZoomAction(1.0f/1.1f, a_photoList, a_selection));

        a_multiplyZoom101 = new JButton("x 1.01");
        display.add(a_multiplyZoom101);
        a_multiplyZoom101.addActionListener(new ZoomAction(1.01f, a_photoList, a_selection));
        a_divideZoom101 = new JButton("/ 1.01");
        display.add(a_divideZoom101);
        a_divideZoom101.addActionListener(new ZoomAction(1.0f/1.01f, a_photoList, a_selection));

        a_resetGeometry = new JButton("reset");
        display.add(a_resetGeometry);
        a_resetGeometry.addActionListener(new ResetGeometryAction(a_photoList, a_selection));

        display.setAlignmentX(Component.LEFT_ALIGNMENT);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        pack();
    }
}
