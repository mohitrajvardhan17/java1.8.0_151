package com.sun.java.swing.plaf.windows;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;
import javax.swing.plaf.UIResource;

public class WindowsDesktopManager
  extends DefaultDesktopManager
  implements Serializable, UIResource
{
  private WeakReference<JInternalFrame> currentFrameRef;
  
  public WindowsDesktopManager() {}
  
  public void activateFrame(JInternalFrame paramJInternalFrame)
  {
    Object localObject = currentFrameRef != null ? (JInternalFrame)currentFrameRef.get() : null;
    try
    {
      super.activateFrame(paramJInternalFrame);
      if ((localObject != null) && (paramJInternalFrame != localObject))
      {
        if ((((JInternalFrame)localObject).isMaximum()) && (paramJInternalFrame.getClientProperty("JInternalFrame.frameType") != "optionDialog") && (!((JInternalFrame)localObject).isIcon()))
        {
          ((JInternalFrame)localObject).setMaximum(false);
          if (paramJInternalFrame.isMaximizable()) {
            if (!paramJInternalFrame.isMaximum()) {
              paramJInternalFrame.setMaximum(true);
            } else if ((paramJInternalFrame.isMaximum()) && (paramJInternalFrame.isIcon())) {
              paramJInternalFrame.setIcon(false);
            } else {
              paramJInternalFrame.setMaximum(false);
            }
          }
        }
        if (((JInternalFrame)localObject).isSelected()) {
          ((JInternalFrame)localObject).setSelected(false);
        }
      }
      if (!paramJInternalFrame.isSelected()) {
        paramJInternalFrame.setSelected(true);
      }
    }
    catch (PropertyVetoException localPropertyVetoException) {}
    if (paramJInternalFrame != localObject) {
      currentFrameRef = new WeakReference(paramJInternalFrame);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsDesktopManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */