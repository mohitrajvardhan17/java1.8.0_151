package com.sun.media.sound;

public final class SoftEnvelopeGenerator
  implements SoftProcess
{
  public static final int EG_OFF = 0;
  public static final int EG_DELAY = 1;
  public static final int EG_ATTACK = 2;
  public static final int EG_HOLD = 3;
  public static final int EG_DECAY = 4;
  public static final int EG_SUSTAIN = 5;
  public static final int EG_RELEASE = 6;
  public static final int EG_SHUTDOWN = 7;
  public static final int EG_END = 8;
  int max_count = 10;
  int used_count = 0;
  private final int[] stage = new int[max_count];
  private final int[] stage_ix = new int[max_count];
  private final double[] stage_v = new double[max_count];
  private final int[] stage_count = new int[max_count];
  private final double[][] on = new double[max_count][1];
  private final double[][] active = new double[max_count][1];
  private final double[][] out = new double[max_count][1];
  private final double[][] delay = new double[max_count][1];
  private final double[][] attack = new double[max_count][1];
  private final double[][] hold = new double[max_count][1];
  private final double[][] decay = new double[max_count][1];
  private final double[][] sustain = new double[max_count][1];
  private final double[][] release = new double[max_count][1];
  private final double[][] shutdown = new double[max_count][1];
  private final double[][] release2 = new double[max_count][1];
  private final double[][] attack2 = new double[max_count][1];
  private final double[][] decay2 = new double[max_count][1];
  private double control_time = 0.0D;
  
  public SoftEnvelopeGenerator() {}
  
  public void reset()
  {
    for (int i = 0; i < used_count; i++)
    {
      stage[i] = 0;
      on[i][0] = 0.0D;
      out[i][0] = 0.0D;
      delay[i][0] = 0.0D;
      attack[i][0] = 0.0D;
      hold[i][0] = 0.0D;
      decay[i][0] = 0.0D;
      sustain[i][0] = 0.0D;
      release[i][0] = 0.0D;
      shutdown[i][0] = 0.0D;
      attack2[i][0] = 0.0D;
      decay2[i][0] = 0.0D;
      release2[i][0] = 0.0D;
    }
    used_count = 0;
  }
  
  public void init(SoftSynthesizer paramSoftSynthesizer)
  {
    control_time = (1.0D / paramSoftSynthesizer.getControlRate());
    processControlLogic();
  }
  
  public double[] get(int paramInt, String paramString)
  {
    if (paramInt >= used_count) {
      used_count = (paramInt + 1);
    }
    if (paramString == null) {
      return out[paramInt];
    }
    if (paramString.equals("on")) {
      return on[paramInt];
    }
    if (paramString.equals("active")) {
      return active[paramInt];
    }
    if (paramString.equals("delay")) {
      return delay[paramInt];
    }
    if (paramString.equals("attack")) {
      return attack[paramInt];
    }
    if (paramString.equals("hold")) {
      return hold[paramInt];
    }
    if (paramString.equals("decay")) {
      return decay[paramInt];
    }
    if (paramString.equals("sustain")) {
      return sustain[paramInt];
    }
    if (paramString.equals("release")) {
      return release[paramInt];
    }
    if (paramString.equals("shutdown")) {
      return shutdown[paramInt];
    }
    if (paramString.equals("attack2")) {
      return attack2[paramInt];
    }
    if (paramString.equals("decay2")) {
      return decay2[paramInt];
    }
    if (paramString.equals("release2")) {
      return release2[paramInt];
    }
    return null;
  }
  
  public void processControlLogic()
  {
    for (int i = 0; i < used_count; i++) {
      if (stage[i] != 8)
      {
        double d1;
        if ((stage[i] > 0) && (stage[i] < 6) && (on[i][0] < 0.5D)) {
          if (on[i][0] < -0.5D)
          {
            stage_count[i] = ((int)(Math.pow(2.0D, shutdown[i][0] / 1200.0D) / control_time));
            if (stage_count[i] < 0) {
              stage_count[i] = 0;
            }
            stage_v[i] = out[i][0];
            stage_ix[i] = 0;
            stage[i] = 7;
          }
          else
          {
            if ((release2[i][0] < 1.0E-6D) && (release[i][0] < 0.0D) && (Double.isInfinite(release[i][0])))
            {
              out[i][0] = 0.0D;
              active[i][0] = 0.0D;
              stage[i] = 8;
              continue;
            }
            stage_count[i] = ((int)(Math.pow(2.0D, release[i][0] / 1200.0D) / control_time));
            stage_count[i] += (int)(release2[i][0] / (control_time * 1000.0D));
            if (stage_count[i] < 0) {
              stage_count[i] = 0;
            }
            stage_ix[i] = 0;
            d1 = 1.0D - out[i][0];
            stage_ix[i] = ((int)(stage_count[i] * d1));
            stage[i] = 6;
          }
        }
        double d2;
        switch (stage[i])
        {
        case 0: 
          active[i][0] = 1.0D;
          if (on[i][0] >= 0.5D)
          {
            stage[i] = 1;
            stage_ix[i] = ((int)(Math.pow(2.0D, delay[i][0] / 1200.0D) / control_time));
            if (stage_ix[i] < 0) {
              stage_ix[i] = 0;
            }
          }
          break;
        case 1: 
          if (stage_ix[i] == 0)
          {
            d1 = attack[i][0];
            d2 = attack2[i][0];
            if ((d2 < 1.0E-6D) && (d1 < 0.0D) && (Double.isInfinite(d1)))
            {
              out[i][0] = 1.0D;
              stage[i] = 3;
              stage_count[i] = ((int)(Math.pow(2.0D, hold[i][0] / 1200.0D) / control_time));
              stage_ix[i] = 0;
            }
            else
            {
              stage[i] = 2;
              stage_count[i] = ((int)(Math.pow(2.0D, d1 / 1200.0D) / control_time));
              stage_count[i] += (int)(d2 / (control_time * 1000.0D));
              if (stage_count[i] < 0) {
                stage_count[i] = 0;
              }
              stage_ix[i] = 0;
            }
          }
          else
          {
            stage_ix[i] -= 1;
          }
          break;
        case 2: 
          stage_ix[i] += 1;
          if (stage_ix[i] >= stage_count[i])
          {
            out[i][0] = 1.0D;
            stage[i] = 3;
          }
          else
          {
            d1 = stage_ix[i] / stage_count[i];
            d1 = 1.0D + 0.4166666666666667D / Math.log(10.0D) * Math.log(d1);
            if (d1 < 0.0D) {
              d1 = 0.0D;
            } else if (d1 > 1.0D) {
              d1 = 1.0D;
            }
            out[i][0] = d1;
          }
          break;
        case 3: 
          stage_ix[i] += 1;
          if (stage_ix[i] >= stage_count[i])
          {
            stage[i] = 4;
            stage_count[i] = ((int)(Math.pow(2.0D, decay[i][0] / 1200.0D) / control_time));
            stage_count[i] += (int)(decay2[i][0] / (control_time * 1000.0D));
            if (stage_count[i] < 0) {
              stage_count[i] = 0;
            }
            stage_ix[i] = 0;
          }
          break;
        case 4: 
          stage_ix[i] += 1;
          d1 = sustain[i][0] * 0.001D;
          if (stage_ix[i] >= stage_count[i])
          {
            out[i][0] = d1;
            stage[i] = 5;
            if (d1 < 0.001D)
            {
              out[i][0] = 0.0D;
              active[i][0] = 0.0D;
              stage[i] = 8;
            }
          }
          else
          {
            d2 = stage_ix[i] / stage_count[i];
            out[i][0] = (1.0D - d2 + d1 * d2);
          }
          break;
        case 5: 
          break;
        case 6: 
          stage_ix[i] += 1;
          if (stage_ix[i] >= stage_count[i])
          {
            out[i][0] = 0.0D;
            active[i][0] = 0.0D;
            stage[i] = 8;
          }
          else
          {
            d2 = stage_ix[i] / stage_count[i];
            out[i][0] = (1.0D - d2);
            if (on[i][0] < -0.5D)
            {
              stage_count[i] = ((int)(Math.pow(2.0D, shutdown[i][0] / 1200.0D) / control_time));
              if (stage_count[i] < 0) {
                stage_count[i] = 0;
              }
              stage_v[i] = out[i][0];
              stage_ix[i] = 0;
              stage[i] = 7;
            }
            if (on[i][0] > 0.5D)
            {
              d1 = sustain[i][0] * 0.001D;
              if (out[i][0] > d1)
              {
                stage[i] = 4;
                stage_count[i] = ((int)(Math.pow(2.0D, decay[i][0] / 1200.0D) / control_time));
                stage_count[i] += (int)(decay2[i][0] / (control_time * 1000.0D));
                if (stage_count[i] < 0) {
                  stage_count[i] = 0;
                }
                d2 = (out[i][0] - 1.0D) / (d1 - 1.0D);
                stage_ix[i] = ((int)(stage_count[i] * d2));
              }
            }
          }
          break;
        case 7: 
          stage_ix[i] += 1;
          if (stage_ix[i] >= stage_count[i])
          {
            out[i][0] = 0.0D;
            active[i][0] = 0.0D;
            stage[i] = 8;
          }
          else
          {
            d2 = stage_ix[i] / stage_count[i];
            out[i][0] = ((1.0D - d2) * stage_v[i]);
          }
          break;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftEnvelopeGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */