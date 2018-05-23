package sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

class OpaqueCopyArgbToAny
  extends Blit
{
  OpaqueCopyArgbToAny()
  {
    super(SurfaceType.IntArgb, CompositeType.SrcNoEa, SurfaceType.Any);
  }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Raster localRaster = paramSurfaceData1.getRaster(paramInt1, paramInt2, paramInt5, paramInt6);
    IntegerComponentRaster localIntegerComponentRaster = (IntegerComponentRaster)localRaster;
    int[] arrayOfInt1 = localIntegerComponentRaster.getDataStorage();
    WritableRaster localWritableRaster = (WritableRaster)paramSurfaceData2.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    ColorModel localColorModel = paramSurfaceData2.getColorModel();
    Region localRegion = CustomComponent.getRegionOfInterest(paramSurfaceData1, paramSurfaceData2, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    SpanIterator localSpanIterator = localRegion.getSpanIterator();
    Object localObject = null;
    int i = localIntegerComponentRaster.getScanlineStride();
    paramInt1 -= paramInt3;
    paramInt2 -= paramInt4;
    int[] arrayOfInt2 = new int[4];
    while (localSpanIterator.nextSpan(arrayOfInt2))
    {
      int j = localIntegerComponentRaster.getDataOffset(0) + (paramInt2 + arrayOfInt2[1]) * i + (paramInt1 + arrayOfInt2[0]);
      for (int k = arrayOfInt2[1]; k < arrayOfInt2[3]; k++)
      {
        int m = j;
        for (int n = arrayOfInt2[0]; n < arrayOfInt2[2]; n++)
        {
          localObject = localColorModel.getDataElements(arrayOfInt1[(m++)], localObject);
          localWritableRaster.setDataElements(n, k, localObject);
        }
        j += i;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\OpaqueCopyArgbToAny.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */