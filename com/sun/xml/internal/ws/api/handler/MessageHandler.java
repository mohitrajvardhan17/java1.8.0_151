package com.sun.xml.internal.ws.api.handler;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public abstract interface MessageHandler<C extends MessageHandlerContext>
  extends Handler<C>
{
  public abstract Set<QName> getHeaders();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\handler\MessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */