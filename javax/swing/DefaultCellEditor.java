package javax.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellEditor;

public class DefaultCellEditor
  extends AbstractCellEditor
  implements TableCellEditor, TreeCellEditor
{
  protected JComponent editorComponent;
  protected EditorDelegate delegate;
  protected int clickCountToStart = 1;
  
  @ConstructorProperties({"component"})
  public DefaultCellEditor(final JTextField paramJTextField)
  {
    editorComponent = paramJTextField;
    clickCountToStart = 2;
    delegate = new EditorDelegate(paramJTextField)
    {
      public void setValue(Object paramAnonymousObject)
      {
        paramJTextField.setText(paramAnonymousObject != null ? paramAnonymousObject.toString() : "");
      }
      
      public Object getCellEditorValue()
      {
        return paramJTextField.getText();
      }
    };
    paramJTextField.addActionListener(delegate);
  }
  
  public DefaultCellEditor(final JCheckBox paramJCheckBox)
  {
    editorComponent = paramJCheckBox;
    delegate = new EditorDelegate(paramJCheckBox)
    {
      public void setValue(Object paramAnonymousObject)
      {
        boolean bool = false;
        if ((paramAnonymousObject instanceof Boolean)) {
          bool = ((Boolean)paramAnonymousObject).booleanValue();
        } else if ((paramAnonymousObject instanceof String)) {
          bool = paramAnonymousObject.equals("true");
        }
        paramJCheckBox.setSelected(bool);
      }
      
      public Object getCellEditorValue()
      {
        return Boolean.valueOf(paramJCheckBox.isSelected());
      }
    };
    paramJCheckBox.addActionListener(delegate);
    paramJCheckBox.setRequestFocusEnabled(false);
  }
  
  public DefaultCellEditor(final JComboBox paramJComboBox)
  {
    editorComponent = paramJComboBox;
    paramJComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    delegate = new EditorDelegate(paramJComboBox)
    {
      public void setValue(Object paramAnonymousObject)
      {
        paramJComboBox.setSelectedItem(paramAnonymousObject);
      }
      
      public Object getCellEditorValue()
      {
        return paramJComboBox.getSelectedItem();
      }
      
      public boolean shouldSelectCell(EventObject paramAnonymousEventObject)
      {
        if ((paramAnonymousEventObject instanceof MouseEvent))
        {
          MouseEvent localMouseEvent = (MouseEvent)paramAnonymousEventObject;
          return localMouseEvent.getID() != 506;
        }
        return true;
      }
      
      public boolean stopCellEditing()
      {
        if (paramJComboBox.isEditable()) {
          paramJComboBox.actionPerformed(new ActionEvent(DefaultCellEditor.this, 0, ""));
        }
        return super.stopCellEditing();
      }
    };
    paramJComboBox.addActionListener(delegate);
  }
  
  public Component getComponent()
  {
    return editorComponent;
  }
  
  public void setClickCountToStart(int paramInt)
  {
    clickCountToStart = paramInt;
  }
  
  public int getClickCountToStart()
  {
    return clickCountToStart;
  }
  
  public Object getCellEditorValue()
  {
    return delegate.getCellEditorValue();
  }
  
  public boolean isCellEditable(EventObject paramEventObject)
  {
    return delegate.isCellEditable(paramEventObject);
  }
  
  public boolean shouldSelectCell(EventObject paramEventObject)
  {
    return delegate.shouldSelectCell(paramEventObject);
  }
  
  public boolean stopCellEditing()
  {
    return delegate.stopCellEditing();
  }
  
  public void cancelCellEditing()
  {
    delegate.cancelCellEditing();
  }
  
  public Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    String str = paramJTree.convertValueToText(paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, false);
    delegate.setValue(str);
    return editorComponent;
  }
  
  public Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    delegate.setValue(paramObject);
    if ((editorComponent instanceof JCheckBox))
    {
      TableCellRenderer localTableCellRenderer = paramJTable.getCellRenderer(paramInt1, paramInt2);
      Component localComponent = localTableCellRenderer.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean, true, paramInt1, paramInt2);
      if (localComponent != null)
      {
        editorComponent.setOpaque(true);
        editorComponent.setBackground(localComponent.getBackground());
        if ((localComponent instanceof JComponent)) {
          editorComponent.setBorder(((JComponent)localComponent).getBorder());
        }
      }
      else
      {
        editorComponent.setOpaque(false);
      }
    }
    return editorComponent;
  }
  
  protected class EditorDelegate
    implements ActionListener, ItemListener, Serializable
  {
    protected Object value;
    
    protected EditorDelegate() {}
    
    public Object getCellEditorValue()
    {
      return value;
    }
    
    public void setValue(Object paramObject)
    {
      value = paramObject;
    }
    
    public boolean isCellEditable(EventObject paramEventObject)
    {
      if ((paramEventObject instanceof MouseEvent)) {
        return ((MouseEvent)paramEventObject).getClickCount() >= clickCountToStart;
      }
      return true;
    }
    
    public boolean shouldSelectCell(EventObject paramEventObject)
    {
      return true;
    }
    
    public boolean startCellEditing(EventObject paramEventObject)
    {
      return true;
    }
    
    public boolean stopCellEditing()
    {
      fireEditingStopped();
      return true;
    }
    
    public void cancelCellEditing()
    {
      fireEditingCanceled();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      DefaultCellEditor.this.stopCellEditing();
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      DefaultCellEditor.this.stopCellEditing();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */