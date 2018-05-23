package com.sun.beans.infos;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ComponentBeanInfo
  extends SimpleBeanInfo
{
  private static final Class<Component> beanClass = Component.class;
  
  public ComponentBeanInfo() {}
  
  public PropertyDescriptor[] getPropertyDescriptors()
  {
    try
    {
      PropertyDescriptor localPropertyDescriptor1 = new PropertyDescriptor("name", beanClass);
      PropertyDescriptor localPropertyDescriptor2 = new PropertyDescriptor("background", beanClass);
      PropertyDescriptor localPropertyDescriptor3 = new PropertyDescriptor("foreground", beanClass);
      PropertyDescriptor localPropertyDescriptor4 = new PropertyDescriptor("font", beanClass);
      PropertyDescriptor localPropertyDescriptor5 = new PropertyDescriptor("enabled", beanClass);
      PropertyDescriptor localPropertyDescriptor6 = new PropertyDescriptor("visible", beanClass);
      PropertyDescriptor localPropertyDescriptor7 = new PropertyDescriptor("focusable", beanClass);
      localPropertyDescriptor5.setExpert(true);
      localPropertyDescriptor6.setHidden(true);
      localPropertyDescriptor2.setBound(true);
      localPropertyDescriptor3.setBound(true);
      localPropertyDescriptor4.setBound(true);
      localPropertyDescriptor7.setBound(true);
      PropertyDescriptor[] arrayOfPropertyDescriptor = { localPropertyDescriptor1, localPropertyDescriptor2, localPropertyDescriptor3, localPropertyDescriptor4, localPropertyDescriptor5, localPropertyDescriptor6, localPropertyDescriptor7 };
      return arrayOfPropertyDescriptor;
    }
    catch (IntrospectionException localIntrospectionException)
    {
      throw new Error(localIntrospectionException.toString());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\infos\ComponentBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */