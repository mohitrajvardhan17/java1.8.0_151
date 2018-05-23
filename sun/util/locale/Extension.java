package sun.util.locale;

class Extension
{
  private final char key;
  private String value;
  private String id;
  
  protected Extension(char paramChar)
  {
    key = paramChar;
  }
  
  Extension(char paramChar, String paramString)
  {
    key = paramChar;
    setValue(paramString);
  }
  
  protected void setValue(String paramString)
  {
    value = paramString;
    id = (key + "-" + paramString);
  }
  
  public char getKey()
  {
    return key;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String getID()
  {
    return id;
  }
  
  public String toString()
  {
    return getID();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\Extension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */