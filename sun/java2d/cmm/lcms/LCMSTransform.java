package sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.ProfileDeferralMgr;

public class LCMSTransform
  implements ColorTransform
{
  long ID;
  private int inFormatter = 0;
  private boolean isInIntPacked = false;
  private int outFormatter = 0;
  private boolean isOutIntPacked = false;
  ICC_Profile[] profiles;
  LCMSProfile[] lcmsProfiles;
  int renderType;
  int transformType;
  private int numInComponents = -1;
  private int numOutComponents = -1;
  private Object disposerReferent = new Object();
  
  public LCMSTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
  {
    profiles = new ICC_Profile[1];
    profiles[0] = paramICC_Profile;
    lcmsProfiles = new LCMSProfile[1];
    lcmsProfiles[0] = LCMS.getProfileID(paramICC_Profile);
    renderType = (paramInt1 == -1 ? 0 : paramInt1);
    transformType = paramInt2;
    numInComponents = profiles[0].getNumComponents();
    numOutComponents = profiles[(profiles.length - 1)].getNumComponents();
  }
  
  public LCMSTransform(ColorTransform[] paramArrayOfColorTransform)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfColorTransform.length; j++) {
      i += profiles.length;
    }
    profiles = new ICC_Profile[i];
    lcmsProfiles = new LCMSProfile[i];
    j = 0;
    for (int k = 0; k < paramArrayOfColorTransform.length; k++)
    {
      LCMSTransform localLCMSTransform = (LCMSTransform)paramArrayOfColorTransform[k];
      System.arraycopy(profiles, 0, profiles, j, profiles.length);
      System.arraycopy(lcmsProfiles, 0, lcmsProfiles, j, lcmsProfiles.length);
      j += profiles.length;
    }
    renderType = 0renderType;
    numInComponents = profiles[0].getNumComponents();
    numOutComponents = profiles[(profiles.length - 1)].getNumComponents();
  }
  
  public int getNumInComponents()
  {
    return numInComponents;
  }
  
  public int getNumOutComponents()
  {
    return numOutComponents;
  }
  
  private synchronized void doTransform(LCMSImageLayout paramLCMSImageLayout1, LCMSImageLayout paramLCMSImageLayout2)
  {
    if ((ID == 0L) || (inFormatter != pixelType) || (isInIntPacked != isIntPacked) || (outFormatter != pixelType) || (isOutIntPacked != isIntPacked))
    {
      if (ID != 0L) {
        disposerReferent = new Object();
      }
      inFormatter = pixelType;
      isInIntPacked = isIntPacked;
      outFormatter = pixelType;
      isOutIntPacked = isIntPacked;
      ID = LCMS.createTransform(lcmsProfiles, renderType, inFormatter, isInIntPacked, outFormatter, isOutIntPacked, disposerReferent);
    }
    LCMS.colorConvert(this, paramLCMSImageLayout1, paramLCMSImageLayout2);
  }
  
  public void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    LCMSImageLayout localLCMSImageLayout2;
    LCMSImageLayout localLCMSImageLayout1;
    try
    {
      if (!paramBufferedImage2.getColorModel().hasAlpha())
      {
        localLCMSImageLayout2 = LCMSImageLayout.createImageLayout(paramBufferedImage2);
        if (localLCMSImageLayout2 != null)
        {
          localLCMSImageLayout1 = LCMSImageLayout.createImageLayout(paramBufferedImage1);
          if (localLCMSImageLayout1 != null)
          {
            doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
            return;
          }
        }
      }
    }
    catch (LCMSImageLayout.ImageLayoutException localImageLayoutException1)
    {
      throw new CMMException("Unable to convert images");
    }
    WritableRaster localWritableRaster1 = paramBufferedImage1.getRaster();
    WritableRaster localWritableRaster2 = paramBufferedImage2.getRaster();
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    ColorModel localColorModel2 = paramBufferedImage2.getColorModel();
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    int k = localColorModel1.getNumColorComponents();
    int m = localColorModel2.getNumColorComponents();
    int n = 8;
    float f = 255.0F;
    for (int i1 = 0; i1 < k; i1++) {
      if (localColorModel1.getComponentSize(i1) > 8)
      {
        n = 16;
        f = 65535.0F;
      }
    }
    for (i1 = 0; i1 < m; i1++) {
      if (localColorModel2.getComponentSize(i1) > 8)
      {
        n = 16;
        f = 65535.0F;
      }
    }
    float[] arrayOfFloat1 = new float[k];
    float[] arrayOfFloat2 = new float[k];
    ColorSpace localColorSpace = localColorModel1.getColorSpace();
    for (int i2 = 0; i2 < k; i2++)
    {
      arrayOfFloat1[i2] = localColorSpace.getMinValue(i2);
      arrayOfFloat2[i2] = (f / (localColorSpace.getMaxValue(i2) - arrayOfFloat1[i2]));
    }
    localColorSpace = localColorModel2.getColorSpace();
    float[] arrayOfFloat3 = new float[m];
    float[] arrayOfFloat4 = new float[m];
    for (int i3 = 0; i3 < m; i3++)
    {
      arrayOfFloat3[i3] = localColorSpace.getMinValue(i3);
      arrayOfFloat4[i3] = ((localColorSpace.getMaxValue(i3) - arrayOfFloat3[i3]) / f);
    }
    boolean bool = localColorModel2.hasAlpha();
    int i4 = (localColorModel1.hasAlpha()) && (bool) ? 1 : 0;
    float[] arrayOfFloat5;
    if (bool) {
      arrayOfFloat5 = new float[m + 1];
    } else {
      arrayOfFloat5 = new float[m];
    }
    Object localObject1;
    Object localObject2;
    float[] arrayOfFloat7;
    Object localObject3;
    float[] arrayOfFloat6;
    int i5;
    int i8;
    int i9;
    if (n == 8)
    {
      localObject1 = new byte[i * k];
      localObject2 = new byte[i * m];
      arrayOfFloat7 = null;
      if (i4 != 0) {
        arrayOfFloat7 = new float[i];
      }
      try
      {
        localLCMSImageLayout1 = new LCMSImageLayout((byte[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents());
        localLCMSImageLayout2 = new LCMSImageLayout((byte[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
      }
      catch (LCMSImageLayout.ImageLayoutException localImageLayoutException2)
      {
        throw new CMMException("Unable to convert images");
      }
      for (int i6 = 0; i6 < j; i6++)
      {
        localObject3 = null;
        arrayOfFloat6 = null;
        i5 = 0;
        for (i8 = 0; i8 < i; i8++)
        {
          localObject3 = localWritableRaster1.getDataElements(i8, i6, localObject3);
          arrayOfFloat6 = localColorModel1.getNormalizedComponents(localObject3, arrayOfFloat6, 0);
          for (i9 = 0; i9 < k; i9++) {
            localObject1[(i5++)] = ((byte)(int)((arrayOfFloat6[i9] - arrayOfFloat1[i9]) * arrayOfFloat2[i9] + 0.5F));
          }
          if (i4 != 0) {
            arrayOfFloat7[i8] = arrayOfFloat6[k];
          }
        }
        doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
        localObject3 = null;
        i5 = 0;
        for (i8 = 0; i8 < i; i8++)
        {
          for (i9 = 0; i9 < m; i9++) {
            arrayOfFloat5[i9] = ((localObject2[(i5++)] & 0xFF) * arrayOfFloat4[i9] + arrayOfFloat3[i9]);
          }
          if (i4 != 0) {
            arrayOfFloat5[m] = arrayOfFloat7[i8];
          } else if (bool) {
            arrayOfFloat5[m] = 1.0F;
          }
          localObject3 = localColorModel2.getDataElements(arrayOfFloat5, 0, localObject3);
          localWritableRaster2.setDataElements(i8, i6, localObject3);
        }
      }
    }
    else
    {
      localObject1 = new short[i * k];
      localObject2 = new short[i * m];
      arrayOfFloat7 = null;
      if (i4 != 0) {
        arrayOfFloat7 = new float[i];
      }
      try
      {
        localLCMSImageLayout1 = new LCMSImageLayout((short[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
        localLCMSImageLayout2 = new LCMSImageLayout((short[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
      }
      catch (LCMSImageLayout.ImageLayoutException localImageLayoutException3)
      {
        throw new CMMException("Unable to convert images");
      }
      for (int i7 = 0; i7 < j; i7++)
      {
        localObject3 = null;
        arrayOfFloat6 = null;
        i5 = 0;
        for (i8 = 0; i8 < i; i8++)
        {
          localObject3 = localWritableRaster1.getDataElements(i8, i7, localObject3);
          arrayOfFloat6 = localColorModel1.getNormalizedComponents(localObject3, arrayOfFloat6, 0);
          for (i9 = 0; i9 < k; i9++) {
            localObject1[(i5++)] = ((short)(int)((arrayOfFloat6[i9] - arrayOfFloat1[i9]) * arrayOfFloat2[i9] + 0.5F));
          }
          if (i4 != 0) {
            arrayOfFloat7[i8] = arrayOfFloat6[k];
          }
        }
        doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
        localObject3 = null;
        i5 = 0;
        for (i8 = 0; i8 < i; i8++)
        {
          for (i9 = 0; i9 < m; i9++) {
            arrayOfFloat5[i9] = ((localObject2[(i5++)] & 0xFFFF) * arrayOfFloat4[i9] + arrayOfFloat3[i9]);
          }
          if (i4 != 0) {
            arrayOfFloat5[m] = arrayOfFloat7[i8];
          } else if (bool) {
            arrayOfFloat5[m] = 1.0F;
          }
          localObject3 = localColorModel2.getDataElements(arrayOfFloat5, 0, localObject3);
          localWritableRaster2.setDataElements(i8, i7, localObject3);
        }
      }
    }
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4)
  {
    SampleModel localSampleModel1 = paramRaster.getSampleModel();
    SampleModel localSampleModel2 = paramWritableRaster.getSampleModel();
    int i = paramRaster.getTransferType();
    int j = paramWritableRaster.getTransferType();
    int k;
    if ((i == 4) || (i == 5)) {
      k = 1;
    } else {
      k = 0;
    }
    int m;
    if ((j == 4) || (j == 5)) {
      m = 1;
    } else {
      m = 0;
    }
    int n = paramRaster.getWidth();
    int i1 = paramRaster.getHeight();
    int i2 = paramRaster.getNumBands();
    int i3 = paramWritableRaster.getNumBands();
    float[] arrayOfFloat1 = new float[i2];
    float[] arrayOfFloat2 = new float[i3];
    float[] arrayOfFloat3 = new float[i2];
    float[] arrayOfFloat4 = new float[i3];
    for (int i4 = 0; i4 < i2; i4++) {
      if (k != 0)
      {
        arrayOfFloat1[i4] = (65535.0F / (paramArrayOfFloat2[i4] - paramArrayOfFloat1[i4]));
        arrayOfFloat3[i4] = paramArrayOfFloat1[i4];
      }
      else
      {
        if (i == 2) {
          arrayOfFloat1[i4] = 2.0000305F;
        } else {
          arrayOfFloat1[i4] = (65535.0F / ((1 << localSampleModel1.getSampleSize(i4)) - 1));
        }
        arrayOfFloat3[i4] = 0.0F;
      }
    }
    for (i4 = 0; i4 < i3; i4++) {
      if (m != 0)
      {
        arrayOfFloat2[i4] = ((paramArrayOfFloat4[i4] - paramArrayOfFloat3[i4]) / 65535.0F);
        arrayOfFloat4[i4] = paramArrayOfFloat3[i4];
      }
      else
      {
        if (j == 2) {
          arrayOfFloat2[i4] = 0.49999237F;
        } else {
          arrayOfFloat2[i4] = (((1 << localSampleModel2.getSampleSize(i4)) - 1) / 65535.0F);
        }
        arrayOfFloat4[i4] = 0.0F;
      }
    }
    i4 = paramRaster.getMinY();
    int i5 = paramWritableRaster.getMinY();
    short[] arrayOfShort1 = new short[n * i2];
    short[] arrayOfShort2 = new short[n * i3];
    LCMSImageLayout localLCMSImageLayout1;
    LCMSImageLayout localLCMSImageLayout2;
    try
    {
      localLCMSImageLayout1 = new LCMSImageLayout(arrayOfShort1, arrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
      localLCMSImageLayout2 = new LCMSImageLayout(arrayOfShort2, arrayOfShort2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
    }
    catch (LCMSImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert rasters");
    }
    int i9 = 0;
    while (i9 < i1)
    {
      int i6 = paramRaster.getMinX();
      int i8 = 0;
      int i10 = 0;
      int i11;
      float f;
      while (i10 < n)
      {
        for (i11 = 0; i11 < i2; i11++)
        {
          f = paramRaster.getSampleFloat(i6, i4, i11);
          arrayOfShort1[(i8++)] = ((short)(int)((f - arrayOfFloat3[i11]) * arrayOfFloat1[i11] + 0.5F));
        }
        i10++;
        i6++;
      }
      doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
      int i7 = paramWritableRaster.getMinX();
      i8 = 0;
      i10 = 0;
      while (i10 < n)
      {
        for (i11 = 0; i11 < i3; i11++)
        {
          f = (arrayOfShort2[(i8++)] & 0xFFFF) * arrayOfFloat2[i11] + arrayOfFloat4[i11];
          paramWritableRaster.setSample(i7, i5, i11, f);
        }
        i10++;
        i7++;
      }
      i9++;
      i4++;
      i5++;
    }
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    LCMSImageLayout localLCMSImageLayout2 = LCMSImageLayout.createImageLayout(paramWritableRaster);
    LCMSImageLayout localLCMSImageLayout1;
    if (localLCMSImageLayout2 != null)
    {
      localLCMSImageLayout1 = LCMSImageLayout.createImageLayout(paramRaster);
      if (localLCMSImageLayout1 != null)
      {
        doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
        return;
      }
    }
    SampleModel localSampleModel1 = paramRaster.getSampleModel();
    SampleModel localSampleModel2 = paramWritableRaster.getSampleModel();
    int i = paramRaster.getTransferType();
    int j = paramWritableRaster.getTransferType();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    int n = paramRaster.getNumBands();
    int i1 = paramWritableRaster.getNumBands();
    int i2 = 8;
    float f = 255.0F;
    for (int i3 = 0; i3 < n; i3++) {
      if (localSampleModel1.getSampleSize(i3) > 8)
      {
        i2 = 16;
        f = 65535.0F;
      }
    }
    for (i3 = 0; i3 < i1; i3++) {
      if (localSampleModel2.getSampleSize(i3) > 8)
      {
        i2 = 16;
        f = 65535.0F;
      }
    }
    float[] arrayOfFloat1 = new float[n];
    float[] arrayOfFloat2 = new float[i1];
    for (int i4 = 0; i4 < n; i4++) {
      if (i == 2) {
        arrayOfFloat1[i4] = (f / 32767.0F);
      } else {
        arrayOfFloat1[i4] = (f / ((1 << localSampleModel1.getSampleSize(i4)) - 1));
      }
    }
    for (i4 = 0; i4 < i1; i4++) {
      if (j == 2) {
        arrayOfFloat2[i4] = (32767.0F / f);
      } else {
        arrayOfFloat2[i4] = (((1 << localSampleModel2.getSampleSize(i4)) - 1) / f);
      }
    }
    i4 = paramRaster.getMinY();
    int i5 = paramWritableRaster.getMinY();
    Object localObject1;
    Object localObject2;
    int i6;
    int i9;
    int i12;
    int i13;
    int i8;
    int i7;
    if (i2 == 8)
    {
      localObject1 = new byte[k * n];
      localObject2 = new byte[k * i1];
      try
      {
        localLCMSImageLayout1 = new LCMSImageLayout((byte[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents());
        localLCMSImageLayout2 = new LCMSImageLayout((byte[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
      }
      catch (LCMSImageLayout.ImageLayoutException localImageLayoutException1)
      {
        throw new CMMException("Unable to convert rasters");
      }
      int i10 = 0;
      while (i10 < m)
      {
        i6 = paramRaster.getMinX();
        i9 = 0;
        i12 = 0;
        while (i12 < k)
        {
          for (i13 = 0; i13 < n; i13++)
          {
            i8 = paramRaster.getSample(i6, i4, i13);
            localObject1[(i9++)] = ((byte)(int)(i8 * arrayOfFloat1[i13] + 0.5F));
          }
          i12++;
          i6++;
        }
        doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
        i7 = paramWritableRaster.getMinX();
        i9 = 0;
        i12 = 0;
        while (i12 < k)
        {
          for (i13 = 0; i13 < i1; i13++)
          {
            i8 = (int)((localObject2[(i9++)] & 0xFF) * arrayOfFloat2[i13] + 0.5F);
            paramWritableRaster.setSample(i7, i5, i13, i8);
          }
          i12++;
          i7++;
        }
        i10++;
        i4++;
        i5++;
      }
    }
    else
    {
      localObject1 = new short[k * n];
      localObject2 = new short[k * i1];
      try
      {
        localLCMSImageLayout1 = new LCMSImageLayout((short[])localObject1, localObject1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
        localLCMSImageLayout2 = new LCMSImageLayout((short[])localObject2, localObject2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
      }
      catch (LCMSImageLayout.ImageLayoutException localImageLayoutException2)
      {
        throw new CMMException("Unable to convert rasters");
      }
      int i11 = 0;
      while (i11 < m)
      {
        i6 = paramRaster.getMinX();
        i9 = 0;
        i12 = 0;
        while (i12 < k)
        {
          for (i13 = 0; i13 < n; i13++)
          {
            i8 = paramRaster.getSample(i6, i4, i13);
            localObject1[(i9++)] = ((short)(int)(i8 * arrayOfFloat1[i13] + 0.5F));
          }
          i12++;
          i6++;
        }
        doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
        i7 = paramWritableRaster.getMinX();
        i9 = 0;
        i12 = 0;
        while (i12 < k)
        {
          for (i13 = 0; i13 < i1; i13++)
          {
            i8 = (int)((localObject2[(i9++)] & 0xFFFF) * arrayOfFloat2[i13] + 0.5F);
            paramWritableRaster.setSample(i7, i5, i13, i8);
          }
          i12++;
          i7++;
        }
        i11++;
        i4++;
        i5++;
      }
    }
  }
  
  public short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    if (paramArrayOfShort2 == null) {
      paramArrayOfShort2 = new short[paramArrayOfShort1.length / getNumInComponents() * getNumOutComponents()];
    }
    try
    {
      LCMSImageLayout localLCMSImageLayout1 = new LCMSImageLayout(paramArrayOfShort1, paramArrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2);
      LCMSImageLayout localLCMSImageLayout2 = new LCMSImageLayout(paramArrayOfShort2, paramArrayOfShort2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
      doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
      return paramArrayOfShort2;
    }
    catch (LCMSImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert data");
    }
  }
  
  public byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte2 == null) {
      paramArrayOfByte2 = new byte[paramArrayOfByte1.length / getNumInComponents() * getNumOutComponents()];
    }
    try
    {
      LCMSImageLayout localLCMSImageLayout1 = new LCMSImageLayout(paramArrayOfByte1, paramArrayOfByte1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents());
      LCMSImageLayout localLCMSImageLayout2 = new LCMSImageLayout(paramArrayOfByte2, paramArrayOfByte2.length / getNumOutComponents(), LCMSImageLayout.CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
      doTransform(localLCMSImageLayout1, localLCMSImageLayout2);
      return paramArrayOfByte2;
    }
    catch (LCMSImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert data");
    }
  }
  
  static
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\lcms\LCMSTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */