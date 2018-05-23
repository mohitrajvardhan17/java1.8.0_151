package sun.management;

import com.sun.management.GcInfo;
import java.io.InvalidObjectException;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;

public class GcInfoCompositeData
  extends LazyCompositeData
{
  private final GcInfo info;
  private final GcInfoBuilder builder;
  private final Object[] gcExtItemValues;
  private static final String ID = "id";
  private static final String START_TIME = "startTime";
  private static final String END_TIME = "endTime";
  private static final String DURATION = "duration";
  private static final String MEMORY_USAGE_BEFORE_GC = "memoryUsageBeforeGc";
  private static final String MEMORY_USAGE_AFTER_GC = "memoryUsageAfterGc";
  private static final String[] baseGcInfoItemNames = { "id", "startTime", "endTime", "duration", "memoryUsageBeforeGc", "memoryUsageAfterGc" };
  private static MappedMXBeanType memoryUsageMapType;
  private static OpenType[] baseGcInfoItemTypes = null;
  private static CompositeType baseGcInfoCompositeType = null;
  private static final long serialVersionUID = -5716428894085882742L;
  
  public GcInfoCompositeData(GcInfo paramGcInfo, GcInfoBuilder paramGcInfoBuilder, Object[] paramArrayOfObject)
  {
    info = paramGcInfo;
    builder = paramGcInfoBuilder;
    gcExtItemValues = paramArrayOfObject;
  }
  
  public GcInfo getGcInfo()
  {
    return info;
  }
  
  public static CompositeData toCompositeData(GcInfo paramGcInfo)
  {
    GcInfoBuilder localGcInfoBuilder = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction()
    {
      public GcInfoBuilder run()
      {
        try
        {
          Class localClass = Class.forName("com.sun.management.GcInfo");
          Field localField = localClass.getDeclaredField("builder");
          localField.setAccessible(true);
          return (GcInfoBuilder)localField.get(val$info);
        }
        catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException localClassNotFoundException) {}
        return null;
      }
    });
    Object[] arrayOfObject = (Object[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object[] run()
      {
        try
        {
          Class localClass = Class.forName("com.sun.management.GcInfo");
          Field localField = localClass.getDeclaredField("extAttributes");
          localField.setAccessible(true);
          return (Object[])localField.get(val$info);
        }
        catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException localClassNotFoundException) {}
        return null;
      }
    });
    GcInfoCompositeData localGcInfoCompositeData = new GcInfoCompositeData(paramGcInfo, localGcInfoBuilder, arrayOfObject);
    return localGcInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData()
  {
    Object[] arrayOfObject1;
    try
    {
      arrayOfObject1 = new Object[] { new Long(info.getId()), new Long(info.getStartTime()), new Long(info.getEndTime()), new Long(info.getDuration()), memoryUsageMapType.toOpenTypeData(info.getMemoryUsageBeforeGc()), memoryUsageMapType.toOpenTypeData(info.getMemoryUsageAfterGc()) };
    }
    catch (OpenDataException localOpenDataException1)
    {
      throw new AssertionError(localOpenDataException1);
    }
    int i = builder.getGcExtItemCount();
    if ((i == 0) && (gcExtItemValues != null) && (gcExtItemValues.length != 0)) {
      throw new AssertionError("Unexpected Gc Extension Item Values");
    }
    if ((i > 0) && ((gcExtItemValues == null) || (i != gcExtItemValues.length))) {
      throw new AssertionError("Unmatched Gc Extension Item Values");
    }
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + i];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
    if (i > 0) {
      System.arraycopy(gcExtItemValues, 0, arrayOfObject2, arrayOfObject1.length, i);
    }
    try
    {
      return new CompositeDataSupport(builder.getGcInfoCompositeType(), builder.getItemNames(), arrayOfObject2);
    }
    catch (OpenDataException localOpenDataException2)
    {
      throw new AssertionError(localOpenDataException2);
    }
  }
  
  static String[] getBaseGcInfoItemNames()
  {
    return baseGcInfoItemNames;
  }
  
  static synchronized OpenType[] getBaseGcInfoItemTypes()
  {
    if (baseGcInfoItemTypes == null)
    {
      OpenType localOpenType = memoryUsageMapType.getOpenType();
      baseGcInfoItemTypes = new OpenType[] { SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, localOpenType, localOpenType };
    }
    return baseGcInfoItemTypes;
  }
  
  public static long getId(CompositeData paramCompositeData)
  {
    return getLong(paramCompositeData, "id");
  }
  
  public static long getStartTime(CompositeData paramCompositeData)
  {
    return getLong(paramCompositeData, "startTime");
  }
  
  public static long getEndTime(CompositeData paramCompositeData)
  {
    return getLong(paramCompositeData, "endTime");
  }
  
  public static Map<String, MemoryUsage> getMemoryUsageBeforeGc(CompositeData paramCompositeData)
  {
    try
    {
      TabularData localTabularData = (TabularData)paramCompositeData.get("memoryUsageBeforeGc");
      return cast(memoryUsageMapType.toJavaTypeData(localTabularData));
    }
    catch (InvalidObjectException|OpenDataException localInvalidObjectException)
    {
      throw new AssertionError(localInvalidObjectException);
    }
  }
  
  public static Map<String, MemoryUsage> cast(Object paramObject)
  {
    return (Map)paramObject;
  }
  
  public static Map<String, MemoryUsage> getMemoryUsageAfterGc(CompositeData paramCompositeData)
  {
    try
    {
      TabularData localTabularData = (TabularData)paramCompositeData.get("memoryUsageAfterGc");
      return cast(memoryUsageMapType.toJavaTypeData(localTabularData));
    }
    catch (InvalidObjectException|OpenDataException localInvalidObjectException)
    {
      throw new AssertionError(localInvalidObjectException);
    }
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      throw new NullPointerException("Null CompositeData");
    }
    if (!isTypeMatched(getBaseGcInfoCompositeType(), paramCompositeData.getCompositeType())) {
      throw new IllegalArgumentException("Unexpected composite type for GcInfo");
    }
  }
  
  static synchronized CompositeType getBaseGcInfoCompositeType()
  {
    if (baseGcInfoCompositeType == null) {
      try
      {
        baseGcInfoCompositeType = new CompositeType("sun.management.BaseGcInfoCompositeType", "CompositeType for Base GcInfo", getBaseGcInfoItemNames(), getBaseGcInfoItemNames(), getBaseGcInfoItemTypes());
      }
      catch (OpenDataException localOpenDataException)
      {
        throw Util.newException(localOpenDataException);
      }
    }
    return baseGcInfoCompositeType;
  }
  
  static
  {
    try
    {
      Method localMethod = GcInfo.class.getMethod("getMemoryUsageBeforeGc", new Class[0]);
      memoryUsageMapType = MappedMXBeanType.getMappedType(localMethod.getGenericReturnType());
    }
    catch (NoSuchMethodException|OpenDataException localNoSuchMethodException)
    {
      throw new AssertionError(localNoSuchMethodException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\GcInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */