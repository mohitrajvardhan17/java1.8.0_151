package java.util.zip;

public class Inflater
{
  private final ZStreamRef zsRef;
  private byte[] buf = defaultBuf;
  private int off;
  private int len;
  private boolean finished;
  private boolean needDict;
  private long bytesRead;
  private long bytesWritten;
  private static final byte[] defaultBuf;
  
  public Inflater(boolean paramBoolean)
  {
    zsRef = new ZStreamRef(init(paramBoolean));
  }
  
  public Inflater()
  {
    this(false);
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
      needDict = false;
    }
  }
  
  public void setDictionary(byte[] paramArrayOfByte)
  {
    setDictionary(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  /* Error */
  public int getRemaining()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 124	java/util/zip/Inflater:zsRef	Ljava/util/zip/ZStreamRef;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 115	java/util/zip/Inflater:len	I
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
    //   0	19	0	this	Inflater
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public boolean needsInput()
  {
    synchronized (zsRef)
    {
      return len <= 0;
    }
  }
  
  /* Error */
  public boolean needsDictionary()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 124	java/util/zip/Inflater:zsRef	Ljava/util/zip/ZStreamRef;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 121	java/util/zip/Inflater:needDict	Z
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
    //   0	19	0	this	Inflater
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public boolean finished()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 124	java/util/zip/Inflater:zsRef	Ljava/util/zip/ZStreamRef;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 120	java/util/zip/Inflater:finished	Z
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
    //   0	19	0	this	Inflater
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public int inflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DataFormatException
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
      int i = len;
      int j = inflateBytes(zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
      bytesWritten += j;
      bytesRead += i - len;
      return j;
    }
  }
  
  public int inflate(byte[] paramArrayOfByte)
    throws DataFormatException
  {
    return inflate(paramArrayOfByte, 0, paramArrayOfByte.length);
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
      buf = defaultBuf;
      finished = false;
      needDict = false;
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
      throw new NullPointerException("Inflater has been closed");
    }
  }
  
  boolean ended()
  {
    synchronized (zsRef)
    {
      return zsRef.address() == 0L;
    }
  }
  
  private static native void initIDs();
  
  private static native long init(boolean paramBoolean);
  
  private static native void setDictionary(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native int inflateBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DataFormatException;
  
  private static native int getAdler(long paramLong);
  
  private static native void reset(long paramLong);
  
  private static native void end(long paramLong);
  
  static
  {
    defaultBuf = new byte[0];
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\Inflater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */