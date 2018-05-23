package sun.awt;

import java.awt.peer.FontPeer;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;
import java.util.Vector;
import sun.font.SunFontManager;
import sun.java2d.FontSupport;

public abstract class PlatformFont
  implements FontPeer
{
  protected FontDescriptor[] componentFonts;
  protected char defaultChar;
  protected FontConfiguration fontConfig;
  protected FontDescriptor defaultFont;
  protected String familyName;
  private Object[] fontCache;
  protected static int FONTCACHESIZE = 256;
  protected static int FONTCACHEMASK = FONTCACHESIZE - 1;
  protected static String osVersion;
  
  public PlatformFont(String paramString, int paramInt)
  {
    SunFontManager localSunFontManager = SunFontManager.getInstance();
    if ((localSunFontManager instanceof FontSupport)) {
      fontConfig = localSunFontManager.getFontConfiguration();
    }
    if (fontConfig == null) {
      return;
    }
    familyName = paramString.toLowerCase(Locale.ENGLISH);
    if (!FontConfiguration.isLogicalFontFamilyName(familyName)) {
      familyName = fontConfig.getFallbackFamilyName(familyName, "sansserif");
    }
    componentFonts = fontConfig.getFontDescriptors(familyName, paramInt);
    char c = getMissingGlyphCharacter();
    defaultChar = '?';
    if (componentFonts.length > 0) {
      defaultFont = componentFonts[0];
    }
    for (int i = 0; i < componentFonts.length; i++) {
      if ((!componentFonts[i].isExcluded(c)) && (componentFonts[i].encoder.canEncode(c)))
      {
        defaultFont = componentFonts[i];
        defaultChar = c;
        break;
      }
    }
  }
  
  protected abstract char getMissingGlyphCharacter();
  
  public CharsetString[] makeMultiCharsetString(String paramString)
  {
    return makeMultiCharsetString(paramString.toCharArray(), 0, paramString.length(), true);
  }
  
  public CharsetString[] makeMultiCharsetString(String paramString, boolean paramBoolean)
  {
    return makeMultiCharsetString(paramString.toCharArray(), 0, paramString.length(), paramBoolean);
  }
  
  public CharsetString[] makeMultiCharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return makeMultiCharsetString(paramArrayOfChar, paramInt1, paramInt2, true);
  }
  
  public CharsetString[] makeMultiCharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramInt2 < 1) {
      return new CharsetString[0];
    }
    Vector localVector = null;
    char[] arrayOfChar = new char[paramInt2];
    int i = defaultChar;
    int j = 0;
    Object localObject = defaultFont;
    for (int k = 0; k < componentFonts.length; k++) {
      if ((!componentFonts[k].isExcluded(paramArrayOfChar[paramInt1])) && (componentFonts[k].encoder.canEncode(paramArrayOfChar[paramInt1])))
      {
        localObject = componentFonts[k];
        i = paramArrayOfChar[paramInt1];
        j = 1;
        break;
      }
    }
    if ((!paramBoolean) && (j == 0)) {
      return null;
    }
    arrayOfChar[0] = i;
    k = 0;
    for (int m = 1; m < paramInt2; m++)
    {
      char c = paramArrayOfChar[(paramInt1 + m)];
      FontDescriptor localFontDescriptor = defaultFont;
      i = defaultChar;
      j = 0;
      for (int i1 = 0; i1 < componentFonts.length; i1++) {
        if ((!componentFonts[i1].isExcluded(c)) && (componentFonts[i1].encoder.canEncode(c)))
        {
          localFontDescriptor = componentFonts[i1];
          i = c;
          j = 1;
          break;
        }
      }
      if ((!paramBoolean) && (j == 0)) {
        return null;
      }
      arrayOfChar[m] = i;
      if (localObject != localFontDescriptor)
      {
        if (localVector == null) {
          localVector = new Vector(3);
        }
        localVector.addElement(new CharsetString(arrayOfChar, k, m - k, (FontDescriptor)localObject));
        localObject = localFontDescriptor;
        localFontDescriptor = defaultFont;
        k = m;
      }
    }
    CharsetString localCharsetString = new CharsetString(arrayOfChar, k, paramInt2 - k, (FontDescriptor)localObject);
    CharsetString[] arrayOfCharsetString;
    if (localVector == null)
    {
      arrayOfCharsetString = new CharsetString[1];
      arrayOfCharsetString[0] = localCharsetString;
    }
    else
    {
      localVector.addElement(localCharsetString);
      arrayOfCharsetString = new CharsetString[localVector.size()];
      for (int n = 0; n < localVector.size(); n++) {
        arrayOfCharsetString[n] = ((CharsetString)localVector.elementAt(n));
      }
    }
    return arrayOfCharsetString;
  }
  
  public boolean mightHaveMultiFontMetrics()
  {
    return fontConfig != null;
  }
  
  public Object[] makeConvertedMultiFontString(String paramString)
  {
    return makeConvertedMultiFontChars(paramString.toCharArray(), 0, paramString.length());
  }
  
  public Object[] makeConvertedMultiFontChars(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    Object localObject1 = new Object[2];
    byte[] arrayOfByte = null;
    int i = paramInt1;
    int j = 0;
    int k = 0;
    Object localObject2 = null;
    FontDescriptor localFontDescriptor1 = null;
    int i1 = paramInt1 + paramInt2;
    if ((paramInt1 < 0) || (i1 > paramArrayOfChar.length)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    if (i >= i1) {
      return null;
    }
    while (i < i1)
    {
      int n = paramArrayOfChar[i];
      int m = n & FONTCACHEMASK;
      PlatformFontCache localPlatformFontCache = (PlatformFontCache)getFontCache()[m];
      if ((localPlatformFontCache == null) || (uniChar != n))
      {
        localObject2 = defaultFont;
        n = defaultChar;
        int i2 = paramArrayOfChar[i];
        i3 = componentFonts.length;
        for (int i4 = 0; i4 < i3; i4++)
        {
          FontDescriptor localFontDescriptor2 = componentFonts[i4];
          encoder.reset();
          if ((!localFontDescriptor2.isExcluded(i2)) && (encoder.canEncode(i2)))
          {
            localObject2 = localFontDescriptor2;
            n = i2;
            break;
          }
        }
        try
        {
          char[] arrayOfChar = new char[1];
          arrayOfChar[0] = n;
          localPlatformFontCache = new PlatformFontCache();
          if (((FontDescriptor)localObject2).useUnicode())
          {
            if (FontDescriptor.isLE)
            {
              bb.put((byte)(arrayOfChar[0] & 0xFF));
              bb.put((byte)(arrayOfChar[0] >> '\b'));
            }
            else
            {
              bb.put((byte)(arrayOfChar[0] >> '\b'));
              bb.put((byte)(arrayOfChar[0] & 0xFF));
            }
          }
          else {
            encoder.encode(CharBuffer.wrap(arrayOfChar), bb, true);
          }
          fontDescriptor = ((FontDescriptor)localObject2);
          uniChar = paramArrayOfChar[i];
          getFontCache()[m] = localPlatformFontCache;
        }
        catch (Exception localException)
        {
          System.err.println(localException);
          localException.printStackTrace();
          return null;
        }
      }
      if (localFontDescriptor1 != fontDescriptor)
      {
        if (localFontDescriptor1 != null)
        {
          localObject1[(k++)] = localFontDescriptor1;
          localObject1[(k++)] = arrayOfByte;
          if (arrayOfByte != null)
          {
            j -= 4;
            arrayOfByte[0] = ((byte)(j >> 24));
            arrayOfByte[1] = ((byte)(j >> 16));
            arrayOfByte[2] = ((byte)(j >> 8));
            arrayOfByte[3] = ((byte)j);
          }
          if (k >= localObject1.length)
          {
            localObject3 = new Object[localObject1.length * 2];
            System.arraycopy(localObject1, 0, localObject3, 0, localObject1.length);
            localObject1 = localObject3;
          }
        }
        if (fontDescriptor.useUnicode()) {
          arrayOfByte = new byte[(i1 - i + 1) * (int)fontDescriptor.unicodeEncoder.maxBytesPerChar() + 4];
        } else {
          arrayOfByte = new byte[(i1 - i + 1) * (int)fontDescriptor.encoder.maxBytesPerChar() + 4];
        }
        j = 4;
        localFontDescriptor1 = fontDescriptor;
      }
      Object localObject3 = bb.array();
      int i3 = bb.position();
      if (i3 == 1)
      {
        arrayOfByte[(j++)] = localObject3[0];
      }
      else if (i3 == 2)
      {
        arrayOfByte[(j++)] = localObject3[0];
        arrayOfByte[(j++)] = localObject3[1];
      }
      else if (i3 == 3)
      {
        arrayOfByte[(j++)] = localObject3[0];
        arrayOfByte[(j++)] = localObject3[1];
        arrayOfByte[(j++)] = localObject3[2];
      }
      else if (i3 == 4)
      {
        arrayOfByte[(j++)] = localObject3[0];
        arrayOfByte[(j++)] = localObject3[1];
        arrayOfByte[(j++)] = localObject3[2];
        arrayOfByte[(j++)] = localObject3[3];
      }
      i++;
    }
    localObject1[(k++)] = localFontDescriptor1;
    localObject1[k] = arrayOfByte;
    if (arrayOfByte != null)
    {
      j -= 4;
      arrayOfByte[0] = ((byte)(j >> 24));
      arrayOfByte[1] = ((byte)(j >> 16));
      arrayOfByte[2] = ((byte)(j >> 8));
      arrayOfByte[3] = ((byte)j);
    }
    return (Object[])localObject1;
  }
  
  protected final Object[] getFontCache()
  {
    if (fontCache == null) {
      fontCache = new Object[FONTCACHESIZE];
    }
    return fontCache;
  }
  
  private static native void initIDs();
  
  static
  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
  
  class PlatformFontCache
  {
    char uniChar;
    FontDescriptor fontDescriptor;
    ByteBuffer bb = ByteBuffer.allocate(4);
    
    PlatformFontCache() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\PlatformFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */