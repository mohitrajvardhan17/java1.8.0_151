package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class DateTimeAtCompleted
  extends DateTimeSyntax
  implements PrintJobAttribute
{
  private static final long serialVersionUID = 6497399708058490000L;
  
  public DateTimeAtCompleted(Date paramDate)
  {
    super(paramDate);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof DateTimeAtCompleted));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return DateTimeAtCompleted.class;
  }
  
  public final String getName()
  {
    return "date-time-at-completed";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\DateTimeAtCompleted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */