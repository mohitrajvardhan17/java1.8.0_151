package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EventSetDescriptor
  extends FeatureDescriptor
{
  private MethodDescriptor[] listenerMethodDescriptors;
  private MethodDescriptor addMethodDescriptor;
  private MethodDescriptor removeMethodDescriptor;
  private MethodDescriptor getMethodDescriptor;
  private Reference<Method[]> listenerMethodsRef;
  private Reference<? extends Class<?>> listenerTypeRef;
  private boolean unicast;
  private boolean inDefaultEventSet = true;
  
  public EventSetDescriptor(Class<?> paramClass1, String paramString1, Class<?> paramClass2, String paramString2)
    throws IntrospectionException
  {
    this(paramClass1, paramString1, paramClass2, new String[] { paramString2 }, "add" + getListenerClassName(paramClass2), "remove" + getListenerClassName(paramClass2), "get" + getListenerClassName(paramClass2) + "s");
    String str = NameGenerator.capitalize(paramString1) + "Event";
    Method[] arrayOfMethod = getListenerMethods();
    if (arrayOfMethod.length > 0)
    {
      Class[] arrayOfClass = getParameterTypes(getClass0(), arrayOfMethod[0]);
      if ((!"vetoableChange".equals(paramString1)) && (!arrayOfClass[0].getName().endsWith(str))) {
        throw new IntrospectionException("Method \"" + paramString2 + "\" should have argument \"" + str + "\"");
      }
    }
  }
  
  private static String getListenerClassName(Class<?> paramClass)
  {
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  public EventSetDescriptor(Class<?> paramClass1, String paramString1, Class<?> paramClass2, String[] paramArrayOfString, String paramString2, String paramString3)
    throws IntrospectionException
  {
    this(paramClass1, paramString1, paramClass2, paramArrayOfString, paramString2, paramString3, null);
  }
  
  public EventSetDescriptor(Class<?> paramClass1, String paramString1, Class<?> paramClass2, String[] paramArrayOfString, String paramString2, String paramString3, String paramString4)
    throws IntrospectionException
  {
    if ((paramClass1 == null) || (paramString1 == null) || (paramClass2 == null)) {
      throw new NullPointerException();
    }
    setName(paramString1);
    setClass0(paramClass1);
    setListenerType(paramClass2);
    Method[] arrayOfMethod = new Method[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if (paramArrayOfString[i] == null) {
        throw new NullPointerException();
      }
      arrayOfMethod[i] = getMethod(paramClass2, paramArrayOfString[i], 1);
    }
    setListenerMethods(arrayOfMethod);
    setAddListenerMethod(getMethod(paramClass1, paramString2, 1));
    setRemoveListenerMethod(getMethod(paramClass1, paramString3, 1));
    Method localMethod = Introspector.findMethod(paramClass1, paramString4, 0);
    if (localMethod != null) {
      setGetListenerMethod(localMethod);
    }
  }
  
  private static Method getMethod(Class<?> paramClass, String paramString, int paramInt)
    throws IntrospectionException
  {
    if (paramString == null) {
      return null;
    }
    Method localMethod = Introspector.findMethod(paramClass, paramString, paramInt);
    if ((localMethod == null) || (Modifier.isStatic(localMethod.getModifiers()))) {
      throw new IntrospectionException("Method not found: " + paramString + " on class " + paramClass.getName());
    }
    return localMethod;
  }
  
  public EventSetDescriptor(String paramString, Class<?> paramClass, Method[] paramArrayOfMethod, Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    this(paramString, paramClass, paramArrayOfMethod, paramMethod1, paramMethod2, null);
  }
  
  public EventSetDescriptor(String paramString, Class<?> paramClass, Method[] paramArrayOfMethod, Method paramMethod1, Method paramMethod2, Method paramMethod3)
    throws IntrospectionException
  {
    setName(paramString);
    setListenerMethods(paramArrayOfMethod);
    setAddListenerMethod(paramMethod1);
    setRemoveListenerMethod(paramMethod2);
    setGetListenerMethod(paramMethod3);
    setListenerType(paramClass);
  }
  
  public EventSetDescriptor(String paramString, Class<?> paramClass, MethodDescriptor[] paramArrayOfMethodDescriptor, Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    setName(paramString);
    listenerMethodDescriptors = (paramArrayOfMethodDescriptor != null ? (MethodDescriptor[])paramArrayOfMethodDescriptor.clone() : null);
    setAddListenerMethod(paramMethod1);
    setRemoveListenerMethod(paramMethod2);
    setListenerType(paramClass);
  }
  
  public Class<?> getListenerType()
  {
    return listenerTypeRef != null ? (Class)listenerTypeRef.get() : null;
  }
  
  private void setListenerType(Class<?> paramClass)
  {
    listenerTypeRef = getWeakReference(paramClass);
  }
  
  public synchronized Method[] getListenerMethods()
  {
    Method[] arrayOfMethod = getListenerMethods0();
    if (arrayOfMethod == null)
    {
      if (listenerMethodDescriptors != null)
      {
        arrayOfMethod = new Method[listenerMethodDescriptors.length];
        for (int i = 0; i < arrayOfMethod.length; i++) {
          arrayOfMethod[i] = listenerMethodDescriptors[i].getMethod();
        }
      }
      setListenerMethods(arrayOfMethod);
    }
    return arrayOfMethod;
  }
  
  private void setListenerMethods(Method[] paramArrayOfMethod)
  {
    if (paramArrayOfMethod == null) {
      return;
    }
    if (listenerMethodDescriptors == null)
    {
      listenerMethodDescriptors = new MethodDescriptor[paramArrayOfMethod.length];
      for (int i = 0; i < paramArrayOfMethod.length; i++) {
        listenerMethodDescriptors[i] = new MethodDescriptor(paramArrayOfMethod[i]);
      }
    }
    listenerMethodsRef = getSoftReference(paramArrayOfMethod);
  }
  
  private Method[] getListenerMethods0()
  {
    return listenerMethodsRef != null ? (Method[])listenerMethodsRef.get() : null;
  }
  
  public synchronized MethodDescriptor[] getListenerMethodDescriptors()
  {
    return listenerMethodDescriptors != null ? (MethodDescriptor[])listenerMethodDescriptors.clone() : null;
  }
  
  public synchronized Method getAddListenerMethod()
  {
    return getMethod(addMethodDescriptor);
  }
  
  private synchronized void setAddListenerMethod(Method paramMethod)
  {
    if (paramMethod == null) {
      return;
    }
    if (getClass0() == null) {
      setClass0(paramMethod.getDeclaringClass());
    }
    addMethodDescriptor = new MethodDescriptor(paramMethod);
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public synchronized Method getRemoveListenerMethod()
  {
    return getMethod(removeMethodDescriptor);
  }
  
  private synchronized void setRemoveListenerMethod(Method paramMethod)
  {
    if (paramMethod == null) {
      return;
    }
    if (getClass0() == null) {
      setClass0(paramMethod.getDeclaringClass());
    }
    removeMethodDescriptor = new MethodDescriptor(paramMethod);
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public synchronized Method getGetListenerMethod()
  {
    return getMethod(getMethodDescriptor);
  }
  
  private synchronized void setGetListenerMethod(Method paramMethod)
  {
    if (paramMethod == null) {
      return;
    }
    if (getClass0() == null) {
      setClass0(paramMethod.getDeclaringClass());
    }
    getMethodDescriptor = new MethodDescriptor(paramMethod);
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public void setUnicast(boolean paramBoolean)
  {
    unicast = paramBoolean;
  }
  
  public boolean isUnicast()
  {
    return unicast;
  }
  
  public void setInDefaultEventSet(boolean paramBoolean)
  {
    inDefaultEventSet = paramBoolean;
  }
  
  public boolean isInDefaultEventSet()
  {
    return inDefaultEventSet;
  }
  
  EventSetDescriptor(EventSetDescriptor paramEventSetDescriptor1, EventSetDescriptor paramEventSetDescriptor2)
  {
    super(paramEventSetDescriptor1, paramEventSetDescriptor2);
    listenerMethodDescriptors = listenerMethodDescriptors;
    if (listenerMethodDescriptors != null) {
      listenerMethodDescriptors = listenerMethodDescriptors;
    }
    listenerTypeRef = listenerTypeRef;
    if (listenerTypeRef != null) {
      listenerTypeRef = listenerTypeRef;
    }
    addMethodDescriptor = addMethodDescriptor;
    if (addMethodDescriptor != null) {
      addMethodDescriptor = addMethodDescriptor;
    }
    removeMethodDescriptor = removeMethodDescriptor;
    if (removeMethodDescriptor != null) {
      removeMethodDescriptor = removeMethodDescriptor;
    }
    getMethodDescriptor = getMethodDescriptor;
    if (getMethodDescriptor != null) {
      getMethodDescriptor = getMethodDescriptor;
    }
    unicast = unicast;
    if ((!inDefaultEventSet) || (!inDefaultEventSet)) {
      inDefaultEventSet = false;
    }
  }
  
  EventSetDescriptor(EventSetDescriptor paramEventSetDescriptor)
  {
    super(paramEventSetDescriptor);
    if (listenerMethodDescriptors != null)
    {
      int i = listenerMethodDescriptors.length;
      listenerMethodDescriptors = new MethodDescriptor[i];
      for (int j = 0; j < i; j++) {
        listenerMethodDescriptors[j] = new MethodDescriptor(listenerMethodDescriptors[j]);
      }
    }
    listenerTypeRef = listenerTypeRef;
    addMethodDescriptor = addMethodDescriptor;
    removeMethodDescriptor = removeMethodDescriptor;
    getMethodDescriptor = getMethodDescriptor;
    unicast = unicast;
    inDefaultEventSet = inDefaultEventSet;
  }
  
  void appendTo(StringBuilder paramStringBuilder)
  {
    appendTo(paramStringBuilder, "unicast", unicast);
    appendTo(paramStringBuilder, "inDefaultEventSet", inDefaultEventSet);
    appendTo(paramStringBuilder, "listenerType", listenerTypeRef);
    appendTo(paramStringBuilder, "getListenerMethod", getMethod(getMethodDescriptor));
    appendTo(paramStringBuilder, "addListenerMethod", getMethod(addMethodDescriptor));
    appendTo(paramStringBuilder, "removeListenerMethod", getMethod(removeMethodDescriptor));
  }
  
  private static Method getMethod(MethodDescriptor paramMethodDescriptor)
  {
    return paramMethodDescriptor != null ? paramMethodDescriptor.getMethod() : null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\EventSetDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */