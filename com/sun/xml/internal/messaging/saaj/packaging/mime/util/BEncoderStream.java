package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.OutputStream;

public class BEncoderStream
  extends BASE64EncoderStream
{
  public BEncoderStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream, Integer.MAX_VALUE);
  }
  
  public static int encodedLength(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte.length + 2) / 3 * 4;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\BEncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */