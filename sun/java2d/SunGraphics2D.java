package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sun.awt.ConstrainableGraphics;
import sun.awt.SunHints;
import sun.awt.SunHints.Key;
import sun.awt.SunHints.Value;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.font.Font2D;
import sun.font.FontDesignMetrics;
import sun.font.FontUtilities;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.FontInfo;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.ValidatePipe;
import sun.misc.PerformanceLogger;

public final class SunGraphics2D
  extends Graphics2D
  implements ConstrainableGraphics, Cloneable, DestSurfaceProvider
{
  public static final int PAINT_CUSTOM = 6;
  public static final int PAINT_TEXTURE = 5;
  public static final int PAINT_RAD_GRADIENT = 4;
  public static final int PAINT_LIN_GRADIENT = 3;
  public static final int PAINT_GRADIENT = 2;
  public static final int PAINT_ALPHACOLOR = 1;
  public static final int PAINT_OPAQUECOLOR = 0;
  public static final int COMP_CUSTOM = 3;
  public static final int COMP_XOR = 2;
  public static final int COMP_ALPHA = 1;
  public static final int COMP_ISCOPY = 0;
  public static final int STROKE_CUSTOM = 3;
  public static final int STROKE_WIDE = 2;
  public static final int STROKE_THINDASHED = 1;
  public static final int STROKE_THIN = 0;
  public static final int TRANSFORM_GENERIC = 4;
  public static final int TRANSFORM_TRANSLATESCALE = 3;
  public static final int TRANSFORM_ANY_TRANSLATE = 2;
  public static final int TRANSFORM_INT_TRANSLATE = 1;
  public static final int TRANSFORM_ISIDENT = 0;
  public static final int CLIP_SHAPE = 2;
  public static final int CLIP_RECTANGULAR = 1;
  public static final int CLIP_DEVICE = 0;
  public int eargb;
  public int pixel;
  public SurfaceData surfaceData;
  public PixelDrawPipe drawpipe;
  public PixelFillPipe fillpipe;
  public DrawImagePipe imagepipe;
  public ShapeDrawPipe shapepipe;
  public TextPipe textpipe;
  public MaskFill alphafill;
  public RenderLoops loops;
  public CompositeType imageComp;
  public int paintState;
  public int compositeState;
  public int strokeState;
  public int transformState;
  public int clipState;
  public Color foregroundColor;
  public Color backgroundColor;
  public AffineTransform transform;
  public int transX;
  public int transY;
  protected static final Stroke defaultStroke = new BasicStroke();
  protected static final Composite defaultComposite = AlphaComposite.SrcOver;
  private static final Font defaultFont = new Font("Dialog", 0, 12);
  public Paint paint;
  public Stroke stroke;
  public Composite composite;
  protected Font font;
  protected FontMetrics fontMetrics;
  public int renderHint;
  public int antialiasHint;
  public int textAntialiasHint;
  protected int fractionalMetricsHint;
  public int lcdTextContrast;
  private static int lcdTextContrastDefaultValue = 140;
  private int interpolationHint;
  public int strokeHint;
  public int interpolationType;
  public RenderingHints hints;
  public Region constrainClip;
  public int constrainX;
  public int constrainY;
  public Region clipRegion;
  public Shape usrClip;
  protected Region devClip;
  private final int devScale;
  private int resolutionVariantHint;
  private boolean validFontInfo;
  private FontInfo fontInfo;
  private FontInfo glyphVectorFontInfo;
  private FontRenderContext glyphVectorFRC;
  private static final int slowTextTransformMask = 120;
  protected static ValidatePipe invalidpipe;
  private static final double[] IDENT_MATRIX;
  private static final AffineTransform IDENT_ATX;
  private static final int MINALLOCATED = 8;
  private static final int TEXTARRSIZE = 17;
  private static double[][] textTxArr;
  private static AffineTransform[] textAtArr;
  static final int NON_UNIFORM_SCALE_MASK = 36;
  public static final double MinPenSizeAA = RenderingEngine.getInstance().getMinimumAAPenSize();
  public static final double MinPenSizeAASquared = MinPenSizeAA * MinPenSizeAA;
  public static final double MinPenSizeSquared = 1.000000001D;
  static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
  Blit lastCAblit;
  Composite lastCAcomp;
  private FontRenderContext cachedFRC;
  
  public SunGraphics2D(SurfaceData paramSurfaceData, Color paramColor1, Color paramColor2, Font paramFont)
  {
    surfaceData = paramSurfaceData;
    foregroundColor = paramColor1;
    backgroundColor = paramColor2;
    transform = new AffineTransform();
    stroke = defaultStroke;
    composite = defaultComposite;
    paint = foregroundColor;
    imageComp = CompositeType.SrcOverNoEa;
    renderHint = 0;
    antialiasHint = 1;
    textAntialiasHint = 0;
    fractionalMetricsHint = 1;
    lcdTextContrast = lcdTextContrastDefaultValue;
    interpolationHint = -1;
    strokeHint = 0;
    resolutionVariantHint = 0;
    interpolationType = 1;
    validateColor();
    devScale = paramSurfaceData.getDefaultScale();
    if (devScale != 1)
    {
      transform.setToScale(devScale, devScale);
      invalidateTransform();
    }
    font = paramFont;
    if (font == null) {
      font = defaultFont;
    }
    setDevClip(paramSurfaceData.getBounds());
    invalidatePipe();
  }
  
  protected Object clone()
  {
    try
    {
      SunGraphics2D localSunGraphics2D = (SunGraphics2D)super.clone();
      transform = new AffineTransform(transform);
      if (hints != null) {
        hints = ((RenderingHints)hints.clone());
      }
      if (fontInfo != null) {
        if (validFontInfo) {
          fontInfo = ((FontInfo)fontInfo.clone());
        } else {
          fontInfo = null;
        }
      }
      if (glyphVectorFontInfo != null)
      {
        glyphVectorFontInfo = ((FontInfo)glyphVectorFontInfo.clone());
        glyphVectorFRC = glyphVectorFRC;
      }
      return localSunGraphics2D;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public Graphics create()
  {
    return (Graphics)clone();
  }
  
  public void setDevClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Region localRegion = constrainClip;
    if (localRegion == null) {
      devClip = Region.getInstanceXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      devClip = localRegion.getIntersectionXYWH(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    validateCompClip();
  }
  
  public void setDevClip(Rectangle paramRectangle)
  {
    setDevClip(x, y, width, height);
  }
  
  public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion)
  {
    if ((paramInt1 | paramInt2) != 0) {
      translate(paramInt1, paramInt2);
    }
    if (transformState > 3)
    {
      clipRect(0, 0, paramInt3, paramInt4);
      return;
    }
    double d1 = transform.getScaleX();
    double d2 = transform.getScaleY();
    paramInt1 = constrainX = (int)transform.getTranslateX();
    paramInt2 = constrainY = (int)transform.getTranslateY();
    paramInt3 = Region.dimAdd(paramInt1, Region.clipScale(paramInt3, d1));
    paramInt4 = Region.dimAdd(paramInt2, Region.clipScale(paramInt4, d2));
    Region localRegion = constrainClip;
    if (localRegion == null) {
      localRegion = Region.getInstanceXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      localRegion = localRegion.getIntersectionXYXY(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    if (paramRegion != null)
    {
      paramRegion = paramRegion.getScaledRegion(d1, d2);
      paramRegion = paramRegion.getTranslatedRegion(paramInt1, paramInt2);
      localRegion = localRegion.getIntersection(paramRegion);
    }
    if (localRegion == constrainClip) {
      return;
    }
    constrainClip = localRegion;
    if (!devClip.isInsideQuickCheck(localRegion))
    {
      devClip = devClip.getIntersection(localRegion);
      validateCompClip();
    }
  }
  
  public void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    constrain(paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  protected void invalidatePipe()
  {
    drawpipe = invalidpipe;
    fillpipe = invalidpipe;
    shapepipe = invalidpipe;
    textpipe = invalidpipe;
    imagepipe = invalidpipe;
    loops = null;
  }
  
  public void validatePipe()
  {
    if (!surfaceData.isValid()) {
      throw new InvalidPipeException("attempt to validate Pipe with invalid SurfaceData");
    }
    surfaceData.validatePipe(this);
  }
  
  Shape intersectShapes(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (((paramShape1 instanceof Rectangle)) && ((paramShape2 instanceof Rectangle))) {
      return ((Rectangle)paramShape1).intersection((Rectangle)paramShape2);
    }
    if ((paramShape1 instanceof Rectangle2D)) {
      return intersectRectShape((Rectangle2D)paramShape1, paramShape2, paramBoolean1, paramBoolean2);
    }
    if ((paramShape2 instanceof Rectangle2D)) {
      return intersectRectShape((Rectangle2D)paramShape2, paramShape1, paramBoolean2, paramBoolean1);
    }
    return intersectByArea(paramShape1, paramShape2, paramBoolean1, paramBoolean2);
  }
  
  Shape intersectRectShape(Rectangle2D paramRectangle2D, Shape paramShape, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramShape instanceof Rectangle2D))
    {
      Rectangle2D localRectangle2D = (Rectangle2D)paramShape;
      Object localObject;
      if (!paramBoolean1) {
        localObject = paramRectangle2D;
      } else if (!paramBoolean2) {
        localObject = localRectangle2D;
      } else {
        localObject = new Rectangle2D.Float();
      }
      double d1 = Math.max(paramRectangle2D.getX(), localRectangle2D.getX());
      double d2 = Math.min(paramRectangle2D.getX() + paramRectangle2D.getWidth(), localRectangle2D.getX() + localRectangle2D.getWidth());
      double d3 = Math.max(paramRectangle2D.getY(), localRectangle2D.getY());
      double d4 = Math.min(paramRectangle2D.getY() + paramRectangle2D.getHeight(), localRectangle2D.getY() + localRectangle2D.getHeight());
      if ((d2 - d1 < 0.0D) || (d4 - d3 < 0.0D)) {
        ((Rectangle2D)localObject).setFrameFromDiagonal(0.0D, 0.0D, 0.0D, 0.0D);
      } else {
        ((Rectangle2D)localObject).setFrameFromDiagonal(d1, d3, d2, d4);
      }
      return (Shape)localObject;
    }
    if (paramRectangle2D.contains(paramShape.getBounds2D()))
    {
      if (paramBoolean2) {
        paramShape = cloneShape(paramShape);
      }
      return paramShape;
    }
    return intersectByArea(paramRectangle2D, paramShape, paramBoolean1, paramBoolean2);
  }
  
  protected static Shape cloneShape(Shape paramShape)
  {
    return new GeneralPath(paramShape);
  }
  
  Shape intersectByArea(Shape paramShape1, Shape paramShape2, boolean paramBoolean1, boolean paramBoolean2)
  {
    Area localArea1;
    if ((!paramBoolean1) && ((paramShape1 instanceof Area)))
    {
      localArea1 = (Area)paramShape1;
    }
    else if ((!paramBoolean2) && ((paramShape2 instanceof Area)))
    {
      localArea1 = (Area)paramShape2;
      paramShape2 = paramShape1;
    }
    else
    {
      localArea1 = new Area(paramShape1);
    }
    Area localArea2;
    if ((paramShape2 instanceof Area)) {
      localArea2 = (Area)paramShape2;
    } else {
      localArea2 = new Area(paramShape2);
    }
    localArea1.intersect(localArea2);
    if (localArea1.isRectangular()) {
      return localArea1.getBounds();
    }
    return localArea1;
  }
  
  public Region getCompClip()
  {
    if (!surfaceData.isValid()) {
      revalidateAll();
    }
    return clipRegion;
  }
  
  public Font getFont()
  {
    if (font == null) {
      font = defaultFont;
    }
    return font;
  }
  
  public FontInfo checkFontInfo(FontInfo paramFontInfo, Font paramFont, FontRenderContext paramFontRenderContext)
  {
    if (paramFontInfo == null) {
      paramFontInfo = new FontInfo();
    }
    float f = paramFont.getSize2D();
    AffineTransform localAffineTransform2 = null;
    int i;
    AffineTransform localAffineTransform1;
    double d3;
    if (paramFont.isTransformed())
    {
      localAffineTransform2 = paramFont.getTransform();
      localAffineTransform2.scale(f, f);
      i = localAffineTransform2.getType();
      originX = ((float)localAffineTransform2.getTranslateX());
      originY = ((float)localAffineTransform2.getTranslateY());
      localAffineTransform2.translate(-originX, -originY);
      if (transformState >= 3)
      {
        transform.getMatrix(devTx = new double[4]);
        localAffineTransform1 = new AffineTransform(devTx);
        localAffineTransform2.preConcatenate(localAffineTransform1);
      }
      else
      {
        devTx = IDENT_MATRIX;
        localAffineTransform1 = IDENT_ATX;
      }
      localAffineTransform2.getMatrix(glyphTx = new double[4]);
      double d1 = localAffineTransform2.getShearX();
      d3 = localAffineTransform2.getScaleY();
      if (d1 != 0.0D) {
        d3 = Math.sqrt(d1 * d1 + d3 * d3);
      }
      pixelHeight = ((int)(Math.abs(d3) + 0.5D));
    }
    else
    {
      i = 0;
      originX = (originY = 0.0F);
      if (transformState >= 3)
      {
        transform.getMatrix(devTx = new double[4]);
        localAffineTransform1 = new AffineTransform(devTx);
        glyphTx = new double[4];
        for (int j = 0; j < 4; j++) {
          glyphTx[j] = (devTx[j] * f);
        }
        localAffineTransform2 = new AffineTransform(glyphTx);
        double d2 = transform.getShearX();
        d3 = transform.getScaleY();
        if (d2 != 0.0D) {
          d3 = Math.sqrt(d2 * d2 + d3 * d3);
        }
        pixelHeight = ((int)(Math.abs(d3 * f) + 0.5D));
      }
      else
      {
        k = (int)f;
        if ((f == k) && (k >= 8) && (k < 17))
        {
          glyphTx = textTxArr[k];
          localAffineTransform2 = textAtArr[k];
          pixelHeight = k;
        }
        else
        {
          pixelHeight = ((int)(f + 0.5D));
        }
        if (localAffineTransform2 == null)
        {
          glyphTx = new double[] { f, 0.0D, 0.0D, f };
          localAffineTransform2 = new AffineTransform(glyphTx);
        }
        devTx = IDENT_MATRIX;
        localAffineTransform1 = IDENT_ATX;
      }
    }
    font2D = FontUtilities.getFont2D(paramFont);
    int k = fractionalMetricsHint;
    if (k == 0) {
      k = 1;
    }
    lcdSubPixPos = false;
    int m;
    if (paramFontRenderContext == null) {
      m = textAntialiasHint;
    } else {
      m = ((SunHints.Value)paramFontRenderContext.getAntiAliasingHint()).getIndex();
    }
    if (m == 0)
    {
      if (antialiasHint == 2) {
        m = 2;
      } else {
        m = 1;
      }
    }
    else if (m == 3)
    {
      if (font2D.useAAForPtSize(pixelHeight)) {
        m = 2;
      } else {
        m = 1;
      }
    }
    else if (m >= 4) {
      if (!surfaceData.canRenderLCDText(this))
      {
        m = 2;
      }
      else
      {
        lcdRGBOrder = true;
        if (m == 5)
        {
          m = 4;
          lcdRGBOrder = false;
        }
        else if (m == 7)
        {
          m = 6;
          lcdRGBOrder = false;
        }
        lcdSubPixPos = ((k == 2) && (m == 4));
      }
    }
    aaHint = m;
    fontStrike = font2D.getStrike(paramFont, localAffineTransform1, localAffineTransform2, m, k);
    return paramFontInfo;
  }
  
  public static boolean isRotated(double[] paramArrayOfDouble)
  {
    return (paramArrayOfDouble[0] != paramArrayOfDouble[3]) || (paramArrayOfDouble[1] != 0.0D) || (paramArrayOfDouble[2] != 0.0D) || (paramArrayOfDouble[0] <= 0.0D);
  }
  
  public void setFont(Font paramFont)
  {
    if ((paramFont != null) && (paramFont != font))
    {
      if ((textAntialiasHint == 3) && (textpipe != invalidpipe)) {
        if ((transformState <= 2) && (!paramFont.isTransformed()) && (fontInfo != null))
        {
          if ((fontInfo.aaHint == 2) == FontUtilities.getFont2D(paramFont).useAAForPtSize(paramFont.getSize())) {}
        }
        else {
          textpipe = invalidpipe;
        }
      }
      font = paramFont;
      fontMetrics = null;
      validFontInfo = false;
    }
  }
  
  public FontInfo getFontInfo()
  {
    if (!validFontInfo)
    {
      fontInfo = checkFontInfo(fontInfo, font, null);
      validFontInfo = true;
    }
    return fontInfo;
  }
  
  public FontInfo getGVFontInfo(Font paramFont, FontRenderContext paramFontRenderContext)
  {
    if ((glyphVectorFontInfo != null) && (glyphVectorFontInfo.font == paramFont) && (glyphVectorFRC == paramFontRenderContext)) {
      return glyphVectorFontInfo;
    }
    glyphVectorFRC = paramFontRenderContext;
    return glyphVectorFontInfo = checkFontInfo(glyphVectorFontInfo, paramFont, paramFontRenderContext);
  }
  
  public FontMetrics getFontMetrics()
  {
    if (fontMetrics != null) {
      return fontMetrics;
    }
    return fontMetrics = FontDesignMetrics.getMetrics(font, getFontRenderContext());
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    if ((fontMetrics != null) && (paramFont == font)) {
      return fontMetrics;
    }
    FontDesignMetrics localFontDesignMetrics = FontDesignMetrics.getMetrics(paramFont, getFontRenderContext());
    if (font == paramFont) {
      fontMetrics = localFontDesignMetrics;
    }
    return localFontDesignMetrics;
  }
  
  public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramShape = stroke.createStrokedShape(paramShape);
    }
    paramShape = transformShape(paramShape);
    if ((constrainX | constrainY) != 0)
    {
      paramRectangle = new Rectangle(paramRectangle);
      paramRectangle.translate(constrainX, constrainY);
    }
    return paramShape.intersects(paramRectangle);
  }
  
  public ColorModel getDeviceColorModel()
  {
    return surfaceData.getColorModel();
  }
  
  public GraphicsConfiguration getDeviceConfiguration()
  {
    return surfaceData.getDeviceConfiguration();
  }
  
  public final SurfaceData getSurfaceData()
  {
    return surfaceData;
  }
  
  public void setComposite(Composite paramComposite)
  {
    if (composite == paramComposite) {
      return;
    }
    CompositeType localCompositeType;
    int i;
    if ((paramComposite instanceof AlphaComposite))
    {
      AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
      localCompositeType = CompositeType.forAlphaComposite(localAlphaComposite);
      if (localCompositeType == CompositeType.SrcOverNoEa)
      {
        if ((paintState == 0) || ((paintState > 1) && (paint.getTransparency() == 1))) {
          i = 0;
        } else {
          i = 1;
        }
      }
      else if ((localCompositeType == CompositeType.SrcNoEa) || (localCompositeType == CompositeType.Src) || (localCompositeType == CompositeType.Clear)) {
        i = 0;
      } else if ((surfaceData.getTransparency() == 1) && (localCompositeType == CompositeType.SrcIn)) {
        i = 0;
      } else {
        i = 1;
      }
    }
    else if ((paramComposite instanceof XORComposite))
    {
      i = 2;
      localCompositeType = CompositeType.Xor;
    }
    else
    {
      if (paramComposite == null) {
        throw new IllegalArgumentException("null Composite");
      }
      surfaceData.checkCustomComposite();
      i = 3;
      localCompositeType = CompositeType.General;
    }
    if ((compositeState != i) || (imageComp != localCompositeType))
    {
      compositeState = i;
      imageComp = localCompositeType;
      invalidatePipe();
      validFontInfo = false;
    }
    composite = paramComposite;
    if (paintState <= 1) {
      validateColor();
    }
  }
  
  public void setPaint(Paint paramPaint)
  {
    if ((paramPaint instanceof Color))
    {
      setColor((Color)paramPaint);
      return;
    }
    if ((paramPaint == null) || (paint == paramPaint)) {
      return;
    }
    paint = paramPaint;
    if (imageComp == CompositeType.SrcOverNoEa) {
      if (paramPaint.getTransparency() == 1)
      {
        if (compositeState != 0) {
          compositeState = 0;
        }
      }
      else if (compositeState == 0) {
        compositeState = 1;
      }
    }
    Class localClass = paramPaint.getClass();
    if (localClass == GradientPaint.class) {
      paintState = 2;
    } else if (localClass == LinearGradientPaint.class) {
      paintState = 3;
    } else if (localClass == RadialGradientPaint.class) {
      paintState = 4;
    } else if (localClass == TexturePaint.class) {
      paintState = 5;
    } else {
      paintState = 6;
    }
    validFontInfo = false;
    invalidatePipe();
  }
  
  private void validateBasicStroke(BasicStroke paramBasicStroke)
  {
    int i = antialiasHint == 2 ? 1 : 0;
    if (transformState < 3)
    {
      if (i != 0)
      {
        if (paramBasicStroke.getLineWidth() <= MinPenSizeAA)
        {
          if (paramBasicStroke.getDashArray() == null) {
            strokeState = 0;
          } else {
            strokeState = 1;
          }
        }
        else {
          strokeState = 2;
        }
      }
      else if (paramBasicStroke == defaultStroke) {
        strokeState = 0;
      } else if (paramBasicStroke.getLineWidth() <= 1.0F)
      {
        if (paramBasicStroke.getDashArray() == null) {
          strokeState = 0;
        } else {
          strokeState = 1;
        }
      }
      else {
        strokeState = 2;
      }
    }
    else
    {
      double d1;
      if ((transform.getType() & 0x24) == 0)
      {
        d1 = Math.abs(transform.getDeterminant());
      }
      else
      {
        double d2 = transform.getScaleX();
        double d3 = transform.getShearX();
        double d4 = transform.getShearY();
        double d5 = transform.getScaleY();
        double d6 = d2 * d2 + d4 * d4;
        double d7 = 2.0D * (d2 * d3 + d4 * d5);
        double d8 = d3 * d3 + d5 * d5;
        double d9 = Math.sqrt(d7 * d7 + (d6 - d8) * (d6 - d8));
        d1 = (d6 + d8 + d9) / 2.0D;
      }
      if (paramBasicStroke != defaultStroke) {
        d1 *= paramBasicStroke.getLineWidth() * paramBasicStroke.getLineWidth();
      }
      if (d1 <= (i != 0 ? MinPenSizeAASquared : 1.000000001D))
      {
        if (paramBasicStroke.getDashArray() == null) {
          strokeState = 0;
        } else {
          strokeState = 1;
        }
      }
      else {
        strokeState = 2;
      }
    }
  }
  
  public void setStroke(Stroke paramStroke)
  {
    if (paramStroke == null) {
      throw new IllegalArgumentException("null Stroke");
    }
    int i = strokeState;
    stroke = paramStroke;
    if ((paramStroke instanceof BasicStroke)) {
      validateBasicStroke((BasicStroke)paramStroke);
    } else {
      strokeState = 3;
    }
    if (strokeState != i) {
      invalidatePipe();
    }
  }
  
  public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject)
  {
    if (!paramKey.isCompatibleValue(paramObject)) {
      throw new IllegalArgumentException(paramObject + " is not compatible with " + paramKey);
    }
    if ((paramKey instanceof SunHints.Key))
    {
      int j = 0;
      int k = 1;
      SunHints.Key localKey = (SunHints.Key)paramKey;
      int m;
      if (localKey == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) {
        m = ((Integer)paramObject).intValue();
      } else {
        m = ((SunHints.Value)paramObject).getIndex();
      }
      int i;
      switch (localKey.getIndex())
      {
      case 0: 
        i = renderHint != m ? 1 : 0;
        if (i != 0)
        {
          renderHint = m;
          if (interpolationHint == -1) {
            interpolationType = (m == 2 ? 2 : 1);
          }
        }
        break;
      case 1: 
        i = antialiasHint != m ? 1 : 0;
        antialiasHint = m;
        if (i != 0)
        {
          j = textAntialiasHint == 0 ? 1 : 0;
          if (strokeState != 3) {
            validateBasicStroke((BasicStroke)stroke);
          }
        }
        break;
      case 2: 
        i = textAntialiasHint != m ? 1 : 0;
        j = i;
        textAntialiasHint = m;
        break;
      case 3: 
        i = fractionalMetricsHint != m ? 1 : 0;
        j = i;
        fractionalMetricsHint = m;
        break;
      case 100: 
        i = 0;
        lcdTextContrast = m;
        break;
      case 5: 
        interpolationHint = m;
        switch (m)
        {
        case 2: 
          m = 3;
          break;
        case 1: 
          m = 2;
          break;
        case 0: 
        default: 
          m = 1;
        }
        i = interpolationType != m ? 1 : 0;
        interpolationType = m;
        break;
      case 8: 
        i = strokeHint != m ? 1 : 0;
        strokeHint = m;
        break;
      case 9: 
        i = resolutionVariantHint != m ? 1 : 0;
        resolutionVariantHint = m;
        break;
      default: 
        k = 0;
        i = 0;
      }
      if (k != 0)
      {
        if (i != 0)
        {
          invalidatePipe();
          if (j != 0)
          {
            fontMetrics = null;
            cachedFRC = null;
            validFontInfo = false;
            glyphVectorFontInfo = null;
          }
        }
        if (hints != null) {
          hints.put(paramKey, paramObject);
        }
        return;
      }
    }
    if (hints == null) {
      hints = makeHints(null);
    }
    hints.put(paramKey, paramObject);
  }
  
  public Object getRenderingHint(RenderingHints.Key paramKey)
  {
    if (hints != null) {
      return hints.get(paramKey);
    }
    if (!(paramKey instanceof SunHints.Key)) {
      return null;
    }
    int i = ((SunHints.Key)paramKey).getIndex();
    switch (i)
    {
    case 0: 
      return SunHints.Value.get(0, renderHint);
    case 1: 
      return SunHints.Value.get(1, antialiasHint);
    case 2: 
      return SunHints.Value.get(2, textAntialiasHint);
    case 3: 
      return SunHints.Value.get(3, fractionalMetricsHint);
    case 100: 
      return new Integer(lcdTextContrast);
    case 5: 
      switch (interpolationHint)
      {
      case 0: 
        return SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      case 1: 
        return SunHints.VALUE_INTERPOLATION_BILINEAR;
      case 2: 
        return SunHints.VALUE_INTERPOLATION_BICUBIC;
      }
      return null;
    case 8: 
      return SunHints.Value.get(8, strokeHint);
    case 9: 
      return SunHints.Value.get(9, resolutionVariantHint);
    }
    return null;
  }
  
  public void setRenderingHints(Map<?, ?> paramMap)
  {
    hints = null;
    renderHint = 0;
    antialiasHint = 1;
    textAntialiasHint = 0;
    fractionalMetricsHint = 1;
    lcdTextContrast = lcdTextContrastDefaultValue;
    interpolationHint = -1;
    interpolationType = 1;
    int i = 0;
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject == SunHints.KEY_RENDERING) || (localObject == SunHints.KEY_ANTIALIASING) || (localObject == SunHints.KEY_TEXT_ANTIALIASING) || (localObject == SunHints.KEY_FRACTIONALMETRICS) || (localObject == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) || (localObject == SunHints.KEY_STROKE_CONTROL) || (localObject == SunHints.KEY_INTERPOLATION)) {
        setRenderingHint((RenderingHints.Key)localObject, paramMap.get(localObject));
      } else {
        i = 1;
      }
    }
    if (i != 0) {
      hints = makeHints(paramMap);
    }
    invalidatePipe();
  }
  
  public void addRenderingHints(Map<?, ?> paramMap)
  {
    int i = 0;
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject == SunHints.KEY_RENDERING) || (localObject == SunHints.KEY_ANTIALIASING) || (localObject == SunHints.KEY_TEXT_ANTIALIASING) || (localObject == SunHints.KEY_FRACTIONALMETRICS) || (localObject == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) || (localObject == SunHints.KEY_STROKE_CONTROL) || (localObject == SunHints.KEY_INTERPOLATION)) {
        setRenderingHint((RenderingHints.Key)localObject, paramMap.get(localObject));
      } else {
        i = 1;
      }
    }
    if (i != 0) {
      if (hints == null) {
        hints = makeHints(paramMap);
      } else {
        hints.putAll(paramMap);
      }
    }
  }
  
  public RenderingHints getRenderingHints()
  {
    if (hints == null) {
      return makeHints(null);
    }
    return (RenderingHints)hints.clone();
  }
  
  RenderingHints makeHints(Map paramMap)
  {
    RenderingHints localRenderingHints = new RenderingHints(paramMap);
    localRenderingHints.put(SunHints.KEY_RENDERING, SunHints.Value.get(0, renderHint));
    localRenderingHints.put(SunHints.KEY_ANTIALIASING, SunHints.Value.get(1, antialiasHint));
    localRenderingHints.put(SunHints.KEY_TEXT_ANTIALIASING, SunHints.Value.get(2, textAntialiasHint));
    localRenderingHints.put(SunHints.KEY_FRACTIONALMETRICS, SunHints.Value.get(3, fractionalMetricsHint));
    localRenderingHints.put(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, Integer.valueOf(lcdTextContrast));
    Object localObject;
    switch (interpolationHint)
    {
    case 0: 
      localObject = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      break;
    case 1: 
      localObject = SunHints.VALUE_INTERPOLATION_BILINEAR;
      break;
    case 2: 
      localObject = SunHints.VALUE_INTERPOLATION_BICUBIC;
      break;
    default: 
      localObject = null;
    }
    if (localObject != null) {
      localRenderingHints.put(SunHints.KEY_INTERPOLATION, localObject);
    }
    localRenderingHints.put(SunHints.KEY_STROKE_CONTROL, SunHints.Value.get(8, strokeHint));
    return localRenderingHints;
  }
  
  public void translate(double paramDouble1, double paramDouble2)
  {
    transform.translate(paramDouble1, paramDouble2);
    invalidateTransform();
  }
  
  public void rotate(double paramDouble)
  {
    transform.rotate(paramDouble);
    invalidateTransform();
  }
  
  public void rotate(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    transform.rotate(paramDouble1, paramDouble2, paramDouble3);
    invalidateTransform();
  }
  
  public void scale(double paramDouble1, double paramDouble2)
  {
    transform.scale(paramDouble1, paramDouble2);
    invalidateTransform();
  }
  
  public void shear(double paramDouble1, double paramDouble2)
  {
    transform.shear(paramDouble1, paramDouble2);
    invalidateTransform();
  }
  
  public void transform(AffineTransform paramAffineTransform)
  {
    transform.concatenate(paramAffineTransform);
    invalidateTransform();
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    transform.translate(paramInt1, paramInt2);
    if (transformState <= 1)
    {
      transX += paramInt1;
      transY += paramInt2;
      transformState = ((transX | transY) == 0 ? 0 : 1);
    }
    else
    {
      invalidateTransform();
    }
  }
  
  public void setTransform(AffineTransform paramAffineTransform)
  {
    if (((constrainX | constrainY) == 0) && (devScale == 1))
    {
      transform.setTransform(paramAffineTransform);
    }
    else
    {
      transform.setTransform(devScale, 0.0D, 0.0D, devScale, constrainX, constrainY);
      transform.concatenate(paramAffineTransform);
    }
    invalidateTransform();
  }
  
  protected void invalidateTransform()
  {
    int i = transform.getType();
    int j = transformState;
    if (i == 0)
    {
      transformState = 0;
      transX = (transY = 0);
    }
    else if (i == 1)
    {
      double d1 = transform.getTranslateX();
      double d2 = transform.getTranslateY();
      transX = ((int)Math.floor(d1 + 0.5D));
      transY = ((int)Math.floor(d2 + 0.5D));
      if ((d1 == transX) && (d2 == transY)) {
        transformState = 1;
      } else {
        transformState = 2;
      }
    }
    else if ((i & 0x78) == 0)
    {
      transformState = 3;
      transX = (transY = 0);
    }
    else
    {
      transformState = 4;
      transX = (transY = 0);
    }
    if ((transformState >= 3) || (j >= 3))
    {
      cachedFRC = null;
      validFontInfo = false;
      fontMetrics = null;
      glyphVectorFontInfo = null;
      if (transformState != j) {
        invalidatePipe();
      }
    }
    if (strokeState != 3) {
      validateBasicStroke((BasicStroke)stroke);
    }
  }
  
  public AffineTransform getTransform()
  {
    if (((constrainX | constrainY) == 0) && (devScale == 1)) {
      return new AffineTransform(transform);
    }
    double d = 1.0D / devScale;
    AffineTransform localAffineTransform = new AffineTransform(d, 0.0D, 0.0D, d, -constrainX * d, -constrainY * d);
    localAffineTransform.concatenate(transform);
    return localAffineTransform;
  }
  
  public AffineTransform cloneTransform()
  {
    return new AffineTransform(transform);
  }
  
  public Paint getPaint()
  {
    return paint;
  }
  
  public Composite getComposite()
  {
    return composite;
  }
  
  public Color getColor()
  {
    return foregroundColor;
  }
  
  final void validateColor()
  {
    int i;
    if (imageComp == CompositeType.Clear)
    {
      i = 0;
    }
    else
    {
      i = foregroundColor.getRGB();
      if ((compositeState <= 1) && (imageComp != CompositeType.SrcNoEa) && (imageComp != CompositeType.SrcOverNoEa))
      {
        AlphaComposite localAlphaComposite = (AlphaComposite)composite;
        int j = Math.round(localAlphaComposite.getAlpha() * (i >>> 24));
        i = i & 0xFFFFFF | j << 24;
      }
    }
    eargb = i;
    pixel = surfaceData.pixelFor(i);
  }
  
  public void setColor(Color paramColor)
  {
    if ((paramColor == null) || (paramColor == paint)) {
      return;
    }
    paint = (foregroundColor = paramColor);
    validateColor();
    if (eargb >> 24 == -1)
    {
      if (paintState == 0) {
        return;
      }
      paintState = 0;
      if (imageComp == CompositeType.SrcOverNoEa) {
        compositeState = 0;
      }
    }
    else
    {
      if (paintState == 1) {
        return;
      }
      paintState = 1;
      if (imageComp == CompositeType.SrcOverNoEa) {
        compositeState = 1;
      }
    }
    validFontInfo = false;
    invalidatePipe();
  }
  
  public void setBackground(Color paramColor)
  {
    backgroundColor = paramColor;
  }
  
  public Color getBackground()
  {
    return backgroundColor;
  }
  
  public Stroke getStroke()
  {
    return stroke;
  }
  
  public Rectangle getClipBounds()
  {
    if (clipState == 0) {
      return null;
    }
    return getClipBounds(new Rectangle());
  }
  
  public Rectangle getClipBounds(Rectangle paramRectangle)
  {
    if (clipState != 0)
    {
      if (transformState <= 1)
      {
        if ((usrClip instanceof Rectangle)) {
          paramRectangle.setBounds((Rectangle)usrClip);
        } else {
          paramRectangle.setFrame(usrClip.getBounds2D());
        }
        paramRectangle.translate(-transX, -transY);
      }
      else
      {
        paramRectangle.setFrame(getClip().getBounds2D());
      }
    }
    else if (paramRectangle == null) {
      throw new NullPointerException("null rectangle parameter");
    }
    return paramRectangle;
  }
  
  public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
      return false;
    }
    if (transformState > 1)
    {
      double[] arrayOfDouble = { paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2, paramInt1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4 };
      transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
      paramInt1 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[0], arrayOfDouble[2]), Math.min(arrayOfDouble[4], arrayOfDouble[6])));
      paramInt2 = (int)Math.floor(Math.min(Math.min(arrayOfDouble[1], arrayOfDouble[3]), Math.min(arrayOfDouble[5], arrayOfDouble[7])));
      paramInt3 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[0], arrayOfDouble[2]), Math.max(arrayOfDouble[4], arrayOfDouble[6])));
      paramInt4 = (int)Math.ceil(Math.max(Math.max(arrayOfDouble[1], arrayOfDouble[3]), Math.max(arrayOfDouble[5], arrayOfDouble[7])));
    }
    else
    {
      paramInt1 += transX;
      paramInt2 += transY;
      paramInt3 += paramInt1;
      paramInt4 += paramInt2;
    }
    try
    {
      if (!getCompClip().intersectsQuickCheckXYXY(paramInt1, paramInt2, paramInt3, paramInt4)) {
        return false;
      }
    }
    catch (InvalidPipeException localInvalidPipeException)
    {
      return false;
    }
    return true;
  }
  
  protected void validateCompClip()
  {
    int i = clipState;
    if (usrClip == null)
    {
      clipState = 0;
      clipRegion = devClip;
    }
    else if ((usrClip instanceof Rectangle2D))
    {
      clipState = 1;
      if ((usrClip instanceof Rectangle)) {
        clipRegion = devClip.getIntersection((Rectangle)usrClip);
      } else {
        clipRegion = devClip.getIntersection(usrClip.getBounds());
      }
    }
    else
    {
      PathIterator localPathIterator = usrClip.getPathIterator(null);
      int[] arrayOfInt = new int[4];
      ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(this);
      try
      {
        localShapeSpanIterator.setOutputArea(devClip);
        localShapeSpanIterator.appendPath(localPathIterator);
        localShapeSpanIterator.getPathBox(arrayOfInt);
        Region localRegion = Region.getInstance(arrayOfInt);
        localRegion.appendSpans(localShapeSpanIterator);
        clipRegion = localRegion;
        clipState = (localRegion.isRectangular() ? 1 : 2);
      }
      finally
      {
        localShapeSpanIterator.dispose();
      }
    }
    if ((i != clipState) && ((clipState == 2) || (i == 2)))
    {
      validFontInfo = false;
      invalidatePipe();
    }
  }
  
  protected Shape transformShape(Shape paramShape)
  {
    if (paramShape == null) {
      return null;
    }
    if (transformState > 1) {
      return transformShape(transform, paramShape);
    }
    return transformShape(transX, transY, paramShape);
  }
  
  public Shape untransformShape(Shape paramShape)
  {
    if (paramShape == null) {
      return null;
    }
    if (transformState > 1) {
      try
      {
        return transformShape(transform.createInverse(), paramShape);
      }
      catch (NoninvertibleTransformException localNoninvertibleTransformException)
      {
        return null;
      }
    }
    return transformShape(-transX, -transY, paramShape);
  }
  
  protected static Shape transformShape(int paramInt1, int paramInt2, Shape paramShape)
  {
    if (paramShape == null) {
      return null;
    }
    if ((paramShape instanceof Rectangle))
    {
      localObject = paramShape.getBounds();
      ((Rectangle)localObject).translate(paramInt1, paramInt2);
      return (Shape)localObject;
    }
    if ((paramShape instanceof Rectangle2D))
    {
      localObject = (Rectangle2D)paramShape;
      return new Rectangle2D.Double(((Rectangle2D)localObject).getX() + paramInt1, ((Rectangle2D)localObject).getY() + paramInt2, ((Rectangle2D)localObject).getWidth(), ((Rectangle2D)localObject).getHeight());
    }
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return cloneShape(paramShape);
    }
    Object localObject = AffineTransform.getTranslateInstance(paramInt1, paramInt2);
    return ((AffineTransform)localObject).createTransformedShape(paramShape);
  }
  
  protected static Shape transformShape(AffineTransform paramAffineTransform, Shape paramShape)
  {
    if (paramShape == null) {
      return null;
    }
    if (((paramShape instanceof Rectangle2D)) && ((paramAffineTransform.getType() & 0x30) == 0))
    {
      Rectangle2D localRectangle2D = (Rectangle2D)paramShape;
      double[] arrayOfDouble = new double[4];
      arrayOfDouble[0] = localRectangle2D.getX();
      arrayOfDouble[1] = localRectangle2D.getY();
      arrayOfDouble[2] = (arrayOfDouble[0] + localRectangle2D.getWidth());
      arrayOfDouble[3] = (arrayOfDouble[1] + localRectangle2D.getHeight());
      paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
      fixRectangleOrientation(arrayOfDouble, localRectangle2D);
      return new Rectangle2D.Double(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2] - arrayOfDouble[0], arrayOfDouble[3] - arrayOfDouble[1]);
    }
    if (paramAffineTransform.isIdentity()) {
      return cloneShape(paramShape);
    }
    return paramAffineTransform.createTransformedShape(paramShape);
  }
  
  private static void fixRectangleOrientation(double[] paramArrayOfDouble, Rectangle2D paramRectangle2D)
  {
    double d;
    if ((paramRectangle2D.getWidth() > 0.0D ? 1 : 0) != (paramArrayOfDouble[2] - paramArrayOfDouble[0] > 0.0D ? 1 : 0))
    {
      d = paramArrayOfDouble[0];
      paramArrayOfDouble[0] = paramArrayOfDouble[2];
      paramArrayOfDouble[2] = d;
    }
    if ((paramRectangle2D.getHeight() > 0.0D ? 1 : 0) != (paramArrayOfDouble[3] - paramArrayOfDouble[1] > 0.0D ? 1 : 0))
    {
      d = paramArrayOfDouble[1];
      paramArrayOfDouble[1] = paramArrayOfDouble[3];
      paramArrayOfDouble[3] = d;
    }
  }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    clip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setClip(new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public Shape getClip()
  {
    return untransformShape(usrClip);
  }
  
  public void setClip(Shape paramShape)
  {
    usrClip = transformShape(paramShape);
    validateCompClip();
  }
  
  public void clip(Shape paramShape)
  {
    paramShape = transformShape(paramShape);
    if (usrClip != null) {
      paramShape = intersectShapes(usrClip, paramShape, true, true);
    }
    usrClip = paramShape;
    validateCompClip();
  }
  
  public void setPaintMode()
  {
    setComposite(AlphaComposite.SrcOver);
  }
  
  public void setXORMode(Color paramColor)
  {
    if (paramColor == null) {
      throw new IllegalArgumentException("null XORColor");
    }
    setComposite(new XORComposite(paramColor, surfaceData));
  }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        doCopyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  private void doCopyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
      return;
    }
    SurfaceData localSurfaceData = surfaceData;
    if (localSurfaceData.copyArea(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6)) {
      return;
    }
    if (transformState > 3) {
      throw new InternalError("transformed copyArea not implemented yet");
    }
    Region localRegion = getCompClip();
    Composite localComposite = composite;
    if (lastCAcomp != localComposite)
    {
      localObject1 = localSurfaceData.getSurfaceType();
      localObject2 = imageComp;
      if ((CompositeType.SrcOverNoEa.equals(localObject2)) && (localSurfaceData.getTransparency() == 1)) {
        localObject2 = CompositeType.SrcNoEa;
      }
      lastCAblit = Blit.locate((SurfaceType)localObject1, (CompositeType)localObject2, (SurfaceType)localObject1);
      lastCAcomp = localComposite;
    }
    Object localObject1 = { paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, paramInt1 + paramInt5, paramInt2 + paramInt6 };
    transform.transform((double[])localObject1, 0, (double[])localObject1, 0, 3);
    paramInt1 = (int)Math.ceil(localObject1[0] - 0.5D);
    paramInt2 = (int)Math.ceil(localObject1[1] - 0.5D);
    paramInt3 = (int)Math.ceil(localObject1[2] - 0.5D) - paramInt1;
    paramInt4 = (int)Math.ceil(localObject1[3] - 0.5D) - paramInt2;
    paramInt5 = (int)Math.ceil(localObject1[4] - 0.5D) - paramInt1;
    paramInt6 = (int)Math.ceil(localObject1[5] - 0.5D) - paramInt2;
    if (paramInt3 < 0)
    {
      paramInt3 *= -1;
      paramInt1 -= paramInt3;
    }
    if (paramInt4 < 0)
    {
      paramInt4 *= -1;
      paramInt2 -= paramInt4;
    }
    Object localObject2 = lastCAblit;
    int i;
    int j;
    if ((paramInt6 == 0) && (paramInt5 > 0) && (paramInt5 < paramInt3))
    {
      while (paramInt3 > 0)
      {
        i = Math.min(paramInt3, paramInt5);
        paramInt3 -= i;
        j = paramInt1 + paramInt3;
        ((Blit)localObject2).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, j, paramInt2, j + paramInt5, paramInt2 + paramInt6, i, paramInt4);
      }
      return;
    }
    if ((paramInt6 > 0) && (paramInt6 < paramInt4) && (paramInt5 > -paramInt3) && (paramInt5 < paramInt3))
    {
      while (paramInt4 > 0)
      {
        i = Math.min(paramInt4, paramInt6);
        paramInt4 -= i;
        j = paramInt2 + paramInt4;
        ((Blit)localObject2).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, paramInt1, j, paramInt1 + paramInt5, j + paramInt6, paramInt3, i);
      }
      return;
    }
    ((Blit)localObject2).Blit(localSurfaceData, localSurfaceData, localComposite, localRegion, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4);
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawLine(this, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        fillpipe.fillRoundRect(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        fillpipe.fillOval(this, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        fillpipe.fillArc(this, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    try
    {
      drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawPolyline(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    try
    {
      drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    try
    {
      fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        fillpipe.fillPolygon(this, paramArrayOfInt1, paramArrayOfInt2, paramInt);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        drawpipe.drawRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        fillpipe.fillRect(this, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  private void revalidateAll()
  {
    surfaceData = surfaceData.getReplacement();
    if (surfaceData == null) {
      surfaceData = NullSurfaceData.theInstance;
    }
    invalidatePipe();
    setDevClip(surfaceData.getBounds());
    if (paintState <= 1) {
      validateColor();
    }
    if ((composite instanceof XORComposite))
    {
      Color localColor = ((XORComposite)composite).getXorColor();
      setComposite(new XORComposite(localColor, surfaceData));
    }
    validatePipe();
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Composite localComposite = composite;
    Paint localPaint = paint;
    setComposite(AlphaComposite.Src);
    setColor(getBackground());
    fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    setPaint(localPaint);
    setComposite(localComposite);
  }
  
  public void draw(Shape paramShape)
  {
    try
    {
      shapepipe.draw(this, paramShape);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        shapepipe.draw(this, paramShape);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void fill(Shape paramShape)
  {
    try
    {
      shapepipe.fill(this, paramShape);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        shapepipe.fill(this, paramShape);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  private static boolean isIntegerTranslation(AffineTransform paramAffineTransform)
  {
    if (paramAffineTransform.isIdentity()) {
      return true;
    }
    if (paramAffineTransform.getType() == 1)
    {
      double d1 = paramAffineTransform.getTranslateX();
      double d2 = paramAffineTransform.getTranslateY();
      return (d1 == (int)d1) && (d2 == (int)d2);
    }
    return false;
  }
  
  private static int getTileIndex(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 -= paramInt2;
    if (paramInt1 < 0) {
      paramInt1 += 1 - paramInt3;
    }
    return paramInt1 / paramInt3;
  }
  
  private static Rectangle getImageRegion(RenderedImage paramRenderedImage, Region paramRegion, AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2)
  {
    Rectangle localRectangle1 = new Rectangle(paramRenderedImage.getMinX(), paramRenderedImage.getMinY(), paramRenderedImage.getWidth(), paramRenderedImage.getHeight());
    Rectangle localRectangle2 = null;
    try
    {
      double[] arrayOfDouble = new double[8];
      arrayOfDouble[0] = (arrayOfDouble[2] = paramRegion.getLoX());
      arrayOfDouble[4] = (arrayOfDouble[6] = paramRegion.getHiX());
      arrayOfDouble[1] = (arrayOfDouble[5] = paramRegion.getLoY());
      arrayOfDouble[3] = (arrayOfDouble[7] = paramRegion.getHiY());
      paramAffineTransform1.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
      paramAffineTransform2.inverseTransform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
      double d2;
      double d1 = d2 = arrayOfDouble[0];
      double d4;
      double d3 = d4 = arrayOfDouble[1];
      int i = 2;
      while (i < 8)
      {
        double d5 = arrayOfDouble[(i++)];
        if (d5 < d1) {
          d1 = d5;
        } else if (d5 > d2) {
          d2 = d5;
        }
        d5 = arrayOfDouble[(i++)];
        if (d5 < d3) {
          d3 = d5;
        } else if (d5 > d4) {
          d4 = d5;
        }
      }
      i = (int)d1 - paramInt1;
      int j = (int)(d2 - d1 + 2 * paramInt1);
      int k = (int)d3 - paramInt2;
      int m = (int)(d4 - d3 + 2 * paramInt2);
      Rectangle localRectangle3 = new Rectangle(i, k, j, m);
      localRectangle2 = localRectangle3.intersection(localRectangle1);
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      localRectangle2 = localRectangle1;
    }
    return localRectangle2;
  }
  
  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform)
  {
    if (paramRenderedImage == null) {
      return;
    }
    if ((paramRenderedImage instanceof BufferedImage))
    {
      BufferedImage localBufferedImage1 = (BufferedImage)paramRenderedImage;
      drawImage(localBufferedImage1, paramAffineTransform, null);
      return;
    }
    int i = (transformState <= 1) && (isIntegerTranslation(paramAffineTransform)) ? 1 : 0;
    int j = i != 0 ? 0 : 3;
    Region localRegion;
    try
    {
      localRegion = getCompClip();
    }
    catch (InvalidPipeException localInvalidPipeException)
    {
      return;
    }
    Rectangle localRectangle = getImageRegion(paramRenderedImage, localRegion, transform, paramAffineTransform, j, j);
    if ((width <= 0) || (height <= 0)) {
      return;
    }
    if (i != 0)
    {
      drawTranslatedRenderedImage(paramRenderedImage, localRectangle, (int)paramAffineTransform.getTranslateX(), (int)paramAffineTransform.getTranslateY());
      return;
    }
    Raster localRaster = paramRenderedImage.getData(localRectangle);
    WritableRaster localWritableRaster = Raster.createWritableRaster(localRaster.getSampleModel(), localRaster.getDataBuffer(), null);
    int k = localRaster.getMinX();
    int m = localRaster.getMinY();
    int n = localRaster.getWidth();
    int i1 = localRaster.getHeight();
    int i2 = k - localRaster.getSampleModelTranslateX();
    int i3 = m - localRaster.getSampleModelTranslateY();
    if ((i2 != 0) || (i3 != 0) || (n != localWritableRaster.getWidth()) || (i1 != localWritableRaster.getHeight())) {
      localWritableRaster = localWritableRaster.createWritableChild(i2, i3, n, i1, 0, 0, null);
    }
    AffineTransform localAffineTransform = (AffineTransform)paramAffineTransform.clone();
    localAffineTransform.translate(k, m);
    ColorModel localColorModel = paramRenderedImage.getColorModel();
    BufferedImage localBufferedImage2 = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
    drawImage(localBufferedImage2, localAffineTransform, null);
  }
  
  private boolean clipTo(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    int i = Math.max(x, x);
    int j = Math.min(x + width, x + width);
    int k = Math.max(y, y);
    int m = Math.min(y + height, y + height);
    if ((j - i < 0) || (m - k < 0))
    {
      width = -1;
      height = -1;
      return false;
    }
    x = i;
    y = k;
    width = (j - i);
    height = (m - k);
    return true;
  }
  
  private void drawTranslatedRenderedImage(RenderedImage paramRenderedImage, Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    int i = paramRenderedImage.getTileGridXOffset();
    int j = paramRenderedImage.getTileGridYOffset();
    int k = paramRenderedImage.getTileWidth();
    int m = paramRenderedImage.getTileHeight();
    int n = getTileIndex(x, i, k);
    int i1 = getTileIndex(y, j, m);
    int i2 = getTileIndex(x + width - 1, i, k);
    int i3 = getTileIndex(y + height - 1, j, m);
    ColorModel localColorModel = paramRenderedImage.getColorModel();
    Rectangle localRectangle = new Rectangle();
    for (int i4 = i1; i4 <= i3; i4++) {
      for (int i5 = n; i5 <= i2; i5++)
      {
        Raster localRaster = paramRenderedImage.getTile(i5, i4);
        x = (i5 * k + i);
        y = (i4 * m + j);
        width = k;
        height = m;
        clipTo(localRectangle, paramRectangle);
        WritableRaster localWritableRaster = null;
        if ((localRaster instanceof WritableRaster)) {
          localWritableRaster = (WritableRaster)localRaster;
        } else {
          localWritableRaster = Raster.createWritableRaster(localRaster.getSampleModel(), localRaster.getDataBuffer(), null);
        }
        localWritableRaster = localWritableRaster.createWritableChild(x, y, width, height, 0, 0, null);
        BufferedImage localBufferedImage = new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
        copyImage(localBufferedImage, x + paramInt1, y + paramInt2, 0, 0, width, height, null, null);
      }
    }
  }
  
  public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform)
  {
    if (paramRenderableImage == null) {
      return;
    }
    AffineTransform localAffineTransform1 = transform;
    AffineTransform localAffineTransform2 = new AffineTransform(paramAffineTransform);
    localAffineTransform2.concatenate(localAffineTransform1);
    RenderContext localRenderContext = new RenderContext(localAffineTransform2);
    AffineTransform localAffineTransform3;
    try
    {
      localAffineTransform3 = localAffineTransform1.createInverse();
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      localRenderContext = new RenderContext(localAffineTransform1);
      localAffineTransform3 = new AffineTransform();
    }
    RenderedImage localRenderedImage = paramRenderableImage.createRendering(localRenderContext);
    drawRenderedImage(localRenderedImage, localAffineTransform3);
  }
  
  protected Rectangle transformBounds(Rectangle paramRectangle, AffineTransform paramAffineTransform)
  {
    if (paramAffineTransform.isIdentity()) {
      return paramRectangle;
    }
    Shape localShape = transformShape(paramAffineTransform, paramRectangle);
    return localShape.getBounds();
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      throw new NullPointerException("String is null");
    }
    if (font.hasLayoutAttributes())
    {
      if (paramString.length() == 0) {
        return;
      }
      new TextLayout(paramString, font, getFontRenderContext()).draw(this, paramInt1, paramInt2);
      return;
    }
    try
    {
      textpipe.drawString(this, paramString, paramInt1, paramInt2);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        textpipe.drawString(this, paramString, paramInt1, paramInt2);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2)
  {
    if (paramString == null) {
      throw new NullPointerException("String is null");
    }
    if (font.hasLayoutAttributes())
    {
      if (paramString.length() == 0) {
        return;
      }
      new TextLayout(paramString, font, getFontRenderContext()).draw(this, paramFloat1, paramFloat2);
      return;
    }
    try
    {
      textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        textpipe.drawString(this, paramString, paramFloat1, paramFloat2);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new NullPointerException("AttributedCharacterIterator is null");
    }
    if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex()) {
      return;
    }
    TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    localTextLayout.draw(this, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new NullPointerException("AttributedCharacterIterator is null");
    }
    if (paramAttributedCharacterIterator.getBeginIndex() == paramAttributedCharacterIterator.getEndIndex()) {
      return;
    }
    TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    localTextLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    if (paramGlyphVector == null) {
      throw new NullPointerException("GlyphVector is null");
    }
    try
    {
      textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        textpipe.drawGlyphVector(this, paramGlyphVector, paramFloat1, paramFloat2);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramArrayOfChar == null) {
      throw new NullPointerException("char data is null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length)) {
      throw new ArrayIndexOutOfBoundsException("bad offset/length");
    }
    if (font.hasLayoutAttributes())
    {
      if (paramArrayOfChar.length == 0) {
        return;
      }
      new TextLayout(new String(paramArrayOfChar, paramInt1, paramInt2), font, getFontRenderContext()).draw(this, paramInt3, paramInt4);
      return;
    }
    try
    {
      textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        textpipe.drawChars(this, paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("byte data is null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length)) {
      throw new ArrayIndexOutOfBoundsException("bad offset/length");
    }
    char[] arrayOfChar = new char[paramInt2];
    int i = paramInt2;
    while (i-- > 0) {
      arrayOfChar[i] = ((char)(paramArrayOfByte[(i + paramInt1)] & 0xFF));
    }
    if (font.hasLayoutAttributes())
    {
      if (paramArrayOfByte.length == 0) {
        return;
      }
      new TextLayout(new String(arrayOfChar), font, getFontRenderContext()).draw(this, paramInt3, paramInt4);
      return;
    }
    try
    {
      textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        textpipe.drawChars(this, arrayOfChar, 0, paramInt2, paramInt3, paramInt4);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  private boolean isHiDPIImage(Image paramImage)
  {
    return (SurfaceManager.getImageScale(paramImage) != 1) || ((resolutionVariantHint != 1) && ((paramImage instanceof MultiResolutionImage)));
  }
  
  private boolean drawHiDPIImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    int i;
    if (SurfaceManager.getImageScale(paramImage) != 1)
    {
      i = SurfaceManager.getImageScale(paramImage);
      paramInt5 = Region.clipScale(paramInt5, i);
      paramInt7 = Region.clipScale(paramInt7, i);
      paramInt6 = Region.clipScale(paramInt6, i);
      paramInt8 = Region.clipScale(paramInt8, i);
    }
    else if ((paramImage instanceof MultiResolutionImage))
    {
      i = paramImage.getWidth(paramImageObserver);
      int j = paramImage.getHeight(paramImageObserver);
      Image localImage = getResolutionVariant((MultiResolutionImage)paramImage, i, j, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8);
      if ((localImage != paramImage) && (localImage != null))
      {
        ImageObserver localImageObserver = MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, i, j, -1, -1);
        int k = localImage.getWidth(localImageObserver);
        int m = localImage.getHeight(localImageObserver);
        if ((0 < i) && (0 < j) && (0 < k) && (0 < m))
        {
          float f1 = k / i;
          float f2 = m / j;
          paramInt5 = Region.clipScale(paramInt5, f1);
          paramInt6 = Region.clipScale(paramInt6, f2);
          paramInt7 = Region.clipScale(paramInt7, f1);
          paramInt8 = Region.clipScale(paramInt8, f2);
          paramImageObserver = localImageObserver;
          paramImage = localImage;
        }
      }
    }
    try
    {
      boolean bool1 = imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
      return bool1;
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        boolean bool2 = imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
        return bool2;
      }
      catch (InvalidPipeException localInvalidPipeException2)
      {
        boolean bool3 = false;
        return bool3;
      }
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  private Image getResolutionVariant(MultiResolutionImage paramMultiResolutionImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      return null;
    }
    int i = paramInt9 - paramInt7;
    int j = paramInt10 - paramInt8;
    if ((i == 0) || (j == 0)) {
      return null;
    }
    int k = transform.getType();
    int m = paramInt5 - paramInt3;
    int n = paramInt6 - paramInt4;
    double d1;
    double d2;
    if ((k & 0xFFFFFFBE) == 0)
    {
      d1 = m;
      d2 = n;
    }
    else if ((k & 0xFFFFFFB8) == 0)
    {
      d1 = m * transform.getScaleX();
      d2 = n * transform.getScaleY();
    }
    else
    {
      d1 = m * Math.hypot(transform.getScaleX(), transform.getShearY());
      d2 = n * Math.hypot(transform.getShearX(), transform.getScaleY());
    }
    int i1 = (int)Math.abs(paramInt1 * d1 / i);
    int i2 = (int)Math.abs(paramInt2 * d2 / j);
    Image localImage = paramMultiResolutionImage.getResolutionVariant(i1, i2);
    if (((localImage instanceof ToolkitImage)) && (((ToolkitImage)localImage).hasError())) {
      return null;
    }
    return localImage;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, null, paramImageObserver);
  }
  
  public boolean copyImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver)
  {
    try
    {
      boolean bool1 = imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
      return bool1;
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        boolean bool2 = imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor, paramImageObserver);
        return bool2;
      }
      catch (InvalidPipeException localInvalidPipeException2)
      {
        boolean bool3 = false;
        return bool3;
      }
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return true;
    }
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (isHiDPIImage(paramImage)) {
      return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4, 0, 0, i, j, paramColor, paramImageObserver);
    }
    if ((paramInt3 == i) && (paramInt4 == j)) {
      return copyImage(paramImage, paramInt1, paramInt2, 0, 0, paramInt3, paramInt4, paramColor, paramImageObserver);
    }
    try
    {
      boolean bool1 = imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
      return bool1;
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        boolean bool2 = imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
        return bool2;
      }
      catch (InvalidPipeException localInvalidPipeException2)
      {
        boolean bool3 = false;
        return bool3;
      }
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    return drawImage(paramImage, paramInt1, paramInt2, null, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    if (isHiDPIImage(paramImage))
    {
      int i = paramImage.getWidth(null);
      int j = paramImage.getHeight(null);
      return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt1 + i, paramInt2 + j, 0, 0, i, j, paramColor, paramImageObserver);
    }
    try
    {
      boolean bool1 = imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
      return bool1;
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        boolean bool2 = imagepipe.copyImage(this, paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
        return bool2;
      }
      catch (InvalidPipeException localInvalidPipeException2)
      {
        boolean bool3 = false;
        return bool3;
      }
    }
    finally
    {
      surfaceData.markDirty();
    }
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
    if ((paramInt1 == paramInt3) || (paramInt2 == paramInt4) || (paramInt5 == paramInt7) || (paramInt6 == paramInt8)) {
      return true;
    }
    if (isHiDPIImage(paramImage)) {
      return drawHiDPIImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
    }
    int k;
    if ((paramInt7 - paramInt5 == paramInt3 - paramInt1) && (paramInt8 - paramInt6 == paramInt4 - paramInt2))
    {
      int n;
      int i;
      if (paramInt7 > paramInt5)
      {
        n = paramInt7 - paramInt5;
        i = paramInt5;
        k = paramInt1;
      }
      else
      {
        n = paramInt5 - paramInt7;
        i = paramInt7;
        k = paramInt3;
      }
      int i1;
      int j;
      int m;
      if (paramInt8 > paramInt6)
      {
        i1 = paramInt8 - paramInt6;
        j = paramInt6;
        m = paramInt2;
      }
      else
      {
        i1 = paramInt6 - paramInt8;
        j = paramInt8;
        m = paramInt4;
      }
      return copyImage(paramImage, k, m, i, j, n, i1, paramColor, paramImageObserver);
    }
    try
    {
      boolean bool1 = imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
      return bool1;
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        boolean bool2 = imagepipe.scaleImage(this, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
        return bool2;
      }
      catch (InvalidPipeException localInvalidPipeException2)
      {
        k = 0;
        return k;
      }
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    if ((paramAffineTransform == null) || (paramAffineTransform.isIdentity())) {
      return drawImage(paramImage, 0, 0, null, paramImageObserver);
    }
    if (isHiDPIImage(paramImage))
    {
      int i = paramImage.getWidth(null);
      int j = paramImage.getHeight(null);
      AffineTransform localAffineTransform = new AffineTransform(transform);
      transform(paramAffineTransform);
      boolean bool4 = drawHiDPIImage(paramImage, 0, 0, i, j, 0, 0, i, j, null, paramImageObserver);
      transform.setTransform(localAffineTransform);
      invalidateTransform();
      return bool4;
    }
    try
    {
      boolean bool1 = imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
      return bool1;
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        boolean bool2 = imagepipe.transformImage(this, paramImage, paramAffineTransform, paramImageObserver);
        return bool2;
      }
      catch (InvalidPipeException localInvalidPipeException2)
      {
        boolean bool3 = false;
        return bool3;
      }
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    if (paramBufferedImage == null) {
      return;
    }
    try
    {
      imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
    }
    catch (InvalidPipeException localInvalidPipeException1)
    {
      try
      {
        revalidateAll();
        imagepipe.transformImage(this, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2);
      }
      catch (InvalidPipeException localInvalidPipeException2) {}
    }
    finally
    {
      surfaceData.markDirty();
    }
  }
  
  public FontRenderContext getFontRenderContext()
  {
    if (cachedFRC == null)
    {
      int i = textAntialiasHint;
      if ((i == 0) && (antialiasHint == 2)) {
        i = 2;
      }
      AffineTransform localAffineTransform = null;
      if (transformState >= 3) {
        if ((transform.getTranslateX() == 0.0D) && (transform.getTranslateY() == 0.0D)) {
          localAffineTransform = transform;
        } else {
          localAffineTransform = new AffineTransform(transform.getScaleX(), transform.getShearY(), transform.getShearX(), transform.getScaleY(), 0.0D, 0.0D);
        }
      }
      cachedFRC = new FontRenderContext(localAffineTransform, SunHints.Value.get(2, i), SunHints.Value.get(3, fractionalMetricsHint));
    }
    return cachedFRC;
  }
  
  public void dispose()
  {
    surfaceData = NullSurfaceData.theInstance;
    invalidatePipe();
  }
  
  public void finalize() {}
  
  public Object getDestination()
  {
    return surfaceData.getDestination();
  }
  
  public Surface getDestSurface()
  {
    return surfaceData;
  }
  
  static
  {
    if (PerformanceLogger.loggingEnabled()) {
      PerformanceLogger.setTime("SunGraphics2D static initialization");
    }
    invalidpipe = new ValidatePipe();
    IDENT_MATRIX = new double[] { 1.0D, 0.0D, 0.0D, 1.0D };
    IDENT_ATX = new AffineTransform();
    textTxArr = new double[17][];
    textAtArr = new AffineTransform[17];
    for (int i = 8; i < 17; i++)
    {
      textTxArr[i] = { i, 0.0D, 0.0D, i };
      textAtArr[i] = new AffineTransform(textTxArr[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\SunGraphics2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */