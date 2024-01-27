package lmzr.photomngr.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class AuthorFactory {

    static private class Record {
        final String a_str;
        int a_incr;
        Record(final String str) {
            a_str = str;
            a_incr = 0;
        }
        void increment() { a_incr++; }
        int getNumber() { return a_incr; }
        String getString() { return a_str; }
    }

    final private Map<String,Record> a_set;
    private String[] a_cache;

    /**
     *
     */
    AuthorFactory() {
        a_set = new HashMap<>();
        a_cache = null;
    }

    /**
     * @param str
     * @return string defining the author
     */
    public String create(final String str) {
        Record r;
        if (a_set.containsKey(str)) {
            r = a_set.get(str);
        } else {
            r = new Record(str);
            a_set.put(str,r);
        }
        r.increment();
        a_cache = null;
        return r.getString();
    }

    /**
     * @return list of authors
     */
    public String[] getAuthors() {
        if ( a_cache == null ) {
            final Record[] r = a_set.values().toArray(new Record[0]);
            Arrays.sort(r,(r1, r2) -> r2.getNumber() - r1.getNumber());
            a_cache = new String[r.length];
            for (int i=0; i<r.length; i++) {
                a_cache[i] = r[i].getString();
            }
        }
        return a_cache;
    }
}
