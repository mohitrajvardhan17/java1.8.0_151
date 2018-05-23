package com.sun.media.sound;

import java.util.Arrays;

public final class SoftChorus
  implements SoftAudioProcessor
{
  private boolean mix = true;
  private SoftAudioBuffer inputA;
  private SoftAudioBuffer left;
  private SoftAudioBuffer right;
  private SoftAudioBuffer reverb;
  private LFODelay vdelay1L;
  private LFODelay vdelay1R;
  private float rgain = 0.0F;
  private boolean dirty = true;
  private double dirty_vdelay1L_rate;
  private double dirty_vdelay1R_rate;
  private double dirty_vdelay1L_depth;
  private double dirty_vdelay1R_depth;
  private float dirty_vdelay1L_feedback;
  private float dirty_vdelay1R_feedback;
  private float dirty_vdelay1L_reverbsendgain;
  private float dirty_vdelay1R_reverbsendgain;
  private float controlrate;
  double silentcounter = 1000.0D;
  
  public SoftChorus() {}
  
  public void init(float paramFloat1, float paramFloat2)
  {
    controlrate = paramFloat2;
    vdelay1L = new LFODelay(paramFloat1, paramFloat2);
    vdelay1R = new LFODelay(paramFloat1, paramFloat2);
    vdelay1L.setGain(1.0F);
    vdelay1R.setGain(1.0F);
    vdelay1L.setPhase(1.5707963267948966D);
    vdelay1R.setPhase(0.0D);
    globalParameterControlChange(new int[] { 130 }, 0L, 2L);
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2)
  {
    if ((paramArrayOfInt.length == 1) && (paramArrayOfInt[0] == 130))
    {
      if (paramLong1 == 0L)
      {
        switch ((int)paramLong2)
        {
        case 0: 
          globalParameterControlChange(paramArrayOfInt, 3L, 0L);
          globalParameterControlChange(paramArrayOfInt, 1L, 3L);
          globalParameterControlChange(paramArrayOfInt, 2L, 5L);
          globalParameterControlChange(paramArrayOfInt, 4L, 0L);
          break;
        case 1: 
          globalParameterControlChange(paramArrayOfInt, 3L, 5L);
          globalParameterControlChange(paramArrayOfInt, 1L, 9L);
          globalParameterControlChange(paramArrayOfInt, 2L, 19L);
          globalParameterControlChange(paramArrayOfInt, 4L, 0L);
          break;
        case 2: 
          globalParameterControlChange(paramArrayOfInt, 3L, 8L);
          globalParameterControlChange(paramArrayOfInt, 1L, 3L);
          globalParameterControlChange(paramArrayOfInt, 2L, 19L);
          globalParameterControlChange(paramArrayOfInt, 4L, 0L);
          break;
        case 3: 
          globalParameterControlChange(paramArrayOfInt, 3L, 16L);
          globalParameterControlChange(paramArrayOfInt, 1L, 9L);
          globalParameterControlChange(paramArrayOfInt, 2L, 16L);
          globalParameterControlChange(paramArrayOfInt, 4L, 0L);
          break;
        case 4: 
          globalParameterControlChange(paramArrayOfInt, 3L, 64L);
          globalParameterControlChange(paramArrayOfInt, 1L, 2L);
          globalParameterControlChange(paramArrayOfInt, 2L, 24L);
          globalParameterControlChange(paramArrayOfInt, 4L, 0L);
          break;
        case 5: 
          globalParameterControlChange(paramArrayOfInt, 3L, 112L);
          globalParameterControlChange(paramArrayOfInt, 1L, 1L);
          globalParameterControlChange(paramArrayOfInt, 2L, 5L);
          globalParameterControlChange(paramArrayOfInt, 4L, 0L);
          break;
        }
      }
      else if (paramLong1 == 1L)
      {
        dirty_vdelay1L_rate = (paramLong2 * 0.122D);
        dirty_vdelay1R_rate = (paramLong2 * 0.122D);
        dirty = true;
      }
      else if (paramLong1 == 2L)
      {
        dirty_vdelay1L_depth = ((paramLong2 + 1L) / 3200.0D);
        dirty_vdelay1R_depth = ((paramLong2 + 1L) / 3200.0D);
        dirty = true;
      }
      else if (paramLong1 == 3L)
      {
        dirty_vdelay1L_feedback = ((float)paramLong2 * 0.00763F);
        dirty_vdelay1R_feedback = ((float)paramLong2 * 0.00763F);
        dirty = true;
      }
      if (paramLong1 == 4L)
      {
        rgain = ((float)paramLong2 * 0.00787F);
        dirty_vdelay1L_reverbsendgain = ((float)paramLong2 * 0.00787F);
        dirty_vdelay1R_reverbsendgain = ((float)paramLong2 * 0.00787F);
        dirty = true;
      }
    }
  }
  
  public void processControlLogic()
  {
    if (dirty)
    {
      dirty = false;
      vdelay1L.setRate(dirty_vdelay1L_rate);
      vdelay1R.setRate(dirty_vdelay1R_rate);
      vdelay1L.setDepth(dirty_vdelay1L_depth);
      vdelay1R.setDepth(dirty_vdelay1R_depth);
      vdelay1L.setFeedBack(dirty_vdelay1L_feedback);
      vdelay1R.setFeedBack(dirty_vdelay1R_feedback);
      vdelay1L.setReverbSendGain(dirty_vdelay1L_reverbsendgain);
      vdelay1R.setReverbSendGain(dirty_vdelay1R_reverbsendgain);
    }
  }
  
  public void processAudio()
  {
    if (inputA.isSilent())
    {
      silentcounter += 1.0F / controlrate;
      if (silentcounter > 1.0D) {
        if (!mix)
        {
          left.clear();
          right.clear();
        }
      }
    }
    else
    {
      silentcounter = 0.0D;
    }
    float[] arrayOfFloat1 = inputA.array();
    float[] arrayOfFloat2 = left.array();
    float[] arrayOfFloat3 = right == null ? null : right.array();
    float[] arrayOfFloat4 = rgain != 0.0F ? reverb.array() : null;
    if (mix)
    {
      vdelay1L.processMix(arrayOfFloat1, arrayOfFloat2, arrayOfFloat4);
      if (arrayOfFloat3 != null) {
        vdelay1R.processMix(arrayOfFloat1, arrayOfFloat3, arrayOfFloat4);
      }
    }
    else
    {
      vdelay1L.processReplace(arrayOfFloat1, arrayOfFloat2, arrayOfFloat4);
      if (arrayOfFloat3 != null) {
        vdelay1R.processReplace(arrayOfFloat1, arrayOfFloat3, arrayOfFloat4);
      }
    }
  }
  
  public void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      inputA = paramSoftAudioBuffer;
    }
  }
  
  public void setMixMode(boolean paramBoolean)
  {
    mix = paramBoolean;
  }
  
  public void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      left = paramSoftAudioBuffer;
    }
    if (paramInt == 1) {
      right = paramSoftAudioBuffer;
    }
    if (paramInt == 2) {
      reverb = paramSoftAudioBuffer;
    }
  }
  
  private static class LFODelay
  {
    private double phase = 1.0D;
    private double phase_step = 0.0D;
    private double depth = 0.0D;
    private SoftChorus.VariableDelay vdelay;
    private final double samplerate;
    private final double controlrate;
    
    LFODelay(double paramDouble1, double paramDouble2)
    {
      samplerate = paramDouble1;
      controlrate = paramDouble2;
      vdelay = new SoftChorus.VariableDelay((int)((depth + 10.0D) * 2.0D));
    }
    
    public void setDepth(double paramDouble)
    {
      depth = (paramDouble * samplerate);
      vdelay = new SoftChorus.VariableDelay((int)((depth + 10.0D) * 2.0D));
    }
    
    public void setRate(double paramDouble)
    {
      double d = 6.283185307179586D * (paramDouble / controlrate);
      phase_step = d;
    }
    
    public void setPhase(double paramDouble)
    {
      phase = paramDouble;
    }
    
    public void setFeedBack(float paramFloat)
    {
      vdelay.setFeedBack(paramFloat);
    }
    
    public void setGain(float paramFloat)
    {
      vdelay.setGain(paramFloat);
    }
    
    public void setReverbSendGain(float paramFloat)
    {
      vdelay.setReverbSendGain(paramFloat);
    }
    
    public void processMix(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
    {
      for (phase += phase_step; phase > 6.283185307179586D; phase -= 6.283185307179586D) {}
      vdelay.setDelay((float)(depth * 0.5D * (Math.cos(phase) + 2.0D)));
      vdelay.processMix(paramArrayOfFloat1, paramArrayOfFloat2, paramArrayOfFloat3);
    }
    
    public void processReplace(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
    {
      for (phase += phase_step; phase > 6.283185307179586D; phase -= 6.283185307179586D) {}
      vdelay.setDelay((float)(depth * 0.5D * (Math.cos(phase) + 2.0D)));
      vdelay.processReplace(paramArrayOfFloat1, paramArrayOfFloat2, paramArrayOfFloat3);
    }
  }
  
  private static class VariableDelay
  {
    private final float[] delaybuffer;
    private int rovepos = 0;
    private float gain = 1.0F;
    private float rgain = 0.0F;
    private float delay = 0.0F;
    private float lastdelay = 0.0F;
    private float feedback = 0.0F;
    
    VariableDelay(int paramInt)
    {
      delaybuffer = new float[paramInt];
    }
    
    public void setDelay(float paramFloat)
    {
      delay = paramFloat;
    }
    
    public void setFeedBack(float paramFloat)
    {
      feedback = paramFloat;
    }
    
    public void setGain(float paramFloat)
    {
      gain = paramFloat;
    }
    
    public void setReverbSendGain(float paramFloat)
    {
      rgain = paramFloat;
    }
    
    public void processMix(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
    {
      float f1 = gain;
      float f2 = delay;
      float f3 = feedback;
      float[] arrayOfFloat = delaybuffer;
      int i = paramArrayOfFloat1.length;
      float f4 = (f2 - lastdelay) / i;
      int j = arrayOfFloat.length;
      int k = rovepos;
      int m;
      float f5;
      int n;
      float f6;
      float f7;
      float f8;
      float f9;
      if (paramArrayOfFloat3 == null) {
        for (m = 0; m < i; m++)
        {
          f5 = k - (lastdelay + 2.0F) + j;
          n = (int)f5;
          f6 = f5 - n;
          f7 = arrayOfFloat[(n % j)];
          f8 = arrayOfFloat[((n + 1) % j)];
          f9 = f7 * (1.0F - f6) + f8 * f6;
          paramArrayOfFloat2[m] += f9 * f1;
          paramArrayOfFloat1[m] += f9 * f3;
          k = (k + 1) % j;
          lastdelay += f4;
        }
      } else {
        for (m = 0; m < i; m++)
        {
          f5 = k - (lastdelay + 2.0F) + j;
          n = (int)f5;
          f6 = f5 - n;
          f7 = arrayOfFloat[(n % j)];
          f8 = arrayOfFloat[((n + 1) % j)];
          f9 = f7 * (1.0F - f6) + f8 * f6;
          paramArrayOfFloat2[m] += f9 * f1;
          paramArrayOfFloat3[m] += f9 * rgain;
          paramArrayOfFloat1[m] += f9 * f3;
          k = (k + 1) % j;
          lastdelay += f4;
        }
      }
      rovepos = k;
      lastdelay = f2;
    }
    
    public void processReplace(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
    {
      Arrays.fill(paramArrayOfFloat2, 0.0F);
      Arrays.fill(paramArrayOfFloat3, 0.0F);
      processMix(paramArrayOfFloat1, paramArrayOfFloat2, paramArrayOfFloat3);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftChorus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */