package java.awt.image;

import java.awt.color.ColorSpace;

public abstract class PackedColorModel
  extends ColorModel
{
  int[] maskArray;
  int[] maskOffsets;
  float[] scaleFactors;
  
  public PackedColorModel(ColorSpace paramColorSpace, int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4)
  {
    super(paramInt1, createBitsArray(paramArrayOfInt, paramInt2), paramColorSpace, paramInt2 != 0, paramBoolean, paramInt3, paramInt4);
    if ((paramInt1 < 1) || (paramInt1 > 32)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 32.");
    }
    maskArray = new int[numComponents];
    maskOffsets = new int[numComponents];
    scaleFactors = new float[numComponents];
    for (int i = 0; i < numColorComponents; i++) {
      DecomposeMask(paramArrayOfInt[i], i, paramColorSpace.getName(i));
    }
    if (paramInt2 != 0)
    {
      DecomposeMask(paramInt2, numColorComponents, "alpha");
      if (nBits[(numComponents - 1)] == 1) {
        transparency = 2;
      }
    }
  }
  
  public PackedColorModel(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6, int paramInt7)
  {
    super(paramInt1, createBitsArray(paramInt2, paramInt3, paramInt4, paramInt5), paramColorSpace, paramInt5 != 0, paramBoolean, paramInt6, paramInt7);
    if (paramColorSpace.getType() != 5) {
      throw new IllegalArgumentException("ColorSpace must be TYPE_RGB.");
    }
    maskArray = new int[numComponents];
    maskOffsets = new int[numComponents];
    scaleFactors = new float[numComponents];
    DecomposeMask(paramInt2, 0, "red");
    DecomposeMask(paramInt3, 1, "green");
    DecomposeMask(paramInt4, 2, "blue");
    if (paramInt5 != 0)
    {
      DecomposeMask(paramInt5, 3, "alpha");
      if (nBits[3] == 1) {
        transparency = 2;
      }
    }
  }
  
  public final int getMask(int paramInt)
  {
    return maskArray[paramInt];
  }
  
  public final int[] getMasks()
  {
    return (int[])maskArray.clone();
  }
  
  private void DecomposeMask(int paramInt1, int paramInt2, String paramString)
  {
    int i = 0;
    int j = nBits[paramInt2];
    maskArray[paramInt2] = paramInt1;
    if (paramInt1 != 0) {
      while ((paramInt1 & 0x1) == 0)
      {
        paramInt1 >>>= 1;
        i++;
      }
    }
    if (i + j > pixel_bits) {
      throw new IllegalArgumentException(paramString + " mask " + Integer.toHexString(maskArray[paramInt2]) + " overflows pixel (expecting " + pixel_bits + " bits");
    }
    maskOffsets[paramInt2] = i;
    if (j == 0) {
      scaleFactors[paramInt2] = 256.0F;
    } else {
      scaleFactors[paramInt2] = (255.0F / ((1 << j) - 1));
    }
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    return new SinglePixelPackedSampleModel(transferType, paramInt1, paramInt2, maskArray);
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel)
  {
    if (!(paramSampleModel instanceof SinglePixelPackedSampleModel)) {
      return false;
    }
    if (numComponents != paramSampleModel.getNumBands()) {
      return false;
    }
    if (paramSampleModel.getTransferType() != transferType) {
      return false;
    }
    SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
    int[] arrayOfInt = localSinglePixelPackedSampleModel.getBitMasks();
    if (arrayOfInt.length != maskArray.length) {
      return false;
    }
    int i = (int)((1L << DataBuffer.getDataTypeSize(transferType)) - 1L);
    for (int j = 0; j < arrayOfInt.length; j++) {
      if ((i & arrayOfInt[j]) != (i & maskArray[j])) {
        return false;
      }
    }
    return true;
  }
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster)
  {
    if (!hasAlpha()) {
      return null;
    }
    int i = paramWritableRaster.getMinX();
    int j = paramWritableRaster.getMinY();
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = (paramWritableRaster.getNumBands() - 1);
    return paramWritableRaster.createWritableChild(i, j, paramWritableRaster.getWidth(), paramWritableRaster.getHeight(), i, j, arrayOfInt);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof PackedColorModel)) {
      return false;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    PackedColorModel localPackedColorModel = (PackedColorModel)paramObject;
    int i = localPackedColorModel.getNumComponents();
    if (i != numComponents) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (maskArray[j] != localPackedColorModel.getMask(j)) {
        return false;
      }
    }
    return true;
  }
  
  private static final int[] createBitsArray(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt.length;
    int j = paramInt == 0 ? 0 : 1;
    int[] arrayOfInt = new int[i + j];
    for (int k = 0; k < i; k++)
    {
      arrayOfInt[k] = countBits(paramArrayOfInt[k]);
      if (arrayOfInt[k] < 0) {
        throw new IllegalArgumentException("Noncontiguous color mask (" + Integer.toHexString(paramArrayOfInt[k]) + "at index " + k);
      }
    }
    if (paramInt != 0)
    {
      arrayOfInt[i] = countBits(paramInt);
      if (arrayOfInt[i] < 0) {
        throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(paramInt));
      }
    }
    return arrayOfInt;
  }
  
  private static final int[] createBitsArray(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int[] arrayOfInt = new int[3 + (paramInt4 == 0 ? 0 : 1)];
    arrayOfInt[0] = countBits(paramInt1);
    arrayOfInt[1] = countBits(paramInt2);
    arrayOfInt[2] = countBits(paramInt3);
    if (arrayOfInt[0] < 0) {
      throw new IllegalArgumentException("Noncontiguous red mask (" + Integer.toHexString(paramInt1));
    }
    if (arrayOfInt[1] < 0) {
      throw new IllegalArgumentException("Noncontiguous green mask (" + Integer.toHexString(paramInt2));
    }
    if (arrayOfInt[2] < 0) {
      throw new IllegalArgumentException("Noncontiguous blue mask (" + Integer.toHexString(paramInt3));
    }
    if (paramInt4 != 0)
    {
      arrayOfInt[3] = countBits(paramInt4);
      if (arrayOfInt[3] < 0) {
        throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(paramInt4));
      }
    }
    return arrayOfInt;
  }
  
  private static final int countBits(int paramInt)
  {
    int i = 0;
    if (paramInt != 0)
    {
      while ((paramInt & 0x1) == 0) {
        paramInt >>>= 1;
      }
      while ((paramInt & 0x1) == 1)
      {
        paramInt >>>= 1;
        i++;
      }
    }
    if (paramInt != 0) {
      return -1;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\PackedColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */