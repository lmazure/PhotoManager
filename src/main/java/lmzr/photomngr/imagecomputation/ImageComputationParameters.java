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
        super();

        if ( (zoom==0) ||
                (width==0) ||
                (height==0) ) {
               throw new IllegalArgumentException("incorrect ImageComputationParameters");
           }

        this.a_width = width;
        this.a_height = height;
        this.a_zoom = zoom;
        this.a_rotation = rotation;
        this.a_focusX = focusX;
        this.a_focusY = focusY;
    }

    /**
     * @return Returns the zoom
     */
    public float getZoom() {
        return this.a_zoom;
    }

    /**
     * @return Returns the height
     */
    public int getHeight() {
        return this.a_height;
    }

    /**
     * @return Returns the width
     */
    public int getWidth() {
        return this.a_width;
    }

    /**
     * @return Return the rotation
     */
    public float getRotation() {
        return this.a_rotation;
    }

    /**
     * @return Return the first coordinate of the focus
     */
    public float getFocusX() {
        return this.a_focusX;
    }

    /**
     * @return Return the second coordinate of the focus
    */
    public float getFocusY() {
        return this.a_focusY;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ImageComputationParameters)) return false;
        final ImageComputationParameters o = (ImageComputationParameters) other;
        if ( o.a_width != this.a_width ) return false;
        if ( o.a_height != this.a_height ) return false;
        if ( o.a_zoom != this.a_zoom ) return false;
        if ( o.a_rotation != this.a_rotation ) return false;
        if ( o.a_focusX != this.a_focusX ) return false;
        if ( o.a_focusY != this.a_focusY ) return false;
        return true;
      }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ( this.a_width * 1024 + this.a_height )
               ^ Float.floatToIntBits(this.a_zoom)
               ^ Float.floatToIntBits(this.a_rotation)
               ^ Float.floatToIntBits(this.a_focusX)
               ^ Float.floatToIntBits(this.a_focusX);
    }
}
