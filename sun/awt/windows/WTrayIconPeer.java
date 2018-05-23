package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.peer.TrayIconPeer;
import sun.awt.SunToolkit;
import sun.awt.image.IntegerComponentRaster;

final class WTrayIconPeer
  extends WObjectPeer
  implements TrayIconPeer
{
  static final int TRAY_ICON_WIDTH = 16;
  static final int TRAY_ICON_HEIGHT = 16;
  static final int TRAY_ICON_MASK_SIZE = 32;
  IconObserver observer = new IconObserver();
  boolean firstUpdate = true;
  Frame popupParent = new Frame("PopupMessageWindow");
  PopupMenu popup;
  
  protected void disposeImpl()
  {
    if (popupParent != null) {
      popupParent.dispose();
    }
    popupParent.dispose();
    _dispose();
    WToolkit.targetDisposedPeer(target, this);
  }
  
  WTrayIconPeer(TrayIcon paramTrayIcon)
  {
    target = paramTrayIcon;
    popupParent.addNotify();
    create();
    updateImage();
  }
  
  public void updateImage()
  {
    Image localImage = ((TrayIcon)target).getImage();
    if (localImage != null) {
      updateNativeImage(localImage);
    }
  }
  
  public native void setToolTip(String paramString);
  
  public synchronized void showPopupMenu(final int paramInt1, final int paramInt2)
  {
    if (isDisposed()) {
      return;
    }
    SunToolkit.executeOnEventHandlerThread(target, new Runnable()
    {
      public void run()
      {
        PopupMenu localPopupMenu = ((TrayIcon)target).getPopupMenu();
        if (popup != localPopupMenu)
        {
          if (popup != null) {
            popupParent.remove(popup);
          }
          if (localPopupMenu != null) {
            popupParent.add(localPopupMenu);
          }
          popup = localPopupMenu;
        }
        if (popup != null) {
          ((WPopupMenuPeer)popup.getPeer()).show(popupParent, new Point(paramInt1, paramInt2));
        }
      }
    });
  }
  
  public void displayMessage(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      paramString1 = "";
    }
    if (paramString2 == null) {
      paramString2 = "";
    }
    _displayMessage(paramString1, paramString2, paramString3);
  }
  
  synchronized void updateNativeImage(Image paramImage)
  {
    if (isDisposed()) {
      return;
    }
    boolean bool = ((TrayIcon)target).isImageAutoSize();
    BufferedImage localBufferedImage = new BufferedImage(16, 16, 2);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    if (localGraphics2D != null) {
      try
      {
        localGraphics2D.setPaintMode();
        localGraphics2D.drawImage(paramImage, 0, 0, bool ? 16 : paramImage.getWidth(observer), bool ? 16 : paramImage.getHeight(observer), observer);
        createNativeImage(localBufferedImage);
        updateNativeIcon(!firstUpdate);
        if (firstUpdate) {
          firstUpdate = false;
        }
      }
      finally
      {
        localGraphics2D.dispose();
      }
    }
  }
  
  void createNativeImage(BufferedImage paramBufferedImage)
  {
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    byte[] arrayOfByte = new byte[32];
    int[] arrayOfInt = ((DataBufferInt)localWritableRaster.getDataBuffer()).getData();
    int i = arrayOfInt.length;
    int j = localWritableRaster.getWidth();
    for (int k = 0; k < i; k++)
    {
      int m = k / 8;
      int n = 1 << 7 - k % 8;
      if (((arrayOfInt[k] & 0xFF000000) == 0) && (m < arrayOfByte.length))
      {
        int tmp83_81 = m;
        byte[] tmp83_80 = arrayOfByte;
        tmp83_80[tmp83_81] = ((byte)(tmp83_80[tmp83_81] | n));
      }
    }
    if ((localWritableRaster instanceof IntegerComponentRaster)) {
      j = ((IntegerComponentRaster)localWritableRaster).getScanlineStride();
    }
    setNativeIcon(((DataBufferInt)paramBufferedImage.getRaster().getDataBuffer()).getData(), arrayOfByte, j, localWritableRaster.getWidth(), localWritableRaster.getHeight());
  }
  
  void postEvent(AWTEvent paramAWTEvent)
  {
    WToolkit.postEvent(WToolkit.targetToAppContext(target), paramAWTEvent);
  }
  
  native void create();
  
  synchronized native void _dispose();
  
  native void updateNativeIcon(boolean paramBoolean);
  
  native void setNativeIcon(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  native void _displayMessage(String paramString1, String paramString2, String paramString3);
  
  class IconObserver
    implements ImageObserver
  {
    IconObserver() {}
    
    public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      if ((paramImage != ((TrayIcon)target).getImage()) || (isDisposed())) {
        return false;
      }
      if ((paramInt1 & 0x33) != 0) {
        updateNativeImage(paramImage);
      }
      return (paramInt1 & 0x20) == 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WTrayIconPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */