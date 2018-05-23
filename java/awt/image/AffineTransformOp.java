package java.awt.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import sun.awt.image.ImagingLib;

public class AffineTransformOp
  implements BufferedImageOp, RasterOp
{
  private AffineTransform xform;
  RenderingHints hints;
  public static final int TYPE_NEAREST_NEIGHBOR = 1;
  public static final int TYPE_BILINEAR = 2;
  public static final int TYPE_BICUBIC = 3;
  int interpolationType = 1;
  
  public AffineTransformOp(AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    validateTransform(paramAffineTransform);
    xform = ((AffineTransform)paramAffineTransform.clone());
    hints = paramRenderingHints;
    if (paramRenderingHints != null)
    {
      Object localObject = paramRenderingHints.get(RenderingHints.KEY_INTERPOLATION);
      if (localObject == null)
      {
        localObject = paramRenderingHints.get(RenderingHints.KEY_RENDERING);
        if (localObject == RenderingHints.VALUE_RENDER_SPEED) {
          interpolationType = 1;
        } else if (localObject == RenderingHints.VALUE_RENDER_QUALITY) {
          interpolationType = 2;
        }
      }
      else if (localObject == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
      {
        interpolationType = 1;
      }
      else if (localObject == RenderingHints.VALUE_INTERPOLATION_BILINEAR)
      {
        interpolationType = 2;
      }
      else if (localObject == RenderingHints.VALUE_INTERPOLATION_BICUBIC)
      {
        interpolationType = 3;
      }
    }
    else
    {
      interpolationType = 1;
    }
  }
  
  public AffineTransformOp(AffineTransform paramAffineTransform, int paramInt)
  {
    validateTransform(paramAffineTransform);
    xform = ((AffineTransform)paramAffineTransform.clone());
    switch (paramInt)
    {
    case 1: 
    case 2: 
    case 3: 
      break;
    default: 
      throw new IllegalArgumentException("Unknown interpolation type: " + paramInt);
    }
    interpolationType = paramInt;
  }
  
  public final int getInterpolationType()
  {
    return interpolationType;
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    if (paramBufferedImage1 == null) {
      throw new NullPointerException("src image is null");
    }
    if (paramBufferedImage1 == paramBufferedImage2) {
      throw new IllegalArgumentException("src image cannot be the same as the dst image");
    }
    int i = 0;
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    BufferedImage localBufferedImage1 = paramBufferedImage2;
    ColorModel localColorModel2;
    if (paramBufferedImage2 == null)
    {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      localColorModel2 = localColorModel1;
      localBufferedImage1 = paramBufferedImage2;
    }
    else
    {
      localColorModel2 = paramBufferedImage2.getColorModel();
      if (localColorModel1.getColorSpace().getType() != localColorModel2.getColorSpace().getType())
      {
        int j = xform.getType();
        int k = (j & (0x18 | 0x20)) != 0 ? 1 : 0;
        Object localObject2;
        if ((k == 0) && (j != 1) && (j != 0))
        {
          localObject2 = new double[4];
          xform.getMatrix((double[])localObject2);
          k = (localObject2[0] != (int)localObject2[0]) || (localObject2[3] != (int)localObject2[3]) ? 1 : 0;
        }
        if ((k != 0) && (localColorModel1.getTransparency() == 1))
        {
          localObject2 = new ColorConvertOp(hints);
          BufferedImage localBufferedImage2 = null;
          int m = paramBufferedImage1.getWidth();
          int n = paramBufferedImage1.getHeight();
          if (localColorModel2.getTransparency() == 1)
          {
            localBufferedImage2 = new BufferedImage(m, n, 2);
          }
          else
          {
            WritableRaster localWritableRaster = localColorModel2.createCompatibleWritableRaster(m, n);
            localBufferedImage2 = new BufferedImage(localColorModel2, localWritableRaster, localColorModel2.isAlphaPremultiplied(), null);
          }
          paramBufferedImage1 = ((ColorConvertOp)localObject2).filter(paramBufferedImage1, localBufferedImage2);
        }
        else
        {
          i = 1;
          paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
        }
      }
    }
    if ((interpolationType != 1) && ((paramBufferedImage2.getColorModel() instanceof IndexColorModel))) {
      paramBufferedImage2 = new BufferedImage(paramBufferedImage2.getWidth(), paramBufferedImage2.getHeight(), 2);
    }
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null) {
      throw new ImagingOpException("Unable to transform src image");
    }
    Object localObject1;
    if (i != 0)
    {
      localObject1 = new ColorConvertOp(hints);
      ((ColorConvertOp)localObject1).filter(paramBufferedImage2, localBufferedImage1);
    }
    else if (localBufferedImage1 != paramBufferedImage2)
    {
      localObject1 = localBufferedImage1.createGraphics();
      try
      {
        ((Graphics2D)localObject1).setComposite(AlphaComposite.Src);
        ((Graphics2D)localObject1).drawImage(paramBufferedImage2, 0, 0, null);
      }
      finally
      {
        ((Graphics2D)localObject1).dispose();
      }
    }
    return localBufferedImage1;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    if (paramRaster == null) {
      throw new NullPointerException("src image is null");
    }
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    }
    if (paramRaster == paramWritableRaster) {
      throw new IllegalArgumentException("src image cannot be the same as the dst image");
    }
    if (paramRaster.getNumBands() != paramWritableRaster.getNumBands()) {
      throw new IllegalArgumentException("Number of src bands (" + paramRaster.getNumBands() + ") does not match number of  dst bands (" + paramWritableRaster.getNumBands() + ")");
    }
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) == null) {
      throw new ImagingOpException("Unable to transform src image");
    }
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage)
  {
    return getBounds2D(paramBufferedImage.getRaster());
  }
  
  public final Rectangle2D getBounds2D(Raster paramRaster)
  {
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    float[] arrayOfFloat = { 0.0F, 0.0F, i, 0.0F, i, j, 0.0F, j };
    xform.transform(arrayOfFloat, 0, arrayOfFloat, 0, 4);
    float f1 = arrayOfFloat[0];
    float f2 = arrayOfFloat[1];
    float f3 = arrayOfFloat[0];
    float f4 = arrayOfFloat[1];
    for (int k = 2; k < 8; k += 2)
    {
      if (arrayOfFloat[k] > f1) {
        f1 = arrayOfFloat[k];
      } else if (arrayOfFloat[k] < f3) {
        f3 = arrayOfFloat[k];
      }
      if (arrayOfFloat[(k + 1)] > f2) {
        f2 = arrayOfFloat[(k + 1)];
      } else if (arrayOfFloat[(k + 1)] < f4) {
        f4 = arrayOfFloat[(k + 1)];
      }
    }
    return new Rectangle2D.Float(f3, f4, f1 - f3, f2 - f4);
  }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel)
  {
    Rectangle localRectangle = getBounds2D(paramBufferedImage).getBounds();
    int i = x + width;
    int j = y + height;
    if (i <= 0) {
      throw new RasterFormatException("Transformed width (" + i + ") is less than or equal to 0.");
    }
    if (j <= 0) {
      throw new RasterFormatException("Transformed height (" + j + ") is less than or equal to 0.");
    }
    BufferedImage localBufferedImage;
    if (paramColorModel == null)
    {
      ColorModel localColorModel = paramBufferedImage.getColorModel();
      if ((interpolationType != 1) && (((localColorModel instanceof IndexColorModel)) || (localColorModel.getTransparency() == 1))) {
        localBufferedImage = new BufferedImage(i, j, 2);
      } else {
        localBufferedImage = new BufferedImage(localColorModel, paramBufferedImage.getRaster().createCompatibleWritableRaster(i, j), localColorModel.isAlphaPremultiplied(), null);
      }
    }
    else
    {
      localBufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    }
    return localBufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster)
  {
    Rectangle2D localRectangle2D = getBounds2D(paramRaster);
    return paramRaster.createCompatibleWritableRaster((int)localRectangle2D.getX(), (int)localRectangle2D.getY(), (int)localRectangle2D.getWidth(), (int)localRectangle2D.getHeight());
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    return xform.transform(paramPoint2D1, paramPoint2D2);
  }
  
  public final AffineTransform getTransform()
  {
    return (AffineTransform)xform.clone();
  }
  
  public final RenderingHints getRenderingHints()
  {
    if (hints == null)
    {
      Object localObject;
      switch (interpolationType)
      {
      case 1: 
        localObject = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        break;
      case 2: 
        localObject = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        break;
      case 3: 
        localObject = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        break;
      default: 
        throw new InternalError("Unknown interpolation type " + interpolationType);
      }
      hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, localObject);
    }
    return hints;
  }
  
  void validateTransform(AffineTransform paramAffineTransform)
  {
    if (Math.abs(paramAffineTransform.getDeterminant()) <= Double.MIN_VALUE) {
      throw new ImagingOpException("Unable to invert transform " + paramAffineTransform);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\AffineTransformOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */