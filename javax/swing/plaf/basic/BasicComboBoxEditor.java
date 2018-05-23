package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import sun.reflect.misc.MethodUtil;

public class BasicComboBoxEditor
  implements ComboBoxEditor, FocusListener
{
  protected JTextField editor = createEditorComponent();
  private Object oldValue;
  
  public BasicComboBoxEditor() {}
  
  public Component getEditorComponent()
  {
    return editor;
  }
  
  protected JTextField createEditorComponent()
  {
    BorderlessTextField localBorderlessTextField = new BorderlessTextField("", 9);
    localBorderlessTextField.setBorder(null);
    return localBorderlessTextField;
  }
  
  public void setItem(Object paramObject)
  {
    String str;
    if (paramObject != null)
    {
      str = paramObject.toString();
      if (str == null) {
        str = "";
      }
      oldValue = paramObject;
    }
    else
    {
      str = "";
    }
    if (!str.equals(editor.getText())) {
      editor.setText(str);
    }
  }
  
  public Object getItem()
  {
    Object localObject = editor.getText();
    if ((oldValue != null) && (!(oldValue instanceof String)))
    {
      if (localObject.equals(oldValue.toString())) {
        return oldValue;
      }
      Class localClass = oldValue.getClass();
      try
      {
        Method localMethod = MethodUtil.getMethod(localClass, "valueOf", new Class[] { String.class });
        localObject = MethodUtil.invoke(localMethod, oldValue, new Object[] { editor.getText() });
      }
      catch (Exception localException) {}
    }
    return localObject;
  }
  
  public void selectAll()
  {
    editor.selectAll();
    editor.requestFocus();
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {}
  
  public void focusLost(FocusEvent paramFocusEvent) {}
  
  public void addActionListener(ActionListener paramActionListener)
  {
    editor.addActionListener(paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener)
  {
    editor.removeActionListener(paramActionListener);
  }
  
  static class BorderlessTextField
    extends JTextField
  {
    public BorderlessTextField(String paramString, int paramInt)
    {
      super(paramInt);
    }
    
    public void setText(String paramString)
    {
      if (getText().equals(paramString)) {
        return;
      }
      super.setText(paramString);
    }
    
    public void setBorder(Border paramBorder)
    {
      if (!(paramBorder instanceof BasicComboBoxEditor.UIResource)) {
        super.setBorder(paramBorder);
      }
    }
  }
  
  public static class UIResource
    extends BasicComboBoxEditor
    implements UIResource
  {
    public UIResource() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicComboBoxEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */