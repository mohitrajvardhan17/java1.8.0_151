package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class FileData
  implements Data
{
  private final DataFile file;
  private final long pointer;
  private final int length;
  
  FileData(DataFile paramDataFile, ByteBuffer paramByteBuffer)
  {
    this(paramDataFile, paramDataFile.writeTo(paramByteBuffer.array(), 0, paramByteBuffer.limit()), paramByteBuffer.limit());
  }
  
  FileData(DataFile paramDataFile, long paramLong, int paramInt)
  {
    file = paramDataFile;
    pointer = paramLong;
    length = paramInt;
  }
  
  public byte[] read()
  {
    byte[] arrayOfByte = new byte[length];
    file.read(pointer, arrayOfByte, 0, length);
    return arrayOfByte;
  }
  
  public long writeTo(DataFile paramDataFile)
  {
    throw new IllegalStateException();
  }
  
  public int size()
  {
    return length;
  }
  
  public Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer)
  {
    return new FileData(file, paramByteBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\FileData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */