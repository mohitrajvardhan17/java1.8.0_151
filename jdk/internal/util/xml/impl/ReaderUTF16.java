package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF16
  extends Reader
{
  private InputStream is;
  private char bo;
  
  public ReaderUTF16(InputStream paramInputStream, char paramChar)
  {
    switch (paramChar)
    {
    case 'l': 
      break;
    case 'b': 
      break;
    default: 
      throw new IllegalArgumentException("");
    }
    bo = paramChar;
    is = paramInputStream;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j;
    if (bo == 'b') {
      while (i < paramInt2)
      {
        if ((j = is.read()) < 0) {
          return i != 0 ? i : -1;
        }
        paramArrayOfChar[(paramInt1++)] = ((char)(j << 8 | is.read() & 0xFF));
        i++;
      }
    }
    while (i < paramInt2)
    {
      if ((j = is.read()) < 0) {
        return i != 0 ? i : -1;
      }
      paramArrayOfChar[(paramInt1++)] = ((char)(is.read() << 8 | j & 0xFF));
      i++;
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
    if (bo == 'b') {
      i = (char)(i << 8 | is.read() & 0xFF);
    } else {
      i = (char)(is.read() << 8 | i & 0xFF);
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    is.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\ReaderUTF16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */