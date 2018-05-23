package java.util.stream;

abstract class AbstractSpinedBuffer
{
  public static final int MIN_CHUNK_POWER = 4;
  public static final int MIN_CHUNK_SIZE = 16;
  public static final int MAX_CHUNK_POWER = 30;
  public static final int MIN_SPINE_SIZE = 8;
  protected final int initialChunkPower;
  protected int elementIndex;
  protected int spineIndex;
  protected long[] priorElementCount;
  
  protected AbstractSpinedBuffer()
  {
    initialChunkPower = 4;
  }
  
  protected AbstractSpinedBuffer(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
    initialChunkPower = Math.max(4, 32 - Integer.numberOfLeadingZeros(paramInt - 1));
  }
  
  public boolean isEmpty()
  {
    return (spineIndex == 0) && (elementIndex == 0);
  }
  
  public long count()
  {
    return spineIndex == 0 ? elementIndex : priorElementCount[spineIndex] + elementIndex;
  }
  
  protected int chunkSize(int paramInt)
  {
    int i = (paramInt == 0) || (paramInt == 1) ? initialChunkPower : Math.min(initialChunkPower + paramInt - 1, 30);
    return 1 << i;
  }
  
  public abstract void clear();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\AbstractSpinedBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */