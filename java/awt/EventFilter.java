package java.awt;

abstract interface EventFilter
{
  public abstract FilterAction acceptEvent(AWTEvent paramAWTEvent);
  
  public static enum FilterAction
  {
    ACCEPT,  REJECT,  ACCEPT_IMMEDIATELY;
    
    private FilterAction() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\EventFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */