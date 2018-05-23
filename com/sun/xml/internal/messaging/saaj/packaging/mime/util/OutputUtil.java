package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract class OutputUtil
{
  private static byte[] newline = { 13, 10 };
  
  public OutputUtil() {}
  
  public static void writeln(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    writeAsAscii(paramString, paramOutputStream);
    writeln(paramOutputStream);
  }
  
  public static void writeAsAscii(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      paramOutputStream.write((byte)paramString.charAt(j));
    }
  }
  
  public static void writeln(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(newline);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\OutputUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */