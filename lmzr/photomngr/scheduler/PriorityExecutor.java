package lmzr.photomngr.scheduler;

import java.util.Comparator;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lmzr.photomngr.scheduler.Scheduler.Category;
import lmzr.photomngr.scheduler.Scheduler.Priority;

/**
 * initially from http://binkley.blogspot.com/2009/04/jumping-work-queue-in-executor.html
 *
 */
public class PriorityExecutor extends ThreadPoolExecutor {
    
    /**
     * @param maxNumberOfThreads
     */
    public PriorityExecutor(final int maxNumberOfThreads) {
        super(0, maxNumberOfThreads, 60L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(2, new PriorityTaskComparator()));
    }

    /**
     * @see java.util.concurrent.AbstractExecutorService#newTaskFor(java.lang.Runnable, java.lang.Object)
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable,
                                               final T value) {

        final PriorityRunnable run = (PriorityRunnable) runnable;
        return new PriorityTask<T>(run.getCategory(), run.getPriority(), run.getSubpriority(), runnable, value);
    }

    /**
     * @param <T>
     */
    private static final class PriorityTask<T> extends FutureTask<T>
                                               implements Comparable<PriorityTask<T>> {
        private final double a_prio;

        /**
         * @param category
         * @param priority
         * @param subpriority
         * @param runnable
         * @param result
         */
        public PriorityTask(final Category category,
                            final Priority priority,
                            final double subpriority,
                            final Runnable runnable,
                            final T result) {
            super(runnable, result);

            a_prio = computePriority(category,priority,subpriority);
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(final PriorityTask<T> o) {
            final double diff = o.a_prio - a_prio;
            if (diff<0) return -1;
            if (diff>0) return 1;
            return 0;
        }
        
        /**
         * @param category
         * @param priority
         * @param subpriority
         * @return
         */
        private static double computePriority(final Category category,
                                              final Priority priority,
                                              final double subpriority) {
            
            double prio = 0;
            
            switch (category) {
            case CATEGORY_NOW:
                prio = 20;
                break;
            case CATEGORY_FUTURE:
                  prio = 10;
                  break;
            case CATEBORY_BACKGROUND:
                prio = 0;
                break;                
            }
            
            switch (priority) {
            case PRIORITY_VERY_HIGH:
                prio += 4;
                break;
            case PRIORITY_HIGH:
                prio += 3;
                break;
            case PRIORITY_MEDIUM:
                prio += 3;
                break;
            case PRIORITY_LOW:
                prio += 2;
                break;
            case PRIORITY_VERY_LOW:
                prio += 1;
                break;
            }
            
            prio += subpriority;
            
            return prio;
        }
    }

    /**
     * @author lmazure
     *
     */
    private static class PriorityTaskComparator implements Comparator<Runnable> {
        @Override
        public int compare(final Runnable left,
                           final Runnable right) {
            return ((PriorityTask) left).compareTo((PriorityTask) right);
        }
    }
}