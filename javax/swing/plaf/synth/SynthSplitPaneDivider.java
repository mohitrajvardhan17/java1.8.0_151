package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import sun.swing.DefaultLookup;

class SynthSplitPaneDivider
  extends BasicSplitPaneDivider
{
  public SynthSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    super(paramBasicSplitPaneUI);
  }
  
  protected void setMouseOver(boolean paramBoolean)
  {
    if (isMouseOver() != paramBoolean) {
      repaint();
    }
    super.setMouseOver(paramBoolean);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    if ((paramPropertyChangeEvent.getSource() == splitPane) && (paramPropertyChangeEvent.getPropertyName() == "orientation"))
    {
      if ((leftButton instanceof SynthArrowButton)) {
        ((SynthArrowButton)leftButton).setDirection(mapDirection(true));
      }
      if ((rightButton instanceof SynthArrowButton)) {
        ((SynthArrowButton)rightButton).setDirection(mapDirection(false));
      }
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    Graphics localGraphics1 = paramGraphics.create();
    SynthContext localSynthContext = ((SynthSplitPaneUI)splitPaneUI).getContext(splitPane, Region.SPLIT_PANE_DIVIDER);
    Rectangle localRectangle1 = getBounds();
    x = (y = 0);
    SynthLookAndFeel.updateSubregion(localSynthContext, paramGraphics, localRectangle1);
    localSynthContext.getPainter().paintSplitPaneDividerBackground(localSynthContext, paramGraphics, 0, 0, width, height, splitPane.getOrientation());
    Object localObject = null;
    localSynthContext.getPainter().paintSplitPaneDividerForeground(localSynthContext, paramGraphics, 0, 0, getWidth(), getHeight(), splitPane.getOrientation());
    localSynthContext.dispose();
    for (int i = 0; i < getComponentCount(); i++)
    {
      Component localComponent = getComponent(i);
      Rectangle localRectangle2 = localComponent.getBounds();
      Graphics localGraphics2 = paramGraphics.create(x, y, width, height);
      localComponent.paint(localGraphics2);
      localGraphics2.dispose();
    }
    localGraphics1.dispose();
  }
  
  private int mapDirection(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (splitPane.getOrientation() == 1) {
        return 7;
      }
      return 1;
    }
    if (splitPane.getOrientation() == 1) {
      return 3;
    }
    return 5;
  }
  
  protected JButton createLeftOneTouchButton()
  {
    SynthArrowButton localSynthArrowButton = new SynthArrowButton(1);
    int i = lookupOneTouchSize();
    localSynthArrowButton.setName("SplitPaneDivider.leftOneTouchButton");
    localSynthArrowButton.setMinimumSize(new Dimension(i, i));
    localSynthArrowButton.setCursor(Cursor.getPredefinedCursor(0));
    localSynthArrowButton.setFocusPainted(false);
    localSynthArrowButton.setBorderPainted(false);
    localSynthArrowButton.setRequestFocusEnabled(false);
    localSynthArrowButton.setDirection(mapDirection(true));
    return localSynthArrowButton;
  }
  
  private int lookupOneTouchSize()
  {
    return DefaultLookup.getInt(splitPaneUI.getSplitPane(), splitPaneUI, "SplitPaneDivider.oneTouchButtonSize", 6);
  }
  
  protected JButton createRightOneTouchButton()
  {
    SynthArrowButton localSynthArrowButton = new SynthArrowButton(1);
    int i = lookupOneTouchSize();
    localSynthArrowButton.setName("SplitPaneDivider.rightOneTouchButton");
    localSynthArrowButton.setMinimumSize(new Dimension(i, i));
    localSynthArrowButton.setCursor(Cursor.getPredefinedCursor(0));
    localSynthArrowButton.setFocusPainted(false);
    localSynthArrowButton.setBorderPainted(false);
    localSynthArrowButton.setRequestFocusEnabled(false);
    localSynthArrowButton.setDirection(mapDirection(false));
    return localSynthArrowButton;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */