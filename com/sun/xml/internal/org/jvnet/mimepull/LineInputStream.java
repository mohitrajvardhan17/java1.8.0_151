package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

final class LineInputStream
  extends FilterInputStream
{
  private char[] lineBuffer = null;
  private static int MAX_INCR = 1048576;
  
  public LineInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public String readLine()
    throws IOException
  {
    char[] arrayOfChar = lineBuffer;
    if (arrayOfChar == null) {
      arrayOfChar = lineBuffer = new char[''];
    }
    int j = arrayOfChar.length;
    int k = 0;
    int i;
    while (((i = in.read()) != -1) && (i != 10))
    {
      if (i == 13)
      {
        int m = 0;
        if (in.markSupported()) {
          in.mark(2);
        }
        int n = in.read();
        if (n == 13)
        {
          m = 1;
          n = in.read();
        }
        if (n == 10) {
          break;
        }
        if (in.markSupported())
        {
          in.reset();
          break;
        }
        if (!(in instanceof PushbackInputStream)) {
          in = new PushbackInputStream(in, 2);
        }
        if (n != -1) {
          ((PushbackInputStream)in).unread(n);
        }
        if (m == 0) {
          break;
        }
        ((PushbackInputStream)in).unread(13);
        break;
      }
      j--;
      if (j < 0)
      {
        if (arrayOfChar.length < MAX_INCR) {
          arrayOfChar = new char[arrayOfChar.length * 2];
        } else {
          arrayOfChar = new char[arrayOfChar.length + MAX_INCR];
        }
        j = arrayOfChar.length - k - 1;
        System.arraycopy(lineBuffer, 0, arrayOfChar, 0, k);
        lineBuffer = arrayOfChar;
      }
      arrayOfChar[(k++)] = ((char)i);
    }
    if ((i == -1) && (k == 0)) {
      return null;
    }
    return String.copyValueOf(arrayOfChar, 0, k);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\LineInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */