package com.sun.xml.internal.messaging.saaj.soap;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.transform.Source;

public abstract interface Envelope
  extends SOAPEnvelope
{
  public abstract Source getContent();
  
  public abstract void output(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract void output(OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\Envelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */