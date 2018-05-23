package com.sun.media.sound;

public final class SoftLowFrequencyOscillator
  implements SoftProcess
{
  private final int max_count = 10;
  private int used_count = 0;
  private final double[][] out = new double[10][1];
  private final double[][] delay = new double[10][1];
  private final double[][] delay2 = new double[10][1];
  private final double[][] freq = new double[10][1];
  private final int[] delay_counter = new int[10];
  private final double[] sin_phase = new double[10];
  private final double[] sin_stepfreq = new double[10];
  private final double[] sin_step = new double[10];
  private double control_time = 0.0D;
  private double sin_factor = 0.0D;
  private static final double PI2 = 6.283185307179586D;
  
  public SoftLowFrequencyOscillator()
  {
    for (int i = 0; i < sin_stepfreq.length; i++) {
      sin_stepfreq[i] = Double.NEGATIVE_INFINITY;
    }
  }
  
  public void reset()
  {
    for (int i = 0; i < used_count; i++)
    {
      out[i][0] = 0.0D;
      delay[i][0] = 0.0D;
      delay2[i][0] = 0.0D;
      freq[i][0] = 0.0D;
      delay_counter[i] = 0;
      sin_phase[i] = 0.0D;
      sin_stepfreq[i] = Double.NEGATIVE_INFINITY;
      sin_step[i] = 0.0D;
    }
    used_count = 0;
  }
  
  public void init(SoftSynthesizer paramSoftSynthesizer)
  {
    control_time = (1.0D / paramSoftSynthesizer.getControlRate());
    sin_factor = (control_time * 2.0D * 3.141592653589793D);
    for (int i = 0; i < used_count; i++)
    {
      delay_counter[i] = ((int)(Math.pow(2.0D, delay[i][0] / 1200.0D) / control_time));
      delay_counter[i] += (int)(delay2[i][0] / (control_time * 1000.0D));
    }
    processControlLogic();
  }
  
  public void processControlLogic()
  {
    for (int i = 0; i < used_count; i++) {
      if (delay_counter[i] > 0)
      {
        delay_counter[i] -= 1;
        out[i][0] = 0.5D;
      }
      else
      {
        double d1 = freq[i][0];
        if (sin_stepfreq[i] != d1)
        {
          sin_stepfreq[i] = d1;
          d2 = 440.0D * Math.exp((d1 - 6900.0D) * (Math.log(2.0D) / 1200.0D));
          sin_step[i] = (d2 * sin_factor);
        }
        double d2 = sin_phase[i];
        for (d2 += sin_step[i]; d2 > 6.283185307179586D; d2 -= 6.283185307179586D) {}
        out[i][0] = (0.5D + Math.sin(d2) * 0.5D);
        sin_phase[i] = d2;
      }
    }
  }
  
  public double[] get(int paramInt, String paramString)
  {
    if (paramInt >= used_count) {
      used_count = (paramInt + 1);
    }
    if (paramString == null) {
      return out[paramInt];
    }
    if (paramString.equals("delay")) {
      return delay[paramInt];
    }
    if (paramString.equals("delay2")) {
      return delay2[paramInt];
    }
    if (paramString.equals("freq")) {
      return freq[paramInt];
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftLowFrequencyOscillator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */