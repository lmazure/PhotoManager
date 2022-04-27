package lmzr.photomngr.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
    static private float[] rotateFactors = { 90.0f, 10.0f, 1.0f, 0.1f };
    static private float[] zoomFactors = { 1.3f, 1.1f, 1.01f};

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
        display.setLayout(new BoxLayout(display, BoxLayout.Y_AXIS));
        display.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(display);

        final JPanel rotation = new JPanel();
        rotation.setLayout(new GridLayout(rotateFactors.length,2));
        for (float r: rotateFactors) {
            final JButton rotateLeft = new JButton(new RotateAction("↺ "+r+"°", KeyEvent.CHAR_UNDEFINED, null, null, a_photoList, a_selection, -r));
            rotation.add(rotateLeft);
            final JButton rotateRight = new JButton(new RotateAction("↻ "+r+"°", KeyEvent.CHAR_UNDEFINED, null, null, a_photoList, a_selection, r));
            rotation.add(rotateRight);
        }

        rotation.setBorder(BorderFactory.createTitledBorder("rotate"));
        display.add(rotation);

        final JPanel zoom = new JPanel();
        zoom.setLayout(new GridLayout(zoomFactors.length,2));
        for (float f: zoomFactors) {
            final JButton multiply = new JButton(new ZoomAction("× "+f, KeyEvent.CHAR_UNDEFINED, null, null, a_photoList, a_selection, f));
            zoom.add(multiply);
            final JButton divide = new JButton(new ZoomAction("÷ "+f, KeyEvent.CHAR_UNDEFINED, null, null, a_photoList, a_selection, 1.0f/f));
            zoom.add(divide);
        }

        zoom.setBorder(BorderFactory.createTitledBorder("zoom"));
        display.add(zoom);

        JButton resetGeometry = new JButton("reset");
        display.add(resetGeometry);
        resetGeometry.addActionListener(new ResetGeometryAction(a_photoList, a_selection));
        resetGeometry.setAlignmentX(Component.CENTER_ALIGNMENT);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        pack();
    }
}
