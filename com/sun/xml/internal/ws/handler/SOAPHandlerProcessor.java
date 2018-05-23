package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;

final class SOAPHandlerProcessor<C extends MessageUpdatableContext>
  extends HandlerProcessor<C>
{
  public SOAPHandlerProcessor(boolean paramBoolean, HandlerTube paramHandlerTube, WSBinding paramWSBinding, List<? extends Handler> paramList)
  {
    super(paramHandlerTube, paramWSBinding, paramList);
    isClient = paramBoolean;
  }
  
  final void insertFaultMessage(C paramC, ProtocolException paramProtocolException)
  {
    try
    {
      if (!paramC.getPacketMessage().isFault())
      {
        Message localMessage = Messages.create(binding.getSOAPVersion(), paramProtocolException, determineFaultCode(binding.getSOAPVersion()));
        paramC.setPacketMessage(localMessage);
      }
    }
    catch (Exception localException)
    {
      logger.log(Level.SEVERE, "exception while creating fault message in handler chain", localException);
      throw new RuntimeException(localException);
    }
  }
  
  private QName determineFaultCode(SOAPVersion paramSOAPVersion)
  {
    return isClient ? faultCodeClient : faultCodeServer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\SOAPHandlerProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */