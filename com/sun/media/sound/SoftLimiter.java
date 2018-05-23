package com.sun.media.sound;

public final class SoftLimiter
  implements SoftAudioProcessor
{
  float lastmax = 0.0F;
  float gain = 1.0F;
  float[] temp_bufferL;
  float[] temp_bufferR;
  boolean mix = false;
  SoftAudioBuffer bufferL;
  SoftAudioBuffer bufferR;
  SoftAudioBuffer bufferLout;
  SoftAudioBuffer bufferRout;
  float controlrate;
  double silentcounter = 0.0D;
  
  public SoftLimiter() {}
  
  public void init(float paramFloat1, float paramFloat2)
  {
    controlrate = paramFloat2;
  }
  
  public void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      bufferL = paramSoftAudioBuffer;
    }
    if (paramInt == 1) {
      bufferR = paramSoftAudioBuffer;
    }
  }
  
  public void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      bufferLout = paramSoftAudioBuffer;
    }
    if (paramInt == 1) {
      bufferRout = paramSoftAudioBuffer;
    }
  }
  
  public void setMixMode(boolean paramBoolean)
  {
    mix = paramBoolean;
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2) {}
  
  public void processAudio()
  {
    if ((bufferL.isSilent()) && ((bufferR == null) || (bufferR.isSilent())))
    {
      silentcounter += 1.0F / controlrate;
      if (silentcounter > 60.0D) {
        if (!mix)
        {
          bufferLout.clear();
          if (bufferRout != null) {
            bufferRout.clear();
          }
        }
      }
    }
    else
    {
      silentcounter = 0.0D;
    }
    float[] arrayOfFloat1 = bufferL.array();
    float[] arrayOfFloat2 = bufferR == null ? null : bufferR.array();
    float[] arrayOfFloat3 = bufferLout.array();
    float[] arrayOfFloat4 = bufferRout == null ? null : bufferRout.array();
    if ((temp_bufferL == null) || (temp_bufferL.length < arrayOfFloat1.length)) {
      temp_bufferL = new float[arrayOfFloat1.length];
    }
    if ((arrayOfFloat2 != null) && ((temp_bufferR == null) || (temp_bufferR.length < arrayOfFloat2.length))) {
      temp_bufferR = new float[arrayOfFloat2.length];
    }
    float f1 = 0.0F;
    int i = arrayOfFloat1.length;
    int j;
    if (arrayOfFloat2 == null) {
      for (j = 0; j < i; j++)
      {
        if (arrayOfFloat1[j] > f1) {
          f1 = arrayOfFloat1[j];
        }
        if (-arrayOfFloat1[j] > f1) {
          f1 = -arrayOfFloat1[j];
        }
      }
    } else {
      for (j = 0; j < i; j++)
      {
        if (arrayOfFloat1[j] > f1) {
          f1 = arrayOfFloat1[j];
        }
        if (arrayOfFloat2[j] > f1) {
          f1 = arrayOfFloat2[j];
        }
        if (-arrayOfFloat1[j] > f1) {
          f1 = -arrayOfFloat1[j];
        }
        if (-arrayOfFloat2[j] > f1) {
          f1 = -arrayOfFloat2[j];
        }
      }
    }
    float f2 = lastmax;
    lastmax = f1;
    if (f2 > f1) {
      f1 = f2;
    }
    float f3 = 1.0F;
    if (f1 > 0.99F) {
      f3 = 0.99F / f1;
    } else {
      f3 = 1.0F;
    }
    if (f3 > gain) {
      f3 = (f3 + gain * 9.0F) / 10.0F;
    }
    float f4 = (f3 - gain) / i;
    int k;
    float f5;
    float f6;
    float f7;
    float f8;
    if (mix)
    {
      if (arrayOfFloat2 == null) {
        for (k = 0; k < i; k++)
        {
          gain += f4;
          f5 = arrayOfFloat1[k];
          f6 = temp_bufferL[k];
          temp_bufferL[k] = f5;
          arrayOfFloat3[k] += f6 * gain;
        }
      } else {
        for (k = 0; k < i; k++)
        {
          gain += f4;
          f5 = arrayOfFloat1[k];
          f6 = arrayOfFloat2[k];
          f7 = temp_bufferL[k];
          f8 = temp_bufferR[k];
          temp_bufferL[k] = f5;
          temp_bufferR[k] = f6;
          arrayOfFloat3[k] += f7 * gain;
          arrayOfFloat4[k] += f8 * gain;
        }
      }
    }
    else if (arrayOfFloat2 == null) {
      for (k = 0; k < i; k++)
      {
        gain += f4;
        f5 = arrayOfFloat1[k];
        f6 = temp_bufferL[k];
        temp_bufferL[k] = f5;
        arrayOfFloat3[k] = (f6 * gain);
      }
    } else {
      for (k = 0; k < i; k++)
      {
        gain += f4;
        f5 = arrayOfFloat1[k];
        f6 = arrayOfFloat2[k];
        f7 = temp_bufferL[k];
        f8 = temp_bufferR[k];
        temp_bufferL[k] = f5;
        temp_bufferR[k] = f6;
        arrayOfFloat3[k] = (f7 * gain);
        arrayOfFloat4[k] = (f8 * gain);
      }
    }
    gain = f3;
  }
  
  public void processControlLogic() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftLimiter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */