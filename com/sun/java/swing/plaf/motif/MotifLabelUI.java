package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;

public class MotifLabelUI
  extends BasicLabelUI
{
  private static final Object MOTIF_LABEL_UI_KEY = new Object();
  
  public MotifLabelUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MotifLabelUI localMotifLabelUI = (MotifLabelUI)localAppContext.get(MOTIF_LABEL_UI_KEY);
    if (localMotifLabelUI == null)
    {
      localMotifLabelUI = new MotifLabelUI();
      localAppContext.put(MOTIF_LABEL_UI_KEY, localMotifLabelUI);
    }
    return localMotifLabelUI;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */