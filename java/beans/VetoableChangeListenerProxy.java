package java.beans;

import java.util.EventListenerProxy;

public class VetoableChangeListenerProxy
  extends EventListenerProxy<VetoableChangeListener>
  implements VetoableChangeListener
{
  private final String propertyName;
  
  public VetoableChangeListenerProxy(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    super(paramVetoableChangeListener);
    propertyName = paramString;
  }
  
  public void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException
  {
    ((VetoableChangeListener)getListener()).vetoableChange(paramPropertyChangeEvent);
  }
  
  public String getPropertyName()
  {
    return propertyName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\VetoableChangeListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */