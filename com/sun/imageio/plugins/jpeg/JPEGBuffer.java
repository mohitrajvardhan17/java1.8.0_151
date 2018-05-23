package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

class JPEGBuffer
{
  private boolean debug = false;
  final int BUFFER_SIZE = 4096;
  byte[] buf = new byte['က'];
  int bufAvail = 0;
  int bufPtr = 0;
  ImageInputStream iis;
  
  JPEGBuffer(ImageInputStream paramImageInputStream)
  {
    iis = paramImageInputStream;
  }
  
  void loadBuf(int paramInt)
    throws IOException
  {
    if (debug)
    {
      System.out.print("loadbuf called with ");
      System.out.print("count " + paramInt + ", ");
      System.out.println("bufAvail " + bufAvail + ", ");
    }
    if (paramInt != 0)
    {
      if (bufAvail < paramInt) {}
    }
    else if (bufAvail == 4096) {
      return;
    }
    if ((bufAvail > 0) && (bufAvail < 4096)) {
      System.arraycopy(buf, bufPtr, buf, 0, bufAvail);
    }
    int i = iis.read(buf, bufAvail, buf.length - bufAvail);
    if (debug) {
      System.out.println("iis.read returned " + i);
    }
    if (i != -1) {
      bufAvail += i;
    }
    bufPtr = 0;
    int j = Math.min(4096, paramInt);
    if (bufAvail < j) {
      throw new IIOException("Image Format Error");
    }
  }
  
  void readData(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if (bufAvail >= i)
    {
      System.arraycopy(buf, bufPtr, paramArrayOfByte, 0, i);
      bufAvail -= i;
      bufPtr += i;
      return;
    }
    int j = 0;
    if (bufAvail > 0)
    {
      System.arraycopy(buf, bufPtr, paramArrayOfByte, 0, bufAvail);
      j = bufAvail;
      i -= bufAvail;
      bufAvail = 0;
      bufPtr = 0;
    }
    if (iis.read(paramArrayOfByte, j, i) != i) {
      throw new IIOException("Image format Error");
    }
  }
  
  void skipData(int paramInt)
    throws IOException
  {
    if (bufAvail >= paramInt)
    {
      bufAvail -= paramInt;
      bufPtr += paramInt;
      return;
    }
    if (bufAvail > 0)
    {
      paramInt -= bufAvail;
      bufAvail = 0;
      bufPtr = 0;
    }
    if (iis.skipBytes(paramInt) != paramInt) {
      throw new IIOException("Image format Error");
    }
  }
  
  void pushBack()
    throws IOException
  {
    iis.seek(iis.getStreamPosition() - bufAvail);
    bufAvail = 0;
    bufPtr = 0;
  }
  
  long getStreamPosition()
    throws IOException
  {
    return iis.getStreamPosition() - bufAvail;
  }
  
  boolean scanForFF(JPEGImageReader paramJPEGImageReader)
    throws IOException
  {
    boolean bool = false;
    int i = 0;
    while (i == 0)
    {
      while (bufAvail > 0)
      {
        if ((buf[(bufPtr++)] & 0xFF) == 255)
        {
          bufAvail -= 1;
          i = 1;
          break;
        }
        bufAvail -= 1;
      }
      loadBuf(0);
      if (i == 1) {
        while ((bufAvail > 0) && ((buf[bufPtr] & 0xFF) == 255))
        {
          bufPtr += 1;
          bufAvail -= 1;
        }
      }
      if (bufAvail == 0)
      {
        bool = true;
        buf[0] = -39;
        bufAvail = 1;
        bufPtr = 0;
        i = 1;
      }
    }
    return bool;
  }
  
  void print(int paramInt)
  {
    System.out.print("buffer has ");
    System.out.print(bufAvail);
    System.out.println(" bytes available");
    if (bufAvail < paramInt) {
      paramInt = bufAvail;
    }
    int i = bufPtr;
    while (paramInt > 0)
    {
      int j = buf[(i++)] & 0xFF;
      System.out.print(" " + Integer.toHexString(j));
      paramInt--;
    }
    System.out.println();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */