package jdk.internal.instrumentation;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public final class Tracer
{
  private final List<InstrumentationData> items = new ArrayList();
  private static final Tracer singleton;
  
  private Tracer() {}
  
  @CallerSensitive
  public static Tracer getInstance()
  {
    Class localClass = Reflection.getCallerClass();
    if (!VM.isSystemDomainLoader(localClass.getClassLoader())) {
      throw new SecurityException("Only classes in the system domain can get a Tracer instance");
    }
    return singleton;
  }
  
  public synchronized void addInstrumentations(List<Class<?>> paramList, Logger paramLogger)
    throws ClassNotFoundException
  {
    if (paramLogger == null) {
      throw new IllegalArgumentException("logger can't be null");
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      InstrumentationTarget localInstrumentationTarget = (InstrumentationTarget)localClass.getAnnotation(InstrumentationTarget.class);
      InstrumentationData localInstrumentationData = new InstrumentationData(null);
      instrumentation = localClass;
      target = Class.forName(localInstrumentationTarget.value(), true, localClass.getClassLoader());
      logger = paramLogger;
      localArrayList.add(target);
      items.add(localInstrumentationData);
    }
    retransformClasses0((Class[])localArrayList.toArray(new Class[0]));
  }
  
  private byte[] transform(Class<?> paramClass, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    Iterator localIterator = items.iterator();
    while (localIterator.hasNext())
    {
      InstrumentationData localInstrumentationData = (InstrumentationData)localIterator.next();
      if (target.equals(paramClass)) {
        try
        {
          logger.trace("Processing instrumentation class: " + instrumentation);
          arrayOfByte = new ClassInstrumentation(instrumentation, paramClass.getName(), arrayOfByte, logger).getNewBytes();
        }
        catch (Throwable localThrowable)
        {
          logger.error("Failure during class instrumentation:", localThrowable);
        }
      }
    }
    if (arrayOfByte == paramArrayOfByte) {
      return null;
    }
    return arrayOfByte;
  }
  
  private static native void retransformClasses0(Class<?>[] paramArrayOfClass);
  
  private static byte[] retransformCallback(Class<?> paramClass, byte[] paramArrayOfByte)
  {
    return singleton.transform(paramClass, paramArrayOfByte);
  }
  
  private static native void init();
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("bci");
        return null;
      }
    }, null, new Permission[] { new RuntimePermission("loadLibrary.bci") });
    singleton = new Tracer();
    init();
  }
  
  private final class InstrumentationData
  {
    Class<?> instrumentation;
    Class<?> target;
    Logger logger;
    
    private InstrumentationData() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\Tracer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */