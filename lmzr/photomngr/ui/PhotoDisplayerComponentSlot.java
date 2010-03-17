package lmzr.photomngr.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Future;

import javax.media.Codec;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Processor;
import javax.media.ResourceUnavailableEvent;
import javax.media.TransitionEvent;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

import lmzr.photomngr.data.DataFormat;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.RotationEffect;
import lmzr.photomngr.imagecomputation.ImageComputationConsumer;
import lmzr.photomngr.imagecomputation.ImageComputationManager;
import lmzr.photomngr.imagecomputation.ImageComputationParameters;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.photomngr.ui.player.Player_myself;

/**
 * @author Laurent Mazuré
 */
public class PhotoDisplayerComponentSlot extends JComponent
                                         implements ImageComputationConsumer, ComponentListener, ControllerListener {

    final static private DateFormat s_dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
    final static private PhotoDisplayerComponentFontManager a_fontManager = new PhotoDisplayerComponentFontManager();
    static private ImageComputationManager a_computationManager;
    
    private Photo a_photo;
    private ImageComputationParameters a_params;
    private BufferedImage a_image;
    private boolean a_imageIsComputed;
    private Future<?> a_computation;
	private Processor a_player;
	private Component a_visual, a_controlPanel;

    /**
     * @param scheduler 
     * @param subsampler
     */
    public PhotoDisplayerComponentSlot(final Scheduler scheduler,
                                       final SubsampledImageCachedManager subsampler) {
        super();
        
        // initialize the ImageComputationManager if this has not already been done
        if ( a_computationManager == null ) {
        	a_computationManager = new ImageComputationManager(scheduler,subsampler);
        }
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        addComponentListener(this);
        a_photo = null;
        a_params = null;
        a_image = null;
        a_imageIsComputed = false;
        a_computation = null;
    }

    /**
     * @param photo data to display (set to null is the slot does not display anything)
     */
    public void setPhoto(final Photo photo) {
    	//System.out.println("setPhoto");
    	
    	// stop the cine if it was running
    	if (a_player != null ) {
    		a_player.stop();
    		a_player.deallocate();
//        	if (!waitForState(Controller.Realized)) {
//        		System.err.println("Failed to realize the processor.");
//        		return ;
//        	}
			if (a_visual != null) {
				remove(a_visual);
			}
			if (a_controlPanel != null) {
				remove(a_controlPanel);
			}
    		a_player.close();
    		a_player = null;
    	}

    	// cancel the current computation if required
    	if ( a_computation != null ) {
			a_computation.cancel(false);
			a_computation = null;
		}

        a_photo = photo;
        a_image = null;
        a_imageIsComputed = false;

        if (a_photo==null ) {
        	// the slot does not display anything
        	// repaint the slot as empty
        	repaint();
        } else if (!(new Player_myself()).isFormatSupported(a_photo.getFormat())) {
        	// unsupported format
        	repaint();
        } else if (a_photo.getFormat()==DataFormat.AVI ) {
        	// display a movie
        	play();
        } else {
        	// display a photo
        	if (getWidth()>0 && getHeight()>0) callImageComputation();
        }
    }
    
    /**
     * 
     */
    public void update() {
    	//System.out.println("update");
    	if ( a_photo!=null && a_photo.getFormat()==DataFormat.JPEG ) callImageComputation();
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
	public void paintComponent(final Graphics g) {
    	//System.out.println("paintComponent");

        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(Color.BLACK);

        if ( a_photo == null ) {
            // no data is attached to this slot
            g2.clearRect(0,0,getWidth(),getHeight());
            return;
        }

        if ( a_imageIsComputed ) {
            repaintImage(g2);
        } else  {
            final Font font = a_fontManager.getMessageFont(getSize());
            if (font ==null) return;
            g2.setFont(font);
            final FontMetrics metrics = g.getFontMetrics();
            g2.setColor(Color.BLACK);
            String str;
            if ((new Player_myself()).isFormatSupported(a_photo.getFormat())) {
            	str = "Loading...";
            } else {
            	str = a_photo.getFormat().toString()+" is not supported";
            }
            final int width = metrics.stringWidth(str);
            g2.drawString(str, getSize().width/2-width/2, getSize().height/2+metrics.getAscent() );        
        }
    }

    /**
     * @see javax.swing.JComponent#paintBorder(java.awt.Graphics)
     */
    @Override
	public void paintBorder(final Graphics g) {
    	//System.out.println("paintBorder");
    	super.paintBorder(g);
    }

    /**
     * @see javax.swing.JComponent#paintChildren(java.awt.Graphics)
     */
    @Override
	public void paintChildren(final Graphics g) {
    	//System.out.println("paintChildren");
    	super.paintChildren(g);
    }

    /**
     * @param g
     */
    private void repaintImage(final Graphics2D g) {
    	//System.out.println("repaintImage");

        g.setBackground(Color.BLACK);
        
        if ( a_photo.getFormat()==DataFormat.JPEG ||
             a_image != null /* this second test for non JPEG files which have nevertheless an image (e.g. an AVI file with a THM thumbnail) */ ) {
        	
	        if ( a_image == null ) {
	            // the photo attached to this slot is not accessible
	            final Font font = a_fontManager.getMessageFont(getSize());
	            if (font ==null) return;
	            g.setFont(font);
	            final FontMetrics metrics = g.getFontMetrics();
	            g.setColor(Color.BLACK);
	            final int index = a_photo.getFilename().lastIndexOf(".");
	            final String str = "Cannot access "+a_photo.getFilename().substring(index+1)+" file";
	            final int width = metrics.stringWidth(str);
	            g.drawString(str, getSize().width/2-width/2, getSize().height/2+metrics.getAscent() );        
	            return;
	        }
	
	        g.drawImage(a_image, 0, 0, this);
	
	        final Font font = a_fontManager.getAnnotationFont(getSize());
	        if ( font == null ) return;
	        
	        g.setFont(font);
	        g.setColor(Color.RED);
	
	        final FontMetrics metrics = g.getFontMetrics();
	        final int height = metrics.getHeight();
	
	        final String location = a_photo.getIndexData().getLocation().toLongString();
	        if (location!=null) {
	            final int widthLocation = metrics.stringWidth(location);
	            g.drawString( a_photo.getIndexData().getLocation().toLongString(), getSize().width/2-widthLocation/2, 1+metrics.getAscent() );        	
	        }
	
	        final Date date = a_photo.getHeaderData().getDate();
	        if ( date != null ) {
	            final String str = s_dateFormat.format(date); 
	            final int widthDate = metrics.stringWidth(str);
	            g.drawString(str, getSize().width/2-widthDate/2, 1+metrics.getAscent()+height );        
	        }
        }
    }
    
    /**
     * @see lmzr.photomngr.imagecomputation.ImageComputationConsumer#consumeImageComputation(lmzr.photomngr.data.Photo, lmzr.photomngr.imagecomputation.ImageComputationParameters, java.awt.image.BufferedImage)
     */
    public void consumeImageComputation(final Photo photo,
                                        final ImageComputationParameters params,
                                        final BufferedImage image) {
    	//System.out.println("consumeImageComputation");
        // check that this is the up-to-date answer
        if ( photo!=a_photo || params!=a_params ) return;
        
        a_image = image;
        a_imageIsComputed = true;
        a_computation = null;
        repaint();
    }

    /**
     * launch the computation of the image
     */
    private void callImageComputation() {
    	//System.out.println("callImageComputation");
    	//if ( a_photo==null && a_computation!=null ) {
		//	a_computation.cancel(false);
    	//	a_computation = null;
    	//}
    	//else {
    		if ( a_photo!=null ) {
    			final ImageComputationParameters params = new ImageComputationParameters(getSize().width,
    				                                                                   	 getSize().height,
    					                                                                 a_photo.getIndexData().getZoom(),
    					                                                                 a_photo.getIndexData().getRotation(),
    					                                                                 a_photo.getIndexData().getFocusX(),
    					                                                                 a_photo.getIndexData().getFocusY());
    			if ( a_params!=params ) {
    				if ( a_computation!=null ) {
    					a_computation.cancel(false);
    				}
    				a_params = params;
    				a_computation = a_computationManager.compute(a_photo,a_params,this);
    			}
    		}
    	//}
        repaint();
    }
    
    /**
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(@SuppressWarnings("unused") final ComponentEvent e) {
    	//System.out.println("componentResized");
    	callImageComputation();
    }

    /**
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(@SuppressWarnings("unused") final ComponentEvent e) {
    	//System.out.println("componentMoved");
        return;
    }

    /**
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(@SuppressWarnings("unused") final ComponentEvent e) {
    	//System.out.println("componentShown");
        return;
    }

    /**
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(@SuppressWarnings("unused") final ComponentEvent e) {
    	//System.out.println("componentHidden");
        return;
    }
    
  
    
    Object waitSync = new Object();
    boolean stateTransitionOK = true;


    /**
     * Given a media locator, create a processor and use that processor
     * as a player to playback the media.
     *
     * During the processor's Configured state, the RotationEffect is
     * inserted into the video track.
     *
     * Much of the code is just standard code to present media in JMF.
     */
    public void play() {

    	try {
    		a_player = Manager.createProcessor(new URL("file:"+a_photo.getFullPath()));
    	} catch (final Exception e) {
    		System.err.println("Failed to create a processor from the given url: " + e);
			e.printStackTrace();
    		return ;
    	}

    	a_player.addControllerListener(this);

    	// Put the Processor into configured state.
    	a_player.configure();
    	if (!waitForState(Processor.Configured)) {
    		System.err.println("Failed to configure the processor.");
    		return ;
    	}

    	// So I can use it as a player.
    	a_player.setContentDescriptor(null);

    	// Search for the track control for the video track.
    	TrackControl videoTrack = null;
    	final TrackControl tc[] = a_player.getTrackControls();
    	for (int i = 0; i < tc.length; i++) {
    		if (tc[i].getFormat() instanceof VideoFormat) {
    			videoTrack = tc[i];
    			break;
    		}
    	}

    	// Instantiate and set the frame access codec to the data flow path.
    	if (videoTrack != null) {
	    	try {
	    		final Codec codec[] = { new RotationEffect(a_photo) };
	    		videoTrack.setCodecChain(codec);
	    	} catch (final UnsupportedPlugInException e) {
	    		System.err.println("The processor does not support effects.");
	    	}
    	}

    	// Realize the processor.
    	a_player.prefetch();
    	if (!waitForState(Controller.Prefetched)) {
    		System.err.println("Failed to realize the processor.");
    		return ;
    	}

    	if((a_visual = a_player.getVisualComponent()) != null) {
    		add(a_visual);
    		a_visual.setMaximumSize(a_visual.getPreferredSize());
    	}
    	if((a_controlPanel = a_player.getControlPanelComponent()) != null) {
    		add(a_controlPanel);
    		if (a_visual!=null)
    			a_controlPanel.setMaximumSize(new Dimension(a_visual.getPreferredSize().width,a_controlPanel.getPreferredSize().height));
    		else
    			a_controlPanel.setMaximumSize(new Dimension(getWidth(),a_controlPanel.getPreferredSize().height));
    	}
    	add(Box.createVerticalGlue());
    	repaint();
    	a_player.start();
    }

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     * @param state
     * @return true is OK, false if failure
     */
    boolean waitForState(final int state) {
    	//System.out.println("Wait for state: " + getStateString(state));
    	synchronized (waitSync) {
    		try {
    	    	//System.out.println("current state: " + getStateString(a_player.getState()));
    			while (a_player.getState() != state && stateTransitionOK) {
        	    	//System.out.println("new current state: " + getStateString(a_player.getState()));
    				waitSync.wait();
    			}
    		} catch (final Exception e) {
        		System.err.println("Failed to wait for state: " + e);
    			e.printStackTrace();    			
    		}
    	}
    	//System.out.println("Wait for state done ------------- ");
    	return stateTransitionOK;
    }


    /**
     * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
     */
    public void controllerUpdate(final ControllerEvent evt) {

    	//System.out.println("update: " + evt);

    	if ( evt instanceof TransitionEvent ) {
       		synchronized (waitSync) {
       			stateTransitionOK = true;
       			waitSync.notifyAll();
       		}
       	} else if ( evt instanceof ResourceUnavailableEvent ) {
       		synchronized (waitSync) {
       			stateTransitionOK = false;
       			waitSync.notifyAll();
       		}
       	}
   }


	/**
	 * @param state
	 * @return string representation of the state
	 */
	@SuppressWarnings("unused")
	static private String getStateString(final int state) {
		if (state==Controller.Unrealized) return "Unrealized";
		if (state==Controller.Realizing) return "Realizing	";
		if (state==Controller.Realized) return "Realized";
		if (state==Controller.Prefetching) return "Prefetching";
		if (state==Controller.Prefetched) return "Prefetched";
		if (state==Controller.Started) return "Started";
		if (state==Processor.Configuring) return "Configuring";
		if (state==Processor.Configured) return "Configured";
		return "?";			
	}
}
