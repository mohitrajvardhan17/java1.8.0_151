package sun.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Vector;

public class TransferProtocolClient
  extends NetworkClient
{
  static final boolean debug = false;
  protected Vector<String> serverResponse = new Vector(1);
  protected int lastReplyCode;
  
  public int readServerResponse()
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer(32);
    int j = -1;
    serverResponse.setSize(0);
    int k;
    for (;;)
    {
      int i;
      if ((i = serverInput.read()) != -1)
      {
        if ((i == 13) && ((i = serverInput.read()) != 10)) {
          localStringBuffer.append('\r');
        }
        localStringBuffer.append((char)i);
        if (i != 10) {
          continue;
        }
      }
      String str = localStringBuffer.toString();
      localStringBuffer.setLength(0);
      if (str.length() == 0)
      {
        k = -1;
      }
      else
      {
        try
        {
          k = Integer.parseInt(str.substring(0, 3));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          k = -1;
        }
        catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
        continue;
      }
      serverResponse.addElement(str);
      if (j != -1)
      {
        if ((k == j) && ((str.length() < 4) || (str.charAt(3) != '-')))
        {
          j = -1;
          break;
        }
      }
      else
      {
        if ((str.length() < 4) || (str.charAt(3) != '-')) {
          break;
        }
        j = k;
      }
    }
    return lastReplyCode = k;
  }
  
  public void sendServer(String paramString)
  {
    serverOutput.print(paramString);
  }
  
  public String getResponseString()
  {
    return (String)serverResponse.elementAt(0);
  }
  
  public Vector<String> getResponseStrings()
  {
    return serverResponse;
  }
  
  public TransferProtocolClient(String paramString, int paramInt)
    throws IOException
  {
    super(paramString, paramInt);
  }
  
  public TransferProtocolClient() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\TransferProtocolClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */