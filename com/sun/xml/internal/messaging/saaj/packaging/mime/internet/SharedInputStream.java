package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.io.InputStream;
import java.io.OutputStream;

public abstract interface SharedInputStream
{
  public abstract long getPosition();
  
  public abstract InputStream newStream(long paramLong1, long paramLong2);
  
  public abstract void writeTo(long paramLong1, long paramLong2, OutputStream paramOutputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\SharedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */