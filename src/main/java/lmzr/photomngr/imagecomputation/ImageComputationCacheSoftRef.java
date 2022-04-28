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
    }

    final private LinkedList<SoftReference<Record>> a_list;

    /**
     *
     */
    ImageComputationCacheSoftRef() {
        this.a_list = new LinkedList<>();
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
        add(new SoftReference<>(new Record(photo,params,image)));
    }

    private SoftReference<Record> get(final Photo photo) {
        for (Iterator<SoftReference<Record>> it=this.a_list.iterator(); it.hasNext(); ) {
            final SoftReference<Record> e = it.next();
            if ( e.get() == null ) {
                this.a_list.remove(e);
                return null;
            }
            if ( e.get().getPhoto() == photo ) return e;
        }
        return null;
    }

    private void remove(final SoftReference<Record> r) {
        this.a_list.remove(r);
    }

    private void add(final SoftReference<Record> r) {
        this.a_list.add(0,r);
    }

}
