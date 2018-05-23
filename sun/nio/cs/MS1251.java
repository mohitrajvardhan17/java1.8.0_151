package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class MS1251
  extends Charset
  implements HistoricallyNamedCharset
{
  private static final String b2cTable = "ЂЃ‚ѓ„…†‡€‰Љ‹ЊЌЋЏђ‘’“”•–—�™љ›њќћџ ЎўЈ¤Ґ¦§Ё©Є«¬­®Ї°±Ііґµ¶·ё№є»јЅѕїАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
  private static final char[] b2c = "ЂЃ‚ѓ„…†‡€‰Љ‹ЊЌЋЏђ‘’“”•–—�™љ›њќћџ ЎўЈ¤Ґ¦§Ё©Є«¬­®Ї°±Ііґµ¶·ё№є»јЅѕїАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".toCharArray();
  private static final char[] c2b = new char['Ԁ'];
  private static final char[] c2bIndex = new char['Ā'];
  
  public MS1251()
  {
    super("windows-1251", StandardCharsets.aliases_MS1251);
  }
  
  public String historicalName()
  {
    return "Cp1251";
  }
  
  public boolean contains(Charset paramCharset)
  {
    return (paramCharset.name().equals("US-ASCII")) || ((paramCharset instanceof MS1251));
  }
  
  public CharsetDecoder newDecoder()
  {
    return new SingleByte.Decoder(this, b2c);
  }
  
  public CharsetEncoder newEncoder()
  {
    return new SingleByte.Encoder(this, c2b, c2bIndex);
  }
  
  static
  {
    char[] arrayOfChar1 = b2c;
    char[] arrayOfChar2 = null;
    SingleByte.initC2B(arrayOfChar1, arrayOfChar2, c2b, c2bIndex);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\MS1251.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */