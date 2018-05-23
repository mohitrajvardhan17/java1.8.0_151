package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobMediaSheetsCompleted
  extends IntegerSyntax
  implements PrintJobAttribute
{
  private static final long serialVersionUID = 1739595973810840475L;
  
  public JobMediaSheetsCompleted(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobMediaSheetsCompleted));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobMediaSheetsCompleted.class;
  }
  
  public final String getName()
  {
    return "job-media-sheets-completed";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobMediaSheetsCompleted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */