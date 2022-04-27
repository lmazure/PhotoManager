/*
 * Created on 2 mai 2005
 *
 */
package lmzr.util.debug;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * @author Laurent
 */
public class StringDebugger {

    /**
     * dump on System.out the characters of a string as hexadecimal values
     * @param s the string to dump
     */
    public static void dump(final String s) {
        final StringCharacterIterator it = new StringCharacterIterator(s);

        for (char c=it.first(); c!=CharacterIterator.DONE; c=it.next()) {
            System.out.println(c+" 0x"+Integer.toString(c,16));
        }
    }
}
