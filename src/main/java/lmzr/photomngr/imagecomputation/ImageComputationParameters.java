package lmzr.photomngr.imagecomputation;

/**
 * @author Laurent Mazuré
 */
public class ImageComputationParameters {

    private final int a_width;
    private final int a_height;
    private final float a_zoom;
    private final float a_rotation;
    private final float a_focusX;
    private final float a_focusY;

    /**
     * @param width
     * @param height
     * @param zoom
     * @param rotation
     * @param focusX
     * @param focusY
     */
    public ImageComputationParameters(final int width,
                                      final int height,
                                      final float zoom,
                                      final float rotation,
                                      final float focusX,
                                      final float focusY) {
        if ( (zoom==0) ||
                (width==0) ||
                (height==0) ) {
               throw new IllegalArgumentException("incorrect ImageComputationParameters");
           }

        a_width = width;
        a_height = height;
        a_zoom = zoom;
        a_rotation = rotation;
        a_focusX = focusX;
        a_focusY = focusY;
    }

    /**
     * @return Returns the zoom
     */
    public float getZoom() {
        return a_zoom;
    }

    /**
     * @return Returns the height
     */
    public int getHeight() {
        return a_height;
    }

    /**
     * @return Returns the width
     */
    public int getWidth() {
        return a_width;
    }

    /**
     * @return Return the rotation
     */
    public float getRotation() {
        return a_rotation;
    }

    /**
     * @return Return the first coordinate of the focus
     */
    public float getFocusX() {
        return a_focusX;
    }

    /**
     * @return Return the second coordinate of the focus
    */
    public float getFocusY() {
        return a_focusY;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof final ImageComputationParameters o)) {
            return false;
        }
        if ( o.a_width != a_width ) {
            return false;
        }
        if ( o.a_height != a_height ) {
            return false;
        }
        if ( o.a_zoom != a_zoom ) {
            return false;
        }
        if ( o.a_rotation != a_rotation ) {
            return false;
        }
        if ( o.a_focusX != a_focusX ) {
            return false;
        }
        if ( o.a_focusY != a_focusY ) {
            return false;
        }
        return true;
      }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ( a_width * 1024 + a_height )
               ^ Float.floatToIntBits(a_zoom)
               ^ Float.floatToIntBits(a_rotation)
               ^ Float.floatToIntBits(a_focusX)
               ^ Float.floatToIntBits(a_focusX);
    }
}
