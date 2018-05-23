package javax.accessibility;

public abstract interface AccessibleSelection
{
  public abstract int getAccessibleSelectionCount();
  
  public abstract Accessible getAccessibleSelection(int paramInt);
  
  public abstract boolean isAccessibleChildSelected(int paramInt);
  
  public abstract void addAccessibleSelection(int paramInt);
  
  public abstract void removeAccessibleSelection(int paramInt);
  
  public abstract void clearAccessibleSelection();
  
  public abstract void selectAllAccessibleSelection();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */