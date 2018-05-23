package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.peer.FontPeer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.PrivilegedExceptionAction;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import sun.font.AttributeMap;
import sun.font.AttributeValues;
import sun.font.CompositeFont;
import sun.font.CoreMetrics;
import sun.font.CreatedFontTracker;
import sun.font.EAttribute;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontAccess;
import sun.font.FontLineMetrics;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.FontUtilities;
import sun.font.GlyphLayout;
import sun.font.StandardGlyphVector;

public class Font
  implements Serializable
{
  private Hashtable<Object, Object> fRequestedAttributes;
  public static final String DIALOG = "Dialog";
  public static final String DIALOG_INPUT = "DialogInput";
  public static final String SANS_SERIF = "SansSerif";
  public static final String SERIF = "Serif";
  public static final String MONOSPACED = "Monospaced";
  public static final int PLAIN = 0;
  public static final int BOLD = 1;
  public static final int ITALIC = 2;
  public static final int ROMAN_BASELINE = 0;
  public static final int CENTER_BASELINE = 1;
  public static final int HANGING_BASELINE = 2;
  public static final int TRUETYPE_FONT = 0;
  public static final int TYPE1_FONT = 1;
  protected String name;
  protected int style;
  protected int size;
  protected float pointSize;
  private transient FontPeer peer;
  private transient long pData;
  private transient Font2DHandle font2DHandle;
  private transient AttributeValues values;
  private transient boolean hasLayoutAttributes;
  private transient boolean createdFont = false;
  private transient boolean nonIdentityTx;
  private static final AffineTransform identityTx = new AffineTransform();
  private static final long serialVersionUID = -4206021311591459213L;
  private static final int RECOGNIZED_MASK = AttributeValues.MASK_ALL & (AttributeValues.getMask(EAttribute.EFONT) ^ 0xFFFFFFFF);
  private static final int PRIMARY_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.EFAMILY, EAttribute.EWEIGHT, EAttribute.EWIDTH, EAttribute.EPOSTURE, EAttribute.ESIZE, EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.ETRACKING });
  private static final int SECONDARY_MASK = RECOGNIZED_MASK & (PRIMARY_MASK ^ 0xFFFFFFFF);
  private static final int LAYOUT_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.ECHAR_REPLACEMENT, EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.EUNDERLINE, EAttribute.ESTRIKETHROUGH, EAttribute.ERUN_DIRECTION, EAttribute.EBIDI_EMBEDDING, EAttribute.EJUSTIFICATION, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE, EAttribute.ESWAP_COLORS, EAttribute.ENUMERIC_SHAPING, EAttribute.EKERNING, EAttribute.ELIGATURES, EAttribute.ETRACKING, EAttribute.ESUPERSCRIPT });
  private static final int EXTRA_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.EWIDTH });
  private static final float[] ssinfo = { 0.0F, 0.375F, 0.625F, 0.7916667F, 0.9027778F, 0.9768519F, 1.0262346F, 1.0591564F };
  transient int hash;
  private int fontSerializedDataVersion = 1;
  private transient SoftReference<FontLineMetrics> flmref;
  public static final int LAYOUT_LEFT_TO_RIGHT = 0;
  public static final int LAYOUT_RIGHT_TO_LEFT = 1;
  public static final int LAYOUT_NO_START_CONTEXT = 2;
  public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;
  
  @Deprecated
  public FontPeer getPeer()
  {
    return getPeer_NoClientCode();
  }
  
  final FontPeer getPeer_NoClientCode()
  {
    if (peer == null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      peer = localToolkit.getFontPeer(name, style);
    }
    return peer;
  }
  
  private AttributeValues getAttributeValues()
  {
    if (values == null)
    {
      AttributeValues localAttributeValues = new AttributeValues();
      localAttributeValues.setFamily(name);
      localAttributeValues.setSize(pointSize);
      if ((style & 0x1) != 0) {
        localAttributeValues.setWeight(2.0F);
      }
      if ((style & 0x2) != 0) {
        localAttributeValues.setPosture(0.2F);
      }
      localAttributeValues.defineAll(PRIMARY_MASK);
      values = localAttributeValues;
    }
    return values;
  }
  
  private Font2D getFont2D()
  {
    FontManager localFontManager = FontManagerFactory.getInstance();
    if ((localFontManager.usingPerAppContextComposites()) && (font2DHandle != null) && ((font2DHandle.font2D instanceof CompositeFont)) && (((CompositeFont)font2DHandle.font2D).isStdComposite())) {
      return localFontManager.findFont2D(name, style, 2);
    }
    if (font2DHandle == null) {
      font2DHandle = findFont2Dname, style, 2).handle;
    }
    return font2DHandle.font2D;
  }
  
  public Font(String paramString, int paramInt1, int paramInt2)
  {
    name = (paramString != null ? paramString : "Default");
    style = ((paramInt1 & 0xFFFFFFFC) == 0 ? paramInt1 : 0);
    size = paramInt2;
    pointSize = paramInt2;
  }
  
  private Font(String paramString, int paramInt, float paramFloat)
  {
    name = (paramString != null ? paramString : "Default");
    style = ((paramInt & 0xFFFFFFFC) == 0 ? paramInt : 0);
    size = ((int)(paramFloat + 0.5D));
    pointSize = paramFloat;
  }
  
  private Font(String paramString, int paramInt, float paramFloat, boolean paramBoolean, Font2DHandle paramFont2DHandle)
  {
    this(paramString, paramInt, paramFloat);
    if (paramBoolean) {
      if (((font2D instanceof CompositeFont)) && (font2D.getStyle() != paramInt))
      {
        FontManager localFontManager = FontManagerFactory.getInstance();
        font2DHandle = localFontManager.getNewComposite(null, paramInt, paramFont2DHandle);
      }
      else
      {
        font2DHandle = paramFont2DHandle;
      }
    }
  }
  
  private Font(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker)
    throws FontFormatException
  {
    createdFont = true;
    FontManager localFontManager = FontManagerFactory.getInstance();
    font2DHandle = createFont2Dhandle;
    name = font2DHandle.font2D.getFontName(Locale.getDefault());
    style = 0;
    size = 1;
    pointSize = 1.0F;
  }
  
  private Font(AttributeValues paramAttributeValues, String paramString, int paramInt, boolean paramBoolean, Font2DHandle paramFont2DHandle)
  {
    createdFont = paramBoolean;
    if (paramBoolean)
    {
      font2DHandle = paramFont2DHandle;
      String str = null;
      if (paramString != null)
      {
        str = paramAttributeValues.getFamily();
        if (paramString.equals(str)) {
          str = null;
        }
      }
      int i = 0;
      if (paramInt == -1)
      {
        i = -1;
      }
      else
      {
        if (paramAttributeValues.getWeight() >= 2.0F) {
          i = 1;
        }
        if (paramAttributeValues.getPosture() >= 0.2F) {
          i |= 0x2;
        }
        if (paramInt == i) {
          i = -1;
        }
      }
      if ((font2D instanceof CompositeFont))
      {
        if ((i != -1) || (str != null))
        {
          FontManager localFontManager = FontManagerFactory.getInstance();
          font2DHandle = localFontManager.getNewComposite(str, i, paramFont2DHandle);
        }
      }
      else if (str != null)
      {
        createdFont = false;
        font2DHandle = null;
      }
    }
    initFromValues(paramAttributeValues);
  }
  
  public Font(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    initFromValues(AttributeValues.fromMap(paramMap, RECOGNIZED_MASK));
  }
  
  protected Font(Font paramFont)
  {
    if (values != null)
    {
      initFromValues(paramFont.getAttributeValues().clone());
    }
    else
    {
      name = name;
      style = style;
      size = size;
      pointSize = pointSize;
    }
    font2DHandle = font2DHandle;
    createdFont = createdFont;
  }
  
  private void initFromValues(AttributeValues paramAttributeValues)
  {
    values = paramAttributeValues;
    paramAttributeValues.defineAll(PRIMARY_MASK);
    name = paramAttributeValues.getFamily();
    pointSize = paramAttributeValues.getSize();
    size = ((int)(paramAttributeValues.getSize() + 0.5D));
    if (paramAttributeValues.getWeight() >= 2.0F) {
      style |= 0x1;
    }
    if (paramAttributeValues.getPosture() >= 0.2F) {
      style |= 0x2;
    }
    nonIdentityTx = paramAttributeValues.anyNonDefault(EXTRA_MASK);
    hasLayoutAttributes = paramAttributeValues.anyNonDefault(LAYOUT_MASK);
  }
  
  public static Font getFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    Object localObject2;
    if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null))
    {
      localObject1 = ((AttributeMap)paramMap).getValues();
      if (((AttributeValues)localObject1).isNonDefault(EAttribute.EFONT))
      {
        localObject2 = ((AttributeValues)localObject1).getFont();
        if (!((AttributeValues)localObject1).anyDefined(SECONDARY_MASK)) {
          return (Font)localObject2;
        }
        localObject1 = ((Font)localObject2).getAttributeValues().clone();
        ((AttributeValues)localObject1).merge(paramMap, SECONDARY_MASK);
        return new Font((AttributeValues)localObject1, name, style, createdFont, font2DHandle);
      }
      return new Font(paramMap);
    }
    Object localObject1 = (Font)paramMap.get(TextAttribute.FONT);
    if (localObject1 != null)
    {
      if (paramMap.size() > 1)
      {
        localObject2 = ((Font)localObject1).getAttributeValues().clone();
        ((AttributeValues)localObject2).merge(paramMap, SECONDARY_MASK);
        return new Font((AttributeValues)localObject2, name, style, createdFont, font2DHandle);
      }
      return (Font)localObject1;
    }
    return new Font(paramMap);
  }
  
  private static boolean hasTempPermission()
  {
    if (System.getSecurityManager() == null) {
      return true;
    }
    File localFile = null;
    boolean bool = false;
    try
    {
      localFile = Files.createTempFile("+~JT", ".tmp", new FileAttribute[0]).toFile();
      localFile.delete();
      localFile = null;
      bool = true;
    }
    catch (Throwable localThrowable) {}
    return bool;
  }
  
  public static Font createFont(int paramInt, InputStream paramInputStream)
    throws FontFormatException, IOException
  {
    if (hasTempPermission()) {
      return createFont0(paramInt, paramInputStream, null);
    }
    CreatedFontTracker localCreatedFontTracker = CreatedFontTracker.getTracker();
    boolean bool = false;
    try
    {
      bool = localCreatedFontTracker.acquirePermit();
      if (!bool) {
        throw new IOException("Timed out waiting for resources.");
      }
      Font localFont = createFont0(paramInt, paramInputStream, localCreatedFontTracker);
      return localFont;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new IOException("Problem reading font data.");
    }
    finally
    {
      if (bool) {
        localCreatedFontTracker.releasePermit();
      }
    }
  }
  
  /* Error */
  private static Font createFont0(int paramInt, InputStream paramInputStream, CreatedFontTracker paramCreatedFontTracker)
    throws FontFormatException, IOException
  {
    // Byte code:
    //   0: iload_0
    //   1: ifeq +18 -> 19
    //   4: iload_0
    //   5: iconst_1
    //   6: if_icmpeq +13 -> 19
    //   9: new 492	java/lang/IllegalArgumentException
    //   12: dup
    //   13: ldc 21
    //   15: invokespecial 1051	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   18: athrow
    //   19: iconst_0
    //   20: istore_3
    //   21: new 464	java/awt/Font$1
    //   24: dup
    //   25: invokespecial 1007	java/awt/Font$1:<init>	()V
    //   28: invokestatic 1084	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
    //   31: checkcast 480	java/io/File
    //   34: astore 4
    //   36: aload_2
    //   37: ifnull +9 -> 46
    //   40: aload_2
    //   41: aload 4
    //   43: invokevirtual 1128	sun/font/CreatedFontTracker:add	(Ljava/io/File;)V
    //   46: iconst_0
    //   47: istore 5
    //   49: new 465	java/awt/Font$2
    //   52: dup
    //   53: aload 4
    //   55: invokespecial 1008	java/awt/Font$2:<init>	(Ljava/io/File;)V
    //   58: invokestatic 1084	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
    //   61: checkcast 486	java/io/OutputStream
    //   64: astore 6
    //   66: aload_2
    //   67: ifnull +11 -> 78
    //   70: aload_2
    //   71: aload 4
    //   73: aload 6
    //   75: invokevirtual 1131	sun/font/CreatedFontTracker:set	(Ljava/io/File;Ljava/io/OutputStream;)V
    //   78: sipush 8192
    //   81: newarray <illegal type>
    //   83: astore 7
    //   85: aload_1
    //   86: aload 7
    //   88: invokevirtual 1040	java/io/InputStream:read	([B)I
    //   91: istore 8
    //   93: iload 8
    //   95: ifge +6 -> 101
    //   98: goto +75 -> 173
    //   101: aload_2
    //   102: ifnull +58 -> 160
    //   105: iload 5
    //   107: iload 8
    //   109: iadd
    //   110: ldc 1
    //   112: if_icmple +13 -> 125
    //   115: new 482	java/io/IOException
    //   118: dup
    //   119: ldc 13
    //   121: invokespecial 1039	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   124: athrow
    //   125: iload 5
    //   127: aload_2
    //   128: invokevirtual 1123	sun/font/CreatedFontTracker:getNumBytes	()I
    //   131: iadd
    //   132: ldc 2
    //   134: if_icmple +13 -> 147
    //   137: new 482	java/io/IOException
    //   140: dup
    //   141: ldc 16
    //   143: invokespecial 1039	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   146: athrow
    //   147: iload 5
    //   149: iload 8
    //   151: iadd
    //   152: istore 5
    //   154: aload_2
    //   155: iload 8
    //   157: invokevirtual 1126	sun/font/CreatedFontTracker:addBytes	(I)V
    //   160: aload 6
    //   162: aload 7
    //   164: iconst_0
    //   165: iload 8
    //   167: invokevirtual 1044	java/io/OutputStream:write	([BII)V
    //   170: goto -85 -> 85
    //   173: aload 6
    //   175: invokevirtual 1043	java/io/OutputStream:close	()V
    //   178: goto +13 -> 191
    //   181: astore 9
    //   183: aload 6
    //   185: invokevirtual 1043	java/io/OutputStream:close	()V
    //   188: aload 9
    //   190: athrow
    //   191: iconst_1
    //   192: istore_3
    //   193: new 463	java/awt/Font
    //   196: dup
    //   197: aload 4
    //   199: iload_0
    //   200: iconst_1
    //   201: aload_2
    //   202: invokespecial 1000	java/awt/Font:<init>	(Ljava/io/File;IZLsun/font/CreatedFontTracker;)V
    //   205: astore 7
    //   207: aload 7
    //   209: astore 8
    //   211: aload_2
    //   212: ifnull +9 -> 221
    //   215: aload_2
    //   216: aload 4
    //   218: invokevirtual 1129	sun/font/CreatedFontTracker:remove	(Ljava/io/File;)V
    //   221: iload_3
    //   222: ifne +26 -> 248
    //   225: aload_2
    //   226: ifnull +9 -> 235
    //   229: aload_2
    //   230: iload 5
    //   232: invokevirtual 1127	sun/font/CreatedFontTracker:subBytes	(I)V
    //   235: new 466	java/awt/Font$3
    //   238: dup
    //   239: aload 4
    //   241: invokespecial 1009	java/awt/Font$3:<init>	(Ljava/io/File;)V
    //   244: invokestatic 1084	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
    //   247: pop
    //   248: aload 8
    //   250: areturn
    //   251: astore 10
    //   253: aload_2
    //   254: ifnull +9 -> 263
    //   257: aload_2
    //   258: aload 4
    //   260: invokevirtual 1129	sun/font/CreatedFontTracker:remove	(Ljava/io/File;)V
    //   263: iload_3
    //   264: ifne +26 -> 290
    //   267: aload_2
    //   268: ifnull +9 -> 277
    //   271: aload_2
    //   272: iload 5
    //   274: invokevirtual 1127	sun/font/CreatedFontTracker:subBytes	(I)V
    //   277: new 466	java/awt/Font$3
    //   280: dup
    //   281: aload 4
    //   283: invokespecial 1009	java/awt/Font$3:<init>	(Ljava/io/File;)V
    //   286: invokestatic 1084	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
    //   289: pop
    //   290: aload 10
    //   292: athrow
    //   293: astore 4
    //   295: aload 4
    //   297: instanceof 468
    //   300: ifeq +9 -> 309
    //   303: aload 4
    //   305: checkcast 468	java/awt/FontFormatException
    //   308: athrow
    //   309: aload 4
    //   311: instanceof 482
    //   314: ifeq +9 -> 323
    //   317: aload 4
    //   319: checkcast 482	java/io/IOException
    //   322: athrow
    //   323: aload 4
    //   325: invokevirtual 1080	java/lang/Throwable:getCause	()Ljava/lang/Throwable;
    //   328: astore 5
    //   330: aload 5
    //   332: instanceof 468
    //   335: ifeq +9 -> 344
    //   338: aload 5
    //   340: checkcast 468	java/awt/FontFormatException
    //   343: athrow
    //   344: new 482	java/io/IOException
    //   347: dup
    //   348: ldc 14
    //   350: invokespecial 1039	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   353: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	354	0	paramInt	int
    //   0	354	1	paramInputStream	InputStream
    //   0	354	2	paramCreatedFontTracker	CreatedFontTracker
    //   20	244	3	i	int
    //   34	248	4	localFile	File
    //   293	31	4	localThrowable1	Throwable
    //   47	226	5	j	int
    //   328	11	5	localThrowable2	Throwable
    //   64	120	6	localOutputStream	OutputStream
    //   83	125	7	localObject1	Object
    //   91	75	8	k	int
    //   209	40	8	localObject2	Object
    //   181	8	9	localObject3	Object
    //   251	40	10	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   78	173	181	finally
    //   181	183	181	finally
    //   49	211	251	finally
    //   251	253	251	finally
    //   21	248	293	java/lang/Throwable
    //   251	293	293	java/lang/Throwable
  }
  
  public static Font createFont(int paramInt, File paramFile)
    throws FontFormatException, IOException
  {
    paramFile = new File(paramFile.getPath());
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("font format not recognized");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      FilePermission localFilePermission = new FilePermission(paramFile.getPath(), "read");
      localSecurityManager.checkPermission(localFilePermission);
    }
    if (!paramFile.canRead()) {
      throw new IOException("Can't read " + paramFile);
    }
    return new Font(paramFile, paramInt, false, null);
  }
  
  public AffineTransform getTransform()
  {
    if (nonIdentityTx)
    {
      AttributeValues localAttributeValues = getAttributeValues();
      AffineTransform localAffineTransform = localAttributeValues.isNonDefault(EAttribute.ETRANSFORM) ? new AffineTransform(localAttributeValues.getTransform()) : new AffineTransform();
      if (localAttributeValues.getSuperscript() != 0)
      {
        int i = localAttributeValues.getSuperscript();
        double d1 = 0.0D;
        int j = 0;
        int k = i > 0 ? 1 : 0;
        int m = k != 0 ? -1 : 1;
        int n = k != 0 ? i : -i;
        while ((n & 0x7) > j)
        {
          int i1 = n & 0x7;
          d1 += m * (ssinfo[i1] - ssinfo[j]);
          n >>= 3;
          m = -m;
          j = i1;
        }
        d1 *= pointSize;
        double d2 = Math.pow(0.6666666666666666D, j);
        localAffineTransform.preConcatenate(AffineTransform.getTranslateInstance(0.0D, d1));
        localAffineTransform.scale(d2, d2);
      }
      if (localAttributeValues.isNonDefault(EAttribute.EWIDTH)) {
        localAffineTransform.scale(localAttributeValues.getWidth(), 1.0D);
      }
      return localAffineTransform;
    }
    return new AffineTransform();
  }
  
  public String getFamily()
  {
    return getFamily_NoClientCode();
  }
  
  final String getFamily_NoClientCode()
  {
    return getFamily(Locale.getDefault());
  }
  
  public String getFamily(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException("null locale doesn't mean default");
    }
    return getFont2D().getFamilyName(paramLocale);
  }
  
  public String getPSName()
  {
    return getFont2D().getPostscriptName();
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getFontName()
  {
    return getFontName(Locale.getDefault());
  }
  
  public String getFontName(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException("null locale doesn't mean default");
    }
    return getFont2D().getFontName(paramLocale);
  }
  
  public int getStyle()
  {
    return style;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public float getSize2D()
  {
    return pointSize;
  }
  
  public boolean isPlain()
  {
    return style == 0;
  }
  
  public boolean isBold()
  {
    return (style & 0x1) != 0;
  }
  
  public boolean isItalic()
  {
    return (style & 0x2) != 0;
  }
  
  public boolean isTransformed()
  {
    return nonIdentityTx;
  }
  
  public boolean hasLayoutAttributes()
  {
    return hasLayoutAttributes;
  }
  
  public static Font getFont(String paramString)
  {
    return getFont(paramString, null);
  }
  
  public static Font decode(String paramString)
  {
    String str1 = paramString;
    String str2 = "";
    int i = 12;
    int j = 0;
    if (paramString == null) {
      return new Font("Dialog", j, i);
    }
    int k = paramString.lastIndexOf('-');
    int m = paramString.lastIndexOf(' ');
    int n = k > m ? 45 : 32;
    NumberFormatException localNumberFormatException1 = paramString.lastIndexOf(n);
    NumberFormatException localNumberFormatException2 = paramString.lastIndexOf(n, localNumberFormatException1 - 1);
    NumberFormatException localNumberFormatException3 = paramString.length();
    if ((localNumberFormatException1 > 0) && (localNumberFormatException1 + 1 < localNumberFormatException3)) {
      try
      {
        i = Integer.valueOf(paramString.substring(localNumberFormatException1 + 1)).intValue();
        if (i <= 0) {
          i = 12;
        }
      }
      catch (NumberFormatException localNumberFormatException4)
      {
        localNumberFormatException2 = localNumberFormatException1;
        localNumberFormatException1 = localNumberFormatException3;
        if (paramString.charAt(localNumberFormatException1 - 1) == n) {
          localNumberFormatException1--;
        }
      }
    }
    if ((localNumberFormatException2 >= 0) && (localNumberFormatException2 + 1 < localNumberFormatException3))
    {
      str2 = paramString.substring(localNumberFormatException2 + 1, localNumberFormatException1);
      str2 = str2.toLowerCase(Locale.ENGLISH);
      if (str2.equals("bolditalic"))
      {
        j = 3;
      }
      else if (str2.equals("italic"))
      {
        j = 2;
      }
      else if (str2.equals("bold"))
      {
        j = 1;
      }
      else if (str2.equals("plain"))
      {
        j = 0;
      }
      else
      {
        localNumberFormatException2 = localNumberFormatException1;
        if (paramString.charAt(localNumberFormatException2 - 1) == n) {
          localNumberFormatException2--;
        }
      }
      str1 = paramString.substring(0, localNumberFormatException2);
    }
    else
    {
      localNumberFormatException4 = localNumberFormatException3;
      if (localNumberFormatException2 > 0) {
        localNumberFormatException4 = localNumberFormatException2;
      } else if (localNumberFormatException1 > 0) {
        localNumberFormatException4 = localNumberFormatException1;
      }
      if ((localNumberFormatException4 > 0) && (paramString.charAt(localNumberFormatException4 - 1) == n)) {
        localNumberFormatException4--;
      }
      str1 = paramString.substring(0, localNumberFormatException4);
    }
    return new Font(str1, j, i);
  }
  
  public static Font getFont(String paramString, Font paramFont)
  {
    String str = null;
    try
    {
      str = System.getProperty(paramString);
    }
    catch (SecurityException localSecurityException) {}
    if (str == null) {
      return paramFont;
    }
    return decode(str);
  }
  
  public int hashCode()
  {
    if (hash == 0)
    {
      hash = (name.hashCode() ^ style ^ size);
      if ((nonIdentityTx) && (values != null) && (values.getTransform() != null)) {
        hash ^= values.getTransform().hashCode();
      }
    }
    return hash;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (paramObject != null) {
      try
      {
        Font localFont = (Font)paramObject;
        if ((size == size) && (style == style) && (nonIdentityTx == nonIdentityTx) && (hasLayoutAttributes == hasLayoutAttributes) && (pointSize == pointSize) && (name.equals(name)))
        {
          if (values == null)
          {
            if (values == null) {
              return true;
            }
            return getAttributeValues().equals(values);
          }
          return values.equals(localFont.getAttributeValues());
        }
      }
      catch (ClassCastException localClassCastException) {}
    }
    return false;
  }
  
  public String toString()
  {
    String str;
    if (isBold()) {
      str = isItalic() ? "bolditalic" : "bold";
    } else {
      str = isItalic() ? "italic" : "plain";
    }
    return getClass().getName() + "[family=" + getFamily() + ",name=" + name + ",style=" + str + ",size=" + size + "]";
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws ClassNotFoundException, IOException
  {
    if (values != null) {
      synchronized (values)
      {
        fRequestedAttributes = values.toSerializableHashtable();
        paramObjectOutputStream.defaultWriteObject();
        fRequestedAttributes = null;
      }
    } else {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    if (pointSize == 0.0F) {
      pointSize = size;
    }
    if (fRequestedAttributes != null)
    {
      values = getAttributeValues();
      AttributeValues localAttributeValues = AttributeValues.fromSerializableHashtable(fRequestedAttributes);
      if (!AttributeValues.is16Hashtable(fRequestedAttributes)) {
        localAttributeValues.unsetDefault();
      }
      values = getAttributeValues().merge(localAttributeValues);
      nonIdentityTx = values.anyNonDefault(EXTRA_MASK);
      hasLayoutAttributes = values.anyNonDefault(LAYOUT_MASK);
      fRequestedAttributes = null;
    }
  }
  
  public int getNumGlyphs()
  {
    return getFont2D().getNumGlyphs();
  }
  
  public int getMissingGlyphCode()
  {
    return getFont2D().getMissingGlyphCode();
  }
  
  public byte getBaselineFor(char paramChar)
  {
    return getFont2D().getBaselineFor(paramChar);
  }
  
  public Map<TextAttribute, ?> getAttributes()
  {
    return new AttributeMap(getAttributeValues());
  }
  
  public AttributedCharacterIterator.Attribute[] getAvailableAttributes()
  {
    AttributedCharacterIterator.Attribute[] arrayOfAttribute = { TextAttribute.FAMILY, TextAttribute.WEIGHT, TextAttribute.WIDTH, TextAttribute.POSTURE, TextAttribute.SIZE, TextAttribute.TRANSFORM, TextAttribute.SUPERSCRIPT, TextAttribute.CHAR_REPLACEMENT, TextAttribute.FOREGROUND, TextAttribute.BACKGROUND, TextAttribute.UNDERLINE, TextAttribute.STRIKETHROUGH, TextAttribute.RUN_DIRECTION, TextAttribute.BIDI_EMBEDDING, TextAttribute.JUSTIFICATION, TextAttribute.INPUT_METHOD_HIGHLIGHT, TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.SWAP_COLORS, TextAttribute.NUMERIC_SHAPING, TextAttribute.KERNING, TextAttribute.LIGATURES, TextAttribute.TRACKING };
    return arrayOfAttribute;
  }
  
  public Font deriveFont(int paramInt, float paramFloat)
  {
    if (values == null) {
      return new Font(name, paramInt, paramFloat, createdFont, font2DHandle);
    }
    AttributeValues localAttributeValues = getAttributeValues().clone();
    int i = style != paramInt ? style : -1;
    applyStyle(paramInt, localAttributeValues);
    localAttributeValues.setSize(paramFloat);
    return new Font(localAttributeValues, null, i, createdFont, font2DHandle);
  }
  
  public Font deriveFont(int paramInt, AffineTransform paramAffineTransform)
  {
    AttributeValues localAttributeValues = getAttributeValues().clone();
    int i = style != paramInt ? style : -1;
    applyStyle(paramInt, localAttributeValues);
    applyTransform(paramAffineTransform, localAttributeValues);
    return new Font(localAttributeValues, null, i, createdFont, font2DHandle);
  }
  
  public Font deriveFont(float paramFloat)
  {
    if (values == null) {
      return new Font(name, style, paramFloat, createdFont, font2DHandle);
    }
    AttributeValues localAttributeValues = getAttributeValues().clone();
    localAttributeValues.setSize(paramFloat);
    return new Font(localAttributeValues, null, -1, createdFont, font2DHandle);
  }
  
  public Font deriveFont(AffineTransform paramAffineTransform)
  {
    AttributeValues localAttributeValues = getAttributeValues().clone();
    applyTransform(paramAffineTransform, localAttributeValues);
    return new Font(localAttributeValues, null, -1, createdFont, font2DHandle);
  }
  
  public Font deriveFont(int paramInt)
  {
    if (values == null) {
      return new Font(name, paramInt, size, createdFont, font2DHandle);
    }
    AttributeValues localAttributeValues = getAttributeValues().clone();
    int i = style != paramInt ? style : -1;
    applyStyle(paramInt, localAttributeValues);
    return new Font(localAttributeValues, null, i, createdFont, font2DHandle);
  }
  
  public Font deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    if (paramMap == null) {
      return this;
    }
    AttributeValues localAttributeValues = getAttributeValues().clone();
    localAttributeValues.merge(paramMap, RECOGNIZED_MASK);
    return new Font(localAttributeValues, name, style, createdFont, font2DHandle);
  }
  
  public boolean canDisplay(char paramChar)
  {
    return getFont2D().canDisplay(paramChar);
  }
  
  public boolean canDisplay(int paramInt)
  {
    if (!Character.isValidCodePoint(paramInt)) {
      throw new IllegalArgumentException("invalid code point: " + Integer.toHexString(paramInt));
    }
    return getFont2D().canDisplay(paramInt);
  }
  
  public int canDisplayUpTo(String paramString)
  {
    Font2D localFont2D = getFont2D();
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      if (!localFont2D.canDisplay(c))
      {
        if (!Character.isHighSurrogate(c)) {
          return j;
        }
        if (!localFont2D.canDisplay(paramString.codePointAt(j))) {
          return j;
        }
        j++;
      }
    }
    return -1;
  }
  
  public int canDisplayUpTo(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    Font2D localFont2D = getFont2D();
    for (int i = paramInt1; i < paramInt2; i++)
    {
      char c = paramArrayOfChar[i];
      if (!localFont2D.canDisplay(c))
      {
        if (!Character.isHighSurrogate(c)) {
          return i;
        }
        if (!localFont2D.canDisplay(Character.codePointAt(paramArrayOfChar, i, paramInt2))) {
          return i;
        }
        i++;
      }
    }
    return -1;
  }
  
  public int canDisplayUpTo(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2)
  {
    Font2D localFont2D = getFont2D();
    char c1 = paramCharacterIterator.setIndex(paramInt1);
    int i = paramInt1;
    while (i < paramInt2)
    {
      if (!localFont2D.canDisplay(c1))
      {
        if (!Character.isHighSurrogate(c1)) {
          return i;
        }
        char c2 = paramCharacterIterator.next();
        if (!Character.isLowSurrogate(c2)) {
          return i;
        }
        if (!localFont2D.canDisplay(Character.toCodePoint(c1, c2))) {
          return i;
        }
        i++;
      }
      i++;
      c1 = paramCharacterIterator.next();
    }
    return -1;
  }
  
  public float getItalicAngle()
  {
    return getItalicAngle(null);
  }
  
  private float getItalicAngle(FontRenderContext paramFontRenderContext)
  {
    Object localObject1;
    Object localObject2;
    if (paramFontRenderContext == null)
    {
      localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      localObject2 = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    }
    else
    {
      localObject1 = paramFontRenderContext.getAntiAliasingHint();
      localObject2 = paramFontRenderContext.getFractionalMetricsHint();
    }
    return getFont2D().getItalicAngle(this, identityTx, localObject1, localObject2);
  }
  
  public boolean hasUniformLineMetrics()
  {
    return false;
  }
  
  private FontLineMetrics defaultLineMetrics(FontRenderContext paramFontRenderContext)
  {
    FontLineMetrics localFontLineMetrics = null;
    if ((flmref == null) || ((localFontLineMetrics = (FontLineMetrics)flmref.get()) == null) || (!frc.equals(paramFontRenderContext)))
    {
      float[] arrayOfFloat1 = new float[8];
      getFont2D().getFontMetrics(this, identityTx, paramFontRenderContext.getAntiAliasingHint(), paramFontRenderContext.getFractionalMetricsHint(), arrayOfFloat1);
      float f1 = arrayOfFloat1[0];
      float f2 = arrayOfFloat1[1];
      float f3 = arrayOfFloat1[2];
      float f4 = 0.0F;
      if ((values != null) && (values.getSuperscript() != 0))
      {
        f4 = (float)getTransform().getTranslateY();
        f1 -= f4;
        f2 += f4;
      }
      float f5 = f1 + f2 + f3;
      int i = 0;
      float[] arrayOfFloat2 = { 0.0F, (f2 / 2.0F - f1) / 2.0F, -f1 };
      float f6 = arrayOfFloat1[4];
      float f7 = arrayOfFloat1[5];
      float f8 = arrayOfFloat1[6];
      float f9 = arrayOfFloat1[7];
      float f10 = getItalicAngle(paramFontRenderContext);
      if (isTransformed())
      {
        localObject = values.getCharTransform();
        if (localObject != null)
        {
          Point2D.Float localFloat = new Point2D.Float();
          localFloat.setLocation(0.0F, f6);
          ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
          f6 = y;
          localFloat.setLocation(0.0F, f7);
          ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
          f7 = y;
          localFloat.setLocation(0.0F, f8);
          ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
          f8 = y;
          localFloat.setLocation(0.0F, f9);
          ((AffineTransform)localObject).deltaTransform(localFloat, localFloat);
          f9 = y;
        }
      }
      f6 += f4;
      f8 += f4;
      Object localObject = new CoreMetrics(f1, f2, f3, f5, i, arrayOfFloat2, f6, f7, f8, f9, f4, f10);
      localFontLineMetrics = new FontLineMetrics(0, (CoreMetrics)localObject, paramFontRenderContext);
      flmref = new SoftReference(localFontLineMetrics);
    }
    return (FontLineMetrics)localFontLineMetrics.clone();
  }
  
  public LineMetrics getLineMetrics(String paramString, FontRenderContext paramFontRenderContext)
  {
    FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    numchars = paramString.length();
    return localFontLineMetrics;
  }
  
  public LineMetrics getLineMetrics(String paramString, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    int i = paramInt2 - paramInt1;
    numchars = (i < 0 ? 0 : i);
    return localFontLineMetrics;
  }
  
  public LineMetrics getLineMetrics(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    int i = paramInt2 - paramInt1;
    numchars = (i < 0 ? 0 : i);
    return localFontLineMetrics;
  }
  
  public LineMetrics getLineMetrics(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    FontLineMetrics localFontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    int i = paramInt2 - paramInt1;
    numchars = (i < 0 ? 0 : i);
    return localFontLineMetrics;
  }
  
  public Rectangle2D getStringBounds(String paramString, FontRenderContext paramFontRenderContext)
  {
    char[] arrayOfChar = paramString.toCharArray();
    return getStringBounds(arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext);
  }
  
  public Rectangle2D getStringBounds(String paramString, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    String str = paramString.substring(paramInt1, paramInt2);
    return getStringBounds(str, paramFontRenderContext);
  }
  
  public Rectangle2D getStringBounds(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    if (paramInt1 < 0) {
      throw new IndexOutOfBoundsException("beginIndex: " + paramInt1);
    }
    if (paramInt2 > paramArrayOfChar.length) {
      throw new IndexOutOfBoundsException("limit: " + paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new IndexOutOfBoundsException("range length: " + (paramInt2 - paramInt1));
    }
    int i = (values == null) || ((values.getKerning() == 0) && (values.getLigatures() == 0) && (values.getBaselineTransform() == null)) ? 1 : 0;
    if (i != 0) {
      i = !FontUtilities.isComplexText(paramArrayOfChar, paramInt1, paramInt2) ? 1 : 0;
    }
    if (i != 0)
    {
      localObject = new StandardGlyphVector(this, paramArrayOfChar, paramInt1, paramInt2 - paramInt1, paramFontRenderContext);
      return ((GlyphVector)localObject).getLogicalBounds();
    }
    Object localObject = new String(paramArrayOfChar, paramInt1, paramInt2 - paramInt1);
    TextLayout localTextLayout = new TextLayout((String)localObject, this, paramFontRenderContext);
    return new Rectangle2D.Float(0.0F, -localTextLayout.getAscent(), localTextLayout.getAdvance(), localTextLayout.getAscent() + localTextLayout.getDescent() + localTextLayout.getLeading());
  }
  
  public Rectangle2D getStringBounds(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext)
  {
    int i = paramCharacterIterator.getBeginIndex();
    int j = paramCharacterIterator.getEndIndex();
    if (paramInt1 < i) {
      throw new IndexOutOfBoundsException("beginIndex: " + paramInt1);
    }
    if (paramInt2 > j) {
      throw new IndexOutOfBoundsException("limit: " + paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new IndexOutOfBoundsException("range length: " + (paramInt2 - paramInt1));
    }
    char[] arrayOfChar = new char[paramInt2 - paramInt1];
    paramCharacterIterator.setIndex(paramInt1);
    for (int k = 0; k < arrayOfChar.length; k++)
    {
      arrayOfChar[k] = paramCharacterIterator.current();
      paramCharacterIterator.next();
    }
    return getStringBounds(arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext);
  }
  
  public Rectangle2D getMaxCharBounds(FontRenderContext paramFontRenderContext)
  {
    float[] arrayOfFloat = new float[4];
    getFont2D().getFontMetrics(this, paramFontRenderContext, arrayOfFloat);
    return new Rectangle2D.Float(0.0F, -arrayOfFloat[0], arrayOfFloat[3], arrayOfFloat[0] + arrayOfFloat[1] + arrayOfFloat[2]);
  }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, String paramString)
  {
    return new StandardGlyphVector(this, paramString, paramFontRenderContext);
  }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar)
  {
    return new StandardGlyphVector(this, paramArrayOfChar, paramFontRenderContext);
  }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, CharacterIterator paramCharacterIterator)
  {
    return new StandardGlyphVector(this, paramCharacterIterator, paramFontRenderContext);
  }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, int[] paramArrayOfInt)
  {
    return new StandardGlyphVector(this, paramArrayOfInt, paramFontRenderContext);
  }
  
  public GlyphVector layoutGlyphVector(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
  {
    GlyphLayout localGlyphLayout = GlyphLayout.get(null);
    StandardGlyphVector localStandardGlyphVector = localGlyphLayout.layout(this, paramFontRenderContext, paramArrayOfChar, paramInt1, paramInt2 - paramInt1, paramInt3, null);
    GlyphLayout.done(localGlyphLayout);
    return localStandardGlyphVector;
  }
  
  private static void applyTransform(AffineTransform paramAffineTransform, AttributeValues paramAttributeValues)
  {
    if (paramAffineTransform == null) {
      throw new IllegalArgumentException("transform must not be null");
    }
    paramAttributeValues.setTransform(paramAffineTransform);
  }
  
  private static void applyStyle(int paramInt, AttributeValues paramAttributeValues)
  {
    paramAttributeValues.setWeight((paramInt & 0x1) != 0 ? 2.0F : 1.0F);
    paramAttributeValues.setPosture((paramInt & 0x2) != 0 ? 0.2F : 0.0F);
  }
  
  private static native void initIDs();
  
  static
  {
    Toolkit.loadLibraries();
    initIDs();
    FontAccess.setFontAccess(new FontAccessImpl(null));
  }
  
  private static class FontAccessImpl
    extends FontAccess
  {
    private FontAccessImpl() {}
    
    public Font2D getFont2D(Font paramFont)
    {
      return paramFont.getFont2D();
    }
    
    public void setFont2D(Font paramFont, Font2DHandle paramFont2DHandle)
    {
      font2DHandle = paramFont2DHandle;
    }
    
    public void setCreatedFont(Font paramFont)
    {
      createdFont = true;
    }
    
    public boolean isCreatedFont(Font paramFont)
    {
      return createdFont;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Font.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */