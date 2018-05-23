package javax.net.ssl;

import java.util.EventObject;

public class SSLSessionBindingEvent
  extends EventObject
{
  private static final long serialVersionUID = 3989172637106345L;
  private String name;
  
  public SSLSessionBindingEvent(SSLSession paramSSLSession, String paramString)
  {
    super(paramSSLSession);
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public SSLSession getSession()
  {
    return (SSLSession)getSource();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLSessionBindingEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */