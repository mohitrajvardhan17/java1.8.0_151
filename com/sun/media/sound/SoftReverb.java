package com.sun.media.sound;

import java.util.Arrays;

public final class SoftReverb
  implements SoftAudioProcessor
{
  private float roomsize;
  private float damp;
  private float gain = 1.0F;
  private Delay delay;
  private Comb[] combL;
  private Comb[] combR;
  private AllPass[] allpassL;
  private AllPass[] allpassR;
  private float[] input;
  private float[] out;
  private float[] pre1;
  private float[] pre2;
  private float[] pre3;
  private boolean denormal_flip = false;
  private boolean mix = true;
  private SoftAudioBuffer inputA;
  private SoftAudioBuffer left;
  private SoftAudioBuffer right;
  private boolean dirty = true;
  private float dirty_roomsize;
  private float dirty_damp;
  private float dirty_predelay;
  private float dirty_gain;
  private float samplerate;
  private boolean light = true;
  private boolean silent = true;
  
  public SoftReverb() {}
  
  public void init(float paramFloat1, float paramFloat2)
  {
    samplerate = paramFloat1;
    double d = paramFloat1 / 44100.0D;
    int i = 23;
    delay = new Delay();
    combL = new Comb[8];
    combR = new Comb[8];
    combL[0] = new Comb((int)(d * 1116.0D));
    combR[0] = new Comb((int)(d * (1116 + i)));
    combL[1] = new Comb((int)(d * 1188.0D));
    combR[1] = new Comb((int)(d * (1188 + i)));
    combL[2] = new Comb((int)(d * 1277.0D));
    combR[2] = new Comb((int)(d * (1277 + i)));
    combL[3] = new Comb((int)(d * 1356.0D));
    combR[3] = new Comb((int)(d * (1356 + i)));
    combL[4] = new Comb((int)(d * 1422.0D));
    combR[4] = new Comb((int)(d * (1422 + i)));
    combL[5] = new Comb((int)(d * 1491.0D));
    combR[5] = new Comb((int)(d * (1491 + i)));
    combL[6] = new Comb((int)(d * 1557.0D));
    combR[6] = new Comb((int)(d * (1557 + i)));
    combL[7] = new Comb((int)(d * 1617.0D));
    combR[7] = new Comb((int)(d * (1617 + i)));
    allpassL = new AllPass[4];
    allpassR = new AllPass[4];
    allpassL[0] = new AllPass((int)(d * 556.0D));
    allpassR[0] = new AllPass((int)(d * (556 + i)));
    allpassL[1] = new AllPass((int)(d * 441.0D));
    allpassR[1] = new AllPass((int)(d * (441 + i)));
    allpassL[2] = new AllPass((int)(d * 341.0D));
    allpassR[2] = new AllPass((int)(d * (341 + i)));
    allpassL[3] = new AllPass((int)(d * 225.0D));
    allpassR[3] = new AllPass((int)(d * (225 + i)));
    for (int j = 0; j < allpassL.length; j++)
    {
      allpassL[j].setFeedBack(0.5F);
      allpassR[j].setFeedBack(0.5F);
    }
    globalParameterControlChange(new int[] { 129 }, 0L, 4L);
  }
  
  public void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      inputA = paramSoftAudioBuffer;
    }
  }
  
  public void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      left = paramSoftAudioBuffer;
    }
    if (paramInt == 1) {
      right = paramSoftAudioBuffer;
    }
  }
  
  public void setMixMode(boolean paramBoolean)
  {
    mix = paramBoolean;
  }
  
  public void processAudio()
  {
    boolean bool = inputA.isSilent();
    if (!bool) {
      silent = false;
    }
    if (silent)
    {
      if (!mix)
      {
        left.clear();
        right.clear();
      }
      return;
    }
    float[] arrayOfFloat1 = inputA.array();
    float[] arrayOfFloat2 = left.array();
    float[] arrayOfFloat3 = right == null ? null : right.array();
    int i = arrayOfFloat1.length;
    if ((input == null) || (input.length < i)) {
      input = new float[i];
    }
    float f1 = gain * 0.018F / 2.0F;
    denormal_flip = (!denormal_flip);
    int j;
    if (denormal_flip) {
      for (j = 0; j < i; j++) {
        input[j] = (arrayOfFloat1[j] * f1 + 1.0E-20F);
      }
    } else {
      for (j = 0; j < i; j++) {
        input[j] = (arrayOfFloat1[j] * f1 - 1.0E-20F);
      }
    }
    delay.processReplace(input);
    float f2;
    if ((light) && (arrayOfFloat3 != null))
    {
      if ((pre1 == null) || (pre1.length < i))
      {
        pre1 = new float[i];
        pre2 = new float[i];
        pre3 = new float[i];
      }
      for (j = 0; j < allpassL.length; j++) {
        allpassL[j].processReplace(input);
      }
      combL[0].processReplace(input, pre3);
      combL[1].processReplace(input, pre3);
      combL[2].processReplace(input, pre1);
      for (j = 4; j < combL.length - 2; j += 2) {
        combL[j].processMix(input, pre1);
      }
      combL[3].processReplace(input, pre2);
      for (j = 5; j < combL.length - 2; j += 2) {
        combL[j].processMix(input, pre2);
      }
      if (!mix)
      {
        Arrays.fill(arrayOfFloat3, 0.0F);
        Arrays.fill(arrayOfFloat2, 0.0F);
      }
      for (j = combR.length - 2; j < combR.length; j++) {
        combR[j].processMix(input, arrayOfFloat3);
      }
      for (j = combL.length - 2; j < combL.length; j++) {
        combL[j].processMix(input, arrayOfFloat2);
      }
      for (j = 0; j < i; j++)
      {
        f2 = pre1[j] - pre2[j];
        float f3 = pre3[j];
        arrayOfFloat2[j] += f3 + f2;
        arrayOfFloat3[j] += f3 - f2;
      }
    }
    else
    {
      if ((out == null) || (out.length < i)) {
        out = new float[i];
      }
      if (arrayOfFloat3 != null)
      {
        if (!mix) {
          Arrays.fill(arrayOfFloat3, 0.0F);
        }
        allpassR[0].processReplace(input, out);
        for (j = 1; j < allpassR.length; j++) {
          allpassR[j].processReplace(out);
        }
        for (j = 0; j < combR.length; j++) {
          combR[j].processMix(out, arrayOfFloat3);
        }
      }
      if (!mix) {
        Arrays.fill(arrayOfFloat2, 0.0F);
      }
      allpassL[0].processReplace(input, out);
      for (j = 1; j < allpassL.length; j++) {
        allpassL[j].processReplace(out);
      }
      for (j = 0; j < combL.length; j++) {
        combL[j].processMix(out, arrayOfFloat2);
      }
    }
    if (bool)
    {
      silent = true;
      for (j = 0; j < i; j++)
      {
        f2 = arrayOfFloat2[j];
        if ((f2 > 1.0E-10D) || (f2 < -1.0E-10D))
        {
          silent = false;
          break;
        }
      }
    }
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2)
  {
    if ((paramArrayOfInt.length == 1) && (paramArrayOfInt[0] == 129)) {
      if (paramLong1 == 0L)
      {
        if (paramLong2 == 0L)
        {
          dirty_roomsize = 1.1F;
          dirty_damp = 5000.0F;
          dirty_predelay = 0.0F;
          dirty_gain = 4.0F;
          dirty = true;
        }
        if (paramLong2 == 1L)
        {
          dirty_roomsize = 1.3F;
          dirty_damp = 5000.0F;
          dirty_predelay = 0.0F;
          dirty_gain = 3.0F;
          dirty = true;
        }
        if (paramLong2 == 2L)
        {
          dirty_roomsize = 1.5F;
          dirty_damp = 5000.0F;
          dirty_predelay = 0.0F;
          dirty_gain = 2.0F;
          dirty = true;
        }
        if (paramLong2 == 3L)
        {
          dirty_roomsize = 1.8F;
          dirty_damp = 24000.0F;
          dirty_predelay = 0.02F;
          dirty_gain = 1.5F;
          dirty = true;
        }
        if (paramLong2 == 4L)
        {
          dirty_roomsize = 1.8F;
          dirty_damp = 24000.0F;
          dirty_predelay = 0.03F;
          dirty_gain = 1.5F;
          dirty = true;
        }
        if (paramLong2 == 8L)
        {
          dirty_roomsize = 1.3F;
          dirty_damp = 2500.0F;
          dirty_predelay = 0.0F;
          dirty_gain = 6.0F;
          dirty = true;
        }
      }
      else if (paramLong1 == 1L)
      {
        dirty_roomsize = ((float)Math.exp((paramLong2 - 40L) * 0.025D));
        dirty = true;
      }
    }
  }
  
  public void processControlLogic()
  {
    if (dirty)
    {
      dirty = false;
      setRoomSize(dirty_roomsize);
      setDamp(dirty_damp);
      setPreDelay(dirty_predelay);
      setGain(dirty_gain);
    }
  }
  
  public void setRoomSize(float paramFloat)
  {
    roomsize = (1.0F - 0.17F / paramFloat);
    for (int i = 0; i < combL.length; i++)
    {
      combL[i].feedback = roomsize;
      combR[i].feedback = roomsize;
    }
  }
  
  public void setPreDelay(float paramFloat)
  {
    delay.setDelay((int)(paramFloat * samplerate));
  }
  
  public void setGain(float paramFloat)
  {
    gain = paramFloat;
  }
  
  public void setDamp(float paramFloat)
  {
    double d1 = paramFloat / samplerate * 6.283185307179586D;
    double d2 = 2.0D - Math.cos(d1);
    damp = ((float)(d2 - Math.sqrt(d2 * d2 - 1.0D)));
    if (damp > 1.0F) {
      damp = 1.0F;
    }
    if (damp < 0.0F) {
      damp = 0.0F;
    }
    for (int i = 0; i < combL.length; i++)
    {
      combL[i].setDamp(damp);
      combR[i].setDamp(damp);
    }
  }
  
  public void setLightMode(boolean paramBoolean)
  {
    light = paramBoolean;
  }
  
  private static final class AllPass
  {
    private final float[] delaybuffer;
    private final int delaybuffersize;
    private int rovepos = 0;
    private float feedback;
    
    AllPass(int paramInt)
    {
      delaybuffer = new float[paramInt];
      delaybuffersize = paramInt;
    }
    
    public void setFeedBack(float paramFloat)
    {
      feedback = paramFloat;
    }
    
    public void processReplace(float[] paramArrayOfFloat)
    {
      int i = paramArrayOfFloat.length;
      int j = delaybuffersize;
      int k = rovepos;
      for (int m = 0; m < i; m++)
      {
        float f1 = delaybuffer[k];
        float f2 = paramArrayOfFloat[m];
        paramArrayOfFloat[m] = (f1 - f2);
        delaybuffer[k] = (f2 + f1 * feedback);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      rovepos = k;
    }
    
    public void processReplace(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = paramArrayOfFloat1.length;
      int j = delaybuffersize;
      int k = rovepos;
      for (int m = 0; m < i; m++)
      {
        float f1 = delaybuffer[k];
        float f2 = paramArrayOfFloat1[m];
        paramArrayOfFloat2[m] = (f1 - f2);
        delaybuffer[k] = (f2 + f1 * feedback);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      rovepos = k;
    }
  }
  
  private static final class Comb
  {
    private final float[] delaybuffer;
    private final int delaybuffersize;
    private int rovepos = 0;
    private float feedback;
    private float filtertemp = 0.0F;
    private float filtercoeff1 = 0.0F;
    private float filtercoeff2 = 1.0F;
    
    Comb(int paramInt)
    {
      delaybuffer = new float[paramInt];
      delaybuffersize = paramInt;
    }
    
    public void setFeedBack(float paramFloat)
    {
      feedback = paramFloat;
      filtercoeff2 = ((1.0F - filtercoeff1) * paramFloat);
    }
    
    public void processMix(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = paramArrayOfFloat1.length;
      int j = delaybuffersize;
      int k = rovepos;
      float f1 = filtertemp;
      float f2 = filtercoeff1;
      float f3 = filtercoeff2;
      for (int m = 0; m < i; m++)
      {
        float f4 = delaybuffer[k];
        f1 = f4 * f3 + f1 * f2;
        paramArrayOfFloat2[m] += f4;
        delaybuffer[k] = (paramArrayOfFloat1[m] + f1);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      filtertemp = f1;
      rovepos = k;
    }
    
    public void processReplace(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = paramArrayOfFloat1.length;
      int j = delaybuffersize;
      int k = rovepos;
      float f1 = filtertemp;
      float f2 = filtercoeff1;
      float f3 = filtercoeff2;
      for (int m = 0; m < i; m++)
      {
        float f4 = delaybuffer[k];
        f1 = f4 * f3 + f1 * f2;
        paramArrayOfFloat2[m] = f4;
        delaybuffer[k] = (paramArrayOfFloat1[m] + f1);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      filtertemp = f1;
      rovepos = k;
    }
    
    public void setDamp(float paramFloat)
    {
      filtercoeff1 = paramFloat;
      filtercoeff2 = ((1.0F - filtercoeff1) * feedback);
    }
  }
  
  private static final class Delay
  {
    private float[] delaybuffer = null;
    private int rovepos = 0;
    
    Delay() {}
    
    public void setDelay(int paramInt)
    {
      if (paramInt == 0) {
        delaybuffer = null;
      } else {
        delaybuffer = new float[paramInt];
      }
      rovepos = 0;
    }
    
    public void processReplace(float[] paramArrayOfFloat)
    {
      if (delaybuffer == null) {
        return;
      }
      int i = paramArrayOfFloat.length;
      int j = delaybuffer.length;
      int k = rovepos;
      for (int m = 0; m < i; m++)
      {
        float f = paramArrayOfFloat[m];
        paramArrayOfFloat[m] = delaybuffer[k];
        delaybuffer[k] = f;
        k++;
        if (k == j) {
          k = 0;
        }
      }
      rovepos = k;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftReverb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */