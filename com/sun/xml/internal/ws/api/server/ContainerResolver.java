package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class ContainerResolver
{
  private static final ThreadLocalContainerResolver DEFAULT = new ThreadLocalContainerResolver();
  private static volatile ContainerResolver theResolver = DEFAULT;
  
  public ContainerResolver() {}
  
  public static void setInstance(ContainerResolver paramContainerResolver)
  {
    if (paramContainerResolver == null) {
      paramContainerResolver = DEFAULT;
    }
    theResolver = paramContainerResolver;
  }
  
  @NotNull
  public static ContainerResolver getInstance()
  {
    return theResolver;
  }
  
  public static ThreadLocalContainerResolver getDefault()
  {
    return DEFAULT;
  }
  
  @NotNull
  public abstract Container getContainer();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\ContainerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */