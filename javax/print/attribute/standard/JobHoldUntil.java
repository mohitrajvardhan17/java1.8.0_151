package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobHoldUntil
  extends DateTimeSyntax
  implements PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = -1664471048860415024L;
  
  public JobHoldUntil(Date paramDate)
  {
    super(paramDate);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobHoldUntil));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobHoldUntil.class;
  }
  
  public final String getName()
  {
    return "job-hold-until";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobHoldUntil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */