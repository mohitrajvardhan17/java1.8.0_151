package javax.naming.event;

import java.util.EventObject;
import javax.naming.NamingException;

public class NamingExceptionEvent
  extends EventObject
{
  private NamingException exception;
  private static final long serialVersionUID = -4877678086134736336L;
  
  public NamingExceptionEvent(EventContext paramEventContext, NamingException paramNamingException)
  {
    super(paramEventContext);
    exception = paramNamingException;
  }
  
  public NamingException getException()
  {
    return exception;
  }
  
  public EventContext getEventContext()
  {
    return (EventContext)getSource();
  }
  
  public void dispatch(NamingListener paramNamingListener)
  {
    paramNamingListener.namingExceptionThrown(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\event\NamingExceptionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */