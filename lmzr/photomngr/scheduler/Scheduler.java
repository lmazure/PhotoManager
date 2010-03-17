package lmzr.photomngr.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Scheduler {

	public enum Category {
		CATEGORY_NOW,
		CATEGORY_FUTURE,
		CATEBORY_BACKGROUND
	};
	
    private final ExecutorService a_executor;

    public Scheduler() {
    	a_executor = Executors.newFixedThreadPool(8);
    }
    
    /**
     * @param description textual description of the task
     * @param category category of the task: CATEGORY_NOW, CATEGORY_FUTURE, or CATEBORY_BACKGROUND
     * @param priority priority between 0.0 and 1.0
     * @param task
     * @return
     */
    public Future<?> submit(final String description,
    		                final Category category,
    		                final double priority,
    		                final Runnable task) {
    	return a_executor.submit(task);
    }
}
