package com.oracle.util;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;

public class Checksums
{
  private Checksums() {}
  
  public static void update(Adler32 paramAdler32, ByteBuffer paramByteBuffer)
  {
    paramAdler32.update(paramByteBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\util\Checksums.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */