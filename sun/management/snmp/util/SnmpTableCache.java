package sun.management.snmp.util;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public abstract class SnmpTableCache
  implements Serializable
{
  protected long validity;
  protected transient WeakReference<SnmpCachedData> datas;
  
  public SnmpTableCache() {}
  
  protected boolean isObsolete(SnmpCachedData paramSnmpCachedData)
  {
    if (paramSnmpCachedData == null) {
      return true;
    }
    if (validity < 0L) {
      return false;
    }
    return System.currentTimeMillis() - lastUpdated > validity;
  }
  
  protected SnmpCachedData getCachedDatas()
  {
    if (datas == null) {
      return null;
    }
    SnmpCachedData localSnmpCachedData = (SnmpCachedData)datas.get();
    if ((localSnmpCachedData == null) || (isObsolete(localSnmpCachedData))) {
      return null;
    }
    return localSnmpCachedData;
  }
  
  protected synchronized SnmpCachedData getTableDatas(Object paramObject)
  {
    SnmpCachedData localSnmpCachedData1 = getCachedDatas();
    if (localSnmpCachedData1 != null) {
      return localSnmpCachedData1;
    }
    SnmpCachedData localSnmpCachedData2 = updateCachedDatas(paramObject);
    if (validity != 0L) {
      datas = new WeakReference(localSnmpCachedData2);
    }
    return localSnmpCachedData2;
  }
  
  protected abstract SnmpCachedData updateCachedDatas(Object paramObject);
  
  public abstract SnmpTableHandler getTableHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\util\SnmpTableCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */