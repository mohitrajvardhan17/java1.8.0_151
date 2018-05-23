package sun.management;

import java.lang.management.LockInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class LockInfoCompositeData
  extends LazyCompositeData
{
  private final LockInfo lock;
  private static final CompositeType lockInfoCompositeType;
  private static final String CLASS_NAME = "className";
  private static final String IDENTITY_HASH_CODE = "identityHashCode";
  private static final String[] lockInfoItemNames = { "className", "identityHashCode" };
  private static final long serialVersionUID = -6374759159749014052L;
  
  private LockInfoCompositeData(LockInfo paramLockInfo)
  {
    lock = paramLockInfo;
  }
  
  public LockInfo getLockInfo()
  {
    return lock;
  }
  
  public static CompositeData toCompositeData(LockInfo paramLockInfo)
  {
    if (paramLockInfo == null) {
      return null;
    }
    LockInfoCompositeData localLockInfoCompositeData = new LockInfoCompositeData(paramLockInfo);
    return localLockInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData()
  {
    Object[] arrayOfObject = { new String(lock.getClassName()), new Integer(lock.getIdentityHashCode()) };
    try
    {
      return new CompositeDataSupport(lockInfoCompositeType, lockInfoItemNames, arrayOfObject);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw Util.newException(localOpenDataException);
    }
  }
  
  static CompositeType getLockInfoCompositeType()
  {
    return lockInfoCompositeType;
  }
  
  public static LockInfo toLockInfo(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      throw new NullPointerException("Null CompositeData");
    }
    if (!isTypeMatched(lockInfoCompositeType, paramCompositeData.getCompositeType())) {
      throw new IllegalArgumentException("Unexpected composite type for LockInfo");
    }
    String str = getString(paramCompositeData, "className");
    int i = getInt(paramCompositeData, "identityHashCode");
    return new LockInfo(str, i);
  }
  
  static
  {
    try
    {
      lockInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(LockInfo.class);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw Util.newException(localOpenDataException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\LockInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */