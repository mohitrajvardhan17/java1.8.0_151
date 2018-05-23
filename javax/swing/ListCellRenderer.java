package javax.swing;

import java.awt.Component;

public abstract interface ListCellRenderer<E>
{
  public abstract Component getListCellRendererComponent(JList<? extends E> paramJList, E paramE, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ListCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */