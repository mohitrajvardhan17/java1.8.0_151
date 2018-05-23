package com.sun.tracing;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import sun.security.action.GetPropertyAction;
import sun.tracing.MultiplexProviderFactory;
import sun.tracing.NullProviderFactory;
import sun.tracing.PrintStreamProviderFactory;
import sun.tracing.dtrace.DTraceProviderFactory;

public abstract class ProviderFactory
{
  protected ProviderFactory() {}
  
  public abstract <T extends Provider> T createProvider(Class<T> paramClass);
  
  public static ProviderFactory getDefaultFactory()
  {
    HashSet localHashSet = new HashSet();
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("com.sun.tracing.dtrace"));
    if (((str1 == null) || (!str1.equals("disable"))) && (DTraceProviderFactory.isSupported())) {
      localHashSet.add(new DTraceProviderFactory());
    }
    str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.tracing.stream"));
    if (str1 != null) {
      for (String str2 : str1.split(","))
      {
        PrintStream localPrintStream = getPrintStreamFromSpec(str2);
        if (localPrintStream != null) {
          localHashSet.add(new PrintStreamProviderFactory(localPrintStream));
        }
      }
    }
    if (localHashSet.size() == 0) {
      return new NullProviderFactory();
    }
    if (localHashSet.size() == 1) {
      return ((ProviderFactory[])localHashSet.toArray(new ProviderFactory[1]))[0];
    }
    return new MultiplexProviderFactory(localHashSet);
  }
  
  private static PrintStream getPrintStreamFromSpec(final String paramString)
  {
    try
    {
      final int i = paramString.lastIndexOf('.');
      Class localClass = Class.forName(paramString.substring(0, i));
      Field localField = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Field run()
          throws NoSuchFieldException
        {
          return val$cls.getField(paramString.substring(i + 1));
        }
      });
      return (PrintStream)localField.get(null);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new AssertionError(localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError(localIllegalAccessException);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new AssertionError(localPrivilegedActionException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\tracing\ProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */