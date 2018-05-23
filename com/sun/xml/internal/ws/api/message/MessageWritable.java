package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.message.ContentType;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.ws.soap.MTOMFeature;

public abstract interface MessageWritable
{
  public abstract ContentType getContentType();
  
  public abstract ContentType writeTo(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract void setMTOMConfiguration(MTOMFeature paramMTOMFeature);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\MessageWritable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */