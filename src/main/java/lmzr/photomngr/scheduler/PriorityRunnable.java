package lmzr.photomngr.scheduler;

import lmzr.photomngr.scheduler.Scheduler.Category;
import lmzr.photomngr.scheduler.Scheduler.Priority;

/**
*
*/
public class PriorityRunnable implements Runnable {

   private final Category a_category;
   private final Priority a_priority;
   private final double a_subpriority;
   private final Runnable a_runnable;

   /**
    * @param category
    * @param priority
    * @param subpriority
    * @param runnable
    */
   public PriorityRunnable(final Category category,
                           final Priority priority,
                           final double subpriority,
                           final Runnable runnable) {
       this.a_category = category;
       this.a_priority = priority;
       this.a_subpriority = subpriority;
       this.a_runnable = runnable;
   }

   /**
    * @return category of the Runnable
    */
   public Category getCategory() {
       return this.a_category;
   }
   /**
    * @return priority of the Runnable
    */
   public Priority getPriority() {
       return this.a_priority;
   }
   /**
    * @return subpriority of the Runnable
    */
   public double getSubpriority() {
       return this.a_subpriority;
   }

   /**
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
       this.a_runnable.run();
   }

}