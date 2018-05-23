package sun.awt.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D.Float;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.security.AccessController;
import java.util.Arrays;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.BytePackedRaster;
import sun.font.CharToGlyphMapper;
import sun.font.CompositeFont;
import sun.font.Font2D;
import sun.font.FontUtilities;
import sun.font.PhysicalFont;
import sun.font.TrueTypeFont;
import sun.print.PathGraphics;
import sun.print.ProxyGraphics2D;
import sun.security.action.GetPropertyAction;

final class WPathGraphics
  extends PathGraphics
{
  private static final int DEFAULT_USER_RES = 72;
  private static final float MIN_DEVICE_LINEWIDTH = 1.2F;
  private static final float MAX_THINLINE_INCHES = 0.014F;
  private static boolean useGDITextLayout = true;
  private static boolean preferGDITextLayout = false;
  
  WPathGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt, boolean paramBoolean)
  {
    super(paramGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, paramBoolean);
  }
  
  public Graphics create()
  {
    return new WPathGraphics((Graphics2D)getDelegate().create(), getPrinterJob(), getPrintable(), getPageFormat(), getPageIndex(), canDoRedraws());
  }
  
  public void draw(Shape paramShape)
  {
    Stroke localStroke = getStroke();
    if ((localStroke instanceof BasicStroke))
    {
      BasicStroke localBasicStroke2 = null;
      BasicStroke localBasicStroke1 = (BasicStroke)localStroke;
      float f2 = localBasicStroke1.getLineWidth();
      Point2D.Float localFloat1 = new Point2D.Float(f2, f2);
      AffineTransform localAffineTransform1 = getTransform();
      localAffineTransform1.deltaTransform(localFloat1, localFloat1);
      float f1 = Math.min(Math.abs(x), Math.abs(y));
      if (f1 < 1.2F)
      {
        Point2D.Float localFloat2 = new Point2D.Float(1.2F, 1.2F);
        try
        {
          AffineTransform localAffineTransform2 = localAffineTransform1.createInverse();
          localAffineTransform2.deltaTransform(localFloat2, localFloat2);
          float f3 = Math.max(Math.abs(x), Math.abs(y));
          localBasicStroke2 = new BasicStroke(f3, localBasicStroke1.getEndCap(), localBasicStroke1.getLineJoin(), localBasicStroke1.getMiterLimit(), localBasicStroke1.getDashArray(), localBasicStroke1.getDashPhase());
          setStroke(localBasicStroke2);
        }
        catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
      }
      super.draw(paramShape);
      if (localBasicStroke2 != null) {
        setStroke(localBasicStroke1);
      }
    }
    else
    {
      super.draw(paramShape);
    }
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    drawString(paramString, paramInt1, paramInt2);
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2)
  {
    drawString(paramString, paramFloat1, paramFloat2, getFont(), getFontRenderContext(), 0.0F);
  }
  
  protected int platformFontCount(Font paramFont, String paramString)
  {
    AffineTransform localAffineTransform1 = getTransform();
    AffineTransform localAffineTransform2 = new AffineTransform(localAffineTransform1);
    localAffineTransform2.concatenate(getFont().getTransform());
    int i = localAffineTransform2.getType();
    int j = (i != 32) && ((i & 0x40) == 0) ? 1 : 0;
    if (j == 0) {
      return 0;
    }
    Font2D localFont2D = FontUtilities.getFont2D(paramFont);
    if (((localFont2D instanceof CompositeFont)) || ((localFont2D instanceof TrueTypeFont))) {
      return 1;
    }
    return 0;
  }
  
  private static boolean isXP()
  {
    String str = System.getProperty("os.version");
    if (str != null)
    {
      Float localFloat = Float.valueOf(str);
      return localFloat.floatValue() >= 5.1F;
    }
    return false;
  }
  
  private boolean strNeedsTextLayout(String paramString, Font paramFont)
  {
    char[] arrayOfChar = paramString.toCharArray();
    boolean bool = FontUtilities.isComplexText(arrayOfChar, 0, arrayOfChar.length);
    if (!bool) {
      return false;
    }
    if (!useGDITextLayout) {
      return true;
    }
    return (!preferGDITextLayout) && ((!isXP()) || (!FontUtilities.textLayoutIsCompatible(paramFont)));
  }
  
  private int getAngle(Point2D.Double paramDouble)
  {
    double d = Math.toDegrees(Math.atan2(y, x));
    if (d < 0.0D) {
      d += 360.0D;
    }
    if (d != 0.0D) {
      d = 360.0D - d;
    }
    return (int)Math.round(d * 10.0D);
  }
  
  private float getAwScale(double paramDouble1, double paramDouble2)
  {
    float f = (float)(paramDouble1 / paramDouble2);
    if ((f > 0.999F) && (f < 1.001F)) {
      f = 1.0F;
    }
    return f;
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3)
  {
    if (paramString.length() == 0) {
      return;
    }
    if (WPrinterJob.shapeTextProp)
    {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
      return;
    }
    boolean bool = strNeedsTextLayout(paramString, paramFont);
    if (((paramFont.hasLayoutAttributes()) || (bool)) && (!printingGlyphVector))
    {
      localObject = new TextLayout(paramString, paramFont, paramFontRenderContext);
      ((TextLayout)localObject).draw(this, paramFloat1, paramFloat2);
      return;
    }
    if (bool)
    {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
      return;
    }
    Object localObject = getTransform();
    AffineTransform localAffineTransform1 = new AffineTransform((AffineTransform)localObject);
    localAffineTransform1.concatenate(paramFont.getTransform());
    int i = localAffineTransform1.getType();
    int j = (i != 32) && ((i & 0x40) == 0) ? 1 : 0;
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    try
    {
      localWPrinterJob.setTextColor((Color)getPaint());
    }
    catch (ClassCastException localClassCastException)
    {
      j = 0;
    }
    if (j == 0)
    {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
      return;
    }
    Point2D.Float localFloat1 = new Point2D.Float(paramFloat1, paramFloat2);
    Point2D.Float localFloat2 = new Point2D.Float();
    if (paramFont.isTransformed())
    {
      AffineTransform localAffineTransform2 = paramFont.getTransform();
      float f2 = (float)localAffineTransform2.getTranslateX();
      float f3 = (float)localAffineTransform2.getTranslateY();
      if (Math.abs(f2) < 1.0E-5D) {
        f2 = 0.0F;
      }
      if (Math.abs(f3) < 1.0E-5D) {
        f3 = 0.0F;
      }
      x += f2;
      y += f3;
    }
    ((AffineTransform)localObject).transform(localFloat1, localFloat2);
    if (getClip() != null) {
      deviceClip(getClip().getPathIterator((AffineTransform)localObject));
    }
    float f1 = paramFont.getSize2D();
    double d1 = localWPrinterJob.getXRes();
    double d2 = localWPrinterJob.getYRes();
    double d3 = d2 / 72.0D;
    int k = getPageFormat().getOrientation();
    if ((k == 0) || (k == 2))
    {
      d4 = d1;
      d1 = d2;
      d2 = d4;
    }
    double d4 = d1 / 72.0D;
    double d5 = d2 / 72.0D;
    localAffineTransform1.scale(1.0D / d4, 1.0D / d5);
    Point2D.Double localDouble1 = new Point2D.Double(0.0D, 1.0D);
    localAffineTransform1.deltaTransform(localDouble1, localDouble1);
    double d6 = Math.sqrt(x * x + y * y);
    float f4 = (float)(f1 * d6 * d3);
    Point2D.Double localDouble2 = new Point2D.Double(1.0D, 0.0D);
    localAffineTransform1.deltaTransform(localDouble2, localDouble2);
    double d7 = Math.sqrt(x * x + y * y);
    float f5 = getAwScale(d7, d6);
    int m = getAngle(localDouble2);
    localDouble2 = new Point2D.Double(1.0D, 0.0D);
    ((AffineTransform)localObject).deltaTransform(localDouble2, localDouble2);
    double d8 = Math.sqrt(x * x + y * y);
    localDouble1 = new Point2D.Double(0.0D, 1.0D);
    ((AffineTransform)localObject).deltaTransform(localDouble1, localDouble1);
    double d9 = Math.sqrt(x * x + y * y);
    Font2D localFont2D = FontUtilities.getFont2D(paramFont);
    if ((localFont2D instanceof TrueTypeFont))
    {
      textOut(paramString, paramFont, (TrueTypeFont)localFont2D, paramFontRenderContext, f4, m, f5, d8, d9, paramFloat1, paramFloat2, x, y, paramFloat3);
    }
    else if ((localFont2D instanceof CompositeFont))
    {
      CompositeFont localCompositeFont = (CompositeFont)localFont2D;
      float f6 = paramFloat1;
      float f7 = paramFloat2;
      float f8 = x;
      float f9 = y;
      char[] arrayOfChar = paramString.toCharArray();
      int n = arrayOfChar.length;
      int[] arrayOfInt = new int[n];
      localCompositeFont.getMapper().charsToGlyphs(n, arrayOfChar, arrayOfInt);
      int i1 = 0;
      int i2 = 0;
      int i3 = 0;
      while (i2 < n)
      {
        i1 = i2;
        i3 = arrayOfInt[i1] >>> 24;
        while ((i2 < n) && (arrayOfInt[i2] >>> 24 == i3)) {
          i2++;
        }
        String str = new String(arrayOfChar, i1, i2 - i1);
        PhysicalFont localPhysicalFont = localCompositeFont.getSlotFont(i3);
        textOut(str, paramFont, localPhysicalFont, paramFontRenderContext, f4, m, f5, d8, d9, f6, f7, f8, f9, 0.0F);
        Rectangle2D localRectangle2D = paramFont.getStringBounds(str, paramFontRenderContext);
        float f10 = (float)localRectangle2D.getWidth();
        f6 += f10;
        x += f10;
        ((AffineTransform)localObject).transform(localFloat1, localFloat2);
        f8 = x;
        f9 = y;
      }
    }
    else
    {
      super.drawString(paramString, paramFloat1, paramFloat2, paramFont, paramFontRenderContext, paramFloat3);
    }
  }
  
  protected boolean printGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    if ((paramGlyphVector.getLayoutFlags() & 0x1) != 0) {
      return false;
    }
    if (paramGlyphVector.getNumGlyphs() == 0) {
      return true;
    }
    AffineTransform localAffineTransform1 = getTransform();
    AffineTransform localAffineTransform2 = new AffineTransform(localAffineTransform1);
    Font localFont = paramGlyphVector.getFont();
    localAffineTransform2.concatenate(localFont.getTransform());
    int i = localAffineTransform2.getType();
    int j = (i != 32) && ((i & 0x40) == 0) ? 1 : 0;
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    try
    {
      localWPrinterJob.setTextColor((Color)getPaint());
    }
    catch (ClassCastException localClassCastException)
    {
      j = 0;
    }
    if ((WPrinterJob.shapeTextProp) || (j == 0)) {
      return false;
    }
    Point2D.Float localFloat1 = new Point2D.Float(paramFloat1, paramFloat2);
    Point2D localPoint2D = paramGlyphVector.getGlyphPosition(0);
    x += (float)localPoint2D.getX();
    y += (float)localPoint2D.getY();
    Point2D.Float localFloat2 = new Point2D.Float();
    if (localFont.isTransformed())
    {
      AffineTransform localAffineTransform3 = localFont.getTransform();
      float f2 = (float)localAffineTransform3.getTranslateX();
      float f3 = (float)localAffineTransform3.getTranslateY();
      if (Math.abs(f2) < 1.0E-5D) {
        f2 = 0.0F;
      }
      if (Math.abs(f3) < 1.0E-5D) {
        f3 = 0.0F;
      }
      x += f2;
      y += f3;
    }
    localAffineTransform1.transform(localFloat1, localFloat2);
    if (getClip() != null) {
      deviceClip(getClip().getPathIterator(localAffineTransform1));
    }
    float f1 = localFont.getSize2D();
    double d1 = localWPrinterJob.getXRes();
    double d2 = localWPrinterJob.getYRes();
    double d3 = d2 / 72.0D;
    int k = getPageFormat().getOrientation();
    if ((k == 0) || (k == 2))
    {
      d4 = d1;
      d1 = d2;
      d2 = d4;
    }
    double d4 = d1 / 72.0D;
    double d5 = d2 / 72.0D;
    localAffineTransform2.scale(1.0D / d4, 1.0D / d5);
    Point2D.Double localDouble1 = new Point2D.Double(0.0D, 1.0D);
    localAffineTransform2.deltaTransform(localDouble1, localDouble1);
    double d6 = Math.sqrt(x * x + y * y);
    float f4 = (float)(f1 * d6 * d3);
    Point2D.Double localDouble2 = new Point2D.Double(1.0D, 0.0D);
    localAffineTransform2.deltaTransform(localDouble2, localDouble2);
    double d7 = Math.sqrt(x * x + y * y);
    float f5 = getAwScale(d7, d6);
    int m = getAngle(localDouble2);
    localDouble2 = new Point2D.Double(1.0D, 0.0D);
    localAffineTransform1.deltaTransform(localDouble2, localDouble2);
    double d8 = Math.sqrt(x * x + y * y);
    localDouble1 = new Point2D.Double(0.0D, 1.0D);
    localAffineTransform1.deltaTransform(localDouble1, localDouble1);
    double d9 = Math.sqrt(x * x + y * y);
    int n = paramGlyphVector.getNumGlyphs();
    Object localObject1 = paramGlyphVector.getGlyphCodes(0, n, null);
    Object localObject2 = paramGlyphVector.getGlyphPositions(0, n, null);
    int i1 = 0;
    for (int i2 = 0; i2 < n; i2++) {
      if ((localObject1[i2] & 0xFFFF) >= 65534) {
        i1++;
      }
    }
    int i4;
    if (i1 > 0)
    {
      i2 = n - i1;
      localObject3 = new int[i2];
      localObject4 = new float[i2 * 2];
      int i3 = 0;
      for (i4 = 0; i4 < n; i4++) {
        if ((localObject1[i4] & 0xFFFF) < 65534)
        {
          localObject3[i3] = localObject1[i4];
          localObject4[(i3 * 2)] = localObject2[(i4 * 2)];
          localObject4[(i3 * 2 + 1)] = localObject2[(i4 * 2 + 1)];
          i3++;
        }
      }
      n = i2;
      localObject1 = localObject3;
      localObject2 = localObject4;
    }
    AffineTransform localAffineTransform4 = AffineTransform.getScaleInstance(d8, d9);
    Object localObject3 = new float[localObject2.length];
    localAffineTransform4.transform((float[])localObject2, 0, (float[])localObject3, 0, localObject2.length / 2);
    Object localObject4 = FontUtilities.getFont2D(localFont);
    Object localObject5;
    if ((localObject4 instanceof TrueTypeFont))
    {
      localObject5 = ((Font2D)localObject4).getFamilyName(null);
      i4 = localFont.getStyle() | ((Font2D)localObject4).getStyle();
      if (!localWPrinterJob.setFont((String)localObject5, f4, i4, m, f5)) {
        return false;
      }
      localWPrinterJob.glyphsOut((int[])localObject1, x, y, (float[])localObject3);
    }
    else if ((localObject4 instanceof CompositeFont))
    {
      localObject5 = (CompositeFont)localObject4;
      float f6 = paramFloat1;
      float f7 = paramFloat2;
      float f8 = x;
      float f9 = y;
      int i5 = 0;
      int i6 = 0;
      int i7 = 0;
      while (i6 < n)
      {
        i5 = i6;
        i7 = localObject1[i5] >>> 24;
        while ((i6 < n) && (localObject1[i6] >>> 24 == i7)) {
          i6++;
        }
        PhysicalFont localPhysicalFont = ((CompositeFont)localObject5).getSlotFont(i7);
        if (!(localPhysicalFont instanceof TrueTypeFont)) {
          return false;
        }
        String str = localPhysicalFont.getFamilyName(null);
        int i8 = localFont.getStyle() | localPhysicalFont.getStyle();
        if (!localWPrinterJob.setFont(str, f4, i8, m, f5)) {
          return false;
        }
        int[] arrayOfInt = Arrays.copyOfRange((int[])localObject1, i5, i6);
        float[] arrayOfFloat = Arrays.copyOfRange((float[])localObject3, i5 * 2, i6 * 2);
        if (i5 != 0)
        {
          Point2D.Float localFloat3 = new Point2D.Float(paramFloat1 + localObject2[(i5 * 2)], paramFloat2 + localObject2[(i5 * 2 + 1)]);
          localAffineTransform1.transform(localFloat3, localFloat3);
          f8 = x;
          f9 = y;
        }
        localWPrinterJob.glyphsOut(arrayOfInt, f8, f9, arrayOfFloat);
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  private void textOut(String paramString, Font paramFont, PhysicalFont paramPhysicalFont, FontRenderContext paramFontRenderContext, float paramFloat1, int paramInt, float paramFloat2, double paramDouble1, double paramDouble2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
  {
    String str = paramPhysicalFont.getFamilyName(null);
    int i = paramFont.getStyle() | paramPhysicalFont.getStyle();
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    boolean bool = localWPrinterJob.setFont(str, paramFloat1, i, paramInt, paramFloat2);
    if (!bool)
    {
      super.drawString(paramString, paramFloat3, paramFloat4, paramFont, paramFontRenderContext, paramFloat7);
      return;
    }
    Object localObject = null;
    if (!okGDIMetrics(paramString, paramFont, paramFontRenderContext, paramDouble1))
    {
      paramString = localWPrinterJob.removeControlChars(paramString);
      char[] arrayOfChar = paramString.toCharArray();
      int j = arrayOfChar.length;
      GlyphVector localGlyphVector = null;
      if (!FontUtilities.isComplexText(arrayOfChar, 0, j)) {
        localGlyphVector = paramFont.createGlyphVector(paramFontRenderContext, paramString);
      }
      if (localGlyphVector == null)
      {
        super.drawString(paramString, paramFloat3, paramFloat4, paramFont, paramFontRenderContext, paramFloat7);
        return;
      }
      localObject = localGlyphVector.getGlyphPositions(0, j, null);
      Point2D localPoint2D = localGlyphVector.getGlyphPosition(localGlyphVector.getNumGlyphs());
      AffineTransform localAffineTransform = AffineTransform.getScaleInstance(paramDouble1, paramDouble2);
      float[] arrayOfFloat = new float[localObject.length];
      localAffineTransform.transform((float[])localObject, 0, arrayOfFloat, 0, localObject.length / 2);
      localObject = arrayOfFloat;
    }
    localWPrinterJob.textOut(paramString, paramFloat5, paramFloat6, (float[])localObject);
  }
  
  private boolean okGDIMetrics(String paramString, Font paramFont, FontRenderContext paramFontRenderContext, double paramDouble)
  {
    Rectangle2D localRectangle2D = paramFont.getStringBounds(paramString, paramFontRenderContext);
    double d1 = localRectangle2D.getWidth();
    d1 = Math.round(d1 * paramDouble);
    int i = ((WPrinterJob)getPrinterJob()).getGDIAdvance(paramString);
    if ((d1 > 0.0D) && (i > 0))
    {
      double d2 = Math.abs(i - d1);
      double d3 = i / d1;
      if (d3 < 1.0D) {
        d3 = 1.0D / d3;
      }
      return (d2 <= 1.0D) || (d3 < 1.01D);
    }
    return true;
  }
  
  protected boolean drawImageToPlatform(Image paramImage, AffineTransform paramAffineTransform, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    BufferedImage localBufferedImage1 = getBufferedImage(paramImage);
    if (localBufferedImage1 == null) {
      return true;
    }
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    AffineTransform localAffineTransform1 = getTransform();
    if (paramAffineTransform == null) {
      paramAffineTransform = new AffineTransform();
    }
    localAffineTransform1.concatenate(paramAffineTransform);
    double[] arrayOfDouble = new double[6];
    localAffineTransform1.getMatrix(arrayOfDouble);
    Point2D.Float localFloat1 = new Point2D.Float(1.0F, 0.0F);
    Point2D.Float localFloat2 = new Point2D.Float(0.0F, 1.0F);
    localAffineTransform1.deltaTransform(localFloat1, localFloat1);
    localAffineTransform1.deltaTransform(localFloat2, localFloat2);
    Point2D.Float localFloat3 = new Point2D.Float(0.0F, 0.0F);
    double d1 = localFloat1.distance(localFloat3);
    double d2 = localFloat2.distance(localFloat3);
    double d3 = localWPrinterJob.getXRes();
    double d4 = localWPrinterJob.getYRes();
    double d5 = d3 / 72.0D;
    double d6 = d4 / 72.0D;
    int i = localAffineTransform1.getType();
    int j = (i & 0x30) != 0 ? 1 : 0;
    if (j != 0)
    {
      if (d1 > d5) {
        d1 = d5;
      }
      if (d2 > d6) {
        d2 = d6;
      }
    }
    if ((d1 != 0.0D) && (d2 != 0.0D))
    {
      AffineTransform localAffineTransform2 = new AffineTransform(arrayOfDouble[0] / d1, arrayOfDouble[1] / d2, arrayOfDouble[2] / d1, arrayOfDouble[3] / d2, arrayOfDouble[4] / d1, arrayOfDouble[5] / d2);
      Rectangle2D.Float localFloat = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
      Shape localShape1 = localAffineTransform2.createTransformedShape(localFloat);
      Rectangle2D localRectangle2D1 = localShape1.getBounds2D();
      localRectangle2D1.setRect(localRectangle2D1.getX(), localRectangle2D1.getY(), localRectangle2D1.getWidth() + 0.001D, localRectangle2D1.getHeight() + 0.001D);
      int k = (int)localRectangle2D1.getWidth();
      int m = (int)localRectangle2D1.getHeight();
      if ((k > 0) && (m > 0))
      {
        int n = 1;
        if ((!paramBoolean) && (hasTransparentPixels(localBufferedImage1)))
        {
          n = 0;
          if (isBitmaskTransparency(localBufferedImage1)) {
            if (paramColor == null)
            {
              if (drawBitmaskImage(localBufferedImage1, paramAffineTransform, paramColor, paramInt1, paramInt2, paramInt3, paramInt4)) {
                return true;
              }
            }
            else if (paramColor.getTransparency() == 1) {
              n = 1;
            }
          }
          if (!canDoRedraws()) {
            n = 1;
          }
        }
        else
        {
          paramColor = null;
        }
        if (((paramInt1 + paramInt3 > localBufferedImage1.getWidth(null)) || (paramInt2 + paramInt4 > localBufferedImage1.getHeight(null))) && (canDoRedraws())) {
          n = 0;
        }
        int i5;
        int i7;
        if (n == 0)
        {
          localAffineTransform1.getMatrix(arrayOfDouble);
          AffineTransform localAffineTransform3 = new AffineTransform(arrayOfDouble[0] / d5, arrayOfDouble[1] / d6, arrayOfDouble[2] / d5, arrayOfDouble[3] / d6, arrayOfDouble[4] / d5, arrayOfDouble[5] / d6);
          localObject1 = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
          localObject2 = localAffineTransform1.createTransformedShape((Shape)localObject1);
          Rectangle2D localRectangle2D2 = ((Shape)localObject2).getBounds2D();
          localRectangle2D2.setRect(localRectangle2D2.getX(), localRectangle2D2.getY(), localRectangle2D2.getWidth() + 0.001D, localRectangle2D2.getHeight() + 0.001D);
          int i3 = (int)localRectangle2D2.getWidth();
          i5 = (int)localRectangle2D2.getHeight();
          i7 = i3 * i5 * 3;
          i8 = 8388608;
          double d7 = d3 < d4 ? d3 : d4;
          int i9 = (int)d7;
          double d8 = 1.0D;
          double d9 = i3 / k;
          double d10 = i5 / m;
          double d11 = d9 > d10 ? d10 : d9;
          int i13 = (int)(i9 / d11);
          if (i13 < 72) {
            i13 = 72;
          }
          while ((i7 > i8) && (i9 > i13))
          {
            d8 *= 2.0D;
            i9 /= 2;
            i7 /= 4;
          }
          if (i9 < i13) {
            d8 = d7 / i13;
          }
          localRectangle2D2.setRect(localRectangle2D2.getX() / d8, localRectangle2D2.getY() / d8, localRectangle2D2.getWidth() / d8, localRectangle2D2.getHeight() / d8);
          localWPrinterJob.saveState(getTransform(), getClip(), localRectangle2D2, d8, d8);
          return true;
        }
        int i1 = 5;
        Object localObject1 = null;
        Object localObject2 = localBufferedImage1.getColorModel();
        int i2 = localBufferedImage1.getType();
        if (((localObject2 instanceof IndexColorModel)) && (((ColorModel)localObject2).getPixelSize() <= 8) && ((i2 == 12) || (i2 == 13)))
        {
          localObject1 = (IndexColorModel)localObject2;
          i1 = i2;
          if ((i2 == 12) && (((ColorModel)localObject2).getPixelSize() == 2))
          {
            int[] arrayOfInt = new int[16];
            ((IndexColorModel)localObject1).getRGBs(arrayOfInt);
            i5 = ((IndexColorModel)localObject1).getTransparency() != 1 ? 1 : 0;
            i7 = ((IndexColorModel)localObject1).getTransparentPixel();
            localObject1 = new IndexColorModel(4, 16, arrayOfInt, 0, i5, i7, 0);
          }
        }
        int i4 = (int)localRectangle2D1.getWidth();
        int i6 = (int)localRectangle2D1.getHeight();
        BufferedImage localBufferedImage2 = null;
        int i8 = 1;
        if (i8 != 0)
        {
          if (localObject1 == null) {
            localBufferedImage2 = new BufferedImage(i4, i6, i1);
          } else {
            localBufferedImage2 = new BufferedImage(i4, i6, i1, (IndexColorModel)localObject1);
          }
          localObject3 = localBufferedImage2.createGraphics();
          ((Graphics2D)localObject3).clipRect(0, 0, localBufferedImage2.getWidth(), localBufferedImage2.getHeight());
          ((Graphics2D)localObject3).translate(-localRectangle2D1.getX(), -localRectangle2D1.getY());
          ((Graphics2D)localObject3).transform(localAffineTransform2);
          if (paramColor == null) {
            paramColor = Color.white;
          }
          ((Graphics2D)localObject3).drawImage(localBufferedImage1, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramColor, null);
          ((Graphics2D)localObject3).dispose();
        }
        else
        {
          localBufferedImage2 = localBufferedImage1;
        }
        Object localObject3 = new Rectangle2D.Float((float)(localRectangle2D1.getX() * d1), (float)(localRectangle2D1.getY() * d2), (float)(localRectangle2D1.getWidth() * d1), (float)(localRectangle2D1.getHeight() * d2));
        WritableRaster localWritableRaster = localBufferedImage2.getRaster();
        byte[] arrayOfByte;
        if ((localWritableRaster instanceof ByteComponentRaster)) {
          arrayOfByte = ((ByteComponentRaster)localWritableRaster).getDataStorage();
        } else if ((localWritableRaster instanceof BytePackedRaster)) {
          arrayOfByte = ((BytePackedRaster)localWritableRaster).getDataStorage();
        } else {
          return false;
        }
        int i10 = 24;
        SampleModel localSampleModel = localBufferedImage2.getSampleModel();
        Object localObject4;
        if ((localSampleModel instanceof ComponentSampleModel))
        {
          localObject4 = (ComponentSampleModel)localSampleModel;
          i10 = ((ComponentSampleModel)localObject4).getPixelStride() * 8;
        }
        else if ((localSampleModel instanceof MultiPixelPackedSampleModel))
        {
          localObject4 = (MultiPixelPackedSampleModel)localSampleModel;
          i10 = ((MultiPixelPackedSampleModel)localObject4).getPixelBitStride();
        }
        else if (localObject1 != null)
        {
          int i11 = localBufferedImage2.getWidth();
          int i12 = localBufferedImage2.getHeight();
          if ((i11 > 0) && (i12 > 0)) {
            i10 = arrayOfByte.length * 8 / i11 / i12;
          }
        }
        Shape localShape2 = getClip();
        clip(paramAffineTransform.createTransformedShape(localFloat));
        deviceClip(getClip().getPathIterator(getTransform()));
        localWPrinterJob.drawDIBImage(arrayOfByte, x, y, (float)Math.rint(width + 0.5D), (float)Math.rint(height + 0.5D), 0.0F, 0.0F, localBufferedImage2.getWidth(), localBufferedImage2.getHeight(), i10, (IndexColorModel)localObject1);
        setClip(localShape2);
      }
    }
    return true;
  }
  
  public void redrawRegion(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, Shape paramShape, AffineTransform paramAffineTransform)
    throws PrinterException
  {
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    Printable localPrintable = getPrintable();
    PageFormat localPageFormat = getPageFormat();
    int i = getPageIndex();
    BufferedImage localBufferedImage = new BufferedImage((int)paramRectangle2D.getWidth(), (int)paramRectangle2D.getHeight(), 5);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    ProxyGraphics2D localProxyGraphics2D = new ProxyGraphics2D(localGraphics2D, localWPrinterJob);
    localProxyGraphics2D.setColor(Color.white);
    localProxyGraphics2D.fillRect(0, 0, localBufferedImage.getWidth(), localBufferedImage.getHeight());
    localProxyGraphics2D.clipRect(0, 0, localBufferedImage.getWidth(), localBufferedImage.getHeight());
    localProxyGraphics2D.translate(-paramRectangle2D.getX(), -paramRectangle2D.getY());
    float f1 = (float)(localWPrinterJob.getXRes() / paramDouble1);
    float f2 = (float)(localWPrinterJob.getYRes() / paramDouble2);
    localProxyGraphics2D.scale(f1 / 72.0F, f2 / 72.0F);
    localProxyGraphics2D.translate(-localWPrinterJob.getPhysicalPrintableX(localPageFormat.getPaper()) / localWPrinterJob.getXRes() * 72.0D, -localWPrinterJob.getPhysicalPrintableY(localPageFormat.getPaper()) / localWPrinterJob.getYRes() * 72.0D);
    localProxyGraphics2D.transform(new AffineTransform(getPageFormat().getMatrix()));
    localProxyGraphics2D.setPaint(Color.black);
    localPrintable.print(localProxyGraphics2D, localPageFormat, i);
    localGraphics2D.dispose();
    if (paramShape != null) {
      deviceClip(paramShape.getPathIterator(paramAffineTransform));
    }
    Rectangle2D.Float localFloat = new Rectangle2D.Float((float)(paramRectangle2D.getX() * paramDouble1), (float)(paramRectangle2D.getY() * paramDouble2), (float)(paramRectangle2D.getWidth() * paramDouble1), (float)(paramRectangle2D.getHeight() * paramDouble2));
    ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)localBufferedImage.getRaster();
    localWPrinterJob.drawImage3ByteBGR(localByteComponentRaster.getDataStorage(), x, y, width, height, 0.0F, 0.0F, localBufferedImage.getWidth(), localBufferedImage.getHeight());
  }
  
  protected void deviceFill(PathIterator paramPathIterator, Color paramColor)
  {
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    convertToWPath(paramPathIterator);
    localWPrinterJob.selectSolidBrush(paramColor);
    localWPrinterJob.fillPath();
  }
  
  protected void deviceClip(PathIterator paramPathIterator)
  {
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    convertToWPath(paramPathIterator);
    localWPrinterJob.selectClipPath();
  }
  
  protected void deviceFrameRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
  {
    AffineTransform localAffineTransform = getTransform();
    int i = localAffineTransform.getType();
    int j = (i & 0x30) != 0 ? 1 : 0;
    if (j != 0)
    {
      draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
      return;
    }
    Stroke localStroke = getStroke();
    if ((localStroke instanceof BasicStroke))
    {
      BasicStroke localBasicStroke = (BasicStroke)localStroke;
      int k = localBasicStroke.getEndCap();
      int m = localBasicStroke.getLineJoin();
      if ((k == 2) && (m == 0) && (localBasicStroke.getMiterLimit() == 10.0F))
      {
        float f1 = localBasicStroke.getLineWidth();
        Point2D.Float localFloat1 = new Point2D.Float(f1, f1);
        localAffineTransform.deltaTransform(localFloat1, localFloat1);
        float f2 = Math.min(Math.abs(x), Math.abs(y));
        Point2D.Float localFloat2 = new Point2D.Float(paramInt1, paramInt2);
        localAffineTransform.transform(localFloat2, localFloat2);
        Point2D.Float localFloat3 = new Point2D.Float(paramInt1 + paramInt3, paramInt2 + paramInt4);
        localAffineTransform.transform(localFloat3, localFloat3);
        float f3 = (float)(localFloat3.getX() - localFloat2.getX());
        float f4 = (float)(localFloat3.getY() - localFloat2.getY());
        WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
        if (localWPrinterJob.selectStylePen(k, m, f2, paramColor) == true)
        {
          localWPrinterJob.frameRect((float)localFloat2.getX(), (float)localFloat2.getY(), f3, f4);
        }
        else
        {
          double d = Math.min(localWPrinterJob.getXRes(), localWPrinterJob.getYRes());
          if (f2 / d < 0.014000000432133675D)
          {
            localWPrinterJob.selectPen(f2, paramColor);
            localWPrinterJob.frameRect((float)localFloat2.getX(), (float)localFloat2.getY(), f3, f4);
          }
          else
          {
            draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
          }
        }
      }
      else
      {
        draw(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
      }
    }
  }
  
  protected void deviceFillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
  {
    AffineTransform localAffineTransform = getTransform();
    int i = localAffineTransform.getType();
    int j = (i & 0x30) != 0 ? 1 : 0;
    if (j != 0)
    {
      fill(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
      return;
    }
    Point2D.Float localFloat1 = new Point2D.Float(paramInt1, paramInt2);
    localAffineTransform.transform(localFloat1, localFloat1);
    Point2D.Float localFloat2 = new Point2D.Float(paramInt1 + paramInt3, paramInt2 + paramInt4);
    localAffineTransform.transform(localFloat2, localFloat2);
    float f1 = (float)(localFloat2.getX() - localFloat1.getX());
    float f2 = (float)(localFloat2.getY() - localFloat1.getY());
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    localWPrinterJob.fillRect((float)localFloat1.getX(), (float)localFloat1.getY(), f1, f2, paramColor);
  }
  
  protected void deviceDrawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
  {
    Stroke localStroke = getStroke();
    if ((localStroke instanceof BasicStroke))
    {
      BasicStroke localBasicStroke = (BasicStroke)localStroke;
      if (localBasicStroke.getDashArray() != null)
      {
        draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
        return;
      }
      float f1 = localBasicStroke.getLineWidth();
      Point2D.Float localFloat1 = new Point2D.Float(f1, f1);
      AffineTransform localAffineTransform = getTransform();
      localAffineTransform.deltaTransform(localFloat1, localFloat1);
      float f2 = Math.min(Math.abs(x), Math.abs(y));
      Point2D.Float localFloat2 = new Point2D.Float(paramInt1, paramInt2);
      localAffineTransform.transform(localFloat2, localFloat2);
      Point2D.Float localFloat3 = new Point2D.Float(paramInt3, paramInt4);
      localAffineTransform.transform(localFloat3, localFloat3);
      int i = localBasicStroke.getEndCap();
      int j = localBasicStroke.getLineJoin();
      if ((localFloat3.getX() == localFloat2.getX()) && (localFloat3.getY() == localFloat2.getY())) {
        i = 1;
      }
      WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
      if (localWPrinterJob.selectStylePen(i, j, f2, paramColor))
      {
        localWPrinterJob.moveTo((float)localFloat2.getX(), (float)localFloat2.getY());
        localWPrinterJob.lineTo((float)localFloat3.getX(), (float)localFloat3.getY());
      }
      else
      {
        double d = Math.min(localWPrinterJob.getXRes(), localWPrinterJob.getYRes());
        if ((i == 1) || (((paramInt1 == paramInt3) || (paramInt2 == paramInt4)) && (f2 / d < 0.014000000432133675D)))
        {
          localWPrinterJob.selectPen(f2, paramColor);
          localWPrinterJob.moveTo((float)localFloat2.getX(), (float)localFloat2.getY());
          localWPrinterJob.lineTo((float)localFloat3.getX(), (float)localFloat3.getY());
        }
        else
        {
          draw(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
        }
      }
    }
  }
  
  private void convertToWPath(PathIterator paramPathIterator)
  {
    float[] arrayOfFloat = new float[6];
    WPrinterJob localWPrinterJob = (WPrinterJob)getPrinterJob();
    int j;
    if (paramPathIterator.getWindingRule() == 0) {
      j = 1;
    } else {
      j = 2;
    }
    localWPrinterJob.setPolyFillMode(j);
    localWPrinterJob.beginPath();
    while (!paramPathIterator.isDone())
    {
      int i = paramPathIterator.currentSegment(arrayOfFloat);
      switch (i)
      {
      case 0: 
        localWPrinterJob.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 1: 
        localWPrinterJob.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 2: 
        int k = localWPrinterJob.getPenX();
        int m = localWPrinterJob.getPenY();
        float f1 = k + (arrayOfFloat[0] - k) * 2.0F / 3.0F;
        float f2 = m + (arrayOfFloat[1] - m) * 2.0F / 3.0F;
        float f3 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
        float f4 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
        localWPrinterJob.polyBezierTo(f1, f2, f3, f4, arrayOfFloat[2], arrayOfFloat[3]);
        break;
      case 3: 
        localWPrinterJob.polyBezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
        break;
      case 4: 
        localWPrinterJob.closeFigure();
      }
      paramPathIterator.next();
    }
    localWPrinterJob.endPath();
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.enableGDITextLayout"));
    if (str != null)
    {
      useGDITextLayout = Boolean.getBoolean(str);
      if ((!useGDITextLayout) && (str.equalsIgnoreCase("prefer")))
      {
        useGDITextLayout = true;
        preferGDITextLayout = true;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPathGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */