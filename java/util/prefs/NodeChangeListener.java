package java.util.prefs;

import java.util.EventListener;

public abstract interface NodeChangeListener
  extends EventListener
{
  public abstract void childAdded(NodeChangeEvent paramNodeChangeEvent);
  
  public abstract void childRemoved(NodeChangeEvent paramNodeChangeEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\NodeChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */