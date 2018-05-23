package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.InputStream;

public class QDecoderStream
  extends QPDecoderStream
{
  public QDecoderStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public int read()
    throws IOException
  {
    int i = in.read();
    if (i == 95) {
      return 32;
    }
    if (i == 61)
    {
      ba[0] = ((byte)in.read());
      ba[1] = ((byte)in.read());
      try
      {
        return ASCIIUtility.parseInt(ba, 0, 2, 16);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IOException("Error in QP stream " + localNumberFormatException.getMessage());
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\QDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */