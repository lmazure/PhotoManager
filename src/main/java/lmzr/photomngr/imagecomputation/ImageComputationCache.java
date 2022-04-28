package lmzr.photomngr.imagecomputation;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

import lmzr.photomngr.data.Photo;

/**
 * @author Laurent Mazuré
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
            this.a_photo = photo;
            this.a_params = params;
            this.a_image = image;
        }

        /**
         * @return Returns the image.
         */
        BufferedImage getImage() {
            return this.a_image;
        }
        /**
         * @return Returns the params.
         */
        ImageComputationParameters getParams() {
            return this.a_params;
        }
        /**
         * @return Returns the photo.
         */
        Photo getPhoto() {
            return this.a_photo;
        }
        /**
         * @return Returns the params.
         */
        int getSize() {
            return this.a_params.getHeight()*this.a_params.getWidth();
        }
    }

    static final private int s_maxSize = 6*1024*1024;
    final private LinkedList<Record> a_list;
    private int a_size;

    /**
     *
     */
    ImageComputationCache() {
        this.a_list = new LinkedList<>();
        this.a_size = 0;
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
        while ( this.a_size > s_maxSize ) {
            remove(this.a_list.getLast());
        }
    }

    private Record get(final Photo photo) {
        for (Iterator<Record> it=this.a_list.iterator(); it.hasNext(); ) {
            final Record e = it.next();
            if (e.getPhoto() == photo) return e;
        }
        return null;
    }

    private void remove(final Record r) {
        this.a_list.remove(r);
        this.a_size -= r.getSize();
    }

    private void add(final Record r) {
        this.a_list.add(0,r);
        this.a_size += r.getSize();
    }

}
