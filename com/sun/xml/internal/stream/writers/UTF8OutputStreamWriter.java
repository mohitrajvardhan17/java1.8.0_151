package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public final class UTF8OutputStreamWriter
  extends Writer
{
  OutputStream out;
  int lastUTF16CodePoint = 0;
  
  public UTF8OutputStreamWriter(OutputStream paramOutputStream)
  {
    out = paramOutputStream;
  }
  
  public String getEncoding()
  {
    return "UTF-8";
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (lastUTF16CodePoint != 0)
    {
      int i = ((lastUTF16CodePoint & 0x3FF) << 10 | paramInt & 0x3FF) + 65536;
      if ((i < 0) || (i >= 2097152)) {
        throw new IOException("Atttempting to write invalid Unicode code point '" + i + "'");
      }
      out.write(0xF0 | i >> 18);
      out.write(0x80 | i >> 12 & 0x3F);
      out.write(0x80 | i >> 6 & 0x3F);
      out.write(0x80 | i & 0x3F);
      lastUTF16CodePoint = 0;
      return;
    }
    if (paramInt < 128)
    {
      out.write(paramInt);
    }
    else if (paramInt < 2048)
    {
      out.write(0xC0 | paramInt >> 6);
      out.write(0x80 | paramInt & 0x3F);
    }
    else if (paramInt <= 65535)
    {
      if ((!XMLChar.isHighSurrogate(paramInt)) && (!XMLChar.isLowSurrogate(paramInt)))
      {
        out.write(0xE0 | paramInt >> 12);
        out.write(0x80 | paramInt >> 6 & 0x3F);
        out.write(0x80 | paramInt & 0x3F);
      }
      else
      {
        lastUTF16CodePoint = paramInt;
      }
    }
  }
  
  public void write(char[] paramArrayOfChar)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfChar.length; i++) {
      write(paramArrayOfChar[i]);
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfChar[(paramInt1 + i)]);
    }
  }
  
  public void write(String paramString)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      write(paramString.charAt(j));
    }
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramString.charAt(paramInt1 + i));
    }
  }
  
  public void flush()
    throws IOException
  {
    out.flush();
  }
  
  public void close()
    throws IOException
  {
    if (lastUTF16CodePoint != 0) {
      throw new IllegalStateException("Attempting to close a UTF8OutputStreamWriter while awaiting for a UTF-16 code unit");
    }
    out.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\writers\UTF8OutputStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */