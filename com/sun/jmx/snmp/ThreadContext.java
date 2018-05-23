package com.sun.jmx.snmp;

public class ThreadContext
  implements Cloneable
{
  private ThreadContext previous;
  private String key;
  private Object value;
  private static ThreadLocal<ThreadContext> localContext = new ThreadLocal();
  
  private ThreadContext(ThreadContext paramThreadContext, String paramString, Object paramObject)
  {
    previous = paramThreadContext;
    key = paramString;
    value = paramObject;
  }
  
  public static Object get(String paramString)
    throws IllegalArgumentException
  {
    ThreadContext localThreadContext = contextContaining(paramString);
    if (localThreadContext == null) {
      return null;
    }
    return value;
  }
  
  public static boolean contains(String paramString)
    throws IllegalArgumentException
  {
    return contextContaining(paramString) != null;
  }
  
  private static ThreadContext contextContaining(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null key");
    }
    for (ThreadContext localThreadContext = getContext(); localThreadContext != null; localThreadContext = previous) {
      if (paramString.equals(key)) {
        return localThreadContext;
      }
    }
    return null;
  }
  
  public static ThreadContext push(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null key");
    }
    ThreadContext localThreadContext1 = getContext();
    if (localThreadContext1 == null) {
      localThreadContext1 = new ThreadContext(null, null, null);
    }
    ThreadContext localThreadContext2 = new ThreadContext(localThreadContext1, paramString, paramObject);
    setContext(localThreadContext2);
    return localThreadContext1;
  }
  
  public static ThreadContext getThreadContext()
  {
    return getContext();
  }
  
  public static void restore(ThreadContext paramThreadContext)
    throws NullPointerException, IllegalArgumentException
  {
    if (paramThreadContext == null) {
      throw new NullPointerException();
    }
    for (ThreadContext localThreadContext = getContext(); localThreadContext != paramThreadContext; localThreadContext = previous) {
      if (localThreadContext == null) {
        throw new IllegalArgumentException("Restored context is not contained in current context");
      }
    }
    if (key == null) {
      paramThreadContext = null;
    }
    setContext(paramThreadContext);
  }
  
  public void setInitialContext(ThreadContext paramThreadContext)
    throws IllegalArgumentException
  {
    if (getContext() != null) {
      throw new IllegalArgumentException("previous context not empty");
    }
    setContext(paramThreadContext);
  }
  
  private static ThreadContext getContext()
  {
    return (ThreadContext)localContext.get();
  }
  
  private static void setContext(ThreadContext paramThreadContext)
  {
    localContext.set(paramThreadContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\ThreadContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */