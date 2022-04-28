package lmzr.photomngr.ui;

import java.awt.Dimension;
import java.awt.Font;

/**
 * @author Laurent Mazuré
 */
public class PhotoDisplayerComponentFontManager {

    private Font a_annotationFont;
    private int a_annotationFontSize;
    private Font a_messageFont;
    private int a_messageFontSize;

    /**
     *
     */
     public PhotoDisplayerComponentFontManager() {
        this.a_annotationFont = null;
        this.a_annotationFontSize = -1;
        this.a_messageFont = null;
        this.a_messageFontSize = -1;
    }

    /**
     * @param slotSize
     * @return font to be used to display annotations
     */
    public Font getAnnotationFont(final Dimension slotSize) {

        int fontSize = slotSize.width/40;
        if (fontSize>10) fontSize=10;
        if (fontSize<5) return null;

        if (fontSize==this.a_annotationFontSize) return this.a_annotationFont;

        this.a_annotationFontSize = fontSize;
        this.a_annotationFont = new Font("Arial", Font.PLAIN, this.a_annotationFontSize);

        return this.a_annotationFont;
    }

    /**
     * @param slotSize
     * @return font to be used to display error messages
     */
    public Font getMessageFont(final Dimension slotSize) {

        final int fontSize = slotSize.width/25;

        if (fontSize==this.a_messageFontSize) return this.a_messageFont;

        this.a_messageFontSize = fontSize;
        this.a_messageFont = new Font("Arial", Font.PLAIN, this.a_messageFontSize);

        return this.a_messageFont;
    }

}
