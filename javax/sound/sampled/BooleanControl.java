package javax.sound.sampled;

public abstract class BooleanControl
  extends Control
{
  private final String trueStateLabel;
  private final String falseStateLabel;
  private boolean value;
  
  protected BooleanControl(Type paramType, boolean paramBoolean, String paramString1, String paramString2)
  {
    super(paramType);
    value = paramBoolean;
    trueStateLabel = paramString1;
    falseStateLabel = paramString2;
  }
  
  protected BooleanControl(Type paramType, boolean paramBoolean)
  {
    this(paramType, paramBoolean, "true", "false");
  }
  
  public void setValue(boolean paramBoolean)
  {
    value = paramBoolean;
  }
  
  public boolean getValue()
  {
    return value;
  }
  
  public String getStateLabel(boolean paramBoolean)
  {
    return paramBoolean == true ? trueStateLabel : falseStateLabel;
  }
  
  public String toString()
  {
    return new String(super.toString() + " with current value: " + getStateLabel(getValue()));
  }
  
  public static class Type
    extends Control.Type
  {
    public static final Type MUTE = new Type("Mute");
    public static final Type APPLY_REVERB = new Type("Apply Reverb");
    
    protected Type(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\BooleanControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */