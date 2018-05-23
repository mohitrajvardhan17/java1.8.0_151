package sun.security.x509;

public class X509AttributeName
{
  private static final char SEPARATOR = '.';
  private String prefix = null;
  private String suffix = null;
  
  public X509AttributeName(String paramString)
  {
    int i = paramString.indexOf('.');
    if (i < 0)
    {
      prefix = paramString;
    }
    else
    {
      prefix = paramString.substring(0, i);
      suffix = paramString.substring(i + 1);
    }
  }
  
  public String getPrefix()
  {
    return prefix;
  }
  
  public String getSuffix()
  {
    return suffix;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\X509AttributeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */