package javax.xml.bind.annotation.adapters;

public class CollapsedStringAdapter
  extends XmlAdapter<String, String>
{
  public CollapsedStringAdapter() {}
  
  public String unmarshal(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    for (int j = 0; (j < i) && (!isWhiteSpace(paramString.charAt(j))); j++) {}
    if (j == i) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(i);
    if (j != 0)
    {
      for (k = 0; k < j; k++) {
        localStringBuilder.append(paramString.charAt(k));
      }
      localStringBuilder.append(' ');
    }
    int k = 1;
    for (int m = j + 1; m < i; m++)
    {
      char c = paramString.charAt(m);
      int n = isWhiteSpace(c);
      if ((k == 0) || (n == 0))
      {
        k = n;
        if (k != 0) {
          localStringBuilder.append(' ');
        } else {
          localStringBuilder.append(c);
        }
      }
    }
    i = localStringBuilder.length();
    if ((i > 0) && (localStringBuilder.charAt(i - 1) == ' ')) {
      localStringBuilder.setLength(i - 1);
    }
    return localStringBuilder.toString();
  }
  
  public String marshal(String paramString)
  {
    return paramString;
  }
  
  protected static boolean isWhiteSpace(char paramChar)
  {
    if (paramChar > ' ') {
      return false;
    }
    return (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r') || (paramChar == ' ');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\adapters\CollapsedStringAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */