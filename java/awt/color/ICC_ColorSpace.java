package java.awt.color;

import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

public class ICC_ColorSpace
  extends ColorSpace
{
  static final long serialVersionUID = 3455889114070431483L;
  private ICC_Profile thisProfile;
  private float[] minVal;
  private float[] maxVal;
  private float[] diffMinMax;
  private float[] invDiffMinMax;
  private boolean needScaleInit = true;
  private transient ColorTransform this2srgb;
  private transient ColorTransform srgb2this;
  private transient ColorTransform this2xyz;
  private transient ColorTransform xyz2this;
  
  public ICC_ColorSpace(ICC_Profile paramICC_Profile)
  {
    super(paramICC_Profile.getColorSpaceType(), paramICC_Profile.getNumComponents());
    int i = paramICC_Profile.getProfileClass();
    if ((i != 0) && (i != 1) && (i != 2) && (i != 4) && (i != 6) && (i != 5)) {
      throw new IllegalArgumentException("Invalid profile type");
    }
    thisProfile = paramICC_Profile;
    setMinMax();
  }
  
  public ICC_Profile getProfile()
  {
    return thisProfile;
  }
  
  public float[] toRGB(float[] paramArrayOfFloat)
  {
    if (this2srgb == null)
    {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
      localObject = (ICC_ColorSpace)ColorSpace.getInstance(1000);
      PCMM localPCMM = CMSManager.getModule();
      arrayOfColorTransform[0] = localPCMM.createTransform(thisProfile, -1, 1);
      arrayOfColorTransform[1] = localPCMM.createTransform(((ICC_ColorSpace)localObject).getProfile(), -1, 2);
      this2srgb = localPCMM.createTransform(arrayOfColorTransform);
      if (needScaleInit) {
        setComponentScaling();
      }
    }
    int i = getNumComponents();
    Object localObject = new short[i];
    for (int j = 0; j < i; j++) {
      localObject[j] = ((short)(int)((paramArrayOfFloat[j] - minVal[j]) * invDiffMinMax[j] + 0.5F));
    }
    localObject = this2srgb.colorConvert((short[])localObject, null);
    float[] arrayOfFloat = new float[3];
    for (int k = 0; k < 3; k++) {
      arrayOfFloat[k] = ((localObject[k] & 0xFFFF) / 65535.0F);
    }
    return arrayOfFloat;
  }
  
  public float[] fromRGB(float[] paramArrayOfFloat)
  {
    if (srgb2this == null)
    {
      localObject1 = new ColorTransform[2];
      ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
      localObject2 = CMSManager.getModule();
      localObject1[0] = ((PCMM)localObject2).createTransform(localICC_ColorSpace.getProfile(), -1, 1);
      localObject1[1] = ((PCMM)localObject2).createTransform(thisProfile, -1, 2);
      srgb2this = ((PCMM)localObject2).createTransform((ColorTransform[])localObject1);
      if (needScaleInit) {
        setComponentScaling();
      }
    }
    Object localObject1 = new short[3];
    for (int i = 0; i < 3; i++) {
      localObject1[i] = ((short)(int)(paramArrayOfFloat[i] * 65535.0F + 0.5F));
    }
    localObject1 = srgb2this.colorConvert((short[])localObject1, null);
    i = getNumComponents();
    Object localObject2 = new float[i];
    for (int j = 0; j < i; j++) {
      localObject2[j] = ((localObject1[j] & 0xFFFF) / 65535.0F * diffMinMax[j] + minVal[j]);
    }
    return (float[])localObject2;
  }
  
  public float[] toCIEXYZ(float[] paramArrayOfFloat)
  {
    if (this2xyz == null)
    {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
      localObject = (ICC_ColorSpace)ColorSpace.getInstance(1001);
      PCMM localPCMM = CMSManager.getModule();
      try
      {
        arrayOfColorTransform[0] = localPCMM.createTransform(thisProfile, 1, 1);
      }
      catch (CMMException localCMMException)
      {
        arrayOfColorTransform[0] = localPCMM.createTransform(thisProfile, -1, 1);
      }
      arrayOfColorTransform[1] = localPCMM.createTransform(((ICC_ColorSpace)localObject).getProfile(), -1, 2);
      this2xyz = localPCMM.createTransform(arrayOfColorTransform);
      if (needScaleInit) {
        setComponentScaling();
      }
    }
    int i = getNumComponents();
    Object localObject = new short[i];
    for (int j = 0; j < i; j++) {
      localObject[j] = ((short)(int)((paramArrayOfFloat[j] - minVal[j]) * invDiffMinMax[j] + 0.5F));
    }
    localObject = this2xyz.colorConvert((short[])localObject, null);
    float f = 1.9999695F;
    float[] arrayOfFloat = new float[3];
    for (int k = 0; k < 3; k++) {
      arrayOfFloat[k] = ((localObject[k] & 0xFFFF) / 65535.0F * f);
    }
    return arrayOfFloat;
  }
  
  public float[] fromCIEXYZ(float[] paramArrayOfFloat)
  {
    if (xyz2this == null)
    {
      localObject = new ColorTransform[2];
      ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
      PCMM localPCMM = CMSManager.getModule();
      localObject[0] = localPCMM.createTransform(localICC_ColorSpace.getProfile(), -1, 1);
      try
      {
        localObject[1] = localPCMM.createTransform(thisProfile, 1, 2);
      }
      catch (CMMException localCMMException)
      {
        localObject[1] = CMSManager.getModule().createTransform(thisProfile, -1, 2);
      }
      xyz2this = localPCMM.createTransform((ColorTransform[])localObject);
      if (needScaleInit) {
        setComponentScaling();
      }
    }
    Object localObject = new short[3];
    float f1 = 1.9999695F;
    float f2 = 65535.0F / f1;
    for (int i = 0; i < 3; i++) {
      localObject[i] = ((short)(int)(paramArrayOfFloat[i] * f2 + 0.5F));
    }
    localObject = xyz2this.colorConvert((short[])localObject, null);
    i = getNumComponents();
    float[] arrayOfFloat = new float[i];
    for (int j = 0; j < i; j++) {
      arrayOfFloat[j] = ((localObject[j] & 0xFFFF) / 65535.0F * diffMinMax[j] + minVal[j]);
    }
    return arrayOfFloat;
  }
  
  public float getMinValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > getNumComponents() - 1)) {
      throw new IllegalArgumentException("Component index out of range: + component");
    }
    return minVal[paramInt];
  }
  
  public float getMaxValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > getNumComponents() - 1)) {
      throw new IllegalArgumentException("Component index out of range: + component");
    }
    return maxVal[paramInt];
  }
  
  private void setMinMax()
  {
    int i = getNumComponents();
    int j = getType();
    minVal = new float[i];
    maxVal = new float[i];
    if (j == 1)
    {
      minVal[0] = 0.0F;
      maxVal[0] = 100.0F;
      minVal[1] = -128.0F;
      maxVal[1] = 127.0F;
      minVal[2] = -128.0F;
      maxVal[2] = 127.0F;
    }
    else if (j == 0)
    {
      minVal[0] = (minVal[1] = minVal[2] = 0.0F);
      maxVal[0] = (maxVal[1] = maxVal[2] = 1.9999695F);
    }
    else
    {
      for (int k = 0; k < i; k++)
      {
        minVal[k] = 0.0F;
        maxVal[k] = 1.0F;
      }
    }
  }
  
  private void setComponentScaling()
  {
    int i = getNumComponents();
    diffMinMax = new float[i];
    invDiffMinMax = new float[i];
    for (int j = 0; j < i; j++)
    {
      minVal[j] = getMinValue(j);
      maxVal[j] = getMaxValue(j);
      diffMinMax[j] = (maxVal[j] - minVal[j]);
      invDiffMinMax[j] = (65535.0F / diffMinMax[j]);
    }
    needScaleInit = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\color\ICC_ColorSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */