package sun.swing;

import java.awt.Color;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.ColorUIResource;
import sun.reflect.misc.ReflectUtil;

public class SwingLazyValue
  implements UIDefaults.LazyValue
{
  private String className;
  private String methodName;
  private Object[] args;
  
  public SwingLazyValue(String paramString)
  {
    this(paramString, (String)null);
  }
  
  public SwingLazyValue(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public SwingLazyValue(String paramString, Object[] paramArrayOfObject)
  {
    this(paramString, null, paramArrayOfObject);
  }
  
  public SwingLazyValue(String paramString1, String paramString2, Object[] paramArrayOfObject)
  {
    className = paramString1;
    methodName = paramString2;
    if (paramArrayOfObject != null) {
      args = ((Object[])paramArrayOfObject.clone());
    }
  }
  
  public Object createValue(UIDefaults paramUIDefaults)
  {
    try
    {
      ReflectUtil.checkPackageAccess(className);
      Class localClass = Class.forName(className, true, null);
      if (methodName != null)
      {
        arrayOfClass = getClassArray(args);
        localObject = localClass.getMethod(methodName, arrayOfClass);
        makeAccessible((AccessibleObject)localObject);
        return ((Method)localObject).invoke(localClass, args);
      }
      Class[] arrayOfClass = getClassArray(args);
      Object localObject = localClass.getConstructor(arrayOfClass);
      makeAccessible((AccessibleObject)localObject);
      return ((Constructor)localObject).newInstance(args);
    }
    catch (Exception localException) {}
    return null;
  }
  
  private void makeAccessible(final AccessibleObject paramAccessibleObject)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        paramAccessibleObject.setAccessible(true);
        return null;
      }
    });
  }
  
  private Class[] getClassArray(Object[] paramArrayOfObject)
  {
    Class[] arrayOfClass = null;
    if (paramArrayOfObject != null)
    {
      arrayOfClass = new Class[paramArrayOfObject.length];
      for (int i = 0; i < paramArrayOfObject.length; i++) {
        if ((paramArrayOfObject[i] instanceof Integer)) {
          arrayOfClass[i] = Integer.TYPE;
        } else if ((paramArrayOfObject[i] instanceof Boolean)) {
          arrayOfClass[i] = Boolean.TYPE;
        } else if ((paramArrayOfObject[i] instanceof ColorUIResource)) {
          arrayOfClass[i] = Color.class;
        } else {
          arrayOfClass[i] = paramArrayOfObject[i].getClass();
        }
      }
    }
    return arrayOfClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\SwingLazyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */