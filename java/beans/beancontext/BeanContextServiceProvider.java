package java.beans.beancontext;

import java.util.Iterator;

public abstract interface BeanContextServiceProvider
{
  public abstract Object getService(BeanContextServices paramBeanContextServices, Object paramObject1, Class paramClass, Object paramObject2);
  
  public abstract void releaseService(BeanContextServices paramBeanContextServices, Object paramObject1, Object paramObject2);
  
  public abstract Iterator getCurrentServiceSelectors(BeanContextServices paramBeanContextServices, Class paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */