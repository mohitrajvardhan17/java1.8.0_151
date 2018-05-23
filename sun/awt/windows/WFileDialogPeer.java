package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.FileDialogPeer;
import java.io.File;
import java.io.FilenameFilter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AWTAccessor.FileDialogAccessor;
import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

final class WFileDialogPeer
  extends WWindowPeer
  implements FileDialogPeer
{
  private WComponentPeer parent;
  private FilenameFilter fileFilter;
  private Vector<WWindowPeer> blockedWindows = new Vector();
  
  private static native void setFilterString(String paramString);
  
  public void setFilenameFilter(FilenameFilter paramFilenameFilter)
  {
    fileFilter = paramFilenameFilter;
  }
  
  boolean checkFilenameFilter(String paramString)
  {
    FileDialog localFileDialog = (FileDialog)target;
    if (fileFilter == null) {
      return true;
    }
    File localFile = new File(paramString);
    return fileFilter.accept(new File(localFile.getParent()), localFile.getName());
  }
  
  WFileDialogPeer(FileDialog paramFileDialog)
  {
    super(paramFileDialog);
  }
  
  void create(WComponentPeer paramWComponentPeer)
  {
    parent = paramWComponentPeer;
  }
  
  protected void checkCreation() {}
  
  void initialize()
  {
    setFilenameFilter(((FileDialog)target).getFilenameFilter());
  }
  
  private native void _dispose();
  
  protected void disposeImpl()
  {
    WToolkit.targetDisposedPeer(target, this);
    _dispose();
  }
  
  private native void _show();
  
  private native void _hide();
  
  public void show()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        WFileDialogPeer.this._show();
      }
    }).start();
  }
  
  void hide()
  {
    _hide();
  }
  
  void setHWnd(long paramLong)
  {
    if (hwnd == paramLong) {
      return;
    }
    hwnd = paramLong;
    Iterator localIterator = blockedWindows.iterator();
    while (localIterator.hasNext())
    {
      WWindowPeer localWWindowPeer = (WWindowPeer)localIterator.next();
      if (paramLong != 0L) {
        localWWindowPeer.modalDisable((Dialog)target, paramLong);
      } else {
        localWWindowPeer.modalEnable((Dialog)target);
      }
    }
  }
  
  void handleSelected(char[] paramArrayOfChar)
  {
    String[] arrayOfString = new String(paramArrayOfChar).split("\000");
    int i = arrayOfString.length > 1 ? 1 : 0;
    String str1 = null;
    String str2 = null;
    File[] arrayOfFile = null;
    int j;
    if (i != 0)
    {
      str1 = arrayOfString[0];
      j = arrayOfString.length - 1;
      arrayOfFile = new File[j];
      for (int k = 0; k < j; k++) {
        arrayOfFile[k] = new File(str1, arrayOfString[(k + 1)]);
      }
      str2 = arrayOfString[1];
    }
    else
    {
      j = arrayOfString[0].lastIndexOf(File.separatorChar);
      if (j == -1)
      {
        str1 = "." + File.separator;
        str2 = arrayOfString[0];
      }
      else
      {
        str1 = arrayOfString[0].substring(0, j + 1);
        str2 = arrayOfString[0].substring(j + 1);
      }
      arrayOfFile = new File[] { new File(str1, str2) };
    }
    final FileDialog localFileDialog = (FileDialog)target;
    AWTAccessor.FileDialogAccessor localFileDialogAccessor = AWTAccessor.getFileDialogAccessor();
    localFileDialogAccessor.setDirectory(localFileDialog, str1);
    localFileDialogAccessor.setFile(localFileDialog, str2);
    localFileDialogAccessor.setFiles(localFileDialog, arrayOfFile);
    WToolkit.executeOnEventHandlerThread(localFileDialog, new Runnable()
    {
      public void run()
      {
        localFileDialog.setVisible(false);
      }
    });
  }
  
  void handleCancel()
  {
    final FileDialog localFileDialog = (FileDialog)target;
    AWTAccessor.getFileDialogAccessor().setFile(localFileDialog, null);
    AWTAccessor.getFileDialogAccessor().setFiles(localFileDialog, null);
    AWTAccessor.getFileDialogAccessor().setDirectory(localFileDialog, null);
    WToolkit.executeOnEventHandlerThread(localFileDialog, new Runnable()
    {
      public void run()
      {
        localFileDialog.setVisible(false);
      }
    });
  }
  
  void blockWindow(WWindowPeer paramWWindowPeer)
  {
    blockedWindows.add(paramWWindowPeer);
    if (hwnd != 0L) {
      paramWWindowPeer.modalDisable((Dialog)target, hwnd);
    }
  }
  
  void unblockWindow(WWindowPeer paramWWindowPeer)
  {
    blockedWindows.remove(paramWWindowPeer);
    if (hwnd != 0L) {
      paramWWindowPeer.modalEnable((Dialog)target);
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
        blockWindow(localWWindowPeer);
      }
    }
  }
  
  public native void toFront();
  
  public native void toBack();
  
  public void updateAlwaysOnTopState() {}
  
  public void setDirectory(String paramString) {}
  
  public void setFile(String paramString) {}
  
  public void setTitle(String paramString) {}
  
  public void setResizable(boolean paramBoolean) {}
  
  void enable() {}
  
  void disable() {}
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public boolean handleEvent(Event paramEvent)
  {
    return false;
  }
  
  public void setForeground(Color paramColor) {}
  
  public void setBackground(Color paramColor) {}
  
  public void setFont(Font paramFont) {}
  
  public void updateMinimumSize() {}
  
  public void updateIconImages() {}
  
  public boolean requestFocus(boolean paramBoolean1, boolean paramBoolean2)
  {
    return false;
  }
  
  public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
  {
    return false;
  }
  
  void start() {}
  
  public void beginValidate() {}
  
  public void endValidate() {}
  
  void invalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void addDropTarget(DropTarget paramDropTarget) {}
  
  public void removeDropTarget(DropTarget paramDropTarget) {}
  
  public void updateFocusableWindowState() {}
  
  public void setZOrder(ComponentPeer paramComponentPeer) {}
  
  private static native void initIDs();
  
  public void applyShape(Region paramRegion) {}
  
  public void setOpacity(float paramFloat) {}
  
  public void setOpaque(boolean paramBoolean) {}
  
  public void updateWindow(BufferedImage paramBufferedImage) {}
  
  public void createScreenSurface(boolean paramBoolean) {}
  
  public void replaceSurfaceData() {}
  
  public boolean isMultipleMode()
  {
    FileDialog localFileDialog = (FileDialog)target;
    return AWTAccessor.getFileDialogAccessor().isMultipleMode(localFileDialog);
  }
  
  static
  {
    initIDs();
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          ResourceBundle localResourceBundle = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
          return localResourceBundle.getString("allFiles");
        }
        catch (MissingResourceException localMissingResourceException) {}
        return "All Files";
      }
    });
    setFilterString(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WFileDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */