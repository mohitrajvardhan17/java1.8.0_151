package java.io;

@Deprecated
public class StringBufferInputStream
  extends InputStream
{
  protected String buffer;
  protected int pos;
  protected int count;
  
  public StringBufferInputStream(String paramString)
  {
    buffer = paramString;
    count = paramString.length();
  }
  
  public synchronized int read()
  {
    return pos < count ? buffer.charAt(pos++) & 0xFF : -1;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (pos >= count) {
      return -1;
    }
    if (pos + paramInt2 > count) {
      paramInt2 = count - pos;
    }
    if (paramInt2 <= 0) {
      return 0;
    }
    String str = buffer;
    int i = paramInt2;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      paramArrayOfByte[(paramInt1++)] = ((byte)str.charAt(pos++));
    }
    return paramInt2;
  }
  
  public synchronized long skip(long paramLong)
  {
    if (paramLong < 0L) {
      return 0L;
    }
    if (paramLong > count - pos) {
      paramLong = count - pos;
    }
    pos = ((int)(pos + paramLong));
    return paramLong;
  }
  
  public synchronized int available()
  {
    return count - pos;
  }
  
  public synchronized void reset()
  {
    pos = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\StringBufferInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */