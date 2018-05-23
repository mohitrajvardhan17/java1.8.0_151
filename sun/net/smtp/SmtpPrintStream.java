package sun.net.smtp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

class SmtpPrintStream
  extends PrintStream
{
  private SmtpClient target;
  private int lastc = 10;
  
  SmtpPrintStream(OutputStream paramOutputStream, SmtpClient paramSmtpClient)
    throws UnsupportedEncodingException
  {
    super(paramOutputStream, false, paramSmtpClient.getEncoding());
    target = paramSmtpClient;
  }
  
  public void close()
  {
    if (target == null) {
      return;
    }
    if (lastc != 10) {
      write(10);
    }
    try
    {
      target.issueCommand(".\r\n", 250);
      target.message = null;
      out = null;
      target = null;
    }
    catch (IOException localIOException) {}
  }
  
  public void write(int paramInt)
  {
    try
    {
      if ((lastc == 10) && (paramInt == 46)) {
        out.write(46);
      }
      if ((paramInt == 10) && (lastc != 13)) {
        out.write(13);
      }
      out.write(paramInt);
      lastc = paramInt;
    }
    catch (IOException localIOException) {}
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      int j;
      for (int i = lastc;; i = j)
      {
        paramInt2--;
        if (paramInt2 < 0) {
          break;
        }
        j = paramArrayOfByte[(paramInt1++)];
        if ((i == 10) && (j == 46)) {
          out.write(46);
        }
        if ((j == 10) && (i != 13)) {
          out.write(13);
        }
        out.write(j);
      }
      lastc = i;
    }
    catch (IOException localIOException) {}
  }
  
  public void print(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      write(paramString.charAt(j));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\smtp\SmtpPrintStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */