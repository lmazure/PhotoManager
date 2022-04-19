package lmzr.photomngr.imagecomputation;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoHeaderData;

/**
 * @author Laurent Mazuré
 */
public class SubsampledImageCachedManager {

	/**
	 *
	 */
	public class SubsampledImage {
		
		final private BufferedImage a_image;
		final private double a_subsampling;
		
		/**
		 * @param image
		 * @param subsampling
		 */
		private SubsampledImage(final BufferedImage image,
								final double subsampling) {
			a_image = image;
			a_subsampling = subsampling;
		}
		
		/**
		 * @return subsampling
		 */
		public double getSubsampling() {
			return a_subsampling;
		}
		
		/**
		 * @return image
		 */
		public BufferedImage getImage() {
			return a_image;
		}
	}
	
	
	private final String a_cacheDirectory;
	private final double reductionFactor = 2;
	private final int minSize = 32;
	
	/**
	 * @param cacheDirectory directory where will be stored the cached files
	 */
	public SubsampledImageCachedManager(final String cacheDirectory) {
		a_cacheDirectory = cacheDirectory;
	}
	
	/**
	 * @param photo
	 * @param params
	 * @param subsamplingFactor
	 * @return sub-sampled image
	 */
	public SubsampledImage getImage(final Photo photo,
								    final ImageComputationParameters params,
								    final double subsamplingFactor) {
		
		double f;
		final PhotoHeaderData header = photo.getHeaderData();
        final int orientation = header.getOrientation();
        final double z = params.getZoom();
        final double paramHeight = params.getHeight();
		final double paramWidth = params.getWidth();
		final double headerHeight = header.getHeight();
		final double headerWidth = header.getWidth();
		switch (orientation) {
	        case 2: {
	            f = z * Math.min(paramHeight/headerHeight, paramWidth/headerWidth);
	            break;        	    
	        }
	        case 3: {
	            f = z * Math.min(paramHeight/headerHeight, paramWidth/headerWidth);
	            break;        	    
	        }
	        case 4: {
	            f = z * Math.min(paramHeight/headerHeight, paramWidth/headerWidth);
	            break;        	    
	        }
	        case 5: {
	            f = z * Math.min(paramHeight/headerWidth, paramWidth/headerHeight);
	            break;
	        }
	        case 6: {
	            f = z * Math.min(paramHeight/headerWidth, paramWidth/headerHeight);
	            break;
	        }
	        case 7: {
	            f = z * Math.min(paramHeight/headerWidth, paramWidth/headerHeight);
	            break;
	        }
	        case 8: {
	            f = z * Math.min(paramHeight/headerWidth,paramWidth/headerHeight);
	            break;
	        }
	        case 1:
	        default: {
	            f = z * Math.min(paramHeight/headerHeight,paramWidth/headerWidth);
	            break;        	    
	        }
        }
		
		int i = 0;
		double ff = f;
		double zz = 1.0;
		
		while ( ( ff < subsamplingFactor/reductionFactor ) &&
		        ( headerHeight*zz > minSize ) &&
		        ( headerWidth*zz > minSize ) ) {
			ff *= reductionFactor;
			zz /= reductionFactor;
			i++;
		}

		// no subsampling required
		if (i==0) return new SubsampledImage(photo.getImage(),1.0);
		
		final File file = new File(a_cacheDirectory +
				                   File.separator +
				                   photo.getFolder() +
				                   File.separator +
				                   photo.getFilename() +
				                   "," +
				                   i + 
				                   ".jpeg");
		BufferedImage im = null;
		
		if (file.exists()) {
			// return the cached subsampled image
	       try {
	            im=ImageIO.read(file);            
	        } catch (final IOException e) {
				e.printStackTrace();
	        }
		} else {
			// generate a cached subsampled image and return it
	        final AffineTransform zoom = AffineTransform.getScaleInstance(zz, zz);
	        final AffineTransformOp op = new AffineTransformOp(zoom,AffineTransformOp.TYPE_BICUBIC);
	        final BufferedImage img = new BufferedImage((int)Math.round(headerWidth*zz),
	        											(int)Math.round(headerHeight*zz),
	                                                    photo.getImage().getType());
	        im = op.filter(photo.getImage(),img);
			
	        recordImage(im,file);
		}

        return new SubsampledImage(im,1/zz);
	}
	
	/**
	 * @param image
	 * @param file
	 */
	private static void recordImage(final BufferedImage image,
			                        final File file) {
		// create the directory
		final File directory = file.getParentFile();
		directory.mkdir();
		
		// save the file
		try {
            ImageIO.write(image, "jpg", file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
	
}
