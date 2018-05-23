package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.util.InjectionPlan;
import javax.xml.ws.WebServiceContext;

public final class DefaultResourceInjector
  extends ResourceInjector
{
  public DefaultResourceInjector() {}
  
  public void inject(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull Object paramObject)
  {
    InjectionPlan.buildInjectionPlan(paramObject.getClass(), WebServiceContext.class, false).inject(paramObject, paramWSWebServiceContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\DefaultResourceInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */