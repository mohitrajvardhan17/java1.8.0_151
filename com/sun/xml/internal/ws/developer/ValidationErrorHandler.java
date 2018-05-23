package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.api.message.Packet;
import org.xml.sax.ErrorHandler;

public abstract class ValidationErrorHandler
  implements ErrorHandler
{
  protected Packet packet;
  
  public ValidationErrorHandler() {}
  
  public void setPacket(Packet paramPacket)
  {
    packet = paramPacket;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\ValidationErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */