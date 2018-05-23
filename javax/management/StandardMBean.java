package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.DescriptorCache;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.MBeanSupport;
import com.sun.jmx.mbeanserver.MXBeanSupport;
import com.sun.jmx.mbeanserver.StandardMBeanSupport;
import com.sun.jmx.mbeanserver.Util;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;

public class StandardMBean
  implements DynamicMBean, MBeanRegistration
{
  private static final DescriptorCache descriptors = DescriptorCache.getInstance(JMX.proof);
  private volatile MBeanSupport<?> mbean;
  private volatile MBeanInfo cachedMBeanInfo;
  private static final Map<Class<?>, Boolean> mbeanInfoSafeMap = new WeakHashMap();
  
  private <T> void construct(T paramT, Class<T> paramClass, boolean paramBoolean1, boolean paramBoolean2)
    throws NotCompliantMBeanException
  {
    if (paramT == null) {
      if (paramBoolean1) {
        paramT = Util.cast(this);
      } else {
        throw new IllegalArgumentException("implementation is null");
      }
    }
    if (paramBoolean2)
    {
      if (paramClass == null) {
        paramClass = (Class)Util.cast(Introspector.getMXBeanInterface(paramT.getClass()));
      }
      mbean = new MXBeanSupport(paramT, paramClass);
    }
    else
    {
      if (paramClass == null) {
        paramClass = (Class)Util.cast(Introspector.getStandardMBeanInterface(paramT.getClass()));
      }
      mbean = new StandardMBeanSupport(paramT, paramClass);
    }
  }
  
  public <T> StandardMBean(T paramT, Class<T> paramClass)
    throws NotCompliantMBeanException
  {
    construct(paramT, paramClass, false, false);
  }
  
  protected StandardMBean(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    construct(null, paramClass, true, false);
  }
  
  public <T> StandardMBean(T paramT, Class<T> paramClass, boolean paramBoolean)
  {
    try
    {
      construct(paramT, paramClass, false, paramBoolean);
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException)
    {
      throw new IllegalArgumentException(localNotCompliantMBeanException);
    }
  }
  
  protected StandardMBean(Class<?> paramClass, boolean paramBoolean)
  {
    try
    {
      construct(null, paramClass, true, paramBoolean);
    }
    catch (NotCompliantMBeanException localNotCompliantMBeanException)
    {
      throw new IllegalArgumentException(localNotCompliantMBeanException);
    }
  }
  
  public void setImplementation(Object paramObject)
    throws NotCompliantMBeanException
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("implementation is null");
    }
    if (isMXBean()) {
      mbean = new MXBeanSupport(paramObject, (Class)Util.cast(getMBeanInterface()));
    } else {
      mbean = new StandardMBeanSupport(paramObject, (Class)Util.cast(getMBeanInterface()));
    }
  }
  
  public Object getImplementation()
  {
    return mbean.getResource();
  }
  
  public final Class<?> getMBeanInterface()
  {
    return mbean.getMBeanInterface();
  }
  
  public Class<?> getImplementationClass()
  {
    return mbean.getResource().getClass();
  }
  
  public Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    return mbean.getAttribute(paramString);
  }
  
  public void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    mbean.setAttribute(paramAttribute);
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString)
  {
    return mbean.getAttributes(paramArrayOfString);
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList)
  {
    return mbean.setAttributes(paramAttributeList);
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    return mbean.invoke(paramString, paramArrayOfObject, paramArrayOfString);
  }
  
  public MBeanInfo getMBeanInfo()
  {
    try
    {
      MBeanInfo localMBeanInfo1 = getCachedMBeanInfo();
      if (localMBeanInfo1 != null) {
        return localMBeanInfo1;
      }
    }
    catch (RuntimeException localRuntimeException1)
    {
      if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", "Failed to get cached MBeanInfo", localRuntimeException1);
      }
    }
    if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MISC_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "getMBeanInfo", "Building MBeanInfo for " + getImplementationClass().getName());
    }
    MBeanSupport localMBeanSupport = mbean;
    MBeanInfo localMBeanInfo2 = localMBeanSupport.getMBeanInfo();
    Object localObject = localMBeanSupport.getResource();
    boolean bool = immutableInfo(getClass());
    String str1 = getClassName(localMBeanInfo2);
    String str2 = getDescription(localMBeanInfo2);
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = getConstructors(localMBeanInfo2, localObject);
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = getAttributes(localMBeanInfo2);
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = getOperations(localMBeanInfo2);
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = getNotifications(localMBeanInfo2);
    Descriptor localDescriptor = getDescriptor(localMBeanInfo2, bool);
    MBeanInfo localMBeanInfo3 = new MBeanInfo(str1, str2, arrayOfMBeanAttributeInfo, arrayOfMBeanConstructorInfo, arrayOfMBeanOperationInfo, arrayOfMBeanNotificationInfo, localDescriptor);
    try
    {
      cacheMBeanInfo(localMBeanInfo3);
    }
    catch (RuntimeException localRuntimeException2)
    {
      if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", "Failed to cache MBeanInfo", localRuntimeException2);
      }
    }
    return localMBeanInfo3;
  }
  
  protected String getClassName(MBeanInfo paramMBeanInfo)
  {
    if (paramMBeanInfo == null) {
      return getImplementationClass().getName();
    }
    return paramMBeanInfo.getClassName();
  }
  
  protected String getDescription(MBeanInfo paramMBeanInfo)
  {
    if (paramMBeanInfo == null) {
      return null;
    }
    return paramMBeanInfo.getDescription();
  }
  
  protected String getDescription(MBeanFeatureInfo paramMBeanFeatureInfo)
  {
    if (paramMBeanFeatureInfo == null) {
      return null;
    }
    return paramMBeanFeatureInfo.getDescription();
  }
  
  protected String getDescription(MBeanAttributeInfo paramMBeanAttributeInfo)
  {
    return getDescription(paramMBeanAttributeInfo);
  }
  
  protected String getDescription(MBeanConstructorInfo paramMBeanConstructorInfo)
  {
    return getDescription(paramMBeanConstructorInfo);
  }
  
  protected String getDescription(MBeanConstructorInfo paramMBeanConstructorInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt)
  {
    if (paramMBeanParameterInfo == null) {
      return null;
    }
    return paramMBeanParameterInfo.getDescription();
  }
  
  protected String getParameterName(MBeanConstructorInfo paramMBeanConstructorInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt)
  {
    if (paramMBeanParameterInfo == null) {
      return null;
    }
    return paramMBeanParameterInfo.getName();
  }
  
  protected String getDescription(MBeanOperationInfo paramMBeanOperationInfo)
  {
    return getDescription(paramMBeanOperationInfo);
  }
  
  protected int getImpact(MBeanOperationInfo paramMBeanOperationInfo)
  {
    if (paramMBeanOperationInfo == null) {
      return 3;
    }
    return paramMBeanOperationInfo.getImpact();
  }
  
  protected String getParameterName(MBeanOperationInfo paramMBeanOperationInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt)
  {
    if (paramMBeanParameterInfo == null) {
      return null;
    }
    return paramMBeanParameterInfo.getName();
  }
  
  protected String getDescription(MBeanOperationInfo paramMBeanOperationInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt)
  {
    if (paramMBeanParameterInfo == null) {
      return null;
    }
    return paramMBeanParameterInfo.getDescription();
  }
  
  protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, Object paramObject)
  {
    if (paramArrayOfMBeanConstructorInfo == null) {
      return null;
    }
    if ((paramObject != null) && (paramObject != this)) {
      return null;
    }
    return paramArrayOfMBeanConstructorInfo;
  }
  
  MBeanNotificationInfo[] getNotifications(MBeanInfo paramMBeanInfo)
  {
    return null;
  }
  
  Descriptor getDescriptor(MBeanInfo paramMBeanInfo, boolean paramBoolean)
  {
    Object localObject1;
    Object localObject2;
    ImmutableDescriptor localImmutableDescriptor;
    if ((paramMBeanInfo == null) || (paramMBeanInfo.getDescriptor() == null) || (paramMBeanInfo.getDescriptor().getFieldNames().length == 0))
    {
      localObject1 = "interfaceClassName=" + getMBeanInterface().getName();
      localObject2 = "immutableInfo=" + paramBoolean;
      localImmutableDescriptor = new ImmutableDescriptor(new String[] { localObject1, localObject2 });
      localImmutableDescriptor = descriptors.get(localImmutableDescriptor);
    }
    else
    {
      localObject1 = paramMBeanInfo.getDescriptor();
      localObject2 = new HashMap();
      for (String str : ((Descriptor)localObject1).getFieldNames()) {
        if (str.equals("immutableInfo")) {
          ((Map)localObject2).put(str, Boolean.toString(paramBoolean));
        } else {
          ((Map)localObject2).put(str, ((Descriptor)localObject1).getFieldValue(str));
        }
      }
      localImmutableDescriptor = new ImmutableDescriptor((Map)localObject2);
    }
    return localImmutableDescriptor;
  }
  
  protected MBeanInfo getCachedMBeanInfo()
  {
    return cachedMBeanInfo;
  }
  
  protected void cacheMBeanInfo(MBeanInfo paramMBeanInfo)
  {
    cachedMBeanInfo = paramMBeanInfo;
  }
  
  private boolean isMXBean()
  {
    return mbean.isMXBean();
  }
  
  private static <T> boolean identicalArrays(T[] paramArrayOfT1, T[] paramArrayOfT2)
  {
    if (paramArrayOfT1 == paramArrayOfT2) {
      return true;
    }
    if ((paramArrayOfT1 == null) || (paramArrayOfT2 == null) || (paramArrayOfT1.length != paramArrayOfT2.length)) {
      return false;
    }
    for (int i = 0; i < paramArrayOfT1.length; i++) {
      if (paramArrayOfT1[i] != paramArrayOfT2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private static <T> boolean equal(T paramT1, T paramT2)
  {
    if (paramT1 == paramT2) {
      return true;
    }
    if ((paramT1 == null) || (paramT2 == null)) {
      return false;
    }
    return paramT1.equals(paramT2);
  }
  
  private static MBeanParameterInfo customize(MBeanParameterInfo paramMBeanParameterInfo, String paramString1, String paramString2)
  {
    if ((equal(paramString1, paramMBeanParameterInfo.getName())) && (equal(paramString2, paramMBeanParameterInfo.getDescription()))) {
      return paramMBeanParameterInfo;
    }
    if ((paramMBeanParameterInfo instanceof OpenMBeanParameterInfo))
    {
      OpenMBeanParameterInfo localOpenMBeanParameterInfo = (OpenMBeanParameterInfo)paramMBeanParameterInfo;
      return new OpenMBeanParameterInfoSupport(paramString1, paramString2, localOpenMBeanParameterInfo.getOpenType(), paramMBeanParameterInfo.getDescriptor());
    }
    return new MBeanParameterInfo(paramString1, paramMBeanParameterInfo.getType(), paramString2, paramMBeanParameterInfo.getDescriptor());
  }
  
  private static MBeanConstructorInfo customize(MBeanConstructorInfo paramMBeanConstructorInfo, String paramString, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo)
  {
    if ((equal(paramString, paramMBeanConstructorInfo.getDescription())) && (identicalArrays(paramArrayOfMBeanParameterInfo, paramMBeanConstructorInfo.getSignature()))) {
      return paramMBeanConstructorInfo;
    }
    if ((paramMBeanConstructorInfo instanceof OpenMBeanConstructorInfo))
    {
      OpenMBeanParameterInfo[] arrayOfOpenMBeanParameterInfo = paramsToOpenParams(paramArrayOfMBeanParameterInfo);
      return new OpenMBeanConstructorInfoSupport(paramMBeanConstructorInfo.getName(), paramString, arrayOfOpenMBeanParameterInfo, paramMBeanConstructorInfo.getDescriptor());
    }
    return new MBeanConstructorInfo(paramMBeanConstructorInfo.getName(), paramString, paramArrayOfMBeanParameterInfo, paramMBeanConstructorInfo.getDescriptor());
  }
  
  private static MBeanOperationInfo customize(MBeanOperationInfo paramMBeanOperationInfo, String paramString, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, int paramInt)
  {
    if ((equal(paramString, paramMBeanOperationInfo.getDescription())) && (identicalArrays(paramArrayOfMBeanParameterInfo, paramMBeanOperationInfo.getSignature())) && (paramInt == paramMBeanOperationInfo.getImpact())) {
      return paramMBeanOperationInfo;
    }
    if ((paramMBeanOperationInfo instanceof OpenMBeanOperationInfo))
    {
      OpenMBeanOperationInfo localOpenMBeanOperationInfo = (OpenMBeanOperationInfo)paramMBeanOperationInfo;
      OpenMBeanParameterInfo[] arrayOfOpenMBeanParameterInfo = paramsToOpenParams(paramArrayOfMBeanParameterInfo);
      return new OpenMBeanOperationInfoSupport(paramMBeanOperationInfo.getName(), paramString, arrayOfOpenMBeanParameterInfo, localOpenMBeanOperationInfo.getReturnOpenType(), paramInt, paramMBeanOperationInfo.getDescriptor());
    }
    return new MBeanOperationInfo(paramMBeanOperationInfo.getName(), paramString, paramArrayOfMBeanParameterInfo, paramMBeanOperationInfo.getReturnType(), paramInt, paramMBeanOperationInfo.getDescriptor());
  }
  
  private static MBeanAttributeInfo customize(MBeanAttributeInfo paramMBeanAttributeInfo, String paramString)
  {
    if (equal(paramString, paramMBeanAttributeInfo.getDescription())) {
      return paramMBeanAttributeInfo;
    }
    if ((paramMBeanAttributeInfo instanceof OpenMBeanAttributeInfo))
    {
      OpenMBeanAttributeInfo localOpenMBeanAttributeInfo = (OpenMBeanAttributeInfo)paramMBeanAttributeInfo;
      return new OpenMBeanAttributeInfoSupport(paramMBeanAttributeInfo.getName(), paramString, localOpenMBeanAttributeInfo.getOpenType(), paramMBeanAttributeInfo.isReadable(), paramMBeanAttributeInfo.isWritable(), paramMBeanAttributeInfo.isIs(), paramMBeanAttributeInfo.getDescriptor());
    }
    return new MBeanAttributeInfo(paramMBeanAttributeInfo.getName(), paramMBeanAttributeInfo.getType(), paramString, paramMBeanAttributeInfo.isReadable(), paramMBeanAttributeInfo.isWritable(), paramMBeanAttributeInfo.isIs(), paramMBeanAttributeInfo.getDescriptor());
  }
  
  private static OpenMBeanParameterInfo[] paramsToOpenParams(MBeanParameterInfo[] paramArrayOfMBeanParameterInfo)
  {
    if ((paramArrayOfMBeanParameterInfo instanceof OpenMBeanParameterInfo[])) {
      return (OpenMBeanParameterInfo[])paramArrayOfMBeanParameterInfo;
    }
    OpenMBeanParameterInfoSupport[] arrayOfOpenMBeanParameterInfoSupport = new OpenMBeanParameterInfoSupport[paramArrayOfMBeanParameterInfo.length];
    System.arraycopy(paramArrayOfMBeanParameterInfo, 0, arrayOfOpenMBeanParameterInfoSupport, 0, paramArrayOfMBeanParameterInfo.length);
    return arrayOfOpenMBeanParameterInfoSupport;
  }
  
  private MBeanConstructorInfo[] getConstructors(MBeanInfo paramMBeanInfo, Object paramObject)
  {
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo1 = getConstructors(paramMBeanInfo.getConstructors(), paramObject);
    if (arrayOfMBeanConstructorInfo1 == null) {
      return null;
    }
    int i = arrayOfMBeanConstructorInfo1.length;
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo2 = new MBeanConstructorInfo[i];
    for (int j = 0; j < i; j++)
    {
      MBeanConstructorInfo localMBeanConstructorInfo = arrayOfMBeanConstructorInfo1[j];
      MBeanParameterInfo[] arrayOfMBeanParameterInfo1 = localMBeanConstructorInfo.getSignature();
      MBeanParameterInfo[] arrayOfMBeanParameterInfo2;
      if (arrayOfMBeanParameterInfo1 != null)
      {
        int k = arrayOfMBeanParameterInfo1.length;
        arrayOfMBeanParameterInfo2 = new MBeanParameterInfo[k];
        for (int m = 0; m < k; m++)
        {
          MBeanParameterInfo localMBeanParameterInfo = arrayOfMBeanParameterInfo1[m];
          arrayOfMBeanParameterInfo2[m] = customize(localMBeanParameterInfo, getParameterName(localMBeanConstructorInfo, localMBeanParameterInfo, m), getDescription(localMBeanConstructorInfo, localMBeanParameterInfo, m));
        }
      }
      else
      {
        arrayOfMBeanParameterInfo2 = null;
      }
      arrayOfMBeanConstructorInfo2[j] = customize(localMBeanConstructorInfo, getDescription(localMBeanConstructorInfo), arrayOfMBeanParameterInfo2);
    }
    return arrayOfMBeanConstructorInfo2;
  }
  
  private MBeanOperationInfo[] getOperations(MBeanInfo paramMBeanInfo)
  {
    MBeanOperationInfo[] arrayOfMBeanOperationInfo1 = paramMBeanInfo.getOperations();
    if (arrayOfMBeanOperationInfo1 == null) {
      return null;
    }
    int i = arrayOfMBeanOperationInfo1.length;
    MBeanOperationInfo[] arrayOfMBeanOperationInfo2 = new MBeanOperationInfo[i];
    for (int j = 0; j < i; j++)
    {
      MBeanOperationInfo localMBeanOperationInfo = arrayOfMBeanOperationInfo1[j];
      MBeanParameterInfo[] arrayOfMBeanParameterInfo1 = localMBeanOperationInfo.getSignature();
      MBeanParameterInfo[] arrayOfMBeanParameterInfo2;
      if (arrayOfMBeanParameterInfo1 != null)
      {
        int k = arrayOfMBeanParameterInfo1.length;
        arrayOfMBeanParameterInfo2 = new MBeanParameterInfo[k];
        for (int m = 0; m < k; m++)
        {
          MBeanParameterInfo localMBeanParameterInfo = arrayOfMBeanParameterInfo1[m];
          arrayOfMBeanParameterInfo2[m] = customize(localMBeanParameterInfo, getParameterName(localMBeanOperationInfo, localMBeanParameterInfo, m), getDescription(localMBeanOperationInfo, localMBeanParameterInfo, m));
        }
      }
      else
      {
        arrayOfMBeanParameterInfo2 = null;
      }
      arrayOfMBeanOperationInfo2[j] = customize(localMBeanOperationInfo, getDescription(localMBeanOperationInfo), arrayOfMBeanParameterInfo2, getImpact(localMBeanOperationInfo));
    }
    return arrayOfMBeanOperationInfo2;
  }
  
  private MBeanAttributeInfo[] getAttributes(MBeanInfo paramMBeanInfo)
  {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo1 = paramMBeanInfo.getAttributes();
    if (arrayOfMBeanAttributeInfo1 == null) {
      return null;
    }
    int i = arrayOfMBeanAttributeInfo1.length;
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo2 = new MBeanAttributeInfo[i];
    for (int j = 0; j < i; j++)
    {
      MBeanAttributeInfo localMBeanAttributeInfo = arrayOfMBeanAttributeInfo1[j];
      arrayOfMBeanAttributeInfo2[j] = customize(localMBeanAttributeInfo, getDescription(localMBeanAttributeInfo));
    }
    return arrayOfMBeanAttributeInfo2;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    mbean.register(paramMBeanServer, paramObjectName);
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean)
  {
    if (!paramBoolean.booleanValue()) {
      mbean.unregister();
    }
  }
  
  public void preDeregister()
    throws Exception
  {}
  
  public void postDeregister()
  {
    mbean.unregister();
  }
  
  static boolean immutableInfo(Class<? extends StandardMBean> paramClass)
  {
    if ((paramClass == StandardMBean.class) || (paramClass == StandardEmitterMBean.class)) {
      return true;
    }
    synchronized (mbeanInfoSafeMap)
    {
      Boolean localBoolean = (Boolean)mbeanInfoSafeMap.get(paramClass);
      if (localBoolean == null)
      {
        try
        {
          MBeanInfoSafeAction localMBeanInfoSafeAction = new MBeanInfoSafeAction(paramClass);
          localBoolean = (Boolean)AccessController.doPrivileged(localMBeanInfoSafeAction);
        }
        catch (Exception localException)
        {
          localBoolean = Boolean.valueOf(false);
        }
        mbeanInfoSafeMap.put(paramClass, localBoolean);
      }
      return localBoolean.booleanValue();
    }
  }
  
  static boolean overrides(Class<?> paramClass1, Class<?> paramClass2, String paramString, Class<?>... paramVarArgs)
  {
    Object localObject = paramClass1;
    while (localObject != paramClass2) {
      try
      {
        ((Class)localObject).getDeclaredMethod(paramString, paramVarArgs);
        return true;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localObject = ((Class)localObject).getSuperclass();
      }
    }
    return false;
  }
  
  private static class MBeanInfoSafeAction
    implements PrivilegedAction<Boolean>
  {
    private final Class<?> subclass;
    
    MBeanInfoSafeAction(Class<?> paramClass)
    {
      subclass = paramClass;
    }
    
    public Boolean run()
    {
      if (StandardMBean.overrides(subclass, StandardMBean.class, "cacheMBeanInfo", new Class[] { MBeanInfo.class })) {
        return Boolean.valueOf(false);
      }
      if (StandardMBean.overrides(subclass, StandardMBean.class, "getCachedMBeanInfo", (Class[])null)) {
        return Boolean.valueOf(false);
      }
      if (StandardMBean.overrides(subclass, StandardMBean.class, "getMBeanInfo", (Class[])null)) {
        return Boolean.valueOf(false);
      }
      if ((StandardEmitterMBean.class.isAssignableFrom(subclass)) && (StandardMBean.overrides(subclass, StandardEmitterMBean.class, "getNotificationInfo", (Class[])null))) {
        return Boolean.valueOf(false);
      }
      return Boolean.valueOf(true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\StandardMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */