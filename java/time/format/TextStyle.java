package java.time.format;

public enum TextStyle
{
  FULL(2, 0),  FULL_STANDALONE(32770, 0),  SHORT(1, 1),  SHORT_STANDALONE(32769, 1),  NARROW(4, 1),  NARROW_STANDALONE(32772, 1);
  
  private final int calendarStyle;
  private final int zoneNameStyleIndex;
  
  private TextStyle(int paramInt1, int paramInt2)
  {
    calendarStyle = paramInt1;
    zoneNameStyleIndex = paramInt2;
  }
  
  public boolean isStandalone()
  {
    return (ordinal() & 0x1) == 1;
  }
  
  public TextStyle asStandalone()
  {
    return values()[(ordinal() | 0x1)];
  }
  
  public TextStyle asNormal()
  {
    return values()[(ordinal() & 0xFFFFFFFE)];
  }
  
  int toCalendarStyle()
  {
    return calendarStyle;
  }
  
  int zoneNameStyleIndex()
  {
    return zoneNameStyleIndex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\format\TextStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */