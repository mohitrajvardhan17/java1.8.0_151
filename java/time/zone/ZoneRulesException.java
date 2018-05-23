package java.time.zone;

import java.time.DateTimeException;

public class ZoneRulesException
  extends DateTimeException
{
  private static final long serialVersionUID = -1632418723876261839L;
  
  public ZoneRulesException(String paramString)
  {
    super(paramString);
  }
  
  public ZoneRulesException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\zone\ZoneRulesException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */