package com.sun.imageio.plugins.common;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

public class ImageUtil
{
  public ImageUtil() {}
  
  public static final ColorModel createColorModel(SampleModel paramSampleModel)
  {
    if (paramSampleModel == null) {
      throw new IllegalArgumentException("sampleModel == null!");
    }
    int i = paramSampleModel.getDataType();
    switch (i)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      break;
    default: 
      return null;
    }
    Object localObject1 = null;
    int[] arrayOfInt = paramSampleModel.getSampleSize();
    Object localObject2;
    boolean bool1;
    boolean bool2;
    int i2;
    if ((paramSampleModel instanceof ComponentSampleModel))
    {
      int j = paramSampleModel.getNumBands();
      localObject2 = null;
      if (j <= 2) {
        localObject2 = ColorSpace.getInstance(1003);
      } else if (j <= 4) {
        localObject2 = ColorSpace.getInstance(1000);
      } else {
        localObject2 = new BogusColorSpace(j);
      }
      bool1 = (j == 2) || (j == 4);
      bool2 = false;
      i2 = bool1 ? 3 : 1;
      localObject1 = new ComponentColorModel((ColorSpace)localObject2, arrayOfInt, bool1, bool2, i2, i);
    }
    else
    {
      int i1;
      if ((paramSampleModel.getNumBands() <= 4) && ((paramSampleModel instanceof SinglePixelPackedSampleModel)))
      {
        SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
        localObject2 = localSinglePixelPackedSampleModel.getBitMasks();
        bool1 = false;
        bool2 = false;
        i2 = 0;
        int i3 = 0;
        int i4 = localObject2.length;
        int n;
        if (i4 <= 2)
        {
          n = i1 = i2 = localObject2[0];
          if (i4 == 2) {
            i3 = localObject2[1];
          }
        }
        else
        {
          n = localObject2[0];
          i1 = localObject2[1];
          i2 = localObject2[2];
          if (i4 == 4) {
            i3 = localObject2[3];
          }
        }
        int i5 = 0;
        for (int i6 = 0; i6 < arrayOfInt.length; i6++) {
          i5 += arrayOfInt[i6];
        }
        return new DirectColorModel(i5, n, i1, i2, i3);
      }
      if ((paramSampleModel instanceof MultiPixelPackedSampleModel))
      {
        int k = arrayOfInt[0];
        int m = 1 << k;
        byte[] arrayOfByte = new byte[m];
        for (i1 = 0; i1 < m; i1++) {
          arrayOfByte[i1] = ((byte)(i1 * 255 / (m - 1)));
        }
        localObject1 = new IndexColorModel(k, m, arrayOfByte, arrayOfByte, arrayOfByte);
      }
    }
    return (ColorModel)localObject1;
  }
  
  public static byte[] getPackedBinaryData(Raster paramRaster, Rectangle paramRectangle)
  {
    SampleModel localSampleModel = paramRaster.getSampleModel();
    if (!isBinary(localSampleModel)) {
      throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
    }
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    DataBuffer localDataBuffer = paramRaster.getDataBuffer();
    int n = i - paramRaster.getSampleModelTranslateX();
    int i1 = j - paramRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)localSampleModel;
    int i2 = localMultiPixelPackedSampleModel.getScanlineStride();
    int i3 = localDataBuffer.getOffset() + localMultiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = localMultiPixelPackedSampleModel.getBitOffset(n);
    int i5 = (k + 7) / 8;
    if (((localDataBuffer instanceof DataBufferByte)) && (i3 == 0) && (i4 == 0) && (i5 == i2) && (((DataBufferByte)localDataBuffer).getData().length == i5 * m)) {
      return ((DataBufferByte)localDataBuffer).getData();
    }
    byte[] arrayOfByte = new byte[i5 * m];
    int i6 = 0;
    Object localObject;
    int i7;
    int i8;
    int i9;
    int i10;
    if (i4 == 0)
    {
      if ((localDataBuffer instanceof DataBufferByte))
      {
        localObject = ((DataBufferByte)localDataBuffer).getData();
        i7 = i5;
        i8 = 0;
        for (i9 = 0; i9 < m; i9++)
        {
          System.arraycopy(localObject, i3, arrayOfByte, i8, i7);
          i8 += i7;
          i3 += i2;
        }
      }
      else if (((localDataBuffer instanceof DataBufferShort)) || ((localDataBuffer instanceof DataBufferUShort)))
      {
        localObject = (localDataBuffer instanceof DataBufferShort) ? ((DataBufferShort)localDataBuffer).getData() : ((DataBufferUShort)localDataBuffer).getData();
        for (i7 = 0; i7 < m; i7++)
        {
          i8 = k;
          i9 = i3;
          while (i8 > 8)
          {
            i10 = localObject[(i9++)];
            arrayOfByte[(i6++)] = ((byte)(i10 >>> 8 & 0xFF));
            arrayOfByte[(i6++)] = ((byte)(i10 & 0xFF));
            i8 -= 16;
          }
          if (i8 > 0) {
            arrayOfByte[(i6++)] = ((byte)(localObject[i9] >>> 8 & 0xFF));
          }
          i3 += i2;
        }
      }
      else if ((localDataBuffer instanceof DataBufferInt))
      {
        localObject = ((DataBufferInt)localDataBuffer).getData();
        for (i7 = 0; i7 < m; i7++)
        {
          i8 = k;
          i9 = i3;
          while (i8 > 24)
          {
            i10 = localObject[(i9++)];
            arrayOfByte[(i6++)] = ((byte)(i10 >>> 24 & 0xFF));
            arrayOfByte[(i6++)] = ((byte)(i10 >>> 16 & 0xFF));
            arrayOfByte[(i6++)] = ((byte)(i10 >>> 8 & 0xFF));
            arrayOfByte[(i6++)] = ((byte)(i10 & 0xFF));
            i8 -= 32;
          }
          i10 = 24;
          while (i8 > 0)
          {
            arrayOfByte[(i6++)] = ((byte)(localObject[i9] >>> i10 & 0xFF));
            i10 -= 8;
            i8 -= 8;
          }
          i3 += i2;
        }
      }
    }
    else
    {
      int i11;
      if ((localDataBuffer instanceof DataBufferByte))
      {
        localObject = ((DataBufferByte)localDataBuffer).getData();
        if ((i4 & 0x7) == 0)
        {
          i7 = i5;
          i8 = 0;
          for (i9 = 0; i9 < m; i9++)
          {
            System.arraycopy(localObject, i3, arrayOfByte, i8, i7);
            i8 += i7;
            i3 += i2;
          }
        }
        else
        {
          i7 = i4 & 0x7;
          i8 = 8 - i7;
          for (i9 = 0; i9 < m; i9++)
          {
            i10 = i3;
            for (i11 = k; i11 > 0; i11 -= 8) {
              if (i11 > i8) {
                arrayOfByte[(i6++)] = ((byte)((localObject[(i10++)] & 0xFF) << i7 | (localObject[i10] & 0xFF) >>> i8));
              } else {
                arrayOfByte[(i6++)] = ((byte)((localObject[i10] & 0xFF) << i7));
              }
            }
            i3 += i2;
          }
        }
      }
      else
      {
        int i12;
        int i13;
        int i14;
        if (((localDataBuffer instanceof DataBufferShort)) || ((localDataBuffer instanceof DataBufferUShort)))
        {
          localObject = (localDataBuffer instanceof DataBufferShort) ? ((DataBufferShort)localDataBuffer).getData() : ((DataBufferUShort)localDataBuffer).getData();
          for (i7 = 0; i7 < m; i7++)
          {
            i8 = i4;
            i9 = 0;
            while (i9 < k)
            {
              i10 = i3 + i8 / 16;
              i11 = i8 % 16;
              i12 = localObject[i10] & 0xFFFF;
              if (i11 <= 8)
              {
                arrayOfByte[(i6++)] = ((byte)(i12 >>> 8 - i11));
              }
              else
              {
                i13 = i11 - 8;
                i14 = localObject[(i10 + 1)] & 0xFFFF;
                arrayOfByte[(i6++)] = ((byte)(i12 << i13 | i14 >>> 16 - i13));
              }
              i9 += 8;
              i8 += 8;
            }
            i3 += i2;
          }
        }
        else if ((localDataBuffer instanceof DataBufferInt))
        {
          localObject = ((DataBufferInt)localDataBuffer).getData();
          for (i7 = 0; i7 < m; i7++)
          {
            i8 = i4;
            i9 = 0;
            while (i9 < k)
            {
              i10 = i3 + i8 / 32;
              i11 = i8 % 32;
              i12 = localObject[i10];
              if (i11 <= 24)
              {
                arrayOfByte[(i6++)] = ((byte)(i12 >>> 24 - i11));
              }
              else
              {
                i13 = i11 - 24;
                i14 = localObject[(i10 + 1)];
                arrayOfByte[(i6++)] = ((byte)(i12 << i13 | i14 >>> 32 - i13));
              }
              i9 += 8;
              i8 += 8;
            }
            i3 += i2;
          }
        }
      }
    }
    return arrayOfByte;
  }
  
  public static byte[] getUnpackedBinaryData(Raster paramRaster, Rectangle paramRectangle)
  {
    SampleModel localSampleModel = paramRaster.getSampleModel();
    if (!isBinary(localSampleModel)) {
      throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
    }
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    DataBuffer localDataBuffer = paramRaster.getDataBuffer();
    int n = i - paramRaster.getSampleModelTranslateX();
    int i1 = j - paramRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)localSampleModel;
    int i2 = localMultiPixelPackedSampleModel.getScanlineStride();
    int i3 = localDataBuffer.getOffset() + localMultiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = localMultiPixelPackedSampleModel.getBitOffset(n);
    byte[] arrayOfByte = new byte[k * m];
    int i5 = j + m;
    int i6 = i + k;
    int i7 = 0;
    Object localObject;
    int i8;
    int i9;
    int i10;
    int i11;
    if ((localDataBuffer instanceof DataBufferByte))
    {
      localObject = ((DataBufferByte)localDataBuffer).getData();
      for (i8 = j; i8 < i5; i8++)
      {
        i9 = i3 * 8 + i4;
        for (i10 = i; i10 < i6; i10++)
        {
          i11 = localObject[(i9 / 8)];
          arrayOfByte[(i7++)] = ((byte)(i11 >>> (7 - i9 & 0x7) & 0x1));
          i9++;
        }
        i3 += i2;
      }
    }
    else if (((localDataBuffer instanceof DataBufferShort)) || ((localDataBuffer instanceof DataBufferUShort)))
    {
      localObject = (localDataBuffer instanceof DataBufferShort) ? ((DataBufferShort)localDataBuffer).getData() : ((DataBufferUShort)localDataBuffer).getData();
      for (i8 = j; i8 < i5; i8++)
      {
        i9 = i3 * 16 + i4;
        for (i10 = i; i10 < i6; i10++)
        {
          i11 = localObject[(i9 / 16)];
          arrayOfByte[(i7++)] = ((byte)(i11 >>> 15 - i9 % 16 & 0x1));
          i9++;
        }
        i3 += i2;
      }
    }
    else if ((localDataBuffer instanceof DataBufferInt))
    {
      localObject = ((DataBufferInt)localDataBuffer).getData();
      for (i8 = j; i8 < i5; i8++)
      {
        i9 = i3 * 32 + i4;
        for (i10 = i; i10 < i6; i10++)
        {
          i11 = localObject[(i9 / 32)];
          arrayOfByte[(i7++)] = ((byte)(i11 >>> 31 - i9 % 32 & 0x1));
          i9++;
        }
        i3 += i2;
      }
    }
    return arrayOfByte;
  }
  
  public static void setPackedBinaryData(byte[] paramArrayOfByte, WritableRaster paramWritableRaster, Rectangle paramRectangle)
  {
    SampleModel localSampleModel = paramWritableRaster.getSampleModel();
    if (!isBinary(localSampleModel)) {
      throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
    }
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    DataBuffer localDataBuffer = paramWritableRaster.getDataBuffer();
    int n = i - paramWritableRaster.getSampleModelTranslateX();
    int i1 = j - paramWritableRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)localSampleModel;
    int i2 = localMultiPixelPackedSampleModel.getScanlineStride();
    int i3 = localDataBuffer.getOffset() + localMultiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = localMultiPixelPackedSampleModel.getBitOffset(n);
    int i5 = 0;
    int i7;
    int i9;
    int i10;
    if (i4 == 0)
    {
      Object localObject1;
      int i8;
      if ((localDataBuffer instanceof DataBufferByte))
      {
        localObject1 = ((DataBufferByte)localDataBuffer).getData();
        if (localObject1 == paramArrayOfByte) {
          return;
        }
        i7 = (k + 7) / 8;
        i8 = 0;
        for (i9 = 0; i9 < m; i9++)
        {
          System.arraycopy(paramArrayOfByte, i8, localObject1, i3, i7);
          i8 += i7;
          i3 += i2;
        }
      }
      else if (((localDataBuffer instanceof DataBufferShort)) || ((localDataBuffer instanceof DataBufferUShort)))
      {
        localObject1 = (localDataBuffer instanceof DataBufferShort) ? ((DataBufferShort)localDataBuffer).getData() : ((DataBufferUShort)localDataBuffer).getData();
        for (i7 = 0; i7 < m; i7++)
        {
          i8 = k;
          i9 = i3;
          while (i8 > 8)
          {
            localObject1[(i9++)] = ((short)((paramArrayOfByte[(i5++)] & 0xFF) << 8 | paramArrayOfByte[(i5++)] & 0xFF));
            i8 -= 16;
          }
          if (i8 > 0) {
            localObject1[(i9++)] = ((short)((paramArrayOfByte[(i5++)] & 0xFF) << 8));
          }
          i3 += i2;
        }
      }
      else if ((localDataBuffer instanceof DataBufferInt))
      {
        localObject1 = ((DataBufferInt)localDataBuffer).getData();
        for (i7 = 0; i7 < m; i7++)
        {
          i8 = k;
          i9 = i3;
          while (i8 > 24)
          {
            localObject1[(i9++)] = ((paramArrayOfByte[(i5++)] & 0xFF) << 24 | (paramArrayOfByte[(i5++)] & 0xFF) << 16 | (paramArrayOfByte[(i5++)] & 0xFF) << 8 | paramArrayOfByte[(i5++)] & 0xFF);
            i8 -= 32;
          }
          i10 = 24;
          while (i8 > 0)
          {
            localObject1[i9] |= (paramArrayOfByte[(i5++)] & 0xFF) << i10;
            i10 -= 8;
            i8 -= 8;
          }
          i3 += i2;
        }
      }
    }
    else
    {
      int i6 = (k + 7) / 8;
      i7 = 0;
      Object localObject2;
      int i11;
      int i12;
      int i13;
      int i14;
      int i15;
      int i16;
      int i17;
      int i18;
      if ((localDataBuffer instanceof DataBufferByte))
      {
        localObject2 = ((DataBufferByte)localDataBuffer).getData();
        if ((i4 & 0x7) == 0)
        {
          for (i9 = 0; i9 < m; i9++)
          {
            System.arraycopy(paramArrayOfByte, i7, localObject2, i3, i6);
            i7 += i6;
            i3 += i2;
          }
        }
        else
        {
          i9 = i4 & 0x7;
          i10 = 8 - i9;
          i11 = 8 + i10;
          i12 = (byte)(255 << i10);
          i13 = (byte)(i12 ^ 0xFFFFFFFF);
          for (i14 = 0; i14 < m; i14++)
          {
            i15 = i3;
            for (i16 = k; i16 > 0; i16 -= 8)
            {
              i17 = paramArrayOfByte[(i5++)];
              if (i16 > i11)
              {
                localObject2[i15] = ((byte)(localObject2[i15] & i12 | (i17 & 0xFF) >>> i9));
                localObject2[(++i15)] = ((byte)((i17 & 0xFF) << i10));
              }
              else if (i16 > i10)
              {
                localObject2[i15] = ((byte)(localObject2[i15] & i12 | (i17 & 0xFF) >>> i9));
                i15++;
                localObject2[i15] = ((byte)(localObject2[i15] & i13 | (i17 & 0xFF) << i10));
              }
              else
              {
                i18 = (1 << i10 - i16) - 1;
                localObject2[i15] = ((byte)(localObject2[i15] & (i12 | i18) | (i17 & 0xFF) >>> i9 & (i18 ^ 0xFFFFFFFF)));
              }
            }
            i3 += i2;
          }
        }
      }
      else
      {
        int i19;
        int i20;
        int i21;
        if (((localDataBuffer instanceof DataBufferShort)) || ((localDataBuffer instanceof DataBufferUShort)))
        {
          localObject2 = (localDataBuffer instanceof DataBufferShort) ? ((DataBufferShort)localDataBuffer).getData() : ((DataBufferUShort)localDataBuffer).getData();
          i9 = i4 & 0x7;
          i10 = 8 - i9;
          i11 = 16 + i10;
          i12 = (short)(255 << i10 ^ 0xFFFFFFFF);
          i13 = (short)(65535 << i10);
          i14 = (short)(i13 ^ 0xFFFFFFFF);
          for (i15 = 0; i15 < m; i15++)
          {
            i16 = i4;
            i17 = k;
            i18 = 0;
            while (i18 < k)
            {
              i19 = i3 + (i16 >> 4);
              i20 = i16 & 0xF;
              i21 = paramArrayOfByte[(i5++)] & 0xFF;
              if (i20 <= 8)
              {
                if (i17 < 8) {
                  i21 &= 255 << 8 - i17;
                }
                localObject2[i19] = ((short)(localObject2[i19] & i12 | i21 << i10));
              }
              else if (i17 > i11)
              {
                localObject2[i19] = ((short)(localObject2[i19] & i13 | i21 >>> i9 & 0xFFFF));
                localObject2[(++i19)] = ((short)(i21 << i10 & 0xFFFF));
              }
              else if (i17 > i10)
              {
                localObject2[i19] = ((short)(localObject2[i19] & i13 | i21 >>> i9 & 0xFFFF));
                i19++;
                localObject2[i19] = ((short)(localObject2[i19] & i14 | i21 << i10 & 0xFFFF));
              }
              else
              {
                int i22 = (1 << i10 - i17) - 1;
                localObject2[i19] = ((short)(localObject2[i19] & (i13 | i22) | i21 >>> i9 & 0xFFFF & (i22 ^ 0xFFFFFFFF)));
              }
              i18 += 8;
              i16 += 8;
              i17 -= 8;
            }
            i3 += i2;
          }
        }
        else if ((localDataBuffer instanceof DataBufferInt))
        {
          localObject2 = ((DataBufferInt)localDataBuffer).getData();
          i9 = i4 & 0x7;
          i10 = 8 - i9;
          i11 = 32 + i10;
          i12 = -1 << i10;
          i13 = i12 ^ 0xFFFFFFFF;
          for (i14 = 0; i14 < m; i14++)
          {
            i15 = i4;
            i16 = k;
            i17 = 0;
            while (i17 < k)
            {
              i18 = i3 + (i15 >> 5);
              i19 = i15 & 0x1F;
              i20 = paramArrayOfByte[(i5++)] & 0xFF;
              if (i19 <= 24)
              {
                i21 = 24 - i19;
                if (i16 < 8) {
                  i20 &= 255 << 8 - i16;
                }
                localObject2[i18] = (localObject2[i18] & (255 << i21 ^ 0xFFFFFFFF) | i20 << i21);
              }
              else if (i16 > i11)
              {
                localObject2[i18] = (localObject2[i18] & i12 | i20 >>> i9);
                localObject2[(++i18)] = (i20 << i10);
              }
              else if (i16 > i10)
              {
                localObject2[i18] = (localObject2[i18] & i12 | i20 >>> i9);
                i18++;
                localObject2[i18] = (localObject2[i18] & i13 | i20 << i10);
              }
              else
              {
                i21 = (1 << i10 - i16) - 1;
                localObject2[i18] = (localObject2[i18] & (i12 | i21) | i20 >>> i9 & (i21 ^ 0xFFFFFFFF));
              }
              i17 += 8;
              i15 += 8;
              i16 -= 8;
            }
            i3 += i2;
          }
        }
      }
    }
  }
  
  public static void setUnpackedBinaryData(byte[] paramArrayOfByte, WritableRaster paramWritableRaster, Rectangle paramRectangle)
  {
    SampleModel localSampleModel = paramWritableRaster.getSampleModel();
    if (!isBinary(localSampleModel)) {
      throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
    }
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    DataBuffer localDataBuffer = paramWritableRaster.getDataBuffer();
    int n = i - paramWritableRaster.getSampleModelTranslateX();
    int i1 = j - paramWritableRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)localSampleModel;
    int i2 = localMultiPixelPackedSampleModel.getScanlineStride();
    int i3 = localDataBuffer.getOffset() + localMultiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = localMultiPixelPackedSampleModel.getBitOffset(n);
    int i5 = 0;
    Object localObject;
    int i6;
    int i7;
    int i8;
    if ((localDataBuffer instanceof DataBufferByte))
    {
      localObject = ((DataBufferByte)localDataBuffer).getData();
      for (i6 = 0; i6 < m; i6++)
      {
        i7 = i3 * 8 + i4;
        for (i8 = 0; i8 < k; i8++)
        {
          if (paramArrayOfByte[(i5++)] != 0)
          {
            int tmp180_179 = (i7 / 8);
            Object tmp180_173 = localObject;
            tmp180_173[tmp180_179] = ((byte)(tmp180_173[tmp180_179] | (byte)(1 << (7 - i7 & 0x7))));
          }
          i7++;
        }
        i3 += i2;
      }
    }
    else if (((localDataBuffer instanceof DataBufferShort)) || ((localDataBuffer instanceof DataBufferUShort)))
    {
      localObject = (localDataBuffer instanceof DataBufferShort) ? ((DataBufferShort)localDataBuffer).getData() : ((DataBufferUShort)localDataBuffer).getData();
      for (i6 = 0; i6 < m; i6++)
      {
        i7 = i3 * 16 + i4;
        for (i8 = 0; i8 < k; i8++)
        {
          if (paramArrayOfByte[(i5++)] != 0)
          {
            int tmp313_312 = (i7 / 16);
            Object tmp313_306 = localObject;
            tmp313_306[tmp313_312] = ((short)(tmp313_306[tmp313_312] | (short)(1 << 15 - i7 % 16)));
          }
          i7++;
        }
        i3 += i2;
      }
    }
    else if ((localDataBuffer instanceof DataBufferInt))
    {
      localObject = ((DataBufferInt)localDataBuffer).getData();
      for (i6 = 0; i6 < m; i6++)
      {
        i7 = i3 * 32 + i4;
        for (i8 = 0; i8 < k; i8++)
        {
          if (paramArrayOfByte[(i5++)] != 0) {
            localObject[(i7 / 32)] |= 1 << 31 - i7 % 32;
          }
          i7++;
        }
        i3 += i2;
      }
    }
  }
  
  public static boolean isBinary(SampleModel paramSampleModel)
  {
    return ((paramSampleModel instanceof MultiPixelPackedSampleModel)) && (((MultiPixelPackedSampleModel)paramSampleModel).getPixelBitStride() == 1) && (paramSampleModel.getNumBands() == 1);
  }
  
  public static ColorModel createColorModel(ColorSpace paramColorSpace, SampleModel paramSampleModel)
  {
    Object localObject = null;
    if (paramSampleModel == null) {
      throw new IllegalArgumentException(I18N.getString("ImageUtil1"));
    }
    int i = paramSampleModel.getNumBands();
    if ((i < 1) || (i > 4)) {
      return null;
    }
    int j = paramSampleModel.getDataType();
    boolean bool2;
    int i2;
    int i4;
    if ((paramSampleModel instanceof ComponentSampleModel))
    {
      if ((j < 0) || (j > 5)) {
        return null;
      }
      if (paramColorSpace == null) {
        paramColorSpace = i <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
      }
      boolean bool1 = (i == 2) || (i == 4);
      int m = bool1 ? 3 : 1;
      bool2 = false;
      i2 = DataBuffer.getDataTypeSize(j);
      int[] arrayOfInt2 = new int[i];
      for (i4 = 0; i4 < i; i4++) {
        arrayOfInt2[i4] = i2;
      }
      localObject = new ComponentColorModel(paramColorSpace, arrayOfInt2, bool1, bool2, m, j);
    }
    else if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      int[] arrayOfInt1 = localSinglePixelPackedSampleModel.getBitMasks();
      bool2 = false;
      i2 = 0;
      int i3 = 0;
      i4 = 0;
      i = arrayOfInt1.length;
      int i1;
      if (i <= 2)
      {
        i1 = i2 = i3 = arrayOfInt1[0];
        if (i == 2) {
          i4 = arrayOfInt1[1];
        }
      }
      else
      {
        i1 = arrayOfInt1[0];
        i2 = arrayOfInt1[1];
        i3 = arrayOfInt1[2];
        if (i == 4) {
          i4 = arrayOfInt1[3];
        }
      }
      int[] arrayOfInt3 = localSinglePixelPackedSampleModel.getSampleSize();
      int i5 = 0;
      for (int i6 = 0; i6 < arrayOfInt3.length; i6++) {
        i5 += arrayOfInt3[i6];
      }
      if (paramColorSpace == null) {
        paramColorSpace = ColorSpace.getInstance(1000);
      }
      localObject = new DirectColorModel(paramColorSpace, i5, i1, i2, i3, i4, false, paramSampleModel.getDataType());
    }
    else if ((paramSampleModel instanceof MultiPixelPackedSampleModel))
    {
      int k = ((MultiPixelPackedSampleModel)paramSampleModel).getPixelBitStride();
      int n = 1 << k;
      byte[] arrayOfByte = new byte[n];
      for (i2 = 0; i2 < n; i2++) {
        arrayOfByte[i2] = ((byte)(255 * i2 / (n - 1)));
      }
      localObject = new IndexColorModel(k, n, arrayOfByte, arrayOfByte, arrayOfByte);
    }
    return (ColorModel)localObject;
  }
  
  public static int getElementSize(SampleModel paramSampleModel)
  {
    int i = DataBuffer.getDataTypeSize(paramSampleModel.getDataType());
    if ((paramSampleModel instanceof MultiPixelPackedSampleModel))
    {
      MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramSampleModel;
      return localMultiPixelPackedSampleModel.getSampleSize(0) * localMultiPixelPackedSampleModel.getNumBands();
    }
    if ((paramSampleModel instanceof ComponentSampleModel)) {
      return paramSampleModel.getNumBands() * i;
    }
    if ((paramSampleModel instanceof SinglePixelPackedSampleModel)) {
      return i;
    }
    return i * paramSampleModel.getNumBands();
  }
  
  public static long getTileSize(SampleModel paramSampleModel)
  {
    int i = DataBuffer.getDataTypeSize(paramSampleModel.getDataType());
    Object localObject;
    if ((paramSampleModel instanceof MultiPixelPackedSampleModel))
    {
      localObject = (MultiPixelPackedSampleModel)paramSampleModel;
      return (((MultiPixelPackedSampleModel)localObject).getScanlineStride() * ((MultiPixelPackedSampleModel)localObject).getHeight() + (((MultiPixelPackedSampleModel)localObject).getDataBitOffset() + i - 1) / i) * ((i + 7) / 8);
    }
    if ((paramSampleModel instanceof ComponentSampleModel))
    {
      localObject = (ComponentSampleModel)paramSampleModel;
      int[] arrayOfInt1 = ((ComponentSampleModel)localObject).getBandOffsets();
      int j = arrayOfInt1[0];
      for (int k = 1; k < arrayOfInt1.length; k++) {
        j = Math.max(j, arrayOfInt1[k]);
      }
      long l2 = 0L;
      int m = ((ComponentSampleModel)localObject).getPixelStride();
      int n = ((ComponentSampleModel)localObject).getScanlineStride();
      if (j >= 0) {
        l2 += j + 1;
      }
      if (m > 0) {
        l2 += m * (paramSampleModel.getWidth() - 1);
      }
      if (n > 0) {
        l2 += n * (paramSampleModel.getHeight() - 1);
      }
      int[] arrayOfInt2 = ((ComponentSampleModel)localObject).getBankIndices();
      j = arrayOfInt2[0];
      for (int i1 = 1; i1 < arrayOfInt2.length; i1++) {
        j = Math.max(j, arrayOfInt2[i1]);
      }
      return l2 * (j + 1) * ((i + 7) / 8);
    }
    if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      localObject = (SinglePixelPackedSampleModel)paramSampleModel;
      long l1 = ((SinglePixelPackedSampleModel)localObject).getScanlineStride() * (((SinglePixelPackedSampleModel)localObject).getHeight() - 1) + ((SinglePixelPackedSampleModel)localObject).getWidth();
      return l1 * ((i + 7) / 8);
    }
    return 0L;
  }
  
  public static long getBandSize(SampleModel paramSampleModel)
  {
    int i = DataBuffer.getDataTypeSize(paramSampleModel.getDataType());
    if ((paramSampleModel instanceof ComponentSampleModel))
    {
      ComponentSampleModel localComponentSampleModel = (ComponentSampleModel)paramSampleModel;
      int j = localComponentSampleModel.getPixelStride();
      int k = localComponentSampleModel.getScanlineStride();
      long l = Math.min(j, k);
      if (j > 0) {
        l += j * (paramSampleModel.getWidth() - 1);
      }
      if (k > 0) {
        l += k * (paramSampleModel.getHeight() - 1);
      }
      return l * ((i + 7) / 8);
    }
    return getTileSize(paramSampleModel);
  }
  
  public static boolean isIndicesForGrayscale(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    if ((paramArrayOfByte1.length != paramArrayOfByte2.length) || (paramArrayOfByte1.length != paramArrayOfByte3.length)) {
      return false;
    }
    int i = paramArrayOfByte1.length;
    if (i != 256) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      int k = (byte)j;
      if ((paramArrayOfByte1[j] != k) || (paramArrayOfByte2[j] != k) || (paramArrayOfByte3[j] != k)) {
        return false;
      }
    }
    return true;
  }
  
  public static String convertObjectToString(Object paramObject)
  {
    if (paramObject == null) {
      return "";
    }
    String str = "";
    Object localObject;
    int i;
    if ((paramObject instanceof byte[]))
    {
      localObject = (byte[])paramObject;
      for (i = 0; i < localObject.length; i++) {
        str = str + localObject[i] + " ";
      }
      return str;
    }
    if ((paramObject instanceof int[]))
    {
      localObject = (int[])paramObject;
      for (i = 0; i < localObject.length; i++) {
        str = str + localObject[i] + " ";
      }
      return str;
    }
    if ((paramObject instanceof short[]))
    {
      localObject = (short[])paramObject;
      for (i = 0; i < localObject.length; i++) {
        str = str + localObject[i] + " ";
      }
      return str;
    }
    return paramObject.toString();
  }
  
  public static final void canEncodeImage(ImageWriter paramImageWriter, ImageTypeSpecifier paramImageTypeSpecifier)
    throws IIOException
  {
    ImageWriterSpi localImageWriterSpi = paramImageWriter.getOriginatingProvider();
    if ((paramImageTypeSpecifier != null) && (localImageWriterSpi != null) && (!localImageWriterSpi.canEncodeImage(paramImageTypeSpecifier))) {
      throw new IIOException(I18N.getString("ImageUtil2") + " " + paramImageWriter.getClass().getName());
    }
  }
  
  public static final void canEncodeImage(ImageWriter paramImageWriter, ColorModel paramColorModel, SampleModel paramSampleModel)
    throws IIOException
  {
    ImageTypeSpecifier localImageTypeSpecifier = null;
    if ((paramColorModel != null) && (paramSampleModel != null)) {
      localImageTypeSpecifier = new ImageTypeSpecifier(paramColorModel, paramSampleModel);
    }
    canEncodeImage(paramImageWriter, localImageTypeSpecifier);
  }
  
  public static final boolean imageIsContiguous(RenderedImage paramRenderedImage)
  {
    Object localObject;
    SampleModel localSampleModel;
    if ((paramRenderedImage instanceof BufferedImage))
    {
      localObject = ((BufferedImage)paramRenderedImage).getRaster();
      localSampleModel = ((WritableRaster)localObject).getSampleModel();
    }
    else
    {
      localSampleModel = paramRenderedImage.getSampleModel();
    }
    if ((localSampleModel instanceof ComponentSampleModel))
    {
      localObject = (ComponentSampleModel)localSampleModel;
      if (((ComponentSampleModel)localObject).getPixelStride() != ((ComponentSampleModel)localObject).getNumBands()) {
        return false;
      }
      int[] arrayOfInt1 = ((ComponentSampleModel)localObject).getBandOffsets();
      for (int i = 0; i < arrayOfInt1.length; i++) {
        if (arrayOfInt1[i] != i) {
          return false;
        }
      }
      int[] arrayOfInt2 = ((ComponentSampleModel)localObject).getBankIndices();
      for (int j = 0; j < arrayOfInt1.length; j++) {
        if (arrayOfInt2[j] != 0) {
          return false;
        }
      }
      return true;
    }
    return isBinary(localSampleModel);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\ImageUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */