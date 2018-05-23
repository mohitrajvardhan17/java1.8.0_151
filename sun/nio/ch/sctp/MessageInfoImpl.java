package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import java.net.SocketAddress;

public class MessageInfoImpl
  extends MessageInfo
{
  private final SocketAddress address;
  private final int bytes;
  private Association association;
  private int assocId;
  private int streamNumber;
  private boolean complete = true;
  private boolean unordered;
  private long timeToLive;
  private int ppid;
  
  public MessageInfoImpl(Association paramAssociation, SocketAddress paramSocketAddress, int paramInt)
  {
    association = paramAssociation;
    address = paramSocketAddress;
    streamNumber = paramInt;
    bytes = 0;
  }
  
  private MessageInfoImpl(int paramInt1, SocketAddress paramSocketAddress, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4)
  {
    assocId = paramInt1;
    address = paramSocketAddress;
    bytes = paramInt2;
    streamNumber = paramInt3;
    complete = paramBoolean1;
    unordered = paramBoolean2;
    ppid = paramInt4;
  }
  
  public Association association()
  {
    return association;
  }
  
  void setAssociation(Association paramAssociation)
  {
    association = paramAssociation;
  }
  
  int associationID()
  {
    return assocId;
  }
  
  public SocketAddress address()
  {
    return address;
  }
  
  public int bytes()
  {
    return bytes;
  }
  
  public int streamNumber()
  {
    return streamNumber;
  }
  
  public MessageInfo streamNumber(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 65536)) {
      throw new IllegalArgumentException("Invalid stream number");
    }
    streamNumber = paramInt;
    return this;
  }
  
  public int payloadProtocolID()
  {
    return ppid;
  }
  
  public MessageInfo payloadProtocolID(int paramInt)
  {
    ppid = paramInt;
    return this;
  }
  
  public boolean isComplete()
  {
    return complete;
  }
  
  public MessageInfo complete(boolean paramBoolean)
  {
    complete = paramBoolean;
    return this;
  }
  
  public boolean isUnordered()
  {
    return unordered;
  }
  
  public MessageInfo unordered(boolean paramBoolean)
  {
    unordered = paramBoolean;
    return this;
  }
  
  public long timeToLive()
  {
    return timeToLive;
  }
  
  public MessageInfo timeToLive(long paramLong)
  {
    timeToLive = paramLong;
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(super.toString());
    localStringBuilder.append("[Address: ").append(address).append(", Association: ").append(association).append(", Assoc ID: ").append(assocId).append(", Bytes: ").append(bytes).append(", Stream Number: ").append(streamNumber).append(", Complete: ").append(complete).append(", isUnordered: ").append(unordered).append("]");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\sctp\MessageInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */