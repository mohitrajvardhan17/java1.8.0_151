package java.beans.beancontext;

import java.util.Iterator;

public class BeanContextServiceAvailableEvent
  extends BeanContextEvent
{
  private static final long serialVersionUID = -5333985775656400778L;
  protected Class serviceClass;
  
  public BeanContextServiceAvailableEvent(BeanContextServices paramBeanContextServices, Class paramClass)
  {
    super(paramBeanContextServices);
    serviceClass = paramClass;
  }
  
  public BeanContextServices getSourceAsBeanContextServices()
  {
    return (BeanContextServices)getBeanContext();
  }
  
  public Class getServiceClass()
  {
    return serviceClass;
  }
  
  public Iterator getCurrentServiceSelectors()
  {
    return ((BeanContextServices)getSource()).getCurrentServiceSelectors(serviceClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextServiceAvailableEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */