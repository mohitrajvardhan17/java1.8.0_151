package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;

final class DataFile
{
  private WeakDataFile weak;
  private long writePointer = 0L;
  
  DataFile(File paramFile)
  {
    weak = new WeakDataFile(this, paramFile);
  }
  
  void close()
  {
    weak.close();
  }
  
  synchronized void read(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    weak.read(paramLong, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  void renameTo(File paramFile)
  {
    weak.renameTo(paramFile);
  }
  
  synchronized long writeTo(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    long l = writePointer;
    writePointer = weak.writeTo(writePointer, paramArrayOfByte, paramInt1, paramInt2);
    return l;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\DataFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */