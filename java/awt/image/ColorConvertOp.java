package java.awt.image;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ColorConvertOp
  implements BufferedImageOp, RasterOp
{
  ICC_Profile[] profileList;
  ColorSpace[] CSList;
  ColorTransform thisTransform;
  ColorTransform thisRasterTransform;
  ICC_Profile thisSrcProfile;
  ICC_Profile thisDestProfile;
  RenderingHints hints;
  boolean gotProfiles;
  float[] srcMinVals;
  float[] srcMaxVals;
  float[] dstMinVals;
  float[] dstMaxVals;
  
  public ColorConvertOp(RenderingHints paramRenderingHints)
  {
    profileList = new ICC_Profile[0];
    hints = paramRenderingHints;
  }
  
  public ColorConvertOp(ColorSpace paramColorSpace, RenderingHints paramRenderingHints)
  {
    if (paramColorSpace == null) {
      throw new NullPointerException("ColorSpace cannot be null");
    }
    if ((paramColorSpace instanceof ICC_ColorSpace))
    {
      profileList = new ICC_Profile[1];
      profileList[0] = ((ICC_ColorSpace)paramColorSpace).getProfile();
    }
    else
    {
      CSList = new ColorSpace[1];
      CSList[0] = paramColorSpace;
    }
    hints = paramRenderingHints;
  }
  
  public ColorConvertOp(ColorSpace paramColorSpace1, ColorSpace paramColorSpace2, RenderingHints paramRenderingHints)
  {
    if ((paramColorSpace1 == null) || (paramColorSpace2 == null)) {
      throw new NullPointerException("ColorSpaces cannot be null");
    }
    if (((paramColorSpace1 instanceof ICC_ColorSpace)) && ((paramColorSpace2 instanceof ICC_ColorSpace)))
    {
      profileList = new ICC_Profile[2];
      profileList[0] = ((ICC_ColorSpace)paramColorSpace1).getProfile();
      profileList[1] = ((ICC_ColorSpace)paramColorSpace2).getProfile();
      getMinMaxValsFromColorSpaces(paramColorSpace1, paramColorSpace2);
    }
    else
    {
      CSList = new ColorSpace[2];
      CSList[0] = paramColorSpace1;
      CSList[1] = paramColorSpace2;
    }
    hints = paramRenderingHints;
  }
  
  public ColorConvertOp(ICC_Profile[] paramArrayOfICC_Profile, RenderingHints paramRenderingHints)
  {
    if (paramArrayOfICC_Profile == null) {
      throw new NullPointerException("Profiles cannot be null");
    }
    gotProfiles = true;
    profileList = new ICC_Profile[paramArrayOfICC_Profile.length];
    for (int i = 0; i < paramArrayOfICC_Profile.length; i++) {
      profileList[i] = paramArrayOfICC_Profile[i];
    }
    hints = paramRenderingHints;
  }
  
  public final ICC_Profile[] getICC_Profiles()
  {
    if (gotProfiles)
    {
      ICC_Profile[] arrayOfICC_Profile = new ICC_Profile[profileList.length];
      for (int i = 0; i < profileList.length; i++) {
        arrayOfICC_Profile[i] = profileList[i];
      }
      return arrayOfICC_Profile;
    }
    return null;
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    BufferedImage localBufferedImage = null;
    Object localObject1;
    if ((paramBufferedImage1.getColorModel() instanceof IndexColorModel))
    {
      localObject1 = (IndexColorModel)paramBufferedImage1.getColorModel();
      paramBufferedImage1 = ((IndexColorModel)localObject1).convertToIntDiscrete(paramBufferedImage1.getRaster(), true);
    }
    ColorSpace localColorSpace1 = paramBufferedImage1.getColorModel().getColorSpace();
    ColorSpace localColorSpace2;
    if (paramBufferedImage2 != null)
    {
      if ((paramBufferedImage2.getColorModel() instanceof IndexColorModel))
      {
        localBufferedImage = paramBufferedImage2;
        paramBufferedImage2 = null;
        localColorSpace2 = null;
      }
      else
      {
        localColorSpace2 = paramBufferedImage2.getColorModel().getColorSpace();
      }
    }
    else {
      localColorSpace2 = null;
    }
    if ((CSList != null) || (!(localColorSpace1 instanceof ICC_ColorSpace)) || ((paramBufferedImage2 != null) && (!(localColorSpace2 instanceof ICC_ColorSpace)))) {
      paramBufferedImage2 = nonICCBIFilter(paramBufferedImage1, localColorSpace1, paramBufferedImage2, localColorSpace2);
    } else {
      paramBufferedImage2 = ICCBIFilter(paramBufferedImage1, localColorSpace1, paramBufferedImage2, localColorSpace2);
    }
    if (localBufferedImage != null)
    {
      localObject1 = localBufferedImage.createGraphics();
      try
      {
        ((Graphics2D)localObject1).drawImage(paramBufferedImage2, 0, 0, null);
      }
      finally
      {
        ((Graphics2D)localObject1).dispose();
      }
      return localBufferedImage;
    }
    return paramBufferedImage2;
  }
  
  private final BufferedImage ICCBIFilter(BufferedImage paramBufferedImage1, ColorSpace paramColorSpace1, BufferedImage paramBufferedImage2, ColorSpace paramColorSpace2)
  {
    int i = profileList.length;
    ICC_Profile localICC_Profile1 = null;
    ICC_Profile localICC_Profile2 = null;
    localICC_Profile1 = ((ICC_ColorSpace)paramColorSpace1).getProfile();
    if (paramBufferedImage2 == null)
    {
      if (i == 0) {
        throw new IllegalArgumentException("Destination ColorSpace is undefined");
      }
      localICC_Profile2 = profileList[(i - 1)];
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
    }
    else
    {
      if ((paramBufferedImage1.getHeight() != paramBufferedImage2.getHeight()) || (paramBufferedImage1.getWidth() != paramBufferedImage2.getWidth())) {
        throw new IllegalArgumentException("Width or height of BufferedImages do not match");
      }
      localICC_Profile2 = ((ICC_ColorSpace)paramColorSpace2).getProfile();
    }
    if (localICC_Profile1 == localICC_Profile2)
    {
      int j = 1;
      for (int k = 0; k < i; k++) {
        if (localICC_Profile1 != profileList[k])
        {
          j = 0;
          break;
        }
      }
      if (j != 0)
      {
        Graphics2D localGraphics2D = paramBufferedImage2.createGraphics();
        try
        {
          localGraphics2D.drawImage(paramBufferedImage1, 0, 0, null);
        }
        finally
        {
          localGraphics2D.dispose();
        }
        return paramBufferedImage2;
      }
    }
    if ((thisTransform == null) || (thisSrcProfile != localICC_Profile1) || (thisDestProfile != localICC_Profile2)) {
      updateBITransform(localICC_Profile1, localICC_Profile2);
    }
    thisTransform.colorConvert(paramBufferedImage1, paramBufferedImage2);
    return paramBufferedImage2;
  }
  
  private void updateBITransform(ICC_Profile paramICC_Profile1, ICC_Profile paramICC_Profile2)
  {
    int i1 = 0;
    int i2 = 0;
    int j = profileList.length;
    int k = j;
    if ((j == 0) || (paramICC_Profile1 != profileList[0]))
    {
      k++;
      i1 = 1;
    }
    if ((j == 0) || (paramICC_Profile2 != profileList[(j - 1)]) || (k < 2))
    {
      k++;
      i2 = 1;
    }
    ICC_Profile[] arrayOfICC_Profile = new ICC_Profile[k];
    int i3 = 0;
    if (i1 != 0) {
      arrayOfICC_Profile[(i3++)] = paramICC_Profile1;
    }
    for (int i = 0; i < j; i++) {
      arrayOfICC_Profile[(i3++)] = profileList[i];
    }
    if (i2 != 0) {
      arrayOfICC_Profile[i3] = paramICC_Profile2;
    }
    ColorTransform[] arrayOfColorTransform = new ColorTransform[k];
    int n;
    if (arrayOfICC_Profile[0].getProfileClass() == 2) {
      n = 1;
    } else {
      n = 0;
    }
    int m = 1;
    PCMM localPCMM = CMSManager.getModule();
    for (i = 0; i < k; i++)
    {
      if (i == k - 1)
      {
        m = 2;
      }
      else if ((m == 4) && (arrayOfICC_Profile[i].getProfileClass() == 5))
      {
        n = 0;
        m = 1;
      }
      arrayOfColorTransform[i] = localPCMM.createTransform(arrayOfICC_Profile[i], n, m);
      n = getRenderingIntent(arrayOfICC_Profile[i]);
      m = 4;
    }
    thisTransform = localPCMM.createTransform(arrayOfColorTransform);
    thisSrcProfile = paramICC_Profile1;
    thisDestProfile = paramICC_Profile2;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    if (CSList != null) {
      return nonICCRasterFilter(paramRaster, paramWritableRaster);
    }
    int i = profileList.length;
    if (i < 2) {
      throw new IllegalArgumentException("Source or Destination ColorSpace is undefined");
    }
    if (paramRaster.getNumBands() != profileList[0].getNumComponents()) {
      throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match");
    }
    if (paramWritableRaster == null)
    {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    }
    else
    {
      if ((paramRaster.getHeight() != paramWritableRaster.getHeight()) || (paramRaster.getWidth() != paramWritableRaster.getWidth())) {
        throw new IllegalArgumentException("Width or height of Rasters do not match");
      }
      if (paramWritableRaster.getNumBands() != profileList[(i - 1)].getNumComponents()) {
        throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match");
      }
    }
    if (thisRasterTransform == null)
    {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[i];
      int m;
      if (profileList[0].getProfileClass() == 2) {
        m = 1;
      } else {
        m = 0;
      }
      k = 1;
      PCMM localPCMM = CMSManager.getModule();
      for (j = 0; j < i; j++)
      {
        if (j == i - 1)
        {
          k = 2;
        }
        else if ((k == 4) && (profileList[j].getProfileClass() == 5))
        {
          m = 0;
          k = 1;
        }
        arrayOfColorTransform[j] = localPCMM.createTransform(profileList[j], m, k);
        m = getRenderingIntent(profileList[j]);
        k = 4;
      }
      thisRasterTransform = localPCMM.createTransform(arrayOfColorTransform);
    }
    int j = paramRaster.getTransferType();
    int k = paramWritableRaster.getTransferType();
    if ((j == 4) || (j == 5) || (k == 4) || (k == 5))
    {
      if (srcMinVals == null) {
        getMinMaxValsFromProfiles(profileList[0], profileList[(i - 1)]);
      }
      thisRasterTransform.colorConvert(paramRaster, paramWritableRaster, srcMinVals, srcMaxVals, dstMinVals, dstMaxVals);
    }
    else
    {
      thisRasterTransform.colorConvert(paramRaster, paramWritableRaster);
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
    Object localObject = null;
    if (paramColorModel == null)
    {
      int i;
      if (CSList == null)
      {
        i = profileList.length;
        if (i == 0) {
          throw new IllegalArgumentException("Destination ColorSpace is undefined");
        }
        ICC_Profile localICC_Profile = profileList[(i - 1)];
        localObject = new ICC_ColorSpace(localICC_Profile);
      }
      else
      {
        i = CSList.length;
        localObject = CSList[(i - 1)];
      }
    }
    return createCompatibleDestImage(paramBufferedImage, paramColorModel, (ColorSpace)localObject);
  }
  
  private BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel, ColorSpace paramColorSpace)
  {
    if (paramColorModel == null)
    {
      ColorModel localColorModel = paramBufferedImage.getColorModel();
      j = paramColorSpace.getNumComponents();
      boolean bool = localColorModel.hasAlpha();
      if (bool) {
        j++;
      }
      int[] arrayOfInt = new int[j];
      for (int k = 0; k < j; k++) {
        arrayOfInt[k] = 8;
      }
      paramColorModel = new ComponentColorModel(paramColorSpace, arrayOfInt, bool, localColorModel.isAlphaPremultiplied(), localColorModel.getTransparency(), 0);
    }
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    BufferedImage localBufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    return localBufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster)
  {
    int i;
    if (CSList != null)
    {
      if (CSList.length != 2) {
        throw new IllegalArgumentException("Destination ColorSpace is undefined");
      }
      i = CSList[1].getNumComponents();
    }
    else
    {
      int j = profileList.length;
      if (j < 2) {
        throw new IllegalArgumentException("Destination ColorSpace is undefined");
      }
      i = profileList[(j - 1)].getNumComponents();
    }
    WritableRaster localWritableRaster = Raster.createInterleavedRaster(0, paramRaster.getWidth(), paramRaster.getHeight(), i, new Point(paramRaster.getMinX(), paramRaster.getMinY()));
    return localWritableRaster;
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    if (paramPoint2D2 == null) {
      paramPoint2D2 = new Point2D.Float();
    }
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  private int getRenderingIntent(ICC_Profile paramICC_Profile)
  {
    byte[] arrayOfByte = paramICC_Profile.getData(1751474532);
    int i = 64;
    return (arrayOfByte[(i + 2)] & 0xFF) << 8 | arrayOfByte[(i + 3)] & 0xFF;
  }
  
  public final RenderingHints getRenderingHints()
  {
    return hints;
  }
  
  private final BufferedImage nonICCBIFilter(BufferedImage paramBufferedImage1, ColorSpace paramColorSpace1, BufferedImage paramBufferedImage2, ColorSpace paramColorSpace2)
  {
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
    if (paramBufferedImage2 == null)
    {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      paramColorSpace2 = paramBufferedImage2.getColorModel().getColorSpace();
    }
    else if ((j != paramBufferedImage2.getHeight()) || (i != paramBufferedImage2.getWidth()))
    {
      throw new IllegalArgumentException("Width or height of BufferedImages do not match");
    }
    WritableRaster localWritableRaster1 = paramBufferedImage1.getRaster();
    WritableRaster localWritableRaster2 = paramBufferedImage2.getRaster();
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    ColorModel localColorModel2 = paramBufferedImage2.getColorModel();
    int k = localColorModel1.getNumColorComponents();
    int m = localColorModel2.getNumColorComponents();
    boolean bool = localColorModel2.hasAlpha();
    int n = (localColorModel1.hasAlpha()) && (bool) ? 1 : 0;
    int i1;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    int i3;
    if ((CSList == null) && (profileList.length != 0))
    {
      if (!(paramColorSpace1 instanceof ICC_ColorSpace))
      {
        i1 = 1;
        localObject1 = localICC_ColorSpace.getProfile();
      }
      else
      {
        i1 = 0;
        localObject1 = ((ICC_ColorSpace)paramColorSpace1).getProfile();
      }
      int i2;
      if (!(paramColorSpace2 instanceof ICC_ColorSpace))
      {
        i2 = 1;
        localObject2 = localICC_ColorSpace.getProfile();
      }
      else
      {
        i2 = 0;
        localObject2 = ((ICC_ColorSpace)paramColorSpace2).getProfile();
      }
      if ((thisTransform == null) || (thisSrcProfile != localObject1) || (thisDestProfile != localObject2)) {
        updateBITransform((ICC_Profile)localObject1, (ICC_Profile)localObject2);
      }
      float f = 65535.0F;
      if (i1 != 0)
      {
        localObject3 = localICC_ColorSpace;
        i3 = 3;
      }
      else
      {
        localObject3 = paramColorSpace1;
        i3 = k;
      }
      float[] arrayOfFloat3 = new float[i3];
      float[] arrayOfFloat4 = new float[i3];
      for (int i6 = 0; i6 < k; i6++)
      {
        arrayOfFloat3[i6] = ((ColorSpace)localObject3).getMinValue(i6);
        arrayOfFloat4[i6] = (f / (((ColorSpace)localObject3).getMaxValue(i6) - arrayOfFloat3[i6]));
      }
      if (i2 != 0)
      {
        localObject3 = localICC_ColorSpace;
        i6 = 3;
      }
      else
      {
        localObject3 = paramColorSpace2;
        i6 = m;
      }
      float[] arrayOfFloat5 = new float[i6];
      float[] arrayOfFloat6 = new float[i6];
      for (int i7 = 0; i7 < m; i7++)
      {
        arrayOfFloat5[i7] = ((ColorSpace)localObject3).getMinValue(i7);
        arrayOfFloat6[i7] = ((((ColorSpace)localObject3).getMaxValue(i7) - arrayOfFloat5[i7]) / f);
      }
      int i8;
      float[] arrayOfFloat7;
      if (bool)
      {
        i8 = m + 1 > 3 ? m + 1 : 3;
        arrayOfFloat7 = new float[i8];
      }
      else
      {
        i8 = m > 3 ? m : 3;
        arrayOfFloat7 = new float[i8];
      }
      short[] arrayOfShort1 = new short[i * i3];
      short[] arrayOfShort2 = new short[i * i6];
      float[] arrayOfFloat9 = null;
      if (n != 0) {
        arrayOfFloat9 = new float[i];
      }
      for (int i10 = 0; i10 < j; i10++)
      {
        Object localObject4 = null;
        float[] arrayOfFloat8 = null;
        int i9 = 0;
        int i12;
        for (int i11 = 0; i11 < i; i11++)
        {
          localObject4 = localWritableRaster1.getDataElements(i11, i10, localObject4);
          arrayOfFloat8 = localColorModel1.getNormalizedComponents(localObject4, arrayOfFloat8, 0);
          if (n != 0) {
            arrayOfFloat9[i11] = arrayOfFloat8[k];
          }
          if (i1 != 0) {
            arrayOfFloat8 = paramColorSpace1.toCIEXYZ(arrayOfFloat8);
          }
          for (i12 = 0; i12 < i3; i12++) {
            arrayOfShort1[(i9++)] = ((short)(int)((arrayOfFloat8[i12] - arrayOfFloat3[i12]) * arrayOfFloat4[i12] + 0.5F));
          }
        }
        thisTransform.colorConvert(arrayOfShort1, arrayOfShort2);
        localObject4 = null;
        i9 = 0;
        for (i11 = 0; i11 < i; i11++)
        {
          for (i12 = 0; i12 < i6; i12++) {
            arrayOfFloat7[i12] = ((arrayOfShort2[(i9++)] & 0xFFFF) * arrayOfFloat6[i12] + arrayOfFloat5[i12]);
          }
          if (i2 != 0)
          {
            arrayOfFloat8 = paramColorSpace1.fromCIEXYZ(arrayOfFloat7);
            for (i12 = 0; i12 < m; i12++) {
              arrayOfFloat7[i12] = arrayOfFloat8[i12];
            }
          }
          if (n != 0) {
            arrayOfFloat7[m] = arrayOfFloat9[i11];
          } else if (bool) {
            arrayOfFloat7[m] = 1.0F;
          }
          localObject4 = localColorModel2.getDataElements(arrayOfFloat7, 0, localObject4);
          localWritableRaster2.setDataElements(i11, i10, localObject4);
        }
      }
    }
    else
    {
      if (CSList == null) {
        i1 = 0;
      } else {
        i1 = CSList.length;
      }
      float[] arrayOfFloat1;
      if (bool) {
        arrayOfFloat1 = new float[m + 1];
      } else {
        arrayOfFloat1 = new float[m];
      }
      localObject1 = null;
      localObject2 = null;
      float[] arrayOfFloat2 = null;
      for (i3 = 0; i3 < j; i3++) {
        for (int i4 = 0; i4 < i; i4++)
        {
          localObject1 = localWritableRaster1.getDataElements(i4, i3, localObject1);
          arrayOfFloat2 = localColorModel1.getNormalizedComponents(localObject1, arrayOfFloat2, 0);
          localObject3 = paramColorSpace1.toCIEXYZ(arrayOfFloat2);
          for (int i5 = 0; i5 < i1; i5++)
          {
            localObject3 = CSList[i5].fromCIEXYZ((float[])localObject3);
            localObject3 = CSList[i5].toCIEXYZ((float[])localObject3);
          }
          localObject3 = paramColorSpace2.fromCIEXYZ((float[])localObject3);
          for (i5 = 0; i5 < m; i5++) {
            arrayOfFloat1[i5] = localObject3[i5];
          }
          if (n != 0) {
            arrayOfFloat1[m] = arrayOfFloat2[k];
          } else if (bool) {
            arrayOfFloat1[m] = 1.0F;
          }
          localObject2 = localColorModel2.getDataElements(arrayOfFloat1, 0, localObject2);
          localWritableRaster2.setDataElements(i4, i3, localObject2);
        }
      }
    }
    return paramBufferedImage2;
  }
  
  private final WritableRaster nonICCRasterFilter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    if (CSList.length != 2) {
      throw new IllegalArgumentException("Destination ColorSpace is undefined");
    }
    if (paramRaster.getNumBands() != CSList[0].getNumComponents()) {
      throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match");
    }
    if (paramWritableRaster == null)
    {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    }
    else
    {
      if ((paramRaster.getHeight() != paramWritableRaster.getHeight()) || (paramRaster.getWidth() != paramWritableRaster.getWidth())) {
        throw new IllegalArgumentException("Width or height of Rasters do not match");
      }
      if (paramWritableRaster.getNumBands() != CSList[1].getNumComponents()) {
        throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match");
      }
    }
    if (srcMinVals == null) {
      getMinMaxValsFromColorSpaces(CSList[0], CSList[1]);
    }
    SampleModel localSampleModel1 = paramRaster.getSampleModel();
    SampleModel localSampleModel2 = paramWritableRaster.getSampleModel();
    int k = paramRaster.getTransferType();
    int m = paramWritableRaster.getTransferType();
    int i;
    if ((k == 4) || (k == 5)) {
      i = 1;
    } else {
      i = 0;
    }
    int j;
    if ((m == 4) || (m == 5)) {
      j = 1;
    } else {
      j = 0;
    }
    int n = paramRaster.getWidth();
    int i1 = paramRaster.getHeight();
    int i2 = paramRaster.getNumBands();
    int i3 = paramWritableRaster.getNumBands();
    float[] arrayOfFloat1 = null;
    float[] arrayOfFloat2 = null;
    if (i == 0)
    {
      arrayOfFloat1 = new float[i2];
      for (i4 = 0; i4 < i2; i4++) {
        if (k == 2) {
          arrayOfFloat1[i4] = ((srcMaxVals[i4] - srcMinVals[i4]) / 32767.0F);
        } else {
          arrayOfFloat1[i4] = ((srcMaxVals[i4] - srcMinVals[i4]) / ((1 << localSampleModel1.getSampleSize(i4)) - 1));
        }
      }
    }
    if (j == 0)
    {
      arrayOfFloat2 = new float[i3];
      for (i4 = 0; i4 < i3; i4++) {
        if (m == 2) {
          arrayOfFloat2[i4] = (32767.0F / (dstMaxVals[i4] - dstMinVals[i4]));
        } else {
          arrayOfFloat2[i4] = (((1 << localSampleModel2.getSampleSize(i4)) - 1) / (dstMaxVals[i4] - dstMinVals[i4]));
        }
      }
    }
    int i4 = paramRaster.getMinY();
    int i5 = paramWritableRaster.getMinY();
    float[] arrayOfFloat3 = new float[i2];
    ColorSpace localColorSpace1 = CSList[0];
    ColorSpace localColorSpace2 = CSList[1];
    int i8 = 0;
    while (i8 < i1)
    {
      int i6 = paramRaster.getMinX();
      int i7 = paramWritableRaster.getMinX();
      int i9 = 0;
      while (i9 < n)
      {
        float f;
        for (int i10 = 0; i10 < i2; i10++)
        {
          f = paramRaster.getSampleFloat(i6, i4, i10);
          if (i == 0) {
            f = f * arrayOfFloat1[i10] + srcMinVals[i10];
          }
          arrayOfFloat3[i10] = f;
        }
        float[] arrayOfFloat4 = localColorSpace1.toCIEXYZ(arrayOfFloat3);
        arrayOfFloat4 = localColorSpace2.fromCIEXYZ(arrayOfFloat4);
        for (i10 = 0; i10 < i3; i10++)
        {
          f = arrayOfFloat4[i10];
          if (j == 0) {
            f = (f - dstMinVals[i10]) * arrayOfFloat2[i10];
          }
          paramWritableRaster.setSample(i7, i5, i10, f);
        }
        i9++;
        i6++;
        i7++;
      }
      i8++;
      i4++;
      i5++;
    }
    return paramWritableRaster;
  }
  
  private void getMinMaxValsFromProfiles(ICC_Profile paramICC_Profile1, ICC_Profile paramICC_Profile2)
  {
    int i = paramICC_Profile1.getColorSpaceType();
    int j = paramICC_Profile1.getNumComponents();
    srcMinVals = new float[j];
    srcMaxVals = new float[j];
    setMinMax(i, j, srcMinVals, srcMaxVals);
    i = paramICC_Profile2.getColorSpaceType();
    j = paramICC_Profile2.getNumComponents();
    dstMinVals = new float[j];
    dstMaxVals = new float[j];
    setMinMax(i, j, dstMinVals, dstMaxVals);
  }
  
  private void setMinMax(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    if (paramInt1 == 1)
    {
      paramArrayOfFloat1[0] = 0.0F;
      paramArrayOfFloat2[0] = 100.0F;
      paramArrayOfFloat1[1] = -128.0F;
      paramArrayOfFloat2[1] = 127.0F;
      paramArrayOfFloat1[2] = -128.0F;
      paramArrayOfFloat2[2] = 127.0F;
    }
    else if (paramInt1 == 0)
    {
      paramArrayOfFloat1[0] = (paramArrayOfFloat1[1] = paramArrayOfFloat1[2] = 0.0F);
      paramArrayOfFloat2[0] = (paramArrayOfFloat2[1] = paramArrayOfFloat2[2] = 1.9999695F);
    }
    else
    {
      for (int i = 0; i < paramInt2; i++)
      {
        paramArrayOfFloat1[i] = 0.0F;
        paramArrayOfFloat2[i] = 1.0F;
      }
    }
  }
  
  private void getMinMaxValsFromColorSpaces(ColorSpace paramColorSpace1, ColorSpace paramColorSpace2)
  {
    int i = paramColorSpace1.getNumComponents();
    srcMinVals = new float[i];
    srcMaxVals = new float[i];
    for (int j = 0; j < i; j++)
    {
      srcMinVals[j] = paramColorSpace1.getMinValue(j);
      srcMaxVals[j] = paramColorSpace1.getMaxValue(j);
    }
    i = paramColorSpace2.getNumComponents();
    dstMinVals = new float[i];
    dstMaxVals = new float[i];
    for (j = 0; j < i; j++)
    {
      dstMinVals[j] = paramColorSpace2.getMinValue(j);
      dstMaxVals[j] = paramColorSpace2.getMaxValue(j);
    }
  }
  
  static
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ColorConvertOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */