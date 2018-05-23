package java.util;

public abstract class EventListenerProxy<T extends EventListener>
  implements EventListener
{
  private final T listener;
  
  public EventListenerProxy(T paramT)
  {
    listener = paramT;
  }
  
  public T getListener()
  {
    return listener;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\EventListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */