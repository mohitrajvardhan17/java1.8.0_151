package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Vector;

final class SnmpMibRequestImpl
  implements SnmpMibRequest
{
  private Vector<SnmpVarBind> varbinds;
  private int version;
  private Object data;
  private SnmpPdu reqPdu = null;
  private SnmpRequestTree tree = null;
  private SnmpEngine engine = null;
  private String principal = null;
  private int securityLevel = -1;
  private int securityModel = -1;
  private byte[] contextName = null;
  private byte[] accessContextName = null;
  
  public SnmpMibRequestImpl(SnmpEngine paramSnmpEngine, SnmpPdu paramSnmpPdu, Vector<SnmpVarBind> paramVector, int paramInt1, Object paramObject, String paramString, int paramInt2, int paramInt3, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    varbinds = paramVector;
    version = paramInt1;
    data = paramObject;
    reqPdu = paramSnmpPdu;
    engine = paramSnmpEngine;
    principal = paramString;
    securityLevel = paramInt2;
    securityModel = paramInt3;
    contextName = paramArrayOfByte1;
    accessContextName = paramArrayOfByte2;
  }
  
  public SnmpEngine getEngine()
  {
    return engine;
  }
  
  public String getPrincipal()
  {
    return principal;
  }
  
  public int getSecurityLevel()
  {
    return securityLevel;
  }
  
  public int getSecurityModel()
  {
    return securityModel;
  }
  
  public byte[] getContextName()
  {
    return contextName;
  }
  
  public byte[] getAccessContextName()
  {
    return accessContextName;
  }
  
  public final SnmpPdu getPdu()
  {
    return reqPdu;
  }
  
  public final Enumeration<SnmpVarBind> getElements()
  {
    return varbinds.elements();
  }
  
  public final Vector<SnmpVarBind> getSubList()
  {
    return varbinds;
  }
  
  public final int getSize()
  {
    if (varbinds == null) {
      return 0;
    }
    return varbinds.size();
  }
  
  public final int getVersion()
  {
    return version;
  }
  
  public final int getRequestPduVersion()
  {
    return reqPdu.version;
  }
  
  public final Object getUserData()
  {
    return data;
  }
  
  public final int getVarIndex(SnmpVarBind paramSnmpVarBind)
  {
    return varbinds.indexOf(paramSnmpVarBind);
  }
  
  public void addVarBind(SnmpVarBind paramSnmpVarBind)
  {
    varbinds.addElement(paramSnmpVarBind);
  }
  
  final void setRequestTree(SnmpRequestTree paramSnmpRequestTree)
  {
    tree = paramSnmpRequestTree;
  }
  
  final SnmpRequestTree getRequestTree()
  {
    return tree;
  }
  
  final Vector<SnmpVarBind> getVarbinds()
  {
    return varbinds;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibRequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */