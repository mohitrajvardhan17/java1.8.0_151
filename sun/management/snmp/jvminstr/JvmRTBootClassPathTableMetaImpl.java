package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.util.Map;
import sun.management.snmp.jvmmib.JvmRTBootClassPathTableMeta;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableCache;
import sun.management.snmp.util.SnmpTableHandler;

public class JvmRTBootClassPathTableMetaImpl
  extends JvmRTBootClassPathTableMeta
{
  static final long serialVersionUID = -8659886610487538299L;
  private SnmpTableCache cache = new JvmRTBootClassPathTableCache(this, -1L);
  static final MibLogger log = new MibLogger(JvmRTBootClassPathTableMetaImpl.class);
  
  public JvmRTBootClassPathTableMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer)
  {
    super(paramSnmpMib, paramSnmpStandardObjectServer);
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
      log.debug("*** **** **** **** getNextOid", "next=" + localSnmpOid);
    }
    if (localSnmpOid == null) {
      throw new SnmpStatusException(224);
    }
    return localSnmpOid;
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
    boolean bool = log.isDebugOn();
    if (bool) {
      log.debug("getEntry", "oid [" + paramSnmpOid + "]");
    }
    if ((paramSnmpOid == null) || (paramSnmpOid.getLength() != 1))
    {
      if (bool) {
        log.debug("getEntry", "Invalid oid [" + paramSnmpOid + "]");
      }
      throw new SnmpStatusException(224);
    }
    Map localMap = JvmContextFactory.getUserData();
    String str = "JvmRTBootClassPathTable.entry." + paramSnmpOid.toString();
    if (localMap != null)
    {
      localObject1 = localMap.get(str);
      if (localObject1 != null)
      {
        if (bool) {
          log.debug("getEntry", "Entry is already in the cache");
        }
        return localObject1;
      }
      if (bool) {
        log.debug("getEntry", "Entry is not in the cache");
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
    if (bool) {
      log.debug("getEntry", "data is a: " + localObject2.getClass().getName());
    }
    JvmRTBootClassPathEntryImpl localJvmRTBootClassPathEntryImpl = new JvmRTBootClassPathEntryImpl((String)localObject2, (int)paramSnmpOid.getOidArc(0));
    if ((localMap != null) && (localJvmRTBootClassPathEntryImpl != null)) {
      localMap.put(str, localJvmRTBootClassPathEntryImpl);
    }
    return localJvmRTBootClassPathEntryImpl;
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
      localSnmpTableHandler = (SnmpTableHandler)localMap.get("JvmRTBootClassPathTable.handler");
      if (localSnmpTableHandler != null) {
        return localSnmpTableHandler;
      }
    }
    SnmpTableHandler localSnmpTableHandler = cache.getTableHandler();
    if ((localMap != null) && (localSnmpTableHandler != null)) {
      localMap.put("JvmRTBootClassPathTable.handler", localSnmpTableHandler);
    }
    return localSnmpTableHandler;
  }
  
  private static class JvmRTBootClassPathTableCache
    extends SnmpTableCache
  {
    static final long serialVersionUID = -2637458695413646098L;
    private JvmRTBootClassPathTableMetaImpl meta;
    
    JvmRTBootClassPathTableCache(JvmRTBootClassPathTableMetaImpl paramJvmRTBootClassPathTableMetaImpl, long paramLong)
    {
      meta = paramJvmRTBootClassPathTableMetaImpl;
      validity = paramLong;
    }
    
    public SnmpTableHandler getTableHandler()
    {
      Map localMap = JvmContextFactory.getUserData();
      return getTableDatas(localMap);
    }
    
    protected SnmpCachedData updateCachedDatas(Object paramObject)
    {
      String[] arrayOfString = JvmRuntimeImpl.getBootClassPath(paramObject);
      long l = System.currentTimeMillis();
      int i = arrayOfString.length;
      SnmpOid[] arrayOfSnmpOid = new SnmpOid[i];
      for (int j = 0; j < i; j++) {
        arrayOfSnmpOid[j] = new SnmpOid(j + 1);
      }
      return new SnmpCachedData(l, arrayOfSnmpOid, arrayOfString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmRTBootClassPathTableMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */