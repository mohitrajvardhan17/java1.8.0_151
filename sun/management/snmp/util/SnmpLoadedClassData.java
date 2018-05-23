package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import java.util.TreeMap;

public final class SnmpLoadedClassData
  extends SnmpCachedData
{
  public SnmpLoadedClassData(long paramLong, TreeMap<SnmpOid, Object> paramTreeMap)
  {
    super(paramLong, paramTreeMap, false);
  }
  
  public final Object getData(SnmpOid paramSnmpOid)
  {
    int i = 0;
    try
    {
      i = (int)paramSnmpOid.getOidArc(0);
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      return null;
    }
    if (i >= datas.length) {
      return null;
    }
    return datas[i];
  }
  
  public final SnmpOid getNext(SnmpOid paramSnmpOid)
  {
    int i = 0;
    if ((paramSnmpOid == null) && (datas != null) && (datas.length >= 1)) {
      return new SnmpOid(0L);
    }
    try
    {
      i = (int)paramSnmpOid.getOidArc(0);
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      return null;
    }
    if (i < datas.length - 1) {
      return new SnmpOid(i + 1);
    }
    return null;
  }
  
  public final boolean contains(SnmpOid paramSnmpOid)
  {
    int i = 0;
    try
    {
      i = (int)paramSnmpOid.getOidArc(0);
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      return false;
    }
    return i < datas.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\util\SnmpLoadedClassData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */