package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.soap.SOAPBinding;

abstract class HandlerConfigurator
{
  HandlerConfigurator() {}
  
  abstract void configureHandlers(@NotNull WSPortInfo paramWSPortInfo, @NotNull BindingImpl paramBindingImpl);
  
  abstract HandlerResolver getResolver();
  
  static final class AnnotationConfigurator
    extends HandlerConfigurator
  {
    private final HandlerChainsModel handlerModel;
    private final Map<WSPortInfo, HandlerAnnotationInfo> chainMap = new HashMap();
    private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
    
    AnnotationConfigurator(WSServiceDelegate paramWSServiceDelegate)
    {
      handlerModel = HandlerAnnotationProcessor.buildHandlerChainsModel(paramWSServiceDelegate.getServiceClass());
      assert (handlerModel != null);
    }
    
    void configureHandlers(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl)
    {
      HandlerAnnotationInfo localHandlerAnnotationInfo = (HandlerAnnotationInfo)chainMap.get(paramWSPortInfo);
      if (localHandlerAnnotationInfo == null)
      {
        logGetChain(paramWSPortInfo);
        localHandlerAnnotationInfo = handlerModel.getHandlersForPortInfo(paramWSPortInfo);
        chainMap.put(paramWSPortInfo, localHandlerAnnotationInfo);
      }
      if ((paramBindingImpl instanceof SOAPBinding)) {
        ((SOAPBinding)paramBindingImpl).setRoles(localHandlerAnnotationInfo.getRoles());
      }
      logSetChain(paramWSPortInfo, localHandlerAnnotationInfo);
      paramBindingImpl.setHandlerChain(localHandlerAnnotationInfo.getHandlers());
    }
    
    HandlerResolver getResolver()
    {
      new HandlerResolver()
      {
        public List<Handler> getHandlerChain(PortInfo paramAnonymousPortInfo)
        {
          return new ArrayList(handlerModel.getHandlersForPortInfo(paramAnonymousPortInfo).getHandlers());
        }
      };
    }
    
    private void logSetChain(WSPortInfo paramWSPortInfo, HandlerAnnotationInfo paramHandlerAnnotationInfo)
    {
      logger.finer("Setting chain of length " + paramHandlerAnnotationInfo.getHandlers().size() + " for port info");
      logPortInfo(paramWSPortInfo, Level.FINER);
    }
    
    private void logGetChain(WSPortInfo paramWSPortInfo)
    {
      logger.fine("No handler chain found for port info:");
      logPortInfo(paramWSPortInfo, Level.FINE);
      logger.fine("Existing handler chains:");
      if (chainMap.isEmpty())
      {
        logger.fine("none");
      }
      else
      {
        Iterator localIterator = chainMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          WSPortInfo localWSPortInfo = (WSPortInfo)localIterator.next();
          logger.fine(((HandlerAnnotationInfo)chainMap.get(localWSPortInfo)).getHandlers().size() + " handlers for port info ");
          logPortInfo(localWSPortInfo, Level.FINE);
        }
      }
    }
    
    private void logPortInfo(WSPortInfo paramWSPortInfo, Level paramLevel)
    {
      logger.log(paramLevel, "binding: " + paramWSPortInfo.getBindingID() + "\nservice: " + paramWSPortInfo.getServiceName() + "\nport: " + paramWSPortInfo.getPortName());
    }
  }
  
  static final class HandlerResolverImpl
    extends HandlerConfigurator
  {
    @Nullable
    private final HandlerResolver resolver;
    
    public HandlerResolverImpl(HandlerResolver paramHandlerResolver)
    {
      resolver = paramHandlerResolver;
    }
    
    void configureHandlers(@NotNull WSPortInfo paramWSPortInfo, @NotNull BindingImpl paramBindingImpl)
    {
      if (resolver != null) {
        paramBindingImpl.setHandlerChain(resolver.getHandlerChain(paramWSPortInfo));
      }
    }
    
    HandlerResolver getResolver()
    {
      return resolver;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\HandlerConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */