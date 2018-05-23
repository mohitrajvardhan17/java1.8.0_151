package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

abstract class HandlerProcessor<C extends MessageUpdatableContext>
{
  boolean isClient;
  static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.handler");
  private List<? extends Handler> handlers;
  WSBinding binding;
  private int index = -1;
  private HandlerTube owner;
  
  protected HandlerProcessor(HandlerTube paramHandlerTube, WSBinding paramWSBinding, List<? extends Handler> paramList)
  {
    owner = paramHandlerTube;
    if (paramList == null) {
      paramList = new ArrayList();
    }
    handlers = paramList;
    binding = paramWSBinding;
  }
  
  int getIndex()
  {
    return index;
  }
  
  void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public boolean callHandlersRequest(Direction paramDirection, C paramC, boolean paramBoolean)
  {
    setDirection(paramDirection, paramC);
    boolean bool;
    try
    {
      if (paramDirection == Direction.OUTBOUND) {
        bool = callHandleMessage(paramC, 0, handlers.size() - 1);
      } else {
        bool = callHandleMessage(paramC, handlers.size() - 1, 0);
      }
    }
    catch (ProtocolException localProtocolException)
    {
      logger.log(Level.FINER, "exception in handler chain", localProtocolException);
      if (paramBoolean)
      {
        insertFaultMessage(paramC, localProtocolException);
        reverseDirection(paramDirection, paramC);
        setHandleFaultProperty();
        if (paramDirection == Direction.OUTBOUND) {
          callHandleFault(paramC, getIndex() - 1, 0);
        } else {
          callHandleFault(paramC, getIndex() + 1, handlers.size() - 1);
        }
        return false;
      }
      throw localProtocolException;
    }
    catch (RuntimeException localRuntimeException)
    {
      logger.log(Level.FINER, "exception in handler chain", localRuntimeException);
      throw localRuntimeException;
    }
    if (!bool)
    {
      if (paramBoolean)
      {
        reverseDirection(paramDirection, paramC);
        if (paramDirection == Direction.OUTBOUND) {
          callHandleMessageReverse(paramC, getIndex() - 1, 0);
        } else {
          callHandleMessageReverse(paramC, getIndex() + 1, handlers.size() - 1);
        }
      }
      else
      {
        setHandleFalseProperty();
      }
      return false;
    }
    return bool;
  }
  
  public void callHandlersResponse(Direction paramDirection, C paramC, boolean paramBoolean)
  {
    setDirection(paramDirection, paramC);
    try
    {
      if (paramBoolean)
      {
        if (paramDirection == Direction.OUTBOUND) {
          callHandleFault(paramC, 0, handlers.size() - 1);
        } else {
          callHandleFault(paramC, handlers.size() - 1, 0);
        }
      }
      else if (paramDirection == Direction.OUTBOUND) {
        callHandleMessageReverse(paramC, 0, handlers.size() - 1);
      } else {
        callHandleMessageReverse(paramC, handlers.size() - 1, 0);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      logger.log(Level.FINER, "exception in handler chain", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  private void reverseDirection(Direction paramDirection, C paramC)
  {
    if (paramDirection == Direction.OUTBOUND) {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(false));
    } else {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(true));
    }
  }
  
  private void setDirection(Direction paramDirection, C paramC)
  {
    if (paramDirection == Direction.OUTBOUND) {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(true));
    } else {
      paramC.put("javax.xml.ws.handler.message.outbound", Boolean.valueOf(false));
    }
  }
  
  private void setHandleFaultProperty()
  {
    owner.setHandleFault();
  }
  
  private void setHandleFalseProperty()
  {
    owner.setHandleFalse();
  }
  
  abstract void insertFaultMessage(C paramC, ProtocolException paramProtocolException);
  
  private boolean callHandleMessage(C paramC, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    try
    {
      if (paramInt1 > paramInt2) {
        while (i >= paramInt2)
        {
          if (!((Handler)handlers.get(i)).handleMessage(paramC))
          {
            setIndex(i);
            return false;
          }
          i--;
        }
      }
      while (i <= paramInt2)
      {
        if (!((Handler)handlers.get(i)).handleMessage(paramC))
        {
          setIndex(i);
          return false;
        }
        i++;
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      setIndex(i);
      throw localRuntimeException;
    }
    return true;
  }
  
  private boolean callHandleMessageReverse(C paramC, int paramInt1, int paramInt2)
  {
    if ((handlers.isEmpty()) || (paramInt1 == -1) || (paramInt1 == handlers.size())) {
      return false;
    }
    int i = paramInt1;
    if (paramInt1 > paramInt2) {
      while (i >= paramInt2)
      {
        if (!((Handler)handlers.get(i)).handleMessage(paramC))
        {
          setHandleFalseProperty();
          return false;
        }
        i--;
      }
    }
    while (i <= paramInt2)
    {
      if (!((Handler)handlers.get(i)).handleMessage(paramC))
      {
        setHandleFalseProperty();
        return false;
      }
      i++;
    }
    return true;
  }
  
  private boolean callHandleFault(C paramC, int paramInt1, int paramInt2)
  {
    if ((handlers.isEmpty()) || (paramInt1 == -1) || (paramInt1 == handlers.size())) {
      return false;
    }
    int i = paramInt1;
    if (paramInt1 > paramInt2) {
      try
      {
        while (i >= paramInt2)
        {
          if (!((Handler)handlers.get(i)).handleFault(paramC)) {
            return false;
          }
          i--;
        }
      }
      catch (RuntimeException localRuntimeException1)
      {
        logger.log(Level.FINER, "exception in handler chain", localRuntimeException1);
        throw localRuntimeException1;
      }
    }
    try
    {
      while (i <= paramInt2)
      {
        if (!((Handler)handlers.get(i)).handleFault(paramC)) {
          return false;
        }
        i++;
      }
    }
    catch (RuntimeException localRuntimeException2)
    {
      logger.log(Level.FINER, "exception in handler chain", localRuntimeException2);
      throw localRuntimeException2;
    }
    return true;
  }
  
  void closeHandlers(MessageContext paramMessageContext, int paramInt1, int paramInt2)
  {
    if ((handlers.isEmpty()) || (paramInt1 == -1)) {
      return;
    }
    int i;
    if (paramInt1 > paramInt2) {
      for (i = paramInt1; i >= paramInt2; i--) {
        try
        {
          ((Handler)handlers.get(i)).close(paramMessageContext);
        }
        catch (RuntimeException localRuntimeException1)
        {
          logger.log(Level.INFO, "Exception ignored during close", localRuntimeException1);
        }
      }
    } else {
      for (i = paramInt1; i <= paramInt2; i++) {
        try
        {
          ((Handler)handlers.get(i)).close(paramMessageContext);
        }
        catch (RuntimeException localRuntimeException2)
        {
          logger.log(Level.INFO, "Exception ignored during close", localRuntimeException2);
        }
      }
    }
  }
  
  public static enum Direction
  {
    OUTBOUND,  INBOUND;
    
    private Direction() {}
  }
  
  public static enum RequestOrResponse
  {
    REQUEST,  RESPONSE;
    
    private RequestOrResponse() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\HandlerProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */