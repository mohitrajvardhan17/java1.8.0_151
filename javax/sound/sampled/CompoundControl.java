package javax.sound.sampled;

public abstract class CompoundControl
  extends Control
{
  private Control[] controls;
  
  protected CompoundControl(Type paramType, Control[] paramArrayOfControl)
  {
    super(paramType);
    controls = paramArrayOfControl;
  }
  
  public Control[] getMemberControls()
  {
    Control[] arrayOfControl = new Control[controls.length];
    for (int i = 0; i < controls.length; i++) {
      arrayOfControl[i] = controls[i];
    }
    return arrayOfControl;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < controls.length; i++)
    {
      if (i != 0)
      {
        localStringBuffer.append(", ");
        if (i + 1 == controls.length) {
          localStringBuffer.append("and ");
        }
      }
      localStringBuffer.append(controls[i].getType());
    }
    return new String(getType() + " Control containing " + localStringBuffer + " Controls.");
  }
  
  public static class Type
    extends Control.Type
  {
    protected Type(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\CompoundControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */