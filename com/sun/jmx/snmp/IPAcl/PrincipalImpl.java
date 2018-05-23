package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;

class PrincipalImpl
  implements Principal, Serializable
{
  private static final long serialVersionUID = -7910027842878976761L;
  private InetAddress[] add = null;
  
  public PrincipalImpl()
    throws UnknownHostException
  {
    add = new InetAddress[1];
    add[0] = InetAddress.getLocalHost();
  }
  
  public PrincipalImpl(String paramString)
    throws UnknownHostException
  {
    if ((paramString.equals("localhost")) || (paramString.equals("127.0.0.1")))
    {
      add = new InetAddress[1];
      add[0] = InetAddress.getByName(paramString);
    }
    else
    {
      add = InetAddress.getAllByName(paramString);
    }
  }
  
  public PrincipalImpl(InetAddress paramInetAddress)
  {
    add = new InetAddress[1];
    add[0] = paramInetAddress;
  }
  
  public String getName()
  {
    return add[0].toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PrincipalImpl))
    {
      for (int i = 0; i < add.length; i++) {
        if (add[i].equals(((PrincipalImpl)paramObject).getAddress())) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  public int hashCode()
  {
    return add[0].hashCode();
  }
  
  public String toString()
  {
    return "PrincipalImpl :" + add[0].toString();
  }
  
  public InetAddress getAddress()
  {
    return add[0];
  }
  
  public InetAddress[] getAddresses()
  {
    return add;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\PrincipalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */