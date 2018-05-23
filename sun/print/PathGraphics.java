package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D.Float;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Line2D.Float;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import sun.awt.image.SunWritableRaster;
import sun.awt.image.ToolkitImage;
import sun.font.CompositeFont;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontUtilities;
import sun.font.PhysicalFont;

public abstract class PathGraphics
  extends ProxyGraphics2D
{
  private Printable mPainter;
  private PageFormat mPageFormat;
  private int mPageIndex;
  private boolean mCanRedraw;
  protected boolean printingGlyphVector;
  protected static SoftReference<Hashtable<Font2DHandle, Object>> fontMapRef = new SoftReference(null);
  
  protected PathGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt, boolean paramBoolean)
  {
    super(paramGraphics2D, paramPrinterJob);
    mPainter = paramPrintable;
    mPageFormat = paramPageFormat;
    mPageIndex = paramInt;
    mCanRedraw = paramBoolean;
  }
  
  protected Printable getPrintable()
  {
    return mPainter;
  }
  
  protected PageFormat getPageFormat()
  {
    return mPageFormat;
  }
  
  protected int getPageIndex()
  {
    return mPageIndex;
  }
  
  public boolean canDoRedraws()
  {
    return mCanRedraw;
  }
  
  public abstract void redrawRegion(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, Shape paramShape, AffineTransform paramAffineTransform)
    throws PrinterException;
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Paint localPaint = getPaint();
    try
    {
      AffineTransform localAffineTransform = getTransform();
      if (getClip() != null) {
        deviceClip(getClip().getPathIterator(localAffineTransform));
      }
      deviceDrawLine(paramInt1, paramInt2, paramInt3, paramInt4, (Color)localPaint);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException("Expected a Color instance");
    }
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Paint localPaint = getPaint();
    try
    {
      AffineTransform localAffineTransform = getTransform();
      if (getClip() != null) {
        deviceClip(getClip().getPathIterator(localAffineTransform));
      }
      deviceFrameRect(paramInt1, paramInt2, paramInt3, paramInt4, (Color)localPaint);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException("Expected a Color instance");
    }
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Paint localPaint = getPaint();
    try
    {
      AffineTransform localAffineTransform = getTransform();
      if (getClip() != null) {
        deviceClip(getClip().getPathIterator(localAffineTransform));
      }
      deviceFillRect(paramInt1, paramInt2, paramInt3, paramInt4, (Color)localPaint);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException("Expected a Color instance");
    }
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    fill(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4), getBackground());
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    draw(new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    fill(new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    draw(new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    fill(new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    draw(new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0));
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    fill(new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2));
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    if (paramInt > 0)
    {
      float f1 = paramArrayOfInt1[0];
      float f2 = paramArrayOfInt2[0];
      for (int i = 1; i < paramInt; i++)
      {
        float f3 = paramArrayOfInt1[i];
        float f4 = paramArrayOfInt2[i];
        draw(new Line2D.Float(f1, f2, f3, f4));
        f1 = f3;
        f2 = f4;
      }
    }
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    draw(new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt));
  }
  
  public void drawPolygon(Polygon paramPolygon)
  {
    draw(paramPolygon);
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    fill(new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt));
  }
  
  public void fillPolygon(Polygon paramPolygon)
  {
    fill(paramPolygon);
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    drawString(paramString, paramInt1, paramInt2);
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2)
  {
    if (paramString.length() == 0) {
      return;
    }
    TextLayout localTextLayout = new TextLayout(paramString, getFont(), getFontRenderContext());
    localTextLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  protected void drawString(String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3)
  {
    TextLayout localTextLayout = new TextLayout(paramString, paramFont, paramFontRenderContext);
    Shape localShape = localTextLayout.getOutline(AffineTransform.getTranslateInstance(paramFloat1, paramFloat2));
    fill(localShape);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new NullPointerException("attributedcharacteriterator is null");
    }
    TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    localTextLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    if (printingGlyphVector)
    {
      assert (!printingGlyphVector);
      fill(paramGlyphVector.getOutline(paramFloat1, paramFloat2));
      return;
    }
    try
    {
      printingGlyphVector = true;
      if ((RasterPrinterJob.shapeTextProp) || (!printedSimpleGlyphVector(paramGlyphVector, paramFloat1, paramFloat2))) {
        fill(paramGlyphVector.getOutline(paramFloat1, paramFloat2));
      }
    }
    finally
    {
      printingGlyphVector = false;
    }
  }
  
  protected int platformFontCount(Font paramFont, String paramString)
  {
    return 0;
  }
  
  protected boolean printGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  boolean printedSimpleGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    int i = paramGlyphVector.getLayoutFlags();
    if ((i != 0) && (i != 2)) {
      return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2);
    }
    Font localFont = paramGlyphVector.getFont();
    Font2D localFont2D = FontUtilities.getFont2D(localFont);
    if (handle.font2D != localFont2D) {
      return false;
    }
    Hashtable localHashtable;
    synchronized (PathGraphics.class)
    {
      localHashtable = (Hashtable)fontMapRef.get();
      if (localHashtable == null)
      {
        localHashtable = new Hashtable();
        fontMapRef = new SoftReference(localHashtable);
      }
    }
    int j = paramGlyphVector.getNumGlyphs();
    int[] arrayOfInt1 = paramGlyphVector.getGlyphCodes(0, j, null);
    char[] arrayOfChar1 = null;
    char[][] arrayOfChar = (char[][])null;
    CompositeFont localCompositeFont = null;
    int k;
    int m;
    synchronized (localHashtable)
    {
      if ((localFont2D instanceof CompositeFont))
      {
        localCompositeFont = (CompositeFont)localFont2D;
        k = localCompositeFont.getNumSlots();
        arrayOfChar = (char[][])localHashtable.get(handle);
        if (arrayOfChar == null)
        {
          arrayOfChar = new char[k][];
          localHashtable.put(handle, arrayOfChar);
        }
        for (m = 0; m < j; m++)
        {
          int n = arrayOfInt1[m] >>> 24;
          if (n >= k) {
            return false;
          }
          if (arrayOfChar[n] == null)
          {
            PhysicalFont localPhysicalFont = localCompositeFont.getSlotFont(n);
            char[] arrayOfChar3 = (char[])localHashtable.get(handle);
            if (arrayOfChar3 == null) {
              arrayOfChar3 = getGlyphToCharMapForFont(localPhysicalFont);
            }
            arrayOfChar[n] = arrayOfChar3;
          }
        }
      }
      else
      {
        arrayOfChar1 = (char[])localHashtable.get(handle);
        if (arrayOfChar1 == null)
        {
          arrayOfChar1 = getGlyphToCharMapForFont(localFont2D);
          localHashtable.put(handle, arrayOfChar1);
        }
      }
    }
    ??? = new char[j];
    if (localCompositeFont != null) {
      for (k = 0; k < j; k++)
      {
        m = arrayOfInt1[k];
        char[] arrayOfChar2 = arrayOfChar[(m >>> 24)];
        m &= 0xFFFFFF;
        if (arrayOfChar2 == null) {
          return false;
        }
        if (m == 65535)
        {
          i2 = 10;
        }
        else
        {
          if ((m < 0) || (m >= arrayOfChar2.length)) {
            return false;
          }
          i2 = arrayOfChar2[m];
        }
        if (i2 != 65535) {
          ???[k] = i2;
        } else {
          return false;
        }
      }
    } else {
      for (k = 0; k < j; k++)
      {
        m = arrayOfInt1[k];
        int i1;
        if (m == 65535)
        {
          i1 = 10;
        }
        else
        {
          if ((m < 0) || (m >= arrayOfChar1.length)) {
            return false;
          }
          i1 = arrayOfChar1[m];
        }
        if (i1 != 65535) {
          ???[k] = i1;
        } else {
          return false;
        }
      }
    }
    FontRenderContext localFontRenderContext1 = paramGlyphVector.getFontRenderContext();
    GlyphVector localGlyphVector = localFont.createGlyphVector(localFontRenderContext1, (char[])???);
    if (localGlyphVector.getNumGlyphs() != j) {
      return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2);
    }
    int[] arrayOfInt2 = localGlyphVector.getGlyphCodes(0, j, null);
    for (int i2 = 0; i2 < j; i2++) {
      if (arrayOfInt1[i2] != arrayOfInt2[i2]) {
        return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2);
      }
    }
    FontRenderContext localFontRenderContext2 = getFontRenderContext();
    boolean bool = localFontRenderContext1.equals(localFontRenderContext2);
    if ((!bool) && (localFontRenderContext1.usesFractionalMetrics() == localFontRenderContext2.usesFractionalMetrics()))
    {
      localObject3 = localFontRenderContext1.getTransform();
      AffineTransform localAffineTransform = getTransform();
      localObject4 = new double[4];
      double[] arrayOfDouble = new double[4];
      ((AffineTransform)localObject3).getMatrix((double[])localObject4);
      localAffineTransform.getMatrix(arrayOfDouble);
      bool = true;
      for (int i5 = 0; i5 < 4; i5++) {
        if (localObject4[i5] != arrayOfDouble[i5])
        {
          bool = false;
          break;
        }
      }
    }
    Object localObject3 = new String((char[])???, 0, j);
    int i3 = platformFontCount(localFont, (String)localObject3);
    if (i3 == 0) {
      return false;
    }
    Object localObject4 = paramGlyphVector.getGlyphPositions(0, j, null);
    int i4 = ((i & 0x2) == 0) || (samePositions(localGlyphVector, arrayOfInt2, arrayOfInt1, (float[])localObject4)) ? 1 : 0;
    Point2D localPoint2D = paramGlyphVector.getGlyphPosition(j);
    float f1 = (float)localPoint2D.getX();
    int i6 = 0;
    Object localObject5;
    if ((localFont.hasLayoutAttributes()) && (printingGlyphVector) && (i4 != 0))
    {
      Map localMap = localFont.getAttributes();
      localObject5 = localMap.get(TextAttribute.TRACKING);
      int i8 = (localObject5 != null) && ((localObject5 instanceof Number)) && (((Number)localObject5).floatValue() != 0.0F) ? 1 : 0;
      if (i8 != 0)
      {
        i4 = 0;
      }
      else
      {
        Rectangle2D localRectangle2D = localFont.getStringBounds((String)localObject3, localFontRenderContext1);
        float f2 = (float)localRectangle2D.getWidth();
        if (Math.abs(f2 - f1) > 1.0E-5D) {
          i6 = 1;
        }
      }
    }
    if ((bool) && (i4 != 0) && (i6 == 0))
    {
      drawString((String)localObject3, paramFloat1, paramFloat2, localFont, localFontRenderContext1, 0.0F);
      return true;
    }
    if ((i3 == 1) && (canDrawStringToWidth()) && (i4 != 0))
    {
      drawString((String)localObject3, paramFloat1, paramFloat2, localFont, localFontRenderContext1, f1);
      return true;
    }
    if (FontUtilities.isComplexText((char[])???, 0, ???.length)) {
      return printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2);
    }
    if ((j > 10) && (printGlyphVector(paramGlyphVector, paramFloat1, paramFloat2))) {
      return true;
    }
    for (int i7 = 0; i7 < j; i7++)
    {
      localObject5 = new String((char[])???, i7, 1);
      drawString((String)localObject5, paramFloat1 + localObject4[(i7 * 2)], paramFloat2 + localObject4[(i7 * 2 + 1)], localFont, localFontRenderContext1, 0.0F);
    }
    return true;
  }
  
  private boolean samePositions(GlyphVector paramGlyphVector, int[] paramArrayOfInt1, int[] paramArrayOfInt2, float[] paramArrayOfFloat)
  {
    int i = paramGlyphVector.getNumGlyphs();
    float[] arrayOfFloat = paramGlyphVector.getGlyphPositions(0, i, null);
    if ((i != paramArrayOfInt1.length) || (paramArrayOfInt2.length != paramArrayOfInt1.length) || (paramArrayOfFloat.length != arrayOfFloat.length)) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if ((paramArrayOfInt1[j] != paramArrayOfInt2[j]) || (arrayOfFloat[j] != paramArrayOfFloat[j])) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean canDrawStringToWidth()
  {
    return false;
  }
  
  private static char[] getGlyphToCharMapForFont(Font2D paramFont2D)
  {
    int i = paramFont2D.getNumGlyphs();
    int j = paramFont2D.getMissingGlyphCode();
    char[] arrayOfChar = new char[i];
    for (int m = 0; m < i; m++) {
      arrayOfChar[m] = 65535;
    }
    for (m = 0; m < 65535; m = (char)(m + 1)) {
      if ((m < 55296) || (m > 57343))
      {
        int k = paramFont2D.charToGlyph(m);
        if ((k != j) && (k >= 0) && (k < i) && (arrayOfChar[k] == 65535)) {
          arrayOfChar[k] = m;
        }
      }
    }
    return arrayOfChar;
  }
  
  public void draw(Shape paramShape)
  {
    fill(getStroke().createStrokedShape(paramShape));
  }
  
  public void fill(Shape paramShape)
  {
    Paint localPaint = getPaint();
    try
    {
      fill(paramShape, (Color)localPaint);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException("Expected a Color instance");
    }
  }
  
  public void fill(Shape paramShape, Color paramColor)
  {
    AffineTransform localAffineTransform = getTransform();
    if (getClip() != null) {
      deviceClip(getClip().getPathIterator(localAffineTransform));
    }
    deviceFill(paramShape.getPathIterator(localAffineTransform), paramColor);
  }
  
  protected abstract void deviceFill(PathIterator paramPathIterator, Color paramColor);
  
  protected abstract void deviceClip(PathIterator paramPathIterator);
  
  protected abstract void deviceFrameRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor);
  
  protected abstract void deviceDrawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor);
  
  protected abstract void deviceFillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor);
  
  protected BufferedImage getBufferedImage(Image paramImage)
  {
    if ((paramImage instanceof BufferedImage)) {
      return (BufferedImage)paramImage;
    }
    if ((paramImage instanceof ToolkitImage)) {
      return ((ToolkitImage)paramImage).getBufferedImage();
    }
    if ((paramImage instanceof VolatileImage)) {
      return ((VolatileImage)paramImage).getSnapshot();
    }
    return null;
  }
  
  protected boolean hasTransparentPixels(BufferedImage paramBufferedImage)
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    boolean bool = localColorModel == null;
    if ((bool) && (paramBufferedImage != null) && ((paramBufferedImage.getType() == 2) || (paramBufferedImage.getType() == 3)))
    {
      DataBuffer localDataBuffer = paramBufferedImage.getRaster().getDataBuffer();
      SampleModel localSampleModel = paramBufferedImage.getRaster().getSampleModel();
      if (((localDataBuffer instanceof DataBufferInt)) && ((localSampleModel instanceof SinglePixelPackedSampleModel)))
      {
        SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)localSampleModel;
        int[] arrayOfInt = SunWritableRaster.stealData((DataBufferInt)localDataBuffer, 0);
        int i = paramBufferedImage.getMinX();
        int j = paramBufferedImage.getMinY();
        int k = paramBufferedImage.getWidth();
        int m = paramBufferedImage.getHeight();
        int n = localSinglePixelPackedSampleModel.getScanlineStride();
        int i1 = 0;
        for (int i2 = j; i2 < j + m; i2++)
        {
          int i3 = i2 * n;
          for (int i4 = i; i4 < i + k; i4++) {
            if ((arrayOfInt[(i3 + i4)] & 0xFF000000) != -16777216)
            {
              i1 = 1;
              break;
            }
          }
          if (i1 != 0) {
            break;
          }
        }
        if (i1 == 0) {
          bool = false;
        }
      }
    }
    return bool;
  }
  
  protected boolean isBitmaskTransparency(BufferedImage paramBufferedImage)
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    return (localColorModel != null) && (localColorModel.getTransparency() == 2);
  }
  
  protected boolean drawBitmaskImage(BufferedImage paramBufferedImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    if (!(localColorModel instanceof IndexColorModel)) {
      return false;
    }
    IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
    if (localColorModel.getTransparency() != 2) {
      return false;
    }
    if ((paramColor != null) && (paramColor.getAlpha() < 128)) {
      return false;
    }
    if ((paramAffineTransform.getType() & 0xFFFFFFF4) != 0) {
      return false;
    }
    if ((getTransform().getType() & 0xFFFFFFF4) != 0) {
      return false;
    }
    BufferedImage localBufferedImage = null;
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    int i = localIndexColorModel.getTransparentPixel();
    byte[] arrayOfByte = new byte[localIndexColorModel.getMapSize()];
    localIndexColorModel.getAlphas(arrayOfByte);
    if (i >= 0) {
      arrayOfByte[i] = 0;
    }
    int j = localWritableRaster.getWidth();
    int k = localWritableRaster.getHeight();
    if ((paramInt1 > j) || (paramInt2 > k)) {
      return false;
    }
    int m;
    int i1;
    if (paramInt1 + paramInt3 > j)
    {
      m = j;
      i1 = m - paramInt1;
    }
    else
    {
      m = paramInt1 + paramInt3;
      i1 = paramInt3;
    }
    int n;
    int i2;
    if (paramInt2 + paramInt4 > k)
    {
      n = k;
      i2 = n - paramInt2;
    }
    else
    {
      n = paramInt2 + paramInt4;
      i2 = paramInt4;
    }
    int[] arrayOfInt = new int[i1];
    for (int i3 = paramInt2; i3 < n; i3++)
    {
      int i4 = -1;
      localWritableRaster.getPixels(paramInt1, i3, i1, 1, arrayOfInt);
      for (int i5 = paramInt1; i5 < m; i5++) {
        if (arrayOfByte[arrayOfInt[(i5 - paramInt1)]] == 0)
        {
          if (i4 >= 0)
          {
            localBufferedImage = paramBufferedImage.getSubimage(i4, i3, i5 - i4, 1);
            paramAffineTransform.translate(i4, i3);
            drawImageToPlatform(localBufferedImage, paramAffineTransform, paramColor, 0, 0, i5 - i4, 1, true);
            paramAffineTransform.translate(-i4, -i3);
            i4 = -1;
          }
        }
        else if (i4 < 0) {
          i4 = i5;
        }
      }
      if (i4 >= 0)
      {
        localBufferedImage = paramBufferedImage.getSubimage(i4, i3, m - i4, 1);
        paramAffineTransform.translate(i4, i3);
        drawImageToPlatform(localBufferedImage, paramAffineTransform, paramColor, 0, 0, m - i4, 1, true);
        paramAffineTransform.translate(-i4, -i3);
      }
    }
    return true;
  }
  
  protected abstract boolean drawImageToPlatform(Image paramImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    boolean bool;
    if ((i < 0) || (j < 0)) {
      bool = false;
    } else {
      bool = drawImage(paramImage, paramInt1, paramInt2, i, j, paramColor, paramImageObserver);
    }
    return bool;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    boolean bool;
    if ((i < 0) || (j < 0)) {
      bool = false;
    } else {
      bool = drawImage(paramImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, 0, 0, i, j, paramImageObserver);
    }
    return bool;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if ((i < 0) || (j < 0)) {
      return true;
    }
    int k = paramInt7 - paramInt5;
    int m = paramInt8 - paramInt6;
    float f1 = (paramInt3 - paramInt1) / k;
    float f2 = (paramInt4 - paramInt2) / m;
    AffineTransform localAffineTransform = new AffineTransform(f1, 0.0F, 0.0F, f2, paramInt1 - paramInt5 * f1, paramInt2 - paramInt6 * f2);
    int n = 0;
    if (paramInt7 < paramInt5)
    {
      n = paramInt5;
      paramInt5 = paramInt7;
      paramInt7 = n;
    }
    if (paramInt8 < paramInt6)
    {
      n = paramInt6;
      paramInt6 = paramInt8;
      paramInt8 = n;
    }
    if (paramInt5 < 0) {
      paramInt5 = 0;
    } else if (paramInt5 > i) {
      paramInt5 = i;
    }
    if (paramInt7 < 0) {
      paramInt7 = 0;
    } else if (paramInt7 > i) {
      paramInt7 = i;
    }
    if (paramInt6 < 0) {
      paramInt6 = 0;
    } else if (paramInt6 > j) {
      paramInt6 = j;
    }
    if (paramInt8 < 0) {
      paramInt8 = 0;
    } else if (paramInt8 > j) {
      paramInt8 = j;
    }
    k = paramInt7 - paramInt5;
    m = paramInt8 - paramInt6;
    if ((k <= 0) || (m <= 0)) {
      return true;
    }
    return drawImageToPlatform(paramImage, localAffineTransform, paramColor, paramInt5, paramInt6, k, m, false);
  }
  
  public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    boolean bool;
    if ((i < 0) || (j < 0)) {
      bool = false;
    } else {
      bool = drawImageToPlatform(paramImage, paramAffineTransform, null, 0, 0, i, j, false);
    }
    return bool;
  }
  
  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    if (paramBufferedImage == null) {
      return;
    }
    int i = paramBufferedImage.getWidth(null);
    int j = paramBufferedImage.getHeight(null);
    if (paramBufferedImageOp != null) {
      paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
    }
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    AffineTransform localAffineTransform = new AffineTransform(1.0F, 0.0F, 0.0F, 1.0F, paramInt1, paramInt2);
    drawImageToPlatform(paramBufferedImage, localAffineTransform, null, 0, 0, i, j, false);
  }
  
  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform)
  {
    if (paramRenderedImage == null) {
      return;
    }
    BufferedImage localBufferedImage = null;
    int i = paramRenderedImage.getWidth();
    int j = paramRenderedImage.getHeight();
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    if ((paramRenderedImage instanceof BufferedImage))
    {
      localBufferedImage = (BufferedImage)paramRenderedImage;
    }
    else
    {
      localBufferedImage = new BufferedImage(i, j, 2);
      Graphics2D localGraphics2D = localBufferedImage.createGraphics();
      localGraphics2D.drawRenderedImage(paramRenderedImage, paramAffineTransform);
    }
    drawImageToPlatform(localBufferedImage, paramAffineTransform, null, 0, 0, i, j, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PathGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */