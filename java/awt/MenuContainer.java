package java.awt;

public abstract interface MenuContainer
{
  public abstract Font getFont();
  
  public abstract void remove(MenuComponent paramMenuComponent);
  
  @Deprecated
  public abstract boolean postEvent(Event paramEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MenuContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */