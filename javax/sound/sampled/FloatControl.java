package javax.sound.sampled;

public abstract class FloatControl
  extends Control
{
  private float minimum;
  private float maximum;
  private float precision;
  private int updatePeriod;
  private final String units;
  private final String minLabel;
  private final String maxLabel;
  private final String midLabel;
  private float value;
  
  protected FloatControl(Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, float paramFloat4, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    super(paramType);
    if (paramFloat1 > paramFloat2) {
      throw new IllegalArgumentException("Minimum value " + paramFloat1 + " exceeds maximum value " + paramFloat2 + ".");
    }
    if (paramFloat4 < paramFloat1) {
      throw new IllegalArgumentException("Initial value " + paramFloat4 + " smaller than allowable minimum value " + paramFloat1 + ".");
    }
    if (paramFloat4 > paramFloat2) {
      throw new IllegalArgumentException("Initial value " + paramFloat4 + " exceeds allowable maximum value " + paramFloat2 + ".");
    }
    minimum = paramFloat1;
    maximum = paramFloat2;
    precision = paramFloat3;
    updatePeriod = paramInt;
    value = paramFloat4;
    units = paramString1;
    minLabel = (paramString2 == null ? "" : paramString2);
    midLabel = (paramString3 == null ? "" : paramString3);
    maxLabel = (paramString4 == null ? "" : paramString4);
  }
  
  protected FloatControl(Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, float paramFloat4, String paramString)
  {
    this(paramType, paramFloat1, paramFloat2, paramFloat3, paramInt, paramFloat4, paramString, "", "", "");
  }
  
  public void setValue(float paramFloat)
  {
    if (paramFloat > maximum) {
      throw new IllegalArgumentException("Requested value " + paramFloat + " exceeds allowable maximum value " + maximum + ".");
    }
    if (paramFloat < minimum) {
      throw new IllegalArgumentException("Requested value " + paramFloat + " smaller than allowable minimum value " + minimum + ".");
    }
    value = paramFloat;
  }
  
  public float getValue()
  {
    return value;
  }
  
  public float getMaximum()
  {
    return maximum;
  }
  
  public float getMinimum()
  {
    return minimum;
  }
  
  public String getUnits()
  {
    return units;
  }
  
  public String getMinLabel()
  {
    return minLabel;
  }
  
  public String getMidLabel()
  {
    return midLabel;
  }
  
  public String getMaxLabel()
  {
    return maxLabel;
  }
  
  public float getPrecision()
  {
    return precision;
  }
  
  public int getUpdatePeriod()
  {
    return updatePeriod;
  }
  
  public void shift(float paramFloat1, float paramFloat2, int paramInt)
  {
    if (paramFloat1 < minimum) {
      throw new IllegalArgumentException("Requested value " + paramFloat1 + " smaller than allowable minimum value " + minimum + ".");
    }
    if (paramFloat1 > maximum) {
      throw new IllegalArgumentException("Requested value " + paramFloat1 + " exceeds allowable maximum value " + maximum + ".");
    }
    setValue(paramFloat2);
  }
  
  public String toString()
  {
    return new String(getType() + " with current value: " + getValue() + " " + units + " (range: " + minimum + " - " + maximum + ")");
  }
  
  public static class Type
    extends Control.Type
  {
    public static final Type MASTER_GAIN = new Type("Master Gain");
    public static final Type AUX_SEND = new Type("AUX Send");
    public static final Type AUX_RETURN = new Type("AUX Return");
    public static final Type REVERB_SEND = new Type("Reverb Send");
    public static final Type REVERB_RETURN = new Type("Reverb Return");
    public static final Type VOLUME = new Type("Volume");
    public static final Type PAN = new Type("Pan");
    public static final Type BALANCE = new Type("Balance");
    public static final Type SAMPLE_RATE = new Type("Sample Rate");
    
    protected Type(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\FloatControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */