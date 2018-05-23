package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class JobOriginatingUserName
  extends TextSyntax
  implements PrintJobAttribute
{
  private static final long serialVersionUID = -8052537926362933477L;
  
  public JobOriginatingUserName(String paramString, Locale paramLocale)
  {
    super(paramString, paramLocale);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobOriginatingUserName));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobOriginatingUserName.class;
  }
  
  public final String getName()
  {
    return "job-originating-user-name";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobOriginatingUserName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */