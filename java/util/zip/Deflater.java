package java.util.zip;

public class Deflater
{
  private final ZStreamRef zsRef;
  private byte[] buf = new byte[0];
  private int off;
  private int len;
  private int level;
  private int strategy;
  private boolean setParams;
  private boolean finish;
  private boolean finished;
  private long bytesRead;
  private long bytesWritten;
  public static final int DEFLATED = 8;
  public static final int NO_COMPRESSION = 0;
  public static final int BEST_SPEED = 1;
  public static final int BEST_COMPRESSION = 9;
  public static final int DEFAULT_COMPRESSION = -1;
  public static final int FILTERED = 1;
  public static final int HUFFMAN_ONLY = 2;
  public static final int DEFAULT_STRATEGY = 0;
  public static final int NO_FLUSH = 0;
  public static final int SYNC_FLUSH = 2;
  public static final int FULL_FLUSH = 3;
  
  public Deflater(int paramInt, boolean paramBoolean)
  {
    level = paramInt;
    strategy = 0;
    zsRef = new ZStreamRef(init(paramInt, 0, paramBoolean));
  }
  
  public Deflater(int paramInt)
  {
    this(paramInt, false);
  }
  
  public Deflater()
  {
    this(-1, false);
  }
  
  public void setInput(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    synchronized (zsRef)
    {
      buf = paramArrayOfByte;
      off = paramInt1;
      len = paramInt2;
    }
  }
  
  public void setInput(byte[] paramArrayOfByte)
  {
    setInput(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void setDictionary(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    synchronized (zsRef)
    {
      ensureOpen();
      setDictionary(zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  public void setDictionary(byte[] paramArrayOfByte)
  {
    setDictionary(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void setStrategy(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
      break;
    default: 
      throw new IllegalArgumentException();
    }
    synchronized (zsRef)
    {
      if (strategy != paramInt)
      {
        strategy = paramInt;
        setParams = true;
      }
    }
  }
  
  public void setLevel(int paramInt)
  {
    if (((paramInt < 0) || (paramInt > 9)) && (paramInt != -1)) {
      throw new IllegalArgumentException("invalid compression level");
    }
    synchronized (zsRef)
    {
      if (level != paramInt)
      {
        level = paramInt;
        setParams = true;
      }
    }
  }
  
  public boolean needsInput()
  {
    synchronized (zsRef)
    {
      return len <= 0;
    }
  }
  
  public void finish()
  {
    synchronized (zsRef)
    {
      finish = true;
    }
  }
  
  /* Error */
  public boolean finished()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 151	java/util/zip/Deflater:zsRef	Ljava/util/zip/ZStreamRef;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 148	java/util/zip/Deflater:finished	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Deflater
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public int deflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return deflate(paramArrayOfByte, paramInt1, paramInt2, 0);
  }
  
  public int deflate(byte[] paramArrayOfByte)
  {
    return deflate(paramArrayOfByte, 0, paramArrayOfByte.length, 0);
  }
  
  public int deflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    synchronized (zsRef)
    {
      ensureOpen();
      if ((paramInt3 == 0) || (paramInt3 == 2) || (paramInt3 == 3))
      {
        int i = len;
        int j = deflateBytes(zsRef.address(), paramArrayOfByte, paramInt1, paramInt2, paramInt3);
        bytesWritten += j;
        bytesRead += i - len;
        return j;
      }
      throw new IllegalArgumentException();
    }
  }
  
  public int getAdler()
  {
    synchronized (zsRef)
    {
      ensureOpen();
      return getAdler(zsRef.address());
    }
  }
  
  public int getTotalIn()
  {
    return (int)getBytesRead();
  }
  
  public long getBytesRead()
  {
    synchronized (zsRef)
    {
      ensureOpen();
      return bytesRead;
    }
  }
  
  public int getTotalOut()
  {
    return (int)getBytesWritten();
  }
  
  public long getBytesWritten()
  {
    synchronized (zsRef)
    {
      ensureOpen();
      return bytesWritten;
    }
  }
  
  public void reset()
  {
    synchronized (zsRef)
    {
      ensureOpen();
      reset(zsRef.address());
      finish = false;
      finished = false;
      off = (len = 0);
      bytesRead = (bytesWritten = 0L);
    }
  }
  
  public void end()
  {
    synchronized (zsRef)
    {
      long l = zsRef.address();
      zsRef.clear();
      if (l != 0L)
      {
        end(l);
        buf = null;
      }
    }
  }
  
  protected void finalize()
  {
    end();
  }
  
  private void ensureOpen()
  {
    assert (Thread.holdsLock(zsRef));
    if (zsRef.address() == 0L) {
      throw new NullPointerException("Deflater has been closed");
    }
  }
  
  private static native void initIDs();
  
  private static native long init(int paramInt1, int paramInt2, boolean paramBoolean);
  
  private static native void setDictionary(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native int deflateBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int getAdler(long paramLong);
  
  private static native void reset(long paramLong);
  
  private static native void end(long paramLong);
  
  static
  {
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\Deflater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */