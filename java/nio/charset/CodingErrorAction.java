package java.nio.charset;

public class CodingErrorAction
{
  private String name;
  public static final CodingErrorAction IGNORE = new CodingErrorAction("IGNORE");
  public static final CodingErrorAction REPLACE = new CodingErrorAction("REPLACE");
  public static final CodingErrorAction REPORT = new CodingErrorAction("REPORT");
  
  private CodingErrorAction(String paramString)
  {
    name = paramString;
  }
  
  public String toString()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\CodingErrorAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */