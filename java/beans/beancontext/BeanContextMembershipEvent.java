package java.beans.beancontext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class BeanContextMembershipEvent
  extends BeanContextEvent
{
  private static final long serialVersionUID = 3499346510334590959L;
  protected Collection children;
  
  public BeanContextMembershipEvent(BeanContext paramBeanContext, Collection paramCollection)
  {
    super(paramBeanContext);
    if (paramCollection == null) {
      throw new NullPointerException("BeanContextMembershipEvent constructor:  changes is null.");
    }
    children = paramCollection;
  }
  
  public BeanContextMembershipEvent(BeanContext paramBeanContext, Object[] paramArrayOfObject)
  {
    super(paramBeanContext);
    if (paramArrayOfObject == null) {
      throw new NullPointerException("BeanContextMembershipEvent:  changes is null.");
    }
    children = Arrays.asList(paramArrayOfObject);
  }
  
  public int size()
  {
    return children.size();
  }
  
  public boolean contains(Object paramObject)
  {
    return children.contains(paramObject);
  }
  
  public Object[] toArray()
  {
    return children.toArray();
  }
  
  public Iterator iterator()
  {
    return children.iterator();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContextMembershipEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */