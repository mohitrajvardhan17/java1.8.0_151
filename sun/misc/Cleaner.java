package sun.misc;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Cleaner
  extends PhantomReference<Object>
{
  private static final ReferenceQueue<Object> dummyQueue = new ReferenceQueue();
  private static Cleaner first = null;
  private Cleaner next = null;
  private Cleaner prev = null;
  private final Runnable thunk;
  
  private static synchronized Cleaner add(Cleaner paramCleaner)
  {
    if (first != null)
    {
      next = first;
      firstprev = paramCleaner;
    }
    first = paramCleaner;
    return paramCleaner;
  }
  
  private static synchronized boolean remove(Cleaner paramCleaner)
  {
    if (next == paramCleaner) {
      return false;
    }
    if (first == paramCleaner) {
      if (next != null) {
        first = next;
      } else {
        first = prev;
      }
    }
    if (next != null) {
      next.prev = prev;
    }
    if (prev != null) {
      prev.next = next;
    }
    next = paramCleaner;
    prev = paramCleaner;
    return true;
  }
  
  private Cleaner(Object paramObject, Runnable paramRunnable)
  {
    super(paramObject, dummyQueue);
    thunk = paramRunnable;
  }
  
  public static Cleaner create(Object paramObject, Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      return null;
    }
    return add(new Cleaner(paramObject, paramRunnable));
  }
  
  public void clean()
  {
    if (!remove(this)) {
      return;
    }
    try
    {
      thunk.run();
    }
    catch (Throwable localThrowable)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          if (System.err != null) {
            new Error("Cleaner terminated abnormally", localThrowable).printStackTrace();
          }
          System.exit(1);
          return null;
        }
      });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Cleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */