package javax.accessibility;

public abstract interface AccessibleAction
{
  public static final String TOGGLE_EXPAND = new String("toggleexpand");
  public static final String INCREMENT = new String("increment");
  public static final String DECREMENT = new String("decrement");
  public static final String CLICK = new String("click");
  public static final String TOGGLE_POPUP = new String("toggle popup");
  
  public abstract int getAccessibleActionCount();
  
  public abstract String getAccessibleActionDescription(int paramInt);
  
  public abstract boolean doAccessibleAction(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */