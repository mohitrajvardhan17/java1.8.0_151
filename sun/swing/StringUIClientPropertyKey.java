package sun.swing;

public class StringUIClientPropertyKey
  implements UIClientPropertyKey
{
  private final String key;
  
  public StringUIClientPropertyKey(String paramString)
  {
    key = paramString;
  }
  
  public String toString()
  {
    return key;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\StringUIClientPropertyKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */