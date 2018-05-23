package sun.java2d.cmm.kcms;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class CMMImageLayout
{
  private static final int typeBase = 256;
  public static final int typeComponentUByte = 256;
  public static final int typeComponentUShort12 = 257;
  public static final int typeComponentUShort = 258;
  public static final int typePixelUByte = 259;
  public static final int typePixelUShort12 = 260;
  public static final int typePixelUShort = 261;
  public static final int typeShort555 = 262;
  public static final int typeShort565 = 263;
  public static final int typeInt101010 = 264;
  public static final int typeIntRGBPacked = 265;
  public int Type;
  public int NumCols;
  public int NumRows;
  public int OffsetColumn;
  public int OffsetRow;
  public int NumChannels;
  public final boolean hasAlpha;
  public Object[] chanData;
  public int[] DataOffsets;
  public int[] sampleInfo;
  private int[] dataArrayLength;
  private static final int MAX_NumChannels = 9;
  
  public CMMImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws CMMImageLayout.ImageLayoutException
  {
    Type = 256;
    chanData = new Object[paramInt2];
    DataOffsets = new int[paramInt2];
    dataArrayLength = new int[paramInt2];
    NumCols = paramInt1;
    NumRows = 1;
    OffsetColumn = paramInt2;
    OffsetRow = (NumCols * OffsetColumn);
    NumChannels = paramInt2;
    for (int i = 0; i < paramInt2; i++)
    {
      chanData[i] = paramArrayOfByte;
      DataOffsets[i] = i;
      dataArrayLength[i] = paramArrayOfByte.length;
    }
    hasAlpha = false;
    verify();
  }
  
  public CMMImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws CMMImageLayout.ImageLayoutException
  {
    Type = 258;
    chanData = new Object[paramInt2];
    DataOffsets = new int[paramInt2];
    dataArrayLength = new int[paramInt2];
    NumCols = paramInt1;
    NumRows = 1;
    OffsetColumn = safeMult(2, paramInt2);
    OffsetRow = (NumCols * OffsetColumn);
    NumChannels = paramInt2;
    for (int i = 0; i < paramInt2; i++)
    {
      chanData[i] = paramArrayOfShort;
      DataOffsets[i] = (i * 2);
      dataArrayLength[i] = (2 * paramArrayOfShort.length);
    }
    hasAlpha = false;
    verify();
  }
  
  public CMMImageLayout(BufferedImage paramBufferedImage)
    throws CMMImageLayout.ImageLayoutException
  {
    Type = paramBufferedImage.getType();
    NumCols = paramBufferedImage.getWidth();
    NumRows = paramBufferedImage.getHeight();
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    int i;
    Object localObject1;
    int k;
    int j;
    Object localObject2;
    int m;
    switch (Type)
    {
    case 1: 
    case 2: 
    case 4: 
      NumChannels = 3;
      hasAlpha = (Type == 2);
      i = hasAlpha ? 4 : 3;
      chanData = new Object[i];
      DataOffsets = new int[i];
      dataArrayLength = new int[i];
      sampleInfo = new int[i];
      OffsetColumn = 4;
      if ((localWritableRaster instanceof IntegerComponentRaster))
      {
        localObject1 = (IntegerComponentRaster)localWritableRaster;
        k = safeMult(4, ((IntegerComponentRaster)localObject1).getPixelStride());
        if (k != OffsetColumn) {
          throw new ImageLayoutException("Incompatible raster type");
        }
        OffsetRow = safeMult(4, ((IntegerComponentRaster)localObject1).getScanlineStride());
        j = safeMult(4, ((IntegerComponentRaster)localObject1).getDataOffset(0));
        localObject2 = ((IntegerComponentRaster)localObject1).getDataStorage();
        for (m = 0; m < 3; m++)
        {
          chanData[m] = localObject2;
          DataOffsets[m] = j;
          dataArrayLength[m] = (4 * localObject2.length);
          if (Type == 4) {
            sampleInfo[m] = (3 - m);
          } else {
            sampleInfo[m] = (m + 1);
          }
        }
        if (hasAlpha)
        {
          chanData[3] = localObject2;
          DataOffsets[3] = j;
          dataArrayLength[3] = (4 * localObject2.length);
          sampleInfo[3] = 0;
        }
      }
      else
      {
        throw new ImageLayoutException("Incompatible raster type");
      }
      break;
    case 5: 
    case 6: 
      NumChannels = 3;
      hasAlpha = (Type == 6);
      if (hasAlpha)
      {
        OffsetColumn = 4;
        i = 4;
      }
      else
      {
        OffsetColumn = 3;
        i = 3;
      }
      chanData = new Object[i];
      DataOffsets = new int[i];
      dataArrayLength = new int[i];
      if ((localWritableRaster instanceof ByteComponentRaster))
      {
        localObject1 = (ByteComponentRaster)localWritableRaster;
        k = ((ByteComponentRaster)localObject1).getPixelStride();
        if (k != OffsetColumn) {
          throw new ImageLayoutException("Incompatible raster type");
        }
        OffsetRow = ((ByteComponentRaster)localObject1).getScanlineStride();
        j = ((ByteComponentRaster)localObject1).getDataOffset(0);
        localObject2 = ((ByteComponentRaster)localObject1).getDataStorage();
        for (m = 0; m < i; m++)
        {
          chanData[m] = localObject2;
          DataOffsets[m] = (j - m);
          dataArrayLength[m] = localObject2.length;
        }
      }
      else
      {
        throw new ImageLayoutException("Incompatible raster type");
      }
      break;
    case 10: 
      Type = 256;
      NumChannels = 1;
      hasAlpha = false;
      chanData = new Object[1];
      DataOffsets = new int[1];
      dataArrayLength = new int[1];
      OffsetColumn = 1;
      if ((localWritableRaster instanceof ByteComponentRaster))
      {
        localObject1 = (ByteComponentRaster)localWritableRaster;
        k = ((ByteComponentRaster)localObject1).getPixelStride();
        if (k != OffsetColumn) {
          throw new ImageLayoutException("Incompatible raster type");
        }
        OffsetRow = ((ByteComponentRaster)localObject1).getScanlineStride();
        localObject2 = ((ByteComponentRaster)localObject1).getDataStorage();
        chanData[0] = localObject2;
        dataArrayLength[0] = localObject2.length;
        DataOffsets[0] = ((ByteComponentRaster)localObject1).getDataOffset(0);
      }
      else
      {
        throw new ImageLayoutException("Incompatible raster type");
      }
      break;
    case 11: 
      Type = 258;
      NumChannels = 1;
      hasAlpha = false;
      chanData = new Object[1];
      DataOffsets = new int[1];
      dataArrayLength = new int[1];
      OffsetColumn = 2;
      if ((localWritableRaster instanceof ShortComponentRaster))
      {
        localObject1 = (ShortComponentRaster)localWritableRaster;
        k = safeMult(2, ((ShortComponentRaster)localObject1).getPixelStride());
        if (k != OffsetColumn) {
          throw new ImageLayoutException("Incompatible raster type");
        }
        OffsetRow = safeMult(2, ((ShortComponentRaster)localObject1).getScanlineStride());
        DataOffsets[0] = safeMult(2, ((ShortComponentRaster)localObject1).getDataOffset(0));
        localObject2 = ((ShortComponentRaster)localObject1).getDataStorage();
        chanData[0] = localObject2;
        dataArrayLength[0] = (2 * localObject2.length);
      }
      else
      {
        throw new ImageLayoutException("Incompatible raster type");
      }
      break;
    case 3: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
    }
    verify();
  }
  
  public CMMImageLayout(BufferedImage paramBufferedImage, SinglePixelPackedSampleModel paramSinglePixelPackedSampleModel, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws CMMImageLayout.ImageLayoutException
  {
    Type = 265;
    NumChannels = 3;
    NumCols = paramBufferedImage.getWidth();
    NumRows = paramBufferedImage.getHeight();
    hasAlpha = (paramInt4 >= 0);
    int i = hasAlpha ? 4 : 3;
    chanData = new Object[i];
    DataOffsets = new int[i];
    dataArrayLength = new int[i];
    sampleInfo = new int[i];
    OffsetColumn = 4;
    int j = paramSinglePixelPackedSampleModel.getScanlineStride();
    OffsetRow = safeMult(4, j);
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    DataBufferInt localDataBufferInt = (DataBufferInt)localWritableRaster.getDataBuffer();
    int k = localWritableRaster.getSampleModelTranslateX();
    int m = localWritableRaster.getSampleModelTranslateY();
    int n = safeMult(m, j);
    int i1 = safeMult(4, k);
    i1 = safeAdd(i1, n);
    int i2 = safeAdd(localDataBufferInt.getOffset(), -i1);
    int[] arrayOfInt = localDataBufferInt.getData();
    for (int i3 = 0; i3 < i; i3++)
    {
      chanData[i3] = arrayOfInt;
      DataOffsets[i3] = i2;
      dataArrayLength[i3] = (arrayOfInt.length * 4);
    }
    sampleInfo[0] = paramInt1;
    sampleInfo[1] = paramInt2;
    sampleInfo[2] = paramInt3;
    if (hasAlpha) {
      sampleInfo[3] = paramInt4;
    }
    verify();
  }
  
  public CMMImageLayout(BufferedImage paramBufferedImage, ComponentSampleModel paramComponentSampleModel)
    throws CMMImageLayout.ImageLayoutException
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    int i = localColorModel.getNumColorComponents();
    if ((i < 0) || (i > 9)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    hasAlpha = localColorModel.hasAlpha();
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
    int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
    NumChannels = i;
    NumCols = paramBufferedImage.getWidth();
    NumRows = paramBufferedImage.getHeight();
    if (hasAlpha) {
      i++;
    }
    chanData = new Object[i];
    DataOffsets = new int[i];
    dataArrayLength = new int[i];
    int j = localWritableRaster.getSampleModelTranslateY();
    int k = localWritableRaster.getSampleModelTranslateX();
    int m = paramComponentSampleModel.getScanlineStride();
    int n = paramComponentSampleModel.getPixelStride();
    int i1 = safeMult(m, j);
    int i2 = safeMult(n, k);
    i2 = safeAdd(i2, i1);
    Object localObject1;
    int[] arrayOfInt3;
    int i3;
    Object localObject2;
    int i4;
    switch (paramComponentSampleModel.getDataType())
    {
    case 0: 
      Type = 256;
      OffsetColumn = n;
      OffsetRow = m;
      localObject1 = (DataBufferByte)localWritableRaster.getDataBuffer();
      arrayOfInt3 = ((DataBufferByte)localObject1).getOffsets();
      for (i3 = 0; i3 < i; i3++)
      {
        localObject2 = ((DataBufferByte)localObject1).getData(arrayOfInt1[i3]);
        chanData[i3] = localObject2;
        dataArrayLength[i3] = localObject2.length;
        i4 = safeAdd(arrayOfInt3[arrayOfInt1[i3]], -i2);
        i4 = safeAdd(i4, arrayOfInt2[i3]);
        DataOffsets[i3] = i4;
      }
      break;
    case 1: 
      Type = 258;
      OffsetColumn = safeMult(2, n);
      OffsetRow = safeMult(2, m);
      localObject1 = (DataBufferUShort)localWritableRaster.getDataBuffer();
      arrayOfInt3 = ((DataBufferUShort)localObject1).getOffsets();
      for (i3 = 0; i3 < i; i3++)
      {
        localObject2 = ((DataBufferUShort)localObject1).getData(arrayOfInt1[i3]);
        chanData[i3] = localObject2;
        dataArrayLength[i3] = (localObject2.length * 2);
        i4 = safeAdd(arrayOfInt3[arrayOfInt1[i3]], -i2);
        i4 = safeAdd(i4, arrayOfInt2[i3]);
        DataOffsets[i3] = safeMult(2, i4);
      }
      break;
    default: 
      throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
    }
    verify();
  }
  
  public CMMImageLayout(Raster paramRaster, ComponentSampleModel paramComponentSampleModel)
    throws CMMImageLayout.ImageLayoutException
  {
    int i = paramRaster.getNumBands();
    if ((i < 0) || (i > 9)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
    int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
    NumChannels = i;
    NumCols = paramRaster.getWidth();
    NumRows = paramRaster.getHeight();
    hasAlpha = false;
    chanData = new Object[i];
    DataOffsets = new int[i];
    dataArrayLength = new int[i];
    int j = paramComponentSampleModel.getScanlineStride();
    int k = paramComponentSampleModel.getPixelStride();
    int m = paramRaster.getMinX();
    int n = paramRaster.getMinY();
    int i1 = paramRaster.getSampleModelTranslateX();
    int i2 = paramRaster.getSampleModelTranslateY();
    int i3 = safeAdd(n, -i2);
    i3 = safeMult(i3, j);
    int i4 = safeAdd(m, -i1);
    i4 = safeMult(i4, k);
    i4 = safeAdd(i4, i3);
    Object localObject1;
    int[] arrayOfInt3;
    int i5;
    Object localObject2;
    int i6;
    switch (paramComponentSampleModel.getDataType())
    {
    case 0: 
      Type = 256;
      OffsetColumn = k;
      OffsetRow = j;
      localObject1 = (DataBufferByte)paramRaster.getDataBuffer();
      arrayOfInt3 = ((DataBufferByte)localObject1).getOffsets();
      for (i5 = 0; i5 < i; i5++)
      {
        localObject2 = ((DataBufferByte)localObject1).getData(arrayOfInt1[i5]);
        chanData[i5] = localObject2;
        dataArrayLength[i5] = localObject2.length;
        i6 = safeAdd(arrayOfInt3[arrayOfInt1[i5]], i4);
        DataOffsets[i5] = safeAdd(i6, arrayOfInt2[i5]);
      }
      break;
    case 1: 
      Type = 258;
      OffsetColumn = safeMult(2, k);
      OffsetRow = safeMult(2, j);
      localObject1 = (DataBufferUShort)paramRaster.getDataBuffer();
      arrayOfInt3 = ((DataBufferUShort)localObject1).getOffsets();
      for (i5 = 0; i5 < i; i5++)
      {
        localObject2 = ((DataBufferUShort)localObject1).getData(arrayOfInt1[i5]);
        chanData[i5] = localObject2;
        dataArrayLength[i5] = (localObject2.length * 2);
        i6 = safeAdd(arrayOfInt3[arrayOfInt1[i5]], i4);
        i6 = safeAdd(i6, arrayOfInt2[i5]);
        DataOffsets[i5] = safeMult(2, i6);
      }
      break;
    default: 
      throw new IllegalArgumentException("CMMImageLayout - bad image type passed to constructor");
    }
    verify();
  }
  
  private final void verify()
    throws CMMImageLayout.ImageLayoutException
  {
    int i = safeMult(OffsetRow, NumRows - 1);
    int j = safeMult(OffsetColumn, NumCols - 1);
    i = safeAdd(i, j);
    int k = NumChannels;
    if (hasAlpha) {
      k++;
    }
    for (int m = 0; m < k; m++)
    {
      int n = DataOffsets[m];
      if ((n < 0) || (n >= dataArrayLength[m])) {
        throw new ImageLayoutException("Invalid image layout");
      }
      n = safeAdd(n, i);
      if ((n < 0) || (n >= dataArrayLength[m])) {
        throw new ImageLayoutException("Invalid image layout");
      }
    }
  }
  
  static int safeAdd(int paramInt1, int paramInt2)
    throws CMMImageLayout.ImageLayoutException
  {
    long l = paramInt1;
    l += paramInt2;
    if ((l < -2147483648L) || (l > 2147483647L)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    return (int)l;
  }
  
  static int safeMult(int paramInt1, int paramInt2)
    throws CMMImageLayout.ImageLayoutException
  {
    long l = paramInt1;
    l *= paramInt2;
    if ((l < -2147483648L) || (l > 2147483647L)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    return (int)l;
  }
  
  public static class ImageLayoutException
    extends Exception
  {
    public ImageLayoutException(String paramString)
    {
      super();
    }
    
    public ImageLayoutException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\kcms\CMMImageLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */