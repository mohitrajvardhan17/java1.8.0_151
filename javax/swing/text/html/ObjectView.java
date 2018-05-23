package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.reflect.Method;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class ObjectView
  extends ComponentView
{
  public ObjectView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected Component createComponent()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.CLASSID);
    try
    {
      ReflectUtil.checkPackageAccess(str);
      Class localClass = Class.forName(str, true, Thread.currentThread().getContextClassLoader());
      Object localObject = localClass.newInstance();
      if ((localObject instanceof Component))
      {
        Component localComponent = (Component)localObject;
        setParameters(localComponent, localAttributeSet);
        return localComponent;
      }
    }
    catch (Throwable localThrowable) {}
    return getUnloadableRepresentation();
  }
  
  Component getUnloadableRepresentation()
  {
    JLabel localJLabel = new JLabel("??");
    localJLabel.setForeground(Color.red);
    return localJLabel;
  }
  
  private void setParameters(Component paramComponent, AttributeSet paramAttributeSet)
  {
    Class localClass = paramComponent.getClass();
    BeanInfo localBeanInfo;
    try
    {
      localBeanInfo = Introspector.getBeanInfo(localClass);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      System.err.println("introspector failed, ex: " + localIntrospectionException);
      return;
    }
    PropertyDescriptor[] arrayOfPropertyDescriptor = localBeanInfo.getPropertyDescriptors();
    for (int i = 0; i < arrayOfPropertyDescriptor.length; i++)
    {
      Object localObject = paramAttributeSet.getAttribute(arrayOfPropertyDescriptor[i].getName());
      if ((localObject instanceof String))
      {
        String str = (String)localObject;
        Method localMethod = arrayOfPropertyDescriptor[i].getWriteMethod();
        if (localMethod == null) {
          return;
        }
        Class[] arrayOfClass = localMethod.getParameterTypes();
        if (arrayOfClass.length != 1) {
          return;
        }
        Object[] arrayOfObject = { str };
        try
        {
          MethodUtil.invoke(localMethod, paramComponent, arrayOfObject);
        }
        catch (Exception localException)
        {
          System.err.println("Invocation failed");
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\ObjectView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */