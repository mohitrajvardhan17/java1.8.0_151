package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.xml.sax.ContentHandler;

public abstract interface Serializer
{
  public abstract void setOutputStream(OutputStream paramOutputStream);
  
  public abstract OutputStream getOutputStream();
  
  public abstract void setWriter(Writer paramWriter);
  
  public abstract Writer getWriter();
  
  public abstract void setOutputFormat(Properties paramProperties);
  
  public abstract Properties getOutputFormat();
  
  public abstract ContentHandler asContentHandler()
    throws IOException;
  
  public abstract DOMSerializer asDOMSerializer()
    throws IOException;
  
  public abstract boolean reset();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\Serializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */