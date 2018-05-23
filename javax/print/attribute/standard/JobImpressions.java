package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobImpressions
  extends IntegerSyntax
  implements PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = 8225537206784322464L;
  
  public JobImpressions(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobImpressions));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobImpressions.class;
  }
  
  public final String getName()
  {
    return "job-impressions";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobImpressions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */