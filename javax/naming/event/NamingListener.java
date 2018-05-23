package javax.naming.event;

import java.util.EventListener;

public abstract interface NamingListener
  extends EventListener
{
  public abstract void namingExceptionThrown(NamingExceptionEvent paramNamingExceptionEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\event\NamingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */