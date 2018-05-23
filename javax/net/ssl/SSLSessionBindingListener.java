package javax.net.ssl;

import java.util.EventListener;

public abstract interface SSLSessionBindingListener
  extends EventListener
{
  public abstract void valueBound(SSLSessionBindingEvent paramSSLSessionBindingEvent);
  
  public abstract void valueUnbound(SSLSessionBindingEvent paramSSLSessionBindingEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLSessionBindingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */