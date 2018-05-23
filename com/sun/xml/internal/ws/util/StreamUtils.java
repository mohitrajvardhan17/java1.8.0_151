package com.sun.xml.internal.ws.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils
{
  public StreamUtils() {}
  
  public static InputStream hasSomeData(InputStream paramInputStream)
  {
    if (paramInputStream != null) {
      try
      {
        if (paramInputStream.available() < 1)
        {
          if (!paramInputStream.markSupported()) {
            paramInputStream = new BufferedInputStream(paramInputStream);
          }
          paramInputStream.mark(1);
          if (paramInputStream.read() != -1) {
            paramInputStream.reset();
          } else {
            paramInputStream = null;
          }
        }
      }
      catch (IOException localIOException)
      {
        paramInputStream = null;
      }
    }
    return paramInputStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\StreamUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */