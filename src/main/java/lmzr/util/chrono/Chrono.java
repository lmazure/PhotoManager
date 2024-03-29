package lmzr.util.chrono;

import java.util.HashMap;

/**
 * @author Laurent
 *
 */
public class Chrono {

    /**
     *
     */
    static public final int START = 0;
    /**
     *
     */
    static public final int MIDDLE = 1;
    /**
     *
     */
    static public final int END = 2;
    /**
     *
     */
    static public final int EVENT = 3;

    private static long s_startTime;
    final private static HashMap<String, Long> s_records =  new HashMap<>();

    /**
     *
     */
    static public void setBeginningOfTime() {
        s_startTime = System.nanoTime();
    }

    /**
     * @param mode
     * @param tag
     */
    static public void getTime(final int mode,
                               final String tag) {

        final long currentTime = System.nanoTime();
        final long relativeCurrentTime = currentTime - s_startTime;

        switch (mode) {
        case START:
            System.out.println("Chrono -- START -- time="+relativeCurrentTime/1000000000d);
            s_records.put(tag, Long.valueOf(currentTime));
            break;
        case MIDDLE: {
            final long startTime = s_records.get(tag).longValue();
            final long duration = currentTime - startTime;
            System.out.println("Chrono -- MIDDLE -- time="+relativeCurrentTime/1000000000d+" -- duration="+duration/1000000000d);
            break;
        }
        case END: {
            final long startTime = s_records.get(tag).longValue();
            s_records.remove(tag);
            final long duration = currentTime - startTime;
            System.out.println("Chrono -- END -- time="+relativeCurrentTime/1000000000d+" -- duration="+duration/1000000000d);
            break;
        }
        case EVENT:
            System.out.println("Chrono -- START -- time="+relativeCurrentTime/1000000000d);
            break;
        default:
            break;
        }
    }
}
