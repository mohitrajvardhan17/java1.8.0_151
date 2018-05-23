package java.beans.beancontext;

import java.beans.DesignMode;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public abstract interface BeanContext
  extends BeanContextChild, Collection, DesignMode, Visibility
{
  public static final Object globalHierarchyLock = new Object();
  
  public abstract Object instantiateChild(String paramString)
    throws IOException, ClassNotFoundException;
  
  public abstract InputStream getResourceAsStream(String paramString, BeanContextChild paramBeanContextChild)
    throws IllegalArgumentException;
  
  public abstract URL getResource(String paramString, BeanContextChild paramBeanContextChild)
    throws IllegalArgumentException;
  
  public abstract void addBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener);
  
  public abstract void removeBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\beancontext\BeanContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */