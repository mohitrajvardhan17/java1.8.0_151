package com.sun.xml.internal.ws.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoCloseInputStream
  extends FilterInputStream
{
  public NoCloseInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public void close()
    throws IOException
  {}
  
  public void doClose()
    throws IOException
  {
    super.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\NoCloseInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */