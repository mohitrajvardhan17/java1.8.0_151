package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobImpressionsCompleted
  extends IntegerSyntax
  implements PrintJobAttribute
{
  private static final long serialVersionUID = 6722648442432393294L;
  
  public JobImpressionsCompleted(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobImpressionsCompleted));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobImpressionsCompleted.class;
  }
  
  public final String getName()
  {
    return "job-impressions-completed";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobImpressionsCompleted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */