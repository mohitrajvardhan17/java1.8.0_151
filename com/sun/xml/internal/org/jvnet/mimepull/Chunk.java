package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class Chunk
{
  volatile Chunk next;
  volatile Data data;
  
  public Chunk(Data paramData)
  {
    data = paramData;
  }
  
  public Chunk createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer)
  {
    return next = new Chunk(data.createNext(paramDataHead, paramByteBuffer));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\Chunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */