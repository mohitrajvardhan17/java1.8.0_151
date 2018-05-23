package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.VolatileImage;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformHelper;

public class DrawImage
  implements DrawImagePipe
{
  private static final double MAX_TX_ERROR = 1.0E-4D;
  
  public DrawImage() {}
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor)
  {
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (isSimpleTranslate(paramSunGraphics2D)) {
      return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + transX, paramInt2 + transY, 0, 0, i, j);
    }
    AffineTransform localAffineTransform = transform;
    if ((paramInt1 | paramInt2) != 0)
    {
      localAffineTransform = new AffineTransform(localAffineTransform);
      localAffineTransform.translate(paramInt1, paramInt2);
    }
    transformImage(paramSunGraphics2D, paramImage, localAffineTransform, interpolationType, 0, 0, i, j, paramColor);
    return true;
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor)
  {
    if (isSimpleTranslate(paramSunGraphics2D)) {
      return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor);
    return true;
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
  {
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if ((paramInt3 > 0) && (paramInt4 > 0) && (isSimpleTranslate(paramSunGraphics2D)))
    {
      double d1 = paramInt1 + transX;
      double d2 = paramInt2 + transY;
      double d3 = d1 + paramInt3;
      double d4 = d2 + paramInt4;
      if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, interpolationType, 0, 0, i, j, d1, d2, d3, d4)) {
        return true;
      }
    }
    AffineTransform localAffineTransform = transform;
    if (((paramInt1 | paramInt2) != 0) || (paramInt3 != i) || (paramInt4 != j))
    {
      localAffineTransform = new AffineTransform(localAffineTransform);
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(paramInt3 / i, paramInt4 / j);
    }
    transformImage(paramSunGraphics2D, paramImage, localAffineTransform, interpolationType, 0, 0, i, j, paramColor);
    return true;
  }
  
  protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, AffineTransform paramAffineTransform, int paramInt3)
  {
    int i = paramAffineTransform.getType();
    int j = paramImage.getWidth(null);
    int k = paramImage.getHeight(null);
    int m;
    if ((transformState <= 2) && ((i == 0) || (i == 1)))
    {
      double d1 = paramAffineTransform.getTranslateX();
      double d2 = paramAffineTransform.getTranslateY();
      d1 += transform.getTranslateX();
      d2 += transform.getTranslateY();
      int n = (int)Math.floor(d1 + 0.5D);
      int i1 = (int)Math.floor(d2 + 0.5D);
      if ((paramInt3 == 1) || ((closeToInteger(n, d1)) && (closeToInteger(i1, d2))))
      {
        renderImageCopy(paramSunGraphics2D, paramImage, null, paramInt1 + n, paramInt2 + i1, 0, 0, j, k);
        return;
      }
      m = 0;
    }
    else if ((transformState <= 3) && ((i & 0x78) == 0))
    {
      localObject = new double[] { 0.0D, 0.0D, j, k };
      paramAffineTransform.transform((double[])localObject, 0, (double[])localObject, 0, 2);
      localObject[0] += paramInt1;
      localObject[1] += paramInt2;
      localObject[2] += paramInt1;
      localObject[3] += paramInt2;
      transform.transform((double[])localObject, 0, (double[])localObject, 0, 2);
      if (tryCopyOrScale(paramSunGraphics2D, paramImage, 0, 0, j, k, null, paramInt3, (double[])localObject)) {
        return;
      }
      m = 0;
    }
    else
    {
      m = 1;
    }
    Object localObject = new AffineTransform(transform);
    ((AffineTransform)localObject).translate(paramInt1, paramInt2);
    ((AffineTransform)localObject).concatenate(paramAffineTransform);
    if (m != 0) {
      transformImage(paramSunGraphics2D, paramImage, (AffineTransform)localObject, paramInt3, 0, 0, j, k, null);
    } else {
      renderImageXform(paramSunGraphics2D, paramImage, (AffineTransform)localObject, paramInt3, 0, 0, j, k, null);
    }
  }
  
  protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
  {
    double[] arrayOfDouble = new double[6];
    arrayOfDouble[2] = (paramInt4 - paramInt2);
    arrayOfDouble[3] = (arrayOfDouble[5] = paramInt5 - paramInt3);
    paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 3);
    if ((Math.abs(arrayOfDouble[0] - arrayOfDouble[4]) < 1.0E-4D) && (Math.abs(arrayOfDouble[3] - arrayOfDouble[5]) < 1.0E-4D) && (tryCopyOrScale(paramSunGraphics2D, paramImage, paramInt2, paramInt3, paramInt4, paramInt5, paramColor, paramInt1, arrayOfDouble))) {
      return;
    }
    renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
  }
  
  protected boolean tryCopyOrScale(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, int paramInt5, double[] paramArrayOfDouble)
  {
    double d1 = paramArrayOfDouble[0];
    double d2 = paramArrayOfDouble[1];
    double d3 = paramArrayOfDouble[2];
    double d4 = paramArrayOfDouble[3];
    double d5 = d3 - d1;
    double d6 = d4 - d2;
    if ((d1 < -2.147483648E9D) || (d1 > 2.147483647E9D) || (d2 < -2.147483648E9D) || (d2 > 2.147483647E9D) || (d3 < -2.147483648E9D) || (d3 > 2.147483647E9D) || (d4 < -2.147483648E9D) || (d4 > 2.147483647E9D)) {
      return false;
    }
    if ((closeToInteger(paramInt3 - paramInt1, d5)) && (closeToInteger(paramInt4 - paramInt2, d6)))
    {
      int i = (int)Math.floor(d1 + 0.5D);
      int j = (int)Math.floor(d2 + 0.5D);
      if ((paramInt5 == 1) || ((closeToInteger(i, d1)) && (closeToInteger(j, d2))))
      {
        renderImageCopy(paramSunGraphics2D, paramImage, paramColor, i, j, paramInt1, paramInt2, paramInt3 - paramInt1, paramInt4 - paramInt2);
        return true;
      }
    }
    return (d5 > 0.0D) && (d6 > 0.0D) && (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramInt5, paramInt1, paramInt2, paramInt3, paramInt4, d1, d2, d3, d4));
  }
  
  BufferedImage makeBufferedImage(Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = paramInt4 - paramInt2;
    int j = paramInt5 - paramInt3;
    BufferedImage localBufferedImage = new BufferedImage(i, j, paramInt1);
    SunGraphics2D localSunGraphics2D = (SunGraphics2D)localBufferedImage.createGraphics();
    localSunGraphics2D.setComposite(AlphaComposite.Src);
    localBufferedImage.setAccelerationPriority(0.0F);
    if (paramColor != null)
    {
      localSunGraphics2D.setColor(paramColor);
      localSunGraphics2D.fillRect(0, 0, i, j);
      localSunGraphics2D.setComposite(AlphaComposite.SrcOver);
    }
    localSunGraphics2D.copyImage(paramImage, 0, 0, paramInt2, paramInt3, i, j, null, null);
    localSunGraphics2D.dispose();
    return localBufferedImage;
  }
  
  protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
  {
    AffineTransform localAffineTransform;
    try
    {
      localAffineTransform = paramAffineTransform.createInverse();
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      return;
    }
    double[] arrayOfDouble = new double[8];
    arrayOfDouble[2] = (arrayOfDouble[6] = paramInt4 - paramInt2);
    arrayOfDouble[5] = (arrayOfDouble[7] = paramInt5 - paramInt3);
    paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
    double d3;
    double d1 = d3 = arrayOfDouble[0];
    double d4;
    double d2 = d4 = arrayOfDouble[1];
    for (int i = 2; i < arrayOfDouble.length; i += 2)
    {
      double d5 = arrayOfDouble[i];
      if (d1 > d5) {
        d1 = d5;
      } else if (d3 < d5) {
        d3 = d5;
      }
      d5 = arrayOfDouble[(i + 1)];
      if (d2 > d5) {
        d2 = d5;
      } else if (d4 < d5) {
        d4 = d5;
      }
    }
    Region localRegion1 = paramSunGraphics2D.getCompClip();
    int j = Math.max((int)Math.floor(d1), lox);
    int k = Math.max((int)Math.floor(d2), loy);
    int m = Math.min((int)Math.ceil(d3), hix);
    int n = Math.min((int)Math.ceil(d4), hiy);
    if ((m <= j) || (n <= k)) {
      return;
    }
    SurfaceData localSurfaceData1 = surfaceData;
    SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, imageComp, paramColor);
    if (localSurfaceData2 == null)
    {
      paramImage = getBufferedImage(paramImage);
      localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, imageComp, paramColor);
      if (localSurfaceData2 == null) {
        return;
      }
    }
    if (isBgOperation(localSurfaceData2, paramColor))
    {
      paramImage = makeBufferedImage(paramImage, paramColor, 1, paramInt2, paramInt3, paramInt4, paramInt5);
      paramInt4 -= paramInt2;
      paramInt5 -= paramInt3;
      paramInt2 = paramInt3 = 0;
      localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, imageComp, paramColor);
    }
    SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
    TransformHelper localTransformHelper = TransformHelper.getFromCache(localSurfaceType1);
    if (localTransformHelper == null)
    {
      int i1 = localSurfaceData2.getTransparency() == 1 ? 1 : 2;
      paramImage = makeBufferedImage(paramImage, null, i1, paramInt2, paramInt3, paramInt4, paramInt5);
      paramInt4 -= paramInt2;
      paramInt5 -= paramInt3;
      paramInt2 = paramInt3 = 0;
      localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, imageComp, null);
      localSurfaceType1 = localSurfaceData2.getSurfaceType();
      localTransformHelper = TransformHelper.getFromCache(localSurfaceType1);
    }
    SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
    if (compositeState <= 1)
    {
      MaskBlit localMaskBlit1 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, imageComp, localSurfaceType2);
      if (localMaskBlit1.getNativePrim() != 0L)
      {
        localTransformHelper.Transform(localMaskBlit1, localSurfaceData2, localSurfaceData1, composite, localRegion1, localAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, j, k, m, n, null, 0, 0);
        return;
      }
    }
    int i2 = m - j;
    int i3 = n - k;
    BufferedImage localBufferedImage = new BufferedImage(i2, i3, 3);
    SurfaceData localSurfaceData3 = SurfaceData.getPrimarySurfaceData(localBufferedImage);
    SurfaceType localSurfaceType3 = localSurfaceData3.getSurfaceType();
    MaskBlit localMaskBlit2 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, CompositeType.SrcNoEa, localSurfaceType3);
    int[] arrayOfInt = new int[i3 * 2 + 2];
    localTransformHelper.Transform(localMaskBlit2, localSurfaceData2, localSurfaceData3, AlphaComposite.Src, null, localAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, i2, i3, arrayOfInt, j, k);
    Region localRegion2 = Region.getInstance(j, k, m, n, arrayOfInt);
    localRegion1 = localRegion1.getIntersection(localRegion2);
    Blit localBlit = Blit.getFromCache(localSurfaceType3, imageComp, localSurfaceType2);
    localBlit.Blit(localSurfaceData3, localSurfaceData1, composite, localRegion1, 0, 0, j, k, i2, i3);
  }
  
  protected boolean renderImageCopy(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Region localRegion = paramSunGraphics2D.getCompClip();
    SurfaceData localSurfaceData1 = surfaceData;
    int i = 0;
    for (;;)
    {
      SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 0, imageComp, paramColor);
      if (localSurfaceData2 == null) {
        return false;
      }
      try
      {
        SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
        SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
        blitSurfaceData(paramSunGraphics2D, localRegion, localSurfaceData2, localSurfaceData1, localSurfaceType1, localSurfaceType2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt5, paramInt6, paramColor);
        return true;
      }
      catch (NullPointerException localNullPointerException)
      {
        if ((!SurfaceData.isNull(localSurfaceData1)) && (!SurfaceData.isNull(localSurfaceData2))) {
          throw localNullPointerException;
        }
        return false;
      }
      catch (InvalidPipeException localInvalidPipeException)
      {
        i++;
        localRegion = paramSunGraphics2D.getCompClip();
        localSurfaceData1 = surfaceData;
        if ((SurfaceData.isNull(localSurfaceData1)) || (SurfaceData.isNull(localSurfaceData2)) || (i > 1)) {
          return false;
        }
      }
    }
  }
  
  protected boolean renderImageScale(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (paramInt1 != 1) {
      return false;
    }
    Region localRegion = paramSunGraphics2D.getCompClip();
    SurfaceData localSurfaceData1 = surfaceData;
    int i = 0;
    for (;;)
    {
      SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 3, imageComp, paramColor);
      if ((localSurfaceData2 == null) || (isBgOperation(localSurfaceData2, paramColor))) {
        return false;
      }
      try
      {
        SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
        SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
        return scaleSurfaceData(paramSunGraphics2D, localRegion, localSurfaceData2, localSurfaceData1, localSurfaceType1, localSurfaceType2, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
      }
      catch (NullPointerException localNullPointerException)
      {
        if (!SurfaceData.isNull(localSurfaceData1)) {
          throw localNullPointerException;
        }
        return false;
      }
      catch (InvalidPipeException localInvalidPipeException)
      {
        i++;
        localRegion = paramSunGraphics2D.getCompClip();
        localSurfaceData1 = surfaceData;
        if ((SurfaceData.isNull(localSurfaceData1)) || (SurfaceData.isNull(localSurfaceData2)) || (i > 1)) {
          return false;
        }
      }
    }
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor)
  {
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int i7 = 0;
    int i;
    int n;
    if (paramInt7 > paramInt5)
    {
      i = paramInt7 - paramInt5;
      n = paramInt5;
    }
    else
    {
      i4 = 1;
      i = paramInt5 - paramInt7;
      n = paramInt7;
    }
    int j;
    int i1;
    if (paramInt8 > paramInt6)
    {
      j = paramInt8 - paramInt6;
      i1 = paramInt6;
    }
    else
    {
      i5 = 1;
      j = paramInt6 - paramInt8;
      i1 = paramInt8;
    }
    int k;
    int i2;
    if (paramInt3 > paramInt1)
    {
      k = paramInt3 - paramInt1;
      i2 = paramInt1;
    }
    else
    {
      k = paramInt1 - paramInt3;
      i6 = 1;
      i2 = paramInt3;
    }
    int m;
    int i3;
    if (paramInt4 > paramInt2)
    {
      m = paramInt4 - paramInt2;
      i3 = paramInt2;
    }
    else
    {
      m = paramInt2 - paramInt4;
      i7 = 1;
      i3 = paramInt4;
    }
    if ((i <= 0) || (j <= 0)) {
      return true;
    }
    if ((i4 == i6) && (i5 == i7) && (isSimpleTranslate(paramSunGraphics2D)))
    {
      double d1 = i2 + transX;
      double d3 = i3 + transY;
      double d5 = d1 + k;
      double d6 = d3 + m;
      if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, interpolationType, n, i1, n + i, i1 + j, d1, d3, d5, d6)) {
        return true;
      }
    }
    AffineTransform localAffineTransform = new AffineTransform(transform);
    localAffineTransform.translate(paramInt1, paramInt2);
    double d2 = (paramInt3 - paramInt1) / (paramInt7 - paramInt5);
    double d4 = (paramInt4 - paramInt2) / (paramInt8 - paramInt6);
    localAffineTransform.scale(d2, d4);
    localAffineTransform.translate(n - paramInt5, i1 - paramInt6);
    int i8 = SurfaceManager.getImageScale(paramImage);
    int i9 = paramImage.getWidth(null) * i8;
    int i10 = paramImage.getHeight(null) * i8;
    i += n;
    j += i1;
    if (i > i9) {
      i = i9;
    }
    if (j > i10) {
      j = i10;
    }
    if (n < 0)
    {
      localAffineTransform.translate(-n, 0.0D);
      n = 0;
    }
    if (i1 < 0)
    {
      localAffineTransform.translate(0.0D, -i1);
      i1 = 0;
    }
    if ((n >= i) || (i1 >= j)) {
      return true;
    }
    transformImage(paramSunGraphics2D, paramImage, localAffineTransform, interpolationType, n, i1, i, j, paramColor);
    return true;
  }
  
  public static boolean closeToInteger(int paramInt, double paramDouble)
  {
    return Math.abs(paramDouble - paramInt) < 1.0E-4D;
  }
  
  public static boolean isSimpleTranslate(SunGraphics2D paramSunGraphics2D)
  {
    int i = transformState;
    if (i <= 1) {
      return true;
    }
    if (i >= 3) {
      return false;
    }
    return interpolationType == 1;
  }
  
  protected static boolean isBgOperation(SurfaceData paramSurfaceData, Color paramColor)
  {
    return (paramSurfaceData == null) || ((paramColor != null) && (paramSurfaceData.getTransparency() != 1));
  }
  
  protected BufferedImage getBufferedImage(Image paramImage)
  {
    if ((paramImage instanceof BufferedImage)) {
      return (BufferedImage)paramImage;
    }
    return ((VolatileImage)paramImage).getSnapshot();
  }
  
  private ColorModel getTransformColorModel(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, AffineTransform paramAffineTransform)
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    Object localObject1 = localColorModel;
    if (paramAffineTransform.isIdentity()) {
      return (ColorModel)localObject1;
    }
    int i = paramAffineTransform.getType();
    int j = (i & 0x38) != 0 ? 1 : 0;
    Object localObject2;
    if ((j == 0) && (i != 1) && (i != 0))
    {
      localObject2 = new double[4];
      paramAffineTransform.getMatrix((double[])localObject2);
      j = (localObject2[0] != (int)localObject2[0]) || (localObject2[3] != (int)localObject2[3]) ? 1 : 0;
    }
    if (renderHint != 2)
    {
      if ((localColorModel instanceof IndexColorModel))
      {
        localObject2 = paramBufferedImage.getRaster();
        IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
        if ((j != 0) && (localColorModel.getTransparency() == 1)) {
          if ((localObject2 instanceof BytePackedRaster))
          {
            localObject1 = ColorModel.getRGBdefault();
          }
          else
          {
            double[] arrayOfDouble = new double[6];
            paramAffineTransform.getMatrix(arrayOfDouble);
            if ((arrayOfDouble[1] != 0.0D) || (arrayOfDouble[2] != 0.0D) || (arrayOfDouble[4] != 0.0D) || (arrayOfDouble[5] != 0.0D))
            {
              int k = localIndexColorModel.getMapSize();
              if (k < 256)
              {
                int[] arrayOfInt = new int[k + 1];
                localIndexColorModel.getRGBs(arrayOfInt);
                arrayOfInt[k] = 0;
                localObject1 = new IndexColorModel(localIndexColorModel.getPixelSize(), k + 1, arrayOfInt, 0, true, k, 0);
              }
              else
              {
                localObject1 = ColorModel.getRGBdefault();
              }
            }
          }
        }
      }
      else if ((j != 0) && (localColorModel.getTransparency() == 1))
      {
        localObject1 = ColorModel.getRGBdefault();
      }
    }
    else if (((localColorModel instanceof IndexColorModel)) || ((j != 0) && (localColorModel.getTransparency() == 1))) {
      localObject1 = ColorModel.getRGBdefault();
    }
    return (ColorModel)localObject1;
  }
  
  protected void blitSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor)
  {
    if ((paramInt5 <= 0) || (paramInt6 <= 0)) {
      return;
    }
    CompositeType localCompositeType = imageComp;
    if ((CompositeType.SrcOverNoEa.equals(localCompositeType)) && ((paramSurfaceData1.getTransparency() == 1) || ((paramColor != null) && (paramColor.getTransparency() == 1)))) {
      localCompositeType = CompositeType.SrcNoEa;
    }
    Object localObject;
    if (!isBgOperation(paramSurfaceData1, paramColor))
    {
      localObject = Blit.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
      ((Blit)localObject).Blit(paramSurfaceData1, paramSurfaceData2, composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    else
    {
      localObject = BlitBg.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
      ((BlitBg)localObject).BlitBg(paramSurfaceData1, paramSurfaceData2, composite, paramRegion, paramColor.getRGB(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
  }
  
  protected boolean scaleSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    CompositeType localCompositeType = imageComp;
    if ((CompositeType.SrcOverNoEa.equals(localCompositeType)) && (paramSurfaceData1.getTransparency() == 1)) {
      localCompositeType = CompositeType.SrcNoEa;
    }
    ScaledBlit localScaledBlit = ScaledBlit.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
    if (localScaledBlit != null)
    {
      localScaledBlit.Scale(paramSurfaceData1, paramSurfaceData2, composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
      return true;
    }
    return false;
  }
  
  protected static boolean imageReady(ToolkitImage paramToolkitImage, ImageObserver paramImageObserver)
  {
    if (paramToolkitImage.hasError())
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(paramToolkitImage, 192, -1, -1, -1, -1);
      }
      return false;
    }
    return true;
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    if (!(paramImage instanceof ToolkitImage)) {
      return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramColor);
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(localToolkitImage, paramImageObserver)) {
      return false;
    }
    ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
    return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramColor, paramImageObserver);
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver)
  {
    if (!(paramImage instanceof ToolkitImage)) {
      return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor);
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(localToolkitImage, paramImageObserver)) {
      return false;
    }
    ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
    return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor, paramImageObserver);
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    if (!(paramImage instanceof ToolkitImage)) {
      return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor);
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(localToolkitImage, paramImageObserver)) {
      return false;
    }
    ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
    return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    if (!(paramImage instanceof ToolkitImage)) {
      return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor);
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(localToolkitImage, paramImageObserver)) {
      return false;
    }
    ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
    return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
  }
  
  public boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
  {
    if (!(paramImage instanceof ToolkitImage))
    {
      transformImage(paramSunGraphics2D, paramImage, 0, 0, paramAffineTransform, interpolationType);
      return true;
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(localToolkitImage, paramImageObserver)) {
      return false;
    }
    ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
    return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramAffineTransform, paramImageObserver);
  }
  
  public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    if (paramBufferedImageOp != null)
    {
      if ((paramBufferedImageOp instanceof AffineTransformOp))
      {
        AffineTransformOp localAffineTransformOp = (AffineTransformOp)paramBufferedImageOp;
        transformImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, localAffineTransformOp.getTransform(), localAffineTransformOp.getInterpolationType());
        return;
      }
      paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
    }
    copyImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\DrawImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */