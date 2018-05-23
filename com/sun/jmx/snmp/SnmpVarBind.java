package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpVarBind
  implements SnmpDataTypeEnums, Cloneable, Serializable
{
  private static final long serialVersionUID = 491778383240759376L;
  private static final String[] statusLegend = { "Status Mapper", "Value not initialized", "Valid Value", "No such object", "No such Instance", "End of Mib View" };
  public static final int stValueUnspecified = 1;
  public static final int stValueOk = 2;
  public static final int stValueNoSuchObject = 3;
  public static final int stValueNoSuchInstance = 4;
  public static final int stValueEndOfMibView = 5;
  public static final SnmpNull noSuchObject = new SnmpNull(128);
  public static final SnmpNull noSuchInstance = new SnmpNull(129);
  public static final SnmpNull endOfMibView = new SnmpNull(130);
  public SnmpOid oid = null;
  public SnmpValue value = null;
  public int status = 1;
  
  public SnmpVarBind() {}
  
  public SnmpVarBind(SnmpOid paramSnmpOid)
  {
    oid = paramSnmpOid;
  }
  
  public SnmpVarBind(SnmpOid paramSnmpOid, SnmpValue paramSnmpValue)
  {
    oid = paramSnmpOid;
    setSnmpValue(paramSnmpValue);
  }
  
  public SnmpVarBind(String paramString)
    throws SnmpStatusException
  {
    if (paramString.startsWith(".")) {
      oid = new SnmpOid(paramString);
    } else {
      try
      {
        int i = paramString.indexOf('.');
        handleLong(paramString, i);
        oid = new SnmpOid(paramString);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        int j = paramString.indexOf('.');
        SnmpOidRecord localSnmpOidRecord;
        if (j <= 0)
        {
          localSnmpOidRecord = resolveVarName(paramString);
          oid = new SnmpOid(localSnmpOidRecord.getName());
        }
        else
        {
          localSnmpOidRecord = resolveVarName(paramString.substring(0, j));
          oid = new SnmpOid(localSnmpOidRecord.getName() + paramString.substring(j));
        }
      }
    }
  }
  
  public final SnmpOid getOid()
  {
    return oid;
  }
  
  public final void setOid(SnmpOid paramSnmpOid)
  {
    oid = paramSnmpOid;
    clearValue();
  }
  
  public final synchronized SnmpValue getSnmpValue()
  {
    return value;
  }
  
  public final void setSnmpValue(SnmpValue paramSnmpValue)
  {
    value = paramSnmpValue;
    setValueValid();
  }
  
  public final SnmpCounter64 getSnmpCounter64Value()
    throws ClassCastException
  {
    return (SnmpCounter64)value;
  }
  
  public final void setSnmpCounter64Value(long paramLong)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpCounter64(paramLong);
    setValueValid();
  }
  
  public final SnmpInt getSnmpIntValue()
    throws ClassCastException
  {
    return (SnmpInt)value;
  }
  
  public final void setSnmpIntValue(long paramLong)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpInt(paramLong);
    setValueValid();
  }
  
  public final SnmpCounter getSnmpCounterValue()
    throws ClassCastException
  {
    return (SnmpCounter)value;
  }
  
  public final void setSnmpCounterValue(long paramLong)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpCounter(paramLong);
    setValueValid();
  }
  
  public final SnmpGauge getSnmpGaugeValue()
    throws ClassCastException
  {
    return (SnmpGauge)value;
  }
  
  public final void setSnmpGaugeValue(long paramLong)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpGauge(paramLong);
    setValueValid();
  }
  
  public final SnmpTimeticks getSnmpTimeticksValue()
    throws ClassCastException
  {
    return (SnmpTimeticks)value;
  }
  
  public final void setSnmpTimeticksValue(long paramLong)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpTimeticks(paramLong);
    setValueValid();
  }
  
  public final SnmpOid getSnmpOidValue()
    throws ClassCastException
  {
    return (SnmpOid)value;
  }
  
  public final void setSnmpOidValue(String paramString)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpOid(paramString);
    setValueValid();
  }
  
  public final SnmpIpAddress getSnmpIpAddressValue()
    throws ClassCastException
  {
    return (SnmpIpAddress)value;
  }
  
  public final void setSnmpIpAddressValue(String paramString)
    throws IllegalArgumentException
  {
    clearValue();
    value = new SnmpIpAddress(paramString);
    setValueValid();
  }
  
  public final SnmpString getSnmpStringValue()
    throws ClassCastException
  {
    return (SnmpString)value;
  }
  
  public final void setSnmpStringValue(String paramString)
  {
    clearValue();
    value = new SnmpString(paramString);
    setValueValid();
  }
  
  public final SnmpOpaque getSnmpOpaqueValue()
    throws ClassCastException
  {
    return (SnmpOpaque)value;
  }
  
  public final void setSnmpOpaqueValue(byte[] paramArrayOfByte)
  {
    clearValue();
    value = new SnmpOpaque(paramArrayOfByte);
    setValueValid();
  }
  
  public final SnmpStringFixed getSnmpStringFixedValue()
    throws ClassCastException
  {
    return (SnmpStringFixed)value;
  }
  
  public final void setSnmpStringFixedValue(String paramString)
  {
    clearValue();
    value = new SnmpStringFixed(paramString);
    setValueValid();
  }
  
  public SnmpOidRecord resolveVarName(String paramString)
    throws SnmpStatusException
  {
    SnmpOidTable localSnmpOidTable = SnmpOid.getSnmpOidTable();
    if (localSnmpOidTable == null) {
      throw new SnmpStatusException(2);
    }
    int i = paramString.indexOf('.');
    if (i < 0) {
      return localSnmpOidTable.resolveVarName(paramString);
    }
    return localSnmpOidTable.resolveVarOid(paramString);
  }
  
  public final int getValueStatus()
  {
    return status;
  }
  
  public final String getValueStatusLegend()
  {
    return statusLegend[status];
  }
  
  public final boolean isValidValue()
  {
    return status == 2;
  }
  
  public final boolean isUnspecifiedValue()
  {
    return status == 1;
  }
  
  public final void clearValue()
  {
    value = null;
    status = 1;
  }
  
  public final boolean isOidEqual(SnmpVarBind paramSnmpVarBind)
  {
    return oid.equals(oid);
  }
  
  public final void addInstance(long paramLong)
  {
    oid.append(paramLong);
  }
  
  public final void addInstance(long[] paramArrayOfLong)
    throws SnmpStatusException
  {
    oid.addToOid(paramArrayOfLong);
  }
  
  public final void addInstance(String paramString)
    throws SnmpStatusException
  {
    if (paramString != null) {
      oid.addToOid(paramString);
    }
  }
  
  public void insertInOid(int paramInt)
  {
    oid.insert(paramInt);
  }
  
  public void appendInOid(SnmpOid paramSnmpOid)
  {
    oid.append(paramSnmpOid);
  }
  
  public final synchronized boolean hasVarBindException()
  {
    switch (status)
    {
    case 1: 
    case 3: 
    case 4: 
    case 5: 
      return true;
    }
    return false;
  }
  
  public void copyValueAndOid(SnmpVarBind paramSnmpVarBind)
  {
    setOid((SnmpOid)oid.clone());
    copyValue(paramSnmpVarBind);
  }
  
  public void copyValue(SnmpVarBind paramSnmpVarBind)
  {
    if (paramSnmpVarBind.isValidValue())
    {
      value = paramSnmpVarBind.getSnmpValue().duplicate();
      setValueValid();
    }
    else
    {
      status = paramSnmpVarBind.getValueStatus();
      if (status == 5) {
        value = endOfMibView;
      } else if (status == 3) {
        value = noSuchObject;
      } else if (status == 4) {
        value = noSuchInstance;
      }
    }
  }
  
  public Object cloneWithoutValue()
  {
    SnmpOid localSnmpOid = (SnmpOid)oid.clone();
    return new SnmpVarBind(localSnmpOid);
  }
  
  public SnmpVarBind clone()
  {
    SnmpVarBind localSnmpVarBind = new SnmpVarBind();
    localSnmpVarBind.copyValueAndOid(this);
    return localSnmpVarBind;
  }
  
  public final String getStringValue()
  {
    return value.toString();
  }
  
  public final void setNoSuchObject()
  {
    value = noSuchObject;
    status = 3;
  }
  
  public final void setNoSuchInstance()
  {
    value = noSuchInstance;
    status = 4;
  }
  
  public final void setEndOfMibView()
  {
    value = endOfMibView;
    status = 5;
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(400);
    localStringBuilder.append("Object ID : ").append(oid.toString());
    if (isValidValue())
    {
      localStringBuilder.append("  (Syntax : ").append(value.getTypeName()).append(")\n");
      localStringBuilder.append("Value : ").append(value.toString());
    }
    else
    {
      localStringBuilder.append("\nValue Exception : ").append(getValueStatusLegend());
    }
    return localStringBuilder.toString();
  }
  
  private void setValueValid()
  {
    if (value == endOfMibView) {
      status = 5;
    } else if (value == noSuchObject) {
      status = 3;
    } else if (value == noSuchInstance) {
      status = 4;
    } else {
      status = 2;
    }
  }
  
  private void handleLong(String paramString, int paramInt)
    throws NumberFormatException, SnmpStatusException
  {
    String str;
    if (paramInt > 0) {
      str = paramString.substring(0, paramInt);
    } else {
      str = paramString;
    }
    Long.parseLong(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpVarBind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */