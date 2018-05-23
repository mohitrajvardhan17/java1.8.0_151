package java.math;

public enum RoundingMode
{
  UP(0),  DOWN(1),  CEILING(2),  FLOOR(3),  HALF_UP(4),  HALF_DOWN(5),  HALF_EVEN(6),  UNNECESSARY(7);
  
  final int oldMode;
  
  private RoundingMode(int paramInt)
  {
    oldMode = paramInt;
  }
  
  public static RoundingMode valueOf(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return UP;
    case 1: 
      return DOWN;
    case 2: 
      return CEILING;
    case 3: 
      return FLOOR;
    case 4: 
      return HALF_UP;
    case 5: 
      return HALF_DOWN;
    case 6: 
      return HALF_EVEN;
    case 7: 
      return UNNECESSARY;
    }
    throw new IllegalArgumentException("argument out of range");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\math\RoundingMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */