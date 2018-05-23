package javax.swing.tree;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTree;

public abstract interface TreeCellEditor
  extends CellEditor
{
  public abstract Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\TreeCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */