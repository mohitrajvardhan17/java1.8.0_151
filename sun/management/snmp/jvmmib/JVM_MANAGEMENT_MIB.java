package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import java.util.Hashtable;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class JVM_MANAGEMENT_MIB
  extends SnmpMib
  implements Serializable
{
  static final long serialVersionUID = 6895037919735816732L;
  private boolean isInitialized = false;
  protected SnmpStandardObjectServer objectserver;
  protected final Hashtable<String, SnmpMibTable> metadatas = new Hashtable();
  
  public JVM_MANAGEMENT_MIB()
  {
    mibName = "JVM_MANAGEMENT_MIB";
  }
  
  public void init()
    throws IllegalAccessException
  {
    if (isInitialized == true) {
      return;
    }
    try
    {
      populate(null, null);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw localIllegalAccessException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      throw new Error(localException.getMessage());
    }
    isInitialized = true;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    if (isInitialized == true) {
      throw new InstanceAlreadyExistsException();
    }
    server = paramMBeanServer;
    populate(paramMBeanServer, paramObjectName);
    isInitialized = true;
    return paramObjectName;
  }
  
  public void populate(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    if (isInitialized == true) {
      return;
    }
    if (objectserver == null) {
      objectserver = new SnmpStandardObjectServer();
    }
    initJvmOS(paramMBeanServer);
    initJvmCompilation(paramMBeanServer);
    initJvmRuntime(paramMBeanServer);
    initJvmThreading(paramMBeanServer);
    initJvmMemory(paramMBeanServer);
    initJvmClassLoading(paramMBeanServer);
    isInitialized = true;
  }
  
  protected void initJvmOS(MBeanServer paramMBeanServer)
    throws Exception
  {
    String str = getGroupOid("JvmOS", "1.3.6.1.4.1.42.2.145.3.163.1.1.6");
    ObjectName localObjectName = null;
    if (paramMBeanServer != null) {
      localObjectName = getGroupObjectName("JvmOS", str, mibName + ":name=sun.management.snmp.jvmmib.JvmOS");
    }
    JvmOSMeta localJvmOSMeta = createJvmOSMetaNode("JvmOS", str, localObjectName, paramMBeanServer);
    if (localJvmOSMeta != null)
    {
      localJvmOSMeta.registerTableNodes(this, paramMBeanServer);
      JvmOSMBean localJvmOSMBean = (JvmOSMBean)createJvmOSMBean("JvmOS", str, localObjectName, paramMBeanServer);
      localJvmOSMeta.setInstance(localJvmOSMBean);
      registerGroupNode("JvmOS", str, localObjectName, localJvmOSMeta, localJvmOSMBean, paramMBeanServer);
    }
  }
  
  protected JvmOSMeta createJvmOSMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
  {
    return new JvmOSMeta(this, objectserver);
  }
  
  protected abstract Object createJvmOSMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmCompilation(MBeanServer paramMBeanServer)
    throws Exception
  {
    String str = getGroupOid("JvmCompilation", "1.3.6.1.4.1.42.2.145.3.163.1.1.5");
    ObjectName localObjectName = null;
    if (paramMBeanServer != null) {
      localObjectName = getGroupObjectName("JvmCompilation", str, mibName + ":name=sun.management.snmp.jvmmib.JvmCompilation");
    }
    JvmCompilationMeta localJvmCompilationMeta = createJvmCompilationMetaNode("JvmCompilation", str, localObjectName, paramMBeanServer);
    if (localJvmCompilationMeta != null)
    {
      localJvmCompilationMeta.registerTableNodes(this, paramMBeanServer);
      JvmCompilationMBean localJvmCompilationMBean = (JvmCompilationMBean)createJvmCompilationMBean("JvmCompilation", str, localObjectName, paramMBeanServer);
      localJvmCompilationMeta.setInstance(localJvmCompilationMBean);
      registerGroupNode("JvmCompilation", str, localObjectName, localJvmCompilationMeta, localJvmCompilationMBean, paramMBeanServer);
    }
  }
  
  protected JvmCompilationMeta createJvmCompilationMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
  {
    return new JvmCompilationMeta(this, objectserver);
  }
  
  protected abstract Object createJvmCompilationMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmRuntime(MBeanServer paramMBeanServer)
    throws Exception
  {
    String str = getGroupOid("JvmRuntime", "1.3.6.1.4.1.42.2.145.3.163.1.1.4");
    ObjectName localObjectName = null;
    if (paramMBeanServer != null) {
      localObjectName = getGroupObjectName("JvmRuntime", str, mibName + ":name=sun.management.snmp.jvmmib.JvmRuntime");
    }
    JvmRuntimeMeta localJvmRuntimeMeta = createJvmRuntimeMetaNode("JvmRuntime", str, localObjectName, paramMBeanServer);
    if (localJvmRuntimeMeta != null)
    {
      localJvmRuntimeMeta.registerTableNodes(this, paramMBeanServer);
      JvmRuntimeMBean localJvmRuntimeMBean = (JvmRuntimeMBean)createJvmRuntimeMBean("JvmRuntime", str, localObjectName, paramMBeanServer);
      localJvmRuntimeMeta.setInstance(localJvmRuntimeMBean);
      registerGroupNode("JvmRuntime", str, localObjectName, localJvmRuntimeMeta, localJvmRuntimeMBean, paramMBeanServer);
    }
  }
  
  protected JvmRuntimeMeta createJvmRuntimeMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
  {
    return new JvmRuntimeMeta(this, objectserver);
  }
  
  protected abstract Object createJvmRuntimeMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmThreading(MBeanServer paramMBeanServer)
    throws Exception
  {
    String str = getGroupOid("JvmThreading", "1.3.6.1.4.1.42.2.145.3.163.1.1.3");
    ObjectName localObjectName = null;
    if (paramMBeanServer != null) {
      localObjectName = getGroupObjectName("JvmThreading", str, mibName + ":name=sun.management.snmp.jvmmib.JvmThreading");
    }
    JvmThreadingMeta localJvmThreadingMeta = createJvmThreadingMetaNode("JvmThreading", str, localObjectName, paramMBeanServer);
    if (localJvmThreadingMeta != null)
    {
      localJvmThreadingMeta.registerTableNodes(this, paramMBeanServer);
      JvmThreadingMBean localJvmThreadingMBean = (JvmThreadingMBean)createJvmThreadingMBean("JvmThreading", str, localObjectName, paramMBeanServer);
      localJvmThreadingMeta.setInstance(localJvmThreadingMBean);
      registerGroupNode("JvmThreading", str, localObjectName, localJvmThreadingMeta, localJvmThreadingMBean, paramMBeanServer);
    }
  }
  
  protected JvmThreadingMeta createJvmThreadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
  {
    return new JvmThreadingMeta(this, objectserver);
  }
  
  protected abstract Object createJvmThreadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmMemory(MBeanServer paramMBeanServer)
    throws Exception
  {
    String str = getGroupOid("JvmMemory", "1.3.6.1.4.1.42.2.145.3.163.1.1.2");
    ObjectName localObjectName = null;
    if (paramMBeanServer != null) {
      localObjectName = getGroupObjectName("JvmMemory", str, mibName + ":name=sun.management.snmp.jvmmib.JvmMemory");
    }
    JvmMemoryMeta localJvmMemoryMeta = createJvmMemoryMetaNode("JvmMemory", str, localObjectName, paramMBeanServer);
    if (localJvmMemoryMeta != null)
    {
      localJvmMemoryMeta.registerTableNodes(this, paramMBeanServer);
      JvmMemoryMBean localJvmMemoryMBean = (JvmMemoryMBean)createJvmMemoryMBean("JvmMemory", str, localObjectName, paramMBeanServer);
      localJvmMemoryMeta.setInstance(localJvmMemoryMBean);
      registerGroupNode("JvmMemory", str, localObjectName, localJvmMemoryMeta, localJvmMemoryMBean, paramMBeanServer);
    }
  }
  
  protected JvmMemoryMeta createJvmMemoryMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
  {
    return new JvmMemoryMeta(this, objectserver);
  }
  
  protected abstract Object createJvmMemoryMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmClassLoading(MBeanServer paramMBeanServer)
    throws Exception
  {
    String str = getGroupOid("JvmClassLoading", "1.3.6.1.4.1.42.2.145.3.163.1.1.1");
    ObjectName localObjectName = null;
    if (paramMBeanServer != null) {
      localObjectName = getGroupObjectName("JvmClassLoading", str, mibName + ":name=sun.management.snmp.jvmmib.JvmClassLoading");
    }
    JvmClassLoadingMeta localJvmClassLoadingMeta = createJvmClassLoadingMetaNode("JvmClassLoading", str, localObjectName, paramMBeanServer);
    if (localJvmClassLoadingMeta != null)
    {
      localJvmClassLoadingMeta.registerTableNodes(this, paramMBeanServer);
      JvmClassLoadingMBean localJvmClassLoadingMBean = (JvmClassLoadingMBean)createJvmClassLoadingMBean("JvmClassLoading", str, localObjectName, paramMBeanServer);
      localJvmClassLoadingMeta.setInstance(localJvmClassLoadingMBean);
      registerGroupNode("JvmClassLoading", str, localObjectName, localJvmClassLoadingMeta, localJvmClassLoadingMBean, paramMBeanServer);
    }
  }
  
  protected JvmClassLoadingMeta createJvmClassLoadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer)
  {
    return new JvmClassLoadingMeta(this, objectserver);
  }
  
  protected abstract Object createJvmClassLoadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  public void registerTableMeta(String paramString, SnmpMibTable paramSnmpMibTable)
  {
    if (metadatas == null) {
      return;
    }
    if (paramString == null) {
      return;
    }
    metadatas.put(paramString, paramSnmpMibTable);
  }
  
  public SnmpMibTable getRegisteredTableMeta(String paramString)
  {
    if (metadatas == null) {
      return null;
    }
    if (paramString == null) {
      return null;
    }
    return (SnmpMibTable)metadatas.get(paramString);
  }
  
  public SnmpStandardObjectServer getStandardObjectServer()
  {
    if (objectserver == null) {
      objectserver = new SnmpStandardObjectServer();
    }
    return objectserver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JVM_MANAGEMENT_MIB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */