package sun.font;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.plaf.FontUIResource;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public final class FontUtilities
{
  public static boolean isSolaris;
  public static boolean isLinux;
  public static boolean isMacOSX;
  public static boolean isSolaris8;
  public static boolean isSolaris9;
  public static boolean isOpenSolaris;
  public static boolean useT2K;
  public static boolean isWindows;
  public static boolean isOpenJDK;
  static final String LUCIDA_FILE_NAME = "LucidaSansRegular.ttf";
  private static boolean debugFonts = false;
  private static PlatformLogger logger = null;
  private static boolean logging;
  public static final int MIN_LAYOUT_CHARCODE = 768;
  public static final int MAX_LAYOUT_CHARCODE = 8303;
  private static volatile SoftReference<ConcurrentHashMap<PhysicalFont, CompositeFont>> compMapRef = new SoftReference(null);
  private static final String[][] nameMap = { { "sans", "sansserif" }, { "sans-serif", "sansserif" }, { "serif", "serif" }, { "monospace", "monospaced" } };
  
  public FontUtilities() {}
  
  public static Font2D getFont2D(Font paramFont)
  {
    return FontAccess.getFontAccess().getFont2D(paramFont);
  }
  
  public static boolean isComplexText(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if ((paramArrayOfChar[i] >= '̀') && (isNonSimpleChar(paramArrayOfChar[i]))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isNonSimpleChar(char paramChar)
  {
    return (isComplexCharCode(paramChar)) || ((paramChar >= 55296) && (paramChar <= 57343));
  }
  
  public static boolean isComplexCharCode(int paramInt)
  {
    if ((paramInt < 768) || (paramInt > 8303)) {
      return false;
    }
    if (paramInt <= 879) {
      return true;
    }
    if (paramInt < 1424) {
      return false;
    }
    if (paramInt <= 1791) {
      return true;
    }
    if (paramInt < 2304) {
      return false;
    }
    if (paramInt <= 3711) {
      return true;
    }
    if (paramInt < 3840) {
      return false;
    }
    if (paramInt <= 4095) {
      return true;
    }
    if (paramInt < 4352) {
      return false;
    }
    if (paramInt < 4607) {
      return true;
    }
    if (paramInt < 6016) {
      return false;
    }
    if (paramInt <= 6143) {
      return true;
    }
    if (paramInt < 8204) {
      return false;
    }
    if (paramInt <= 8205) {
      return true;
    }
    if ((paramInt >= 8234) && (paramInt <= 8238)) {
      return true;
    }
    return (paramInt >= 8298) && (paramInt <= 8303);
  }
  
  public static PlatformLogger getLogger()
  {
    return logger;
  }
  
  public static boolean isLogging()
  {
    return logging;
  }
  
  public static boolean debugFonts()
  {
    return debugFonts;
  }
  
  public static boolean fontSupportsDefaultEncoding(Font paramFont)
  {
    return getFont2D(paramFont) instanceof CompositeFont;
  }
  
  public static FontUIResource getCompositeFontUIResource(Font paramFont)
  {
    FontUIResource localFontUIResource = new FontUIResource(paramFont);
    Font2D localFont2D1 = getFont2D(paramFont);
    if (!(localFont2D1 instanceof PhysicalFont)) {
      return localFontUIResource;
    }
    FontManager localFontManager = FontManagerFactory.getInstance();
    Font2D localFont2D2 = localFontManager.findFont2D("dialog", paramFont.getStyle(), 0);
    if ((localFont2D2 == null) || (!(localFont2D2 instanceof CompositeFont))) {
      return localFontUIResource;
    }
    CompositeFont localCompositeFont1 = (CompositeFont)localFont2D2;
    PhysicalFont localPhysicalFont = (PhysicalFont)localFont2D1;
    ConcurrentHashMap localConcurrentHashMap = (ConcurrentHashMap)compMapRef.get();
    if (localConcurrentHashMap == null)
    {
      localConcurrentHashMap = new ConcurrentHashMap();
      compMapRef = new SoftReference(localConcurrentHashMap);
    }
    CompositeFont localCompositeFont2 = (CompositeFont)localConcurrentHashMap.get(localPhysicalFont);
    if (localCompositeFont2 == null)
    {
      localCompositeFont2 = new CompositeFont(localPhysicalFont, localCompositeFont1);
      localConcurrentHashMap.put(localPhysicalFont, localCompositeFont2);
    }
    FontAccess.getFontAccess().setFont2D(localFontUIResource, handle);
    FontAccess.getFontAccess().setCreatedFont(localFontUIResource);
    return localFontUIResource;
  }
  
  public static String mapFcName(String paramString)
  {
    for (int i = 0; i < nameMap.length; i++) {
      if (paramString.equals(nameMap[i][0])) {
        return nameMap[i][1];
      }
    }
    return null;
  }
  
  public static FontUIResource getFontConfigFUIR(String paramString, int paramInt1, int paramInt2)
  {
    String str = mapFcName(paramString);
    if (str == null) {
      str = "sansserif";
    }
    FontManager localFontManager = FontManagerFactory.getInstance();
    FontUIResource localFontUIResource;
    if ((localFontManager instanceof SunFontManager))
    {
      SunFontManager localSunFontManager = (SunFontManager)localFontManager;
      localFontUIResource = localSunFontManager.getFontConfigFUIR(str, paramInt1, paramInt2);
    }
    else
    {
      localFontUIResource = new FontUIResource(str, paramInt1, paramInt2);
    }
    return localFontUIResource;
  }
  
  public static boolean textLayoutIsCompatible(Font paramFont)
  {
    Font2D localFont2D = getFont2D(paramFont);
    if ((localFont2D instanceof TrueTypeFont))
    {
      TrueTypeFont localTrueTypeFont = (TrueTypeFont)localFont2D;
      return (localTrueTypeFont.getDirectoryEntry(1196643650) == null) || (localTrueTypeFont.getDirectoryEntry(1196445523) != null);
    }
    return false;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        String str1 = System.getProperty("os.name", "unknownOS");
        FontUtilities.isSolaris = str1.startsWith("SunOS");
        FontUtilities.isLinux = str1.startsWith("Linux");
        FontUtilities.isMacOSX = str1.contains("OS X");
        String str2 = System.getProperty("sun.java2d.font.scaler");
        if (str2 != null) {
          FontUtilities.useT2K = "t2k".equals(str2);
        } else {
          FontUtilities.useT2K = false;
        }
        if (FontUtilities.isSolaris)
        {
          str3 = System.getProperty("os.version", "0.0");
          FontUtilities.isSolaris8 = str3.startsWith("5.8");
          FontUtilities.isSolaris9 = str3.startsWith("5.9");
          float f = Float.parseFloat(str3);
          if (f > 5.1F)
          {
            localFile = new File("/etc/release");
            str5 = null;
            try
            {
              FileInputStream localFileInputStream = new FileInputStream(localFile);
              InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream, "ISO-8859-1");
              BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
              str5 = localBufferedReader.readLine();
              localFileInputStream.close();
            }
            catch (Exception localException) {}
            if ((str5 != null) && (str5.indexOf("OpenSolaris") >= 0)) {
              FontUtilities.isOpenSolaris = true;
            } else {
              FontUtilities.isOpenSolaris = false;
            }
          }
          else
          {
            FontUtilities.isOpenSolaris = false;
          }
        }
        else
        {
          FontUtilities.isSolaris8 = false;
          FontUtilities.isSolaris9 = false;
          FontUtilities.isOpenSolaris = false;
        }
        FontUtilities.isWindows = str1.startsWith("Windows");
        String str3 = System.getProperty("java.home", "") + File.separator + "lib";
        String str4 = str3 + File.separator + "fonts";
        File localFile = new File(str4 + File.separator + "LucidaSansRegular.ttf");
        FontUtilities.isOpenJDK = !localFile.exists();
        String str5 = System.getProperty("sun.java2d.debugfonts");
        if ((str5 != null) && (!str5.equals("false")))
        {
          FontUtilities.access$002(true);
          FontUtilities.access$102(PlatformLogger.getLogger("sun.java2d"));
          if (str5.equals("warning")) {
            FontUtilities.logger.setLevel(PlatformLogger.Level.WARNING);
          } else if (str5.equals("severe")) {
            FontUtilities.logger.setLevel(PlatformLogger.Level.SEVERE);
          }
        }
        if (FontUtilities.debugFonts)
        {
          FontUtilities.access$102(PlatformLogger.getLogger("sun.java2d"));
          FontUtilities.access$202(FontUtilities.logger.isEnabled());
        }
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */