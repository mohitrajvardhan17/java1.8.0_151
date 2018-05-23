package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.Serializable;
import java.util.ArrayList;

public class ObjectPool
  implements Serializable
{
  static final long serialVersionUID = -8519013691660936643L;
  private final Class objectType;
  private final ArrayList freeStack;
  
  public ObjectPool(Class paramClass)
  {
    objectType = paramClass;
    freeStack = new ArrayList();
  }
  
  public ObjectPool(String paramString)
  {
    try
    {
      objectType = ObjectFactory.findProviderClass(paramString, true);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new WrappedRuntimeException(localClassNotFoundException);
    }
    freeStack = new ArrayList();
  }
  
  public ObjectPool(Class paramClass, int paramInt)
  {
    objectType = paramClass;
    freeStack = new ArrayList(paramInt);
  }
  
  public ObjectPool()
  {
    objectType = null;
    freeStack = new ArrayList();
  }
  
  public synchronized Object getInstanceIfFree()
  {
    if (!freeStack.isEmpty())
    {
      Object localObject = freeStack.remove(freeStack.size() - 1);
      return localObject;
    }
    return null;
  }
  
  public synchronized Object getInstance()
  {
    if (freeStack.isEmpty())
    {
      try
      {
        return objectType.newInstance();
      }
      catch (InstantiationException localInstantiationException) {}catch (IllegalAccessException localIllegalAccessException) {}
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_EXCEPTION_CREATING_POOL", null));
    }
    Object localObject = freeStack.remove(freeStack.size() - 1);
    return localObject;
  }
  
  public synchronized void freeInstance(Object paramObject)
  {
    freeStack.add(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\ObjectPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */