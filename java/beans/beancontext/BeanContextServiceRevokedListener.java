package java.beans.beancontext;

import java.util.EventListener;

public abstract interface BeanContextServiceRevokedListener
  extends EventListener
{
  public abstract void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextServiceRevokedListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */