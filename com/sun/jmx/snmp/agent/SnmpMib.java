package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public abstract class SnmpMib
  extends SnmpMibAgent
  implements Serializable
{
  protected SnmpMibOid root = new SnmpMibOid();
  private transient long[] rootOid = null;
  
  public SnmpMib() {}
  
  protected String getGroupOid(String paramString1, String paramString2)
  {
    return paramString2;
  }
  
  protected ObjectName getGroupObjectName(String paramString1, String paramString2, String paramString3)
    throws MalformedObjectNameException
  {
    return new ObjectName(paramString3);
  }
  
  protected void registerGroupNode(String paramString1, String paramString2, ObjectName paramObjectName, SnmpMibNode paramSnmpMibNode, Object paramObject, MBeanServer paramMBeanServer)
    throws NotCompliantMBeanException, MBeanRegistrationException, InstanceAlreadyExistsException, IllegalAccessException
  {
    root.registerNode(paramString2, paramSnmpMibNode);
    if ((paramMBeanServer != null) && (paramObjectName != null) && (paramObject != null)) {
      paramMBeanServer.registerMBean(paramObject, paramObjectName);
    }
  }
  
  public abstract void registerTableMeta(String paramString, SnmpMibTable paramSnmpMibTable);
  
  public abstract SnmpMibTable getRegisteredTableMeta(String paramString);
  
  public void get(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    SnmpRequestTree localSnmpRequestTree = getHandlers(paramSnmpMibRequest, false, false, 160);
    SnmpRequestTree.Handler localHandler = null;
    SnmpMibNode localSnmpMibNode = null;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "get", "Processing handlers for GET... ");
    }
    Enumeration localEnumeration1 = localSnmpRequestTree.getHandlers();
    while (localEnumeration1.hasMoreElements())
    {
      localHandler = (SnmpRequestTree.Handler)localEnumeration1.nextElement();
      localSnmpMibNode = localSnmpRequestTree.getMetaNode(localHandler);
      int i = localSnmpRequestTree.getOidDepth(localHandler);
      Enumeration localEnumeration2 = localSnmpRequestTree.getSubRequests(localHandler);
      while (localEnumeration2.hasMoreElements()) {
        localSnmpMibNode.get((SnmpMibSubRequest)localEnumeration2.nextElement(), i);
      }
    }
  }
  
  public void set(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    SnmpRequestTree localSnmpRequestTree = null;
    if ((paramSnmpMibRequest instanceof SnmpMibRequestImpl)) {
      localSnmpRequestTree = ((SnmpMibRequestImpl)paramSnmpMibRequest).getRequestTree();
    }
    if (localSnmpRequestTree == null) {
      localSnmpRequestTree = getHandlers(paramSnmpMibRequest, false, true, 163);
    }
    localSnmpRequestTree.switchCreationFlag(false);
    localSnmpRequestTree.setPduType(163);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "set", "Processing handlers for SET... ");
    }
    Enumeration localEnumeration1 = localSnmpRequestTree.getHandlers();
    while (localEnumeration1.hasMoreElements())
    {
      SnmpRequestTree.Handler localHandler = (SnmpRequestTree.Handler)localEnumeration1.nextElement();
      SnmpMibNode localSnmpMibNode = localSnmpRequestTree.getMetaNode(localHandler);
      int i = localSnmpRequestTree.getOidDepth(localHandler);
      Enumeration localEnumeration2 = localSnmpRequestTree.getSubRequests(localHandler);
      while (localEnumeration2.hasMoreElements()) {
        localSnmpMibNode.set((SnmpMibSubRequest)localEnumeration2.nextElement(), i);
      }
    }
  }
  
  public void check(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    SnmpRequestTree localSnmpRequestTree = getHandlers(paramSnmpMibRequest, true, true, 253);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "check", "Processing handlers for CHECK... ");
    }
    Enumeration localEnumeration1 = localSnmpRequestTree.getHandlers();
    while (localEnumeration1.hasMoreElements())
    {
      SnmpRequestTree.Handler localHandler = (SnmpRequestTree.Handler)localEnumeration1.nextElement();
      SnmpMibNode localSnmpMibNode = localSnmpRequestTree.getMetaNode(localHandler);
      int i = localSnmpRequestTree.getOidDepth(localHandler);
      Enumeration localEnumeration2 = localSnmpRequestTree.getSubRequests(localHandler);
      while (localEnumeration2.hasMoreElements()) {
        localSnmpMibNode.check((SnmpMibSubRequest)localEnumeration2.nextElement(), i);
      }
    }
    if ((paramSnmpMibRequest instanceof SnmpMibRequestImpl)) {
      ((SnmpMibRequestImpl)paramSnmpMibRequest).setRequestTree(localSnmpRequestTree);
    }
  }
  
  public void getNext(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    SnmpRequestTree localSnmpRequestTree = getGetNextHandlers(paramSnmpMibRequest);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getNext", "Processing handlers for GET-NEXT... ");
    }
    Enumeration localEnumeration1 = localSnmpRequestTree.getHandlers();
    while (localEnumeration1.hasMoreElements())
    {
      SnmpRequestTree.Handler localHandler = (SnmpRequestTree.Handler)localEnumeration1.nextElement();
      SnmpMibNode localSnmpMibNode = localSnmpRequestTree.getMetaNode(localHandler);
      int i = localSnmpRequestTree.getOidDepth(localHandler);
      Enumeration localEnumeration2 = localSnmpRequestTree.getSubRequests(localHandler);
      while (localEnumeration2.hasMoreElements()) {
        localSnmpMibNode.get((SnmpMibSubRequest)localEnumeration2.nextElement(), i);
      }
    }
  }
  
  public void getBulk(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    getBulkWithGetNext(paramSnmpMibRequest, paramInt1, paramInt2);
  }
  
  public long[] getRootOid()
  {
    if (rootOid == null)
    {
      Vector localVector = new Vector(10);
      root.getRootOid(localVector);
      rootOid = new long[localVector.size()];
      int i = 0;
      Enumeration localEnumeration = localVector.elements();
      while (localEnumeration.hasMoreElements())
      {
        Integer localInteger = (Integer)localEnumeration.nextElement();
        rootOid[(i++)] = localInteger.longValue();
      }
    }
    return (long[])rootOid.clone();
  }
  
  private SnmpRequestTree getHandlers(SnmpMibRequest paramSnmpMibRequest, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    throws SnmpStatusException
  {
    SnmpRequestTree localSnmpRequestTree = new SnmpRequestTree(paramSnmpMibRequest, paramBoolean1, paramInt);
    int i = 0;
    int j = paramSnmpMibRequest.getVersion();
    Enumeration localEnumeration = paramSnmpMibRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      try
      {
        root.findHandlingNode(localSnmpVarBind, oid.longValue(false), 0, localSnmpRequestTree);
      }
      catch (SnmpStatusException localSnmpStatusException1)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "Couldn't find a handling node for " + oid.toString());
        }
        if (j == 0)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tV1: Throwing exception");
          }
          SnmpStatusException localSnmpStatusException2 = new SnmpStatusException(localSnmpStatusException1, i + 1);
          localSnmpStatusException2.initCause(localSnmpStatusException1);
          throw localSnmpStatusException2;
        }
        SnmpStatusException localSnmpStatusException4;
        if ((paramInt == 253) || (paramInt == 163))
        {
          int k = SnmpRequestTree.mapSetException(localSnmpStatusException1.getStatus(), j);
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tSET: Throwing exception");
          }
          localSnmpStatusException4 = new SnmpStatusException(k, i + 1);
          localSnmpStatusException4.initCause(localSnmpStatusException1);
          throw localSnmpStatusException4;
        }
        if (paramBoolean2)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tATOMIC: Throwing exception");
          }
          SnmpStatusException localSnmpStatusException3 = new SnmpStatusException(localSnmpStatusException1, i + 1);
          localSnmpStatusException3.initCause(localSnmpStatusException1);
          throw localSnmpStatusException3;
        }
        int m = SnmpRequestTree.mapGetException(localSnmpStatusException1.getStatus(), j);
        if (m == 224)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering noSuchInstance");
          }
          value = SnmpVarBind.noSuchInstance;
        }
        else if (m == 225)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering noSuchObject");
          }
          value = SnmpVarBind.noSuchObject;
        }
        else
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getHandlers", "\tGET: Registering global error: " + m);
          }
          localSnmpStatusException4 = new SnmpStatusException(m, i + 1);
          localSnmpStatusException4.initCause(localSnmpStatusException1);
          throw localSnmpStatusException4;
        }
      }
      i++;
    }
    return localSnmpRequestTree;
  }
  
  private SnmpRequestTree getGetNextHandlers(SnmpMibRequest paramSnmpMibRequest)
    throws SnmpStatusException
  {
    SnmpRequestTree localSnmpRequestTree = new SnmpRequestTree(paramSnmpMibRequest, false, 161);
    localSnmpRequestTree.setGetNextFlag();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "Received MIB request : " + paramSnmpMibRequest);
    }
    AcmChecker localAcmChecker = new AcmChecker(paramSnmpMibRequest);
    int i = 0;
    SnmpVarBind localSnmpVarBind = null;
    int j = paramSnmpMibRequest.getVersion();
    Object localObject = null;
    Enumeration localEnumeration = paramSnmpMibRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      try
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", " Next OID of : " + oid);
        }
        SnmpOid localSnmpOid = new SnmpOid(root.findNextHandlingNode(localSnmpVarBind, oid.longValue(false), 0, 0, localSnmpRequestTree, localAcmChecker));
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", " is : " + localSnmpOid);
        }
        oid = localSnmpOid;
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        if (j == 0)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "\tThrowing exception " + localSnmpStatusException.toString());
          }
          throw new SnmpStatusException(localSnmpStatusException, i + 1);
        }
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "getGetNextHandlers", "Exception : " + localSnmpStatusException.getStatus());
        }
        localSnmpVarBind.setSnmpValue(SnmpVarBind.endOfMibView);
      }
      i++;
    }
    return localSnmpRequestTree;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpMib.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */