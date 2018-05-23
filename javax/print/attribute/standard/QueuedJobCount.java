package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class QueuedJobCount
  extends IntegerSyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = 7499723077864047742L;
  
  public QueuedJobCount(int paramInt)
  {
    super(paramInt, 0, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof QueuedJobCount));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return QueuedJobCount.class;
  }
  
  public final String getName()
  {
    return "queued-job-count";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\QueuedJobCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */