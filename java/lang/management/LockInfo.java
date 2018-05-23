package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.LockInfoCompositeData;

public class LockInfo
{
  private String className;
  private int identityHashCode;
  
  public LockInfo(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new NullPointerException("Parameter className cannot be null");
    }
    className = paramString;
    identityHashCode = paramInt;
  }
  
  LockInfo(Object paramObject)
  {
    className = paramObject.getClass().getName();
    identityHashCode = System.identityHashCode(paramObject);
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public int getIdentityHashCode()
  {
    return identityHashCode;
  }
  
  public static LockInfo from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof LockInfoCompositeData)) {
      return ((LockInfoCompositeData)paramCompositeData).getLockInfo();
    }
    return LockInfoCompositeData.toLockInfo(paramCompositeData);
  }
  
  public String toString()
  {
    return className + '@' + Integer.toHexString(identityHashCode);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\LockInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */