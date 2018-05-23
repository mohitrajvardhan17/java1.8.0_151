package java.awt.image;

import java.awt.color.ColorSpace;
import java.util.Arrays;

public class DirectColorModel
  extends PackedColorModel
{
  private int red_mask;
  private int green_mask;
  private int blue_mask;
  private int alpha_mask;
  private int red_offset;
  private int green_offset;
  private int blue_offset;
  private int alpha_offset;
  private int red_scale;
  private int green_scale;
  private int blue_scale;
  private int alpha_scale;
  private boolean is_LinearRGB;
  private int lRGBprecision;
  private byte[] tosRGB8LUT;
  private byte[] fromsRGB8LUT8;
  private short[] fromsRGB8LUT16;
  
  public DirectColorModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, 0);
  }
  
  public DirectColorModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(ColorSpace.getInstance(1000), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, false, paramInt5 == 0 ? 1 : 3, ColorModel.getDefaultTransferType(paramInt1));
    setFields();
  }
  
  public DirectColorModel(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6)
  {
    super(paramColorSpace, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, paramInt5 == 0 ? 1 : 3, paramInt6);
    if (ColorModel.isLinearRGBspace(colorSpace))
    {
      is_LinearRGB = true;
      if (maxBits <= 8)
      {
        lRGBprecision = 8;
        tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
        fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
      }
      else
      {
        lRGBprecision = 16;
        tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
        fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      }
    }
    else if (!is_sRGB)
    {
      for (int i = 0; i < 3; i++) {
        if ((paramColorSpace.getMinValue(i) != 0.0F) || (paramColorSpace.getMaxValue(i) != 1.0F)) {
          throw new IllegalArgumentException("Illegal min/max RGB component value");
        }
      }
    }
    setFields();
  }
  
  public final int getRedMask()
  {
    return maskArray[0];
  }
  
  public final int getGreenMask()
  {
    return maskArray[1];
  }
  
  public final int getBlueMask()
  {
    return maskArray[2];
  }
  
  public final int getAlphaMask()
  {
    if (supportsAlpha) {
      return maskArray[3];
    }
    return 0;
  }
  
  private float[] getDefaultRGBComponents(int paramInt)
  {
    int[] arrayOfInt = getComponents(paramInt, null, 0);
    float[] arrayOfFloat = getNormalizedComponents(arrayOfInt, 0, null, 0);
    return colorSpace.toRGB(arrayOfFloat);
  }
  
  private int getsRGBComponentFromsRGB(int paramInt1, int paramInt2)
  {
    int i = (paramInt1 & maskArray[paramInt2]) >>> maskOffsets[paramInt2];
    if (isAlphaPremultiplied)
    {
      int j = (paramInt1 & maskArray[3]) >>> maskOffsets[3];
      i = j == 0 ? 0 : (int)(i * scaleFactors[paramInt2] * 255.0F / (j * scaleFactors[3]) + 0.5F);
    }
    else if (scaleFactors[paramInt2] != 1.0F)
    {
      i = (int)(i * scaleFactors[paramInt2] + 0.5F);
    }
    return i;
  }
  
  private int getsRGBComponentFromLinearRGB(int paramInt1, int paramInt2)
  {
    int i = (paramInt1 & maskArray[paramInt2]) >>> maskOffsets[paramInt2];
    if (isAlphaPremultiplied)
    {
      float f = (1 << lRGBprecision) - 1;
      int j = (paramInt1 & maskArray[3]) >>> maskOffsets[3];
      i = j == 0 ? 0 : (int)(i * scaleFactors[paramInt2] * f / (j * scaleFactors[3]) + 0.5F);
    }
    else if (nBits[paramInt2] != lRGBprecision)
    {
      if (lRGBprecision == 16) {
        i = (int)(i * scaleFactors[paramInt2] * 257.0F + 0.5F);
      } else {
        i = (int)(i * scaleFactors[paramInt2] + 0.5F);
      }
    }
    return tosRGB8LUT[i] & 0xFF;
  }
  
  public final int getRed(int paramInt)
  {
    if (is_sRGB) {
      return getsRGBComponentFromsRGB(paramInt, 0);
    }
    if (is_LinearRGB) {
      return getsRGBComponentFromLinearRGB(paramInt, 0);
    }
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return (int)(arrayOfFloat[0] * 255.0F + 0.5F);
  }
  
  public final int getGreen(int paramInt)
  {
    if (is_sRGB) {
      return getsRGBComponentFromsRGB(paramInt, 1);
    }
    if (is_LinearRGB) {
      return getsRGBComponentFromLinearRGB(paramInt, 1);
    }
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return (int)(arrayOfFloat[1] * 255.0F + 0.5F);
  }
  
  public final int getBlue(int paramInt)
  {
    if (is_sRGB) {
      return getsRGBComponentFromsRGB(paramInt, 2);
    }
    if (is_LinearRGB) {
      return getsRGBComponentFromLinearRGB(paramInt, 2);
    }
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return (int)(arrayOfFloat[2] * 255.0F + 0.5F);
  }
  
  public final int getAlpha(int paramInt)
  {
    if (!supportsAlpha) {
      return 255;
    }
    int i = (paramInt & maskArray[3]) >>> maskOffsets[3];
    if (scaleFactors[3] != 1.0F) {
      i = (int)(i * scaleFactors[3] + 0.5F);
    }
    return i;
  }
  
  public final int getRGB(int paramInt)
  {
    if ((is_sRGB) || (is_LinearRGB)) {
      return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0;
    }
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return getAlpha(paramInt) << 24 | (int)(arrayOfFloat[0] * 255.0F + 0.5F) << 16 | (int)(arrayOfFloat[1] * 255.0F + 0.5F) << 8 | (int)(arrayOfFloat[2] * 255.0F + 0.5F) << 0;
  }
  
  public int getRed(Object paramObject)
  {
    int i = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getRed(i);
  }
  
  public int getGreen(Object paramObject)
  {
    int i = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getGreen(i);
  }
  
  public int getBlue(Object paramObject)
  {
    int i = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getBlue(i);
  }
  
  public int getAlpha(Object paramObject)
  {
    int i = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getAlpha(i);
  }
  
  public int getRGB(Object paramObject)
  {
    int i = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getRGB(i);
  }
  
  public Object getDataElements(int paramInt, Object paramObject)
  {
    int[] arrayOfInt = null;
    if ((transferType == 3) && (paramObject != null))
    {
      arrayOfInt = (int[])paramObject;
      arrayOfInt[0] = 0;
    }
    else
    {
      arrayOfInt = new int[1];
    }
    ColorModel localColorModel = ColorModel.getRGBdefault();
    if ((this == localColorModel) || (equals(localColorModel)))
    {
      arrayOfInt[0] = paramInt;
      return arrayOfInt;
    }
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    float f;
    int m;
    Object localObject;
    if ((is_sRGB) || (is_LinearRGB))
    {
      int n;
      if (is_LinearRGB)
      {
        if (lRGBprecision == 8)
        {
          i = fromsRGB8LUT8[i] & 0xFF;
          j = fromsRGB8LUT8[j] & 0xFF;
          k = fromsRGB8LUT8[k] & 0xFF;
          n = 8;
          f = 0.003921569F;
        }
        else
        {
          i = fromsRGB8LUT16[i] & 0xFFFF;
          j = fromsRGB8LUT16[j] & 0xFFFF;
          k = fromsRGB8LUT16[k] & 0xFFFF;
          n = 16;
          f = 1.5259022E-5F;
        }
      }
      else
      {
        n = 8;
        f = 0.003921569F;
      }
      if (supportsAlpha)
      {
        m = paramInt >> 24 & 0xFF;
        if (isAlphaPremultiplied)
        {
          f *= m * 0.003921569F;
          n = -1;
        }
        if (nBits[3] != 8)
        {
          m = (int)(m * 0.003921569F * ((1 << nBits[3]) - 1) + 0.5F);
          if (m > (1 << nBits[3]) - 1) {
            m = (1 << nBits[3]) - 1;
          }
        }
        arrayOfInt[0] = (m << maskOffsets[3]);
      }
      if (nBits[0] != n) {
        i = (int)(i * f * ((1 << nBits[0]) - 1) + 0.5F);
      }
      if (nBits[1] != n) {
        j = (int)(j * f * ((1 << nBits[1]) - 1) + 0.5F);
      }
      if (nBits[2] != n) {
        k = (int)(k * f * ((1 << nBits[2]) - 1) + 0.5F);
      }
    }
    else
    {
      localObject = new float[3];
      f = 0.003921569F;
      localObject[0] = (i * f);
      localObject[1] = (j * f);
      localObject[2] = (k * f);
      localObject = colorSpace.fromRGB((float[])localObject);
      if (supportsAlpha)
      {
        m = paramInt >> 24 & 0xFF;
        if (isAlphaPremultiplied)
        {
          f *= m;
          for (int i1 = 0; i1 < 3; i1++) {
            localObject[i1] *= f;
          }
        }
        if (nBits[3] != 8)
        {
          m = (int)(m * 0.003921569F * ((1 << nBits[3]) - 1) + 0.5F);
          if (m > (1 << nBits[3]) - 1) {
            m = (1 << nBits[3]) - 1;
          }
        }
        arrayOfInt[0] = (m << maskOffsets[3]);
      }
      i = (int)(localObject[0] * ((1 << nBits[0]) - 1) + 0.5F);
      j = (int)(localObject[1] * ((1 << nBits[1]) - 1) + 0.5F);
      k = (int)(localObject[2] * ((1 << nBits[2]) - 1) + 0.5F);
    }
    if (maxBits > 23)
    {
      if (i > (1 << nBits[0]) - 1) {
        i = (1 << nBits[0]) - 1;
      }
      if (j > (1 << nBits[1]) - 1) {
        j = (1 << nBits[1]) - 1;
      }
      if (k > (1 << nBits[2]) - 1) {
        k = (1 << nBits[2]) - 1;
      }
    }
    arrayOfInt[0] |= i << maskOffsets[0] | j << maskOffsets[1] | k << maskOffsets[2];
    switch (transferType)
    {
    case 0: 
      if (paramObject == null) {
        localObject = new byte[1];
      } else {
        localObject = (byte[])paramObject;
      }
      localObject[0] = ((byte)(0xFF & arrayOfInt[0]));
      return localObject;
    case 1: 
      if (paramObject == null) {
        localObject = new short[1];
      } else {
        localObject = (short[])paramObject;
      }
      localObject[0] = ((short)(arrayOfInt[0] & 0xFFFF));
      return localObject;
    case 3: 
      return arrayOfInt;
    }
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
  }
  
  public final int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt2 + numComponents];
    }
    for (int i = 0; i < numComponents; i++) {
      paramArrayOfInt[(paramInt2 + i)] = ((paramInt1 & maskArray[i]) >>> maskOffsets[i]);
    }
    return paramArrayOfInt;
  }
  
  public final int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getComponents(i, paramArrayOfInt, paramInt);
  }
  
  public final WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new IllegalArgumentException("Width (" + paramInt1 + ") and height (" + paramInt2 + ") cannot be <= 0");
    }
    int[] arrayOfInt;
    if (supportsAlpha)
    {
      arrayOfInt = new int[4];
      arrayOfInt[3] = alpha_mask;
    }
    else
    {
      arrayOfInt = new int[3];
    }
    arrayOfInt[0] = red_mask;
    arrayOfInt[1] = green_mask;
    arrayOfInt[2] = blue_mask;
    if (pixel_bits > 16) {
      return Raster.createPackedRaster(3, paramInt1, paramInt2, arrayOfInt, null);
    }
    if (pixel_bits > 8) {
      return Raster.createPackedRaster(1, paramInt1, paramInt2, arrayOfInt, null);
    }
    return Raster.createPackedRaster(0, paramInt1, paramInt2, arrayOfInt, null);
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    for (int j = 0; j < numComponents; j++) {
      i |= paramArrayOfInt[(paramInt + j)] << maskOffsets[j] & maskArray[j];
    }
    return i;
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject)
  {
    int i = 0;
    for (int j = 0; j < numComponents; j++) {
      i |= paramArrayOfInt[(paramInt + j)] << maskOffsets[j] & maskArray[j];
    }
    Object localObject;
    switch (transferType)
    {
    case 0: 
      if ((paramObject instanceof byte[]))
      {
        localObject = (byte[])paramObject;
        localObject[0] = ((byte)(i & 0xFF));
        return localObject;
      }
      localObject = new byte[] { (byte)(i & 0xFF) };
      return localObject;
    case 1: 
      if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        localObject[0] = ((short)(i & 0xFFFF));
        return localObject;
      }
      localObject = new short[] { (short)(i & 0xFFFF) };
      return localObject;
    case 3: 
      if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        localObject[0] = i;
        return localObject;
      }
      localObject = new int[] { i };
      return localObject;
    }
    throw new ClassCastException("This method has not been implemented for transferType " + transferType);
  }
  
  public final ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean)
  {
    if ((!supportsAlpha) || (isAlphaPremultiplied() == paramBoolean)) {
      return this;
    }
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = numColorComponents;
    float f2 = 1.0F / ((1 << nBits[k]) - 1);
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    int i2;
    int i1;
    int i3;
    float f1;
    if (paramBoolean)
    {
      int i4;
      switch (transferType)
      {
      case 0: 
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            f1 = arrayOfInt1[k] * f2;
            if (f1 != 0.0F)
            {
              for (i4 = 0; i4 < k; i4++) {
                arrayOfInt1[i4] = ((int)(arrayOfInt1[i4] * f1 + 0.5F));
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            }
            else
            {
              if (arrayOfInt2 == null)
              {
                arrayOfInt2 = new int[numComponents];
                Arrays.fill(arrayOfInt2, 0);
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt2);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 1: 
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            f1 = arrayOfInt1[k] * f2;
            if (f1 != 0.0F)
            {
              for (i4 = 0; i4 < k; i4++) {
                arrayOfInt1[i4] = ((int)(arrayOfInt1[i4] * f1 + 0.5F));
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            }
            else
            {
              if (arrayOfInt2 == null)
              {
                arrayOfInt2 = new int[numComponents];
                Arrays.fill(arrayOfInt2, 0);
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt2);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 3: 
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            f1 = arrayOfInt1[k] * f2;
            if (f1 != 0.0F)
            {
              for (i4 = 0; i4 < k; i4++) {
                arrayOfInt1[i4] = ((int)(arrayOfInt1[i4] * f1 + 0.5F));
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            }
            else
            {
              if (arrayOfInt2 == null)
              {
                arrayOfInt2 = new int[numComponents];
                Arrays.fill(arrayOfInt2, 0);
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt2);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 2: 
      default: 
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
      }
    }
    else
    {
      float f3;
      int i5;
      switch (transferType)
      {
      case 0: 
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            f1 = arrayOfInt1[k] * f2;
            if (f1 != 0.0F)
            {
              f3 = 1.0F / f1;
              for (i5 = 0; i5 < k; i5++) {
                arrayOfInt1[i5] = ((int)(arrayOfInt1[i5] * f3 + 0.5F));
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 1: 
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            f1 = arrayOfInt1[k] * f2;
            if (f1 != 0.0F)
            {
              f3 = 1.0F / f1;
              for (i5 = 0; i5 < k; i5++) {
                arrayOfInt1[i5] = ((int)(arrayOfInt1[i5] * f3 + 0.5F));
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 3: 
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            f1 = arrayOfInt1[k] * f2;
            if (f1 != 0.0F)
            {
              f3 = 1.0F / f1;
              for (i5 = 0; i5 < k; i5++) {
                arrayOfInt1[i5] = ((int)(arrayOfInt1[i5] * f3 + 0.5F));
              }
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 2: 
      default: 
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
      }
    }
    return new DirectColorModel(colorSpace, pixel_bits, maskArray[0], maskArray[1], maskArray[2], maskArray[3], paramBoolean, transferType);
  }
  
  public boolean isCompatibleRaster(Raster paramRaster)
  {
    SampleModel localSampleModel = paramRaster.getSampleModel();
    SinglePixelPackedSampleModel localSinglePixelPackedSampleModel;
    if ((localSampleModel instanceof SinglePixelPackedSampleModel)) {
      localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)localSampleModel;
    } else {
      return false;
    }
    if (localSinglePixelPackedSampleModel.getNumBands() != getNumComponents()) {
      return false;
    }
    int[] arrayOfInt = localSinglePixelPackedSampleModel.getBitMasks();
    for (int i = 0; i < numComponents; i++) {
      if (arrayOfInt[i] != maskArray[i]) {
        return false;
      }
    }
    return paramRaster.getTransferType() == transferType;
  }
  
  private void setFields()
  {
    red_mask = maskArray[0];
    red_offset = maskOffsets[0];
    green_mask = maskArray[1];
    green_offset = maskOffsets[1];
    blue_mask = maskArray[2];
    blue_offset = maskOffsets[2];
    if (nBits[0] < 8) {
      red_scale = ((1 << nBits[0]) - 1);
    }
    if (nBits[1] < 8) {
      green_scale = ((1 << nBits[1]) - 1);
    }
    if (nBits[2] < 8) {
      blue_scale = ((1 << nBits[2]) - 1);
    }
    if (supportsAlpha)
    {
      alpha_mask = maskArray[3];
      alpha_offset = maskOffsets[3];
      if (nBits[3] < 8) {
        alpha_scale = ((1 << nBits[3]) - 1);
      }
    }
  }
  
  public String toString()
  {
    return new String("DirectColorModel: rmask=" + Integer.toHexString(red_mask) + " gmask=" + Integer.toHexString(green_mask) + " bmask=" + Integer.toHexString(blue_mask) + " amask=" + Integer.toHexString(alpha_mask));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\DirectColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */