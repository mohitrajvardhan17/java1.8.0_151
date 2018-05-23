package javax.net.ssl;

import java.util.EventListener;

public abstract interface HandshakeCompletedListener
  extends EventListener
{
  public abstract void handshakeCompleted(HandshakeCompletedEvent paramHandshakeCompletedEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\HandshakeCompletedListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */