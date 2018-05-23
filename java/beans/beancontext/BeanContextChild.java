package java.beans.beancontext;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

public abstract interface BeanContextChild
{
  public abstract void setBeanContext(BeanContext paramBeanContext)
    throws PropertyVetoException;
  
  public abstract BeanContext getBeanContext();
  
  public abstract void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener);
  
  public abstract void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextChild.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */