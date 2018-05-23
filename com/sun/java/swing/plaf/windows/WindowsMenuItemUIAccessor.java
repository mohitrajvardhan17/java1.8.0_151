package com.sun.java.swing.plaf.windows;

import javax.swing.JMenuItem;

abstract interface WindowsMenuItemUIAccessor
{
  public abstract JMenuItem getMenuItem();
  
  public abstract TMSchema.State getState(JMenuItem paramJMenuItem);
  
  public abstract TMSchema.Part getPart(JMenuItem paramJMenuItem);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsMenuItemUIAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */