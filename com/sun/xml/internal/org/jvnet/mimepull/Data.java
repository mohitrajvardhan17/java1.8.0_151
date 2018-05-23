package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

abstract interface Data
{
  public abstract int size();
  
  public abstract byte[] read();
  
  public abstract long writeTo(DataFile paramDataFile);
  
  public abstract Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */