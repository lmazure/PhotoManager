package lmzr.photomngr.data;

import java.util.HashMap;

/**
 * @author Laurent Mazuré
 */
public class StringPool {

    private final HashMap<String,String> a_pool;

    /**
     *
     */
    public StringPool() {
        this.a_pool = new HashMap<>();
    }

    /**
     * @param str
     * @return interned string
     */
    public String replace(final String str) {

        final String s = this.a_pool.get(str);

        if ( s!=null ) {
            return s;
        }

        this.a_pool.put(str,str);
        return str;
    }

}
