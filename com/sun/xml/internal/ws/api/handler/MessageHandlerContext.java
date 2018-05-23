package com.sun.xml.internal.ws.api.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

public abstract interface MessageHandlerContext
  extends MessageContext
{
  public abstract Message getMessage();
  
  public abstract void setMessage(Message paramMessage);
  
  public abstract Set<String> getRoles();
  
  public abstract WSBinding getWSBinding();
  
  @Nullable
  public abstract SEIModel getSEIModel();
  
  @Nullable
  public abstract WSDLPort getPort();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\handler\MessageHandlerContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */