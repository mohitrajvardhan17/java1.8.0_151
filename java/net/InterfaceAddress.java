package java.net;

public class InterfaceAddress
{
  private InetAddress address = null;
  private Inet4Address broadcast = null;
  private short maskLength = 0;
  
  InterfaceAddress() {}
  
  public InetAddress getAddress()
  {
    return address;
  }
  
  public InetAddress getBroadcast()
  {
    return broadcast;
  }
  
  public short getNetworkPrefixLength()
  {
    return maskLength;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof InterfaceAddress)) {
      return false;
    }
    InterfaceAddress localInterfaceAddress = (InterfaceAddress)paramObject;
    if (address == null ? address != null : !address.equals(address)) {
      return false;
    }
    if (broadcast == null ? broadcast != null : !broadcast.equals(broadcast)) {
      return false;
    }
    return maskLength == maskLength;
  }
  
  public int hashCode()
  {
    return address.hashCode() + (broadcast != null ? broadcast.hashCode() : 0) + maskLength;
  }
  
  public String toString()
  {
    return address + "/" + maskLength + " [" + broadcast + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\InterfaceAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */