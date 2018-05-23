package com.sun.jmx.snmp.internal;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpEngineFactory;
import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUsmKeyHandler;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpEngineImpl
  implements SnmpEngine, Serializable
{
  private static final long serialVersionUID = -2564301391365614725L;
  public static final int noAuthNoPriv = 0;
  public static final int authNoPriv = 1;
  public static final int authPriv = 3;
  public static final int reportableFlag = 4;
  public static final int authMask = 1;
  public static final int privMask = 2;
  public static final int authPrivMask = 3;
  private SnmpEngineId engineid = null;
  private SnmpEngineFactory factory = null;
  private long startTime = 0L;
  private int boot = 0;
  private boolean checkOid = false;
  private transient SnmpUsmKeyHandler usmKeyHandler = null;
  private transient SnmpLcd lcd = null;
  private transient SnmpSecuritySubSystem securitySub = null;
  private transient SnmpMsgProcessingSubSystem messageSub = null;
  private transient SnmpAccessControlSubSystem accessSub = null;
  
  public synchronized int getEngineTime()
  {
    long l = System.currentTimeMillis() / 1000L - startTime;
    if (l > 2147483647L)
    {
      startTime = (System.currentTimeMillis() / 1000L);
      if (boot != Integer.MAX_VALUE) {
        boot += 1;
      }
      storeNBBoots(boot);
    }
    return (int)(System.currentTimeMillis() / 1000L - startTime);
  }
  
  public SnmpEngineId getEngineId()
  {
    return engineid;
  }
  
  public SnmpUsmKeyHandler getUsmKeyHandler()
  {
    return usmKeyHandler;
  }
  
  public SnmpLcd getLcd()
  {
    return lcd;
  }
  
  public int getEngineBoots()
  {
    return boot;
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd, SnmpEngineId paramSnmpEngineId)
    throws UnknownHostException
  {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (engineid == null) {
      if (paramSnmpEngineId != null) {
        engineid = paramSnmpEngineId;
      } else {
        engineid = SnmpEngineId.createEngineId();
      }
    }
    paramSnmpLcd.storeEngineId(engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,SnmpEngineId)", "LOCAL ENGINE ID: " + engineid);
    }
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd, InetAddress paramInetAddress, int paramInt)
    throws UnknownHostException
  {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (engineid == null) {
      engineid = SnmpEngineId.createEngineId(paramInetAddress, paramInt);
    }
    paramSnmpLcd.storeEngineId(engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,InetAddress,int)", "LOCAL ENGINE ID: " + engineid + " / LOCAL ENGINE NB BOOTS: " + boot + " / LOCAL ENGINE START TIME: " + getEngineTime());
    }
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd, int paramInt)
    throws UnknownHostException
  {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (engineid == null) {
      engineid = SnmpEngineId.createEngineId(paramInt);
    }
    paramSnmpLcd.storeEngineId(engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,int)", "LOCAL ENGINE ID: " + engineid + " / LOCAL ENGINE NB BOOTS: " + boot + " / LOCAL ENGINE START TIME: " + getEngineTime());
    }
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd)
    throws UnknownHostException
  {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (engineid == null) {
      engineid = SnmpEngineId.createEngineId();
    }
    paramSnmpLcd.storeEngineId(engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd)", "LOCAL ENGINE ID: " + engineid + " / LOCAL ENGINE NB BOOTS: " + boot + " / LOCAL ENGINE START TIME: " + getEngineTime());
    }
  }
  
  public synchronized void activateCheckOid()
  {
    checkOid = true;
  }
  
  public synchronized void deactivateCheckOid()
  {
    checkOid = false;
  }
  
  public synchronized boolean isCheckOidActivated()
  {
    return checkOid;
  }
  
  private void storeNBBoots(int paramInt)
  {
    if ((paramInt < 0) || (paramInt == Integer.MAX_VALUE))
    {
      paramInt = Integer.MAX_VALUE;
      lcd.storeEngineBoots(paramInt);
    }
    else
    {
      lcd.storeEngineBoots(paramInt + 1);
    }
  }
  
  private void init(SnmpLcd paramSnmpLcd, SnmpEngineFactory paramSnmpEngineFactory)
  {
    factory = paramSnmpEngineFactory;
    lcd = paramSnmpLcd;
    boot = paramSnmpLcd.getEngineBoots();
    if ((boot == -1) || (boot == 0)) {
      boot = 1;
    }
    storeNBBoots(boot);
    startTime = (System.currentTimeMillis() / 1000L);
  }
  
  void setUsmKeyHandler(SnmpUsmKeyHandler paramSnmpUsmKeyHandler)
  {
    usmKeyHandler = paramSnmpUsmKeyHandler;
  }
  
  private void initEngineID()
    throws UnknownHostException
  {
    String str = lcd.getEngineId();
    if (str != null) {
      engineid = SnmpEngineId.createEngineId(str);
    }
  }
  
  public SnmpMsgProcessingSubSystem getMsgProcessingSubSystem()
  {
    return messageSub;
  }
  
  public void setMsgProcessingSubSystem(SnmpMsgProcessingSubSystem paramSnmpMsgProcessingSubSystem)
  {
    messageSub = paramSnmpMsgProcessingSubSystem;
  }
  
  public SnmpSecuritySubSystem getSecuritySubSystem()
  {
    return securitySub;
  }
  
  public void setSecuritySubSystem(SnmpSecuritySubSystem paramSnmpSecuritySubSystem)
  {
    securitySub = paramSnmpSecuritySubSystem;
  }
  
  public void setAccessControlSubSystem(SnmpAccessControlSubSystem paramSnmpAccessControlSubSystem)
  {
    accessSub = paramSnmpAccessControlSubSystem;
  }
  
  public SnmpAccessControlSubSystem getAccessControlSubSystem()
  {
    return accessSub;
  }
  
  public static void checkSecurityLevel(byte paramByte)
    throws SnmpBadSecurityLevelException
  {
    int i = paramByte & 0x3;
    if (((i & 0x2) != 0) && ((i & 0x1) == 0)) {
      throw new SnmpBadSecurityLevelException("Security level: noAuthPriv!!!");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpEngineImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */