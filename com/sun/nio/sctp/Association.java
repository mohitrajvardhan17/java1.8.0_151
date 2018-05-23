package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class Association
{
  private final int associationID;
  private final int maxInStreams;
  private final int maxOutStreams;
  
  protected Association(int paramInt1, int paramInt2, int paramInt3)
  {
    associationID = paramInt1;
    maxInStreams = paramInt2;
    maxOutStreams = paramInt3;
  }
  
  public final int associationID()
  {
    return associationID;
  }
  
  public final int maxInboundStreams()
  {
    return maxInStreams;
  }
  
  public final int maxOutboundStreams()
  {
    return maxOutStreams;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\nio\sctp\Association.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */