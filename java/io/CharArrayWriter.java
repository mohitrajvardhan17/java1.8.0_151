package java.io;

import java.util.Arrays;

public class CharArrayWriter
  extends Writer
{
  protected char[] buf;
  protected int count;
  
  public CharArrayWriter()
  {
    this(32);
  }
  
  public CharArrayWriter(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative initial size: " + paramInt);
    }
    buf = new char[paramInt];
  }
  
  public void write(int paramInt)
  {
    synchronized (lock)
    {
      int i = count + 1;
      if (i > buf.length) {
        buf = Arrays.copyOf(buf, Math.max(buf.length << 1, i));
      }
      buf[count] = ((char)paramInt);
      count = i;
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return;
    }
    synchronized (lock)
    {
      int i = count + paramInt2;
      if (i > buf.length) {
        buf = Arrays.copyOf(buf, Math.max(buf.length << 1, i));
      }
      System.arraycopy(paramArrayOfChar, paramInt1, buf, count, paramInt2);
      count = i;
    }
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
  {
    synchronized (lock)
    {
      int i = count + paramInt2;
      if (i > buf.length) {
        buf = Arrays.copyOf(buf, Math.max(buf.length << 1, i));
      }
      paramString.getChars(paramInt1, paramInt1 + paramInt2, buf, count);
      count = i;
    }
  }
  
  public void writeTo(Writer paramWriter)
    throws IOException
  {
    synchronized (lock)
    {
      paramWriter.write(buf, 0, count);
    }
  }
  
  public CharArrayWriter append(CharSequence paramCharSequence)
  {
    String str = paramCharSequence == null ? "null" : paramCharSequence.toString();
    write(str, 0, str.length());
    return this;
  }
  
  public CharArrayWriter append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    String str = (paramCharSequence == null ? "null" : paramCharSequence).subSequence(paramInt1, paramInt2).toString();
    write(str, 0, str.length());
    return this;
  }
  
  public CharArrayWriter append(char paramChar)
  {
    write(paramChar);
    return this;
  }
  
  public void reset()
  {
    count = 0;
  }
  
  /* Error */
  public char[] toCharArray()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 106	java/io/CharArrayWriter:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 105	java/io/CharArrayWriter:buf	[C
    //   11: aload_0
    //   12: getfield 104	java/io/CharArrayWriter:count	I
    //   15: invokestatic 126	java/util/Arrays:copyOf	([CI)[C
    //   18: aload_1
    //   19: monitorexit
    //   20: areturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	CharArrayWriter
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  public int size()
  {
    return count;
  }
  
  /* Error */
  public String toString()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 106	java/io/CharArrayWriter:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: new 63	java/lang/String
    //   10: dup
    //   11: aload_0
    //   12: getfield 105	java/io/CharArrayWriter:buf	[C
    //   15: iconst_0
    //   16: aload_0
    //   17: getfield 104	java/io/CharArrayWriter:count	I
    //   20: invokespecial 120	java/lang/String:<init>	([CII)V
    //   23: aload_1
    //   24: monitorexit
    //   25: areturn
    //   26: astore_2
    //   27: aload_1
    //   28: monitorexit
    //   29: aload_2
    //   30: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	CharArrayWriter
    //   5	23	1	Ljava/lang/Object;	Object
    //   26	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	25	26	finally
    //   26	29	26	finally
  }
  
  public void flush() {}
  
  public void close() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\CharArrayWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */