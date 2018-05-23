package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16LE_BOM
  extends Unicode
{
  public UTF_16LE_BOM()
  {
    super("x-UTF-16LE-BOM", StandardCharsets.aliases_UTF_16LE_BOM);
  }
  
  public String historicalName()
  {
    return "UnicodeLittle";
  }
  
  public CharsetDecoder newDecoder()
  {
    return new Decoder(this);
  }
  
  public CharsetEncoder newEncoder()
  {
    return new Encoder(this);
  }
  
  private static class Decoder
    extends UnicodeDecoder
  {
    public Decoder(Charset paramCharset)
    {
      super(0, 2);
    }
  }
  
  private static class Encoder
    extends UnicodeEncoder
  {
    public Encoder(Charset paramCharset)
    {
      super(1, true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\UTF_16LE_BOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */