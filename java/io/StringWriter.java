package java.io;

public class StringWriter
  extends Writer
{
  private StringBuffer buf;
  
  public StringWriter()
  {
    buf = new StringBuffer();
    lock = buf;
  }
  
  public StringWriter(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative buffer size");
    }
    buf = new StringBuffer(paramInt);
    lock = buf;
  }
  
  public void write(int paramInt)
  {
    buf.append((char)paramInt);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return;
    }
    buf.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void write(String paramString)
  {
    buf.append(paramString);
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
  {
    buf.append(paramString.substring(paramInt1, paramInt1 + paramInt2));
  }
  
  public StringWriter append(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      write("null");
    } else {
      write(paramCharSequence.toString());
    }
    return this;
  }
  
  public StringWriter append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    CharSequence localCharSequence = paramCharSequence == null ? "null" : paramCharSequence;
    write(localCharSequence.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public StringWriter append(char paramChar)
  {
    write(paramChar);
    return this;
  }
  
  public String toString()
  {
    return buf.toString();
  }
  
  public StringBuffer getBuffer()
  {
    return buf;
  }
  
  public void flush() {}
  
  public void close()
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\StringWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */