package sun.net;

import java.net.SocketException;

public class ConnectionResetException
  extends SocketException
{
  private static final long serialVersionUID = -7633185991801851556L;
  
  public ConnectionResetException(String paramString)
  {
    super(paramString);
  }
  
  public ConnectionResetException() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ConnectionResetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */