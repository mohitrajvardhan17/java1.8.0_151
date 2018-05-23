package com.sun.jmx.snmp;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SnmpPeer
  implements Serializable
{
  private static final long serialVersionUID = -5554565062847175999L;
  public static final int defaultSnmpRequestPktSize = 2048;
  public static final int defaultSnmpResponsePktSize = 8192;
  private int maxVarBindLimit = 25;
  private int portNum = 161;
  private int maxTries = 3;
  private int timeout = 3000;
  private SnmpPduFactory pduFactory = new SnmpPduFactoryBER();
  private long _maxrtt;
  private long _minrtt;
  private long _avgrtt;
  private SnmpParams _snmpParameter = new SnmpParameters();
  private InetAddress _devAddr = null;
  private int maxSnmpPacketSize = 2048;
  InetAddress[] _devAddrList = null;
  int _addrIndex = 0;
  private boolean customPduFactory = false;
  
  public SnmpPeer(String paramString)
    throws UnknownHostException
  {
    this(paramString, 161);
  }
  
  public SnmpPeer(InetAddress paramInetAddress, int paramInt)
  {
    _devAddr = paramInetAddress;
    portNum = paramInt;
  }
  
  public SnmpPeer(InetAddress paramInetAddress)
  {
    _devAddr = paramInetAddress;
  }
  
  public SnmpPeer(String paramString, int paramInt)
    throws UnknownHostException
  {
    useIPAddress(paramString);
    portNum = paramInt;
  }
  
  public final synchronized void useIPAddress(String paramString)
    throws UnknownHostException
  {
    _devAddr = InetAddress.getByName(paramString);
  }
  
  public final synchronized String ipAddressInUse()
  {
    byte[] arrayOfByte = _devAddr.getAddress();
    return (arrayOfByte[0] & 0xFF) + "." + (arrayOfByte[1] & 0xFF) + "." + (arrayOfByte[2] & 0xFF) + "." + (arrayOfByte[3] & 0xFF);
  }
  
  public final synchronized void useAddressList(InetAddress[] paramArrayOfInetAddress)
  {
    _devAddrList = (paramArrayOfInetAddress != null ? (InetAddress[])paramArrayOfInetAddress.clone() : null);
    _addrIndex = 0;
    useNextAddress();
  }
  
  public final synchronized void useNextAddress()
  {
    if (_devAddrList == null) {
      return;
    }
    if (_addrIndex > _devAddrList.length - 1) {
      _addrIndex = 0;
    }
    _devAddr = _devAddrList[(_addrIndex++)];
  }
  
  public boolean allowSnmpSets()
  {
    return _snmpParameter.allowSnmpSets();
  }
  
  public final InetAddress[] getDestAddrList()
  {
    return _devAddrList == null ? null : (InetAddress[])_devAddrList.clone();
  }
  
  public final InetAddress getDestAddr()
  {
    return _devAddr;
  }
  
  public final int getDestPort()
  {
    return portNum;
  }
  
  public final synchronized void setDestPort(int paramInt)
  {
    portNum = paramInt;
  }
  
  public final int getTimeout()
  {
    return timeout;
  }
  
  public final synchronized void setTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    timeout = paramInt;
  }
  
  public final int getMaxTries()
  {
    return maxTries;
  }
  
  public final synchronized void setMaxTries(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    maxTries = paramInt;
  }
  
  public final String getDevName()
  {
    return getDestAddr().getHostName();
  }
  
  public String toString()
  {
    return "Peer/Port : " + getDestAddr().getHostAddress() + "/" + getDestPort();
  }
  
  public final synchronized int getVarBindLimit()
  {
    return maxVarBindLimit;
  }
  
  public final synchronized void setVarBindLimit(int paramInt)
  {
    maxVarBindLimit = paramInt;
  }
  
  public void setParams(SnmpParams paramSnmpParams)
  {
    _snmpParameter = paramSnmpParams;
  }
  
  public SnmpParams getParams()
  {
    return _snmpParameter;
  }
  
  public final int getMaxSnmpPktSize()
  {
    return maxSnmpPacketSize;
  }
  
  public final synchronized void setMaxSnmpPktSize(int paramInt)
  {
    maxSnmpPacketSize = paramInt;
  }
  
  boolean isCustomPduFactory()
  {
    return customPduFactory;
  }
  
  protected void finalize()
  {
    _devAddr = null;
    _devAddrList = null;
    _snmpParameter = null;
  }
  
  public long getMinRtt()
  {
    return _minrtt;
  }
  
  public long getMaxRtt()
  {
    return _maxrtt;
  }
  
  public long getAvgRtt()
  {
    return _avgrtt;
  }
  
  private void updateRttStats(long paramLong)
  {
    if (_minrtt > paramLong) {
      _minrtt = paramLong;
    } else if (_maxrtt < paramLong) {
      _maxrtt = paramLong;
    } else {
      _avgrtt = paramLong;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */