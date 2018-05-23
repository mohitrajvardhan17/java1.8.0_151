package java.beans.beancontext;

import java.util.EventListener;

public abstract interface BeanContextMembershipListener
  extends EventListener
{
  public abstract void childrenAdded(BeanContextMembershipEvent paramBeanContextMembershipEvent);
  
  public abstract void childrenRemoved(BeanContextMembershipEvent paramBeanContextMembershipEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextMembershipListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */