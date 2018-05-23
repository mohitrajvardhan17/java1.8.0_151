package javax.sound.sampled;

public abstract class Control
{
  private final Type type;
  
  protected Control(Type paramType)
  {
    type = paramType;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public String toString()
  {
    return new String(getType() + " Control");
  }
  
  public static class Type
  {
    private String name;
    
    protected Type(String paramString)
    {
      name = paramString;
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
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\Control.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */