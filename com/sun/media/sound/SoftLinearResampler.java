package com.sun.media.sound;

public final class SoftLinearResampler
  extends SoftAbstractResampler
{
  public SoftLinearResampler() {}
  
  public int getPadding()
  {
    return 2;
  }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt)
  {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    int k;
    float f4;
    float f5;
    if (paramFloat2 == 0.0F) {
      while ((f2 < f3) && (i < j))
      {
        k = (int)f2;
        f4 = f2 - k;
        f5 = paramArrayOfFloat1[k];
        paramArrayOfFloat4[(i++)] = (f5 + (paramArrayOfFloat1[(k + 1)] - f5) * f4);
        f2 += f1;
      }
    }
    while ((f2 < f3) && (i < j))
    {
      k = (int)f2;
      f4 = f2 - k;
      f5 = paramArrayOfFloat1[k];
      paramArrayOfFloat4[(i++)] = (f5 + (paramArrayOfFloat1[(k + 1)] - f5) * f4);
      f2 += f1;
      f1 += paramFloat2;
    }
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftLinearResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */