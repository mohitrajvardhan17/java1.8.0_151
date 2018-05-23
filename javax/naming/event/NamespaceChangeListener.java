package javax.naming.event;

public abstract interface NamespaceChangeListener
  extends NamingListener
{
  public abstract void objectAdded(NamingEvent paramNamingEvent);
  
  public abstract void objectRemoved(NamingEvent paramNamingEvent);
  
  public abstract void objectRenamed(NamingEvent paramNamingEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\event\NamespaceChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */