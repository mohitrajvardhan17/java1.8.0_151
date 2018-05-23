package sun.awt.windows;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.DestSurfaceProvider;
import sun.java2d.InvalidPipeException;
import sun.java2d.Surface;
import sun.java2d.d3d.D3DSurfaceData;
import sun.java2d.opengl.WGLSurfaceData;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.security.action.GetPropertyAction;

abstract class TranslucentWindowPainter
{
  protected Window window;
  protected WWindowPeer peer;
  private static final boolean forceOpt = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forceopt", "false"))).booleanValue();
  private static final boolean forceSW = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forcesw", "false"))).booleanValue();
  
  public static TranslucentWindowPainter createInstance(WWindowPeer paramWWindowPeer)
  {
    GraphicsConfiguration localGraphicsConfiguration = paramWWindowPeer.getGraphicsConfiguration();
    if ((!forceSW) && ((localGraphicsConfiguration instanceof AccelGraphicsConfig)))
    {
      String str = localGraphicsConfiguration.getClass().getSimpleName();
      AccelGraphicsConfig localAccelGraphicsConfig = (AccelGraphicsConfig)localGraphicsConfiguration;
      if (((localAccelGraphicsConfig.getContextCapabilities().getCaps() & 0x100) != 0) || (forceOpt))
      {
        if (str.startsWith("D3D")) {
          return new VIOptD3DWindowPainter(paramWWindowPeer);
        }
        if ((forceOpt) && (str.startsWith("WGL"))) {
          return new VIOptWGLWindowPainter(paramWWindowPeer);
        }
      }
    }
    return new BIWindowPainter(paramWWindowPeer);
  }
  
  protected TranslucentWindowPainter(WWindowPeer paramWWindowPeer)
  {
    peer = paramWWindowPeer;
    window = ((Window)paramWWindowPeer.getTarget());
  }
  
  protected abstract Image getBackBuffer(boolean paramBoolean);
  
  protected abstract boolean update(Image paramImage);
  
  public abstract void flush();
  
  public void updateWindow(boolean paramBoolean)
  {
    boolean bool = false;
    Image localImage = getBackBuffer(paramBoolean);
    while (!bool)
    {
      if (paramBoolean)
      {
        Graphics2D localGraphics2D = (Graphics2D)localImage.getGraphics();
        try
        {
          window.paintAll(localGraphics2D);
        }
        finally
        {
          localGraphics2D.dispose();
        }
      }
      bool = update(localImage);
      if (!bool)
      {
        paramBoolean = true;
        localImage = getBackBuffer(true);
      }
    }
  }
  
  private static final Image clearImage(Image paramImage)
  {
    Graphics2D localGraphics2D = (Graphics2D)paramImage.getGraphics();
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    localGraphics2D.setComposite(AlphaComposite.Src);
    localGraphics2D.setColor(new Color(0, 0, 0, 0));
    localGraphics2D.fillRect(0, 0, i, j);
    return paramImage;
  }
  
  private static class BIWindowPainter
    extends TranslucentWindowPainter
  {
    private BufferedImage backBuffer;
    
    protected BIWindowPainter(WWindowPeer paramWWindowPeer)
    {
      super();
    }
    
    protected Image getBackBuffer(boolean paramBoolean)
    {
      int i = window.getWidth();
      int j = window.getHeight();
      if ((backBuffer == null) || (backBuffer.getWidth() != i) || (backBuffer.getHeight() != j))
      {
        flush();
        backBuffer = new BufferedImage(i, j, 3);
      }
      return paramBoolean ? (BufferedImage)TranslucentWindowPainter.clearImage(backBuffer) : backBuffer;
    }
    
    protected boolean update(Image paramImage)
    {
      VolatileImage localVolatileImage = null;
      if ((paramImage instanceof BufferedImage))
      {
        localObject = (BufferedImage)paramImage;
        int[] arrayOfInt1 = ((DataBufferInt)((BufferedImage)localObject).getRaster().getDataBuffer()).getData();
        peer.updateWindowImpl(arrayOfInt1, ((BufferedImage)localObject).getWidth(), ((BufferedImage)localObject).getHeight());
        return true;
      }
      if ((paramImage instanceof VolatileImage))
      {
        localVolatileImage = (VolatileImage)paramImage;
        if ((paramImage instanceof DestSurfaceProvider))
        {
          localObject = ((DestSurfaceProvider)paramImage).getDestSurface();
          if ((localObject instanceof BufImgSurfaceData))
          {
            int i = localVolatileImage.getWidth();
            int j = localVolatileImage.getHeight();
            BufImgSurfaceData localBufImgSurfaceData = (BufImgSurfaceData)localObject;
            int[] arrayOfInt3 = ((DataBufferInt)localBufImgSurfaceData.getRaster(0, 0, i, j).getDataBuffer()).getData();
            peer.updateWindowImpl(arrayOfInt3, i, j);
            return true;
          }
        }
      }
      Object localObject = (BufferedImage)TranslucentWindowPainter.clearImage(backBuffer);
      int[] arrayOfInt2 = ((DataBufferInt)((BufferedImage)localObject).getRaster().getDataBuffer()).getData();
      peer.updateWindowImpl(arrayOfInt2, ((BufferedImage)localObject).getWidth(), ((BufferedImage)localObject).getHeight());
      return !localVolatileImage.contentsLost();
    }
    
    public void flush()
    {
      if (backBuffer != null)
      {
        backBuffer.flush();
        backBuffer = null;
      }
    }
  }
  
  private static class VIOptD3DWindowPainter
    extends TranslucentWindowPainter.VIOptWindowPainter
  {
    protected VIOptD3DWindowPainter(WWindowPeer paramWWindowPeer)
    {
      super();
    }
    
    protected boolean updateWindowAccel(long paramLong, int paramInt1, int paramInt2)
    {
      return D3DSurfaceData.updateWindowAccelImpl(paramLong, peer.getData(), paramInt1, paramInt2);
    }
  }
  
  private static class VIOptWGLWindowPainter
    extends TranslucentWindowPainter.VIOptWindowPainter
  {
    protected VIOptWGLWindowPainter(WWindowPeer paramWWindowPeer)
    {
      super();
    }
    
    protected boolean updateWindowAccel(long paramLong, int paramInt1, int paramInt2)
    {
      return WGLSurfaceData.updateWindowAccelImpl(paramLong, peer, paramInt1, paramInt2);
    }
  }
  
  private static abstract class VIOptWindowPainter
    extends TranslucentWindowPainter.VIWindowPainter
  {
    protected VIOptWindowPainter(WWindowPeer paramWWindowPeer)
    {
      super();
    }
    
    protected abstract boolean updateWindowAccel(long paramLong, int paramInt1, int paramInt2);
    
    protected boolean update(Image paramImage)
    {
      if ((paramImage instanceof DestSurfaceProvider))
      {
        Surface localSurface = ((DestSurfaceProvider)paramImage).getDestSurface();
        if ((localSurface instanceof AccelSurface))
        {
          final int i = paramImage.getWidth(null);
          final int j = paramImage.getHeight(null);
          final boolean[] arrayOfBoolean = { false };
          final AccelSurface localAccelSurface = (AccelSurface)localSurface;
          RenderQueue localRenderQueue = localAccelSurface.getContext().getRenderQueue();
          localRenderQueue.lock();
          try
          {
            BufferedContext.validateContext(localAccelSurface);
            localRenderQueue.flushAndInvokeNow(new Runnable()
            {
              public void run()
              {
                long l = localAccelSurface.getNativeOps();
                arrayOfBoolean[0] = updateWindowAccel(l, i, j);
              }
            });
          }
          catch (InvalidPipeException localInvalidPipeException) {}finally
          {
            localRenderQueue.unlock();
          }
          return arrayOfBoolean[0];
        }
      }
      return super.update(paramImage);
    }
  }
  
  private static class VIWindowPainter
    extends TranslucentWindowPainter.BIWindowPainter
  {
    private VolatileImage viBB;
    
    protected VIWindowPainter(WWindowPeer paramWWindowPeer)
    {
      super();
    }
    
    protected Image getBackBuffer(boolean paramBoolean)
    {
      int i = window.getWidth();
      int j = window.getHeight();
      GraphicsConfiguration localGraphicsConfiguration = peer.getGraphicsConfiguration();
      if ((viBB == null) || (viBB.getWidth() != i) || (viBB.getHeight() != j) || (viBB.validate(localGraphicsConfiguration) == 2))
      {
        flush();
        if ((localGraphicsConfiguration instanceof AccelGraphicsConfig))
        {
          AccelGraphicsConfig localAccelGraphicsConfig = (AccelGraphicsConfig)localGraphicsConfiguration;
          viBB = localAccelGraphicsConfig.createCompatibleVolatileImage(i, j, 3, 2);
        }
        if (viBB == null) {
          viBB = localGraphicsConfiguration.createCompatibleVolatileImage(i, j, 3);
        }
        viBB.validate(localGraphicsConfiguration);
      }
      return paramBoolean ? TranslucentWindowPainter.clearImage(viBB) : viBB;
    }
    
    public void flush()
    {
      if (viBB != null)
      {
        viBB.flush();
        viBB = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\TranslucentWindowPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */