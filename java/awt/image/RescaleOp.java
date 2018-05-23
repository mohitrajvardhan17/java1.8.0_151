package java.awt.image;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class RescaleOp
  implements BufferedImageOp, RasterOp
{
  float[] scaleFactors;
  float[] offsets;
  int length = 0;
  RenderingHints hints;
  private int srcNbits;
  private int dstNbits;
  
  public RescaleOp(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, RenderingHints paramRenderingHints)
  {
    length = paramArrayOfFloat1.length;
    if (length > paramArrayOfFloat2.length) {
      length = paramArrayOfFloat2.length;
    }
    scaleFactors = new float[length];
    offsets = new float[length];
    for (int i = 0; i < length; i++)
    {
      scaleFactors[i] = paramArrayOfFloat1[i];
      offsets[i] = paramArrayOfFloat2[i];
    }
    hints = paramRenderingHints;
  }
  
  public RescaleOp(float paramFloat1, float paramFloat2, RenderingHints paramRenderingHints)
  {
    length = 1;
    scaleFactors = new float[1];
    offsets = new float[1];
    scaleFactors[0] = paramFloat1;
    offsets[0] = paramFloat2;
    hints = paramRenderingHints;
  }
  
  public final float[] getScaleFactors(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      return (float[])scaleFactors.clone();
    }
    System.arraycopy(scaleFactors, 0, paramArrayOfFloat, 0, Math.min(scaleFactors.length, paramArrayOfFloat.length));
    return paramArrayOfFloat;
  }
  
  public final float[] getOffsets(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      return (float[])offsets.clone();
    }
    System.arraycopy(offsets, 0, paramArrayOfFloat, 0, Math.min(offsets.length, paramArrayOfFloat.length));
    return paramArrayOfFloat;
  }
  
  public final int getNumFactors()
  {
    return length;
  }
  
  private ByteLookupTable createByteLut(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2)
  {
    byte[][] arrayOfByte = new byte[paramArrayOfFloat1.length][paramInt2];
    for (int i = 0; i < paramArrayOfFloat1.length; i++)
    {
      float f1 = paramArrayOfFloat1[i];
      float f2 = paramArrayOfFloat2[i];
      byte[] arrayOfByte1 = arrayOfByte[i];
      for (int j = 0; j < paramInt2; j++)
      {
        int k = (int)(j * f1 + f2);
        if ((k & 0xFF00) != 0) {
          if (k < 0) {
            k = 0;
          } else {
            k = 255;
          }
        }
        arrayOfByte1[j] = ((byte)k);
      }
    }
    return new ByteLookupTable(0, arrayOfByte);
  }
  
  private ShortLookupTable createShortLut(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2)
  {
    short[][] arrayOfShort = new short[paramArrayOfFloat1.length][paramInt2];
    for (int i = 0; i < paramArrayOfFloat1.length; i++)
    {
      float f1 = paramArrayOfFloat1[i];
      float f2 = paramArrayOfFloat2[i];
      short[] arrayOfShort1 = arrayOfShort[i];
      for (int j = 0; j < paramInt2; j++)
      {
        int k = (int)(j * f1 + f2);
        if ((k & 0xFFFF0000) != 0) {
          if (k < 0) {
            k = 0;
          } else {
            k = 65535;
          }
        }
        arrayOfShort1[j] = ((short)k);
      }
    }
    return new ShortLookupTable(0, arrayOfShort);
  }
  
  private boolean canUseLookup(Raster paramRaster1, Raster paramRaster2)
  {
    int i = paramRaster1.getDataBuffer().getDataType();
    if ((i != 0) && (i != 1)) {
      return false;
    }
    SampleModel localSampleModel1 = paramRaster2.getSampleModel();
    dstNbits = localSampleModel1.getSampleSize(0);
    if ((dstNbits != 8) && (dstNbits != 16)) {
      return false;
    }
    for (int j = 1; j < paramRaster1.getNumBands(); j++)
    {
      k = localSampleModel1.getSampleSize(j);
      if (k != dstNbits) {
        return false;
      }
    }
    SampleModel localSampleModel2 = paramRaster1.getSampleModel();
    srcNbits = localSampleModel2.getSampleSize(0);
    if (srcNbits > 16) {
      return false;
    }
    for (int k = 1; k < paramRaster1.getNumBands(); k++)
    {
      int m = localSampleModel2.getSampleSize(k);
      if (m != srcNbits) {
        return false;
      }
    }
    return true;
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    int i = localColorModel1.getNumColorComponents();
    if ((localColorModel1 instanceof IndexColorModel)) {
      throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
    }
    if ((length != 1) && (length != i) && (length != localColorModel1.getNumComponents())) {
      throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components");
    }
    int j = 0;
    if ((length > i) && (localColorModel1.hasAlpha())) {
      length = (i + 1);
    }
    int k = paramBufferedImage1.getWidth();
    int m = paramBufferedImage1.getHeight();
    ColorModel localColorModel2;
    if (paramBufferedImage2 == null)
    {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      localColorModel2 = localColorModel1;
    }
    else
    {
      if (k != paramBufferedImage2.getWidth()) {
        throw new IllegalArgumentException("Src width (" + k + ") not equal to dst width (" + paramBufferedImage2.getWidth() + ")");
      }
      if (m != paramBufferedImage2.getHeight()) {
        throw new IllegalArgumentException("Src height (" + m + ") not equal to dst height (" + paramBufferedImage2.getHeight() + ")");
      }
      localColorModel2 = paramBufferedImage2.getColorModel();
      if (localColorModel1.getColorSpace().getType() != localColorModel2.getColorSpace().getType())
      {
        j = 1;
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      }
    }
    BufferedImage localBufferedImage = paramBufferedImage2;
    Object localObject;
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null)
    {
      localObject = paramBufferedImage1.getRaster();
      WritableRaster localWritableRaster = paramBufferedImage2.getRaster();
      int n;
      int i1;
      if ((localColorModel1.hasAlpha()) && ((i - 1 == length) || (length == 1)))
      {
        n = ((WritableRaster)localObject).getMinX();
        i1 = ((WritableRaster)localObject).getMinY();
        int[] arrayOfInt1 = new int[i - 1];
        for (int i3 = 0; i3 < i - 1; i3++) {
          arrayOfInt1[i3] = i3;
        }
        localObject = ((WritableRaster)localObject).createWritableChild(n, i1, ((WritableRaster)localObject).getWidth(), ((WritableRaster)localObject).getHeight(), n, i1, arrayOfInt1);
      }
      if (localColorModel2.hasAlpha())
      {
        n = localWritableRaster.getNumBands();
        if ((n - 1 == length) || (length == 1))
        {
          i1 = localWritableRaster.getMinX();
          int i2 = localWritableRaster.getMinY();
          int[] arrayOfInt2 = new int[i - 1];
          for (int i4 = 0; i4 < i - 1; i4++) {
            arrayOfInt2[i4] = i4;
          }
          localWritableRaster = localWritableRaster.createWritableChild(i1, i2, localWritableRaster.getWidth(), localWritableRaster.getHeight(), i1, i2, arrayOfInt2);
        }
      }
      filter((Raster)localObject, localWritableRaster);
    }
    if (j != 0)
    {
      localObject = new ColorConvertOp(hints);
      ((ColorConvertOp)localObject).filter(paramBufferedImage2, localBufferedImage);
    }
    return localBufferedImage;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    int i = paramRaster.getNumBands();
    int j = paramRaster.getWidth();
    int k = paramRaster.getHeight();
    int[] arrayOfInt1 = null;
    int m = 0;
    int n = 0;
    if (paramWritableRaster == null)
    {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    }
    else
    {
      if ((k != paramWritableRaster.getHeight()) || (j != paramWritableRaster.getWidth())) {
        throw new IllegalArgumentException("Width or height of Rasters do not match");
      }
      if (i != paramWritableRaster.getNumBands()) {
        throw new IllegalArgumentException("Number of bands in src " + i + " does not equal number of bands in dest " + paramWritableRaster.getNumBands());
      }
    }
    if ((length != 1) && (length != paramRaster.getNumBands())) {
      throw new IllegalArgumentException("Number of scaling constants does not equal the number of of bands in the src raster");
    }
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) != null) {
      return paramWritableRaster;
    }
    int i1;
    int i2;
    if (canUseLookup(paramRaster, paramWritableRaster))
    {
      i1 = 1 << srcNbits;
      i2 = 1 << dstNbits;
      Object localObject;
      LookupOp localLookupOp;
      if (i2 == 256)
      {
        localObject = createByteLut(scaleFactors, offsets, i, i1);
        localLookupOp = new LookupOp((LookupTable)localObject, hints);
        localLookupOp.filter(paramRaster, paramWritableRaster);
      }
      else
      {
        localObject = createShortLut(scaleFactors, offsets, i, i1);
        localLookupOp = new LookupOp((LookupTable)localObject, hints);
        localLookupOp.filter(paramRaster, paramWritableRaster);
      }
    }
    else
    {
      if (length > 1) {
        m = 1;
      }
      i1 = paramRaster.getMinX();
      i2 = paramRaster.getMinY();
      int i3 = paramWritableRaster.getMinX();
      int i4 = paramWritableRaster.getMinY();
      int[] arrayOfInt2 = new int[i];
      int[] arrayOfInt3 = new int[i];
      SampleModel localSampleModel = paramWritableRaster.getSampleModel();
      for (int i8 = 0; i8 < i; i8++)
      {
        int i7 = localSampleModel.getSampleSize(i8);
        arrayOfInt2[i8] = ((1 << i7) - 1);
        arrayOfInt2[i8] ^= 0xFFFFFFFF;
      }
      int i9 = 0;
      while (i9 < k)
      {
        int i6 = i3;
        int i5 = i1;
        int i10 = 0;
        while (i10 < j)
        {
          arrayOfInt1 = paramRaster.getPixel(i5, i2, arrayOfInt1);
          n = 0;
          int i11 = 0;
          while (i11 < i)
          {
            i8 = (int)(arrayOfInt1[i11] * scaleFactors[n] + offsets[n]);
            if ((i8 & arrayOfInt3[i11]) != 0) {
              if (i8 < 0) {
                i8 = 0;
              } else {
                i8 = arrayOfInt2[i11];
              }
            }
            arrayOfInt1[i11] = i8;
            i11++;
            n += m;
          }
          paramWritableRaster.setPixel(i6, i4, arrayOfInt1);
          i10++;
          i5++;
          i6++;
        }
        i9++;
        i2++;
        i4++;
      }
    }
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage)
  {
    return getBounds2D(paramBufferedImage.getRaster());
  }
  
  public final Rectangle2D getBounds2D(Raster paramRaster)
  {
    return paramRaster.getBounds();
  }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel)
  {
    BufferedImage localBufferedImage;
    if (paramColorModel == null)
    {
      ColorModel localColorModel = paramBufferedImage.getColorModel();
      localBufferedImage = new BufferedImage(localColorModel, paramBufferedImage.getRaster().createCompatibleWritableRaster(), localColorModel.isAlphaPremultiplied(), null);
    }
    else
    {
      int i = paramBufferedImage.getWidth();
      int j = paramBufferedImage.getHeight();
      localBufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    }
    return localBufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster)
  {
    return paramRaster.createCompatibleWritableRaster(paramRaster.getWidth(), paramRaster.getHeight());
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    if (paramPoint2D2 == null) {
      paramPoint2D2 = new Point2D.Float();
    }
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  public final RenderingHints getRenderingHints()
  {
    return hints;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\RescaleOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */