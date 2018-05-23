package javax.annotation.processing;

public class Completions
{
  private Completions() {}
  
  public static Completion of(String paramString1, String paramString2)
  {
    return new SimpleCompletion(paramString1, paramString2);
  }
  
  public static Completion of(String paramString)
  {
    return new SimpleCompletion(paramString, "");
  }
  
  private static class SimpleCompletion
    implements Completion
  {
    private String value;
    private String message;
    
    SimpleCompletion(String paramString1, String paramString2)
    {
      if ((paramString1 == null) || (paramString2 == null)) {
        throw new NullPointerException("Null completion strings not accepted.");
      }
      value = paramString1;
      message = paramString2;
    }
    
    public String getValue()
    {
      return value;
    }
    
    public String getMessage()
    {
      return message;
    }
    
    public String toString()
    {
      return "[\"" + value + "\", \"" + message + "\"]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\processing\Completions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */