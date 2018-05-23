package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobPriority
  extends IntegerSyntax
  implements PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = -4599900369040602769L;
  
  public JobPriority(int paramInt)
  {
    super(paramInt, 1, 100);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobPriority));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobPriority.class;
  }
  
  public final String getName()
  {
    return "job-priority";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobPriority.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */