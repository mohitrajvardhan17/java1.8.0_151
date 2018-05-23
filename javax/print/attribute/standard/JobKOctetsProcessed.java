package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobKOctetsProcessed
  extends IntegerSyntax
  implements PrintJobAttribute
{
  private static final long serialVersionUID = -6265238509657881806L;
  
  public JobKOctetsProcessed(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobKOctetsProcessed));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobKOctetsProcessed.class;
  }
  
  public final String getName()
  {
    return "job-k-octets-processed";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobKOctetsProcessed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */