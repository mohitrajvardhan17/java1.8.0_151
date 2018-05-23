package javax.sound.sampled;

public class ReverbType
{
  private String name;
  private int earlyReflectionDelay;
  private float earlyReflectionIntensity;
  private int lateReflectionDelay;
  private float lateReflectionIntensity;
  private int decayTime;
  
  protected ReverbType(String paramString, int paramInt1, float paramFloat1, int paramInt2, float paramFloat2, int paramInt3)
  {
    name = paramString;
    earlyReflectionDelay = paramInt1;
    earlyReflectionIntensity = paramFloat1;
    lateReflectionDelay = paramInt2;
    lateReflectionIntensity = paramFloat2;
    decayTime = paramInt3;
  }
  
  public String getName()
  {
    return name;
  }
  
  public final int getEarlyReflectionDelay()
  {
    return earlyReflectionDelay;
  }
  
  public final float getEarlyReflectionIntensity()
  {
    return earlyReflectionIntensity;
  }
  
  public final int getLateReflectionDelay()
  {
    return lateReflectionDelay;
  }
  
  public final float getLateReflectionIntensity()
  {
    return lateReflectionIntensity;
  }
  
  public final int getDecayTime()
  {
    return decayTime;
  }
  
  public final boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  public final String toString()
  {
    return name + ", early reflection delay " + earlyReflectionDelay + " ns, early reflection intensity " + earlyReflectionIntensity + " dB, late deflection delay " + lateReflectionDelay + " ns, late reflection intensity " + lateReflectionIntensity + " dB, decay time " + decayTime;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\ReverbType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */