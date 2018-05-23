package javax.swing;

public abstract interface ComboBoxModel<E>
  extends ListModel<E>
{
  public abstract void setSelectedItem(Object paramObject);
  
  public abstract Object getSelectedItem();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ComboBoxModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */