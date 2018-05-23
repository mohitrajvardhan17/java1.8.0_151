package sun.nio.ch;

public abstract class AbstractPollArrayWrapper
{
  static final short SIZE_POLLFD = 8;
  static final short FD_OFFSET = 0;
  static final short EVENT_OFFSET = 4;
  static final short REVENT_OFFSET = 6;
  protected AllocatedNativeObject pollArray;
  protected int totalChannels = 0;
  protected long pollArrayAddress;
  
  public AbstractPollArrayWrapper() {}
  
  int getEventOps(int paramInt)
  {
    int i = 8 * paramInt + 4;
    return pollArray.getShort(i);
  }
  
  int getReventOps(int paramInt)
  {
    int i = 8 * paramInt + 6;
    return pollArray.getShort(i);
  }
  
  int getDescriptor(int paramInt)
  {
    int i = 8 * paramInt + 0;
    return pollArray.getInt(i);
  }
  
  void putEventOps(int paramInt1, int paramInt2)
  {
    int i = 8 * paramInt1 + 4;
    pollArray.putShort(i, (short)paramInt2);
  }
  
  void putReventOps(int paramInt1, int paramInt2)
  {
    int i = 8 * paramInt1 + 6;
    pollArray.putShort(i, (short)paramInt2);
  }
  
  void putDescriptor(int paramInt1, int paramInt2)
  {
    int i = 8 * paramInt1 + 0;
    pollArray.putInt(i, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\AbstractPollArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */