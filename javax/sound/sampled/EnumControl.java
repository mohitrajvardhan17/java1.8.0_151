package javax.sound.sampled;

public abstract class EnumControl
  extends Control
{
  private Object[] values;
  private Object value;
  
  protected EnumControl(Type paramType, Object[] paramArrayOfObject, Object paramObject)
  {
    super(paramType);
    values = paramArrayOfObject;
    value = paramObject;
  }
  
  public void setValue(Object paramObject)
  {
    if (!isValueSupported(paramObject)) {
      throw new IllegalArgumentException("Requested value " + paramObject + " is not supported.");
    }
    value = paramObject;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public Object[] getValues()
  {
    Object[] arrayOfObject = new Object[values.length];
    for (int i = 0; i < values.length; i++) {
      arrayOfObject[i] = values[i];
    }
    return arrayOfObject;
  }
  
  private boolean isValueSupported(Object paramObject)
  {
    for (int i = 0; i < values.length; i++) {
      if (paramObject.equals(values[i])) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return new String(getType() + " with current value: " + getValue());
  }
  
  public static class Type
    extends Control.Type
  {
    public static final Type REVERB = new Type("Reverb");
    
    protected Type(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\EnumControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */