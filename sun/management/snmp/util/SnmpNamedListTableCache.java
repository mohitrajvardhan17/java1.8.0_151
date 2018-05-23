package sun.management.snmp.util;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class SnmpNamedListTableCache
  extends SnmpListTableCache
{
  protected TreeMap<String, SnmpOid> names = new TreeMap();
  protected long last = 0L;
  boolean wrapped = false;
  static final MibLogger log = new MibLogger(SnmpNamedListTableCache.class);
  
  public SnmpNamedListTableCache() {}
  
  protected abstract String getKey(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2);
  
  protected SnmpOid makeIndex(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2)
  {
    if (++last > 4294967295L)
    {
      log.debug("makeIndex", "Index wrapping...");
      last = 0L;
      wrapped = true;
    }
    if (!wrapped) {
      return new SnmpOid(last);
    }
    for (int i = 1; i < 4294967295L; i++)
    {
      if (++last > 4294967295L) {
        last = 1L;
      }
      SnmpOid localSnmpOid = new SnmpOid(last);
      if (names == null) {
        return localSnmpOid;
      }
      if (!names.containsValue(localSnmpOid))
      {
        if (paramObject1 == null) {
          return localSnmpOid;
        }
        if (!((Map)paramObject1).containsValue(localSnmpOid)) {
          return localSnmpOid;
        }
      }
    }
    return null;
  }
  
  protected SnmpOid getIndex(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2)
  {
    String str = getKey(paramObject1, paramList, paramInt, paramObject2);
    Object localObject = (names == null) || (str == null) ? null : names.get(str);
    SnmpOid localSnmpOid = localObject != null ? (SnmpOid)localObject : makeIndex(paramObject1, paramList, paramInt, paramObject2);
    if ((paramObject1 != null) && (str != null) && (localSnmpOid != null))
    {
      Map localMap = (Map)Util.cast(paramObject1);
      localMap.put(str, localSnmpOid);
    }
    log.debug("getIndex", "key=" + str + ", index=" + localSnmpOid);
    return localSnmpOid;
  }
  
  protected SnmpCachedData updateCachedDatas(Object paramObject, List<?> paramList)
  {
    TreeMap localTreeMap = new TreeMap();
    SnmpCachedData localSnmpCachedData = super.updateCachedDatas(paramObject, paramList);
    names = localTreeMap;
    return localSnmpCachedData;
  }
  
  protected abstract List<?> loadRawDatas(Map<Object, Object> paramMap);
  
  protected abstract String getRawDatasKey();
  
  protected List<?> getRawDatas(Map<Object, Object> paramMap, String paramString)
  {
    List localList = null;
    if (paramMap != null) {
      localList = (List)paramMap.get(paramString);
    }
    if (localList == null)
    {
      localList = loadRawDatas(paramMap);
      if ((localList != null) && (paramMap != null)) {
        paramMap.put(paramString, localList);
      }
    }
    return localList;
  }
  
  protected SnmpCachedData updateCachedDatas(Object paramObject)
  {
    Map localMap = (paramObject instanceof Map) ? (Map)Util.cast(paramObject) : null;
    List localList = getRawDatas(localMap, getRawDatasKey());
    log.debug("updateCachedDatas", "rawDatas.size()=" + (localList == null ? "<no data>" : new StringBuilder().append("").append(localList.size()).toString()));
    TreeMap localTreeMap = new TreeMap();
    SnmpCachedData localSnmpCachedData = super.updateCachedDatas(localTreeMap, localList);
    names = localTreeMap;
    return localSnmpCachedData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\util\SnmpNamedListTableCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */