package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;

public abstract interface Attachment
{
  @NotNull
  public abstract String getContentId();
  
  public abstract String getContentType();
  
  public abstract byte[] asByteArray();
  
  public abstract DataHandler asDataHandler();
  
  public abstract Source asSource();
  
  public abstract InputStream asInputStream();
  
  public abstract void writeTo(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\Attachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */