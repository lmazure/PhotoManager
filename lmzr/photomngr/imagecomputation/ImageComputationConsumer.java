package lmzr.photomngr.imagecomputation;

import java.awt.image.BufferedImage;

import lmzr.photomngr.data.Photo;

/**
 * @author Laurent Mazuré
 */
public interface ImageComputationConsumer {

    /**
     * @param photo
     * @param params
     * @param image
     */
    void consumeImageComputation(Photo photo,
                                 ImageComputationParameters params,
                                 BufferedImage image);

}
