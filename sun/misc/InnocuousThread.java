package sun.misc;

import java.security.AccessControlContext;
import java.security.ProtectionDomain;

public final class InnocuousThread
  extends Thread
{
  private static final Unsafe UNSAFE;
  private static final ThreadGroup THREADGROUP;
  private static final AccessControlContext ACC;
  private static final long THREADLOCALS;
  private static final long INHERITABLETHREADLOCALS;
  private static final long INHERITEDACCESSCONTROLCONTEXT;
  private volatile boolean hasRun;
  
  public InnocuousThread(Runnable paramRunnable)
  {
    super(THREADGROUP, paramRunnable, "anInnocuousThread");
    UNSAFE.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, ACC);
    eraseThreadLocals();
  }
  
  public ClassLoader getContextClassLoader()
  {
    return ClassLoader.getSystemClassLoader();
  }
  
  public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler) {}
  
  public void setContextClassLoader(ClassLoader paramClassLoader)
  {
    throw new SecurityException("setContextClassLoader");
  }
  
  public void run()
  {
    if ((Thread.currentThread() == this) && (!hasRun))
    {
      hasRun = true;
      super.run();
    }
  }
  
  public void eraseThreadLocals()
  {
    UNSAFE.putObject(this, THREADLOCALS, null);
    UNSAFE.putObject(this, INHERITABLETHREADLOCALS, null);
  }
  
  static
  {
    try
    {
      ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
      UNSAFE = Unsafe.getUnsafe();
      Class localClass1 = Thread.class;
      Class localClass2 = ThreadGroup.class;
      THREADLOCALS = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("threadLocals"));
      INHERITABLETHREADLOCALS = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("inheritableThreadLocals"));
      INHERITEDACCESSCONTROLCONTEXT = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("inheritedAccessControlContext"));
      long l1 = UNSAFE.objectFieldOffset(localClass1.getDeclaredField("group"));
      long l2 = UNSAFE.objectFieldOffset(localClass2.getDeclaredField("parent"));
      ThreadGroup localThreadGroup;
      for (Object localObject = (ThreadGroup)UNSAFE.getObject(Thread.currentThread(), l1); localObject != null; localObject = localThreadGroup)
      {
        localThreadGroup = (ThreadGroup)UNSAFE.getObject(localObject, l2);
        if (localThreadGroup == null) {
          break;
        }
      }
      THREADGROUP = new ThreadGroup((ThreadGroup)localObject, "InnocuousThreadGroup");
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\InnocuousThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */