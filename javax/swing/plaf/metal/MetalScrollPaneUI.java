package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MetalScrollPaneUI
  extends BasicScrollPaneUI
{
  private PropertyChangeListener scrollBarSwapListener;
  
  public MetalScrollPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalScrollPaneUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    JScrollPane localJScrollPane = (JScrollPane)paramJComponent;
    updateScrollbarsFreeStanding();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    JScrollPane localJScrollPane = (JScrollPane)paramJComponent;
    JScrollBar localJScrollBar1 = localJScrollPane.getHorizontalScrollBar();
    JScrollBar localJScrollBar2 = localJScrollPane.getVerticalScrollBar();
    if (localJScrollBar1 != null) {
      localJScrollBar1.putClientProperty("JScrollBar.isFreeStanding", null);
    }
    if (localJScrollBar2 != null) {
      localJScrollBar2.putClientProperty("JScrollBar.isFreeStanding", null);
    }
  }
  
  public void installListeners(JScrollPane paramJScrollPane)
  {
    super.installListeners(paramJScrollPane);
    scrollBarSwapListener = createScrollBarSwapListener();
    paramJScrollPane.addPropertyChangeListener(scrollBarSwapListener);
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    super.uninstallListeners(paramJComponent);
    paramJComponent.removePropertyChangeListener(scrollBarSwapListener);
  }
  
  @Deprecated
  public void uninstallListeners(JScrollPane paramJScrollPane)
  {
    super.uninstallListeners(paramJScrollPane);
    paramJScrollPane.removePropertyChangeListener(scrollBarSwapListener);
  }
  
  private void updateScrollbarsFreeStanding()
  {
    if (scrollpane == null) {
      return;
    }
    Border localBorder = scrollpane.getBorder();
    Boolean localBoolean;
    if ((localBorder instanceof MetalBorders.ScrollPaneBorder)) {
      localBoolean = Boolean.FALSE;
    } else {
      localBoolean = Boolean.TRUE;
    }
    JScrollBar localJScrollBar = scrollpane.getHorizontalScrollBar();
    if (localJScrollBar != null) {
      localJScrollBar.putClientProperty("JScrollBar.isFreeStanding", localBoolean);
    }
    localJScrollBar = scrollpane.getVerticalScrollBar();
    if (localJScrollBar != null) {
      localJScrollBar.putClientProperty("JScrollBar.isFreeStanding", localBoolean);
    }
  }
  
  protected PropertyChangeListener createScrollBarSwapListener()
  {
    new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        String str = paramAnonymousPropertyChangeEvent.getPropertyName();
        if ((str.equals("verticalScrollBar")) || (str.equals("horizontalScrollBar")))
        {
          JScrollBar localJScrollBar1 = (JScrollBar)paramAnonymousPropertyChangeEvent.getOldValue();
          if (localJScrollBar1 != null) {
            localJScrollBar1.putClientProperty("JScrollBar.isFreeStanding", null);
          }
          JScrollBar localJScrollBar2 = (JScrollBar)paramAnonymousPropertyChangeEvent.getNewValue();
          if (localJScrollBar2 != null) {
            localJScrollBar2.putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE);
          }
        }
        else if ("border".equals(str))
        {
          MetalScrollPaneUI.this.updateScrollbarsFreeStanding();
        }
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */