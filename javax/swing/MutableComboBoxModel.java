package javax.swing;

public abstract interface MutableComboBoxModel<E>
  extends ComboBoxModel<E>
{
  public abstract void addElement(E paramE);
  
  public abstract void removeElement(Object paramObject);
  
  public abstract void insertElementAt(E paramE, int paramInt);
  
  public abstract void removeElementAt(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\MutableComboBoxModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */