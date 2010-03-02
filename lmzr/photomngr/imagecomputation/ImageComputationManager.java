package lmzr.photomngr.imagecomputation;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import javax.swing.SwingUtilities;

import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoHeaderData;
import lmzr.photomngr.imagecomputation.SubsampledImageCachedManager.SubsampledImage;

/**
 *
 */
public class ImageComputationManager {

    final private ImageComputationCache a_cache;
    final private SubsampledImageCachedManager a_subsampler;
    final private ExecutorService a_executor;

    /**
     * @param photo
     * @param params
     * @param subsampling 
     * @return the affine transform used to display the photo with the display parameters params
     */
    public static AffineTransform getAffineTransform(final Photo photo,
    		                                         final ImageComputationParameters params,
    		                                         final double subsampling) {
        AffineTransform zoom;
        final PhotoHeaderData header = photo.getHeaderData();
        final int orientation = photo.getHeaderData().getOrientation();
        final double z = params.getZoom();
        final double r = params.getRotation()*Math.PI/180;
        final double fx = params.getFocusX(); 
        final double fy = params.getFocusY();
        final double headerHeight = header.getHeight() / subsampling;
		final double headerWidth = header.getWidth() / subsampling;
		final double paramHeight = params.getHeight();
		final double paramWidth = params.getWidth();
		
		switch (orientation) {
	        case 2: {
	            final double f = z * Math.min(paramHeight/headerHeight,paramWidth/headerWidth);
	            zoom = new AffineTransform(-f,0,
	                                       0,f,
	                                       (paramWidth+f*headerWidth)/2,(paramHeight-f*headerHeight)/2);
	            break;        	    
	        }
	        case 3: {
	            final double f = z * Math.min(paramHeight/headerHeight,paramWidth/headerWidth);
	            zoom = new AffineTransform(-f,0,
	                                       0,-f,
	                                       (paramWidth+f*headerWidth)/2,(paramHeight+f*headerHeight)/2);
	            break;        	    
	        }
	        case 4: {
	            final double f = z * Math.min(paramHeight/headerHeight,paramWidth/headerWidth);
	            zoom = new AffineTransform(f,0,
	                                       0,-f,
	                                       (paramWidth-f*headerWidth)/2,(paramHeight+f*headerHeight)/2);
	            break;        	    
	        }
	        case 5: {
	            final double f = z * Math.min(paramHeight/headerWidth,paramWidth/headerHeight);
	            zoom = new AffineTransform(0,f,
	                                       f,0,
	                                       (paramWidth-f*headerHeight)/2,(paramHeight-f*headerWidth)/2);
	            break;
	        }
	        case 6: {
	            final double f = z * Math.min(paramHeight/headerWidth,paramWidth/headerHeight);
	            zoom = new AffineTransform(0,f,
	                                       -f,0,
	                                       (paramWidth+f*headerHeight)/2,(paramHeight-f*headerWidth)/2);
	            break;
	        }
	        case 7: {
	            final double f = z * Math.min(paramHeight/headerWidth,paramWidth/headerHeight);
	            zoom = new AffineTransform(0,f,
	                                       -f,0,
	                                       (paramWidth+f*headerHeight)/2,(paramHeight-f*headerWidth)/2);
	            break;
	        }
	        case 8: {
	            final double f = z * Math.min(paramHeight/headerWidth,paramWidth/headerHeight);
	            zoom = new AffineTransform(0,-f,
	                                       f,0,
	                                       (paramWidth-f*headerHeight)/2,(paramHeight+f*headerWidth)/2);
	            break;
	        }
	        case 1:
	        default: {
	            final double f = z * Math.min(paramHeight/headerHeight,paramWidth/headerWidth);
	            zoom = new AffineTransform(f,0,
	                                       0,f,
	                                       (paramWidth-f*headerWidth*(1+fx))/2,(paramHeight-f*headerHeight*(1+fy))/2);
	            break;        	    
	        }
        }
        zoom.preConcatenate(AffineTransform.getRotateInstance(r,paramWidth/2,paramHeight/2));
        
        return(zoom);
    }
    
    private class Computer implements Runnable {
        
        final private Photo a_photo;
        final private ImageComputationParameters a_params;
        final private ImageComputationConsumer a_consumer;
        
        /**
         * @param photo
         * @param params
         * @param consumer
         */
        Computer(final Photo photo,
                 final ImageComputationParameters params,
                 final ImageComputationConsumer consumer) {
            a_photo = photo;
            a_params = params;
            a_consumer = consumer;            
        }
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
            	System.out.println("run photo="+a_photo.getFullPath());
            	final SubsampledImage image = a_subsampler.getImage(a_photo, a_params, 1.0);
	            if ( image.getImage() == null ) {
	                a_consumer.consumeImageComputation(a_photo,a_params,null);
	                return;
	            }
	            
	            final AffineTransform zoom = getAffineTransform(a_photo, a_params, image.getSubsampling());
	            final AffineTransformOp op = new AffineTransformOp(zoom,AffineTransformOp.TYPE_BICUBIC);
	            final BufferedImage img = new BufferedImage(a_params.getWidth(),
	                                                        a_params.getHeight(),
	                                                        image.getImage().getType());
	            final BufferedImage im = op.filter(image.getImage(),img);
	            
	            a_cache.record(a_photo, a_params, im);
	            
	            a_consumer.consumeImageComputation(a_photo,a_params,im);
            } catch (final Throwable ex) {
            	ex.printStackTrace();
            	//throw(ex);
            }
        }

    }
    
    /**
     * @param subsampler
     */
    public ImageComputationManager(final SubsampledImageCachedManager subsampler) {
        a_cache = new ImageComputationCache();
        a_subsampler = subsampler;
        a_executor = Executors.newFixedThreadPool(8);
    }
    
    /**
     * @param photo photo on which the computation should be performed
     * @param params parameters of the computation
     * @param consumer object using the result of the computation
     * @return the computation task (if the image is already in the cache, the consumer
     *  is called at once and this method returns null)
     */
    public Future<?> compute(final Photo photo,
                             final ImageComputationParameters params,
                             final ImageComputationConsumer consumer) {
        
        final BufferedImage i = a_cache.get(photo, params);
        if (i!=null) {
        	// the image to compute is already in the cache
            consumer.consumeImageComputation(photo,params,i);
            return null;
        }
		// the image to compute is not in the cache
		return a_executor.submit(new Computer(photo,params,consumer));
    }
    
}