package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

class AsciiOutputStream
  extends OutputStream
{
  private boolean breakOnNonAscii;
  private int ascii = 0;
  private int non_ascii = 0;
  private int linelen = 0;
  private boolean longLine = false;
  private boolean badEOL = false;
  private boolean checkEOL = false;
  private int lastb = 0;
  private int ret = 0;
  
  public AsciiOutputStream(boolean paramBoolean1, boolean paramBoolean2)
  {
    breakOnNonAscii = paramBoolean1;
    checkEOL = ((paramBoolean2) && (paramBoolean1));
  }
  
  public void write(int paramInt)
    throws IOException
  {
    check(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 += paramInt1;
    for (int i = paramInt1; i < paramInt2; i++) {
      check(paramArrayOfByte[i]);
    }
  }
  
  private final void check(int paramInt)
    throws IOException
  {
    paramInt &= 0xFF;
    if ((checkEOL) && (((lastb == 13) && (paramInt != 10)) || ((lastb != 13) && (paramInt == 10)))) {
      badEOL = true;
    }
    if ((paramInt == 13) || (paramInt == 10))
    {
      linelen = 0;
    }
    else
    {
      linelen += 1;
      if (linelen > 998) {
        longLine = true;
      }
    }
    if (MimeUtility.nonascii(paramInt))
    {
      non_ascii += 1;
      if (breakOnNonAscii)
      {
        ret = 3;
        throw new EOFException();
      }
    }
    else
    {
      ascii += 1;
    }
    lastb = paramInt;
  }
  
  public int getAscii()
  {
    if (ret != 0) {
      return ret;
    }
    if (badEOL) {
      return 3;
    }
    if (non_ascii == 0)
    {
      if (longLine) {
        return 2;
      }
      return 1;
    }
    if (ascii > non_ascii) {
      return 2;
    }
    return 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\AsciiOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */