package java.beans;

import java.util.EventListener;

public abstract interface VetoableChangeListener
  extends EventListener
{
  public abstract void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\VetoableChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */