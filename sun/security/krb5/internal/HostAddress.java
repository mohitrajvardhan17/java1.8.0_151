package sun.security.krb5.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class HostAddress
  implements Cloneable
{
  int addrType;
  byte[] address = null;
  private static InetAddress localInetAddress;
  private static final boolean DEBUG = Krb5.DEBUG;
  private volatile int hashCode = 0;
  
  private HostAddress(int paramInt) {}
  
  public Object clone()
  {
    HostAddress localHostAddress = new HostAddress(0);
    addrType = addrType;
    if (address != null) {
      address = ((byte[])address.clone());
    }
    return localHostAddress;
  }
  
  public int hashCode()
  {
    if (hashCode == 0)
    {
      int i = 17;
      i = 37 * i + addrType;
      if (address != null) {
        for (int j = 0; j < address.length; j++) {
          i = 37 * i + address[j];
        }
      }
      hashCode = i;
    }
    return hashCode;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof HostAddress)) {
      return false;
    }
    HostAddress localHostAddress = (HostAddress)paramObject;
    if ((addrType != addrType) || ((address != null) && (address == null)) || ((address == null) && (address != null))) {
      return false;
    }
    if ((address != null) && (address != null))
    {
      if (address.length != address.length) {
        return false;
      }
      for (int i = 0; i < address.length; i++) {
        if (address[i] != address[i]) {
          return false;
        }
      }
    }
    return true;
  }
  
  private static synchronized InetAddress getLocalInetAddress()
    throws UnknownHostException
  {
    if (localInetAddress == null) {
      localInetAddress = InetAddress.getLocalHost();
    }
    if (localInetAddress == null) {
      throw new UnknownHostException();
    }
    return localInetAddress;
  }
  
  public InetAddress getInetAddress()
    throws UnknownHostException
  {
    if ((addrType == 2) || (addrType == 24)) {
      return InetAddress.getByAddress(address);
    }
    return null;
  }
  
  private int getAddrType(InetAddress paramInetAddress)
  {
    int i = 0;
    if ((paramInetAddress instanceof Inet4Address)) {
      i = 2;
    } else if ((paramInetAddress instanceof Inet6Address)) {
      i = 24;
    }
    return i;
  }
  
  public HostAddress()
    throws UnknownHostException
  {
    InetAddress localInetAddress1 = getLocalInetAddress();
    addrType = getAddrType(localInetAddress1);
    address = localInetAddress1.getAddress();
  }
  
  public HostAddress(int paramInt, byte[] paramArrayOfByte)
    throws KrbApErrException, UnknownHostException
  {
    switch (paramInt)
    {
    case 2: 
      if (paramArrayOfByte.length != 4) {
        throw new KrbApErrException(0, "Invalid Internet address");
      }
      break;
    case 5: 
      if (paramArrayOfByte.length != 2) {
        throw new KrbApErrException(0, "Invalid CHAOSnet address");
      }
      break;
    case 7: 
      break;
    case 6: 
      if (paramArrayOfByte.length != 6) {
        throw new KrbApErrException(0, "Invalid XNS address");
      }
      break;
    case 16: 
      if (paramArrayOfByte.length != 3) {
        throw new KrbApErrException(0, "Invalid DDP address");
      }
      break;
    case 12: 
      if (paramArrayOfByte.length != 2) {
        throw new KrbApErrException(0, "Invalid DECnet Phase IV address");
      }
      break;
    case 24: 
      if (paramArrayOfByte.length != 16) {
        throw new KrbApErrException(0, "Invalid Internet IPv6 address");
      }
      break;
    }
    addrType = paramInt;
    if (paramArrayOfByte != null) {
      address = ((byte[])paramArrayOfByte.clone());
    }
    if ((DEBUG) && ((addrType == 2) || (addrType == 24))) {
      System.out.println("Host address is " + InetAddress.getByAddress(address));
    }
  }
  
  public HostAddress(InetAddress paramInetAddress)
  {
    addrType = getAddrType(paramInetAddress);
    address = paramInetAddress.getAddress();
  }
  
  public HostAddress(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    DerValue localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      addrType = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 1) {
      address = localDerValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(addrType);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOctetString(address);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public static HostAddress parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new HostAddress(localDerValue2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\HostAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */