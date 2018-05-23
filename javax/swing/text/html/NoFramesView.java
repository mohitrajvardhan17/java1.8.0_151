package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class NoFramesView
  extends BlockView
{
  boolean visible = false;
  
  public NoFramesView(Element paramElement, int paramInt)
  {
    super(paramElement, paramInt);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Container localContainer = getContainer();
    if ((localContainer != null) && (visible != ((JTextComponent)localContainer).isEditable())) {
      visible = ((JTextComponent)localContainer).isEditable();
    }
    if (!isVisible()) {
      return;
    }
    super.paint(paramGraphics, paramShape);
  }
  
  public void setParent(View paramView)
  {
    if (paramView != null)
    {
      Container localContainer = paramView.getContainer();
      if (localContainer != null) {
        visible = ((JTextComponent)localContainer).isEditable();
      }
    }
    super.setParent(paramView);
  }
  
  public boolean isVisible()
  {
    return visible;
  }
  
  protected void layout(int paramInt1, int paramInt2)
  {
    if (!isVisible()) {
      return;
    }
    super.layout(paramInt1, paramInt2);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    if (!visible) {
      return 0.0F;
    }
    return super.getPreferredSpan(paramInt);
  }
  
  public float getMinimumSpan(int paramInt)
  {
    if (!visible) {
      return 0.0F;
    }
    return super.getMinimumSpan(paramInt);
  }
  
  public float getMaximumSpan(int paramInt)
  {
    if (!visible) {
      return 0.0F;
    }
    return super.getMaximumSpan(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\NoFramesView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */