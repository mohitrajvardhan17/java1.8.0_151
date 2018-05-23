package com.sun.media.sound;

public final class SoftFilter
{
  public static final int FILTERTYPE_LP6 = 0;
  public static final int FILTERTYPE_LP12 = 1;
  public static final int FILTERTYPE_HP12 = 17;
  public static final int FILTERTYPE_BP12 = 33;
  public static final int FILTERTYPE_NP12 = 49;
  public static final int FILTERTYPE_LP24 = 3;
  public static final int FILTERTYPE_HP24 = 19;
  private int filtertype = 0;
  private final float samplerate;
  private float x1;
  private float x2;
  private float y1;
  private float y2;
  private float xx1;
  private float xx2;
  private float yy1;
  private float yy2;
  private float a0;
  private float a1;
  private float a2;
  private float b1;
  private float b2;
  private float q;
  private float gain = 1.0F;
  private float wet = 0.0F;
  private float last_wet = 0.0F;
  private float last_a0;
  private float last_a1;
  private float last_a2;
  private float last_b1;
  private float last_b2;
  private float last_q;
  private float last_gain;
  private boolean last_set = false;
  private double cutoff = 44100.0D;
  private double resonancedB = 0.0D;
  private boolean dirty = true;
  
  public SoftFilter(float paramFloat)
  {
    samplerate = paramFloat;
    dirty = true;
  }
  
  public void setFrequency(double paramDouble)
  {
    if (cutoff == paramDouble) {
      return;
    }
    cutoff = paramDouble;
    dirty = true;
  }
  
  public void setResonance(double paramDouble)
  {
    if (resonancedB == paramDouble) {
      return;
    }
    resonancedB = paramDouble;
    dirty = true;
  }
  
  public void reset()
  {
    dirty = true;
    last_set = false;
    x1 = 0.0F;
    x2 = 0.0F;
    y1 = 0.0F;
    y2 = 0.0F;
    xx1 = 0.0F;
    xx2 = 0.0F;
    yy1 = 0.0F;
    yy2 = 0.0F;
    wet = 0.0F;
    gain = 1.0F;
    a0 = 0.0F;
    a1 = 0.0F;
    a2 = 0.0F;
    b1 = 0.0F;
    b2 = 0.0F;
  }
  
  public void setFilterType(int paramInt)
  {
    filtertype = paramInt;
  }
  
  public void processAudio(SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (filtertype == 0) {
      filter1(paramSoftAudioBuffer);
    }
    if (filtertype == 1) {
      filter2(paramSoftAudioBuffer);
    }
    if (filtertype == 17) {
      filter2(paramSoftAudioBuffer);
    }
    if (filtertype == 33) {
      filter2(paramSoftAudioBuffer);
    }
    if (filtertype == 49) {
      filter2(paramSoftAudioBuffer);
    }
    if (filtertype == 3) {
      filter4(paramSoftAudioBuffer);
    }
    if (filtertype == 19) {
      filter4(paramSoftAudioBuffer);
    }
  }
  
  public void filter4(SoftAudioBuffer paramSoftAudioBuffer)
  {
    float[] arrayOfFloat = paramSoftAudioBuffer.array();
    if (dirty)
    {
      filter2calc();
      dirty = false;
    }
    if (!last_set)
    {
      last_a0 = a0;
      last_a1 = a1;
      last_a2 = a2;
      last_b1 = b1;
      last_b2 = b2;
      last_gain = gain;
      last_wet = wet;
      last_set = true;
    }
    if ((wet > 0.0F) || (last_wet > 0.0F))
    {
      int i = arrayOfFloat.length;
      float f1 = last_a0;
      float f2 = last_a1;
      float f3 = last_a2;
      float f4 = last_b1;
      float f5 = last_b2;
      float f6 = last_gain;
      float f7 = last_wet;
      float f8 = (a0 - last_a0) / i;
      float f9 = (a1 - last_a1) / i;
      float f10 = (a2 - last_a2) / i;
      float f11 = (b1 - last_b1) / i;
      float f12 = (b2 - last_b2) / i;
      float f13 = (gain - last_gain) / i;
      float f14 = (wet - last_wet) / i;
      float f15 = x1;
      float f16 = x2;
      float f17 = y1;
      float f18 = y2;
      float f19 = xx1;
      float f20 = xx2;
      float f21 = yy1;
      float f22 = yy2;
      int j;
      float f23;
      float f24;
      float f25;
      float f26;
      if (f14 != 0.0F) {
        for (j = 0; j < i; j++)
        {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          f7 += f14;
          f23 = arrayOfFloat[j];
          f24 = f1 * f23 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          f25 = f24 * f6 * f7 + f23 * (1.0F - f7);
          f16 = f15;
          f15 = f23;
          f18 = f17;
          f17 = f24;
          f26 = f1 * f25 + f2 * f19 + f3 * f20 - f4 * f21 - f5 * f22;
          arrayOfFloat[j] = (f26 * f6 * f7 + f25 * (1.0F - f7));
          f20 = f19;
          f19 = f25;
          f22 = f21;
          f21 = f26;
        }
      } else if ((f8 == 0.0F) && (f9 == 0.0F) && (f10 == 0.0F) && (f11 == 0.0F) && (f12 == 0.0F)) {
        for (j = 0; j < i; j++)
        {
          f23 = arrayOfFloat[j];
          f24 = f1 * f23 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          f25 = f24 * f6 * f7 + f23 * (1.0F - f7);
          f16 = f15;
          f15 = f23;
          f18 = f17;
          f17 = f24;
          f26 = f1 * f25 + f2 * f19 + f3 * f20 - f4 * f21 - f5 * f22;
          arrayOfFloat[j] = (f26 * f6 * f7 + f25 * (1.0F - f7));
          f20 = f19;
          f19 = f25;
          f22 = f21;
          f21 = f26;
        }
      } else {
        for (j = 0; j < i; j++)
        {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          f23 = arrayOfFloat[j];
          f24 = f1 * f23 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          f25 = f24 * f6 * f7 + f23 * (1.0F - f7);
          f16 = f15;
          f15 = f23;
          f18 = f17;
          f17 = f24;
          f26 = f1 * f25 + f2 * f19 + f3 * f20 - f4 * f21 - f5 * f22;
          arrayOfFloat[j] = (f26 * f6 * f7 + f25 * (1.0F - f7));
          f20 = f19;
          f19 = f25;
          f22 = f21;
          f21 = f26;
        }
      }
      if (Math.abs(f15) < 1.0E-8D) {
        f15 = 0.0F;
      }
      if (Math.abs(f16) < 1.0E-8D) {
        f16 = 0.0F;
      }
      if (Math.abs(f17) < 1.0E-8D) {
        f17 = 0.0F;
      }
      if (Math.abs(f18) < 1.0E-8D) {
        f18 = 0.0F;
      }
      x1 = f15;
      x2 = f16;
      y1 = f17;
      y2 = f18;
      xx1 = f19;
      xx2 = f20;
      yy1 = f21;
      yy2 = f22;
    }
    last_a0 = a0;
    last_a1 = a1;
    last_a2 = a2;
    last_b1 = b1;
    last_b2 = b2;
    last_gain = gain;
    last_wet = wet;
  }
  
  private double sinh(double paramDouble)
  {
    return (Math.exp(paramDouble) - Math.exp(-paramDouble)) * 0.5D;
  }
  
  public void filter2calc()
  {
    double d1 = resonancedB;
    if (d1 < 0.0D) {
      d1 = 0.0D;
    }
    if (d1 > 30.0D) {
      d1 = 30.0D;
    }
    if ((filtertype == 3) || (filtertype == 19)) {
      d1 *= 0.6D;
    }
    double d2;
    double d3;
    double d4;
    double d5;
    double d6;
    double d7;
    double d8;
    double d9;
    double d10;
    double d11;
    double d12;
    double d13;
    double d14;
    if (filtertype == 33)
    {
      wet = 1.0F;
      d2 = cutoff / samplerate;
      if (d2 > 0.45D) {
        d2 = 0.45D;
      }
      d3 = 3.141592653589793D * Math.pow(10.0D, -(d1 / 20.0D));
      d4 = 6.283185307179586D * d2;
      d5 = Math.cos(d4);
      d6 = Math.sin(d4);
      d7 = d6 * sinh(Math.log(2.0D) * d3 * d4 / (d6 * 2.0D));
      d8 = d7;
      d9 = 0.0D;
      d10 = -d7;
      d11 = 1.0D + d7;
      d12 = -2.0D * d5;
      d13 = 1.0D - d7;
      d14 = 1.0D / d11;
      b1 = ((float)(d12 * d14));
      b2 = ((float)(d13 * d14));
      a0 = ((float)(d8 * d14));
      a1 = ((float)(d9 * d14));
      a2 = ((float)(d10 * d14));
    }
    if (filtertype == 49)
    {
      wet = 1.0F;
      d2 = cutoff / samplerate;
      if (d2 > 0.45D) {
        d2 = 0.45D;
      }
      d3 = 3.141592653589793D * Math.pow(10.0D, -(d1 / 20.0D));
      d4 = 6.283185307179586D * d2;
      d5 = Math.cos(d4);
      d6 = Math.sin(d4);
      d7 = d6 * sinh(Math.log(2.0D) * d3 * d4 / (d6 * 2.0D));
      d8 = 1.0D;
      d9 = -2.0D * d5;
      d10 = 1.0D;
      d11 = 1.0D + d7;
      d12 = -2.0D * d5;
      d13 = 1.0D - d7;
      d14 = 1.0D / d11;
      b1 = ((float)(d12 * d14));
      b2 = ((float)(d13 * d14));
      a0 = ((float)(d8 * d14));
      a1 = ((float)(d9 * d14));
      a2 = ((float)(d10 * d14));
    }
    if ((filtertype == 1) || (filtertype == 3))
    {
      d2 = cutoff / samplerate;
      if (d2 > 0.45D)
      {
        if (wet == 0.0F) {
          if (d1 < 1.0E-5D) {
            wet = 0.0F;
          } else {
            wet = 1.0F;
          }
        }
        d2 = 0.45D;
      }
      else
      {
        wet = 1.0F;
      }
      d3 = 1.0D / Math.tan(3.141592653589793D * d2);
      d4 = d3 * d3;
      d5 = Math.pow(10.0D, -(d1 / 20.0D));
      d6 = Math.sqrt(2.0D) * d5;
      d7 = 1.0D / (1.0D + d6 * d3 + d4);
      d8 = 2.0D * d7;
      d9 = d7;
      d10 = 2.0D * d7 * (1.0D - d4);
      d11 = d7 * (1.0D - d6 * d3 + d4);
      a0 = ((float)d7);
      a1 = ((float)d8);
      a2 = ((float)d9);
      b1 = ((float)d10);
      b2 = ((float)d11);
    }
    if ((filtertype == 17) || (filtertype == 19))
    {
      d2 = cutoff / samplerate;
      if (d2 > 0.45D) {
        d2 = 0.45D;
      }
      if (d2 < 1.0E-4D) {
        d2 = 1.0E-4D;
      }
      wet = 1.0F;
      d3 = Math.tan(3.141592653589793D * d2);
      d4 = d3 * d3;
      d5 = Math.pow(10.0D, -(d1 / 20.0D));
      d6 = Math.sqrt(2.0D) * d5;
      d7 = 1.0D / (1.0D + d6 * d3 + d4);
      d8 = -2.0D * d7;
      d9 = d7;
      d10 = 2.0D * d7 * (d4 - 1.0D);
      d11 = d7 * (1.0D - d6 * d3 + d4);
      a0 = ((float)d7);
      a1 = ((float)d8);
      a2 = ((float)d9);
      b1 = ((float)d10);
      b2 = ((float)d11);
    }
  }
  
  public void filter2(SoftAudioBuffer paramSoftAudioBuffer)
  {
    float[] arrayOfFloat = paramSoftAudioBuffer.array();
    if (dirty)
    {
      filter2calc();
      dirty = false;
    }
    if (!last_set)
    {
      last_a0 = a0;
      last_a1 = a1;
      last_a2 = a2;
      last_b1 = b1;
      last_b2 = b2;
      last_q = q;
      last_gain = gain;
      last_wet = wet;
      last_set = true;
    }
    if ((wet > 0.0F) || (last_wet > 0.0F))
    {
      int i = arrayOfFloat.length;
      float f1 = last_a0;
      float f2 = last_a1;
      float f3 = last_a2;
      float f4 = last_b1;
      float f5 = last_b2;
      float f6 = last_gain;
      float f7 = last_wet;
      float f8 = (a0 - last_a0) / i;
      float f9 = (a1 - last_a1) / i;
      float f10 = (a2 - last_a2) / i;
      float f11 = (b1 - last_b1) / i;
      float f12 = (b2 - last_b2) / i;
      float f13 = (gain - last_gain) / i;
      float f14 = (wet - last_wet) / i;
      float f15 = x1;
      float f16 = x2;
      float f17 = y1;
      float f18 = y2;
      int j;
      float f19;
      float f20;
      if (f14 != 0.0F) {
        for (j = 0; j < i; j++)
        {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          f7 += f14;
          f19 = arrayOfFloat[j];
          f20 = f1 * f19 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          arrayOfFloat[j] = (f20 * f6 * f7 + f19 * (1.0F - f7));
          f16 = f15;
          f15 = f19;
          f18 = f17;
          f17 = f20;
        }
      } else if ((f8 == 0.0F) && (f9 == 0.0F) && (f10 == 0.0F) && (f11 == 0.0F) && (f12 == 0.0F)) {
        for (j = 0; j < i; j++)
        {
          f19 = arrayOfFloat[j];
          f20 = f1 * f19 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          arrayOfFloat[j] = (f20 * f6);
          f16 = f15;
          f15 = f19;
          f18 = f17;
          f17 = f20;
        }
      } else {
        for (j = 0; j < i; j++)
        {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          f19 = arrayOfFloat[j];
          f20 = f1 * f19 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          arrayOfFloat[j] = (f20 * f6);
          f16 = f15;
          f15 = f19;
          f18 = f17;
          f17 = f20;
        }
      }
      if (Math.abs(f15) < 1.0E-8D) {
        f15 = 0.0F;
      }
      if (Math.abs(f16) < 1.0E-8D) {
        f16 = 0.0F;
      }
      if (Math.abs(f17) < 1.0E-8D) {
        f17 = 0.0F;
      }
      if (Math.abs(f18) < 1.0E-8D) {
        f18 = 0.0F;
      }
      x1 = f15;
      x2 = f16;
      y1 = f17;
      y2 = f18;
    }
    last_a0 = a0;
    last_a1 = a1;
    last_a2 = a2;
    last_b1 = b1;
    last_b2 = b2;
    last_q = q;
    last_gain = gain;
    last_wet = wet;
  }
  
  public void filter1calc()
  {
    if (cutoff < 120.0D) {
      cutoff = 120.0D;
    }
    double d = 7.3303828583761845D * cutoff / samplerate;
    if (d > 1.0D) {
      d = 1.0D;
    }
    a0 = ((float)(Math.sqrt(1.0D - Math.cos(d)) * Math.sqrt(1.5707963267948966D)));
    if (resonancedB < 0.0D) {
      resonancedB = 0.0D;
    }
    if (resonancedB > 20.0D) {
      resonancedB = 20.0D;
    }
    q = ((float)(Math.sqrt(0.5D) * Math.pow(10.0D, -(resonancedB / 20.0D))));
    gain = ((float)Math.pow(10.0D, -resonancedB / 40.0D));
    if ((wet == 0.0F) && ((resonancedB > 1.0E-5D) || (d < 0.9999999D))) {
      wet = 1.0F;
    }
  }
  
  public void filter1(SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (dirty)
    {
      filter1calc();
      dirty = false;
    }
    if (!last_set)
    {
      last_a0 = a0;
      last_q = q;
      last_gain = gain;
      last_wet = wet;
      last_set = true;
    }
    if ((wet > 0.0F) || (last_wet > 0.0F))
    {
      float[] arrayOfFloat = paramSoftAudioBuffer.array();
      int i = arrayOfFloat.length;
      float f1 = last_a0;
      float f2 = last_q;
      float f3 = last_gain;
      float f4 = last_wet;
      float f5 = (a0 - last_a0) / i;
      float f6 = (q - last_q) / i;
      float f7 = (gain - last_gain) / i;
      float f8 = (wet - last_wet) / i;
      float f9 = y2;
      float f10 = y1;
      if (f8 != 0.0F)
      {
        for (int j = 0; j < i; j++)
        {
          f1 += f5;
          f2 += f6;
          f3 += f7;
          f4 += f8;
          float f12 = 1.0F - f2 * f1;
          f10 = f12 * f10 + f1 * (arrayOfFloat[j] - f9);
          f9 = f12 * f9 + f1 * f10;
          arrayOfFloat[j] = (f9 * f3 * f4 + arrayOfFloat[j] * (1.0F - f4));
        }
      }
      else if ((f5 == 0.0F) && (f6 == 0.0F))
      {
        float f11 = 1.0F - f2 * f1;
        for (int m = 0; m < i; m++)
        {
          f10 = f11 * f10 + f1 * (arrayOfFloat[m] - f9);
          f9 = f11 * f9 + f1 * f10;
          arrayOfFloat[m] = (f9 * f3);
        }
      }
      else
      {
        for (int k = 0; k < i; k++)
        {
          f1 += f5;
          f2 += f6;
          f3 += f7;
          float f13 = 1.0F - f2 * f1;
          f10 = f13 * f10 + f1 * (arrayOfFloat[k] - f9);
          f9 = f13 * f9 + f1 * f10;
          arrayOfFloat[k] = (f9 * f3);
        }
      }
      if (Math.abs(f9) < 1.0E-8D) {
        f9 = 0.0F;
      }
      if (Math.abs(f10) < 1.0E-8D) {
        f10 = 0.0F;
      }
      y2 = f9;
      y1 = f10;
    }
    last_a0 = a0;
    last_q = q;
    last_gain = gain;
    last_wet = wet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */