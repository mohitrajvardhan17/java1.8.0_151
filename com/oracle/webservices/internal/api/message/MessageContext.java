package com.oracle.webservices.internal.api.message;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public abstract interface MessageContext
  extends DistributedPropertySet
{
  public abstract SOAPMessage getAsSOAPMessage()
    throws SOAPException;
  
  /**
   * @deprecated
   */
  public abstract SOAPMessage getSOAPMessage()
    throws SOAPException;
  
  public abstract ContentType writeTo(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract ContentType getContentType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\message\MessageContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */