package sun.security.krb5.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class HostAddresses
  implements Cloneable
{
  private static boolean DEBUG = Krb5.DEBUG;
  private HostAddress[] addresses = null;
  private volatile int hashCode = 0;
  
  public HostAddresses(HostAddress[] paramArrayOfHostAddress)
    throws IOException
  {
    if (paramArrayOfHostAddress != null)
    {
      addresses = new HostAddress[paramArrayOfHostAddress.length];
      for (int i = 0; i < paramArrayOfHostAddress.length; i++)
      {
        if (paramArrayOfHostAddress[i] == null) {
          throw new IOException("Cannot create a HostAddress");
        }
        addresses[i] = ((HostAddress)paramArrayOfHostAddress[i].clone());
      }
    }
  }
  
  public HostAddresses()
    throws UnknownHostException
  {
    addresses = new HostAddress[1];
    addresses[0] = new HostAddress();
  }
  
  private HostAddresses(int paramInt) {}
  
  public HostAddresses(PrincipalName paramPrincipalName)
    throws UnknownHostException, KrbException
  {
    String[] arrayOfString = paramPrincipalName.getNameStrings();
    if ((paramPrincipalName.getNameType() != 3) || (arrayOfString.length < 2)) {
      throw new KrbException(60, "Bad name");
    }
    String str = arrayOfString[1];
    InetAddress[] arrayOfInetAddress = InetAddress.getAllByName(str);
    HostAddress[] arrayOfHostAddress = new HostAddress[arrayOfInetAddress.length];
    for (int i = 0; i < arrayOfInetAddress.length; i++) {
      arrayOfHostAddress[i] = new HostAddress(arrayOfInetAddress[i]);
    }
    addresses = arrayOfHostAddress;
  }
  
  public Object clone()
  {
    HostAddresses localHostAddresses = new HostAddresses(0);
    if (addresses != null)
    {
      addresses = new HostAddress[addresses.length];
      for (int i = 0; i < addresses.length; i++) {
        addresses[i] = ((HostAddress)addresses[i].clone());
      }
    }
    return localHostAddresses;
  }
  
  public boolean inList(HostAddress paramHostAddress)
  {
    if (addresses != null) {
      for (int i = 0; i < addresses.length; i++) {
        if (addresses[i].equals(paramHostAddress)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    if (hashCode == 0)
    {
      int i = 17;
      if (addresses != null) {
        for (int j = 0; j < addresses.length; j++) {
          i = 37 * i + addresses[j].hashCode();
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
    if (!(paramObject instanceof HostAddresses)) {
      return false;
    }
    HostAddresses localHostAddresses = (HostAddresses)paramObject;
    if (((addresses == null) && (addresses != null)) || ((addresses != null) && (addresses == null))) {
      return false;
    }
    if ((addresses != null) && (addresses != null))
    {
      if (addresses.length != addresses.length) {
        return false;
      }
      for (int i = 0; i < addresses.length; i++) {
        if (!addresses[i].equals(addresses[i])) {
          return false;
        }
      }
    }
    return true;
  }
  
  public HostAddresses(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    Vector localVector = new Vector();
    DerValue localDerValue = null;
    while (paramDerValue.getData().available() > 0)
    {
      localDerValue = paramDerValue.getData().getDerValue();
      localVector.addElement(new HostAddress(localDerValue));
    }
    if (localVector.size() > 0)
    {
      addresses = new HostAddress[localVector.size()];
      localVector.copyInto(addresses);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    if ((addresses != null) && (addresses.length > 0)) {
      for (int i = 0; i < addresses.length; i++) {
        localDerOutputStream1.write(addresses[i].asn1Encode());
      }
    }
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public static HostAddresses parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
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
    return new HostAddresses(localDerValue2);
  }
  
  public void writeAddrs(CCacheOutputStream paramCCacheOutputStream)
    throws IOException
  {
    paramCCacheOutputStream.write32(addresses.length);
    for (int i = 0; i < addresses.length; i++)
    {
      paramCCacheOutputStream.write16(addresses[i].addrType);
      paramCCacheOutputStream.write32(addresses[i].address.length);
      paramCCacheOutputStream.write(addresses[i].address, 0, addresses[i].address.length);
    }
  }
  
  public InetAddress[] getInetAddresses()
  {
    if ((addresses == null) || (addresses.length == 0)) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(addresses.length);
    for (int i = 0; i < addresses.length; i++) {
      try
      {
        if ((addresses[i].addrType == 2) || (addresses[i].addrType == 24)) {
          localArrayList.add(addresses[i].getInetAddress());
        }
      }
      catch (UnknownHostException localUnknownHostException)
      {
        return null;
      }
    }
    InetAddress[] arrayOfInetAddress = new InetAddress[localArrayList.size()];
    return (InetAddress[])localArrayList.toArray(arrayOfInetAddress);
  }
  
  public static HostAddresses getLocalAddresses()
    throws IOException
  {
    String str = null;
    InetAddress[] arrayOfInetAddress = null;
    try
    {
      InetAddress localInetAddress = InetAddress.getLocalHost();
      str = localInetAddress.getHostName();
      arrayOfInetAddress = InetAddress.getAllByName(str);
      HostAddress[] arrayOfHostAddress = new HostAddress[arrayOfInetAddress.length];
      for (int i = 0; i < arrayOfInetAddress.length; i++) {
        arrayOfHostAddress[i] = new HostAddress(arrayOfInetAddress[i]);
      }
      if (DEBUG)
      {
        System.out.println(">>> KrbKdcReq local addresses for " + str + " are: ");
        for (i = 0; i < arrayOfInetAddress.length; i++)
        {
          System.out.println("\n\t" + arrayOfInetAddress[i]);
          if ((arrayOfInetAddress[i] instanceof Inet4Address)) {
            System.out.println("IPv4 address");
          }
          if ((arrayOfInetAddress[i] instanceof Inet6Address)) {
            System.out.println("IPv6 address");
          }
        }
      }
      return new HostAddresses(arrayOfHostAddress);
    }
    catch (Exception localException)
    {
      throw new IOException(localException.toString());
    }
  }
  
  public HostAddresses(InetAddress[] paramArrayOfInetAddress)
  {
    if (paramArrayOfInetAddress == null)
    {
      addresses = null;
      return;
    }
    addresses = new HostAddress[paramArrayOfInetAddress.length];
    for (int i = 0; i < paramArrayOfInetAddress.length; i++) {
      addresses[i] = new HostAddress(paramArrayOfInetAddress[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\HostAddresses.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */