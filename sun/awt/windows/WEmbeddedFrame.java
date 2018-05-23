package sun.awt.windows;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.EmbeddedFrame;
import sun.awt.image.ByteInterleavedRaster;
import sun.security.action.GetPropertyAction;

public class WEmbeddedFrame
  extends EmbeddedFrame
{
  private long handle;
  private int bandWidth = 0;
  private int bandHeight = 0;
  private int imgWid = 0;
  private int imgHgt = 0;
  private static int pScale = 0;
  private static final int MAX_BAND_SIZE = 30720;
  private boolean isEmbeddedInIE = false;
  private static String printScale = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pluginscalefactor"));
  
  public WEmbeddedFrame()
  {
    this(0L);
  }
  
  @Deprecated
  public WEmbeddedFrame(int paramInt)
  {
    this(paramInt);
  }
  
  public WEmbeddedFrame(long paramLong)
  {
    handle = paramLong;
    if (paramLong != 0L)
    {
      addNotify();
      show();
    }
  }
  
  public void addNotify()
  {
    if (getPeer() == null)
    {
      WToolkit localWToolkit = (WToolkit)Toolkit.getDefaultToolkit();
      setPeer(localWToolkit.createEmbeddedFrame(this));
    }
    super.addNotify();
  }
  
  public long getEmbedderHandle()
  {
    return handle;
  }
  
  void print(long paramLong)
  {
    BufferedImage localBufferedImage = null;
    int i = 1;
    int j = 1;
    if (isPrinterDC(paramLong)) {
      i = j = getPrintScaleFactor();
    }
    int k = getHeight();
    if (localBufferedImage == null)
    {
      bandWidth = getWidth();
      if (bandWidth % 4 != 0) {
        bandWidth += 4 - bandWidth % 4;
      }
      if (bandWidth <= 0) {
        return;
      }
      bandHeight = Math.min(30720 / bandWidth, k);
      imgWid = (bandWidth * i);
      imgHgt = (bandHeight * j);
      localBufferedImage = new BufferedImage(imgWid, imgHgt, 5);
    }
    Graphics localGraphics = localBufferedImage.getGraphics();
    localGraphics.setColor(Color.white);
    Graphics2D localGraphics2D = (Graphics2D)localBufferedImage.getGraphics();
    localGraphics2D.translate(0, imgHgt);
    localGraphics2D.scale(i, -j);
    ByteInterleavedRaster localByteInterleavedRaster = (ByteInterleavedRaster)localBufferedImage.getRaster();
    byte[] arrayOfByte = localByteInterleavedRaster.getDataStorage();
    int m = 0;
    while (m < k)
    {
      localGraphics.fillRect(0, 0, bandWidth, bandHeight);
      printComponents(localGraphics2D);
      int n = 0;
      int i1 = bandHeight;
      int i2 = imgHgt;
      if (m + bandHeight > k)
      {
        i1 = k - m;
        i2 = i1 * j;
        n = imgWid * (imgHgt - i2) * 3;
      }
      printBand(paramLong, arrayOfByte, n, 0, 0, imgWid, i2, 0, m, bandWidth, i1);
      localGraphics2D.translate(0, -bandHeight);
      m += bandHeight;
    }
  }
  
  protected static int getPrintScaleFactor()
  {
    if (pScale != 0) {
      return pScale;
    }
    if (printScale == null) {
      printScale = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getenv("JAVA2D_PLUGIN_PRINT_SCALE");
        }
      });
    }
    int i = 4;
    int j = i;
    if (printScale != null) {
      try
      {
        j = Integer.parseInt(printScale);
        if ((j > 8) || (j < 1)) {
          j = i;
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    pScale = j;
    return pScale;
  }
  
  private native boolean isPrinterDC(long paramLong);
  
  private native void printBand(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
  
  private static native void initIDs();
  
  public void activateEmbeddingTopLevel() {}
  
  public void synthesizeWindowActivation(boolean paramBoolean)
  {
    if ((!paramBoolean) || (EventQueue.isDispatchThread()))
    {
      ((WFramePeer)getPeer()).emulateActivation(paramBoolean);
    }
    else
    {
      Runnable local2 = new Runnable()
      {
        public void run()
        {
          ((WFramePeer)getPeer()).emulateActivation(true);
        }
      };
      WToolkit.postEvent(WToolkit.targetToAppContext(this), new InvocationEvent(this, local2));
    }
  }
  
  public void registerAccelerator(AWTKeyStroke paramAWTKeyStroke) {}
  
  public void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke) {}
  
  public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean)
  {
    try
    {
      ComponentPeer localComponentPeer1 = (ComponentPeer)WToolkit.targetToPeer(this);
      ComponentPeer localComponentPeer2 = (ComponentPeer)WToolkit.targetToPeer(paramDialog);
      notifyModalBlockedImpl((WEmbeddedFramePeer)localComponentPeer1, (WWindowPeer)localComponentPeer2, paramBoolean);
    }
    catch (Exception localException)
    {
      localException.printStackTrace(System.err);
    }
  }
  
  native void notifyModalBlockedImpl(WEmbeddedFramePeer paramWEmbeddedFramePeer, WWindowPeer paramWWindowPeer, boolean paramBoolean);
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WEmbeddedFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */