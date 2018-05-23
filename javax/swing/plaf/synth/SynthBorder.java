package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

class SynthBorder
  extends AbstractBorder
  implements UIResource
{
  private SynthUI ui;
  private Insets insets;
  
  SynthBorder(SynthUI paramSynthUI, Insets paramInsets)
  {
    ui = paramSynthUI;
    insets = paramInsets;
  }
  
  SynthBorder(SynthUI paramSynthUI)
  {
    this(paramSynthUI, null);
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    JComponent localJComponent = (JComponent)paramComponent;
    SynthContext localSynthContext = ui.getContext(localJComponent);
    SynthStyle localSynthStyle = localSynthContext.getStyle();
    if (localSynthStyle == null)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError("SynthBorder is being used outside after the UI has been uninstalled");
      }
      return;
    }
    ui.paintBorder(localSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    localSynthContext.dispose();
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    if (insets != null)
    {
      if (paramInsets == null)
      {
        paramInsets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
      }
      else
      {
        top = insets.top;
        bottom = insets.bottom;
        left = insets.left;
        right = insets.right;
      }
    }
    else if (paramInsets == null) {
      paramInsets = new Insets(0, 0, 0, 0);
    } else {
      top = (bottom = left = right = 0);
    }
    if ((paramComponent instanceof JComponent))
    {
      Region localRegion = Region.getRegion((JComponent)paramComponent);
      Insets localInsets = null;
      if (((localRegion == Region.ARROW_BUTTON) || (localRegion == Region.BUTTON) || (localRegion == Region.CHECK_BOX) || (localRegion == Region.CHECK_BOX_MENU_ITEM) || (localRegion == Region.MENU) || (localRegion == Region.MENU_ITEM) || (localRegion == Region.RADIO_BUTTON) || (localRegion == Region.RADIO_BUTTON_MENU_ITEM) || (localRegion == Region.TOGGLE_BUTTON)) && ((paramComponent instanceof AbstractButton))) {
        localInsets = ((AbstractButton)paramComponent).getMargin();
      } else if (((localRegion == Region.EDITOR_PANE) || (localRegion == Region.FORMATTED_TEXT_FIELD) || (localRegion == Region.PASSWORD_FIELD) || (localRegion == Region.TEXT_AREA) || (localRegion == Region.TEXT_FIELD) || (localRegion == Region.TEXT_PANE)) && ((paramComponent instanceof JTextComponent))) {
        localInsets = ((JTextComponent)paramComponent).getMargin();
      } else if ((localRegion == Region.TOOL_BAR) && ((paramComponent instanceof JToolBar))) {
        localInsets = ((JToolBar)paramComponent).getMargin();
      } else if ((localRegion == Region.MENU_BAR) && ((paramComponent instanceof JMenuBar))) {
        localInsets = ((JMenuBar)paramComponent).getMargin();
      }
      if (localInsets != null)
      {
        top += top;
        bottom += bottom;
        left += left;
        right += right;
      }
    }
    return paramInsets;
  }
  
  public boolean isBorderOpaque()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */