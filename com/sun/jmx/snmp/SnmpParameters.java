package com.sun.jmx.snmp;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class SnmpParameters
  extends SnmpParams
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = -1822462497931733790L;
  static final String defaultRdCommunity = "public";
  private int _protocolVersion = 0;
  private String _readCommunity;
  private String _writeCommunity;
  private String _informCommunity;
  
  public SnmpParameters()
  {
    _readCommunity = "public";
    _informCommunity = "public";
  }
  
  public SnmpParameters(String paramString1, String paramString2)
  {
    _readCommunity = paramString1;
    _writeCommunity = paramString2;
    _informCommunity = "public";
  }
  
  public SnmpParameters(String paramString1, String paramString2, String paramString3)
  {
    _readCommunity = paramString1;
    _writeCommunity = paramString2;
    _informCommunity = paramString3;
  }
  
  public String getRdCommunity()
  {
    return _readCommunity;
  }
  
  public synchronized void setRdCommunity(String paramString)
  {
    if (paramString == null) {
      _readCommunity = "public";
    } else {
      _readCommunity = paramString;
    }
  }
  
  public String getWrCommunity()
  {
    return _writeCommunity;
  }
  
  public void setWrCommunity(String paramString)
  {
    _writeCommunity = paramString;
  }
  
  public String getInformCommunity()
  {
    return _informCommunity;
  }
  
  public void setInformCommunity(String paramString)
  {
    if (paramString == null) {
      _informCommunity = "public";
    } else {
      _informCommunity = paramString;
    }
  }
  
  public boolean allowSnmpSets()
  {
    return _writeCommunity != null;
  }
  
  public synchronized boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SnmpParameters)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    SnmpParameters localSnmpParameters = (SnmpParameters)paramObject;
    return (_protocolVersion == _protocolVersion) && (_readCommunity.equals(_readCommunity));
  }
  
  public synchronized int hashCode()
  {
    return _protocolVersion * 31 ^ Objects.hashCode(_readCommunity);
  }
  
  public synchronized Object clone()
  {
    SnmpParameters localSnmpParameters = null;
    try
    {
      localSnmpParameters = (SnmpParameters)super.clone();
      _readCommunity = _readCommunity;
      _writeCommunity = _writeCommunity;
      _informCommunity = _informCommunity;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
    return localSnmpParameters;
  }
  
  public byte[] encodeAuthentication(int paramInt)
    throws SnmpStatusException
  {
    try
    {
      if (paramInt == 163) {
        return _writeCommunity.getBytes("8859_1");
      }
      if (paramInt == 166) {
        return _informCommunity.getBytes("8859_1");
      }
      return _readCommunity.getBytes("8859_1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new SnmpStatusException(localUnsupportedEncodingException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */