package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

public class SnmpCachedData
  implements SnmpTableHandler
{
  public static final Comparator<SnmpOid> oidComparator = new Comparator()
  {
    public int compare(SnmpOid paramAnonymousSnmpOid1, SnmpOid paramAnonymousSnmpOid2)
    {
      return paramAnonymousSnmpOid1.compareTo(paramAnonymousSnmpOid2);
    }
    
    public boolean equals(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      if (paramAnonymousObject1 == paramAnonymousObject2) {
        return true;
      }
      return paramAnonymousObject1.equals(paramAnonymousObject2);
    }
  };
  public final long lastUpdated;
  public final SnmpOid[] indexes;
  public final Object[] datas;
  
  public SnmpCachedData(long paramLong, SnmpOid[] paramArrayOfSnmpOid, Object[] paramArrayOfObject)
  {
    lastUpdated = paramLong;
    indexes = paramArrayOfSnmpOid;
    datas = paramArrayOfObject;
  }
  
  public SnmpCachedData(long paramLong, TreeMap<SnmpOid, Object> paramTreeMap)
  {
    this(paramLong, paramTreeMap, true);
  }
  
  public SnmpCachedData(long paramLong, TreeMap<SnmpOid, Object> paramTreeMap, boolean paramBoolean)
  {
    int i = paramTreeMap.size();
    lastUpdated = paramLong;
    indexes = new SnmpOid[i];
    datas = new Object[i];
    if (paramBoolean)
    {
      paramTreeMap.keySet().toArray(indexes);
      paramTreeMap.values().toArray(datas);
    }
    else
    {
      paramTreeMap.values().toArray(datas);
    }
  }
  
  public final int find(SnmpOid paramSnmpOid)
  {
    return Arrays.binarySearch(indexes, paramSnmpOid, oidComparator);
  }
  
  public Object getData(SnmpOid paramSnmpOid)
  {
    int i = find(paramSnmpOid);
    if ((i < 0) || (i >= datas.length)) {
      return null;
    }
    return datas[i];
  }
  
  public SnmpOid getNext(SnmpOid paramSnmpOid)
  {
    if (paramSnmpOid == null)
    {
      if (indexes.length > 0) {
        return indexes[0];
      }
      return null;
    }
    int i = find(paramSnmpOid);
    if (i > -1)
    {
      if (i < indexes.length - 1) {
        return indexes[(i + 1)];
      }
      return null;
    }
    int j = -i - 1;
    if ((j > -1) && (j < indexes.length)) {
      return indexes[j];
    }
    return null;
  }
  
  public boolean contains(SnmpOid paramSnmpOid)
  {
    int i = find(paramSnmpOid);
    return (i > -1) && (i < indexes.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\util\SnmpCachedData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */