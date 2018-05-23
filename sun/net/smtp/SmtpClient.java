package sun.net.smtp;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.AccessController;
import sun.net.TransferProtocolClient;
import sun.security.action.GetPropertyAction;

public class SmtpClient
  extends TransferProtocolClient
{
  private static int DEFAULT_SMTP_PORT = 25;
  String mailhost;
  SmtpPrintStream message;
  
  public void closeServer()
    throws IOException
  {
    if (serverIsOpen())
    {
      closeMessage();
      issueCommand("QUIT\r\n", 221);
      super.closeServer();
    }
  }
  
  void issueCommand(String paramString, int paramInt)
    throws IOException
  {
    sendServer(paramString);
    int i;
    while ((i = readServerResponse()) != paramInt) {
      if (i != 220) {
        throw new SmtpProtocolException(getResponseString());
      }
    }
  }
  
  private void toCanonical(String paramString)
    throws IOException
  {
    if (paramString.startsWith("<")) {
      issueCommand("rcpt to: " + paramString + "\r\n", 250);
    } else {
      issueCommand("rcpt to: <" + paramString + ">\r\n", 250);
    }
  }
  
  public void to(String paramString)
    throws IOException
  {
    if (paramString.indexOf('\n') != -1) {
      throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
    }
    int i = 0;
    int j = paramString.length();
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    while (k < j)
    {
      int i2 = paramString.charAt(k);
      if (n > 0)
      {
        if (i2 == 40) {
          n++;
        } else if (i2 == 41) {
          n--;
        }
        if (n == 0) {
          if (m > i) {
            i1 = 1;
          } else {
            i = k + 1;
          }
        }
      }
      else if (i2 == 40)
      {
        n++;
      }
      else if (i2 == 60)
      {
        i = m = k + 1;
      }
      else if (i2 == 62)
      {
        i1 = 1;
      }
      else if (i2 == 44)
      {
        if (m > i) {
          toCanonical(paramString.substring(i, m));
        }
        i = k + 1;
        i1 = 0;
      }
      else if ((i2 > 32) && (i1 == 0))
      {
        m = k + 1;
      }
      else if (i == k)
      {
        i++;
      }
      k++;
    }
    if (m > i) {
      toCanonical(paramString.substring(i, m));
    }
  }
  
  public void from(String paramString)
    throws IOException
  {
    if (paramString.indexOf('\n') != -1) {
      throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
    }
    if (paramString.startsWith("<")) {
      issueCommand("mail from: " + paramString + "\r\n", 250);
    } else {
      issueCommand("mail from: <" + paramString + ">\r\n", 250);
    }
  }
  
  private void openServer(String paramString)
    throws IOException
  {
    mailhost = paramString;
    openServer(mailhost, DEFAULT_SMTP_PORT);
    issueCommand("helo " + InetAddress.getLocalHost().getHostName() + "\r\n", 250);
  }
  
  public PrintStream startMessage()
    throws IOException
  {
    issueCommand("data\r\n", 354);
    try
    {
      message = new SmtpPrintStream(serverOutput, this);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(encoding + " encoding not found", localUnsupportedEncodingException);
    }
    return message;
  }
  
  void closeMessage()
    throws IOException
  {
    if (message != null) {
      message.close();
    }
  }
  
  public SmtpClient(String paramString)
    throws IOException
  {
    if (paramString != null) {
      try
      {
        openServer(paramString);
        mailhost = paramString;
        return;
      }
      catch (Exception localException1) {}
    }
    try
    {
      mailhost = ((String)AccessController.doPrivileged(new GetPropertyAction("mail.host")));
      if (mailhost != null)
      {
        openServer(mailhost);
        return;
      }
    }
    catch (Exception localException2) {}
    try
    {
      mailhost = "localhost";
      openServer(mailhost);
    }
    catch (Exception localException3)
    {
      mailhost = "mailhost";
      openServer(mailhost);
    }
  }
  
  public SmtpClient()
    throws IOException
  {
    this(null);
  }
  
  public SmtpClient(int paramInt)
    throws IOException
  {
    setConnectTimeout(paramInt);
    try
    {
      mailhost = ((String)AccessController.doPrivileged(new GetPropertyAction("mail.host")));
      if (mailhost != null)
      {
        openServer(mailhost);
        return;
      }
    }
    catch (Exception localException1) {}
    try
    {
      mailhost = "localhost";
      openServer(mailhost);
    }
    catch (Exception localException2)
    {
      mailhost = "mailhost";
      openServer(mailhost);
    }
  }
  
  public String getMailHost()
  {
    return mailhost;
  }
  
  String getEncoding()
  {
    return encoding;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\smtp\SmtpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */