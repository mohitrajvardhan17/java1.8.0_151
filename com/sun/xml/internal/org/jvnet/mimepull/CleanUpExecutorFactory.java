package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.concurrent.Executor;

public abstract class CleanUpExecutorFactory
{
  private static final String DEFAULT_PROPERTY_NAME = CleanUpExecutorFactory.class.getName();
  
  protected CleanUpExecutorFactory() {}
  
  public static CleanUpExecutorFactory newInstance()
  {
    try
    {
      return (CleanUpExecutorFactory)FactoryFinder.find(DEFAULT_PROPERTY_NAME);
    }
    catch (Exception localException) {}
    return null;
  }
  
  public abstract Executor getExecutor();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\CleanUpExecutorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */