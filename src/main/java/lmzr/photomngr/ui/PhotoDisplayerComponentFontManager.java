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
        a_annotationFont = null;
        a_annotationFontSize = -1;
        a_messageFont = null;
        a_messageFontSize = -1;
    }
    
    /**
     * @param slotSize
     * @return font to be used to display annotations
     */
    public Font getAnnotationFont(final Dimension slotSize) {
        
        int fontSize = slotSize.width/40;
        if (fontSize>10) fontSize=10;
        if (fontSize<5) return null;
        
        if (fontSize==a_annotationFontSize) return a_annotationFont;
        
        a_annotationFontSize = fontSize;
        a_annotationFont = new Font("Arial", Font.PLAIN, a_annotationFontSize);
        
        return a_annotationFont;        
    }

    /**
     * @param slotSize
     * @return font to be used to display error messages
     */
    public Font getMessageFont(final Dimension slotSize) {
        
        final int fontSize = slotSize.width/25;
        
        if (fontSize==a_messageFontSize) return a_messageFont;
        
        a_messageFontSize = fontSize;
        a_messageFont = new Font("Arial", Font.PLAIN, a_messageFontSize);

        return a_messageFont;        
    }

}
