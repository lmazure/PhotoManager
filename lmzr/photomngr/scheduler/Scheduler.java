package lmzr.photomngr.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lmazure
 *
 */
public class Scheduler {

	/**
	 *  Category of the tasks
	 */
	public enum Category {
	    /** task to be ran now */
		CATEGORY_NOW,
        /** task that can be ran later */
		CATEGORY_FUTURE,
        /** background task */
		CATEBORY_BACKGROUND
	};
	
	/**
	 * Priority of the tasks
	 */
	public enum Priority {
        /** very high priority */
	    PRIORITY_VERY_HIGH,
        /** very  priority */
        PRIORITY_HIGH,
        /** very medium */
        PRIORITY_MEDIUM,
        /** very low */
        PRIORITY_LOW,
        /** very high low */
        PRIORITY_VERY_LOW
	}
	
    private final ExecutorService a_executor;

    /**
     * 
     */
    public Scheduler() {
    	a_executor = Executors.newFixedThreadPool(8);
    }
    
    /**
     * @param description textual description of the task
     * @param category category of the task
     * @param priority priority of the task
     * @param subpriority subpriority of the task: between 0.0 and 1.0
     * @param task
     * @return future result of the task
     */
    public Future<?> submit(final String description,
    		                final Category category,
    		                final Priority priority,
    		                final double subpriority,
    		                final Runnable task) {
    	return a_executor.submit(task);
    }
}
