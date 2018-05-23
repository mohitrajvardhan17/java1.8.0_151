package sun.tracing;

class NullProbe
  extends ProbeSkeleton
{
  public NullProbe(Class<?>[] paramArrayOfClass)
  {
    super(paramArrayOfClass);
  }
  
  public boolean isEnabled()
  {
    return false;
  }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\NullProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */