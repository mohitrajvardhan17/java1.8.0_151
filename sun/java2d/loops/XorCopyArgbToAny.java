package sun.java2d.loops;

import java.awt.Color;
import java.awt.Composite;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.SpanIterator;

class XorCopyArgbToAny
  extends Blit
{
  XorCopyArgbToAny()
  {
    super(SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any);
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
    int i = ((XORComposite)paramComposite).getXorColor().getRGB();
    Object localObject1 = localColorModel.getDataElements(i, null);
    Object localObject2 = null;
    Object localObject3 = null;
    int j = localIntegerComponentRaster.getScanlineStride();
    paramInt1 -= paramInt3;
    paramInt2 -= paramInt4;
    int[] arrayOfInt2 = new int[4];
    while (localSpanIterator.nextSpan(arrayOfInt2))
    {
      int k = localIntegerComponentRaster.getDataOffset(0) + (paramInt2 + arrayOfInt2[1]) * j + (paramInt1 + arrayOfInt2[0]);
      for (int m = arrayOfInt2[1]; m < arrayOfInt2[3]; m++)
      {
        int n = k;
        for (int i1 = arrayOfInt2[0]; i1 < arrayOfInt2[2]; i1++)
        {
          localObject2 = localColorModel.getDataElements(arrayOfInt1[(n++)], localObject2);
          localObject3 = localWritableRaster.getDataElements(i1, m, localObject3);
          switch (localColorModel.getTransferType())
          {
          case 0: 
            byte[] arrayOfByte1 = (byte[])localObject2;
            byte[] arrayOfByte2 = (byte[])localObject3;
            byte[] arrayOfByte3 = (byte[])localObject1;
            for (int i2 = 0; i2 < arrayOfByte2.length; i2++)
            {
              int tmp325_323 = i2;
              byte[] tmp325_321 = arrayOfByte2;
              tmp325_321[tmp325_323] = ((byte)(tmp325_321[tmp325_323] ^ arrayOfByte1[i2] ^ arrayOfByte3[i2]));
            }
            break;
          case 1: 
          case 2: 
            short[] arrayOfShort1 = (short[])localObject2;
            short[] arrayOfShort2 = (short[])localObject3;
            short[] arrayOfShort3 = (short[])localObject1;
            for (int i3 = 0; i3 < arrayOfShort2.length; i3++)
            {
              int tmp395_393 = i3;
              short[] tmp395_391 = arrayOfShort2;
              tmp395_391[tmp395_393] = ((short)(tmp395_391[tmp395_393] ^ arrayOfShort1[i3] ^ arrayOfShort3[i3]));
            }
            break;
          case 3: 
            int[] arrayOfInt3 = (int[])localObject2;
            int[] arrayOfInt4 = (int[])localObject3;
            int[] arrayOfInt5 = (int[])localObject1;
            for (int i4 = 0; i4 < arrayOfInt4.length; i4++) {
              arrayOfInt4[i4] ^= arrayOfInt3[i4] ^ arrayOfInt5[i4];
            }
            break;
          case 4: 
            float[] arrayOfFloat1 = (float[])localObject2;
            float[] arrayOfFloat2 = (float[])localObject3;
            float[] arrayOfFloat3 = (float[])localObject1;
            for (int i5 = 0; i5 < arrayOfFloat2.length; i5++)
            {
              int i6 = Float.floatToIntBits(arrayOfFloat2[i5]) ^ Float.floatToIntBits(arrayOfFloat1[i5]) ^ Float.floatToIntBits(arrayOfFloat3[i5]);
              arrayOfFloat2[i5] = Float.intBitsToFloat(i6);
            }
            break;
          case 5: 
            double[] arrayOfDouble1 = (double[])localObject2;
            double[] arrayOfDouble2 = (double[])localObject3;
            double[] arrayOfDouble3 = (double[])localObject1;
            for (int i7 = 0; i7 < arrayOfDouble2.length; i7++)
            {
              long l = Double.doubleToLongBits(arrayOfDouble2[i7]) ^ Double.doubleToLongBits(arrayOfDouble1[i7]) ^ Double.doubleToLongBits(arrayOfDouble3[i7]);
              arrayOfDouble2[i7] = Double.longBitsToDouble(l);
            }
            break;
          default: 
            throw new InternalError("Unsupported XOR pixel type");
          }
          localWritableRaster.setDataElements(i1, m, localObject3);
        }
        k += j;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\XorCopyArgbToAny.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */