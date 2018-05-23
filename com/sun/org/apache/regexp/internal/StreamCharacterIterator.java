package com.sun.org.apache.regexp.internal;

import java.io.IOException;
import java.io.InputStream;

public final class StreamCharacterIterator
  implements CharacterIterator
{
  private final InputStream is;
  private final StringBuffer buff;
  private boolean closed;
  
  public StreamCharacterIterator(InputStream paramInputStream)
  {
    is = paramInputStream;
    buff = new StringBuffer(512);
    closed = false;
  }
  
  public String substring(int paramInt1, int paramInt2)
  {
    try
    {
      ensure(paramInt2);
      return buff.toString().substring(paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  public String substring(int paramInt)
  {
    try
    {
      readAll();
      return buff.toString().substring(paramInt);
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  public char charAt(int paramInt)
  {
    try
    {
      ensure(paramInt);
      return buff.charAt(paramInt);
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  public boolean isEnd(int paramInt)
  {
    if (buff.length() > paramInt) {
      return false;
    }
    try
    {
      ensure(paramInt);
      return buff.length() <= paramInt;
    }
    catch (IOException localIOException)
    {
      throw new StringIndexOutOfBoundsException(localIOException.getMessage());
    }
  }
  
  private int read(int paramInt)
    throws IOException
  {
    if (closed) {
      return 0;
    }
    int j = paramInt;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      int i = is.read();
      if (i < 0)
      {
        closed = true;
        break;
      }
      buff.append((char)i);
    }
    return paramInt - j;
  }
  
  private void readAll()
    throws IOException
  {
    while (!closed) {
      read(1000);
    }
  }
  
  private void ensure(int paramInt)
    throws IOException
  {
    if (closed) {
      return;
    }
    if (paramInt < buff.length()) {
      return;
    }
    read(paramInt + 1 - buff.length());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\StreamCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */