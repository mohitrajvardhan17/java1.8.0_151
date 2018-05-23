package com.sun.org.apache.bcel.internal.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class ByteSequence
  extends DataInputStream
{
  private ByteArrayStream byte_stream = (ByteArrayStream)in;
  
  public ByteSequence(byte[] paramArrayOfByte)
  {
    super(new ByteArrayStream(paramArrayOfByte));
  }
  
  public final int getIndex()
  {
    return byte_stream.getPosition();
  }
  
  final void unreadByte()
  {
    byte_stream.unreadByte();
  }
  
  private static final class ByteArrayStream
    extends ByteArrayInputStream
  {
    ByteArrayStream(byte[] paramArrayOfByte)
    {
      super();
    }
    
    final int getPosition()
    {
      return pos;
    }
    
    final void unreadByte()
    {
      if (pos > 0) {
        pos -= 1;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ByteSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */