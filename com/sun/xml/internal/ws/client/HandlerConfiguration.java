package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.handler.HandlerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.soap.SOAPHandler;

public class HandlerConfiguration
{
  private final Set<String> roles;
  private final List<Handler> handlerChain;
  private final List<LogicalHandler> logicalHandlers;
  private final List<SOAPHandler> soapHandlers;
  private final List<MessageHandler> messageHandlers;
  private final Set<QName> handlerKnownHeaders;
  
  public HandlerConfiguration(Set<String> paramSet, List<Handler> paramList)
  {
    roles = paramSet;
    handlerChain = paramList;
    logicalHandlers = new ArrayList();
    soapHandlers = new ArrayList();
    messageHandlers = new ArrayList();
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Handler localHandler = (Handler)localIterator.next();
      if ((localHandler instanceof LogicalHandler))
      {
        logicalHandlers.add((LogicalHandler)localHandler);
      }
      else
      {
        Set localSet;
        if ((localHandler instanceof SOAPHandler))
        {
          soapHandlers.add((SOAPHandler)localHandler);
          localSet = ((SOAPHandler)localHandler).getHeaders();
          if (localSet != null) {
            localHashSet.addAll(localSet);
          }
        }
        else if ((localHandler instanceof MessageHandler))
        {
          messageHandlers.add((MessageHandler)localHandler);
          localSet = ((MessageHandler)localHandler).getHeaders();
          if (localSet != null) {
            localHashSet.addAll(localSet);
          }
        }
        else
        {
          throw new HandlerException("handler.not.valid.type", new Object[] { localHandler.getClass() });
        }
      }
    }
    handlerKnownHeaders = Collections.unmodifiableSet(localHashSet);
  }
  
  public HandlerConfiguration(Set<String> paramSet, HandlerConfiguration paramHandlerConfiguration)
  {
    roles = paramSet;
    handlerChain = handlerChain;
    logicalHandlers = logicalHandlers;
    soapHandlers = soapHandlers;
    messageHandlers = messageHandlers;
    handlerKnownHeaders = handlerKnownHeaders;
  }
  
  public Set<String> getRoles()
  {
    return roles;
  }
  
  public List<Handler> getHandlerChain()
  {
    if (handlerChain == null) {
      return Collections.emptyList();
    }
    return new ArrayList(handlerChain);
  }
  
  public List<LogicalHandler> getLogicalHandlers()
  {
    return logicalHandlers;
  }
  
  public List<SOAPHandler> getSoapHandlers()
  {
    return soapHandlers;
  }
  
  public List<MessageHandler> getMessageHandlers()
  {
    return messageHandlers;
  }
  
  public Set<QName> getHandlerKnownHeaders()
  {
    return handlerKnownHeaders;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\HandlerConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */