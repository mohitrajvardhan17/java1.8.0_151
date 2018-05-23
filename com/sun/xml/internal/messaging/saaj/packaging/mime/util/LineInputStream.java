package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class LineInputStream
  extends FilterInputStream
{
  private char[] lineBuffer = null;
  
  public LineInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public String readLine()
    throws IOException
  {
    Object localObject = in;
    char[] arrayOfChar = lineBuffer;
    if (arrayOfChar == null) {
      arrayOfChar = lineBuffer = new char[''];
    }
    int j = arrayOfChar.length;
    int k = 0;
    int i;
    while (((i = ((InputStream)localObject).read()) != -1) && (i != 10))
    {
      if (i == 13)
      {
        int m = ((InputStream)localObject).read();
        if (m == 13) {
          m = ((InputStream)localObject).read();
        }
        if (m == 10) {
          break;
        }
        if (!(localObject instanceof PushbackInputStream)) {
          localObject = in = new PushbackInputStream((InputStream)localObject);
        }
        ((PushbackInputStream)localObject).unread(m);
        break;
      }
      j--;
      if (j < 0)
      {
        arrayOfChar = new char[k + 128];
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\LineInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */