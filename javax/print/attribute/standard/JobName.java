package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.TextSyntax;

public final class JobName
  extends TextSyntax
  implements PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = 4660359192078689545L;
  
  public JobName(String paramString, Locale paramLocale)
  {
    super(paramString, paramLocale);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobName));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobName.class;
  }
  
  public final String getName()
  {
    return "job-name";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */