package java.beans;

import java.util.EventListenerProxy;

public class PropertyChangeListenerProxy
  extends EventListenerProxy<PropertyChangeListener>
  implements PropertyChangeListener
{
  private final String propertyName;
  
  public PropertyChangeListenerProxy(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    super(paramPropertyChangeListener);
    propertyName = paramString;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    ((PropertyChangeListener)getListener()).propertyChange(paramPropertyChangeEvent);
  }
  
  public String getPropertyName()
  {
    return propertyName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PropertyChangeListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */