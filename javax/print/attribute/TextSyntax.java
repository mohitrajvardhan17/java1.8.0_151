package javax.print.attribute;

import java.io.Serializable;
import java.util.Locale;

public abstract class TextSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -8130648736378144102L;
  private String value;
  private Locale locale;
  
  protected TextSyntax(String paramString, Locale paramLocale)
  {
    value = verify(paramString);
    locale = verify(paramLocale);
  }
  
  private static String verify(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException(" value is null");
    }
    return paramString;
  }
  
  private static Locale verify(Locale paramLocale)
  {
    if (paramLocale == null) {
      return Locale.getDefault();
    }
    return paramLocale;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public Locale getLocale()
  {
    return locale;
  }
  
  public int hashCode()
  {
    return value.hashCode() ^ locale.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof TextSyntax)) && (value.equals(value)) && (locale.equals(locale));
  }
  
  public String toString()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\TextSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */