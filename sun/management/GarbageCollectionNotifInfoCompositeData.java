package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class GarbageCollectionNotifInfoCompositeData
  extends LazyCompositeData
{
  private final GarbageCollectionNotificationInfo gcNotifInfo;
  private static final String GC_NAME = "gcName";
  private static final String GC_ACTION = "gcAction";
  private static final String GC_CAUSE = "gcCause";
  private static final String GC_INFO = "gcInfo";
  private static final String[] gcNotifInfoItemNames = { "gcName", "gcAction", "gcCause", "gcInfo" };
  private static HashMap<GcInfoBuilder, CompositeType> compositeTypeByBuilder = new HashMap();
  private static CompositeType baseGcNotifInfoCompositeType = null;
  private static final long serialVersionUID = -1805123446483771292L;
  
  public GarbageCollectionNotifInfoCompositeData(GarbageCollectionNotificationInfo paramGarbageCollectionNotificationInfo)
  {
    gcNotifInfo = paramGarbageCollectionNotificationInfo;
  }
  
  public GarbageCollectionNotificationInfo getGarbageCollectionNotifInfo()
  {
    return gcNotifInfo;
  }
  
  public static CompositeData toCompositeData(GarbageCollectionNotificationInfo paramGarbageCollectionNotificationInfo)
  {
    GarbageCollectionNotifInfoCompositeData localGarbageCollectionNotifInfoCompositeData = new GarbageCollectionNotifInfoCompositeData(paramGarbageCollectionNotificationInfo);
    return localGarbageCollectionNotifInfoCompositeData.getCompositeData();
  }
  
  private CompositeType getCompositeTypeByBuilder()
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
          return (GcInfoBuilder)localField.get(gcNotifInfo.getGcInfo());
        }
        catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException localClassNotFoundException) {}
        return null;
      }
    });
    CompositeType localCompositeType = null;
    synchronized (compositeTypeByBuilder)
    {
      localCompositeType = (CompositeType)compositeTypeByBuilder.get(localGcInfoBuilder);
      if (localCompositeType == null)
      {
        OpenType[] arrayOfOpenType = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, localGcInfoBuilder.getGcInfoCompositeType() };
        try
        {
          localCompositeType = new CompositeType("sun.management.GarbageCollectionNotifInfoCompositeType", "CompositeType for GC notification info", gcNotifInfoItemNames, gcNotifInfoItemNames, arrayOfOpenType);
          compositeTypeByBuilder.put(localGcInfoBuilder, localCompositeType);
        }
        catch (OpenDataException localOpenDataException)
        {
          throw Util.newException(localOpenDataException);
        }
      }
    }
    return localCompositeType;
  }
  
  protected CompositeData getCompositeData()
  {
    Object[] arrayOfObject = { gcNotifInfo.getGcName(), gcNotifInfo.getGcAction(), gcNotifInfo.getGcCause(), GcInfoCompositeData.toCompositeData(gcNotifInfo.getGcInfo()) };
    CompositeType localCompositeType = getCompositeTypeByBuilder();
    try
    {
      return new CompositeDataSupport(localCompositeType, gcNotifInfoItemNames, arrayOfObject);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw new AssertionError(localOpenDataException);
    }
  }
  
  public static String getGcName(CompositeData paramCompositeData)
  {
    String str = getString(paramCompositeData, "gcName");
    if (str == null) {
      throw new IllegalArgumentException("Invalid composite data: Attribute gcName has null value");
    }
    return str;
  }
  
  public static String getGcAction(CompositeData paramCompositeData)
  {
    String str = getString(paramCompositeData, "gcAction");
    if (str == null) {
      throw new IllegalArgumentException("Invalid composite data: Attribute gcAction has null value");
    }
    return str;
  }
  
  public static String getGcCause(CompositeData paramCompositeData)
  {
    String str = getString(paramCompositeData, "gcCause");
    if (str == null) {
      throw new IllegalArgumentException("Invalid composite data: Attribute gcCause has null value");
    }
    return str;
  }
  
  public static GcInfo getGcInfo(CompositeData paramCompositeData)
  {
    CompositeData localCompositeData = (CompositeData)paramCompositeData.get("gcInfo");
    return GcInfo.from(localCompositeData);
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      throw new NullPointerException("Null CompositeData");
    }
    if (!isTypeMatched(getBaseGcNotifInfoCompositeType(), paramCompositeData.getCompositeType())) {
      throw new IllegalArgumentException("Unexpected composite type for GarbageCollectionNotificationInfo");
    }
  }
  
  private static synchronized CompositeType getBaseGcNotifInfoCompositeType()
  {
    if (baseGcNotifInfoCompositeType == null) {
      try
      {
        OpenType[] arrayOfOpenType = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, GcInfoCompositeData.getBaseGcInfoCompositeType() };
        baseGcNotifInfoCompositeType = new CompositeType("sun.management.BaseGarbageCollectionNotifInfoCompositeType", "CompositeType for Base GarbageCollectionNotificationInfo", gcNotifInfoItemNames, gcNotifInfoItemNames, arrayOfOpenType);
      }
      catch (OpenDataException localOpenDataException)
      {
        throw Util.newException(localOpenDataException);
      }
    }
    return baseGcNotifInfoCompositeType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\GarbageCollectionNotifInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */