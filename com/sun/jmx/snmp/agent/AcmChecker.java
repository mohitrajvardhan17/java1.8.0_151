package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpUnknownModelException;
import com.sun.jmx.snmp.internal.SnmpAccessControlModel;
import com.sun.jmx.snmp.internal.SnmpAccessControlSubSystem;
import com.sun.jmx.snmp.internal.SnmpEngineImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

class AcmChecker
{
  SnmpAccessControlModel model = null;
  String principal = null;
  int securityLevel = -1;
  int version = -1;
  int pduType = -1;
  int securityModel = -1;
  byte[] contextName = null;
  SnmpEngineImpl engine = null;
  LongList l = null;
  
  AcmChecker(SnmpMibRequest paramSnmpMibRequest)
  {
    engine = ((SnmpEngineImpl)paramSnmpMibRequest.getEngine());
    if ((engine != null) && (engine.isCheckOidActivated())) {
      try
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", "SNMP V3 Access Control to be done");
        }
        model = ((SnmpAccessControlModel)engine.getAccessControlSubSystem().getModel(3));
        principal = paramSnmpMibRequest.getPrincipal();
        securityLevel = paramSnmpMibRequest.getSecurityLevel();
        pduType = getPdutype;
        version = paramSnmpMibRequest.getRequestPduVersion();
        securityModel = paramSnmpMibRequest.getSecurityModel();
        contextName = paramSnmpMibRequest.getAccessContextName();
        l = new LongList();
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        {
          StringBuilder localStringBuilder = new StringBuilder().append("Will check oid for : principal : ").append(principal).append("; securityLevel : ").append(securityLevel).append("; pduType : ").append(pduType).append("; version : ").append(version).append("; securityModel : ").append(securityModel).append("; contextName : ").append(contextName);
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", localStringBuilder.toString());
        }
      }
      catch (SnmpUnknownModelException localSnmpUnknownModelException)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "AcmChecker(SnmpMibRequest)", "Unknown Model, no ACM check.");
        }
      }
    }
  }
  
  void add(int paramInt, long paramLong)
  {
    if (model != null) {
      l.add(paramInt, paramLong);
    }
  }
  
  void remove(int paramInt)
  {
    if (model != null) {
      l.remove(paramInt);
    }
  }
  
  void add(int paramInt1, long[] paramArrayOfLong, int paramInt2, int paramInt3)
  {
    if (model != null) {
      l.add(paramInt1, paramArrayOfLong, paramInt2, paramInt3);
    }
  }
  
  void remove(int paramInt1, int paramInt2)
  {
    if (model != null) {
      l.remove(paramInt1, paramInt2);
    }
  }
  
  void checkCurrentOid()
    throws SnmpStatusException
  {
    if (model != null)
    {
      SnmpOid localSnmpOid = new SnmpOid(l.toArray());
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(), "checkCurrentOid", "Checking access for : " + localSnmpOid);
      }
      model.checkAccess(version, principal, securityLevel, pduType, securityModel, contextName, localSnmpOid);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\AcmChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */