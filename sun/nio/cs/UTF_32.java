package sun.nio.cs;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class UTF_32
  extends Unicode
{
  public UTF_32()
  {
    super("UTF-32", StandardCharsets.aliases_UTF_32);
  }
  
  public String historicalName()
  {
    return "UTF-32";
  }
  
  public CharsetDecoder newDecoder()
  {
    return new UTF_32Coder.Decoder(this, 0);
  }
  
  public CharsetEncoder newEncoder()
  {
    return new UTF_32Coder.Encoder(this, 1, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\UTF_32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */