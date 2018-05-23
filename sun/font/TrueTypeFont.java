package sun.font;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Point2D.Float;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import sun.awt.SunToolkit;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class TrueTypeFont
  extends FileFont
{
  public static final int cmapTag = 1668112752;
  public static final int glyfTag = 1735162214;
  public static final int headTag = 1751474532;
  public static final int hheaTag = 1751672161;
  public static final int hmtxTag = 1752003704;
  public static final int locaTag = 1819239265;
  public static final int maxpTag = 1835104368;
  public static final int nameTag = 1851878757;
  public static final int postTag = 1886352244;
  public static final int os_2Tag = 1330851634;
  public static final int GDEFTag = 1195656518;
  public static final int GPOSTag = 1196445523;
  public static final int GSUBTag = 1196643650;
  public static final int mortTag = 1836020340;
  public static final int fdscTag = 1717859171;
  public static final int fvarTag = 1719034226;
  public static final int featTag = 1717920116;
  public static final int EBLCTag = 1161972803;
  public static final int gaspTag = 1734439792;
  public static final int ttcfTag = 1953784678;
  public static final int v1ttTag = 65536;
  public static final int trueTag = 1953658213;
  public static final int ottoTag = 1330926671;
  public static final int MS_PLATFORM_ID = 3;
  public static final short ENGLISH_LOCALE_ID = 1033;
  public static final int FAMILY_NAME_ID = 1;
  public static final int FULL_NAME_ID = 4;
  public static final int POSTSCRIPT_NAME_ID = 6;
  private static final short US_LCID = 1033;
  private static Map<String, Short> lcidMap;
  TTDisposerRecord disposerRecord = new TTDisposerRecord(null);
  int fontIndex = 0;
  int directoryCount = 1;
  int directoryOffset;
  int numTables;
  DirectoryEntry[] tableDirectory;
  private boolean supportsJA;
  private boolean supportsCJK;
  private Locale nameLocale;
  private String localeFamilyName;
  private String localeFullName;
  private static final int TTCHEADERSIZE = 12;
  private static final int DIRECTORYHEADERSIZE = 12;
  private static final int DIRECTORYENTRYSIZE = 16;
  static final String[] encoding_mapping = { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "", "", "", "", "", "", "", "", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361", "", "", "", "", "", "", "", "", "", "" };
  private static final String[][] languages = { { "en", "ca", "da", "de", "es", "fi", "fr", "is", "it", "nl", "no", "pt", "sq", "sv" }, { "cs", "cz", "et", "hr", "hu", "nr", "pl", "ro", "sk", "sl", "sq", "sr" }, { "bg", "mk", "ru", "sh", "uk" }, { "el" }, { "tr" }, { "he" }, { "ar" }, { "et", "lt", "lv" }, { "th" }, { "ja" }, { "zh", "zh_CN" }, { "ko" }, { "zh_HK", "zh_TW" }, { "ko" } };
  private static final String[] codePages = { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361" };
  private static String defaultCodePage = null;
  public static final int reserved_bits1 = Integer.MIN_VALUE;
  public static final int reserved_bits2 = 65535;
  private int fontWidth = 0;
  private int fontWeight = 0;
  private static final int fsSelectionItalicBit = 1;
  private static final int fsSelectionBoldBit = 32;
  private static final int fsSelectionRegularBit = 64;
  private float stSize;
  private float stPos;
  private float ulSize;
  private float ulPos;
  private char[] gaspTable;
  
  public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean)
    throws FontFormatException
  {
    this(paramString, paramObject, paramInt, paramBoolean, true);
  }
  
  public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws FontFormatException
  {
    super(paramString, paramObject);
    useJavaRasterizer = paramBoolean1;
    fontRank = 3;
    try
    {
      verify(paramBoolean2);
      init(paramInt);
      if (!paramBoolean2) {
        close();
      }
    }
    catch (Throwable localThrowable)
    {
      close();
      if ((localThrowable instanceof FontFormatException)) {
        throw ((FontFormatException)localThrowable);
      }
      throw new FontFormatException("Unexpected runtime exception.");
    }
    Disposer.addObjectRecord(this, disposerRecord);
  }
  
  protected boolean checkUseNatives()
  {
    if (checkedNatives) {
      return useNatives;
    }
    if ((!FontUtilities.isSolaris) || (useJavaRasterizer) || (FontUtilities.useT2K) || (nativeNames == null) || (getDirectoryEntry(1161972803) != null) || (GraphicsEnvironment.isHeadless()))
    {
      checkedNatives = true;
      return false;
    }
    Object localObject;
    if ((nativeNames instanceof String))
    {
      localObject = (String)nativeNames;
      if (((String)localObject).indexOf("8859") > 0)
      {
        checkedNatives = true;
        return false;
      }
      if (NativeFont.hasExternalBitmaps((String)localObject))
      {
        nativeFonts = new NativeFont[1];
        try
        {
          nativeFonts[0] = new NativeFont((String)localObject, true);
          useNatives = true;
        }
        catch (FontFormatException localFontFormatException1)
        {
          nativeFonts = null;
        }
      }
    }
    else if ((nativeNames instanceof String[]))
    {
      localObject = (String[])nativeNames;
      int i = localObject.length;
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        if (localObject[k].indexOf("8859") > 0)
        {
          checkedNatives = true;
          return false;
        }
        if (NativeFont.hasExternalBitmaps(localObject[k])) {
          j = 1;
        }
      }
      if (j == 0)
      {
        checkedNatives = true;
        return false;
      }
      useNatives = true;
      nativeFonts = new NativeFont[i];
      for (k = 0; k < i; k++) {
        try
        {
          nativeFonts[k] = new NativeFont(localObject[k], true);
        }
        catch (FontFormatException localFontFormatException2)
        {
          useNatives = false;
          nativeFonts = null;
        }
      }
    }
    if (useNatives) {
      glyphToCharMap = new char[getMapper().getNumGlyphs()];
    }
    checkedNatives = true;
    return useNatives;
  }
  
  private synchronized FileChannel open()
    throws FontFormatException
  {
    return open(true);
  }
  
  private synchronized FileChannel open(boolean paramBoolean)
    throws FontFormatException
  {
    if (disposerRecord.channel == null)
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().info("open TTF: " + platName);
      }
      try
      {
        RandomAccessFile localRandomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            try
            {
              return new RandomAccessFile(platName, "r");
            }
            catch (FileNotFoundException localFileNotFoundException) {}
            return null;
          }
        });
        disposerRecord.channel = localRandomAccessFile.getChannel();
        fileSize = ((int)disposerRecord.channel.size());
        if (paramBoolean)
        {
          FontManager localFontManager = FontManagerFactory.getInstance();
          if ((localFontManager instanceof SunFontManager)) {
            ((SunFontManager)localFontManager).addToPool(this);
          }
        }
      }
      catch (NullPointerException localNullPointerException)
      {
        close();
        throw new FontFormatException(localNullPointerException.toString());
      }
      catch (ClosedChannelException localClosedChannelException)
      {
        Thread.interrupted();
        close();
        open();
      }
      catch (IOException localIOException)
      {
        close();
        throw new FontFormatException(localIOException.toString());
      }
    }
    return disposerRecord.channel;
  }
  
  protected synchronized void close()
  {
    disposerRecord.dispose();
  }
  
  int readBlock(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    int i = 0;
    try
    {
      synchronized (this)
      {
        if (disposerRecord.channel == null) {
          open();
        }
        if (paramInt1 + paramInt2 > fileSize)
        {
          if (paramInt1 >= fileSize)
          {
            if (FontUtilities.isLogging())
            {
              String str1 = "Read offset is " + paramInt1 + " file size is " + fileSize + " file is " + platName;
              FontUtilities.getLogger().severe(str1);
            }
            return -1;
          }
          paramInt2 = fileSize - paramInt1;
        }
        paramByteBuffer.clear();
        disposerRecord.channel.position(paramInt1);
        while (i < paramInt2)
        {
          int j = disposerRecord.channel.read(paramByteBuffer);
          if (j == -1)
          {
            String str2 = "Unexpected EOF " + this;
            int k = (int)disposerRecord.channel.size();
            if (k != fileSize) {
              str2 = str2 + " File size was " + fileSize + " and now is " + k;
            }
            if (FontUtilities.isLogging()) {
              FontUtilities.getLogger().severe(str2);
            }
            if ((i > paramInt2 / 2) || (i > 16384))
            {
              paramByteBuffer.flip();
              if (FontUtilities.isLogging())
              {
                str2 = "Returning " + i + " bytes instead of " + paramInt2;
                FontUtilities.getLogger().severe(str2);
              }
            }
            else
            {
              i = -1;
            }
            throw new IOException(str2);
          }
          i += j;
        }
        paramByteBuffer.flip();
        if (i > paramInt2) {
          i = paramInt2;
        }
      }
    }
    catch (FontFormatException localFontFormatException)
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().severe("While reading " + platName, localFontFormatException);
      }
      i = -1;
      deregisterFontAndClearStrikeCache();
    }
    catch (ClosedChannelException localClosedChannelException)
    {
      Thread.interrupted();
      close();
      return readBlock(paramByteBuffer, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().severe("While reading " + platName, localIOException);
      }
      if (i == 0)
      {
        i = -1;
        deregisterFontAndClearStrikeCache();
      }
    }
    return i;
  }
  
  ByteBuffer readBlock(int paramInt1, int paramInt2)
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(paramInt2);
    try
    {
      synchronized (this)
      {
        if (disposerRecord.channel == null) {
          open();
        }
        if (paramInt1 + paramInt2 > fileSize)
        {
          if (paramInt1 > fileSize) {
            return null;
          }
          localByteBuffer = ByteBuffer.allocate(fileSize - paramInt1);
        }
        disposerRecord.channel.position(paramInt1);
        disposerRecord.channel.read(localByteBuffer);
        localByteBuffer.flip();
      }
    }
    catch (FontFormatException localFontFormatException)
    {
      return null;
    }
    catch (ClosedChannelException localClosedChannelException)
    {
      Thread.interrupted();
      close();
      readBlock(localByteBuffer, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      return null;
    }
    return localByteBuffer;
  }
  
  byte[] readBytes(int paramInt1, int paramInt2)
  {
    ByteBuffer localByteBuffer = readBlock(paramInt1, paramInt2);
    if (localByteBuffer.hasArray()) {
      return localByteBuffer.array();
    }
    byte[] arrayOfByte = new byte[localByteBuffer.limit()];
    localByteBuffer.get(arrayOfByte);
    return arrayOfByte;
  }
  
  private void verify(boolean paramBoolean)
    throws FontFormatException
  {
    open(paramBoolean);
  }
  
  protected void init(int paramInt)
    throws FontFormatException
  {
    int i = 0;
    ByteBuffer localByteBuffer1 = readBlock(0, 12);
    try
    {
      switch (localByteBuffer1.getInt())
      {
      case 1953784678: 
        localByteBuffer1.getInt();
        directoryCount = localByteBuffer1.getInt();
        if (paramInt >= directoryCount) {
          throw new FontFormatException("Bad collection index");
        }
        fontIndex = paramInt;
        localByteBuffer1 = readBlock(12 + 4 * paramInt, 4);
        i = localByteBuffer1.getInt();
        break;
      case 65536: 
      case 1330926671: 
      case 1953658213: 
        break;
      default: 
        throw new FontFormatException("Unsupported sfnt " + getPublicFileName());
      }
      localByteBuffer1 = readBlock(i + 4, 2);
      numTables = localByteBuffer1.getShort();
      directoryOffset = (i + 12);
      ByteBuffer localByteBuffer2 = readBlock(directoryOffset, numTables * 16);
      IntBuffer localIntBuffer = localByteBuffer2.asIntBuffer();
      tableDirectory = new DirectoryEntry[numTables];
      for (int j = 0; j < numTables; j++)
      {
        DirectoryEntry localDirectoryEntry;
        tableDirectory[j] = (localDirectoryEntry = new DirectoryEntry());
        tag = localIntBuffer.get();
        localIntBuffer.get();
        offset = localIntBuffer.get();
        length = localIntBuffer.get();
        if (offset + length > fileSize) {
          throw new FontFormatException("bad table, tag=" + tag);
        }
      }
      if (getDirectoryEntry(1751474532) == null) {
        throw new FontFormatException("missing head table");
      }
      if (getDirectoryEntry(1835104368) == null) {
        throw new FontFormatException("missing maxp table");
      }
      if ((getDirectoryEntry(1752003704) != null) && (getDirectoryEntry(1751672161) == null)) {
        throw new FontFormatException("missing hhea table");
      }
      initNames();
    }
    catch (Exception localException)
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().severe(localException.toString());
      }
      if ((localException instanceof FontFormatException)) {
        throw ((FontFormatException)localException);
      }
      throw new FontFormatException(localException.toString());
    }
    if ((familyName == null) || (fullName == null)) {
      throw new FontFormatException("Font name not found");
    }
    ByteBuffer localByteBuffer3 = getTableBuffer(1330851634);
    setStyle(localByteBuffer3);
    setCJKSupport(localByteBuffer3);
  }
  
  static String getCodePage()
  {
    if (defaultCodePage != null) {
      return defaultCodePage;
    }
    if (FontUtilities.isWindows)
    {
      defaultCodePage = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
    }
    else
    {
      if (languages.length != codePages.length) {
        throw new InternalError("wrong code pages array length");
      }
      Locale localLocale = SunToolkit.getStartupLocale();
      String str1 = localLocale.getLanguage();
      if (str1 != null)
      {
        if (str1.equals("zh"))
        {
          String str2 = localLocale.getCountry();
          if (str2 != null) {
            str1 = str1 + "_" + str2;
          }
        }
        for (int i = 0; i < languages.length; i++) {
          for (int j = 0; j < languages[i].length; j++) {
            if (str1.equals(languages[i][j]))
            {
              defaultCodePage = codePages[i];
              return defaultCodePage;
            }
          }
        }
      }
    }
    if (defaultCodePage == null) {
      defaultCodePage = "";
    }
    return defaultCodePage;
  }
  
  boolean supportsEncoding(String paramString)
  {
    if (paramString == null) {
      paramString = getCodePage();
    }
    if ("".equals(paramString)) {
      return false;
    }
    paramString = paramString.toLowerCase();
    if (paramString.equals("gb18030")) {
      paramString = "gbk";
    } else if (paramString.equals("ms950_hkscs")) {
      paramString = "ms950";
    }
    ByteBuffer localByteBuffer = getTableBuffer(1330851634);
    if ((localByteBuffer == null) || (localByteBuffer.capacity() < 86)) {
      return false;
    }
    int i = localByteBuffer.getInt(78);
    int j = localByteBuffer.getInt(82);
    for (int k = 0; k < encoding_mapping.length; k++) {
      if ((encoding_mapping[k].equals(paramString)) && ((1 << k & i) != 0)) {
        return true;
      }
    }
    return false;
  }
  
  private void setCJKSupport(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 50)) {
      return;
    }
    int i = paramByteBuffer.getInt(46);
    supportsCJK = ((i & 0x29BF0000) != 0);
    supportsJA = ((i & 0x60000) != 0);
  }
  
  boolean supportsJA()
  {
    return supportsJA;
  }
  
  ByteBuffer getTableBuffer(int paramInt)
  {
    DirectoryEntry localDirectoryEntry = null;
    for (int i = 0; i < numTables; i++) {
      if (tableDirectory[i].tag == paramInt)
      {
        localDirectoryEntry = tableDirectory[i];
        break;
      }
    }
    if ((localDirectoryEntry == null) || (length == 0) || (offset + length > fileSize)) {
      return null;
    }
    i = 0;
    ByteBuffer localByteBuffer = ByteBuffer.allocate(length);
    synchronized (this)
    {
      try
      {
        if (disposerRecord.channel == null) {
          open();
        }
        disposerRecord.channel.position(offset);
        i = disposerRecord.channel.read(localByteBuffer);
        localByteBuffer.flip();
      }
      catch (ClosedChannelException localClosedChannelException)
      {
        Thread.interrupted();
        close();
        return getTableBuffer(paramInt);
      }
      catch (IOException localIOException)
      {
        return null;
      }
      catch (FontFormatException localFontFormatException)
      {
        return null;
      }
      if (i < length) {
        return null;
      }
      return localByteBuffer;
    }
  }
  
  long getLayoutTableCache()
  {
    try
    {
      return getScaler().getLayoutTableCache();
    }
    catch (FontScalerException localFontScalerException) {}
    return 0L;
  }
  
  byte[] getTableBytes(int paramInt)
  {
    ByteBuffer localByteBuffer = getTableBuffer(paramInt);
    if (localByteBuffer == null) {
      return null;
    }
    if (localByteBuffer.hasArray()) {
      try
      {
        return localByteBuffer.array();
      }
      catch (Exception localException) {}
    }
    byte[] arrayOfByte = new byte[getTableSize(paramInt)];
    localByteBuffer.get(arrayOfByte);
    return arrayOfByte;
  }
  
  int getTableSize(int paramInt)
  {
    for (int i = 0; i < numTables; i++) {
      if (tableDirectory[i].tag == paramInt) {
        return tableDirectory[i].length;
      }
    }
    return 0;
  }
  
  int getTableOffset(int paramInt)
  {
    for (int i = 0; i < numTables; i++) {
      if (tableDirectory[i].tag == paramInt) {
        return tableDirectory[i].offset;
      }
    }
    return 0;
  }
  
  DirectoryEntry getDirectoryEntry(int paramInt)
  {
    for (int i = 0; i < numTables; i++) {
      if (tableDirectory[i].tag == paramInt) {
        return tableDirectory[i];
      }
    }
    return null;
  }
  
  boolean useEmbeddedBitmapsForSize(int paramInt)
  {
    if (!supportsCJK) {
      return false;
    }
    if (getDirectoryEntry(1161972803) == null) {
      return false;
    }
    ByteBuffer localByteBuffer = getTableBuffer(1161972803);
    int i = localByteBuffer.getInt(4);
    for (int j = 0; j < i; j++)
    {
      int k = localByteBuffer.get(8 + j * 48 + 45) & 0xFF;
      if (k == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public String getFullName()
  {
    return fullName;
  }
  
  protected void setStyle()
  {
    setStyle(getTableBuffer(1330851634));
  }
  
  public int getWidth()
  {
    return fontWidth > 0 ? fontWidth : super.getWidth();
  }
  
  public int getWeight()
  {
    return fontWeight > 0 ? fontWeight : super.getWeight();
  }
  
  private void setStyle(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer == null) {
      return;
    }
    if (paramByteBuffer.capacity() >= 8)
    {
      fontWeight = (paramByteBuffer.getChar(4) & 0xFFFF);
      fontWidth = (paramByteBuffer.getChar(6) & 0xFFFF);
    }
    if (paramByteBuffer.capacity() < 64)
    {
      super.setStyle();
      return;
    }
    int i = paramByteBuffer.getChar(62) & 0xFFFF;
    int j = i & 0x1;
    int k = i & 0x20;
    int m = i & 0x40;
    if ((m != 0) && ((j | k) != 0))
    {
      super.setStyle();
      return;
    }
    if ((m | j | k) == 0)
    {
      super.setStyle();
      return;
    }
    switch (k | j)
    {
    case 1: 
      style = 2;
      break;
    case 32: 
      if ((FontUtilities.isSolaris) && (platName.endsWith("HG-GothicB.ttf"))) {
        style = 0;
      } else {
        style = 1;
      }
      break;
    case 33: 
      style = 3;
    }
  }
  
  private void setStrikethroughMetrics(ByteBuffer paramByteBuffer, int paramInt)
  {
    if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 30) || (paramInt < 0))
    {
      stSize = 0.05F;
      stPos = -0.4F;
      return;
    }
    ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
    stSize = (localShortBuffer.get(13) / paramInt);
    stPos = (-localShortBuffer.get(14) / paramInt);
  }
  
  private void setUnderlineMetrics(ByteBuffer paramByteBuffer, int paramInt)
  {
    if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 12) || (paramInt < 0))
    {
      ulSize = 0.05F;
      ulPos = 0.1F;
      return;
    }
    ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
    ulSize = (localShortBuffer.get(5) / paramInt);
    ulPos = (-localShortBuffer.get(4) / paramInt);
  }
  
  public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt)
  {
    if ((ulSize == 0.0F) && (ulPos == 0.0F))
    {
      ByteBuffer localByteBuffer1 = getTableBuffer(1751474532);
      int i = -1;
      if ((localByteBuffer1 != null) && (localByteBuffer1.capacity() >= 18))
      {
        localObject = localByteBuffer1.asShortBuffer();
        i = ((ShortBuffer)localObject).get(9) & 0xFFFF;
        if ((i < 16) || (i > 16384)) {
          i = 2048;
        }
      }
      Object localObject = getTableBuffer(1330851634);
      setStrikethroughMetrics((ByteBuffer)localObject, i);
      ByteBuffer localByteBuffer2 = getTableBuffer(1886352244);
      setUnderlineMetrics(localByteBuffer2, i);
    }
    paramArrayOfFloat[paramInt] = (stPos * paramFloat);
    paramArrayOfFloat[(paramInt + 1)] = (stSize * paramFloat);
    paramArrayOfFloat[(paramInt + 2)] = (ulPos * paramFloat);
    paramArrayOfFloat[(paramInt + 3)] = (ulSize * paramFloat);
  }
  
  private String makeString(byte[] paramArrayOfByte, int paramInt, short paramShort)
  {
    Object localObject;
    if ((paramShort >= 2) && (paramShort <= 6))
    {
      localObject = paramArrayOfByte;
      int i = paramInt;
      paramArrayOfByte = new byte[i];
      paramInt = 0;
      for (int j = 0; j < i; j++) {
        if (localObject[j] != 0) {
          paramArrayOfByte[(paramInt++)] = localObject[j];
        }
      }
    }
    switch (paramShort)
    {
    case 1: 
      localObject = "UTF-16";
      break;
    case 0: 
      localObject = "UTF-16";
      break;
    case 2: 
      localObject = "SJIS";
      break;
    case 3: 
      localObject = "GBK";
      break;
    case 4: 
      localObject = "MS950";
      break;
    case 5: 
      localObject = "EUC_KR";
      break;
    case 6: 
      localObject = "Johab";
      break;
    default: 
      localObject = "UTF-16";
    }
    try
    {
      return new String(paramArrayOfByte, 0, paramInt, (String)localObject);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().warning(localUnsupportedEncodingException + " EncodingID=" + paramShort);
      }
      return new String(paramArrayOfByte, 0, paramInt);
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  protected void initNames()
  {
    byte[] arrayOfByte = new byte['Ā'];
    ByteBuffer localByteBuffer = getTableBuffer(1851878757);
    if (localByteBuffer != null)
    {
      ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
      localShortBuffer.get();
      int i = localShortBuffer.get();
      int j = localShortBuffer.get() & 0xFFFF;
      nameLocale = SunToolkit.getStartupLocale();
      int k = getLCIDFromLocale(nameLocale);
      for (int m = 0; m < i; m++)
      {
        int n = localShortBuffer.get();
        if (n != 3)
        {
          localShortBuffer.position(localShortBuffer.position() + 5);
        }
        else
        {
          short s = localShortBuffer.get();
          int i1 = localShortBuffer.get();
          int i2 = localShortBuffer.get();
          int i3 = localShortBuffer.get() & 0xFFFF;
          int i4 = (localShortBuffer.get() & 0xFFFF) + j;
          String str = null;
          switch (i2)
          {
          case 1: 
            if ((familyName == null) || (i1 == 1033) || (i1 == k))
            {
              localByteBuffer.position(i4);
              localByteBuffer.get(arrayOfByte, 0, i3);
              str = makeString(arrayOfByte, i3, s);
              if ((familyName == null) || (i1 == 1033)) {
                familyName = str;
              }
              if (i1 == k) {
                localeFamilyName = str;
              }
            }
            break;
          case 4: 
            if ((fullName == null) || (i1 == 1033) || (i1 == k))
            {
              localByteBuffer.position(i4);
              localByteBuffer.get(arrayOfByte, 0, i3);
              str = makeString(arrayOfByte, i3, s);
              if ((fullName == null) || (i1 == 1033)) {
                fullName = str;
              }
              if (i1 == k) {
                localeFullName = str;
              }
            }
            break;
          }
        }
      }
      if (localeFamilyName == null) {
        localeFamilyName = familyName;
      }
      if (localeFullName == null) {
        localeFullName = fullName;
      }
    }
  }
  
  protected String lookupName(short paramShort, int paramInt)
  {
    String str = null;
    byte[] arrayOfByte = new byte['Ѐ'];
    ByteBuffer localByteBuffer = getTableBuffer(1851878757);
    if (localByteBuffer != null)
    {
      ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
      localShortBuffer.get();
      int i = localShortBuffer.get();
      int j = localShortBuffer.get() & 0xFFFF;
      for (int k = 0; k < i; k++)
      {
        int m = localShortBuffer.get();
        if (m != 3)
        {
          localShortBuffer.position(localShortBuffer.position() + 5);
        }
        else
        {
          short s1 = localShortBuffer.get();
          short s2 = localShortBuffer.get();
          int n = localShortBuffer.get();
          int i1 = localShortBuffer.get() & 0xFFFF;
          int i2 = (localShortBuffer.get() & 0xFFFF) + j;
          if ((n == paramInt) && (((str == null) && (s2 == 1033)) || (s2 == paramShort)))
          {
            localByteBuffer.position(i2);
            localByteBuffer.get(arrayOfByte, 0, i1);
            str = makeString(arrayOfByte, i1, s1);
            if (s2 == paramShort) {
              return str;
            }
          }
        }
      }
    }
    return str;
  }
  
  public int getFontCount()
  {
    return directoryCount;
  }
  
  protected synchronized FontScaler getScaler()
  {
    if (scaler == null) {
      scaler = FontScaler.getScaler(this, fontIndex, supportsCJK, fileSize);
    }
    return scaler;
  }
  
  public String getPostscriptName()
  {
    String str = lookupName((short)1033, 6);
    if (str == null) {
      return fullName;
    }
    return str;
  }
  
  public String getFontName(Locale paramLocale)
  {
    if (paramLocale == null) {
      return fullName;
    }
    if ((paramLocale.equals(nameLocale)) && (localeFullName != null)) {
      return localeFullName;
    }
    short s = getLCIDFromLocale(paramLocale);
    String str = lookupName(s, 4);
    if (str == null) {
      return fullName;
    }
    return str;
  }
  
  private static void addLCIDMapEntry(Map<String, Short> paramMap, String paramString, short paramShort)
  {
    paramMap.put(paramString, Short.valueOf(paramShort));
  }
  
  private static synchronized void createLCIDMap()
  {
    if (lcidMap != null) {
      return;
    }
    HashMap localHashMap = new HashMap(200);
    addLCIDMapEntry(localHashMap, "ar", (short)1025);
    addLCIDMapEntry(localHashMap, "bg", (short)1026);
    addLCIDMapEntry(localHashMap, "ca", (short)1027);
    addLCIDMapEntry(localHashMap, "zh", (short)1028);
    addLCIDMapEntry(localHashMap, "cs", (short)1029);
    addLCIDMapEntry(localHashMap, "da", (short)1030);
    addLCIDMapEntry(localHashMap, "de", (short)1031);
    addLCIDMapEntry(localHashMap, "el", (short)1032);
    addLCIDMapEntry(localHashMap, "es", (short)1034);
    addLCIDMapEntry(localHashMap, "fi", (short)1035);
    addLCIDMapEntry(localHashMap, "fr", (short)1036);
    addLCIDMapEntry(localHashMap, "iw", (short)1037);
    addLCIDMapEntry(localHashMap, "hu", (short)1038);
    addLCIDMapEntry(localHashMap, "is", (short)1039);
    addLCIDMapEntry(localHashMap, "it", (short)1040);
    addLCIDMapEntry(localHashMap, "ja", (short)1041);
    addLCIDMapEntry(localHashMap, "ko", (short)1042);
    addLCIDMapEntry(localHashMap, "nl", (short)1043);
    addLCIDMapEntry(localHashMap, "no", (short)1044);
    addLCIDMapEntry(localHashMap, "pl", (short)1045);
    addLCIDMapEntry(localHashMap, "pt", (short)1046);
    addLCIDMapEntry(localHashMap, "rm", (short)1047);
    addLCIDMapEntry(localHashMap, "ro", (short)1048);
    addLCIDMapEntry(localHashMap, "ru", (short)1049);
    addLCIDMapEntry(localHashMap, "hr", (short)1050);
    addLCIDMapEntry(localHashMap, "sk", (short)1051);
    addLCIDMapEntry(localHashMap, "sq", (short)1052);
    addLCIDMapEntry(localHashMap, "sv", (short)1053);
    addLCIDMapEntry(localHashMap, "th", (short)1054);
    addLCIDMapEntry(localHashMap, "tr", (short)1055);
    addLCIDMapEntry(localHashMap, "ur", (short)1056);
    addLCIDMapEntry(localHashMap, "in", (short)1057);
    addLCIDMapEntry(localHashMap, "uk", (short)1058);
    addLCIDMapEntry(localHashMap, "be", (short)1059);
    addLCIDMapEntry(localHashMap, "sl", (short)1060);
    addLCIDMapEntry(localHashMap, "et", (short)1061);
    addLCIDMapEntry(localHashMap, "lv", (short)1062);
    addLCIDMapEntry(localHashMap, "lt", (short)1063);
    addLCIDMapEntry(localHashMap, "fa", (short)1065);
    addLCIDMapEntry(localHashMap, "vi", (short)1066);
    addLCIDMapEntry(localHashMap, "hy", (short)1067);
    addLCIDMapEntry(localHashMap, "eu", (short)1069);
    addLCIDMapEntry(localHashMap, "mk", (short)1071);
    addLCIDMapEntry(localHashMap, "tn", (short)1074);
    addLCIDMapEntry(localHashMap, "xh", (short)1076);
    addLCIDMapEntry(localHashMap, "zu", (short)1077);
    addLCIDMapEntry(localHashMap, "af", (short)1078);
    addLCIDMapEntry(localHashMap, "ka", (short)1079);
    addLCIDMapEntry(localHashMap, "fo", (short)1080);
    addLCIDMapEntry(localHashMap, "hi", (short)1081);
    addLCIDMapEntry(localHashMap, "mt", (short)1082);
    addLCIDMapEntry(localHashMap, "se", (short)1083);
    addLCIDMapEntry(localHashMap, "gd", (short)1084);
    addLCIDMapEntry(localHashMap, "ms", (short)1086);
    addLCIDMapEntry(localHashMap, "kk", (short)1087);
    addLCIDMapEntry(localHashMap, "ky", (short)1088);
    addLCIDMapEntry(localHashMap, "sw", (short)1089);
    addLCIDMapEntry(localHashMap, "tt", (short)1092);
    addLCIDMapEntry(localHashMap, "bn", (short)1093);
    addLCIDMapEntry(localHashMap, "pa", (short)1094);
    addLCIDMapEntry(localHashMap, "gu", (short)1095);
    addLCIDMapEntry(localHashMap, "ta", (short)1097);
    addLCIDMapEntry(localHashMap, "te", (short)1098);
    addLCIDMapEntry(localHashMap, "kn", (short)1099);
    addLCIDMapEntry(localHashMap, "ml", (short)1100);
    addLCIDMapEntry(localHashMap, "mr", (short)1102);
    addLCIDMapEntry(localHashMap, "sa", (short)1103);
    addLCIDMapEntry(localHashMap, "mn", (short)1104);
    addLCIDMapEntry(localHashMap, "cy", (short)1106);
    addLCIDMapEntry(localHashMap, "gl", (short)1110);
    addLCIDMapEntry(localHashMap, "dv", (short)1125);
    addLCIDMapEntry(localHashMap, "qu", (short)1131);
    addLCIDMapEntry(localHashMap, "mi", (short)1153);
    addLCIDMapEntry(localHashMap, "ar_IQ", (short)2049);
    addLCIDMapEntry(localHashMap, "zh_CN", (short)2052);
    addLCIDMapEntry(localHashMap, "de_CH", (short)2055);
    addLCIDMapEntry(localHashMap, "en_GB", (short)2057);
    addLCIDMapEntry(localHashMap, "es_MX", (short)2058);
    addLCIDMapEntry(localHashMap, "fr_BE", (short)2060);
    addLCIDMapEntry(localHashMap, "it_CH", (short)2064);
    addLCIDMapEntry(localHashMap, "nl_BE", (short)2067);
    addLCIDMapEntry(localHashMap, "no_NO_NY", (short)2068);
    addLCIDMapEntry(localHashMap, "pt_PT", (short)2070);
    addLCIDMapEntry(localHashMap, "ro_MD", (short)2072);
    addLCIDMapEntry(localHashMap, "ru_MD", (short)2073);
    addLCIDMapEntry(localHashMap, "sr_CS", (short)2074);
    addLCIDMapEntry(localHashMap, "sv_FI", (short)2077);
    addLCIDMapEntry(localHashMap, "az_AZ", (short)2092);
    addLCIDMapEntry(localHashMap, "se_SE", (short)2107);
    addLCIDMapEntry(localHashMap, "ga_IE", (short)2108);
    addLCIDMapEntry(localHashMap, "ms_BN", (short)2110);
    addLCIDMapEntry(localHashMap, "uz_UZ", (short)2115);
    addLCIDMapEntry(localHashMap, "qu_EC", (short)2155);
    addLCIDMapEntry(localHashMap, "ar_EG", (short)3073);
    addLCIDMapEntry(localHashMap, "zh_HK", (short)3076);
    addLCIDMapEntry(localHashMap, "de_AT", (short)3079);
    addLCIDMapEntry(localHashMap, "en_AU", (short)3081);
    addLCIDMapEntry(localHashMap, "fr_CA", (short)3084);
    addLCIDMapEntry(localHashMap, "sr_CS", (short)3098);
    addLCIDMapEntry(localHashMap, "se_FI", (short)3131);
    addLCIDMapEntry(localHashMap, "qu_PE", (short)3179);
    addLCIDMapEntry(localHashMap, "ar_LY", (short)4097);
    addLCIDMapEntry(localHashMap, "zh_SG", (short)4100);
    addLCIDMapEntry(localHashMap, "de_LU", (short)4103);
    addLCIDMapEntry(localHashMap, "en_CA", (short)4105);
    addLCIDMapEntry(localHashMap, "es_GT", (short)4106);
    addLCIDMapEntry(localHashMap, "fr_CH", (short)4108);
    addLCIDMapEntry(localHashMap, "hr_BA", (short)4122);
    addLCIDMapEntry(localHashMap, "ar_DZ", (short)5121);
    addLCIDMapEntry(localHashMap, "zh_MO", (short)5124);
    addLCIDMapEntry(localHashMap, "de_LI", (short)5127);
    addLCIDMapEntry(localHashMap, "en_NZ", (short)5129);
    addLCIDMapEntry(localHashMap, "es_CR", (short)5130);
    addLCIDMapEntry(localHashMap, "fr_LU", (short)5132);
    addLCIDMapEntry(localHashMap, "bs_BA", (short)5146);
    addLCIDMapEntry(localHashMap, "ar_MA", (short)6145);
    addLCIDMapEntry(localHashMap, "en_IE", (short)6153);
    addLCIDMapEntry(localHashMap, "es_PA", (short)6154);
    addLCIDMapEntry(localHashMap, "fr_MC", (short)6156);
    addLCIDMapEntry(localHashMap, "sr_BA", (short)6170);
    addLCIDMapEntry(localHashMap, "ar_TN", (short)7169);
    addLCIDMapEntry(localHashMap, "en_ZA", (short)7177);
    addLCIDMapEntry(localHashMap, "es_DO", (short)7178);
    addLCIDMapEntry(localHashMap, "sr_BA", (short)7194);
    addLCIDMapEntry(localHashMap, "ar_OM", (short)8193);
    addLCIDMapEntry(localHashMap, "en_JM", (short)8201);
    addLCIDMapEntry(localHashMap, "es_VE", (short)8202);
    addLCIDMapEntry(localHashMap, "ar_YE", (short)9217);
    addLCIDMapEntry(localHashMap, "es_CO", (short)9226);
    addLCIDMapEntry(localHashMap, "ar_SY", (short)10241);
    addLCIDMapEntry(localHashMap, "en_BZ", (short)10249);
    addLCIDMapEntry(localHashMap, "es_PE", (short)10250);
    addLCIDMapEntry(localHashMap, "ar_JO", (short)11265);
    addLCIDMapEntry(localHashMap, "en_TT", (short)11273);
    addLCIDMapEntry(localHashMap, "es_AR", (short)11274);
    addLCIDMapEntry(localHashMap, "ar_LB", (short)12289);
    addLCIDMapEntry(localHashMap, "en_ZW", (short)12297);
    addLCIDMapEntry(localHashMap, "es_EC", (short)12298);
    addLCIDMapEntry(localHashMap, "ar_KW", (short)13313);
    addLCIDMapEntry(localHashMap, "en_PH", (short)13321);
    addLCIDMapEntry(localHashMap, "es_CL", (short)13322);
    addLCIDMapEntry(localHashMap, "ar_AE", (short)14337);
    addLCIDMapEntry(localHashMap, "es_UY", (short)14346);
    addLCIDMapEntry(localHashMap, "ar_BH", (short)15361);
    addLCIDMapEntry(localHashMap, "es_PY", (short)15370);
    addLCIDMapEntry(localHashMap, "ar_QA", (short)16385);
    addLCIDMapEntry(localHashMap, "es_BO", (short)16394);
    addLCIDMapEntry(localHashMap, "es_SV", (short)17418);
    addLCIDMapEntry(localHashMap, "es_HN", (short)18442);
    addLCIDMapEntry(localHashMap, "es_NI", (short)19466);
    addLCIDMapEntry(localHashMap, "es_PR", (short)20490);
    lcidMap = localHashMap;
  }
  
  private static short getLCIDFromLocale(Locale paramLocale)
  {
    if (paramLocale.equals(Locale.US)) {
      return 1033;
    }
    if (lcidMap == null) {
      createLCIDMap();
    }
    int i;
    for (String str = paramLocale.toString(); !"".equals(str); str = str.substring(0, i))
    {
      Short localShort = (Short)lcidMap.get(str);
      if (localShort != null) {
        return localShort.shortValue();
      }
      i = str.lastIndexOf('_');
      if (i < 1) {
        return 1033;
      }
    }
    return 1033;
  }
  
  public String getFamilyName(Locale paramLocale)
  {
    if (paramLocale == null) {
      return familyName;
    }
    if ((paramLocale.equals(nameLocale)) && (localeFamilyName != null)) {
      return localeFamilyName;
    }
    short s = getLCIDFromLocale(paramLocale);
    String str = lookupName(s, 1);
    if (str == null) {
      return familyName;
    }
    return str;
  }
  
  public CharToGlyphMapper getMapper()
  {
    if (mapper == null) {
      mapper = new TrueTypeGlyphMapper(this);
    }
    return mapper;
  }
  
  protected void initAllNames(int paramInt, HashSet paramHashSet)
  {
    byte[] arrayOfByte = new byte['Ā'];
    ByteBuffer localByteBuffer = getTableBuffer(1851878757);
    if (localByteBuffer != null)
    {
      ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
      localShortBuffer.get();
      int i = localShortBuffer.get();
      int j = localShortBuffer.get() & 0xFFFF;
      for (int k = 0; k < i; k++)
      {
        int m = localShortBuffer.get();
        if (m != 3)
        {
          localShortBuffer.position(localShortBuffer.position() + 5);
        }
        else
        {
          short s = localShortBuffer.get();
          int n = localShortBuffer.get();
          int i1 = localShortBuffer.get();
          int i2 = localShortBuffer.get() & 0xFFFF;
          int i3 = (localShortBuffer.get() & 0xFFFF) + j;
          if (i1 == paramInt)
          {
            localByteBuffer.position(i3);
            localByteBuffer.get(arrayOfByte, 0, i2);
            paramHashSet.add(makeString(arrayOfByte, i2, s));
          }
        }
      }
    }
  }
  
  String[] getAllFamilyNames()
  {
    HashSet localHashSet = new HashSet();
    try
    {
      initAllNames(1, localHashSet);
    }
    catch (Exception localException) {}
    return (String[])localHashSet.toArray(new String[0]);
  }
  
  String[] getAllFullNames()
  {
    HashSet localHashSet = new HashSet();
    try
    {
      initAllNames(4, localHashSet);
    }
    catch (Exception localException) {}
    return (String[])localHashSet.toArray(new String[0]);
  }
  
  Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2)
  {
    try
    {
      return getScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
    }
    catch (FontScalerException localFontScalerException) {}
    return null;
  }
  
  private char[] getGaspTable()
  {
    if (gaspTable != null) {
      return gaspTable;
    }
    ByteBuffer localByteBuffer = getTableBuffer(1734439792);
    if (localByteBuffer == null) {
      return gaspTable = new char[0];
    }
    CharBuffer localCharBuffer = localByteBuffer.asCharBuffer();
    int i = localCharBuffer.get();
    if (i > 1) {
      return gaspTable = new char[0];
    }
    int j = localCharBuffer.get();
    if (4 + j * 4 > getTableSize(1734439792)) {
      return gaspTable = new char[0];
    }
    gaspTable = new char[2 * j];
    localCharBuffer.get(gaspTable);
    return gaspTable;
  }
  
  public boolean useAAForPtSize(int paramInt)
  {
    char[] arrayOfChar = getGaspTable();
    if (arrayOfChar.length > 0)
    {
      for (int i = 0; i < arrayOfChar.length; i += 2) {
        if (paramInt <= arrayOfChar[i]) {
          return (arrayOfChar[(i + 1)] & 0x2) != 0;
        }
      }
      return true;
    }
    if (style == 1) {
      return true;
    }
    return (paramInt <= 8) || (paramInt >= 18);
  }
  
  public boolean hasSupplementaryChars()
  {
    return ((TrueTypeGlyphMapper)getMapper()).hasSupplementaryChars();
  }
  
  public String toString()
  {
    return "** TrueType Font: Family=" + familyName + " Name=" + fullName + " style=" + style + " fileName=" + getPublicFileName();
  }
  
  static class DirectoryEntry
  {
    int tag;
    int offset;
    int length;
    
    DirectoryEntry() {}
  }
  
  private static class TTDisposerRecord
    implements DisposerRecord
  {
    FileChannel channel = null;
    
    private TTDisposerRecord() {}
    
    /* Error */
    public synchronized void dispose()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 30	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
      //   4: ifnull +10 -> 14
      //   7: aload_0
      //   8: getfield 30	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
      //   11: invokevirtual 32	java/nio/channels/FileChannel:close	()V
      //   14: aload_0
      //   15: aconst_null
      //   16: putfield 30	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
      //   19: goto +20 -> 39
      //   22: astore_1
      //   23: aload_0
      //   24: aconst_null
      //   25: putfield 30	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
      //   28: goto +11 -> 39
      //   31: astore_2
      //   32: aload_0
      //   33: aconst_null
      //   34: putfield 30	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
      //   37: aload_2
      //   38: athrow
      //   39: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	40	0	this	TTDisposerRecord
      //   22	1	1	localIOException	IOException
      //   31	7	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   0	14	22	java/io/IOException
      //   0	14	31	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\TrueTypeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */