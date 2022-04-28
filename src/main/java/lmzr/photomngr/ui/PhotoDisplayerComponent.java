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
import java.awt.image.BufferedImage;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.imagecomputation.ImageComputationConsumer;
import lmzr.photomngr.imagecomputation.ImageComputationManager;
import lmzr.photomngr.imagecomputation.ImageComputationParameters;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;

/**
 * @author Laurent Mazuré
 */
public class PhotoDisplayerComponent extends JComponent
                                     implements ImageComputationConsumer, ListSelectionListener, TableModelListener, MouseWheelListener, MouseListener, MouseMotionListener {

    final private ListSelectionManager a_selection;
    final private SubsampledImageCachedManager a_subsampler;
    final private PhotoList a_photoList;
    final private ImageComputationManager a_computationManager;
    private PhotoDisplayerComponentSlot[] a_slots;
    private int[] a_photoIndex;
    private int a_startX, a_startY;
    final private Scheduler a_scheduler;
    private Future<?> a_nextPrefetchFuture;
    private Photo a_nextPrefetchPhoto;
    private Future<?> a_previousPrefetchFuture;
    private Photo a_previousPrefetchPhoto;

    /**
     * @param scheduler
     * @param photoList
     * @param subsampler
     * @param selection
     * @param computationManager
     */
    public PhotoDisplayerComponent(final Scheduler scheduler,
                                   final PhotoList photoList,
                                   final SubsampledImageCachedManager subsampler,
                                   final ListSelectionManager selection,
                                   final ImageComputationManager computationManager) {
        super();
        addMouseListener(this);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        this.a_selection = selection;
        this.a_selection.addListener(this);
        this.a_subsampler = subsampler;
        this.a_photoList = photoList;
        this.a_photoList.addTableModelListener(this);
        this.a_computationManager = computationManager;

        setLayout(new GridLayout(1,1));
        this.a_slots = new PhotoDisplayerComponentSlot[1];
        this.a_slots[0] = new PhotoDisplayerComponentSlot(scheduler,this.a_subsampler,this.a_computationManager);
        this.a_slots[0].setName("initial slot");
        add(this.a_slots[0]);
        this.a_photoIndex = new int[1];
        this.a_photoIndex[0] = -1;
        this.a_scheduler = scheduler;

        this.a_nextPrefetchFuture = null;
        this.a_nextPrefetchPhoto = null;
        this.a_previousPrefetchFuture = null;
        this.a_previousPrefetchPhoto = null;
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(final ListSelectionEvent e) {

        if (e.getValueIsAdjusting()) {
            // event compression -> only the last one is taken into account
            return;
        }

        final int selection[] = this.a_selection.getSelection();

        final Dimension dim = computeFormat(selection.length);
        final GridLayout layout = (GridLayout)getLayout();
        if ( ( layout.getRows() != dim.height ) || ( layout.getColumns() != dim.width ) ) {
            buildSlots(dim);
        }

        for (int i=0; i<selection.length; i++) {
            this.a_slots[i].setPhoto(this.a_photoList.getPhoto(selection[i]));
            this.a_photoIndex[i] = selection[i];
        }
        for (int i=selection.length; i<this.a_slots.length; i++) {
            this.a_slots[i].setPhoto(null);
            this.a_photoIndex[i] = -1;
        }

        if ( this.a_previousPrefetchFuture != null ) {
            this.a_previousPrefetchFuture.cancel(false);
            this.a_previousPrefetchFuture = null;
            this.a_previousPrefetchPhoto = null;
        }

        if ( this.a_nextPrefetchFuture != null ) {
            this.a_nextPrefetchFuture.cancel(false);
            this.a_nextPrefetchFuture = null;
            this.a_nextPrefetchPhoto = null;
        }

        if ( selection.length==1 ) {
            int previous = (selection[0]>0) ? (selection[0]-1) : (this.a_photoList.getRowCount()-1);
            this.a_previousPrefetchPhoto = this.a_photoList.getPhoto(previous);
            this.a_previousPrefetchFuture = prefetch(this.a_previousPrefetchPhoto);

            int next = (selection[selection.length-1]<(this.a_photoList.getRowCount()-1)) ? (selection[selection.length-1]+1) : 0;
            this.a_nextPrefetchPhoto = this.a_photoList.getPhoto(next);
            this.a_nextPrefetchFuture = prefetch(this.a_nextPrefetchPhoto);
        }
    }

    private Future<?> prefetch(final Photo photo)
    {
        if ( (getSize().width==0) || (getSize().height==0) ) return null;

        final ImageComputationParameters params = new ImageComputationParameters(getSize().width,
                                                                                   getSize().height,
                                                                                   photo.getIndexData().getZoom(),
                                                                                 photo.getIndexData().getRotation(),
                                                                                 photo.getIndexData().getFocusX(),
                                                                                 photo.getIndexData().getFocusY());
        return this.a_computationManager.compute(photo,params,this,false);
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

        for (int i =0; i<this.a_slots.length; i++) {
            remove(this.a_slots[i]);
        }

        final int n = dim.height * dim.width;

        if ( n > this.a_slots.length ) {
            final PhotoDisplayerComponentSlot slots[] = new PhotoDisplayerComponentSlot[n];
            for (int i =0; i<this.a_slots.length; i++) slots[i] = this.a_slots[i];
            for (int i=this.a_slots.length; i<n; i++) slots[i] = new PhotoDisplayerComponentSlot(this.a_scheduler,this.a_subsampler,this.a_computationManager);
            this.a_slots = slots;
            this.a_photoIndex = new int[n];
        } else if ( n < this.a_slots.length ) {
            final PhotoDisplayerComponentSlot slots[] = new PhotoDisplayerComponentSlot[n];
            for (int i=0; i<n; i++) slots[i]=this.a_slots[i];
            for (int i=n; i<this.a_slots.length; i++)
                this.a_slots[i].setPhoto(null);
            this.a_slots = slots;
            this.a_photoIndex = new int[n];
        }

        for (int i =0; i<this.a_slots.length; i++) {
            this.a_slots[i].setName("slot_"+Integer.toString(i%dim.width)+"_"+Integer.toString(i/dim.width));
            add(this.a_slots[i]);
        }

        validate();
    }

    /**
     * @param e
     */
    @Override
    public void mouseDragged(final MouseEvent e) {

        if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK) return;

        final int transX = e.getX() - this.a_startX;
        final int transY = e.getY() - this.a_startY;
        this.a_startX = e.getX();
        this.a_startY = e.getY();
        final int selection[] = this.a_selection.getSelection();
        for (int i=0; i<selection.length; i++) {
            final Photo photo = this.a_photoList.getPhoto(selection[i]);
            final ImageComputationParameters params = new ImageComputationParameters(this.a_slots[i].getSize().width,
                                                                                     this.a_slots[i].getSize().height,
                                                                                     photo.getIndexData().getZoom(),
                                                                                     photo.getIndexData().getRotation(),
                                                                                     photo.getIndexData().getFocusX(),
                                                                                     photo.getIndexData().getFocusY());
            try {
                final AffineTransform transform = ImageComputationManager.getAffineTransform(photo, params, 1.0).createInverse();
                final Point2D orig = new Point2D.Double(transX, transY);
                final Point2D res = transform.deltaTransform(orig, null);
                final float newFocusX = (float) (photo.getIndexData().getFocusX() - 2 * res.getX()/photo.getHeaderData().getWidth());
                final float newFocusY = (float) (photo.getIndexData().getFocusY() - 2 * res.getY()/photo.getHeaderData().getHeight());
                this.a_photoList.setValueAt(Float.valueOf(newFocusX), selection[i],PhotoList.PARAM_FOCUS_X);
                this.a_photoList.setValueAt(Float.valueOf(newFocusY), selection[i],PhotoList.PARAM_FOCUS_Y);
            } catch (final NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * @param e
     */
    @Override
    public void mouseMoved(final MouseEvent e) {
        // do nothing
    }

       /**
     * @param e
     */
    @Override
    public void mouseClicked(final MouseEvent e) {
        // do nothing
    }

    /**
     * @param e
     */
    @Override
    public void mousePressed(final MouseEvent e) {
        this.a_startX = e.getX();
        this.a_startY = e.getY();
    }

    /**
     * @param e
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
        mouseDragged(e);
    }

    /**
     * @param e
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
        // do nothing
    }

    /**
     * @param e
     */
    @Override
    public void mouseExited(final MouseEvent e) {
        // do nothing
    }

    /**
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {

        if ( this.a_photoList.getRowCount()==0 ) {
            // to avoid a crash with e.getWheelRotation() % a_photoList.getRowCount()
            return;
        }

        if (e.getModifiersEx()==0) {
            final int incr = e.getWheelRotation() % this.a_photoList.getRowCount();
            if (incr > 0) {
                this.a_selection.next(incr);
            } else {
                this.a_selection.previous(-incr);
            }
        } else if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) {
            final float incr = (float)Math.pow(1.1,e.getWheelRotation());
            final int selection[] = this.a_selection.getSelection();
            for (int i=0; i<selection.length; i++) {
                final float oldZoom = this.a_photoList.getPhoto(selection[i]).getIndexData().getZoom();
                final float newZoom = oldZoom * incr;
                this.a_photoList.setValueAt(Float.valueOf(newZoom),selection[i],PhotoList.PARAM_ZOOM);
            }
        } else if (e.getModifiersEx()==InputEvent.ALT_DOWN_MASK) {
            final float incr = e.getWheelRotation();
            final int selection[] = this.a_selection.getSelection();
            for (int i=0; i<selection.length; i++) {
                final float oldRot = this.a_photoList.getPhoto(selection[i]).getIndexData().getRotation();
                float newRot = oldRot + incr;
                if ( newRot>180 ) newRot -= 360;
                if ( newRot<=180 ) newRot += 360;
                this.a_photoList.setValueAt(Float.valueOf(newRot),selection[i],PhotoList.PARAM_ROTATION);
            }
        }
    }

    /**
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    @Override
    public void tableChanged(final TableModelEvent e) {

        final int firstRow = e.getFirstRow();
        final int lastRow = (e.getLastRow()==Integer.MAX_VALUE) ? (this.a_photoList.getRowCount()-1)
                                                                : e.getLastRow();
        for (int i =0; i<this.a_slots.length; i++) {
            final int index = this.a_photoIndex[i];
            if (  index>=firstRow && index<=lastRow) this.a_slots[i].update();
        }
    }

    /**
     * @param photo
     * @param params
     * @param image
     */
    @Override
    public void consumeImageComputation(final Photo photo,
                                        final ImageComputationParameters params,
                                        final BufferedImage image) {
        if (photo==this.a_nextPrefetchPhoto) {
            this.a_nextPrefetchFuture = null;
            this.a_nextPrefetchPhoto = null;
        }

        if (photo==this.a_previousPrefetchPhoto) {
            this.a_previousPrefetchFuture = null;
            this.a_previousPrefetchPhoto = null;
        }
    }

}