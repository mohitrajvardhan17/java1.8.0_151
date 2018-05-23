package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.Invoker;
import org.xml.sax.EntityResolver;

public class EndpointCreationAttributes
{
  private final boolean processHandlerAnnotation;
  private final Invoker invoker;
  private final EntityResolver entityResolver;
  private final boolean isTransportSynchronous;
  
  public EndpointCreationAttributes(boolean paramBoolean1, Invoker paramInvoker, EntityResolver paramEntityResolver, boolean paramBoolean2)
  {
    processHandlerAnnotation = paramBoolean1;
    invoker = paramInvoker;
    entityResolver = paramEntityResolver;
    isTransportSynchronous = paramBoolean2;
  }
  
  public boolean isProcessHandlerAnnotation()
  {
    return processHandlerAnnotation;
  }
  
  public Invoker getInvoker()
  {
    return invoker;
  }
  
  public EntityResolver getEntityResolver()
  {
    return entityResolver;
  }
  
  public boolean isTransportSynchronous()
  {
    return isTransportSynchronous;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\config\management\EndpointCreationAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */