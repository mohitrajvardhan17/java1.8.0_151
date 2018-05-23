package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class NetMaskImpl
  extends PrincipalImpl
  implements Group, Serializable
{
  private static final long serialVersionUID = -7332541893877932896L;
  protected byte[] subnet = null;
  protected int prefix = -1;
  
  public NetMaskImpl()
    throws UnknownHostException
  {}
  
  private byte[] extractSubNet(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte = null;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
    {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "BINARY ARRAY :");
      StringBuffer localStringBuffer = new StringBuffer();
      for (k = 0; k < i; k++) {
        localStringBuffer.append((paramArrayOfByte[k] & 0xFF) + ":");
      }
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", localStringBuffer.toString());
    }
    int j = prefix / 8;
    if (j == i)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "The mask is the complete address, strange..." + i);
      }
      arrayOfByte = paramArrayOfByte;
      return arrayOfByte;
    }
    if (j > i)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "The number of covered byte is longer than the address. BUG");
      }
      throw new IllegalArgumentException("The number of covered byte is longer than the address.");
    }
    int k = j;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Partially covered index : " + k);
    }
    int m = paramArrayOfByte[k];
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Partially covered byte : " + m);
    }
    int n = prefix % 8;
    int i1 = 0;
    if (n == 0) {
      i1 = k;
    } else {
      i1 = k + 1;
    }
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Remains : " + n);
    }
    int i2 = 0;
    for (int i3 = 0; i3 < n; i3++) {
      i2 = (byte)(i2 | 1 << 7 - i3);
    }
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Mask value : " + (i2 & 0xFF));
    }
    i3 = (byte)(m & i2);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Masked byte : " + (i3 & 0xFF));
    }
    arrayOfByte = new byte[i1];
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Resulting subnet : ");
    }
    for (int i4 = 0; i4 < k; i4++)
    {
      arrayOfByte[i4] = paramArrayOfByte[i4];
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", (arrayOfByte[i4] & 0xFF) + ":");
      }
    }
    if (n != 0)
    {
      arrayOfByte[k] = i3;
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Last subnet byte : " + (arrayOfByte[k] & 0xFF));
      }
    }
    return arrayOfByte;
  }
  
  public NetMaskImpl(String paramString, int paramInt)
    throws UnknownHostException
  {
    super(paramString);
    prefix = paramInt;
    subnet = extractSubNet(getAddress().getAddress());
  }
  
  public boolean addMember(Principal paramPrincipal)
  {
    return true;
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (((paramObject instanceof PrincipalImpl)) || ((paramObject instanceof NetMaskImpl)))
    {
      PrincipalImpl localPrincipalImpl = (PrincipalImpl)paramObject;
      InetAddress localInetAddress = localPrincipalImpl.getAddress();
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "Received Address : " + localInetAddress);
      }
      byte[] arrayOfByte = localInetAddress.getAddress();
      for (int i = 0; i < subnet.length; i++)
      {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        {
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "(recAddr[i]) : " + (arrayOfByte[i] & 0xFF));
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "(recAddr[i] & subnet[i]) : " + (arrayOfByte[i] & subnet[i] & 0xFF) + " subnet[i] : " + (subnet[i] & 0xFF));
        }
        if ((arrayOfByte[i] & subnet[i]) != subnet[i])
        {
          if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "FALSE");
          }
          return false;
        }
      }
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "TRUE");
      }
      return true;
    }
    return false;
  }
  
  public boolean isMember(Principal paramPrincipal)
  {
    return (paramPrincipal.hashCode() & super.hashCode()) == paramPrincipal.hashCode();
  }
  
  public Enumeration<? extends Principal> members()
  {
    Vector localVector = new Vector(1);
    localVector.addElement(this);
    return localVector.elements();
  }
  
  public boolean removeMember(Principal paramPrincipal)
  {
    return true;
  }
  
  public String toString()
  {
    return "NetMaskImpl :" + super.getAddress().toString() + "/" + prefix;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\NetMaskImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */