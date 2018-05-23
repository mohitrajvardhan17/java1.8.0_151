package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class JobPrioritySupported
  extends IntegerSyntax
  implements SupportedValuesAttribute
{
  private static final long serialVersionUID = 2564840378013555894L;
  
  public JobPrioritySupported(int paramInt)
  {
    super(paramInt, 1, 100);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobPrioritySupported));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobPrioritySupported.class;
  }
  
  public final String getName()
  {
    return "job-priority-supported";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobPrioritySupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */