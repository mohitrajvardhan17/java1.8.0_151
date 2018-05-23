package javax.print.attribute.standard;

import java.util.Collection;
import java.util.HashSet;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;

public final class JobStateReasons
  extends HashSet<JobStateReason>
  implements PrintJobAttribute
{
  private static final long serialVersionUID = 8849088261264331812L;
  
  public JobStateReasons() {}
  
  public JobStateReasons(int paramInt)
  {
    super(paramInt);
  }
  
  public JobStateReasons(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public JobStateReasons(Collection<JobStateReason> paramCollection)
  {
    super(paramCollection);
  }
  
  public boolean add(JobStateReason paramJobStateReason)
  {
    if (paramJobStateReason == null) {
      throw new NullPointerException();
    }
    return super.add(paramJobStateReason);
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobStateReasons.class;
  }
  
  public final String getName()
  {
    return "job-state-reasons";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobStateReasons.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */