package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class ServiceInterceptorFactory
{
  private static ThreadLocal<Set<ServiceInterceptorFactory>> threadLocalFactories = new ThreadLocal()
  {
    protected Set<ServiceInterceptorFactory> initialValue()
    {
      return new HashSet();
    }
  };
  
  public ServiceInterceptorFactory() {}
  
  public abstract ServiceInterceptor create(@NotNull WSService paramWSService);
  
  @NotNull
  public static ServiceInterceptor load(@NotNull WSService paramWSService, @Nullable ClassLoader paramClassLoader)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ServiceFinder.find(ServiceInterceptorFactory.class).iterator();
    ServiceInterceptorFactory localServiceInterceptorFactory;
    while (localIterator.hasNext())
    {
      localServiceInterceptorFactory = (ServiceInterceptorFactory)localIterator.next();
      localArrayList.add(localServiceInterceptorFactory.create(paramWSService));
    }
    localIterator = ((Set)threadLocalFactories.get()).iterator();
    while (localIterator.hasNext())
    {
      localServiceInterceptorFactory = (ServiceInterceptorFactory)localIterator.next();
      localArrayList.add(localServiceInterceptorFactory.create(paramWSService));
    }
    return ServiceInterceptor.aggregate((ServiceInterceptor[])localArrayList.toArray(new ServiceInterceptor[localArrayList.size()]));
  }
  
  public static boolean registerForThread(ServiceInterceptorFactory paramServiceInterceptorFactory)
  {
    return ((Set)threadLocalFactories.get()).add(paramServiceInterceptorFactory);
  }
  
  public static boolean unregisterForThread(ServiceInterceptorFactory paramServiceInterceptorFactory)
  {
    return ((Set)threadLocalFactories.get()).remove(paramServiceInterceptorFactory);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\client\ServiceInterceptorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */