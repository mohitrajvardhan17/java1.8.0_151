package java.awt.image;

public class PixelInterleavedSampleModel
  extends ComponentSampleModel
{
  public PixelInterleavedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramArrayOfInt);
    int i = bandOffsets[0];
    int j = bandOffsets[0];
    for (int k = 1; k < bandOffsets.length; k++)
    {
      i = Math.min(i, bandOffsets[k]);
      j = Math.max(j, bandOffsets[k]);
    }
    j -= i;
    if (j > paramInt5) {
      throw new IllegalArgumentException("Offsets between bands must be less than the scanline  stride");
    }
    if (paramInt4 * paramInt2 > paramInt5) {
      throw new IllegalArgumentException("Pixel stride times width must be less than or equal to the scanline stride");
    }
    if (paramInt4 < j) {
      throw new IllegalArgumentException("Pixel stride must be greater than or equal to the offsets between bands");
    }
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    int i = bandOffsets[0];
    int j = bandOffsets.length;
    for (int k = 1; k < j; k++) {
      if (bandOffsets[k] < i) {
        i = bandOffsets[k];
      }
    }
    int[] arrayOfInt;
    if (i > 0)
    {
      arrayOfInt = new int[j];
      for (int m = 0; m < j; m++) {
        arrayOfInt[m] = (bandOffsets[m] - i);
      }
    }
    else
    {
      arrayOfInt = bandOffsets;
    }
    return new PixelInterleavedSampleModel(dataType, paramInt1, paramInt2, pixelStride, pixelStride * paramInt1, arrayOfInt);
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt)
  {
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfInt[i] = bandOffsets[paramArrayOfInt[i]];
    }
    return new PixelInterleavedSampleModel(dataType, width, height, pixelStride, scanlineStride, arrayOfInt);
  }
  
  public int hashCode()
  {
    return super.hashCode() ^ 0x1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\PixelInterleavedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */