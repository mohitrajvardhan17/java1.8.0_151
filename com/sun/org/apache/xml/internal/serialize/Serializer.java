package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;

public abstract interface Serializer
{
  public abstract void setOutputByteStream(OutputStream paramOutputStream);
  
  public abstract void setOutputCharStream(Writer paramWriter);
  
  public abstract void setOutputFormat(OutputFormat paramOutputFormat);
  
  public abstract DocumentHandler asDocumentHandler()
    throws IOException;
  
  public abstract ContentHandler asContentHandler()
    throws IOException;
  
  public abstract DOMSerializer asDOMSerializer()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serialize\Serializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */