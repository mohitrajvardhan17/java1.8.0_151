package java.beans;

import com.sun.beans.TypeResolver;
import com.sun.beans.WeakCache;
import com.sun.beans.finder.BeanInfoFinder;
import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.MethodFinder;
import java.awt.Component;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.TreeMap;
import sun.reflect.misc.ReflectUtil;

public class Introspector
{
  public static final int USE_ALL_BEANINFO = 1;
  public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
  public static final int IGNORE_ALL_BEANINFO = 3;
  private static final WeakCache<Class<?>, Method[]> declaredMethodCache = new WeakCache();
  private Class<?> beanClass;
  private BeanInfo explicitBeanInfo;
  private BeanInfo superBeanInfo;
  private BeanInfo[] additionalBeanInfo;
  private boolean propertyChangeSource = false;
  private static Class<EventListener> eventListenerType = EventListener.class;
  private String defaultEventName;
  private String defaultPropertyName;
  private int defaultEventIndex = -1;
  private int defaultPropertyIndex = -1;
  private Map<String, MethodDescriptor> methods;
  private Map<String, PropertyDescriptor> properties;
  private Map<String, EventSetDescriptor> events;
  private static final EventSetDescriptor[] EMPTY_EVENTSETDESCRIPTORS = new EventSetDescriptor[0];
  static final String ADD_PREFIX = "add";
  static final String REMOVE_PREFIX = "remove";
  static final String GET_PREFIX = "get";
  static final String SET_PREFIX = "set";
  static final String IS_PREFIX = "is";
  private HashMap<String, List<PropertyDescriptor>> pdStore = new HashMap();
  
  public static BeanInfo getBeanInfo(Class<?> paramClass)
    throws IntrospectionException
  {
    if (!ReflectUtil.isPackageAccessible(paramClass)) {
      return new Introspector(paramClass, null, 1).getBeanInfo();
    }
    ThreadGroupContext localThreadGroupContext = ThreadGroupContext.getContext();
    BeanInfo localBeanInfo;
    synchronized (declaredMethodCache)
    {
      localBeanInfo = localThreadGroupContext.getBeanInfo(paramClass);
    }
    if (localBeanInfo == null)
    {
      localBeanInfo = new Introspector(paramClass, null, 1).getBeanInfo();
      synchronized (declaredMethodCache)
      {
        localThreadGroupContext.putBeanInfo(paramClass, localBeanInfo);
      }
    }
    return localBeanInfo;
  }
  
  public static BeanInfo getBeanInfo(Class<?> paramClass, int paramInt)
    throws IntrospectionException
  {
    return getBeanInfo(paramClass, null, paramInt);
  }
  
  public static BeanInfo getBeanInfo(Class<?> paramClass1, Class<?> paramClass2)
    throws IntrospectionException
  {
    return getBeanInfo(paramClass1, paramClass2, 1);
  }
  
  public static BeanInfo getBeanInfo(Class<?> paramClass1, Class<?> paramClass2, int paramInt)
    throws IntrospectionException
  {
    BeanInfo localBeanInfo;
    if ((paramClass2 == null) && (paramInt == 1)) {
      localBeanInfo = getBeanInfo(paramClass1);
    } else {
      localBeanInfo = new Introspector(paramClass1, paramClass2, paramInt).getBeanInfo();
    }
    return localBeanInfo;
  }
  
  public static String decapitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    if ((paramString.length() > 1) && (Character.isUpperCase(paramString.charAt(1))) && (Character.isUpperCase(paramString.charAt(0)))) {
      return paramString;
    }
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toLowerCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  public static String[] getBeanInfoSearchPath()
  {
    return ThreadGroupContext.getContext().getBeanInfoFinder().getPackages();
  }
  
  public static void setBeanInfoSearchPath(String[] paramArrayOfString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertiesAccess();
    }
    ThreadGroupContext.getContext().getBeanInfoFinder().setPackages(paramArrayOfString);
  }
  
  public static void flushCaches()
  {
    synchronized (declaredMethodCache)
    {
      ThreadGroupContext.getContext().clearBeanInfoCache();
      declaredMethodCache.clear();
    }
  }
  
  public static void flushFromCaches(Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException();
    }
    synchronized (declaredMethodCache)
    {
      ThreadGroupContext.getContext().removeBeanInfo(paramClass);
      declaredMethodCache.put(paramClass, null);
    }
  }
  
  private Introspector(Class<?> paramClass1, Class<?> paramClass2, int paramInt)
    throws IntrospectionException
  {
    beanClass = paramClass1;
    if (paramClass2 != null)
    {
      int i = 0;
      for (Class localClass2 = paramClass1.getSuperclass(); localClass2 != null; localClass2 = localClass2.getSuperclass()) {
        if (localClass2 == paramClass2) {
          i = 1;
        }
      }
      if (i == 0) {
        throw new IntrospectionException(paramClass2.getName() + " not superclass of " + paramClass1.getName());
      }
    }
    if (paramInt == 1) {
      explicitBeanInfo = findExplicitBeanInfo(paramClass1);
    }
    Class localClass1 = paramClass1.getSuperclass();
    if (localClass1 != paramClass2)
    {
      int j = paramInt;
      if (j == 2) {
        j = 1;
      }
      superBeanInfo = getBeanInfo(localClass1, paramClass2, j);
    }
    if (explicitBeanInfo != null) {
      additionalBeanInfo = explicitBeanInfo.getAdditionalBeanInfo();
    }
    if (additionalBeanInfo == null) {
      additionalBeanInfo = new BeanInfo[0];
    }
  }
  
  private BeanInfo getBeanInfo()
    throws IntrospectionException
  {
    BeanDescriptor localBeanDescriptor = getTargetBeanDescriptor();
    MethodDescriptor[] arrayOfMethodDescriptor = getTargetMethodInfo();
    EventSetDescriptor[] arrayOfEventSetDescriptor = getTargetEventInfo();
    PropertyDescriptor[] arrayOfPropertyDescriptor = getTargetPropertyInfo();
    int i = getTargetDefaultEventIndex();
    int j = getTargetDefaultPropertyIndex();
    return new GenericBeanInfo(localBeanDescriptor, arrayOfEventSetDescriptor, i, arrayOfPropertyDescriptor, j, arrayOfMethodDescriptor, explicitBeanInfo);
  }
  
  private static BeanInfo findExplicitBeanInfo(Class<?> paramClass)
  {
    return (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(paramClass);
  }
  
  private PropertyDescriptor[] getTargetPropertyInfo()
  {
    PropertyDescriptor[] arrayOfPropertyDescriptor = null;
    if (explicitBeanInfo != null) {
      arrayOfPropertyDescriptor = getPropertyDescriptors(explicitBeanInfo);
    }
    if ((arrayOfPropertyDescriptor == null) && (superBeanInfo != null)) {
      addPropertyDescriptors(getPropertyDescriptors(superBeanInfo));
    }
    for (int i = 0; i < additionalBeanInfo.length; i++) {
      addPropertyDescriptors(additionalBeanInfo[i].getPropertyDescriptors());
    }
    int j;
    if (arrayOfPropertyDescriptor != null)
    {
      addPropertyDescriptors(arrayOfPropertyDescriptor);
    }
    else
    {
      localObject1 = getPublicDeclaredMethods(beanClass);
      for (j = 0; j < localObject1.length; j++)
      {
        Method localMethod = localObject1[j];
        if (localMethod != null)
        {
          int k = localMethod.getModifiers();
          if (!Modifier.isStatic(k))
          {
            String str = localMethod.getName();
            Class[] arrayOfClass = localMethod.getParameterTypes();
            Class localClass = localMethod.getReturnType();
            int m = arrayOfClass.length;
            Object localObject2 = null;
            if ((str.length() > 3) || (str.startsWith("is")))
            {
              try
              {
                if (m == 0)
                {
                  if (str.startsWith("get")) {
                    localObject2 = new PropertyDescriptor(beanClass, str.substring(3), localMethod, null);
                  } else if ((localClass == Boolean.TYPE) && (str.startsWith("is"))) {
                    localObject2 = new PropertyDescriptor(beanClass, str.substring(2), localMethod, null);
                  }
                }
                else if (m == 1)
                {
                  if ((Integer.TYPE.equals(arrayOfClass[0])) && (str.startsWith("get")))
                  {
                    localObject2 = new IndexedPropertyDescriptor(beanClass, str.substring(3), null, null, localMethod, null);
                  }
                  else if ((Void.TYPE.equals(localClass)) && (str.startsWith("set")))
                  {
                    localObject2 = new PropertyDescriptor(beanClass, str.substring(3), null, localMethod);
                    if (throwsException(localMethod, PropertyVetoException.class)) {
                      ((PropertyDescriptor)localObject2).setConstrained(true);
                    }
                  }
                }
                else if ((m == 2) && (Void.TYPE.equals(localClass)) && (Integer.TYPE.equals(arrayOfClass[0])) && (str.startsWith("set")))
                {
                  localObject2 = new IndexedPropertyDescriptor(beanClass, str.substring(3), null, null, null, localMethod);
                  if (throwsException(localMethod, PropertyVetoException.class)) {
                    ((PropertyDescriptor)localObject2).setConstrained(true);
                  }
                }
              }
              catch (IntrospectionException localIntrospectionException)
              {
                localObject2 = null;
              }
              if (localObject2 != null)
              {
                if (propertyChangeSource) {
                  ((PropertyDescriptor)localObject2).setBound(true);
                }
                addPropertyDescriptor((PropertyDescriptor)localObject2);
              }
            }
          }
        }
      }
    }
    processPropertyDescriptors();
    Object localObject1 = (PropertyDescriptor[])properties.values().toArray(new PropertyDescriptor[properties.size()]);
    if (defaultPropertyName != null) {
      for (j = 0; j < localObject1.length; j++) {
        if (defaultPropertyName.equals(localObject1[j].getName())) {
          defaultPropertyIndex = j;
        }
      }
    }
    return (PropertyDescriptor[])localObject1;
  }
  
  private void addPropertyDescriptor(PropertyDescriptor paramPropertyDescriptor)
  {
    String str = paramPropertyDescriptor.getName();
    Object localObject = (List)pdStore.get(str);
    if (localObject == null)
    {
      localObject = new ArrayList();
      pdStore.put(str, localObject);
    }
    if (beanClass != paramPropertyDescriptor.getClass0())
    {
      Method localMethod1 = paramPropertyDescriptor.getReadMethod();
      Method localMethod2 = paramPropertyDescriptor.getWriteMethod();
      int i = 1;
      if (localMethod1 != null) {
        i = (i != 0) && ((localMethod1.getGenericReturnType() instanceof Class)) ? 1 : 0;
      }
      if (localMethod2 != null) {
        i = (i != 0) && ((localMethod2.getGenericParameterTypes()[0] instanceof Class)) ? 1 : 0;
      }
      if ((paramPropertyDescriptor instanceof IndexedPropertyDescriptor))
      {
        IndexedPropertyDescriptor localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor;
        Method localMethod3 = localIndexedPropertyDescriptor.getIndexedReadMethod();
        Method localMethod4 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
        if (localMethod3 != null) {
          i = (i != 0) && ((localMethod3.getGenericReturnType() instanceof Class)) ? 1 : 0;
        }
        if (localMethod4 != null) {
          i = (i != 0) && ((localMethod4.getGenericParameterTypes()[1] instanceof Class)) ? 1 : 0;
        }
        if (i == 0)
        {
          paramPropertyDescriptor = new IndexedPropertyDescriptor(localIndexedPropertyDescriptor);
          paramPropertyDescriptor.updateGenericsFor(beanClass);
        }
      }
      else if (i == 0)
      {
        paramPropertyDescriptor = new PropertyDescriptor(paramPropertyDescriptor);
        paramPropertyDescriptor.updateGenericsFor(beanClass);
      }
    }
    ((List)localObject).add(paramPropertyDescriptor);
  }
  
  private void addPropertyDescriptors(PropertyDescriptor[] paramArrayOfPropertyDescriptor)
  {
    if (paramArrayOfPropertyDescriptor != null) {
      for (PropertyDescriptor localPropertyDescriptor : paramArrayOfPropertyDescriptor) {
        addPropertyDescriptor(localPropertyDescriptor);
      }
    }
  }
  
  private PropertyDescriptor[] getPropertyDescriptors(BeanInfo paramBeanInfo)
  {
    PropertyDescriptor[] arrayOfPropertyDescriptor = paramBeanInfo.getPropertyDescriptors();
    int i = paramBeanInfo.getDefaultPropertyIndex();
    if ((0 <= i) && (i < arrayOfPropertyDescriptor.length)) {
      defaultPropertyName = arrayOfPropertyDescriptor[i].getName();
    }
    return arrayOfPropertyDescriptor;
  }
  
  private void processPropertyDescriptors()
  {
    if (properties == null) {
      properties = new TreeMap();
    }
    Iterator localIterator = pdStore.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      Object localObject5 = null;
      Object localObject6 = null;
      List localList = (List)localIterator.next();
      Object localObject7;
      for (int i = 0; i < localList.size(); i++)
      {
        localObject1 = (PropertyDescriptor)localList.get(i);
        if ((localObject1 instanceof IndexedPropertyDescriptor))
        {
          localObject4 = (IndexedPropertyDescriptor)localObject1;
          if (((IndexedPropertyDescriptor)localObject4).getIndexedReadMethod() != null) {
            if (localObject5 != null) {
              localObject5 = new IndexedPropertyDescriptor((PropertyDescriptor)localObject5, (PropertyDescriptor)localObject4);
            } else {
              localObject5 = localObject4;
            }
          }
        }
        else if (((PropertyDescriptor)localObject1).getReadMethod() != null)
        {
          localObject7 = ((PropertyDescriptor)localObject1).getReadMethod().getName();
          if (localObject2 != null)
          {
            String str = ((PropertyDescriptor)localObject2).getReadMethod().getName();
            if ((str.equals(localObject7)) || (!str.startsWith("is"))) {
              localObject2 = new PropertyDescriptor((PropertyDescriptor)localObject2, (PropertyDescriptor)localObject1);
            }
          }
          else
          {
            localObject2 = localObject1;
          }
        }
      }
      for (i = 0; i < localList.size(); i++)
      {
        localObject1 = (PropertyDescriptor)localList.get(i);
        if ((localObject1 instanceof IndexedPropertyDescriptor))
        {
          localObject4 = (IndexedPropertyDescriptor)localObject1;
          if (((IndexedPropertyDescriptor)localObject4).getIndexedWriteMethod() != null) {
            if (localObject5 != null)
            {
              if (isAssignable(((IndexedPropertyDescriptor)localObject5).getIndexedPropertyType(), ((IndexedPropertyDescriptor)localObject4).getIndexedPropertyType())) {
                if (localObject6 != null) {
                  localObject6 = new IndexedPropertyDescriptor((PropertyDescriptor)localObject6, (PropertyDescriptor)localObject4);
                } else {
                  localObject6 = localObject4;
                }
              }
            }
            else if (localObject6 != null) {
              localObject6 = new IndexedPropertyDescriptor((PropertyDescriptor)localObject6, (PropertyDescriptor)localObject4);
            } else {
              localObject6 = localObject4;
            }
          }
        }
        else if (((PropertyDescriptor)localObject1).getWriteMethod() != null)
        {
          if (localObject2 != null)
          {
            if (isAssignable(((PropertyDescriptor)localObject2).getPropertyType(), ((PropertyDescriptor)localObject1).getPropertyType())) {
              if (localObject3 != null) {
                localObject3 = new PropertyDescriptor((PropertyDescriptor)localObject3, (PropertyDescriptor)localObject1);
              } else {
                localObject3 = localObject1;
              }
            }
          }
          else if (localObject3 != null) {
            localObject3 = new PropertyDescriptor((PropertyDescriptor)localObject3, (PropertyDescriptor)localObject1);
          } else {
            localObject3 = localObject1;
          }
        }
      }
      localObject1 = null;
      localObject4 = null;
      if ((localObject5 != null) && (localObject6 != null))
      {
        if ((localObject2 == localObject3) || (localObject2 == null)) {
          localObject1 = localObject3;
        } else if (localObject3 == null) {
          localObject1 = localObject2;
        } else if ((localObject3 instanceof IndexedPropertyDescriptor)) {
          localObject1 = mergePropertyWithIndexedProperty((PropertyDescriptor)localObject2, (IndexedPropertyDescriptor)localObject3);
        } else if ((localObject2 instanceof IndexedPropertyDescriptor)) {
          localObject1 = mergePropertyWithIndexedProperty((PropertyDescriptor)localObject3, (IndexedPropertyDescriptor)localObject2);
        } else {
          localObject1 = mergePropertyDescriptor((PropertyDescriptor)localObject2, (PropertyDescriptor)localObject3);
        }
        if (localObject5 == localObject6) {
          localObject4 = localObject5;
        } else {
          localObject4 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject5, (IndexedPropertyDescriptor)localObject6);
        }
        if (localObject1 == null)
        {
          localObject1 = localObject4;
        }
        else
        {
          Class localClass = ((PropertyDescriptor)localObject1).getPropertyType();
          localObject7 = ((IndexedPropertyDescriptor)localObject4).getIndexedPropertyType();
          if ((localClass.isArray()) && (localClass.getComponentType() == localObject7)) {
            localObject1 = ((PropertyDescriptor)localObject1).getClass0().isAssignableFrom(((IndexedPropertyDescriptor)localObject4).getClass0()) ? new IndexedPropertyDescriptor((PropertyDescriptor)localObject1, (PropertyDescriptor)localObject4) : new IndexedPropertyDescriptor((PropertyDescriptor)localObject4, (PropertyDescriptor)localObject1);
          } else if (((PropertyDescriptor)localObject1).getClass0().isAssignableFrom(((IndexedPropertyDescriptor)localObject4).getClass0())) {
            localObject1 = ((PropertyDescriptor)localObject1).getClass0().isAssignableFrom(((IndexedPropertyDescriptor)localObject4).getClass0()) ? new PropertyDescriptor((PropertyDescriptor)localObject1, (PropertyDescriptor)localObject4) : new PropertyDescriptor((PropertyDescriptor)localObject4, (PropertyDescriptor)localObject1);
          } else {
            localObject1 = localObject4;
          }
        }
      }
      else if ((localObject2 != null) && (localObject3 != null))
      {
        if (localObject5 != null) {
          localObject2 = mergePropertyWithIndexedProperty((PropertyDescriptor)localObject2, (IndexedPropertyDescriptor)localObject5);
        }
        if (localObject6 != null) {
          localObject3 = mergePropertyWithIndexedProperty((PropertyDescriptor)localObject3, (IndexedPropertyDescriptor)localObject6);
        }
        if (localObject2 == localObject3) {
          localObject1 = localObject2;
        } else if ((localObject3 instanceof IndexedPropertyDescriptor)) {
          localObject1 = mergePropertyWithIndexedProperty((PropertyDescriptor)localObject2, (IndexedPropertyDescriptor)localObject3);
        } else if ((localObject2 instanceof IndexedPropertyDescriptor)) {
          localObject1 = mergePropertyWithIndexedProperty((PropertyDescriptor)localObject3, (IndexedPropertyDescriptor)localObject2);
        } else {
          localObject1 = mergePropertyDescriptor((PropertyDescriptor)localObject2, (PropertyDescriptor)localObject3);
        }
      }
      else if (localObject6 != null)
      {
        localObject1 = localObject6;
        if (localObject3 != null) {
          localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject6, (PropertyDescriptor)localObject3);
        }
        if (localObject2 != null) {
          localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject6, (PropertyDescriptor)localObject2);
        }
      }
      else if (localObject5 != null)
      {
        localObject1 = localObject5;
        if (localObject2 != null) {
          localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject5, (PropertyDescriptor)localObject2);
        }
        if (localObject3 != null) {
          localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject5, (PropertyDescriptor)localObject3);
        }
      }
      else if (localObject3 != null)
      {
        localObject1 = localObject3;
      }
      else if (localObject2 != null)
      {
        localObject1 = localObject2;
      }
      if ((localObject1 instanceof IndexedPropertyDescriptor))
      {
        localObject4 = (IndexedPropertyDescriptor)localObject1;
        if ((((IndexedPropertyDescriptor)localObject4).getIndexedReadMethod() == null) && (((IndexedPropertyDescriptor)localObject4).getIndexedWriteMethod() == null)) {
          localObject1 = new PropertyDescriptor((PropertyDescriptor)localObject4);
        }
      }
      if ((localObject1 == null) && (localList.size() > 0)) {
        localObject1 = (PropertyDescriptor)localList.get(0);
      }
      if (localObject1 != null) {
        properties.put(((PropertyDescriptor)localObject1).getName(), localObject1);
      }
    }
  }
  
  private static boolean isAssignable(Class<?> paramClass1, Class<?> paramClass2)
  {
    return (paramClass1 == null) || (paramClass2 == null) ? false : paramClass1 == paramClass2 ? true : paramClass1.isAssignableFrom(paramClass2);
  }
  
  private PropertyDescriptor mergePropertyWithIndexedProperty(PropertyDescriptor paramPropertyDescriptor, IndexedPropertyDescriptor paramIndexedPropertyDescriptor)
  {
    Class localClass = paramPropertyDescriptor.getPropertyType();
    if ((localClass.isArray()) && (localClass.getComponentType() == paramIndexedPropertyDescriptor.getIndexedPropertyType())) {
      return paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0()) ? new IndexedPropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor) : new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor);
    }
    return paramPropertyDescriptor;
  }
  
  private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor, PropertyDescriptor paramPropertyDescriptor)
  {
    Object localObject = null;
    Class localClass1 = paramPropertyDescriptor.getPropertyType();
    Class localClass2 = paramIndexedPropertyDescriptor.getIndexedPropertyType();
    if ((localClass1.isArray()) && (localClass1.getComponentType() == localClass2))
    {
      if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0())) {
        localObject = new IndexedPropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor);
      } else {
        localObject = new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor);
      }
    }
    else if ((paramIndexedPropertyDescriptor.getReadMethod() == null) && (paramIndexedPropertyDescriptor.getWriteMethod() == null))
    {
      if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0())) {
        localObject = new PropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor);
      } else {
        localObject = new PropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor);
      }
    }
    else if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0()))
    {
      localObject = paramIndexedPropertyDescriptor;
    }
    else
    {
      localObject = paramPropertyDescriptor;
      Method localMethod1 = ((PropertyDescriptor)localObject).getWriteMethod();
      Method localMethod2 = ((PropertyDescriptor)localObject).getReadMethod();
      if ((localMethod2 == null) && (localMethod1 != null))
      {
        localMethod2 = findMethod(((PropertyDescriptor)localObject).getClass0(), "get" + NameGenerator.capitalize(((PropertyDescriptor)localObject).getName()), 0);
        if (localMethod2 != null) {
          try
          {
            ((PropertyDescriptor)localObject).setReadMethod(localMethod2);
          }
          catch (IntrospectionException localIntrospectionException1) {}
        }
      }
      if ((localMethod1 == null) && (localMethod2 != null))
      {
        localMethod1 = findMethod(((PropertyDescriptor)localObject).getClass0(), "set" + NameGenerator.capitalize(((PropertyDescriptor)localObject).getName()), 1, new Class[] { FeatureDescriptor.getReturnType(((PropertyDescriptor)localObject).getClass0(), localMethod2) });
        if (localMethod1 != null) {
          try
          {
            ((PropertyDescriptor)localObject).setWriteMethod(localMethod1);
          }
          catch (IntrospectionException localIntrospectionException2) {}
        }
      }
    }
    return (PropertyDescriptor)localObject;
  }
  
  private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2)
  {
    if (paramPropertyDescriptor1.getClass0().isAssignableFrom(paramPropertyDescriptor2.getClass0())) {
      return new PropertyDescriptor(paramPropertyDescriptor1, paramPropertyDescriptor2);
    }
    return new PropertyDescriptor(paramPropertyDescriptor2, paramPropertyDescriptor1);
  }
  
  private IndexedPropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor1, IndexedPropertyDescriptor paramIndexedPropertyDescriptor2)
  {
    if (paramIndexedPropertyDescriptor1.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor2.getClass0())) {
      return new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor1, paramIndexedPropertyDescriptor2);
    }
    return new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor2, paramIndexedPropertyDescriptor1);
  }
  
  private EventSetDescriptor[] getTargetEventInfo()
    throws IntrospectionException
  {
    if (events == null) {
      events = new HashMap();
    }
    EventSetDescriptor[] arrayOfEventSetDescriptor1 = null;
    if (explicitBeanInfo != null)
    {
      arrayOfEventSetDescriptor1 = explicitBeanInfo.getEventSetDescriptors();
      int i = explicitBeanInfo.getDefaultEventIndex();
      if ((i >= 0) && (i < arrayOfEventSetDescriptor1.length)) {
        defaultEventName = arrayOfEventSetDescriptor1[i].getName();
      }
    }
    if ((arrayOfEventSetDescriptor1 == null) && (superBeanInfo != null))
    {
      EventSetDescriptor[] arrayOfEventSetDescriptor2 = superBeanInfo.getEventSetDescriptors();
      for (int k = 0; k < arrayOfEventSetDescriptor2.length; k++) {
        addEvent(arrayOfEventSetDescriptor2[k]);
      }
      k = superBeanInfo.getDefaultEventIndex();
      if ((k >= 0) && (k < arrayOfEventSetDescriptor2.length)) {
        defaultEventName = arrayOfEventSetDescriptor2[k].getName();
      }
    }
    Object localObject2;
    for (int j = 0; j < additionalBeanInfo.length; j++)
    {
      localObject2 = additionalBeanInfo[j].getEventSetDescriptors();
      if (localObject2 != null) {
        for (int n = 0; n < localObject2.length; n++) {
          addEvent(localObject2[n]);
        }
      }
    }
    Object localObject1;
    if (arrayOfEventSetDescriptor1 != null)
    {
      for (j = 0; j < arrayOfEventSetDescriptor1.length; j++) {
        addEvent(arrayOfEventSetDescriptor1[j]);
      }
    }
    else
    {
      localObject1 = getPublicDeclaredMethods(beanClass);
      localObject2 = null;
      HashMap localHashMap1 = null;
      HashMap localHashMap2 = null;
      Object localObject3;
      Object localObject4;
      Object localObject5;
      Object localObject6;
      Class localClass;
      Object localObject7;
      for (int i1 = 0; i1 < localObject1.length; i1++)
      {
        localObject3 = localObject1[i1];
        if (localObject3 != null)
        {
          int i2 = ((Method)localObject3).getModifiers();
          if (!Modifier.isStatic(i2))
          {
            localObject4 = ((Method)localObject3).getName();
            if ((((String)localObject4).startsWith("add")) || (((String)localObject4).startsWith("remove")) || (((String)localObject4).startsWith("get"))) {
              if (((String)localObject4).startsWith("add"))
              {
                localObject5 = ((Method)localObject3).getReturnType();
                if (localObject5 == Void.TYPE)
                {
                  localObject6 = ((Method)localObject3).getGenericParameterTypes();
                  if (localObject6.length == 1)
                  {
                    localClass = TypeResolver.erase(TypeResolver.resolveInClass(beanClass, localObject6[0]));
                    if (isSubclass(localClass, eventListenerType))
                    {
                      localObject7 = ((String)localObject4).substring(3);
                      if ((((String)localObject7).length() > 0) && (localClass.getName().endsWith((String)localObject7)))
                      {
                        if (localObject2 == null) {
                          localObject2 = new HashMap();
                        }
                        ((Map)localObject2).put(localObject7, localObject3);
                      }
                    }
                  }
                }
              }
              else if (((String)localObject4).startsWith("remove"))
              {
                localObject5 = ((Method)localObject3).getReturnType();
                if (localObject5 == Void.TYPE)
                {
                  localObject6 = ((Method)localObject3).getGenericParameterTypes();
                  if (localObject6.length == 1)
                  {
                    localClass = TypeResolver.erase(TypeResolver.resolveInClass(beanClass, localObject6[0]));
                    if (isSubclass(localClass, eventListenerType))
                    {
                      localObject7 = ((String)localObject4).substring(6);
                      if ((((String)localObject7).length() > 0) && (localClass.getName().endsWith((String)localObject7)))
                      {
                        if (localHashMap1 == null) {
                          localHashMap1 = new HashMap();
                        }
                        localHashMap1.put(localObject7, localObject3);
                      }
                    }
                  }
                }
              }
              else if (((String)localObject4).startsWith("get"))
              {
                localObject5 = ((Method)localObject3).getParameterTypes();
                if (localObject5.length == 0)
                {
                  localObject6 = FeatureDescriptor.getReturnType(beanClass, (Method)localObject3);
                  if (((Class)localObject6).isArray())
                  {
                    localClass = ((Class)localObject6).getComponentType();
                    if (isSubclass(localClass, eventListenerType))
                    {
                      localObject7 = ((String)localObject4).substring(3, ((String)localObject4).length() - 1);
                      if ((((String)localObject7).length() > 0) && (localClass.getName().endsWith((String)localObject7)))
                      {
                        if (localHashMap2 == null) {
                          localHashMap2 = new HashMap();
                        }
                        localHashMap2.put(localObject7, localObject3);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      if ((localObject2 != null) && (localHashMap1 != null))
      {
        Iterator localIterator = ((Map)localObject2).keySet().iterator();
        while (localIterator.hasNext())
        {
          localObject3 = (String)localIterator.next();
          if ((localHashMap1.get(localObject3) != null) && (((String)localObject3).endsWith("Listener")))
          {
            String str = decapitalize(((String)localObject3).substring(0, ((String)localObject3).length() - 8));
            localObject4 = (Method)((Map)localObject2).get(localObject3);
            localObject5 = (Method)localHashMap1.get(localObject3);
            localObject6 = null;
            if (localHashMap2 != null) {
              localObject6 = (Method)localHashMap2.get(localObject3);
            }
            localClass = FeatureDescriptor.getParameterTypes(beanClass, localObject4)[0];
            localObject7 = getPublicDeclaredMethods(localClass);
            ArrayList localArrayList = new ArrayList(localObject7.length);
            for (int i3 = 0; i3 < localObject7.length; i3++) {
              if ((localObject7[i3] != null) && (isEventHandler(localObject7[i3]))) {
                localArrayList.add(localObject7[i3]);
              }
            }
            Method[] arrayOfMethod = (Method[])localArrayList.toArray(new Method[localArrayList.size()]);
            EventSetDescriptor localEventSetDescriptor = new EventSetDescriptor(str, localClass, arrayOfMethod, (Method)localObject4, (Method)localObject5, (Method)localObject6);
            if (throwsException((Method)localObject4, TooManyListenersException.class)) {
              localEventSetDescriptor.setUnicast(true);
            }
            addEvent(localEventSetDescriptor);
          }
        }
      }
    }
    if (events.size() == 0)
    {
      localObject1 = EMPTY_EVENTSETDESCRIPTORS;
    }
    else
    {
      localObject1 = new EventSetDescriptor[events.size()];
      localObject1 = (EventSetDescriptor[])events.values().toArray((Object[])localObject1);
      if (defaultEventName != null) {
        for (int m = 0; m < localObject1.length; m++) {
          if (defaultEventName.equals(localObject1[m].getName())) {
            defaultEventIndex = m;
          }
        }
      }
    }
    return (EventSetDescriptor[])localObject1;
  }
  
  private void addEvent(EventSetDescriptor paramEventSetDescriptor)
  {
    String str = paramEventSetDescriptor.getName();
    if (paramEventSetDescriptor.getName().equals("propertyChange")) {
      propertyChangeSource = true;
    }
    EventSetDescriptor localEventSetDescriptor1 = (EventSetDescriptor)events.get(str);
    if (localEventSetDescriptor1 == null)
    {
      events.put(str, paramEventSetDescriptor);
      return;
    }
    EventSetDescriptor localEventSetDescriptor2 = new EventSetDescriptor(localEventSetDescriptor1, paramEventSetDescriptor);
    events.put(str, localEventSetDescriptor2);
  }
  
  private MethodDescriptor[] getTargetMethodInfo()
  {
    if (methods == null) {
      methods = new HashMap(100);
    }
    MethodDescriptor[] arrayOfMethodDescriptor1 = null;
    if (explicitBeanInfo != null) {
      arrayOfMethodDescriptor1 = explicitBeanInfo.getMethodDescriptors();
    }
    if ((arrayOfMethodDescriptor1 == null) && (superBeanInfo != null))
    {
      MethodDescriptor[] arrayOfMethodDescriptor2 = superBeanInfo.getMethodDescriptors();
      for (int j = 0; j < arrayOfMethodDescriptor2.length; j++) {
        addMethod(arrayOfMethodDescriptor2[j]);
      }
    }
    for (int i = 0; i < additionalBeanInfo.length; i++)
    {
      MethodDescriptor[] arrayOfMethodDescriptor3 = additionalBeanInfo[i].getMethodDescriptors();
      if (arrayOfMethodDescriptor3 != null) {
        for (int m = 0; m < arrayOfMethodDescriptor3.length; m++) {
          addMethod(arrayOfMethodDescriptor3[m]);
        }
      }
    }
    if (arrayOfMethodDescriptor1 != null)
    {
      for (i = 0; i < arrayOfMethodDescriptor1.length; i++) {
        addMethod(arrayOfMethodDescriptor1[i]);
      }
    }
    else
    {
      localObject = getPublicDeclaredMethods(beanClass);
      for (int k = 0; k < localObject.length; k++)
      {
        Method localMethod = localObject[k];
        if (localMethod != null)
        {
          MethodDescriptor localMethodDescriptor = new MethodDescriptor(localMethod);
          addMethod(localMethodDescriptor);
        }
      }
    }
    Object localObject = new MethodDescriptor[methods.size()];
    localObject = (MethodDescriptor[])methods.values().toArray((Object[])localObject);
    return (MethodDescriptor[])localObject;
  }
  
  private void addMethod(MethodDescriptor paramMethodDescriptor)
  {
    String str = paramMethodDescriptor.getName();
    MethodDescriptor localMethodDescriptor1 = (MethodDescriptor)methods.get(str);
    if (localMethodDescriptor1 == null)
    {
      methods.put(str, paramMethodDescriptor);
      return;
    }
    String[] arrayOfString1 = paramMethodDescriptor.getParamNames();
    String[] arrayOfString2 = localMethodDescriptor1.getParamNames();
    int i = 0;
    if (arrayOfString1.length == arrayOfString2.length)
    {
      i = 1;
      for (int j = 0; j < arrayOfString1.length; j++) {
        if (arrayOfString1[j] != arrayOfString2[j])
        {
          i = 0;
          break;
        }
      }
    }
    if (i != 0)
    {
      localObject = new MethodDescriptor(localMethodDescriptor1, paramMethodDescriptor);
      methods.put(str, localObject);
      return;
    }
    Object localObject = makeQualifiedMethodName(str, arrayOfString1);
    localMethodDescriptor1 = (MethodDescriptor)methods.get(localObject);
    if (localMethodDescriptor1 == null)
    {
      methods.put(localObject, paramMethodDescriptor);
      return;
    }
    MethodDescriptor localMethodDescriptor2 = new MethodDescriptor(localMethodDescriptor1, paramMethodDescriptor);
    methods.put(localObject, localMethodDescriptor2);
  }
  
  private static String makeQualifiedMethodName(String paramString, String[] paramArrayOfString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString);
    localStringBuffer.append('=');
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      localStringBuffer.append(':');
      localStringBuffer.append(paramArrayOfString[i]);
    }
    return localStringBuffer.toString();
  }
  
  private int getTargetDefaultEventIndex()
  {
    return defaultEventIndex;
  }
  
  private int getTargetDefaultPropertyIndex()
  {
    return defaultPropertyIndex;
  }
  
  private BeanDescriptor getTargetBeanDescriptor()
  {
    if (explicitBeanInfo != null)
    {
      BeanDescriptor localBeanDescriptor = explicitBeanInfo.getBeanDescriptor();
      if (localBeanDescriptor != null) {
        return localBeanDescriptor;
      }
    }
    return new BeanDescriptor(beanClass, findCustomizerClass(beanClass));
  }
  
  private static Class<?> findCustomizerClass(Class<?> paramClass)
  {
    String str = paramClass.getName() + "Customizer";
    try
    {
      paramClass = ClassFinder.findClass(str, paramClass.getClassLoader());
      if ((Component.class.isAssignableFrom(paramClass)) && (Customizer.class.isAssignableFrom(paramClass))) {
        return paramClass;
      }
    }
    catch (Exception localException) {}
    return null;
  }
  
  private boolean isEventHandler(Method paramMethod)
  {
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    if (arrayOfType.length != 1) {
      return false;
    }
    return isSubclass(TypeResolver.erase(TypeResolver.resolveInClass(beanClass, arrayOfType[0])), EventObject.class);
  }
  
  private static Method[] getPublicDeclaredMethods(Class<?> paramClass)
  {
    if (!ReflectUtil.isPackageAccessible(paramClass)) {
      return new Method[0];
    }
    synchronized (declaredMethodCache)
    {
      Method[] arrayOfMethod = (Method[])declaredMethodCache.get(paramClass);
      if (arrayOfMethod == null)
      {
        arrayOfMethod = paramClass.getMethods();
        for (int i = 0; i < arrayOfMethod.length; i++)
        {
          Method localMethod = arrayOfMethod[i];
          if (!localMethod.getDeclaringClass().equals(paramClass)) {
            arrayOfMethod[i] = null;
          } else {
            try
            {
              localMethod = MethodFinder.findAccessibleMethod(localMethod);
              Class localClass = localMethod.getDeclaringClass();
              arrayOfMethod[i] = ((localClass.equals(paramClass)) || (localClass.isInterface()) ? localMethod : null);
            }
            catch (NoSuchMethodException localNoSuchMethodException) {}
          }
        }
        declaredMethodCache.put(paramClass, arrayOfMethod);
      }
      return arrayOfMethod;
    }
  }
  
  private static Method internalFindMethod(Class<?> paramClass, String paramString, int paramInt, Class[] paramArrayOfClass)
  {
    Method localMethod = null;
    for (Object localObject = paramClass; localObject != null; localObject = ((Class)localObject).getSuperclass())
    {
      Method[] arrayOfMethod = getPublicDeclaredMethods((Class)localObject);
      for (int j = 0; j < arrayOfMethod.length; j++)
      {
        localMethod = arrayOfMethod[j];
        if ((localMethod != null) && (localMethod.getName().equals(paramString)))
        {
          Type[] arrayOfType = localMethod.getGenericParameterTypes();
          if (arrayOfType.length == paramInt)
          {
            if (paramArrayOfClass != null)
            {
              int k = 0;
              if (paramInt > 0)
              {
                for (int m = 0; m < paramInt; m++) {
                  if (TypeResolver.erase(TypeResolver.resolveInClass(paramClass, arrayOfType[m])) != paramArrayOfClass[m]) {
                    k = 1;
                  }
                }
                if (k != 0) {
                  continue;
                }
              }
            }
            return localMethod;
          }
        }
      }
    }
    localMethod = null;
    localObject = paramClass.getInterfaces();
    for (int i = 0; i < localObject.length; i++)
    {
      localMethod = internalFindMethod(localObject[i], paramString, paramInt, null);
      if (localMethod != null) {
        break;
      }
    }
    return localMethod;
  }
  
  static Method findMethod(Class<?> paramClass, String paramString, int paramInt)
  {
    return findMethod(paramClass, paramString, paramInt, null);
  }
  
  static Method findMethod(Class<?> paramClass, String paramString, int paramInt, Class[] paramArrayOfClass)
  {
    if (paramString == null) {
      return null;
    }
    return internalFindMethod(paramClass, paramString, paramInt, paramArrayOfClass);
  }
  
  static boolean isSubclass(Class<?> paramClass1, Class<?> paramClass2)
  {
    if (paramClass1 == paramClass2) {
      return true;
    }
    if ((paramClass1 == null) || (paramClass2 == null)) {
      return false;
    }
    for (Object localObject = paramClass1; localObject != null; localObject = ((Class)localObject).getSuperclass())
    {
      if (localObject == paramClass2) {
        return true;
      }
      if (paramClass2.isInterface())
      {
        Class[] arrayOfClass = ((Class)localObject).getInterfaces();
        for (int i = 0; i < arrayOfClass.length; i++) {
          if (isSubclass(arrayOfClass[i], paramClass2)) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  private boolean throwsException(Method paramMethod, Class<?> paramClass)
  {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      if (arrayOfClass[i] == paramClass) {
        return true;
      }
    }
    return false;
  }
  
  static Object instantiate(Class<?> paramClass, String paramString)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException
  {
    ClassLoader localClassLoader = paramClass.getClassLoader();
    Class localClass = ClassFinder.findClass(paramString, localClassLoader);
    return localClass.newInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\Introspector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */