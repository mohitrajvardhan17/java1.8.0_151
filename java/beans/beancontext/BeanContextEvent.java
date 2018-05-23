package java.beans.beancontext;

import java.util.EventObject;

public abstract class BeanContextEvent
  extends EventObject
{
  private static final long serialVersionUID = 7267998073569045052L;
  protected BeanContext propagatedFrom;
  
  protected BeanContextEvent(BeanContext paramBeanContext)
  {
    super(paramBeanContext);
  }
  
  public BeanContext getBeanContext()
  {
    return (BeanContext)getSource();
  }
  
  public synchronized void setPropagatedFrom(BeanContext paramBeanContext)
  {
    propagatedFrom = paramBeanContext;
  }
  
  public synchronized BeanContext getPropagatedFrom()
  {
    return propagatedFrom;
  }
  
  public synchronized boolean isPropagated()
  {
    return propagatedFrom != null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */