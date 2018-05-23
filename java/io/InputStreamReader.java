package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.StreamDecoder;

public class InputStreamReader
  extends Reader
{
  private final StreamDecoder sd;
  
  public InputStreamReader(InputStream paramInputStream)
  {
    super(paramInputStream);
    try
    {
      sd = StreamDecoder.forInputStreamReader(paramInputStream, this, (String)null);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new Error(localUnsupportedEncodingException);
    }
  }
  
  public InputStreamReader(InputStream paramInputStream, String paramString)
    throws UnsupportedEncodingException
  {
    super(paramInputStream);
    if (paramString == null) {
      throw new NullPointerException("charsetName");
    }
    sd = StreamDecoder.forInputStreamReader(paramInputStream, this, paramString);
  }
  
  public InputStreamReader(InputStream paramInputStream, Charset paramCharset)
  {
    super(paramInputStream);
    if (paramCharset == null) {
      throw new NullPointerException("charset");
    }
    sd = StreamDecoder.forInputStreamReader(paramInputStream, this, paramCharset);
  }
  
  public InputStreamReader(InputStream paramInputStream, CharsetDecoder paramCharsetDecoder)
  {
    super(paramInputStream);
    if (paramCharsetDecoder == null) {
      throw new NullPointerException("charset decoder");
    }
    sd = StreamDecoder.forInputStreamReader(paramInputStream, this, paramCharsetDecoder);
  }
  
  public String getEncoding()
  {
    return sd.getEncoding();
  }
  
  public int read()
    throws IOException
  {
    return sd.read();
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    return sd.read(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public boolean ready()
    throws IOException
  {
    return sd.ready();
  }
  
  public void close()
    throws IOException
  {
    sd.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\InputStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */