package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import sun.awt.image.ImagingLib;

public class BandCombineOp
  implements RasterOp
{
  float[][] matrix;
  int nrows = 0;
  int ncols = 0;
  RenderingHints hints;
  
  public BandCombineOp(float[][] paramArrayOfFloat, RenderingHints paramRenderingHints)
  {
    nrows = paramArrayOfFloat.length;
    ncols = paramArrayOfFloat[0].length;
    matrix = new float[nrows][];
    for (int i = 0; i < nrows; i++)
    {
      if (ncols > paramArrayOfFloat[i].length) {
        throw new IndexOutOfBoundsException("row " + i + " too short");
      }
      matrix[i] = Arrays.copyOf(paramArrayOfFloat[i], ncols);
    }
    hints = paramRenderingHints;
  }
  
  public final float[][] getMatrix()
  {
    float[][] arrayOfFloat = new float[nrows][];
    for (int i = 0; i < nrows; i++) {
      arrayOfFloat[i] = Arrays.copyOf(matrix[i], ncols);
    }
    return arrayOfFloat;
  }
  
  public WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    int i = paramRaster.getNumBands();
    if ((ncols != i) && (ncols != i + 1)) {
      throw new IllegalArgumentException("Number of columns in the matrix (" + ncols + ") must be equal to the number of bands ([+1]) in src (" + i + ").");
    }
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else if (nrows != paramWritableRaster.getNumBands()) {
      throw new IllegalArgumentException("Number of rows in the matrix (" + nrows + ") must be equal to the number of bands ([+1]) in dst (" + i + ").");
    }
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) != null) {
      return paramWritableRaster;
    }
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = new int[paramWritableRaster.getNumBands()];
    int j = paramRaster.getMinX();
    int k = paramRaster.getMinY();
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    int i3;
    int i2;
    int i1;
    int i4;
    int i5;
    float f;
    int i6;
    if (ncols == i)
    {
      i3 = 0;
      while (i3 < paramRaster.getHeight())
      {
        i2 = m;
        i1 = j;
        i4 = 0;
        while (i4 < paramRaster.getWidth())
        {
          arrayOfInt1 = paramRaster.getPixel(i1, k, arrayOfInt1);
          for (i5 = 0; i5 < nrows; i5++)
          {
            f = 0.0F;
            for (i6 = 0; i6 < ncols; i6++) {
              f += matrix[i5][i6] * arrayOfInt1[i6];
            }
            arrayOfInt2[i5] = ((int)f);
          }
          paramWritableRaster.setPixel(i2, n, arrayOfInt2);
          i4++;
          i1++;
          i2++;
        }
        i3++;
        k++;
        n++;
      }
    }
    else
    {
      i3 = 0;
      while (i3 < paramRaster.getHeight())
      {
        i2 = m;
        i1 = j;
        i4 = 0;
        while (i4 < paramRaster.getWidth())
        {
          arrayOfInt1 = paramRaster.getPixel(i1, k, arrayOfInt1);
          for (i5 = 0; i5 < nrows; i5++)
          {
            f = 0.0F;
            for (i6 = 0; i6 < i; i6++) {
              f += matrix[i5][i6] * arrayOfInt1[i6];
            }
            arrayOfInt2[i5] = ((int)(f + matrix[i5][i]));
          }
          paramWritableRaster.setPixel(i2, n, arrayOfInt2);
          i4++;
          i1++;
          i2++;
        }
        i3++;
        k++;
        n++;
      }
    }
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(Raster paramRaster)
  {
    return paramRaster.getBounds();
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster)
  {
    int i = paramRaster.getNumBands();
    if ((ncols != i) && (ncols != i + 1)) {
      throw new IllegalArgumentException("Number of columns in the matrix (" + ncols + ") must be equal to the number of bands ([+1]) in src (" + i + ").");
    }
    if (paramRaster.getNumBands() == nrows) {
      return paramRaster.createCompatibleWritableRaster();
    }
    throw new IllegalArgumentException("Don't know how to create a  compatible Raster with " + nrows + " bands.");
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\BandCombineOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */