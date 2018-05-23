package java.beans;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.net.URL;

public class SimpleBeanInfo
  implements BeanInfo
{
  public SimpleBeanInfo() {}
  
  public BeanDescriptor getBeanDescriptor()
  {
    return null;
  }
  
  public PropertyDescriptor[] getPropertyDescriptors()
  {
    return null;
  }
  
  public int getDefaultPropertyIndex()
  {
    return -1;
  }
  
  public EventSetDescriptor[] getEventSetDescriptors()
  {
    return null;
  }
  
  public int getDefaultEventIndex()
  {
    return -1;
  }
  
  public MethodDescriptor[] getMethodDescriptors()
  {
    return null;
  }
  
  public BeanInfo[] getAdditionalBeanInfo()
  {
    return null;
  }
  
  public Image getIcon(int paramInt)
  {
    return null;
  }
  
  public Image loadImage(String paramString)
  {
    try
    {
      URL localURL = getClass().getResource(paramString);
      if (localURL != null)
      {
        ImageProducer localImageProducer = (ImageProducer)localURL.getContent();
        if (localImageProducer != null) {
          return Toolkit.getDefaultToolkit().createImage(localImageProducer);
        }
      }
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\SimpleBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */