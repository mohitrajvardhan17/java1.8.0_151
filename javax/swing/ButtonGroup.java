package javax.swing;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class ButtonGroup
  implements Serializable
{
  protected Vector<AbstractButton> buttons = new Vector();
  ButtonModel selection = null;
  
  public ButtonGroup() {}
  
  public void add(AbstractButton paramAbstractButton)
  {
    if (paramAbstractButton == null) {
      return;
    }
    buttons.addElement(paramAbstractButton);
    if (paramAbstractButton.isSelected()) {
      if (selection == null) {
        selection = paramAbstractButton.getModel();
      } else {
        paramAbstractButton.setSelected(false);
      }
    }
    paramAbstractButton.getModel().setGroup(this);
  }
  
  public void remove(AbstractButton paramAbstractButton)
  {
    if (paramAbstractButton == null) {
      return;
    }
    buttons.removeElement(paramAbstractButton);
    if (paramAbstractButton.getModel() == selection) {
      selection = null;
    }
    paramAbstractButton.getModel().setGroup(null);
  }
  
  public void clearSelection()
  {
    if (selection != null)
    {
      ButtonModel localButtonModel = selection;
      selection = null;
      localButtonModel.setSelected(false);
    }
  }
  
  public Enumeration<AbstractButton> getElements()
  {
    return buttons.elements();
  }
  
  public ButtonModel getSelection()
  {
    return selection;
  }
  
  public void setSelected(ButtonModel paramButtonModel, boolean paramBoolean)
  {
    if ((paramBoolean) && (paramButtonModel != null) && (paramButtonModel != selection))
    {
      ButtonModel localButtonModel = selection;
      selection = paramButtonModel;
      if (localButtonModel != null) {
        localButtonModel.setSelected(false);
      }
      paramButtonModel.setSelected(true);
    }
  }
  
  public boolean isSelected(ButtonModel paramButtonModel)
  {
    return paramButtonModel == selection;
  }
  
  public int getButtonCount()
  {
    if (buttons == null) {
      return 0;
    }
    return buttons.size();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ButtonGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */