package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.util.Map;
import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MXBeanProxy
{
  private final Map<Method, Handler> handlerMap = Util.newMap();
  
  public MXBeanProxy(Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("Null parameter");
    }
    MBeanAnalyzer localMBeanAnalyzer;
    try
    {
      localMBeanAnalyzer = MXBeanIntrospector.getInstance().getAnalyzer(paramClass);
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException)
    {
      throw new IllegalArgumentException(localNotCompliantMBeanException);
    }
    localMBeanAnalyzer.visit(new Visitor(null));
  }
  
  public Object invoke(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    Handler localHandler = (Handler)handlerMap.get(paramMethod);
    ConvertingMethod localConvertingMethod = localHandler.getConvertingMethod();
    MXBeanLookup localMXBeanLookup1 = MXBeanLookup.lookupFor(paramMBeanServerConnection);
    MXBeanLookup localMXBeanLookup2 = MXBeanLookup.getLookup();
    try
    {
      MXBeanLookup.setLookup(localMXBeanLookup1);
      Object[] arrayOfObject = localConvertingMethod.toOpenParameters(localMXBeanLookup1, paramArrayOfObject);
      Object localObject1 = localHandler.invoke(paramMBeanServerConnection, paramObjectName, arrayOfObject);
      Object localObject2 = localConvertingMethod.fromOpenReturnValue(localMXBeanLookup1, localObject1);
      return localObject2;
    }
    finally
    {
      MXBeanLookup.setLookup(localMXBeanLookup2);
    }
  }
  
  private static class GetHandler
    extends MXBeanProxy.Handler
  {
    GetHandler(String paramString, ConvertingMethod paramConvertingMethod)
    {
      super(paramConvertingMethod);
    }
    
    Object invoke(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Object[] paramArrayOfObject)
      throws Exception
    {
      assert ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0));
      return paramMBeanServerConnection.getAttribute(paramObjectName, getName());
    }
  }
  
  private static abstract class Handler
  {
    private final String name;
    private final ConvertingMethod convertingMethod;
    
    Handler(String paramString, ConvertingMethod paramConvertingMethod)
    {
      name = paramString;
      convertingMethod = paramConvertingMethod;
    }
    
    String getName()
    {
      return name;
    }
    
    ConvertingMethod getConvertingMethod()
    {
      return convertingMethod;
    }
    
    abstract Object invoke(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Object[] paramArrayOfObject)
      throws Exception;
  }
  
  private static class InvokeHandler
    extends MXBeanProxy.Handler
  {
    private final String[] signature;
    
    InvokeHandler(String paramString, String[] paramArrayOfString, ConvertingMethod paramConvertingMethod)
    {
      super(paramConvertingMethod);
      signature = paramArrayOfString;
    }
    
    Object invoke(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Object[] paramArrayOfObject)
      throws Exception
    {
      return paramMBeanServerConnection.invoke(paramObjectName, getName(), paramArrayOfObject, signature);
    }
  }
  
  private static class SetHandler
    extends MXBeanProxy.Handler
  {
    SetHandler(String paramString, ConvertingMethod paramConvertingMethod)
    {
      super(paramConvertingMethod);
    }
    
    Object invoke(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Object[] paramArrayOfObject)
      throws Exception
    {
      assert (paramArrayOfObject.length == 1);
      Attribute localAttribute = new Attribute(getName(), paramArrayOfObject[0]);
      paramMBeanServerConnection.setAttribute(paramObjectName, localAttribute);
      return null;
    }
  }
  
  private class Visitor
    implements MBeanAnalyzer.MBeanVisitor<ConvertingMethod>
  {
    private Visitor() {}
    
    public void visitAttribute(String paramString, ConvertingMethod paramConvertingMethod1, ConvertingMethod paramConvertingMethod2)
    {
      Method localMethod;
      if (paramConvertingMethod1 != null)
      {
        paramConvertingMethod1.checkCallToOpen();
        localMethod = paramConvertingMethod1.getMethod();
        handlerMap.put(localMethod, new MXBeanProxy.GetHandler(paramString, paramConvertingMethod1));
      }
      if (paramConvertingMethod2 != null)
      {
        localMethod = paramConvertingMethod2.getMethod();
        handlerMap.put(localMethod, new MXBeanProxy.SetHandler(paramString, paramConvertingMethod2));
      }
    }
    
    public void visitOperation(String paramString, ConvertingMethod paramConvertingMethod)
    {
      paramConvertingMethod.checkCallToOpen();
      Method localMethod = paramConvertingMethod.getMethod();
      String[] arrayOfString = paramConvertingMethod.getOpenSignature();
      handlerMap.put(localMethod, new MXBeanProxy.InvokeHandler(paramString, arrayOfString, paramConvertingMethod));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\MXBeanProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */