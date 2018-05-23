package com.sun.xml.internal.org.jvnet.staxex;

import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract interface XMLStreamWriterEx
  extends XMLStreamWriter
{
  public abstract void writeBinary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
    throws XMLStreamException;
  
  public abstract void writeBinary(DataHandler paramDataHandler)
    throws XMLStreamException;
  
  public abstract OutputStream writeBinary(String paramString)
    throws XMLStreamException;
  
  public abstract void writePCDATA(CharSequence paramCharSequence)
    throws XMLStreamException;
  
  public abstract NamespaceContextEx getNamespaceContext();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\XMLStreamWriterEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */