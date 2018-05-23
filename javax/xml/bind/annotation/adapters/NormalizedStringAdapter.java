package javax.xml.bind.annotation.adapters;

public final class NormalizedStringAdapter
  extends XmlAdapter<String, String>
{
  public NormalizedStringAdapter() {}
  
  public String unmarshal(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    for (int i = paramString.length() - 1; (i >= 0) && (!isWhiteSpaceExceptSpace(paramString.charAt(i))); i--) {}
    if (i < 0) {
      return paramString;
    }
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[(i--)] = ' ';
    while (i >= 0)
    {
      if (isWhiteSpaceExceptSpace(arrayOfChar[i])) {
        arrayOfChar[i] = ' ';
      }
      i--;
    }
    return new String(arrayOfChar);
  }
  
  public String marshal(String paramString)
  {
    return paramString;
  }
  
  protected static boolean isWhiteSpaceExceptSpace(char paramChar)
  {
    if (paramChar >= ' ') {
      return false;
    }
    return (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\adapters\NormalizedStringAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */