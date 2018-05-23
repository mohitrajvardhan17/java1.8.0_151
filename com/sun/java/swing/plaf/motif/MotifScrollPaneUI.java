package com.sun.java.swing.plaf.motif;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class MotifScrollPaneUI
  extends BasicScrollPaneUI
{
  private static final Border vsbMarginBorderR = new EmptyBorder(0, 4, 0, 0);
  private static final Border vsbMarginBorderL = new EmptyBorder(0, 0, 0, 4);
  private static final Border hsbMarginBorder = new EmptyBorder(4, 0, 0, 0);
  private CompoundBorder vsbBorder;
  private CompoundBorder hsbBorder;
  private PropertyChangeListener propertyChangeHandler;
  
  public MotifScrollPaneUI() {}
  
  protected void installListeners(JScrollPane paramJScrollPane)
  {
    super.installListeners(paramJScrollPane);
    propertyChangeHandler = createPropertyChangeHandler();
    paramJScrollPane.addPropertyChangeListener(propertyChangeHandler);
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    super.uninstallListeners(paramJComponent);
    paramJComponent.removePropertyChangeListener(propertyChangeHandler);
  }
  
  private PropertyChangeListener createPropertyChangeHandler()
  {
    new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        String str = paramAnonymousPropertyChangeEvent.getPropertyName();
        if (str.equals("componentOrientation"))
        {
          JScrollPane localJScrollPane = (JScrollPane)paramAnonymousPropertyChangeEvent.getSource();
          JScrollBar localJScrollBar = localJScrollPane.getVerticalScrollBar();
          if ((localJScrollBar != null) && (vsbBorder != null) && (localJScrollBar.getBorder() == vsbBorder))
          {
            if (MotifGraphicsUtils.isLeftToRight(localJScrollPane)) {
              vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderR, vsbBorder.getInsideBorder());
            } else {
              vsbBorder = new CompoundBorder(MotifScrollPaneUI.vsbMarginBorderL, vsbBorder.getInsideBorder());
            }
            localJScrollBar.setBorder(vsbBorder);
          }
        }
      }
    };
  }
  
  protected void installDefaults(JScrollPane paramJScrollPane)
  {
    super.installDefaults(paramJScrollPane);
    JScrollBar localJScrollBar1 = paramJScrollPane.getVerticalScrollBar();
    if (localJScrollBar1 != null)
    {
      if (MotifGraphicsUtils.isLeftToRight(paramJScrollPane)) {
        vsbBorder = new CompoundBorder(vsbMarginBorderR, localJScrollBar1.getBorder());
      } else {
        vsbBorder = new CompoundBorder(vsbMarginBorderL, localJScrollBar1.getBorder());
      }
      localJScrollBar1.setBorder(vsbBorder);
    }
    JScrollBar localJScrollBar2 = paramJScrollPane.getHorizontalScrollBar();
    if (localJScrollBar2 != null)
    {
      hsbBorder = new CompoundBorder(hsbMarginBorder, localJScrollBar2.getBorder());
      localJScrollBar2.setBorder(hsbBorder);
    }
  }
  
  protected void uninstallDefaults(JScrollPane paramJScrollPane)
  {
    super.uninstallDefaults(paramJScrollPane);
    JScrollBar localJScrollBar1 = scrollpane.getVerticalScrollBar();
    if (localJScrollBar1 != null)
    {
      if (localJScrollBar1.getBorder() == vsbBorder) {
        localJScrollBar1.setBorder(null);
      }
      vsbBorder = null;
    }
    JScrollBar localJScrollBar2 = scrollpane.getHorizontalScrollBar();
    if (localJScrollBar2 != null)
    {
      if (localJScrollBar2.getBorder() == hsbBorder) {
        localJScrollBar2.setBorder(null);
      }
      hsbBorder = null;
    }
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifScrollPaneUI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */