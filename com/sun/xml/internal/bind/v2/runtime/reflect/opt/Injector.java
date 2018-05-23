package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Injector
{
  private static final ReentrantReadWriteLock irwl;
  private static final Lock ir;
  private static final Lock iw;
  private static final Map<ClassLoader, WeakReference<Injector>> injectors;
  private static final Logger logger;
  private final Map<String, Class> classes = new HashMap();
  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock r = rwl.readLock();
  private final Lock w = rwl.writeLock();
  private final ClassLoader parent;
  private final boolean loadable;
  private static final Method defineClass;
  private static final Method resolveClass;
  private static final Method findLoadedClass;
  
  static Class inject(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte)
  {
    Injector localInjector = get(paramClassLoader);
    if (localInjector != null) {
      return localInjector.inject(paramString, paramArrayOfByte);
    }
    return null;
  }
  
  static Class find(ClassLoader paramClassLoader, String paramString)
  {
    Injector localInjector = get(paramClassLoader);
    if (localInjector != null) {
      return localInjector.find(paramString);
    }
    return null;
  }
  
  /* Error */
  private static Injector get(ClassLoader paramClassLoader)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: getstatic 219	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:ir	Ljava/util/concurrent/locks/Lock;
    //   5: invokeinterface 261 1 0
    //   10: getstatic 218	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:injectors	Ljava/util/Map;
    //   13: aload_0
    //   14: invokeinterface 259 2 0
    //   19: checkcast 127	java/lang/ref/WeakReference
    //   22: astore_2
    //   23: getstatic 219	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:ir	Ljava/util/concurrent/locks/Lock;
    //   26: invokeinterface 262 1 0
    //   31: goto +14 -> 45
    //   34: astore_3
    //   35: getstatic 219	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:ir	Ljava/util/concurrent/locks/Lock;
    //   38: invokeinterface 262 1 0
    //   43: aload_3
    //   44: athrow
    //   45: aload_2
    //   46: ifnull +11 -> 57
    //   49: aload_2
    //   50: invokevirtual 247	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
    //   53: checkcast 10	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector
    //   56: astore_1
    //   57: aload_1
    //   58: ifnonnull +94 -> 152
    //   61: new 127	java/lang/ref/WeakReference
    //   64: dup
    //   65: new 10	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector
    //   68: dup
    //   69: aload_0
    //   70: invokespecial 229	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:<init>	(Ljava/lang/ClassLoader;)V
    //   73: dup
    //   74: astore_1
    //   75: invokespecial 248	java/lang/ref/WeakReference:<init>	(Ljava/lang/Object;)V
    //   78: astore_2
    //   79: getstatic 220	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:iw	Ljava/util/concurrent/locks/Lock;
    //   82: invokeinterface 261 1 0
    //   87: getstatic 218	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:injectors	Ljava/util/Map;
    //   90: aload_0
    //   91: invokeinterface 258 2 0
    //   96: ifne +14 -> 110
    //   99: getstatic 218	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:injectors	Ljava/util/Map;
    //   102: aload_0
    //   103: aload_2
    //   104: invokeinterface 260 3 0
    //   109: pop
    //   110: getstatic 220	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:iw	Ljava/util/concurrent/locks/Lock;
    //   113: invokeinterface 262 1 0
    //   118: goto +16 -> 134
    //   121: astore 4
    //   123: getstatic 220	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:iw	Ljava/util/concurrent/locks/Lock;
    //   126: invokeinterface 262 1 0
    //   131: aload 4
    //   133: athrow
    //   134: goto +18 -> 152
    //   137: astore_3
    //   138: getstatic 225	com/sun/xml/internal/bind/v2/runtime/reflect/opt/Injector:logger	Ljava/util/logging/Logger;
    //   141: getstatic 227	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   144: ldc 3
    //   146: aload_3
    //   147: invokevirtual 257	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   150: aconst_null
    //   151: areturn
    //   152: aload_1
    //   153: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	154	0	paramClassLoader	ClassLoader
    //   1	152	1	localInjector	Injector
    //   22	82	2	localWeakReference	WeakReference
    //   34	10	3	localObject1	Object
    //   137	10	3	localSecurityException	SecurityException
    //   121	11	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   10	23	34	finally
    //   87	110	121	finally
    //   121	123	121	finally
    //   61	134	137	java/lang/SecurityException
  }
  
  private Injector(ClassLoader paramClassLoader)
  {
    parent = paramClassLoader;
    assert (paramClassLoader != null);
    boolean bool = false;
    try
    {
      bool = paramClassLoader.loadClass(Accessor.class.getName()) == Accessor.class;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    loadable = bool;
  }
  
  private Class inject(String paramString, byte[] paramArrayOfByte)
  {
    if (!loadable) {
      return null;
    }
    int i = 0;
    int j = 0;
    try
    {
      r.lock();
      j = 1;
      Class localClass1 = (Class)classes.get(paramString);
      r.unlock();
      j = 0;
      Throwable localThrowable;
      if (localClass1 == null)
      {
        try
        {
          localClass1 = (Class)findLoadedClass.invoke(parent, new Object[] { paramString.replace('/', '.') });
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          logger.log(Level.FINE, "Unable to find " + paramString, localIllegalArgumentException);
        }
        catch (IllegalAccessException localIllegalAccessException1)
        {
          logger.log(Level.FINE, "Unable to find " + paramString, localIllegalAccessException1);
        }
        catch (InvocationTargetException localInvocationTargetException1)
        {
          localThrowable = localInvocationTargetException1.getTargetException();
          logger.log(Level.FINE, "Unable to find " + paramString, localThrowable);
        }
        if (localClass1 != null)
        {
          w.lock();
          i = 1;
          classes.put(paramString, localClass1);
          w.unlock();
          i = 0;
          Class localClass2 = localClass1;
          return localClass2;
        }
      }
      if (localClass1 == null)
      {
        r.lock();
        j = 1;
        localClass1 = (Class)classes.get(paramString);
        r.unlock();
        j = 0;
        if (localClass1 == null)
        {
          try
          {
            localClass1 = (Class)defineClass.invoke(parent, new Object[] { paramString.replace('/', '.'), paramArrayOfByte, Integer.valueOf(0), Integer.valueOf(paramArrayOfByte.length) });
            resolveClass.invoke(parent, new Object[] { localClass1 });
          }
          catch (IllegalAccessException localIllegalAccessException2)
          {
            logger.log(Level.FINE, "Unable to inject " + paramString, localIllegalAccessException2);
            localThrowable = null;
            return localThrowable;
          }
          catch (InvocationTargetException localInvocationTargetException2)
          {
            localThrowable = localInvocationTargetException2.getTargetException();
            if ((localThrowable instanceof LinkageError)) {
              logger.log(Level.FINE, "duplicate class definition bug occured? Please report this : " + paramString, localThrowable);
            } else {
              logger.log(Level.FINE, "Unable to inject " + paramString, localThrowable);
            }
            Class localClass4 = null;
            return localClass4;
          }
          catch (SecurityException localSecurityException)
          {
            logger.log(Level.FINE, "Unable to inject " + paramString, localSecurityException);
            localThrowable = null;
            return localThrowable;
          }
          catch (LinkageError localLinkageError)
          {
            logger.log(Level.FINE, "Unable to inject " + paramString, localLinkageError);
            localThrowable = null;
            return localThrowable;
          }
          w.lock();
          i = 1;
          if (!classes.containsKey(paramString)) {
            classes.put(paramString, localClass1);
          }
          w.unlock();
          i = 0;
        }
      }
      Class localClass3 = localClass1;
      return localClass3;
    }
    finally
    {
      if (j != 0) {
        r.unlock();
      }
      if (i != 0) {
        w.unlock();
      }
    }
  }
  
  private Class find(String paramString)
  {
    r.lock();
    try
    {
      Class localClass = (Class)classes.get(paramString);
      return localClass;
    }
    finally
    {
      r.unlock();
    }
  }
  
  static
  {
    irwl = new ReentrantReadWriteLock();
    ir = irwl.readLock();
    iw = irwl.writeLock();
    injectors = new WeakHashMap();
    logger = Util.getClassLogger();
    try
    {
      defineClass = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, Integer.TYPE, Integer.TYPE });
      resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", new Class[] { Class.class });
      findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] { String.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Injector.defineClass.setAccessible(true);
        Injector.resolveClass.setAccessible(true);
        Injector.findLoadedClass.setAccessible(true);
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\Injector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */