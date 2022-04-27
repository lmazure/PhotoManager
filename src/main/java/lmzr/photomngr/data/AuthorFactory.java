package lmzr.photomngr.data;

import java.util.Arrays;
import java.util.Comparator;
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
            this.a_str = str;
            this.a_incr = 0;
        }
        void increment() { this.a_incr++; }
        int getNumber() { return this.a_incr; }
        String getString() { return this.a_str; }
    }

    final private Map<String,Record> a_set;
    private String[] a_cache;

    /**
     *
     */
    AuthorFactory() {
        this.a_set = new HashMap<>();
        this.a_cache = null;
    }

    /**
     * @param str
     * @return string defining the author
     */
    public String create(final String str) {
        Record r;
        if (this.a_set.containsKey(str)) {
            r = this.a_set.get(str);
        } else {
            r = new Record(str);
            this.a_set.put(str,r);
        }
        r.increment();
        this.a_cache = null;
        return r.getString();
    }

    /**
     * @return list of authors
     */
    public String[] getAuthors() {
        if ( this.a_cache == null ) {
            final Record[] r = this.a_set.values().toArray(new Record[0]);
            Arrays.sort(r,new Comparator<Record>() { @Override
                public int compare(final Record r1, final Record r2) { return r2.getNumber() - r1.getNumber(); }
            });
            this.a_cache = new String[r.length];
            for (int i=0; i<r.length; i++) this.a_cache[i] = r[i].getString();
        }
        return this.a_cache;
    }
}
