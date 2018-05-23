package java.time.format;

public enum SignStyle
{
  NORMAL,  ALWAYS,  NEVER,  NOT_NEGATIVE,  EXCEEDS_PAD;
  
  private SignStyle() {}
  
  boolean parse(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    switch (ordinal())
    {
    case 0: 
      return (!paramBoolean1) || (!paramBoolean2);
    case 1: 
    case 4: 
      return true;
    }
    return (!paramBoolean2) && (!paramBoolean3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\format\SignStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */