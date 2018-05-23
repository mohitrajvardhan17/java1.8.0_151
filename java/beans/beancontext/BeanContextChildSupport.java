package java.beans.beancontext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BeanContextChildSupport
  implements BeanContextChild, BeanContextServicesListener, Serializable
{
  static final long serialVersionUID = 6328947014421475877L;
  public BeanContextChild beanContextChildPeer;
  protected PropertyChangeSupport pcSupport;
  protected VetoableChangeSupport vcSupport;
  protected transient BeanContext beanContext;
  protected transient boolean rejectedSetBCOnce;
  
  public BeanContextChildSupport()
  {
    beanContextChildPeer = this;
    pcSupport = new PropertyChangeSupport(beanContextChildPeer);
    vcSupport = new VetoableChangeSupport(beanContextChildPeer);
  }
  
  public BeanContextChildSupport(BeanContextChild paramBeanContextChild)
  {
    beanContextChildPeer = (paramBeanContextChild != null ? paramBeanContextChild : this);
    pcSupport = new PropertyChangeSupport(beanContextChildPeer);
    vcSupport = new VetoableChangeSupport(beanContextChildPeer);
  }
  
  public synchronized void setBeanContext(BeanContext paramBeanContext)
    throws PropertyVetoException
  {
    if (paramBeanContext == beanContext) {
      return;
    }
    BeanContext localBeanContext1 = beanContext;
    BeanContext localBeanContext2 = paramBeanContext;
    if (!rejectedSetBCOnce)
    {
      if ((rejectedSetBCOnce = !validatePendingSetBeanContext(paramBeanContext) ? 1 : 0) != 0) {
        throw new PropertyVetoException("setBeanContext() change rejected:", new PropertyChangeEvent(beanContextChildPeer, "beanContext", localBeanContext1, localBeanContext2));
      }
      try
      {
        fireVetoableChange("beanContext", localBeanContext1, localBeanContext2);
      }
      catch (PropertyVetoException localPropertyVetoException)
      {
        rejectedSetBCOnce = true;
        throw localPropertyVetoException;
      }
    }
    if (beanContext != null) {
      releaseBeanContextResources();
    }
    beanContext = localBeanContext2;
    rejectedSetBCOnce = false;
    firePropertyChange("beanContext", localBeanContext1, localBeanContext2);
    if (beanContext != null) {
      initializeBeanContextResources();
    }
  }
  
  public synchronized BeanContext getBeanContext()
  {
    return beanContext;
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    pcSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    pcSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    vcSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    vcSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent) {}
  
  public void serviceAvailable(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent) {}
  
  public BeanContextChild getBeanContextChildPeer()
  {
    return beanContextChildPeer;
  }
  
  public boolean isDelegated()
  {
    return !equals(beanContextChildPeer);
  }
  
  public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    pcSupport.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2)
    throws PropertyVetoException
  {
    vcSupport.fireVetoableChange(paramString, paramObject1, paramObject2);
  }
  
  public boolean validatePendingSetBeanContext(BeanContext paramBeanContext)
  {
    return true;
  }
  
  protected void releaseBeanContextResources() {}
  
  protected void initializeBeanContextResources() {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if ((!equals(beanContextChildPeer)) && (!(beanContextChildPeer instanceof Serializable))) {
      throw new IOException("BeanContextChildSupport beanContextChildPeer not Serializable");
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextChildSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */