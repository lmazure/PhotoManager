package lmzr.photomngr.ui;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.imagecomputation.ImageComputationManager;
import lmzr.photomngr.imagecomputation.ImageComputationParameters;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;

/**
 * @author Laurent
 *
 */
public class PhotoDisplayerComponent extends JComponent
                                     implements ListSelectionListener, TableModelListener, MouseWheelListener, MouseListener, MouseMotionListener {
    
    final private ListSelectionManager a_selection;
    final private SubsampledImageCachedManager a_subsampler;
    final private PhotoList a_photoList;
    private PhotoDisplayerComponentSlot[] a_slots;
    private int[] a_photoIndex;
    private int a_startX, a_startY;
    final private Scheduler a_scheduler;
    
    /**
     * @param scheduler 
     * @param photoList
     * @param subsampler
     * @param selection
     */
    public PhotoDisplayerComponent(final Scheduler scheduler,
                                   final PhotoList photoList,
    							   final SubsampledImageCachedManager subsampler,
                                   final ListSelectionManager selection) {
        super();
        addMouseListener(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        a_selection = selection;
        a_selection.addListener(this);
        a_subsampler = subsampler;
        a_photoList = photoList;
        a_photoList.addTableModelListener(this);
        setLayout(new GridLayout(1,1));
        a_slots = new PhotoDisplayerComponentSlot[1];
        a_slots[0] = new PhotoDisplayerComponentSlot(scheduler,a_subsampler);
        a_slots[0].setName("initial slot");
        add(a_slots[0]);
        a_photoIndex = new int[1];
        a_photoIndex[0] = -1;
        a_scheduler = scheduler;
    }
    
    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(final ListSelectionEvent e) {
        
        if (e.getValueIsAdjusting()) {
            // event compression -> only the last one is taken into account
            return;
        }
        
        final int selection[] = a_selection.getSelection();
        
        final Dimension dim = computeFormat(selection.length);
        final GridLayout layout = (GridLayout)getLayout();
        if ( ( layout.getRows() != dim.height ) || ( layout.getColumns() != dim.width ) ) {
            buildSlots(dim);
        }

        for (int i=0; i<selection.length; i++) {
            a_slots[i].setPhoto(a_photoList.getPhoto(selection[i]));
            a_photoIndex[i] = selection[i];
        }
        for (int i=selection.length; i<a_slots.length; i++) {
            a_slots[i].setPhoto(null);
            a_photoIndex[i] = -1;
        }
    }
    
    /**
     * @param numberOfImages
     * @return format rows x columns for the image slots
     */
    private Dimension computeFormat(final int numberOfImages) {
        
        if ( numberOfImages == 0 ) {
            return new Dimension(1,1);
        }
        
        final double n = numberOfImages;
        final double w = getWidth();
        final double h = getHeight();
        final int xx = (int)Math.floor(Math.sqrt(n*w/h));
        final int yy = (int)Math.floor(Math.sqrt(n*h/w));
        
        int x,y;
        if ( xx*yy >= numberOfImages ) {
            x = xx;
            y = yy;
        } else if ( xx>yy ) {
            if ( xx*(yy+1) >= numberOfImages ) {
                x = xx;
                y = yy+1;
            } else if ( (xx+1)*yy >= numberOfImages ) {
                x = xx+1;
                y = yy;
            } else {
                x = xx+1;
                y = yy+1;
            }                
        } else {
            if ( (xx+1)*yy >= numberOfImages ) {
                x = xx+1;
                y = yy;
            } else if ( xx*(yy+1) >= numberOfImages ) {
                    x = xx;
                    y = yy+1;
            } else {
                x = xx+1;
                y = yy+1;
            }                
            
        }
        return new Dimension(x,y);
    }
    
    /**
     * @param dim
     */
    private void buildSlots(final Dimension dim) {
        
        final GridLayout layout = (GridLayout)getLayout();
        layout.setRows(dim.height);
        layout.setColumns(dim.width);
        
        for (int i =0; i<a_slots.length; i++) {
            remove(a_slots[i]);
        }
        
        final int n = dim.height * dim.width;
        
        if ( n > a_slots.length ) {
            final PhotoDisplayerComponentSlot slots[] = new PhotoDisplayerComponentSlot[n];
            for (int i =0; i<a_slots.length; i++) slots[i]=a_slots[i];
            for (int i=a_slots.length; i<n; i++) slots[i]=new PhotoDisplayerComponentSlot(a_scheduler,a_subsampler);
            a_slots = slots;
            a_photoIndex = new int[n];
        } else if ( n < a_slots.length ) {
            final PhotoDisplayerComponentSlot slots[] = new PhotoDisplayerComponentSlot[n];
            for (int i=0; i<n; i++) slots[i]=a_slots[i];
            for (int i=n; i<a_slots.length; i++) 
                a_slots[i].setPhoto(null);
            a_slots = slots;
            a_photoIndex = new int[n];
        }

        for (int i =0; i<a_slots.length; i++) {
            a_slots[i].setName("slot_"+Integer.toString(i%dim.width)+"_"+Integer.toString(i/dim.width));
            add(a_slots[i]);
        }
        
        validate();
    }

	/**
	 * @param e
	 */
	public void mouseDragged(final MouseEvent e) {
		
		if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK) return;
		
		final int transX = e.getX() - a_startX;
		final int transY = e.getY() - a_startY;
		a_startX = e.getX();
		a_startY = e.getY();
        final int selection[] = a_selection.getSelection();
        for (int i=0; i<selection.length; i++) {
        	final Photo photo = a_photoList.getPhoto(selection[i]);
			final ImageComputationParameters params = new ImageComputationParameters(a_slots[i].getSize().width,
					                                                                 a_slots[i].getSize().height,
                                                                                     photo.getIndexData().getZoom(),
                                                                                     photo.getIndexData().getRotation(),
                                                                                     photo.getIndexData().getFocusX(),
                                                                                     photo.getIndexData().getFocusY());
			try {
				final AffineTransform transform = ImageComputationManager.getAffineTransform(photo, params, 1.0).createInverse();
				final Point2D orig = new Point2D.Double(transX, transY);
				final Point2D res = transform.deltaTransform(orig, null);
				final double newFocusX = photo.getIndexData().getFocusX() - 2 * res.getX()/photo.getHeaderData().getWidth();
				final double newFocusY = photo.getIndexData().getFocusY() - 2 * res.getY()/photo.getHeaderData().getHeight();
	            a_photoList.setValueAt(new Float(newFocusX),selection[i],PhotoList.PARAM_FOCUS_X);
	            a_photoList.setValueAt(new Float(newFocusY),selection[i],PhotoList.PARAM_FOCUS_Y);
			} catch (final NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}
        }
    }

	/**
	 * @param e
	 */
	public void mouseMoved(final MouseEvent e) {
		// do nothing
	}
	
   	/**
	 * @param e
	 */
	public void mouseClicked(final MouseEvent e) {
		// do nothing
	}

	/**
	 * @param e
	 */
	public void mousePressed(final MouseEvent e) {
		a_startX = e.getX();
		a_startY = e.getY();
	}

	/**
	 * @param e
	 */
	public void mouseReleased(final MouseEvent e) {
		mouseDragged(e);
	}

	/**
	 * @param e
	 */
	public void mouseEntered(final MouseEvent e) {
		// do nothing
	}

	/**
	 * @param e
	 */
	public void mouseExited(final MouseEvent e) {
		// do nothing
	}

	/**
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(final MouseWheelEvent e) {
		
		if ( a_photoList.getRowCount()==0 ) {
			// to avoid a crash with e.getWheelRotation() % a_photoList.getRowCount()
			return;
		}

	    if (e.getModifiersEx()==0) {
	    	final int incr = e.getWheelRotation() % a_photoList.getRowCount();
	    	if (incr > 0) {
	    		a_selection.next(incr);
	    	} else { 
	    		a_selection.previous(-incr);
	    	}
	    } else if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) {
	    	final float incr = (float)Math.pow(1.1,e.getWheelRotation());
	        final int selection[] = a_selection.getSelection();
	        for (int i=0; i<selection.length; i++) {
	            final float oldZoom = a_photoList.getPhoto(selection[i]).getIndexData().getZoom();
	            final float newZoom = oldZoom * incr;
	            a_photoList.setValueAt(new Float(newZoom),selection[i],PhotoList.PARAM_ZOOM);
	        }
	    } else if (e.getModifiersEx()==InputEvent.ALT_DOWN_MASK) {
	    	final float incr = e.getWheelRotation();
	        final int selection[] = a_selection.getSelection();
	        for (int i=0; i<selection.length; i++) {
	    		final float oldRot = a_photoList.getPhoto(selection[i]).getIndexData().getRotation();
	    		float newRot = oldRot + incr;
	    		if ( newRot>180 ) newRot -= 360;
	    		if ( newRot<=180 ) newRot += 360;
	    		a_photoList.setValueAt(new Float(newRot),selection[i],PhotoList.PARAM_ROTATION);
	    	}
	    }
	}

	/**
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	public void tableChanged(final TableModelEvent e) {
        final int firstRow = e.getFirstRow();
        final int lastRow = (e.getLastRow()==Integer.MAX_VALUE) ? (a_photoList.getRowCount()-1)
                                                                : e.getLastRow();
        for (int i =0; i<a_slots.length; i++) {
        	final int index = a_photoIndex[i];
            if (  index>=firstRow && index<=lastRow) a_slots[i].update();
        }
	}

}