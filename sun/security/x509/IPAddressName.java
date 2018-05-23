package sun.security.x509;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class IPAddressName
  implements GeneralNameInterface
{
  private byte[] address;
  private boolean isIPv4;
  private String name;
  private static final int MASKSIZE = 16;
  
  public IPAddressName(DerValue paramDerValue)
    throws IOException
  {
    this(paramDerValue.getOctetString());
  }
  
  public IPAddressName(byte[] paramArrayOfByte)
    throws IOException
  {
    if ((paramArrayOfByte.length == 4) || (paramArrayOfByte.length == 8)) {
      isIPv4 = true;
    } else if ((paramArrayOfByte.length == 16) || (paramArrayOfByte.length == 32)) {
      isIPv4 = false;
    } else {
      throw new IOException("Invalid IPAddressName");
    }
    address = paramArrayOfByte;
  }
  
  public IPAddressName(String paramString)
    throws IOException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IOException("IPAddress cannot be null or empty");
    }
    if (paramString.charAt(paramString.length() - 1) == '/') {
      throw new IOException("Invalid IPAddress: " + paramString);
    }
    if (paramString.indexOf(':') >= 0)
    {
      parseIPv6(paramString);
      isIPv4 = false;
    }
    else if (paramString.indexOf('.') >= 0)
    {
      parseIPv4(paramString);
      isIPv4 = true;
    }
    else
    {
      throw new IOException("Invalid IPAddress: " + paramString);
    }
  }
  
  private void parseIPv4(String paramString)
    throws IOException
  {
    int i = paramString.indexOf('/');
    if (i == -1)
    {
      address = InetAddress.getByName(paramString).getAddress();
    }
    else
    {
      address = new byte[8];
      byte[] arrayOfByte1 = InetAddress.getByName(paramString.substring(i + 1)).getAddress();
      byte[] arrayOfByte2 = InetAddress.getByName(paramString.substring(0, i)).getAddress();
      System.arraycopy(arrayOfByte2, 0, address, 0, 4);
      System.arraycopy(arrayOfByte1, 0, address, 4, 4);
    }
  }
  
  private void parseIPv6(String paramString)
    throws IOException
  {
    int i = paramString.indexOf('/');
    if (i == -1)
    {
      address = InetAddress.getByName(paramString).getAddress();
    }
    else
    {
      address = new byte[32];
      byte[] arrayOfByte1 = InetAddress.getByName(paramString.substring(0, i)).getAddress();
      System.arraycopy(arrayOfByte1, 0, address, 0, 16);
      int j = Integer.parseInt(paramString.substring(i + 1));
      if ((j < 0) || (j > 128)) {
        throw new IOException("IPv6Address prefix length (" + j + ") in out of valid range [0,128]");
      }
      BitArray localBitArray = new BitArray(128);
      for (int k = 0; k < j; k++) {
        localBitArray.set(k, true);
      }
      byte[] arrayOfByte2 = localBitArray.toByteArray();
      for (int m = 0; m < 16; m++) {
        address[(16 + m)] = arrayOfByte2[m];
      }
    }
  }
  
  public int getType()
  {
    return 7;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putOctetString(address);
  }
  
  public String toString()
  {
    try
    {
      return "IPAddress: " + getName();
    }
    catch (IOException localIOException)
    {
      HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
      return "IPAddress: " + localHexDumpEncoder.encodeBuffer(address);
    }
  }
  
  public String getName()
    throws IOException
  {
    if (name != null) {
      return name;
    }
    byte[] arrayOfByte1;
    byte[] arrayOfByte2;
    if (isIPv4)
    {
      arrayOfByte1 = new byte[4];
      System.arraycopy(address, 0, arrayOfByte1, 0, 4);
      name = InetAddress.getByAddress(arrayOfByte1).getHostAddress();
      if (address.length == 8)
      {
        arrayOfByte2 = new byte[4];
        System.arraycopy(address, 4, arrayOfByte2, 0, 4);
        name = (name + "/" + InetAddress.getByAddress(arrayOfByte2).getHostAddress());
      }
    }
    else
    {
      arrayOfByte1 = new byte[16];
      System.arraycopy(address, 0, arrayOfByte1, 0, 16);
      name = InetAddress.getByAddress(arrayOfByte1).getHostAddress();
      if (address.length == 32)
      {
        arrayOfByte2 = new byte[16];
        for (int i = 16; i < 32; i++) {
          arrayOfByte2[(i - 16)] = address[i];
        }
        BitArray localBitArray = new BitArray(128, arrayOfByte2);
        for (int j = 0; (j < 128) && (localBitArray.get(j)); j++) {}
        name = (name + "/" + j);
        while (j < 128)
        {
          if (localBitArray.get(j)) {
            throw new IOException("Invalid IPv6 subdomain - set bit " + j + " not contiguous");
          }
          j++;
        }
      }
    }
    return name;
  }
  
  public byte[] getBytes()
  {
    return (byte[])address.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof IPAddressName)) {
      return false;
    }
    IPAddressName localIPAddressName = (IPAddressName)paramObject;
    byte[] arrayOfByte = address;
    if (arrayOfByte.length != address.length) {
      return false;
    }
    if ((address.length == 8) || (address.length == 32))
    {
      int i = address.length / 2;
      for (int j = 0; j < i; j++)
      {
        int k = (byte)(address[j] & address[(j + i)]);
        int m = (byte)(arrayOfByte[j] & arrayOfByte[(j + i)]);
        if (k != m) {
          return false;
        }
      }
      for (j = i; j < address.length; j++) {
        if (address[j] != arrayOfByte[j]) {
          return false;
        }
      }
      return true;
    }
    return Arrays.equals(arrayOfByte, address);
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < address.length; j++) {
      i += address[j] * j;
    }
    return i;
  }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface)
    throws UnsupportedOperationException
  {
    int i;
    if (paramGeneralNameInterface == null)
    {
      i = -1;
    }
    else if (paramGeneralNameInterface.getType() != 7)
    {
      i = -1;
    }
    else if (((IPAddressName)paramGeneralNameInterface).equals(this))
    {
      i = 0;
    }
    else
    {
      IPAddressName localIPAddressName = (IPAddressName)paramGeneralNameInterface;
      byte[] arrayOfByte = address;
      if ((arrayOfByte.length == 4) && (address.length == 4))
      {
        i = 3;
      }
      else
      {
        int j;
        int k;
        if (((arrayOfByte.length == 8) && (address.length == 8)) || ((arrayOfByte.length == 32) && (address.length == 32)))
        {
          j = 1;
          k = 1;
          int m = 0;
          int n = 0;
          int i1 = address.length / 2;
          for (int i2 = 0; i2 < i1; i2++)
          {
            if ((byte)(address[i2] & address[(i2 + i1)]) != address[i2]) {
              m = 1;
            }
            if ((byte)(arrayOfByte[i2] & arrayOfByte[(i2 + i1)]) != arrayOfByte[i2]) {
              n = 1;
            }
            if (((byte)(address[(i2 + i1)] & arrayOfByte[(i2 + i1)]) != address[(i2 + i1)]) || ((byte)(address[i2] & address[(i2 + i1)]) != (byte)(arrayOfByte[i2] & address[(i2 + i1)]))) {
              j = 0;
            }
            if (((byte)(arrayOfByte[(i2 + i1)] & address[(i2 + i1)]) != arrayOfByte[(i2 + i1)]) || ((byte)(arrayOfByte[i2] & arrayOfByte[(i2 + i1)]) != (byte)(address[i2] & arrayOfByte[(i2 + i1)]))) {
              k = 0;
            }
          }
          if ((m != 0) || (n != 0))
          {
            if ((m != 0) && (n != 0)) {
              i = 0;
            } else if (m != 0) {
              i = 2;
            } else {
              i = 1;
            }
          }
          else if (j != 0) {
            i = 1;
          } else if (k != 0) {
            i = 2;
          } else {
            i = 3;
          }
        }
        else if ((arrayOfByte.length == 8) || (arrayOfByte.length == 32))
        {
          j = 0;
          k = arrayOfByte.length / 2;
          while ((j < k) && ((address[j] & arrayOfByte[(j + k)]) == arrayOfByte[j])) {
            j++;
          }
          if (j == k) {
            i = 2;
          } else {
            i = 3;
          }
        }
        else if ((address.length == 8) || (address.length == 32))
        {
          j = 0;
          k = address.length / 2;
          while ((j < k) && ((arrayOfByte[j] & address[(j + k)]) == address[j])) {
            j++;
          }
          if (j == k) {
            i = 1;
          } else {
            i = 3;
          }
        }
        else
        {
          i = 3;
        }
      }
    }
    return i;
  }
  
  public int subtreeDepth()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("subtreeDepth() not defined for IPAddressName");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\IPAddressName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */