package sun.font;

import java.awt.FontFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class Type1Font
  extends FileFont
{
  WeakReference bufferRef = new WeakReference(null);
  private String psName = null;
  private static HashMap styleAbbreviationsMapping = new HashMap();
  private static HashSet styleNameTokes = new HashSet();
  private static final int PSEOFTOKEN = 0;
  private static final int PSNAMETOKEN = 1;
  private static final int PSSTRINGTOKEN = 2;
  
  public Type1Font(String paramString, Object paramObject)
    throws FontFormatException
  {
    this(paramString, paramObject, false);
  }
  
  public Type1Font(String paramString, Object paramObject, boolean paramBoolean)
    throws FontFormatException
  {
    super(paramString, paramObject);
    fontRank = 4;
    checkedNatives = true;
    try
    {
      verify();
    }
    catch (Throwable localThrowable)
    {
      if (paramBoolean)
      {
        T1DisposerRecord localT1DisposerRecord = new T1DisposerRecord(paramString);
        Disposer.addObjectRecord(bufferRef, localT1DisposerRecord);
        bufferRef = null;
      }
      if ((localThrowable instanceof FontFormatException)) {
        throw ((FontFormatException)localThrowable);
      }
      throw new FontFormatException("Unexpected runtime exception.");
    }
  }
  
  private synchronized ByteBuffer getBuffer()
    throws FontFormatException
  {
    MappedByteBuffer localMappedByteBuffer = (MappedByteBuffer)bufferRef.get();
    if (localMappedByteBuffer == null) {
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
        FileChannel localFileChannel = localRandomAccessFile.getChannel();
        fileSize = ((int)localFileChannel.size());
        localMappedByteBuffer = localFileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileSize);
        localMappedByteBuffer.position(0);
        bufferRef = new WeakReference(localMappedByteBuffer);
        localFileChannel.close();
      }
      catch (NullPointerException localNullPointerException)
      {
        throw new FontFormatException(localNullPointerException.toString());
      }
      catch (ClosedChannelException localClosedChannelException)
      {
        Thread.interrupted();
        return getBuffer();
      }
      catch (IOException localIOException)
      {
        throw new FontFormatException(localIOException.toString());
      }
    }
    return localMappedByteBuffer;
  }
  
  protected void close() {}
  
  void readFile(ByteBuffer paramByteBuffer)
  {
    RandomAccessFile localRandomAccessFile = null;
    try
    {
      localRandomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction()
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
      FileChannel localFileChannel = localRandomAccessFile.getChannel();
      while ((paramByteBuffer.remaining() > 0) && (localFileChannel.read(paramByteBuffer) != -1)) {}
      return;
    }
    catch (NullPointerException localNullPointerException) {}catch (ClosedChannelException localClosedChannelException)
    {
      try
      {
        if (localRandomAccessFile != null)
        {
          localRandomAccessFile.close();
          localRandomAccessFile = null;
        }
      }
      catch (IOException localIOException6) {}
      Thread.interrupted();
      readFile(paramByteBuffer);
    }
    catch (IOException localIOException4) {}finally
    {
      if (localRandomAccessFile != null) {
        try
        {
          localRandomAccessFile.close();
        }
        catch (IOException localIOException7) {}
      }
    }
  }
  
  public synchronized ByteBuffer readBlock(int paramInt1, int paramInt2)
  {
    ByteBuffer localByteBuffer = null;
    try
    {
      localByteBuffer = getBuffer();
      if (paramInt1 > fileSize) {
        paramInt1 = fileSize;
      }
      localByteBuffer.position(paramInt1);
      return localByteBuffer.slice();
    }
    catch (FontFormatException localFontFormatException) {}
    return null;
  }
  
  private void verify()
    throws FontFormatException
  {
    ByteBuffer localByteBuffer = getBuffer();
    if (localByteBuffer.capacity() < 6) {
      throw new FontFormatException("short file");
    }
    int i = localByteBuffer.get(0) & 0xFF;
    if ((localByteBuffer.get(0) & 0xFF) == 128)
    {
      verifyPFB(localByteBuffer);
      localByteBuffer.position(6);
    }
    else
    {
      verifyPFA(localByteBuffer);
      localByteBuffer.position(0);
    }
    initNames(localByteBuffer);
    if ((familyName == null) || (fullName == null)) {
      throw new FontFormatException("Font name not found");
    }
    setStyle();
  }
  
  public int getFileSize()
  {
    if (fileSize == 0) {
      try
      {
        getBuffer();
      }
      catch (FontFormatException localFontFormatException) {}
    }
    return fileSize;
  }
  
  private void verifyPFA(ByteBuffer paramByteBuffer)
    throws FontFormatException
  {
    if (paramByteBuffer.getShort() != 9505) {
      throw new FontFormatException("bad pfa font");
    }
  }
  
  /* Error */
  private void verifyPFB(ByteBuffer paramByteBuffer)
    throws FontFormatException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_1
    //   3: iload_2
    //   4: invokevirtual 561	java/nio/ByteBuffer:getShort	(I)S
    //   7: ldc 4
    //   9: iand
    //   10: istore_3
    //   11: iload_3
    //   12: ldc 1
    //   14: if_icmpeq +9 -> 23
    //   17: iload_3
    //   18: ldc 2
    //   20: if_icmpne +54 -> 74
    //   23: aload_1
    //   24: getstatic 514	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   27: invokevirtual 565	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   30: pop
    //   31: aload_1
    //   32: iload_2
    //   33: iconst_2
    //   34: iadd
    //   35: invokevirtual 560	java/nio/ByteBuffer:getInt	(I)I
    //   38: istore 4
    //   40: aload_1
    //   41: getstatic 513	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   44: invokevirtual 565	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   47: pop
    //   48: iload 4
    //   50: ifgt +13 -> 63
    //   53: new 362	java/awt/FontFormatException
    //   56: dup
    //   57: ldc 107
    //   59: invokespecial 528	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
    //   62: athrow
    //   63: iload_2
    //   64: iload 4
    //   66: bipush 6
    //   68: iadd
    //   69: iadd
    //   70: istore_2
    //   71: goto +20 -> 91
    //   74: iload_3
    //   75: ldc 3
    //   77: if_icmpne +4 -> 81
    //   80: return
    //   81: new 362	java/awt/FontFormatException
    //   84: dup
    //   85: ldc 106
    //   87: invokespecial 528	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
    //   90: athrow
    //   91: goto -89 -> 2
    //   94: astore_3
    //   95: new 362	java/awt/FontFormatException
    //   98: dup
    //   99: aload_3
    //   100: invokevirtual 553	java/nio/BufferUnderflowException:toString	()Ljava/lang/String;
    //   103: invokespecial 528	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
    //   106: athrow
    //   107: astore_3
    //   108: new 362	java/awt/FontFormatException
    //   111: dup
    //   112: aload_3
    //   113: invokevirtual 532	java/lang/Exception:toString	()Ljava/lang/String;
    //   116: invokespecial 528	java/awt/FontFormatException:<init>	(Ljava/lang/String;)V
    //   119: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	120	0	this	Type1Font
    //   0	120	1	paramByteBuffer	ByteBuffer
    //   1	70	2	i	int
    //   10	68	3	j	int
    //   94	6	3	localBufferUnderflowException	BufferUnderflowException
    //   107	6	3	localException	Exception
    //   38	31	4	k	int
    // Exception table:
    //   from	to	target	type
    //   2	80	94	java/nio/BufferUnderflowException
    //   81	91	94	java/nio/BufferUnderflowException
    //   2	80	107	java/lang/Exception
    //   81	91	107	java/lang/Exception
  }
  
  private void initNames(ByteBuffer paramByteBuffer)
    throws FontFormatException
  {
    int i = 0;
    Object localObject = null;
    try
    {
      while (((fullName == null) || (familyName == null) || (psName == null) || (localObject == null)) && (i == 0))
      {
        int j = nextTokenType(paramByteBuffer);
        if (j == 1)
        {
          int k = paramByteBuffer.position();
          if (paramByteBuffer.get(k) == 70)
          {
            String str2 = getSimpleToken(paramByteBuffer);
            if ("FullName".equals(str2))
            {
              if (nextTokenType(paramByteBuffer) == 2) {
                fullName = getString(paramByteBuffer);
              }
            }
            else if ("FamilyName".equals(str2))
            {
              if (nextTokenType(paramByteBuffer) == 2) {
                familyName = getString(paramByteBuffer);
              }
            }
            else if ("FontName".equals(str2))
            {
              if (nextTokenType(paramByteBuffer) == 1) {
                psName = getSimpleToken(paramByteBuffer);
              }
            }
            else if ("FontType".equals(str2))
            {
              String str3 = getSimpleToken(paramByteBuffer);
              if ("def".equals(getSimpleToken(paramByteBuffer))) {
                localObject = str3;
              }
            }
          }
          else
          {
            while (paramByteBuffer.get() > 32) {}
          }
        }
        else if (j == 0)
        {
          i = 1;
        }
      }
    }
    catch (Exception localException)
    {
      throw new FontFormatException(localException.toString());
    }
    if (!"1".equals(localObject)) {
      throw new FontFormatException("Unsupported font type");
    }
    if (psName == null)
    {
      paramByteBuffer.position(0);
      if (paramByteBuffer.getShort() != 9505) {
        paramByteBuffer.position(8);
      }
      String str1 = getSimpleToken(paramByteBuffer);
      if ((!str1.startsWith("FontType1-")) && (!str1.startsWith("PS-AdobeFont-"))) {
        throw new FontFormatException("Unsupported font format [" + str1 + "]");
      }
      psName = getSimpleToken(paramByteBuffer);
    }
    if (i != 0) {
      if (fullName != null)
      {
        familyName = fullName2FamilyName(fullName);
      }
      else if (familyName != null)
      {
        fullName = familyName;
      }
      else
      {
        fullName = psName2FullName(psName);
        familyName = psName2FamilyName(psName);
      }
    }
  }
  
  private String fullName2FamilyName(String paramString)
  {
    int i;
    for (int j = paramString.length(); j > 0; j = i)
    {
      for (i = j - 1; (i > 0) && (paramString.charAt(i) != ' '); i--) {}
      if (!isStyleToken(paramString.substring(i + 1, j))) {
        return paramString.substring(0, j);
      }
    }
    return paramString;
  }
  
  private String expandAbbreviation(String paramString)
  {
    if (styleAbbreviationsMapping.containsKey(paramString)) {
      return (String)styleAbbreviationsMapping.get(paramString);
    }
    return paramString;
  }
  
  private boolean isStyleToken(String paramString)
  {
    return styleNameTokes.contains(paramString);
  }
  
  private String psName2FullName(String paramString)
  {
    int i = paramString.indexOf("-");
    String str;
    if (i >= 0)
    {
      str = expandName(paramString.substring(0, i), false);
      str = str + " " + expandName(paramString.substring(i + 1), true);
    }
    else
    {
      str = expandName(paramString, false);
    }
    return str;
  }
  
  private String psName2FamilyName(String paramString)
  {
    String str = paramString;
    if (str.indexOf("-") > 0) {
      str = str.substring(0, str.indexOf("-"));
    }
    return expandName(str, false);
  }
  
  private int nextCapitalLetter(String paramString, int paramInt)
  {
    while ((paramInt >= 0) && (paramInt < paramString.length()))
    {
      if ((paramString.charAt(paramInt) >= 'A') && (paramString.charAt(paramInt) <= 'Z')) {
        return paramInt;
      }
      paramInt++;
    }
    return -1;
  }
  
  private String expandName(String paramString, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString.length() + 10);
    int j;
    for (int i = 0; i < paramString.length(); i = j)
    {
      j = nextCapitalLetter(paramString, i + 1);
      if (j < 0) {
        j = paramString.length();
      }
      if (i != 0) {
        localStringBuffer.append(" ");
      }
      if (paramBoolean) {
        localStringBuffer.append(expandAbbreviation(paramString.substring(i, j)));
      } else {
        localStringBuffer.append(paramString.substring(i, j));
      }
    }
    return localStringBuffer.toString();
  }
  
  private byte skip(ByteBuffer paramByteBuffer)
  {
    byte b = paramByteBuffer.get();
    while (b == 37) {
      do
      {
        b = paramByteBuffer.get();
        if (b == 13) {
          break;
        }
      } while (b != 10);
    }
    while (b <= 32) {
      b = paramByteBuffer.get();
    }
    return b;
  }
  
  private int nextTokenType(ByteBuffer paramByteBuffer)
  {
    try
    {
      int i = skip(paramByteBuffer);
      for (;;)
      {
        if (i == 47) {
          return 1;
        }
        if (i == 40) {
          return 2;
        }
        if ((i == 13) || (i == 10)) {
          i = skip(paramByteBuffer);
        } else {
          i = paramByteBuffer.get();
        }
      }
      return 0;
    }
    catch (BufferUnderflowException localBufferUnderflowException) {}
  }
  
  private String getSimpleToken(ByteBuffer paramByteBuffer)
  {
    while (paramByteBuffer.get() <= 32) {}
    int i = paramByteBuffer.position() - 1;
    while (paramByteBuffer.get() > 32) {}
    int j = paramByteBuffer.position();
    byte[] arrayOfByte = new byte[j - i - 1];
    paramByteBuffer.position(i);
    paramByteBuffer.get(arrayOfByte);
    try
    {
      return new String(arrayOfByte, "US-ASCII");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return new String(arrayOfByte);
  }
  
  private String getString(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position();
    while (paramByteBuffer.get() != 41) {}
    int j = paramByteBuffer.position();
    byte[] arrayOfByte = new byte[j - i - 1];
    paramByteBuffer.position(i);
    paramByteBuffer.get(arrayOfByte);
    try
    {
      return new String(arrayOfByte, "US-ASCII");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return new String(arrayOfByte);
  }
  
  public String getPostscriptName()
  {
    return psName;
  }
  
  protected synchronized FontScaler getScaler()
  {
    if (scaler == null) {
      scaler = FontScaler.getScaler(this, 0, false, fileSize);
    }
    return scaler;
  }
  
  CharToGlyphMapper getMapper()
  {
    if (mapper == null) {
      mapper = new Type1GlyphMapper(this);
    }
    return mapper;
  }
  
  public int getNumGlyphs()
  {
    try
    {
      return getScaler().getNumGlyphs();
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getNumGlyphs();
  }
  
  public int getMissingGlyphCode()
  {
    try
    {
      return getScaler().getMissingGlyphCode();
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getMissingGlyphCode();
  }
  
  public int getGlyphCode(char paramChar)
  {
    try
    {
      return getScaler().getGlyphCode(paramChar);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getGlyphCode(paramChar);
  }
  
  public String toString()
  {
    return "** Type1 Font: Family=" + familyName + " Name=" + fullName + " style=" + style + " fileName=" + getPublicFileName();
  }
  
  static
  {
    String[] arrayOfString1 = { "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Meduium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Semi", "Ultra", "Extra", "Alternate", "Alternate", "Deutsche Fraktur", "Expert", "Inline", "Ornaments", "Outline", "Roman", "Rounded", "Script", "Shaded", "Swash", "Titling", "Typewriter" };
    String[] arrayOfString2 = { "Blk", "Bd", "Bk", "Dm", "Hv", "Lt", "Md", "Nd", "Po", "Rg", "Su", "Th", "Cm", "Cn", "Ct", "Ex", "Nr", "Ic", "It", "Ks", "Obl", "Up", "Sl", "Sm", "Ult", "X", "A", "Alt", "Dfr", "Exp", "In", "Or", "Ou", "Rm", "Rd", "Scr", "Sh", "Sw", "Ti", "Typ" };
    String[] arrayOfString3 = { "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Medium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Slanted", "Semi", "Ultra", "Extra" };
    for (int i = 0; i < arrayOfString1.length; i++) {
      styleAbbreviationsMapping.put(arrayOfString2[i], arrayOfString1[i]);
    }
    for (i = 0; i < arrayOfString3.length; i++) {
      styleNameTokes.add(arrayOfString3[i]);
    }
  }
  
  private static class T1DisposerRecord
    implements DisposerRecord
  {
    String fileName = null;
    
    T1DisposerRecord(String paramString)
    {
      fileName = paramString;
    }
    
    public synchronized void dispose()
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          if (fileName != null) {
            new File(fileName).delete();
          }
          return null;
        }
      });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\Type1Font.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */