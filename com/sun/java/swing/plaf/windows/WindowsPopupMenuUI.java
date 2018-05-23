package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingUtilities2;

public class WindowsPopupMenuUI
  extends BasicPopupMenuUI
{
  static MnemonicListener mnemonicListener = null;
  static final Object GUTTER_OFFSET_KEY = new StringUIClientPropertyKey("GUTTER_OFFSET_KEY");
  
  public WindowsPopupMenuUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsPopupMenuUI();
  }
  
  public void installListeners()
  {
    super.installListeners();
    if ((!UIManager.getBoolean("Button.showMnemonics")) && (mnemonicListener == null))
    {
      mnemonicListener = new MnemonicListener();
      MenuSelectionManager.defaultManager().addChangeListener(mnemonicListener);
    }
  }
  
  public Popup getPopup(JPopupMenu paramJPopupMenu, int paramInt1, int paramInt2)
  {
    PopupFactory localPopupFactory = PopupFactory.getSharedInstance();
    return localPopupFactory.getPopup(paramJPopupMenu.getInvoker(), paramJPopupMenu, paramInt1, paramInt2);
  }
  
  static int getTextOffset(JComponent paramJComponent)
  {
    int i = -1;
    Object localObject = paramJComponent.getClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET);
    if ((localObject instanceof Integer))
    {
      i = ((Integer)localObject).intValue();
      int j = 0;
      Component localComponent = paramJComponent.getComponent(0);
      if (localComponent != null) {
        j = localComponent.getX();
      }
      i += j;
    }
    return i;
  }
  
  static int getSpanBeforeGutter()
  {
    return 3;
  }
  
  static int getSpanAfterGutter()
  {
    return 3;
  }
  
  static int getGutterWidth()
  {
    int i = 2;
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(null, TMSchema.Part.MP_POPUPGUTTER);
      i = localSkin.getWidth();
    }
    return i;
  }
  
  private static boolean isLeftToRight(JComponent paramJComponent)
  {
    boolean bool = true;
    for (int i = paramJComponent.getComponentCount() - 1; (i >= 0) && (bool); i--) {
      bool = paramJComponent.getComponent(i).getComponentOrientation().isLeftToRight();
    }
    return bool;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (WindowsMenuItemUI.isVistaPainting(localXPStyle))
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.MP_POPUPBACKGROUND);
      localSkin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), TMSchema.State.NORMAL);
      int i = getTextOffset(paramJComponent);
      if ((i >= 0) && (isLeftToRight(paramJComponent)))
      {
        localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.MP_POPUPGUTTER);
        int j = getGutterWidth();
        int k = i - getSpanAfterGutter() - j;
        paramJComponent.putClientProperty(GUTTER_OFFSET_KEY, Integer.valueOf(k));
        Insets localInsets = paramJComponent.getInsets();
        localSkin.paintSkin(paramGraphics, k, top, j, paramJComponent.getHeight() - bottom - top, TMSchema.State.NORMAL);
      }
      else if (paramJComponent.getClientProperty(GUTTER_OFFSET_KEY) != null)
      {
        paramJComponent.putClientProperty(GUTTER_OFFSET_KEY, null);
      }
    }
    else
    {
      super.paint(paramGraphics, paramJComponent);
    }
  }
  
  static class MnemonicListener
    implements ChangeListener
  {
    JRootPane repaintRoot = null;
    
    MnemonicListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      MenuSelectionManager localMenuSelectionManager = (MenuSelectionManager)paramChangeEvent.getSource();
      MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
      Object localObject;
      if (arrayOfMenuElement.length == 0)
      {
        if (!WindowsLookAndFeel.isMnemonicHidden())
        {
          WindowsLookAndFeel.setMnemonicHidden(true);
          if (repaintRoot != null)
          {
            localObject = SwingUtilities.getWindowAncestor(repaintRoot);
            WindowsGraphicsUtils.repaintMnemonicsInWindow((Window)localObject);
          }
        }
      }
      else
      {
        localObject = (Component)arrayOfMenuElement[0];
        if ((localObject instanceof JPopupMenu)) {
          localObject = ((JPopupMenu)localObject).getInvoker();
        }
        repaintRoot = SwingUtilities.getRootPane((Component)localObject);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */