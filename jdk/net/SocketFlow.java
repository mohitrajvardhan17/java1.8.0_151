package jdk.net;

import jdk.Exported;

@Exported
public class SocketFlow
{
  private static final int UNSET = -1;
  public static final int NORMAL_PRIORITY = 1;
  public static final int HIGH_PRIORITY = 2;
  private int priority = 1;
  private long bandwidth = -1L;
  private Status status = Status.NO_STATUS;
  
  private SocketFlow() {}
  
  public static SocketFlow create()
  {
    return new SocketFlow();
  }
  
  public SocketFlow priority(int paramInt)
  {
    if ((paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("invalid priority");
    }
    priority = paramInt;
    return this;
  }
  
  public SocketFlow bandwidth(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("invalid bandwidth");
    }
    bandwidth = paramLong;
    return this;
  }
  
  public int priority()
  {
    return priority;
  }
  
  public long bandwidth()
  {
    return bandwidth;
  }
  
  public Status status()
  {
    return status;
  }
  
  @Exported
  public static enum Status
  {
    NO_STATUS,  OK,  NO_PERMISSION,  NOT_CONNECTED,  NOT_SUPPORTED,  ALREADY_CREATED,  IN_PROGRESS,  OTHER;
    
    private Status() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\net\SocketFlow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */