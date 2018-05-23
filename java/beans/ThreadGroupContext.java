package java.beans;

import com.sun.beans.finder.BeanInfoFinder;
import com.sun.beans.finder.PropertyEditorFinder;
import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.WeakHashMap;

final class ThreadGroupContext
{
  private static final WeakIdentityMap<ThreadGroupContext> contexts = new WeakIdentityMap()
  {
    protected ThreadGroupContext create(Object paramAnonymousObject)
    {
      return new ThreadGroupContext(null);
    }
  };
  private volatile boolean isDesignTime;
  private volatile Boolean isGuiAvailable;
  private Map<Class<?>, BeanInfo> beanInfoCache;
  private BeanInfoFinder beanInfoFinder;
  private PropertyEditorFinder propertyEditorFinder;
  
  static ThreadGroupContext getContext()
  {
    return (ThreadGroupContext)contexts.get(Thread.currentThread().getThreadGroup());
  }
  
  private ThreadGroupContext() {}
  
  boolean isDesignTime()
  {
    return isDesignTime;
  }
  
  void setDesignTime(boolean paramBoolean)
  {
    isDesignTime = paramBoolean;
  }
  
  boolean isGuiAvailable()
  {
    Boolean localBoolean = isGuiAvailable;
    return !GraphicsEnvironment.isHeadless() ? true : localBoolean != null ? localBoolean.booleanValue() : false;
  }
  
  void setGuiAvailable(boolean paramBoolean)
  {
    isGuiAvailable = Boolean.valueOf(paramBoolean);
  }
  
  BeanInfo getBeanInfo(Class<?> paramClass)
  {
    return beanInfoCache != null ? (BeanInfo)beanInfoCache.get(paramClass) : null;
  }
  
  BeanInfo putBeanInfo(Class<?> paramClass, BeanInfo paramBeanInfo)
  {
    if (beanInfoCache == null) {
      beanInfoCache = new WeakHashMap();
    }
    return (BeanInfo)beanInfoCache.put(paramClass, paramBeanInfo);
  }
  
  void removeBeanInfo(Class<?> paramClass)
  {
    if (beanInfoCache != null) {
      beanInfoCache.remove(paramClass);
    }
  }
  
  void clearBeanInfoCache()
  {
    if (beanInfoCache != null) {
      beanInfoCache.clear();
    }
  }
  
  synchronized BeanInfoFinder getBeanInfoFinder()
  {
    if (beanInfoFinder == null) {
      beanInfoFinder = new BeanInfoFinder();
    }
    return beanInfoFinder;
  }
  
  synchronized PropertyEditorFinder getPropertyEditorFinder()
  {
    if (propertyEditorFinder == null) {
      propertyEditorFinder = new PropertyEditorFinder();
    }
    return propertyEditorFinder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\ThreadGroupContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */