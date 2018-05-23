package sun.awt;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import sun.nio.cs.HistoricallyNamedCharset;
import sun.security.action.GetPropertyAction;

public class FontDescriptor
  implements Cloneable
{
  String nativeName;
  public CharsetEncoder encoder;
  String charsetName;
  private int[] exclusionRanges;
  public CharsetEncoder unicodeEncoder;
  boolean useUnicode;
  static boolean isLE;
  
  public FontDescriptor(String paramString, CharsetEncoder paramCharsetEncoder, int[] paramArrayOfInt)
  {
    nativeName = paramString;
    encoder = paramCharsetEncoder;
    exclusionRanges = paramArrayOfInt;
    useUnicode = false;
    Charset localCharset = paramCharsetEncoder.charset();
    if ((localCharset instanceof HistoricallyNamedCharset)) {
      charsetName = ((HistoricallyNamedCharset)localCharset).historicalName();
    } else {
      charsetName = localCharset.name();
    }
  }
  
  public String getNativeName()
  {
    return nativeName;
  }
  
  public CharsetEncoder getFontCharsetEncoder()
  {
    return encoder;
  }
  
  public String getFontCharsetName()
  {
    return charsetName;
  }
  
  public int[] getExclusionRanges()
  {
    return exclusionRanges;
  }
  
  public boolean isExcluded(char paramChar)
  {
    int i = 0;
    while (i < exclusionRanges.length)
    {
      char c1 = exclusionRanges[(i++)];
      char c2 = exclusionRanges[(i++)];
      if ((paramChar >= c1) && (paramChar <= c2)) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return super.toString() + " [" + nativeName + "|" + encoder + "]";
  }
  
  private static native void initIDs();
  
  public boolean useUnicode()
  {
    if ((useUnicode) && (unicodeEncoder == null)) {
      try
      {
        unicodeEncoder = (isLE ? StandardCharsets.UTF_16LE.newEncoder() : StandardCharsets.UTF_16BE.newEncoder());
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    return useUnicode;
  }
  
  static
  {
    NativeLibLoader.loadLibraries();
    initIDs();
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.io.unicode.encoding", "UnicodeBig"));
    isLE = !"UnicodeBig".equals(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\FontDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */