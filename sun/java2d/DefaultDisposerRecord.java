package sun.java2d;

public class DefaultDisposerRecord
  implements DisposerRecord
{
  private long dataPointer;
  private long disposerMethodPointer;
  
  public DefaultDisposerRecord(long paramLong1, long paramLong2)
  {
    disposerMethodPointer = paramLong1;
    dataPointer = paramLong2;
  }
  
  public void dispose()
  {
    invokeNativeDispose(disposerMethodPointer, dataPointer);
  }
  
  public long getDataPointer()
  {
    return dataPointer;
  }
  
  public long getDisposerMethodPointer()
  {
    return disposerMethodPointer;
  }
  
  public static native void invokeNativeDispose(long paramLong1, long paramLong2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\DefaultDisposerRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */