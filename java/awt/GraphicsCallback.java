package java.awt;

import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import sun.awt.SunGraphicsCallback;

abstract class GraphicsCallback
  extends SunGraphicsCallback
{
  GraphicsCallback() {}
  
  static final class PaintAllCallback
    extends GraphicsCallback
  {
    private static PaintAllCallback instance = new PaintAllCallback();
    
    private PaintAllCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      paramComponent.paintAll(paramGraphics);
    }
    
    static PaintAllCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PaintCallback
    extends GraphicsCallback
  {
    private static PaintCallback instance = new PaintCallback();
    
    private PaintCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      paramComponent.paint(paramGraphics);
    }
    
    static PaintCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PaintHeavyweightComponentsCallback
    extends GraphicsCallback
  {
    private static PaintHeavyweightComponentsCallback instance = new PaintHeavyweightComponentsCallback();
    
    private PaintHeavyweightComponentsCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      if ((peer instanceof LightweightPeer)) {
        paramComponent.paintHeavyweightComponents(paramGraphics);
      } else {
        paramComponent.paintAll(paramGraphics);
      }
    }
    
    static PaintHeavyweightComponentsCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PeerPaintCallback
    extends GraphicsCallback
  {
    private static PeerPaintCallback instance = new PeerPaintCallback();
    
    private PeerPaintCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      paramComponent.validate();
      if ((peer instanceof LightweightPeer)) {
        paramComponent.lightweightPaint(paramGraphics);
      } else {
        peer.paint(paramGraphics);
      }
    }
    
    static PeerPaintCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PeerPrintCallback
    extends GraphicsCallback
  {
    private static PeerPrintCallback instance = new PeerPrintCallback();
    
    private PeerPrintCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      paramComponent.validate();
      if ((peer instanceof LightweightPeer)) {
        paramComponent.lightweightPrint(paramGraphics);
      } else {
        peer.print(paramGraphics);
      }
    }
    
    static PeerPrintCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PrintAllCallback
    extends GraphicsCallback
  {
    private static PrintAllCallback instance = new PrintAllCallback();
    
    private PrintAllCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      paramComponent.printAll(paramGraphics);
    }
    
    static PrintAllCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PrintCallback
    extends GraphicsCallback
  {
    private static PrintCallback instance = new PrintCallback();
    
    private PrintCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      paramComponent.print(paramGraphics);
    }
    
    static PrintCallback getInstance()
    {
      return instance;
    }
  }
  
  static final class PrintHeavyweightComponentsCallback
    extends GraphicsCallback
  {
    private static PrintHeavyweightComponentsCallback instance = new PrintHeavyweightComponentsCallback();
    
    private PrintHeavyweightComponentsCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      if ((peer instanceof LightweightPeer)) {
        paramComponent.printHeavyweightComponents(paramGraphics);
      } else {
        paramComponent.printAll(paramGraphics);
      }
    }
    
    static PrintHeavyweightComponentsCallback getInstance()
    {
      return instance;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GraphicsCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */