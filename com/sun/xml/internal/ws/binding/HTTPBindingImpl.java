package com.sun.xml.internal.ws.binding;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.http.HTTPBinding;

public class HTTPBindingImpl
  extends BindingImpl
  implements HTTPBinding
{
  HTTPBindingImpl()
  {
    this(EMPTY_FEATURES);
  }
  
  HTTPBindingImpl(WebServiceFeature... paramVarArgs)
  {
    super(BindingID.XML_HTTP, paramVarArgs);
  }
  
  public void setHandlerChain(List<Handler> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Handler localHandler = (Handler)localIterator.next();
      if (!(localHandler instanceof LogicalHandler)) {
        throw new WebServiceException(ClientMessages.NON_LOGICAL_HANDLER_SET(localHandler.getClass()));
      }
    }
    setHandlerConfig(new HandlerConfiguration(Collections.emptySet(), paramList));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\binding\HTTPBindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */