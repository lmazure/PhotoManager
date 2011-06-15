package lmzr.photomngr.imagecomputation;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;

import lmzr.photomngr.data.Photo;

/**
 * @author Laurent Mazuré
 */
public class ImageComputationCacheSoftRef {

    static private class Record {
        
        final private Photo a_photo;
        final private ImageComputationParameters a_params;
        final private BufferedImage a_image;
        
        /**
         * @param photo
         * @param params
         * @param image
         */
        Record(final Photo photo,
               final ImageComputationParameters params,
               final BufferedImage image) {
            a_photo = photo;
            a_params = params;
            a_image = image;
        }
        
        /**
         * @return Returns the image.
         */
        BufferedImage getImage() {
            return a_image;
        }
        /**
         * @return Returns the params.
         */
        ImageComputationParameters getParams() {
            return a_params;
        }
        /**
         * @return Returns the photo.
         */
        Photo getPhoto() {
            return a_photo;
        }
    }
    
    final private LinkedList<SoftReference<Record>> a_list;
    
    /**
     * 
     */
    ImageComputationCacheSoftRef() {
        a_list = new LinkedList<SoftReference<Record>>();
    }
    
    /**
     * @param photo
     * @param params
     * @return the cached image, null if the image is not cached
     */
    synchronized BufferedImage get(final Photo photo,
                                   final ImageComputationParameters params) {
        final SoftReference<Record> r = get(photo);
        if ( r == null ) return null;
        if ( !r.get().getParams().equals(params) ) return null;
        remove(r);
        add(r);
        return r.get().getImage();
    }
    
    /**
     * @param photo
     * @param params
     * @param image
     */
    synchronized void record(final Photo photo,
                             final ImageComputationParameters params,
                             final BufferedImage image) {
        final SoftReference<Record> r = get(photo);
        if (r!=null) remove(r);
        add(new SoftReference<Record>(new Record(photo,params,image)));
    }
    
    private SoftReference<Record> get(final Photo photo) {
        for (Iterator<SoftReference<Record>> it=a_list.iterator(); it.hasNext(); ) {
            final SoftReference<Record> e = it.next();
            if ( e.get() == null ) {
                a_list.remove(e);
                return null;
            }
            if ( e.get().getPhoto() == photo ) return e; 
        }
        return null;
    }
    
    private void remove(final SoftReference<Record> r) {
        a_list.remove(r);
    }

    private void add(final SoftReference<Record> r) {
        a_list.add(0,r);
    }

}
