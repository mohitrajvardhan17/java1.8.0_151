package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUnknownModelLcdException;
import com.sun.jmx.snmp.SnmpUnknownSubSystemException;
import java.util.Hashtable;

public abstract class SnmpLcd
{
  private Hashtable<SnmpSubSystem, SubSysLcdManager> subs = new Hashtable();
  
  public SnmpLcd() {}
  
  public abstract int getEngineBoots();
  
  public abstract String getEngineId();
  
  public abstract void storeEngineBoots(int paramInt);
  
  public abstract void storeEngineId(SnmpEngineId paramSnmpEngineId);
  
  public void addModelLcd(SnmpSubSystem paramSnmpSubSystem, int paramInt, SnmpModelLcd paramSnmpModelLcd)
  {
    SubSysLcdManager localSubSysLcdManager = (SubSysLcdManager)subs.get(paramSnmpSubSystem);
    if (localSubSysLcdManager == null)
    {
      localSubSysLcdManager = new SubSysLcdManager();
      subs.put(paramSnmpSubSystem, localSubSysLcdManager);
    }
    localSubSysLcdManager.addModelLcd(paramInt, paramSnmpModelLcd);
  }
  
  public void removeModelLcd(SnmpSubSystem paramSnmpSubSystem, int paramInt)
    throws SnmpUnknownModelLcdException, SnmpUnknownSubSystemException
  {
    SubSysLcdManager localSubSysLcdManager = (SubSysLcdManager)subs.get(paramSnmpSubSystem);
    if (localSubSysLcdManager != null)
    {
      SnmpModelLcd localSnmpModelLcd = localSubSysLcdManager.removeModelLcd(paramInt);
      if (localSnmpModelLcd == null) {
        throw new SnmpUnknownModelLcdException("Model : " + paramInt);
      }
    }
    else
    {
      throw new SnmpUnknownSubSystemException(paramSnmpSubSystem.toString());
    }
  }
  
  public SnmpModelLcd getModelLcd(SnmpSubSystem paramSnmpSubSystem, int paramInt)
  {
    SubSysLcdManager localSubSysLcdManager = (SubSysLcdManager)subs.get(paramSnmpSubSystem);
    if (localSubSysLcdManager == null) {
      return null;
    }
    return localSubSysLcdManager.getModelLcd(paramInt);
  }
  
  class SubSysLcdManager
  {
    private Hashtable<Integer, SnmpModelLcd> models = new Hashtable();
    
    SubSysLcdManager() {}
    
    public void addModelLcd(int paramInt, SnmpModelLcd paramSnmpModelLcd)
    {
      models.put(new Integer(paramInt), paramSnmpModelLcd);
    }
    
    public SnmpModelLcd getModelLcd(int paramInt)
    {
      return (SnmpModelLcd)models.get(new Integer(paramInt));
    }
    
    public SnmpModelLcd removeModelLcd(int paramInt)
    {
      return (SnmpModelLcd)models.remove(new Integer(paramInt));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpLcd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */