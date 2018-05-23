package javax.accessibility;

public abstract interface AccessibleTable
{
  public abstract Accessible getAccessibleCaption();
  
  public abstract void setAccessibleCaption(Accessible paramAccessible);
  
  public abstract Accessible getAccessibleSummary();
  
  public abstract void setAccessibleSummary(Accessible paramAccessible);
  
  public abstract int getAccessibleRowCount();
  
  public abstract int getAccessibleColumnCount();
  
  public abstract Accessible getAccessibleAt(int paramInt1, int paramInt2);
  
  public abstract int getAccessibleRowExtentAt(int paramInt1, int paramInt2);
  
  public abstract int getAccessibleColumnExtentAt(int paramInt1, int paramInt2);
  
  public abstract AccessibleTable getAccessibleRowHeader();
  
  public abstract void setAccessibleRowHeader(AccessibleTable paramAccessibleTable);
  
  public abstract AccessibleTable getAccessibleColumnHeader();
  
  public abstract void setAccessibleColumnHeader(AccessibleTable paramAccessibleTable);
  
  public abstract Accessible getAccessibleRowDescription(int paramInt);
  
  public abstract void setAccessibleRowDescription(int paramInt, Accessible paramAccessible);
  
  public abstract Accessible getAccessibleColumnDescription(int paramInt);
  
  public abstract void setAccessibleColumnDescription(int paramInt, Accessible paramAccessible);
  
  public abstract boolean isAccessibleSelected(int paramInt1, int paramInt2);
  
  public abstract boolean isAccessibleRowSelected(int paramInt);
  
  public abstract boolean isAccessibleColumnSelected(int paramInt);
  
  public abstract int[] getSelectedAccessibleRows();
  
  public abstract int[] getSelectedAccessibleColumns();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */