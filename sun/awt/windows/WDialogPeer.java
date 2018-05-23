package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.peer.DialogPeer;
import java.util.Iterator;
import java.util.List;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.im.InputMethodManager;

final class WDialogPeer
  extends WWindowPeer
  implements DialogPeer
{
  static final Color defaultBackground = SystemColor.control;
  boolean needDefaultBackground;
  
  WDialogPeer(Dialog paramDialog)
  {
    super(paramDialog);
    InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
    String str = localInputMethodManager.getTriggerMenuString();
    if (str != null) {
      pSetIMMOption(str);
    }
  }
  
  native void createAwtDialog(WComponentPeer paramWComponentPeer);
  
  void create(WComponentPeer paramWComponentPeer)
  {
    preCreate(paramWComponentPeer);
    createAwtDialog(paramWComponentPeer);
  }
  
  native void showModal();
  
  native void endModal();
  
  void initialize()
  {
    Dialog localDialog = (Dialog)target;
    if (needDefaultBackground) {
      localDialog.setBackground(defaultBackground);
    }
    super.initialize();
    if (localDialog.getTitle() != null) {
      setTitle(localDialog.getTitle());
    }
    setResizable(localDialog.isResizable());
  }
  
  protected void realShow()
  {
    Dialog localDialog = (Dialog)target;
    if (localDialog.getModalityType() != Dialog.ModalityType.MODELESS) {
      showModal();
    } else {
      super.realShow();
    }
  }
  
  void hide()
  {
    Dialog localDialog = (Dialog)target;
    if (localDialog.getModalityType() != Dialog.ModalityType.MODELESS) {
      endModal();
    } else {
      super.hide();
    }
  }
  
  public void blockWindows(List<Window> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Window localWindow = (Window)localIterator.next();
      WWindowPeer localWWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow);
      if (localWWindowPeer != null) {
        localWWindowPeer.setModalBlocked((Dialog)target, true);
      }
    }
  }
  
  public Dimension getMinimumSize()
  {
    if (((Dialog)target).isUndecorated()) {
      return super.getMinimumSize();
    }
    return new Dimension(getSysMinWidth(), getSysMinHeight());
  }
  
  boolean isTargetUndecorated()
  {
    return ((Dialog)target).isUndecorated();
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (((Dialog)target).isUndecorated()) {
      super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      reshapeFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  private void setDefaultColor()
  {
    needDefaultBackground = true;
  }
  
  native void pSetIMMOption(String paramString);
  
  void notifyIMMOptionChange()
  {
    InputMethodManager.getInstance().notifyChangeRequest((Component)target);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */