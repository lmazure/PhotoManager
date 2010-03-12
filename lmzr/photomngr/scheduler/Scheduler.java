package lmzr.photomngr.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Scheduler {

    private final ExecutorService a_executor;

    public Scheduler() {
    	a_executor = Executors.newFixedThreadPool(8);
    }
    
    public Future<?> submit(Runnable task) {
    	return a_executor.submit(task);
    }
}
