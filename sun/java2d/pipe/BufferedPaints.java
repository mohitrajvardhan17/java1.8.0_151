package sun.java2d.pipe;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import sun.awt.image.PixelConverter;
import sun.awt.image.PixelConverter.ArgbPre;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

public class BufferedPaints
{
  public static final int MULTI_MAX_FRACTIONS = 12;
  
  public BufferedPaints() {}
  
  static void setPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, Paint paramPaint, int paramInt)
  {
    if (paintState <= 1)
    {
      setColor(paramRenderQueue, pixel);
    }
    else
    {
      boolean bool = (paramInt & 0x2) != 0;
      switch (paintState)
      {
      case 2: 
        setGradientPaint(paramRenderQueue, paramSunGraphics2D, (GradientPaint)paramPaint, bool);
        break;
      case 3: 
        setLinearGradientPaint(paramRenderQueue, paramSunGraphics2D, (LinearGradientPaint)paramPaint, bool);
        break;
      case 4: 
        setRadialGradientPaint(paramRenderQueue, paramSunGraphics2D, (RadialGradientPaint)paramPaint, bool);
        break;
      case 5: 
        setTexturePaint(paramRenderQueue, paramSunGraphics2D, (TexturePaint)paramPaint, bool);
        break;
      }
    }
  }
  
  static void resetPaint(RenderQueue paramRenderQueue)
  {
    paramRenderQueue.ensureCapacity(4);
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    localRenderBuffer.putInt(100);
  }
  
  private static void setColor(RenderQueue paramRenderQueue, int paramInt)
  {
    paramRenderQueue.ensureCapacity(8);
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    localRenderBuffer.putInt(101);
    localRenderBuffer.putInt(paramInt);
  }
  
  private static void setGradientPaint(RenderQueue paramRenderQueue, AffineTransform paramAffineTransform, Color paramColor1, Color paramColor2, Point2D paramPoint2D1, Point2D paramPoint2D2, boolean paramBoolean1, boolean paramBoolean2)
  {
    PixelConverter localPixelConverter = PixelConverter.ArgbPre.instance;
    int i = localPixelConverter.rgbToPixel(paramColor1.getRGB(), null);
    int j = localPixelConverter.rgbToPixel(paramColor2.getRGB(), null);
    double d1 = paramPoint2D1.getX();
    double d2 = paramPoint2D1.getY();
    paramAffineTransform.translate(d1, d2);
    d1 = paramPoint2D2.getX() - d1;
    d2 = paramPoint2D2.getY() - d2;
    double d3 = Math.sqrt(d1 * d1 + d2 * d2);
    paramAffineTransform.rotate(d1, d2);
    paramAffineTransform.scale(2.0D * d3, 1.0D);
    paramAffineTransform.translate(-0.25D, 0.0D);
    double d4;
    double d5;
    double d6;
    try
    {
      paramAffineTransform.invert();
      d4 = paramAffineTransform.getScaleX();
      d5 = paramAffineTransform.getShearX();
      d6 = paramAffineTransform.getTranslateX();
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      d4 = d5 = d6 = 0.0D;
    }
    paramRenderQueue.ensureCapacityAndAlignment(44, 12);
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    localRenderBuffer.putInt(102);
    localRenderBuffer.putInt(paramBoolean2 ? 1 : 0);
    localRenderBuffer.putInt(paramBoolean1 ? 1 : 0);
    localRenderBuffer.putDouble(d4).putDouble(d5).putDouble(d6);
    localRenderBuffer.putInt(i).putInt(j);
  }
  
  private static void setGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, GradientPaint paramGradientPaint, boolean paramBoolean)
  {
    setGradientPaint(paramRenderQueue, (AffineTransform)transform.clone(), paramGradientPaint.getColor1(), paramGradientPaint.getColor2(), paramGradientPaint.getPoint1(), paramGradientPaint.getPoint2(), paramGradientPaint.isCyclic(), paramBoolean);
  }
  
  private static void setTexturePaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, TexturePaint paramTexturePaint, boolean paramBoolean)
  {
    BufferedImage localBufferedImage = paramTexturePaint.getImage();
    SurfaceData localSurfaceData1 = surfaceData;
    SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
    int i = interpolationType != 1 ? 1 : 0;
    AffineTransform localAffineTransform = (AffineTransform)transform.clone();
    Rectangle2D localRectangle2D = paramTexturePaint.getAnchorRect();
    localAffineTransform.translate(localRectangle2D.getX(), localRectangle2D.getY());
    localAffineTransform.scale(localRectangle2D.getWidth(), localRectangle2D.getHeight());
    double d1;
    double d2;
    double d3;
    double d4;
    double d5;
    double d6;
    try
    {
      localAffineTransform.invert();
      d1 = localAffineTransform.getScaleX();
      d2 = localAffineTransform.getShearX();
      d3 = localAffineTransform.getTranslateX();
      d4 = localAffineTransform.getShearY();
      d5 = localAffineTransform.getScaleY();
      d6 = localAffineTransform.getTranslateY();
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      d1 = d2 = d3 = d4 = d5 = d6 = 0.0D;
    }
    paramRenderQueue.ensureCapacityAndAlignment(68, 12);
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    localRenderBuffer.putInt(105);
    localRenderBuffer.putInt(paramBoolean ? 1 : 0);
    localRenderBuffer.putInt(i != 0 ? 1 : 0);
    localRenderBuffer.putLong(localSurfaceData2.getNativeOps());
    localRenderBuffer.putDouble(d1).putDouble(d2).putDouble(d3);
    localRenderBuffer.putDouble(d4).putDouble(d5).putDouble(d6);
  }
  
  public static int convertSRGBtoLinearRGB(int paramInt)
  {
    float f1 = paramInt / 255.0F;
    float f2;
    if (f1 <= 0.04045F) {
      f2 = f1 / 12.92F;
    } else {
      f2 = (float)Math.pow((f1 + 0.055D) / 1.055D, 2.4D);
    }
    return Math.round(f2 * 255.0F);
  }
  
  private static int colorToIntArgbPrePixel(Color paramColor, boolean paramBoolean)
  {
    int i = paramColor.getRGB();
    if ((!paramBoolean) && (i >> 24 == -1)) {
      return i;
    }
    int j = i >>> 24;
    int k = i >> 16 & 0xFF;
    int m = i >> 8 & 0xFF;
    int n = i & 0xFF;
    if (paramBoolean)
    {
      k = convertSRGBtoLinearRGB(k);
      m = convertSRGBtoLinearRGB(m);
      n = convertSRGBtoLinearRGB(n);
    }
    int i1 = j + (j >> 7);
    k = k * i1 >> 8;
    m = m * i1 >> 8;
    n = n * i1 >> 8;
    return j << 24 | k << 16 | m << 8 | n;
  }
  
  private static int[] convertToIntArgbPrePixels(Color[] paramArrayOfColor, boolean paramBoolean)
  {
    int[] arrayOfInt = new int[paramArrayOfColor.length];
    for (int i = 0; i < paramArrayOfColor.length; i++) {
      arrayOfInt[i] = colorToIntArgbPrePixel(paramArrayOfColor[i], paramBoolean);
    }
    return arrayOfInt;
  }
  
  private static void setLinearGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, LinearGradientPaint paramLinearGradientPaint, boolean paramBoolean)
  {
    boolean bool1 = paramLinearGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
    Color[] arrayOfColor = paramLinearGradientPaint.getColors();
    int i = arrayOfColor.length;
    Point2D localPoint2D1 = paramLinearGradientPaint.getStartPoint();
    Point2D localPoint2D2 = paramLinearGradientPaint.getEndPoint();
    AffineTransform localAffineTransform = paramLinearGradientPaint.getTransform();
    localAffineTransform.preConcatenate(transform);
    if ((!bool1) && (i == 2) && (paramLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT))
    {
      boolean bool2 = paramLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.NO_CYCLE;
      setGradientPaint(paramRenderQueue, localAffineTransform, arrayOfColor[0], arrayOfColor[1], localPoint2D1, localPoint2D2, bool2, paramBoolean);
      return;
    }
    int j = paramLinearGradientPaint.getCycleMethod().ordinal();
    float[] arrayOfFloat = paramLinearGradientPaint.getFractions();
    int[] arrayOfInt = convertToIntArgbPrePixels(arrayOfColor, bool1);
    double d1 = localPoint2D1.getX();
    double d2 = localPoint2D1.getY();
    localAffineTransform.translate(d1, d2);
    d1 = localPoint2D2.getX() - d1;
    d2 = localPoint2D2.getY() - d2;
    double d3 = Math.sqrt(d1 * d1 + d2 * d2);
    localAffineTransform.rotate(d1, d2);
    localAffineTransform.scale(d3, 1.0D);
    float f1;
    float f2;
    float f3;
    try
    {
      localAffineTransform.invert();
      f1 = (float)localAffineTransform.getScaleX();
      f2 = (float)localAffineTransform.getShearX();
      f3 = (float)localAffineTransform.getTranslateX();
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      f1 = f2 = f3 = 0.0F;
    }
    paramRenderQueue.ensureCapacity(32 + i * 4 * 2);
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    localRenderBuffer.putInt(103);
    localRenderBuffer.putInt(paramBoolean ? 1 : 0);
    localRenderBuffer.putInt(bool1 ? 1 : 0);
    localRenderBuffer.putInt(j);
    localRenderBuffer.putInt(i);
    localRenderBuffer.putFloat(f1);
    localRenderBuffer.putFloat(f2);
    localRenderBuffer.putFloat(f3);
    localRenderBuffer.put(arrayOfFloat);
    localRenderBuffer.put(arrayOfInt);
  }
  
  private static void setRadialGradientPaint(RenderQueue paramRenderQueue, SunGraphics2D paramSunGraphics2D, RadialGradientPaint paramRadialGradientPaint, boolean paramBoolean)
  {
    boolean bool = paramRadialGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
    int i = paramRadialGradientPaint.getCycleMethod().ordinal();
    float[] arrayOfFloat = paramRadialGradientPaint.getFractions();
    Color[] arrayOfColor = paramRadialGradientPaint.getColors();
    int j = arrayOfColor.length;
    int[] arrayOfInt = convertToIntArgbPrePixels(arrayOfColor, bool);
    Point2D localPoint2D1 = paramRadialGradientPaint.getCenterPoint();
    Point2D localPoint2D2 = paramRadialGradientPaint.getFocusPoint();
    float f = paramRadialGradientPaint.getRadius();
    double d1 = localPoint2D1.getX();
    double d2 = localPoint2D1.getY();
    double d3 = localPoint2D2.getX();
    double d4 = localPoint2D2.getY();
    AffineTransform localAffineTransform = paramRadialGradientPaint.getTransform();
    localAffineTransform.preConcatenate(transform);
    localPoint2D2 = localAffineTransform.transform(localPoint2D2, localPoint2D2);
    localAffineTransform.translate(d1, d2);
    localAffineTransform.rotate(d3 - d1, d4 - d2);
    localAffineTransform.scale(f, f);
    try
    {
      localAffineTransform.invert();
    }
    catch (Exception localException)
    {
      localAffineTransform.setToScale(0.0D, 0.0D);
    }
    localPoint2D2 = localAffineTransform.transform(localPoint2D2, localPoint2D2);
    d3 = Math.min(localPoint2D2.getX(), 0.99D);
    paramRenderQueue.ensureCapacity(48 + j * 4 * 2);
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    localRenderBuffer.putInt(104);
    localRenderBuffer.putInt(paramBoolean ? 1 : 0);
    localRenderBuffer.putInt(bool ? 1 : 0);
    localRenderBuffer.putInt(j);
    localRenderBuffer.putInt(i);
    localRenderBuffer.putFloat((float)localAffineTransform.getScaleX());
    localRenderBuffer.putFloat((float)localAffineTransform.getShearX());
    localRenderBuffer.putFloat((float)localAffineTransform.getTranslateX());
    localRenderBuffer.putFloat((float)localAffineTransform.getShearY());
    localRenderBuffer.putFloat((float)localAffineTransform.getScaleY());
    localRenderBuffer.putFloat((float)localAffineTransform.getTranslateY());
    localRenderBuffer.putFloat((float)d3);
    localRenderBuffer.put(arrayOfFloat);
    localRenderBuffer.put(arrayOfInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */