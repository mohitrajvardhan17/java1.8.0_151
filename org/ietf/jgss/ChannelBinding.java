package org.ietf.jgss;

import java.net.InetAddress;
import java.util.Arrays;

public class ChannelBinding
{
  private InetAddress initiator;
  private InetAddress acceptor;
  private byte[] appData;
  
  public ChannelBinding(InetAddress paramInetAddress1, InetAddress paramInetAddress2, byte[] paramArrayOfByte)
  {
    initiator = paramInetAddress1;
    acceptor = paramInetAddress2;
    if (paramArrayOfByte != null)
    {
      appData = new byte[paramArrayOfByte.length];
      System.arraycopy(paramArrayOfByte, 0, appData, 0, paramArrayOfByte.length);
    }
  }
  
  public ChannelBinding(byte[] paramArrayOfByte)
  {
    this(null, null, paramArrayOfByte);
  }
  
  public InetAddress getInitiatorAddress()
  {
    return initiator;
  }
  
  public InetAddress getAcceptorAddress()
  {
    return acceptor;
  }
  
  public byte[] getApplicationData()
  {
    if (appData == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[appData.length];
    System.arraycopy(appData, 0, arrayOfByte, 0, appData.length);
    return arrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ChannelBinding)) {
      return false;
    }
    ChannelBinding localChannelBinding = (ChannelBinding)paramObject;
    if (((initiator != null) && (initiator == null)) || ((initiator == null) && (initiator != null))) {
      return false;
    }
    if ((initiator != null) && (!initiator.equals(initiator))) {
      return false;
    }
    if (((acceptor != null) && (acceptor == null)) || ((acceptor == null) && (acceptor != null))) {
      return false;
    }
    if ((acceptor != null) && (!acceptor.equals(acceptor))) {
      return false;
    }
    return Arrays.equals(appData, appData);
  }
  
  public int hashCode()
  {
    if (initiator != null) {
      return initiator.hashCode();
    }
    if (acceptor != null) {
      return acceptor.hashCode();
    }
    if (appData != null) {
      return new String(appData).hashCode();
    }
    return 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\ietf\jgss\ChannelBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */