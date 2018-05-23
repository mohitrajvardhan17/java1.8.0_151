package javax.management.openmbean;

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import com.sun.jmx.mbeanserver.MXBeanLookup;
import com.sun.jmx.mbeanserver.MXBeanMapping;
import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CompositeDataInvocationHandler
  implements InvocationHandler
{
  private final CompositeData compositeData;
  private final MXBeanLookup lookup;
  
  public CompositeDataInvocationHandler(CompositeData paramCompositeData)
  {
    this(paramCompositeData, null);
  }
  
  CompositeDataInvocationHandler(CompositeData paramCompositeData, MXBeanLookup paramMXBeanLookup)
  {
    if (paramCompositeData == null) {
      throw new IllegalArgumentException("compositeData");
    }
    compositeData = paramCompositeData;
    lookup = paramMXBeanLookup;
  }
  
  public CompositeData getCompositeData()
  {
    assert (compositeData != null);
    return compositeData;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    String str1 = paramMethod.getName();
    if (paramMethod.getDeclaringClass() == Object.class)
    {
      if ((str1.equals("toString")) && (paramArrayOfObject == null)) {
        return "Proxy[" + compositeData + "]";
      }
      if ((str1.equals("hashCode")) && (paramArrayOfObject == null)) {
        return Integer.valueOf(compositeData.hashCode() + 1128548680);
      }
      if ((str1.equals("equals")) && (paramArrayOfObject.length == 1) && (paramMethod.getParameterTypes()[0] == Object.class)) {
        return Boolean.valueOf(equals(paramObject, paramArrayOfObject[0]));
      }
      return paramMethod.invoke(this, paramArrayOfObject);
    }
    String str2 = DefaultMXBeanMappingFactory.propertyName(paramMethod);
    if (str2 == null) {
      throw new IllegalArgumentException("Method is not getter: " + paramMethod.getName());
    }
    Object localObject1;
    if (compositeData.containsKey(str2))
    {
      localObject1 = compositeData.get(str2);
    }
    else
    {
      localObject2 = DefaultMXBeanMappingFactory.decapitalize(str2);
      if (compositeData.containsKey((String)localObject2))
      {
        localObject1 = compositeData.get((String)localObject2);
      }
      else
      {
        String str3 = "No CompositeData item " + str2 + (((String)localObject2).equals(str2) ? "" : new StringBuilder().append(" or ").append((String)localObject2).toString()) + " to match " + str1;
        throw new IllegalArgumentException(str3);
      }
    }
    Object localObject2 = MXBeanMappingFactory.DEFAULT.mappingForType(paramMethod.getGenericReturnType(), MXBeanMappingFactory.DEFAULT);
    return ((MXBeanMapping)localObject2).fromOpenValue(localObject1);
  }
  
  private boolean equals(Object paramObject1, Object paramObject2)
  {
    if (paramObject2 == null) {
      return false;
    }
    Class localClass1 = paramObject1.getClass();
    Class localClass2 = paramObject2.getClass();
    if (localClass1 != localClass2) {
      return false;
    }
    InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramObject2);
    if (!(localInvocationHandler instanceof CompositeDataInvocationHandler)) {
      return false;
    }
    CompositeDataInvocationHandler localCompositeDataInvocationHandler = (CompositeDataInvocationHandler)localInvocationHandler;
    return compositeData.equals(compositeData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\CompositeDataInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */