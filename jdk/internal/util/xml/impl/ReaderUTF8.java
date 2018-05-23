package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ReaderUTF8
  extends Reader
{
  private InputStream is;
  
  public ReaderUTF8(InputStream paramInputStream)
  {
    is = paramInputStream;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++)
    {
      int j;
      if ((j = is.read()) < 0) {
        return i != 0 ? i : -1;
      }
      switch (j & 0xF0)
      {
      case 192: 
      case 208: 
        paramArrayOfChar[(paramInt1++)] = ((char)((j & 0x1F) << 6 | is.read() & 0x3F));
        break;
      case 224: 
        paramArrayOfChar[(paramInt1++)] = ((char)((j & 0xF) << 12 | (is.read() & 0x3F) << 6 | is.read() & 0x3F));
        break;
      case 240: 
        throw new UnsupportedEncodingException("UTF-32 (or UCS-4) encoding not supported.");
      default: 
        paramArrayOfChar[(paramInt1++)] = ((char)j);
      }
    }
    return i;
  }
  
  public int read()
    throws IOException
  {
    int i;
    if ((i = is.read()) < 0) {
      return -1;
    }
    switch (i & 0xF0)
    {
    case 192: 
    case 208: 
      i = (i & 0x1F) << 6 | is.read() & 0x3F;
      break;
    case 224: 
      i = (i & 0xF) << 12 | (is.read() & 0x3F) << 6 | is.read() & 0x3F;
      break;
    case 240: 
      throw new UnsupportedEncodingException();
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    is.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\ReaderUTF8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */