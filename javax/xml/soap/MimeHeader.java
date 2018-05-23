package javax.xml.soap;

public class MimeHeader
{
  private String name;
  private String value;
  
  public MimeHeader(String paramString1, String paramString2)
  {
    name = paramString1;
    value = paramString2;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\MimeHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */