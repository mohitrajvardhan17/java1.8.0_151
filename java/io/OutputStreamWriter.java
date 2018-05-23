package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.StreamEncoder;

public class OutputStreamWriter
  extends Writer
{
  private final StreamEncoder se;
  
  public OutputStreamWriter(OutputStream paramOutputStream, String paramString)
    throws UnsupportedEncodingException
  {
    super(paramOutputStream);
    if (paramString == null) {
      throw new NullPointerException("charsetName");
    }
    se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, paramString);
  }
  
  public OutputStreamWriter(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    try
    {
      se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, (String)null);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error(localUnsupportedEncodingException);
    }
  }
  
  public OutputStreamWriter(OutputStream paramOutputStream, Charset paramCharset)
  {
    super(paramOutputStream);
    if (paramCharset == null) {
      throw new NullPointerException("charset");
    }
    se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, paramCharset);
  }
  
  public OutputStreamWriter(OutputStream paramOutputStream, CharsetEncoder paramCharsetEncoder)
  {
    super(paramOutputStream);
    if (paramCharsetEncoder == null) {
      throw new NullPointerException("charset encoder");
    }
    se = StreamEncoder.forOutputStreamWriter(paramOutputStream, this, paramCharsetEncoder);
  }
  
  public String getEncoding()
  {
    return se.getEncoding();
  }
  
  void flushBuffer()
    throws IOException
  {
    se.flushBuffer();
  }
  
  public void write(int paramInt)
    throws IOException
  {
    se.write(paramInt);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    se.write(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    se.write(paramString, paramInt1, paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    se.flush();
  }
  
  public void close()
    throws IOException
  {
    se.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\OutputStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */