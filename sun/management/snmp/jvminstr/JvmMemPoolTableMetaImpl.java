package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmMemPoolTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpNamedListTableCache;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmMemPoolTableMetaImpl
  extends JvmMemPoolTableMeta
{
  static final long serialVersionUID = -2525820976094284957L;
  protected SnmpTableCache cache;
  static final MibLogger log = new MibLogger(JvmMemPoolTableMetaImpl.class);
  
  public JvmMemPoolTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
    cache = new JvmMemPoolTableCache(((JVM_MANAGEMENT_MIB_IMPL)paramSnmpMib).validity() * 30L);
  }
  
  protected SnmpOid getNextOid(Object paramObject)
    throws SnmpStatusException
  {
    return getNextOid(null, paramObject);
  }
  
  protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
    throws SnmpStatusException
  {
    boolean bool = log.isDebugOn();
    try
    {
      if (bool) {
        log.debug("getNextOid", "previous=" + paramSnmpOid);
      }
      SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
      if (localSnmpTableHandler == null)
      {
        if (bool) {
          log.debug("getNextOid", "handler is null!");
        }
        throw new SnmpStatusException(224);
      }
      SnmpOid localSnmpOid = localSnmpTableHandler.getNext(paramSnmpOid);
      if (bool) {
        log.debug("getNextOid", "next=" + localSnmpOid);
      }
      if (localSnmpOid == null) {
        throw new SnmpStatusException(224);
      }
      return localSnmpOid;
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      if (bool) {
        log.debug("getNextOid", "End of MIB View: " + localSnmpStatusException);
      }
      throw localSnmpStatusException;
    }
    catch (RuntimeException localRuntimeException)
    {
      if (bool) {
        log.debug("getNextOid", "Unexpected exception: " + localRuntimeException);
      }
      if (bool) {
        log.debug("getNextOid", localRuntimeException);
      }
      throw localRuntimeException;
    }
  }
  
  protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
  {
    SnmpTableHandler localSnmpTableHandler = getHandler(paramObject);
    if (localSnmpTableHandler == null) {
      return false;
    }
    return localSnmpTableHandler.contains(paramSnmpOid);
  }
  
  public Object getEntry(SnmpOid paramSnmpOid)
    throws SnmpStatusException
  {
    if (paramSnmpOid == null) {
      throw new SnmpStatusException(224);
    }
    Map localMap = (Map)Util.cast(JvmContextFactory.getUserData());
    long l = paramSnmpOid.getOidArc(0);
    String str = "JvmMemPoolTable.entry." + l;
    if (localMap != null)
    {
      localObject1 = localMap.get(str);
      if (localObject1 != null) {
        return localObject1;
      }
    }
    Object localObject1 = getHandler(localMap);
    if (localObject1 == null) {
      throw new SnmpStatusException(224);
    }
    Object localObject2 = ((SnmpTableHandler)localObject1).getData(paramSnmpOid);
    if (localObject2 == null) {
      throw new SnmpStatusException(224);
    }
    if (log.isDebugOn()) {
      log.debug("getEntry", "data is a: " + localObject2.getClass().getName());
    }
    JvmMemPoolEntryImpl localJvmMemPoolEntryImpl = new JvmMemPoolEntryImpl((MemoryPoolMXBean)localObject2, (int)l);
    if ((localMap != null) && (localJvmMemPoolEntryImpl != null)) {
      localMap.put(str, localJvmMemPoolEntryImpl);
    }
    return localJvmMemPoolEntryImpl;
  }
  
  protected SnmpTableHandler getHandler(Object paramObject)
  {
    Map localMap;
    if ((paramObject instanceof Map)) {
      localMap = (Map)Util.cast(paramObject);
    } else {
      localMap = null;
    }
    if (localMap != null)
    {
      localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmMemPoolTable.handler");
      if (localSnmpTableHandler != null) {
        return localSnmpTableHandler;
      }
    }
    SnmpTableHandler localSnmpTableHandler = cache.getTableHandler();
    if ((localMap != null) && (localSnmpTableHandler != null)) {
      localMap.put("JvmMemPoolTable.handler", localSnmpTableHandler);
    }
    return localSnmpTableHandler;
  }
  
  private static class JvmMemPoolTableCache
    extends SnmpNamedListTableCache
  {
    static final long serialVersionUID = -1755520683086760574L;
    
    JvmMemPoolTableCache(long paramLong)
    {
      validity = paramLong;
    }
    
    protected String getKey(Object paramObject1, List<?> paramList, int paramInt, Object paramObject2)
    {
      if (paramObject2 == null) {
        return null;
      }
      String str = ((MemoryPoolMXBean)paramObject2).getName();
      JvmMemPoolTableMetaImpl.log.debug("getKey", "key=" + str);
      return str;
    }
    
    public SnmpTableHandler getTableHandler()
    {
      Map localMap = JvmContextFactory.getUserData();
      return getTableDatas(localMap);
    }
    
    protected String getRawDatasKey()
    {
      return "JvmMemManagerTable.getMemoryPools";
    }
    
    protected List<MemoryPoolMXBean> loadRawDatas(Map<Object, Object> paramMap)
    {
      return ManagementFactory.getMemoryPoolMXBeans();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmMemPoolTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */