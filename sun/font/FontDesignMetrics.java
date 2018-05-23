package sun.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.Disposer;
import sun.java2d.Disposer.PollDisposable;
import sun.java2d.DisposerRecord;

public final class FontDesignMetrics
  extends FontMetrics
{
  static final long serialVersionUID = 4480069578560887773L;
  private static final float UNKNOWN_WIDTH = -1.0F;
  private static final int CURRENT_VERSION = 1;
  private static float roundingUpValue = 0.95F;
  private Font font;
  private float ascent;
  private float descent;
  private float leading;
  private float maxAdvance;
  private double[] matrix;
  private int[] cache;
  private int serVersion = 0;
  private boolean isAntiAliased;
  private boolean usesFractionalMetrics;
  private AffineTransform frcTx;
  private transient float[] advCache;
  private transient int height = -1;
  private transient FontRenderContext frc;
  private transient double[] devmatrix = null;
  private transient FontStrike fontStrike;
  private static FontRenderContext DEFAULT_FRC = null;
  private static final ConcurrentHashMap<Object, KeyReference> metricsCache = new ConcurrentHashMap();
  private static final int MAXRECENT = 5;
  private static final FontDesignMetrics[] recentMetrics = new FontDesignMetrics[5];
  private static int recentIndex = 0;
  
  private static FontRenderContext getDefaultFrc()
  {
    if (DEFAULT_FRC == null)
    {
      AffineTransform localAffineTransform;
      if (GraphicsEnvironment.isHeadless()) {
        localAffineTransform = new AffineTransform();
      } else {
        localAffineTransform = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
      }
      DEFAULT_FRC = new FontRenderContext(localAffineTransform, false, false);
    }
    return DEFAULT_FRC;
  }
  
  public static FontDesignMetrics getMetrics(Font paramFont)
  {
    return getMetrics(paramFont, getDefaultFrc());
  }
  
  public static FontDesignMetrics getMetrics(Font paramFont, FontRenderContext paramFontRenderContext)
  {
    SunFontManager localSunFontManager = SunFontManager.getInstance();
    if ((localSunFontManager.maybeUsingAlternateCompositeFonts()) && ((FontUtilities.getFont2D(paramFont) instanceof CompositeFont))) {
      return new FontDesignMetrics(paramFont, paramFontRenderContext);
    }
    FontDesignMetrics localFontDesignMetrics = null;
    boolean bool = paramFontRenderContext.equals(getDefaultFrc());
    KeyReference localKeyReference;
    if (bool) {
      localKeyReference = (KeyReference)metricsCache.get(paramFont);
    } else {
      synchronized (MetricsKey.class)
      {
        MetricsKey.key.init(paramFont, paramFontRenderContext);
        localKeyReference = (KeyReference)metricsCache.get(MetricsKey.key);
      }
    }
    if (localKeyReference != null) {
      localFontDesignMetrics = (FontDesignMetrics)localKeyReference.get();
    }
    if (localFontDesignMetrics == null)
    {
      localFontDesignMetrics = new FontDesignMetrics(paramFont, paramFontRenderContext);
      if (bool)
      {
        metricsCache.put(paramFont, new KeyReference(paramFont, localFontDesignMetrics));
      }
      else
      {
        ??? = new MetricsKey(paramFont, paramFontRenderContext);
        metricsCache.put(???, new KeyReference(???, localFontDesignMetrics));
      }
    }
    for (??? = 0; ??? < recentMetrics.length; ???++) {
      if (recentMetrics[???] == localFontDesignMetrics) {
        return localFontDesignMetrics;
      }
    }
    synchronized (recentMetrics)
    {
      recentMetrics[(recentIndex++)] = localFontDesignMetrics;
      if (recentIndex == 5) {
        recentIndex = 0;
      }
    }
    return localFontDesignMetrics;
  }
  
  private FontDesignMetrics(Font paramFont)
  {
    this(paramFont, getDefaultFrc());
  }
  
  private FontDesignMetrics(Font paramFont, FontRenderContext paramFontRenderContext)
  {
    super(paramFont);
    font = paramFont;
    frc = paramFontRenderContext;
    isAntiAliased = paramFontRenderContext.isAntiAliased();
    usesFractionalMetrics = paramFontRenderContext.usesFractionalMetrics();
    frcTx = paramFontRenderContext.getTransform();
    matrix = new double[4];
    initMatrixAndMetrics();
    initAdvCache();
  }
  
  private void initMatrixAndMetrics()
  {
    Font2D localFont2D = FontUtilities.getFont2D(font);
    fontStrike = localFont2D.getStrike(font, frc);
    StrikeMetrics localStrikeMetrics = fontStrike.getFontMetrics();
    ascent = localStrikeMetrics.getAscent();
    descent = localStrikeMetrics.getDescent();
    leading = localStrikeMetrics.getLeading();
    maxAdvance = localStrikeMetrics.getMaxAdvance();
    devmatrix = new double[4];
    frcTx.getMatrix(devmatrix);
  }
  
  private void initAdvCache()
  {
    advCache = new float['Ā'];
    for (int i = 0; i < 256; i++) {
      advCache[i] = -1.0F;
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (serVersion != 1)
    {
      frc = getDefaultFrc();
      isAntiAliased = frc.isAntiAliased();
      usesFractionalMetrics = frc.usesFractionalMetrics();
      frcTx = frc.getTransform();
    }
    else
    {
      frc = new FontRenderContext(frcTx, isAntiAliased, usesFractionalMetrics);
    }
    height = -1;
    cache = null;
    initMatrixAndMetrics();
    initAdvCache();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    cache = new int['Ā'];
    for (int i = 0; i < 256; i++) {
      cache[i] = -1;
    }
    serVersion = 1;
    paramObjectOutputStream.defaultWriteObject();
    cache = null;
  }
  
  private float handleCharWidth(int paramInt)
  {
    return fontStrike.getCodePointAdvance(paramInt);
  }
  
  private float getLatinCharWidth(char paramChar)
  {
    float f = advCache[paramChar];
    if (f == -1.0F)
    {
      f = handleCharWidth(paramChar);
      advCache[paramChar] = f;
    }
    return f;
  }
  
  public FontRenderContext getFontRenderContext()
  {
    return frc;
  }
  
  public int charWidth(char paramChar)
  {
    float f;
    if (paramChar < 'Ā') {
      f = getLatinCharWidth(paramChar);
    } else {
      f = handleCharWidth(paramChar);
    }
    return (int)(0.5D + f);
  }
  
  public int charWidth(int paramInt)
  {
    if (!Character.isValidCodePoint(paramInt)) {
      paramInt = 65535;
    }
    float f = handleCharWidth(paramInt);
    return (int)(0.5D + f);
  }
  
  public int stringWidth(String paramString)
  {
    float f = 0.0F;
    if (font.hasLayoutAttributes())
    {
      if (paramString == null) {
        throw new NullPointerException("str is null");
      }
      if (paramString.length() == 0) {
        return 0;
      }
      f = new TextLayout(paramString, font, frc).getAdvance();
    }
    else
    {
      int i = paramString.length();
      for (int j = 0; j < i; j++)
      {
        char c = paramString.charAt(j);
        if (c < 'Ā')
        {
          f += getLatinCharWidth(c);
        }
        else
        {
          if (FontUtilities.isNonSimpleChar(c))
          {
            f = new TextLayout(paramString, font, frc).getAdvance();
            break;
          }
          f += handleCharWidth(c);
        }
      }
    }
    return (int)(0.5D + f);
  }
  
  public int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    float f = 0.0F;
    if (font.hasLayoutAttributes())
    {
      if (paramInt2 == 0) {
        return 0;
      }
      String str1 = new String(paramArrayOfChar, paramInt1, paramInt2);
      f = new TextLayout(str1, font, frc).getAdvance();
    }
    else
    {
      if (paramInt2 < 0) {
        throw new IndexOutOfBoundsException("len=" + paramInt2);
      }
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++)
      {
        char c = paramArrayOfChar[j];
        if (c < 'Ā')
        {
          f += getLatinCharWidth(c);
        }
        else
        {
          if (FontUtilities.isNonSimpleChar(c))
          {
            String str2 = new String(paramArrayOfChar, paramInt1, paramInt2);
            f = new TextLayout(str2, font, frc).getAdvance();
            break;
          }
          f += handleCharWidth(c);
        }
      }
    }
    return (int)(0.5D + f);
  }
  
  public int[] getWidths()
  {
    int[] arrayOfInt = new int['Ā'];
    for (int i = 0; i < 256; i = (char)(i + 1))
    {
      float f = advCache[i];
      if (f == -1.0F) {
        f = advCache[i] = handleCharWidth(i);
      }
      arrayOfInt[i] = ((int)(0.5D + f));
    }
    return arrayOfInt;
  }
  
  public int getMaxAdvance()
  {
    return (int)(0.99F + maxAdvance);
  }
  
  public int getAscent()
  {
    return (int)(roundingUpValue + ascent);
  }
  
  public int getDescent()
  {
    return (int)(roundingUpValue + descent);
  }
  
  public int getLeading()
  {
    return (int)(roundingUpValue + descent + leading) - (int)(roundingUpValue + descent);
  }
  
  public int getHeight()
  {
    if (height < 0) {
      height = (getAscent() + (int)(roundingUpValue + descent + leading));
    }
    return height;
  }
  
  private static class KeyReference
    extends SoftReference
    implements DisposerRecord, Disposer.PollDisposable
  {
    static ReferenceQueue queue = ;
    Object key;
    
    KeyReference(Object paramObject1, Object paramObject2)
    {
      super(queue);
      key = paramObject1;
      Disposer.addReference(this, this);
    }
    
    public void dispose()
    {
      if (FontDesignMetrics.metricsCache.get(key) == this) {
        FontDesignMetrics.metricsCache.remove(key);
      }
    }
  }
  
  private static class MetricsKey
  {
    Font font;
    FontRenderContext frc;
    int hash;
    static final MetricsKey key = new MetricsKey();
    
    MetricsKey() {}
    
    MetricsKey(Font paramFont, FontRenderContext paramFontRenderContext)
    {
      init(paramFont, paramFontRenderContext);
    }
    
    void init(Font paramFont, FontRenderContext paramFontRenderContext)
    {
      font = paramFont;
      frc = paramFontRenderContext;
      hash = (paramFont.hashCode() + paramFontRenderContext.hashCode());
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof MetricsKey)) {
        return false;
      }
      return (font.equals(font)) && (frc.equals(frc));
    }
    
    public int hashCode()
    {
      return hash;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontDesignMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */