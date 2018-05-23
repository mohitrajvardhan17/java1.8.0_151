package sun.nio.ch;

class PollArrayWrapper
{
  private AllocatedNativeObject pollArray;
  long pollArrayAddress;
  private static final short FD_OFFSET = 0;
  private static final short EVENT_OFFSET = 4;
  static short SIZE_POLLFD = 8;
  private int size;
  
  PollArrayWrapper(int paramInt)
  {
    int i = paramInt * SIZE_POLLFD;
    pollArray = new AllocatedNativeObject(i, true);
    pollArrayAddress = pollArray.address();
    size = paramInt;
  }
  
  void addEntry(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    putDescriptor(paramInt, channel.getFDVal());
  }
  
  void replaceEntry(PollArrayWrapper paramPollArrayWrapper1, int paramInt1, PollArrayWrapper paramPollArrayWrapper2, int paramInt2)
  {
    paramPollArrayWrapper2.putDescriptor(paramInt2, paramPollArrayWrapper1.getDescriptor(paramInt1));
    paramPollArrayWrapper2.putEventOps(paramInt2, paramPollArrayWrapper1.getEventOps(paramInt1));
  }
  
  void grow(int paramInt)
  {
    PollArrayWrapper localPollArrayWrapper = new PollArrayWrapper(paramInt);
    for (int i = 0; i < size; i++) {
      replaceEntry(this, i, localPollArrayWrapper, i);
    }
    pollArray.free();
    pollArray = pollArray;
    size = size;
    pollArrayAddress = pollArray.address();
  }
  
  void free()
  {
    pollArray.free();
  }
  
  void putDescriptor(int paramInt1, int paramInt2)
  {
    pollArray.putInt(SIZE_POLLFD * paramInt1 + 0, paramInt2);
  }
  
  void putEventOps(int paramInt1, int paramInt2)
  {
    pollArray.putShort(SIZE_POLLFD * paramInt1 + 4, (short)paramInt2);
  }
  
  int getEventOps(int paramInt)
  {
    return pollArray.getShort(SIZE_POLLFD * paramInt + 4);
  }
  
  int getDescriptor(int paramInt)
  {
    return pollArray.getInt(SIZE_POLLFD * paramInt + 0);
  }
  
  void addWakeupSocket(int paramInt1, int paramInt2)
  {
    putDescriptor(paramInt2, paramInt1);
    putEventOps(paramInt2, Net.POLLIN);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\PollArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */