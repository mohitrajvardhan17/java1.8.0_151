package java.time.chrono;

import java.time.DateTimeException;

public enum MinguoEra
  implements Era
{
  BEFORE_ROC,  ROC;
  
  private MinguoEra() {}
  
  public static MinguoEra of(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return BEFORE_ROC;
    case 1: 
      return ROC;
    }
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue()
  {
    return ordinal();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\MinguoEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */