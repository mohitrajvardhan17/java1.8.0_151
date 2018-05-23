package javax.swing.text;

public abstract interface Position
{
  public abstract int getOffset();
  
  public static final class Bias
  {
    public static final Bias Forward = new Bias("Forward");
    public static final Bias Backward = new Bias("Backward");
    private String name;
    
    public String toString()
    {
      return name;
    }
    
    private Bias(String paramString)
    {
      name = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Position.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */