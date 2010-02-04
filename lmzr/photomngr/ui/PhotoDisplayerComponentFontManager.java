package lmzr.photomngr.ui;

import java.awt.Dimension;
import java.awt.Font;

/**
 * @author Laurent Mazuré
 */
public class PhotoDisplayerComponentFontManager {

    private Font s_theFont1;
    int s_fontSize1;
    private Font s_theFont2;
    int s_fontSize2;
    
    /**
     * 
     */
     public PhotoDisplayerComponentFontManager() {
        s_theFont1 = null;
        s_fontSize1 = -1;
        s_theFont2 = null;
        s_fontSize2 = -1;
    }
    
    /**
     * @param slotSize
     * @return font to be used to display annotations
     */
    public Font getAnnotationFont(final Dimension slotSize) {
        
        int fontSize = slotSize.width/40;
        if (fontSize>10) fontSize=10;
        if (fontSize<5) return null;
        
        if (fontSize==s_fontSize1) return s_theFont1;
        
        s_fontSize1 = fontSize;
        s_theFont1 = new Font("Arial", Font.PLAIN, s_fontSize1);
        
        return s_theFont1;        
    }

    /**
     * @param slotSize
     * @return font to be used to display error messages
     */
    public Font getMessageFont(final Dimension slotSize) {
        
        final int fontSize = slotSize.width/15;
        
        if (fontSize==s_fontSize2) return s_theFont2;
        
        s_fontSize2 = fontSize;
        s_theFont2 = new Font("Arial", Font.PLAIN, s_fontSize2);

        return s_theFont2;        
    }

}
