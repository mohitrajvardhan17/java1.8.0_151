package sun.awt.windows;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.ChoicePeer;
import sun.awt.SunToolkit;

final class WChoicePeer
  extends WComponentPeer
  implements ChoicePeer
{
  private WindowListener windowListener;
  
  public Dimension getMinimumSize()
  {
    FontMetrics localFontMetrics = getFontMetrics(((Choice)target).getFont());
    Choice localChoice = (Choice)target;
    int i = 0;
    int j = localChoice.getItemCount();
    while (j-- > 0) {
      i = Math.max(localFontMetrics.stringWidth(localChoice.getItem(j)), i);
    }
    return new Dimension(28 + i, Math.max(localFontMetrics.getHeight() + 6, 15));
  }
  
  public boolean isFocusable()
  {
    return true;
  }
  
  public native void select(int paramInt);
  
  public void add(String paramString, int paramInt)
  {
    addItem(paramString, paramInt);
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return false;
  }
  
  public native void removeAll();
  
  public native void remove(int paramInt);
  
  public void addItem(String paramString, int paramInt)
  {
    addItems(new String[] { paramString }, paramInt);
  }
  
  public native void addItems(String[] paramArrayOfString, int paramInt);
  
  public synchronized native void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  WChoicePeer(Choice paramChoice)
  {
    super(paramChoice);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize()
  {
    Choice localChoice = (Choice)target;
    int i = localChoice.getItemCount();
    if (i > 0)
    {
      localObject = new String[i];
      for (int j = 0; j < i; j++) {
        localObject[j] = localChoice.getItem(j);
      }
      addItems((String[])localObject, 0);
      if (localChoice.getSelectedIndex() >= 0) {
        select(localChoice.getSelectedIndex());
      }
    }
    Object localObject = SunToolkit.getContainingWindow((Component)target);
    if (localObject != null)
    {
      WWindowPeer localWWindowPeer = (WWindowPeer)((Window)localObject).getPeer();
      if (localWWindowPeer != null)
      {
        windowListener = new WindowAdapter()
        {
          public void windowIconified(WindowEvent paramAnonymousWindowEvent)
          {
            closeList();
          }
          
          public void windowClosing(WindowEvent paramAnonymousWindowEvent)
          {
            closeList();
          }
        };
        localWWindowPeer.addWindowListener(windowListener);
      }
    }
    super.initialize();
  }
  
  protected void disposeImpl()
  {
    Window localWindow = SunToolkit.getContainingWindow((Component)target);
    if (localWindow != null)
    {
      WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
      if (localWWindowPeer != null) {
        localWWindowPeer.removeWindowListener(windowListener);
      }
    }
    super.disposeImpl();
  }
  
  void handleAction(final int paramInt)
  {
    final Choice localChoice = (Choice)target;
    WToolkit.executeOnEventHandlerThread(localChoice, new Runnable()
    {
      public void run()
      {
        localChoice.select(paramInt);
        postEvent(new ItemEvent(localChoice, 701, localChoice.getItem(paramInt), 1));
      }
    });
  }
  
  int getDropDownHeight()
  {
    Choice localChoice = (Choice)target;
    FontMetrics localFontMetrics = getFontMetrics(localChoice.getFont());
    int i = Math.min(localChoice.getItemCount(), 8);
    return localFontMetrics.getHeight() * i;
  }
  
  native void closeList();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WChoicePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */