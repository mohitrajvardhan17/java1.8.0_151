package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class EditableView
  extends ComponentView
{
  private boolean isVisible;
  
  EditableView(Element paramElement)
  {
    super(paramElement);
  }
  
  public float getMinimumSpan(int paramInt)
  {
    if (isVisible) {
      return super.getMinimumSpan(paramInt);
    }
    return 0.0F;
  }
  
  public float getPreferredSpan(int paramInt)
  {
    if (isVisible) {
      return super.getPreferredSpan(paramInt);
    }
    return 0.0F;
  }
  
  public float getMaximumSpan(int paramInt)
  {
    if (isVisible) {
      return super.getMaximumSpan(paramInt);
    }
    return 0.0F;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Component localComponent = getComponent();
    Container localContainer = getContainer();
    if (((localContainer instanceof JTextComponent)) && (isVisible != ((JTextComponent)localContainer).isEditable()))
    {
      isVisible = ((JTextComponent)localContainer).isEditable();
      preferenceChanged(null, true, true);
      localContainer.repaint();
    }
    if (isVisible) {
      super.paint(paramGraphics, paramShape);
    } else {
      setSize(0.0F, 0.0F);
    }
    if (localComponent != null) {
      localComponent.setFocusable(isVisible);
    }
  }
  
  public void setParent(View paramView)
  {
    if (paramView != null)
    {
      Container localContainer = paramView.getContainer();
      if (localContainer != null) {
        if ((localContainer instanceof JTextComponent)) {
          isVisible = ((JTextComponent)localContainer).isEditable();
        } else {
          isVisible = false;
        }
      }
    }
    super.setParent(paramView);
  }
  
  public boolean isVisible()
  {
    return isVisible;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\EditableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */