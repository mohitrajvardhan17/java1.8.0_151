package java.beans.beancontext;

public class BeanContextServiceRevokedEvent
  extends BeanContextEvent
{
  private static final long serialVersionUID = -1295543154724961754L;
  protected Class serviceClass;
  private boolean invalidateRefs;
  
  public BeanContextServiceRevokedEvent(BeanContextServices paramBeanContextServices, Class paramClass, boolean paramBoolean)
  {
    super(paramBeanContextServices);
    serviceClass = paramClass;
    invalidateRefs = paramBoolean;
  }
  
  public BeanContextServices getSourceAsBeanContextServices()
  {
    return (BeanContextServices)getBeanContext();
  }
  
  public Class getServiceClass()
  {
    return serviceClass;
  }
  
  public boolean isServiceClass(Class paramClass)
  {
    return serviceClass.equals(paramClass);
  }
  
  public boolean isCurrentServiceInvalidNow()
  {
    return invalidateRefs;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextServiceRevokedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */