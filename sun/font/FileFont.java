package sun.font;

import java.awt.FontFormatException;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public abstract class FileFont
  extends PhysicalFont
{
  protected boolean useJavaRasterizer = true;
  protected int fileSize;
  protected FontScaler scaler;
  protected boolean checkedNatives;
  protected boolean useNatives;
  protected NativeFont[] nativeFonts;
  protected char[] glyphToCharMap;
  
  FileFont(String paramString, Object paramObject)
    throws FontFormatException
  {
    super(paramString, paramObject);
  }
  
  FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc)
  {
    if (!checkedNatives) {
      checkUseNatives();
    }
    return new FileFontStrike(this, paramFontStrikeDesc);
  }
  
  protected boolean checkUseNatives()
  {
    checkedNatives = true;
    return useNatives;
  }
  
  protected abstract void close();
  
  abstract ByteBuffer readBlock(int paramInt1, int paramInt2);
  
  public boolean canDoStyle(int paramInt)
  {
    return true;
  }
  
  void setFileToRemove(File paramFile, CreatedFontTracker paramCreatedFontTracker)
  {
    Disposer.addObjectRecord(this, new CreatedFontFileDisposerRecord(paramFile, paramCreatedFontTracker, null));
  }
  
  static void setFileToRemove(Object paramObject, File paramFile, CreatedFontTracker paramCreatedFontTracker)
  {
    Disposer.addObjectRecord(paramObject, new CreatedFontFileDisposerRecord(paramFile, paramCreatedFontTracker, null));
  }
  
  synchronized void deregisterFontAndClearStrikeCache()
  {
    SunFontManager localSunFontManager = SunFontManager.getInstance();
    localSunFontManager.deRegisterBadFont(this);
    Iterator localIterator = strikeCache.values().iterator();
    while (localIterator.hasNext())
    {
      Reference localReference = (Reference)localIterator.next();
      if (localReference != null)
      {
        FileFontStrike localFileFontStrike = (FileFontStrike)localReference.get();
        if ((localFileFontStrike != null) && (pScalerContext != 0L)) {
          scaler.invalidateScalerContext(pScalerContext);
        }
      }
    }
    if (scaler != null) {
      scaler.dispose();
    }
    scaler = FontScaler.getNullScaler();
  }
  
  StrikeMetrics getFontMetrics(long paramLong)
  {
    try
    {
      return getScaler().getFontMetrics(paramLong);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getFontMetrics(paramLong);
  }
  
  float getGlyphAdvance(long paramLong, int paramInt)
  {
    try
    {
      return getScaler().getGlyphAdvance(paramLong, paramInt);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getGlyphAdvance(paramLong, paramInt);
  }
  
  void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat)
  {
    try
    {
      getScaler().getGlyphMetrics(paramLong, paramInt, paramFloat);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
      getGlyphMetrics(paramLong, paramInt, paramFloat);
    }
  }
  
  long getGlyphImage(long paramLong, int paramInt)
  {
    try
    {
      return getScaler().getGlyphImage(paramLong, paramInt);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getGlyphImage(paramLong, paramInt);
  }
  
  Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt)
  {
    try
    {
      return getScaler().getGlyphOutlineBounds(paramLong, paramInt);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getGlyphOutlineBounds(paramLong, paramInt);
  }
  
  GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2)
  {
    try
    {
      return getScaler().getGlyphOutline(paramLong, paramInt, paramFloat1, paramFloat2);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getGlyphOutline(paramLong, paramInt, paramFloat1, paramFloat2);
  }
  
  GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2)
  {
    try
    {
      return getScaler().getGlyphVectorOutline(paramLong, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getGlyphVectorOutline(paramLong, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
  }
  
  protected abstract FontScaler getScaler();
  
  protected long getUnitsPerEm()
  {
    return getScaler().getUnitsPerEm();
  }
  
  protected String getPublicFileName()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return platName;
    }
    int i = 1;
    try
    {
      localSecurityManager.checkPropertyAccess("java.io.tmpdir");
    }
    catch (SecurityException localSecurityException)
    {
      i = 0;
    }
    if (i != 0) {
      return platName;
    }
    final File localFile = new File(platName);
    Boolean localBoolean = Boolean.FALSE;
    try
    {
      localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Boolean run()
        {
          File localFile = new File(System.getProperty("java.io.tmpdir"));
          try
          {
            String str1 = localFile.getCanonicalPath();
            String str2 = localFile.getCanonicalPath();
            return Boolean.valueOf((str2 == null) || (str2.startsWith(str1)));
          }
          catch (IOException localIOException) {}
          return Boolean.TRUE;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      localBoolean = Boolean.TRUE;
    }
    return localBoolean.booleanValue() ? "temp file" : platName;
  }
  
  private static class CreatedFontFileDisposerRecord
    implements DisposerRecord
  {
    File fontFile = null;
    CreatedFontTracker tracker;
    
    private CreatedFontFileDisposerRecord(File paramFile, CreatedFontTracker paramCreatedFontTracker)
    {
      fontFile = paramFile;
      tracker = paramCreatedFontTracker;
    }
    
    public void dispose()
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          if (fontFile != null) {
            try
            {
              if (tracker != null) {
                tracker.subBytes((int)fontFile.length());
              }
              fontFile.delete();
              getInstancetmpFontFiles.remove(fontFile);
            }
            catch (Exception localException) {}
          }
          return null;
        }
      });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FileFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */