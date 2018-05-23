package javax.naming.event;

import java.util.EventObject;
import javax.naming.Binding;

public class NamingEvent
  extends EventObject
{
  public static final int OBJECT_ADDED = 0;
  public static final int OBJECT_REMOVED = 1;
  public static final int OBJECT_RENAMED = 2;
  public static final int OBJECT_CHANGED = 3;
  protected Object changeInfo;
  protected int type;
  protected Binding oldBinding;
  protected Binding newBinding;
  private static final long serialVersionUID = -7126752885365133499L;
  
  public NamingEvent(EventContext paramEventContext, int paramInt, Binding paramBinding1, Binding paramBinding2, Object paramObject)
  {
    super(paramEventContext);
    type = paramInt;
    oldBinding = paramBinding2;
    newBinding = paramBinding1;
    changeInfo = paramObject;
  }
  
  public int getType()
  {
    return type;
  }
  
  public EventContext getEventContext()
  {
    return (EventContext)getSource();
  }
  
  public Binding getOldBinding()
  {
    return oldBinding;
  }
  
  public Binding getNewBinding()
  {
    return newBinding;
  }
  
  public Object getChangeInfo()
  {
    return changeInfo;
  }
  
  public void dispatch(NamingListener paramNamingListener)
  {
    switch (type)
    {
    case 0: 
      ((NamespaceChangeListener)paramNamingListener).objectAdded(this);
      break;
    case 1: 
      ((NamespaceChangeListener)paramNamingListener).objectRemoved(this);
      break;
    case 2: 
      ((NamespaceChangeListener)paramNamingListener).objectRenamed(this);
      break;
    case 3: 
      ((ObjectChangeListener)paramNamingListener).objectChanged(this);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\event\NamingEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */