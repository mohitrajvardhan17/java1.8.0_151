package javax.sql;

import java.util.EventListener;

public abstract interface ConnectionEventListener
  extends EventListener
{
  public abstract void connectionClosed(ConnectionEvent paramConnectionEvent);
  
  public abstract void connectionErrorOccurred(ConnectionEvent paramConnectionEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\ConnectionEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */