package lmzr.photomngr.imagecomputation;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import lmzr.photomngr.data.Photo;

/**
 * @author Laurent
 */
public class ImageComputationCache {

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
        /**
         * @return Returns the params.
         */
        int getSize() {
            return a_params.getHeight()*a_params.getWidth();
        }
    }
    
    static final private int s_maxSize = 6*1024*1024;
    final private LinkedList<Record> a_list;
    private int a_size;
    
    /**
     * 
     */
    ImageComputationCache() {
        a_list = new LinkedList<Record>();
        a_size = 0;
    }
    
    /**
     * @param photo
     * @param params
     * @return the cached image, null if the image is not cached
     */
    synchronized BufferedImage get(final Photo photo,
                                   final ImageComputationParameters params) {
        final Record r = get(photo);
        if ( r == null ) return null;
        if ( !r.getParams().equals(params) ) return null;
        remove(r);
        add(r);
        return r.getImage();
    }
    
    /**
     * @param photo
     * @param params
     * @param image
     */
    synchronized void record(final Photo photo,
                             final ImageComputationParameters params,
                             final BufferedImage image) {
        final Record r = get(photo);
        if (r!=null) remove(r);
        add(new Record(photo,params,image));
        while ( a_size > s_maxSize ) {
            remove(a_list.getLast());
        }
    }
    
    private Record get(final Photo photo) {
        for (Iterator<Record> it=a_list.iterator(); it.hasNext(); ) {
            final Record e = it.next();
            if (e.getPhoto() == photo) return e; 
        }
        return null;
    }
    
    private void remove(final Record r) {
        a_list.remove(r);
        a_size -= r.getSize();
    }

    private void add(final Record r) {
        a_list.add(0,r);
        a_size += r.getSize();
    }

}
