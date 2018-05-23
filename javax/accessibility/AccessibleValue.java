package javax.accessibility;

public abstract interface AccessibleValue
{
  public abstract Number getCurrentAccessibleValue();
  
  public abstract boolean setCurrentAccessibleValue(Number paramNumber);
  
  public abstract Number getMinimumAccessibleValue();
  
  public abstract Number getMaximumAccessibleValue();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */