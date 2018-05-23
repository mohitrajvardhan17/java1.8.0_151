package com.sun.jmx.mbeanserver;

import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

final class PerInterface<M>
{
  private final Class<?> mbeanInterface;
  private final MBeanIntrospector<M> introspector;
  private final MBeanInfo mbeanInfo;
  private final Map<String, M> getters = Util.newMap();
  private final Map<String, M> setters = Util.newMap();
  private final Map<String, List<PerInterface<M>.MethodAndSig>> ops = Util.newMap();
  
  PerInterface(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector, MBeanAnalyzer<M> paramMBeanAnalyzer, MBeanInfo paramMBeanInfo)
  {
    mbeanInterface = paramClass;
    introspector = paramMBeanIntrospector;
    mbeanInfo = paramMBeanInfo;
    paramMBeanAnalyzer.visit(new InitMaps(null));
  }
  
  Class<?> getMBeanInterface()
  {
    return mbeanInterface;
  }
  
  MBeanInfo getMBeanInfo()
  {
    return mbeanInfo;
  }
  
  boolean isMXBean()
  {
    return introspector.isMXBean();
  }
  
  Object getAttribute(Object paramObject1, String paramString, Object paramObject2)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    Object localObject = getters.get(paramString);
    if (localObject == null)
    {
      String str;
      if (setters.containsKey(paramString)) {
        str = "Write-only attribute: " + paramString;
      } else {
        str = "No such attribute: " + paramString;
      }
      throw new AttributeNotFoundException(str);
    }
    return introspector.invokeM(localObject, paramObject1, (Object[])null, paramObject2);
  }
  
  void setAttribute(Object paramObject1, String paramString, Object paramObject2, Object paramObject3)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    Object localObject = setters.get(paramString);
    if (localObject == null)
    {
      String str;
      if (getters.containsKey(paramString)) {
        str = "Read-only attribute: " + paramString;
      } else {
        str = "No such attribute: " + paramString;
      }
      throw new AttributeNotFoundException(str);
    }
    introspector.invokeSetter(paramString, localObject, paramObject1, paramObject2, paramObject3);
  }
  
  Object invoke(Object paramObject1, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString, Object paramObject2)
    throws MBeanException, ReflectionException
  {
    List localList = (List)ops.get(paramString);
    if (localList == null)
    {
      localObject1 = "No such operation: " + paramString;
      return noSuchMethod((String)localObject1, paramObject1, paramString, paramArrayOfObject, paramArrayOfString, paramObject2);
    }
    if (paramArrayOfString == null) {
      paramArrayOfString = new String[0];
    }
    Object localObject1 = null;
    Object localObject2 = localList.iterator();
    Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (MethodAndSig)((Iterator)localObject2).next();
      if (Arrays.equals(signature, paramArrayOfString))
      {
        localObject1 = localObject3;
        break;
      }
    }
    if (localObject1 == null)
    {
      localObject2 = sigString(paramArrayOfString);
      if (localList.size() == 1) {
        localObject3 = "Signature mismatch for operation " + paramString + ": " + (String)localObject2 + " should be " + sigString(get0signature);
      } else {
        localObject3 = "Operation " + paramString + " exists but not with this signature: " + (String)localObject2;
      }
      return noSuchMethod((String)localObject3, paramObject1, paramString, paramArrayOfObject, paramArrayOfString, paramObject2);
    }
    return introspector.invokeM(method, paramObject1, paramArrayOfObject, paramObject2);
  }
  
  private Object noSuchMethod(String paramString1, Object paramObject1, String paramString2, Object[] paramArrayOfObject, String[] paramArrayOfString, Object paramObject2)
    throws MBeanException, ReflectionException
  {
    NoSuchMethodException localNoSuchMethodException = new NoSuchMethodException(paramString2 + sigString(paramArrayOfString));
    ReflectionException localReflectionException = new ReflectionException(localNoSuchMethodException, paramString1);
    if (introspector.isMXBean()) {
      throw localReflectionException;
    }
    GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.invoke.getters");
    String str1;
    try
    {
      str1 = (String)AccessController.doPrivileged(localGetPropertyAction);
    }
    catch (Exception localException)
    {
      str1 = null;
    }
    if (str1 == null) {
      throw localReflectionException;
    }
    int i = 0;
    Map localMap = null;
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
    {
      if (paramString2.startsWith("get")) {
        i = 3;
      } else if (paramString2.startsWith("is")) {
        i = 2;
      }
      if (i != 0) {
        localMap = getters;
      }
    }
    else if ((paramArrayOfString.length == 1) && (paramString2.startsWith("set")))
    {
      i = 3;
      localMap = setters;
    }
    if (i != 0)
    {
      String str2 = paramString2.substring(i);
      Object localObject = localMap.get(str2);
      if ((localObject != null) && (introspector.getName(localObject).equals(paramString2)))
      {
        String[] arrayOfString = introspector.getSignature(localObject);
        if (((paramArrayOfString == null) && (arrayOfString.length == 0)) || (Arrays.equals(paramArrayOfString, arrayOfString))) {
          return introspector.invokeM(localObject, paramObject1, paramArrayOfObject, paramObject2);
        }
      }
    }
    throw localReflectionException;
  }
  
  private String sigString(String[] paramArrayOfString)
  {
    StringBuilder localStringBuilder = new StringBuilder("(");
    if (paramArrayOfString != null) {
      for (String str : paramArrayOfString)
      {
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(str);
      }
    }
    return ")";
  }
  
  private class InitMaps
    implements MBeanAnalyzer.MBeanVisitor<M>
  {
    private InitMaps() {}
    
    public void visitAttribute(String paramString, M paramM1, M paramM2)
    {
      Object localObject;
      if (paramM1 != null)
      {
        introspector.checkMethod(paramM1);
        localObject = getters.put(paramString, paramM1);
        assert (localObject == null);
      }
      if (paramM2 != null)
      {
        introspector.checkMethod(paramM2);
        localObject = setters.put(paramString, paramM2);
        assert (localObject == null);
      }
    }
    
    public void visitOperation(String paramString, M paramM)
    {
      introspector.checkMethod(paramM);
      String[] arrayOfString = introspector.getSignature(paramM);
      PerInterface.MethodAndSig localMethodAndSig = new PerInterface.MethodAndSig(PerInterface.this, null);
      method = paramM;
      signature = arrayOfString;
      List localList = (List)ops.get(paramString);
      if (localList == null)
      {
        localList = Collections.singletonList(localMethodAndSig);
      }
      else
      {
        if (localList.size() == 1) {
          localList = Util.newList(localList);
        }
        localList.add(localMethodAndSig);
      }
      ops.put(paramString, localList);
    }
  }
  
  private class MethodAndSig
  {
    M method;
    String[] signature;
    
    private MethodAndSig() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\PerInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */