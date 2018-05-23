package javax.accessibility;

public abstract interface AccessibleTableModelChange
{
  public static final int INSERT = 1;
  public static final int UPDATE = 0;
  public static final int DELETE = -1;
  
  public abstract int getType();
  
  public abstract int getFirstRow();
  
  public abstract int getLastRow();
  
  public abstract int getFirstColumn();
  
  public abstract int getLastColumn();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleTableModelChange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */