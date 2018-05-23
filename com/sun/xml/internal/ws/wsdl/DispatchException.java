package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Message;

public final class DispatchException
  extends Exception
{
  public final Message fault;
  
  public DispatchException(Message paramMessage)
  {
    fault = paramMessage;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\DispatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */