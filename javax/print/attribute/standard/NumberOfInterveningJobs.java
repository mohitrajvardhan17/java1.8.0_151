package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class NumberOfInterveningJobs
  extends IntegerSyntax
  implements PrintJobAttribute
{
  private static final long serialVersionUID = 2568141124844982746L;
  
  public NumberOfInterveningJobs(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof NumberOfInterveningJobs));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return NumberOfInterveningJobs.class;
  }
  
  public final String getName()
  {
    return "number-of-intervening-jobs";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\NumberOfInterveningJobs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */