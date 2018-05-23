package java.beans;

import com.sun.beans.finder.BeanInfoFinder;
import java.awt.Image;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

class GenericBeanInfo
  extends SimpleBeanInfo
{
  private BeanDescriptor beanDescriptor;
  private EventSetDescriptor[] events;
  private int defaultEvent;
  private PropertyDescriptor[] properties;
  private int defaultProperty;
  private MethodDescriptor[] methods;
  private Reference<BeanInfo> targetBeanInfoRef;
  
  public GenericBeanInfo(BeanDescriptor paramBeanDescriptor, EventSetDescriptor[] paramArrayOfEventSetDescriptor, int paramInt1, PropertyDescriptor[] paramArrayOfPropertyDescriptor, int paramInt2, MethodDescriptor[] paramArrayOfMethodDescriptor, BeanInfo paramBeanInfo)
  {
    beanDescriptor = paramBeanDescriptor;
    events = paramArrayOfEventSetDescriptor;
    defaultEvent = paramInt1;
    properties = paramArrayOfPropertyDescriptor;
    defaultProperty = paramInt2;
    methods = paramArrayOfMethodDescriptor;
    targetBeanInfoRef = (paramBeanInfo != null ? new SoftReference(paramBeanInfo) : null);
  }
  
  GenericBeanInfo(GenericBeanInfo paramGenericBeanInfo)
  {
    beanDescriptor = new BeanDescriptor(beanDescriptor);
    int i;
    int j;
    if (events != null)
    {
      i = events.length;
      events = new EventSetDescriptor[i];
      for (j = 0; j < i; j++) {
        events[j] = new EventSetDescriptor(events[j]);
      }
    }
    defaultEvent = defaultEvent;
    if (properties != null)
    {
      i = properties.length;
      properties = new PropertyDescriptor[i];
      for (j = 0; j < i; j++)
      {
        PropertyDescriptor localPropertyDescriptor = properties[j];
        if ((localPropertyDescriptor instanceof IndexedPropertyDescriptor)) {
          properties[j] = new IndexedPropertyDescriptor((IndexedPropertyDescriptor)localPropertyDescriptor);
        } else {
          properties[j] = new PropertyDescriptor(localPropertyDescriptor);
        }
      }
    }
    defaultProperty = defaultProperty;
    if (methods != null)
    {
      i = methods.length;
      methods = new MethodDescriptor[i];
      for (j = 0; j < i; j++) {
        methods[j] = new MethodDescriptor(methods[j]);
      }
    }
    targetBeanInfoRef = targetBeanInfoRef;
  }
  
  public PropertyDescriptor[] getPropertyDescriptors()
  {
    return properties;
  }
  
  public int getDefaultPropertyIndex()
  {
    return defaultProperty;
  }
  
  public EventSetDescriptor[] getEventSetDescriptors()
  {
    return events;
  }
  
  public int getDefaultEventIndex()
  {
    return defaultEvent;
  }
  
  public MethodDescriptor[] getMethodDescriptors()
  {
    return methods;
  }
  
  public BeanDescriptor getBeanDescriptor()
  {
    return beanDescriptor;
  }
  
  public Image getIcon(int paramInt)
  {
    BeanInfo localBeanInfo = getTargetBeanInfo();
    if (localBeanInfo != null) {
      return localBeanInfo.getIcon(paramInt);
    }
    return super.getIcon(paramInt);
  }
  
  private BeanInfo getTargetBeanInfo()
  {
    if (targetBeanInfoRef == null) {
      return null;
    }
    BeanInfo localBeanInfo = (BeanInfo)targetBeanInfoRef.get();
    if (localBeanInfo == null)
    {
      localBeanInfo = (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(beanDescriptor.getBeanClass());
      if (localBeanInfo != null) {
        targetBeanInfoRef = new SoftReference(localBeanInfo);
      }
    }
    return localBeanInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\GenericBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */