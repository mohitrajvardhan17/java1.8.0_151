package sun.swing;

import java.awt.Point;
import javax.swing.RepaintManager;
import javax.swing.TransferHandler.DropLocation;
import javax.swing.text.JTextComponent;
import sun.misc.Unsafe;

public final class SwingAccessor
{
  private static final Unsafe unsafe = ;
  private static JTextComponentAccessor jtextComponentAccessor;
  private static JLightweightFrameAccessor jLightweightFrameAccessor;
  private static RepaintManagerAccessor repaintManagerAccessor;
  
  private SwingAccessor() {}
  
  public static void setJTextComponentAccessor(JTextComponentAccessor paramJTextComponentAccessor)
  {
    jtextComponentAccessor = paramJTextComponentAccessor;
  }
  
  public static JTextComponentAccessor getJTextComponentAccessor()
  {
    if (jtextComponentAccessor == null) {
      unsafe.ensureClassInitialized(JTextComponent.class);
    }
    return jtextComponentAccessor;
  }
  
  public static void setJLightweightFrameAccessor(JLightweightFrameAccessor paramJLightweightFrameAccessor)
  {
    jLightweightFrameAccessor = paramJLightweightFrameAccessor;
  }
  
  public static JLightweightFrameAccessor getJLightweightFrameAccessor()
  {
    if (jLightweightFrameAccessor == null) {
      unsafe.ensureClassInitialized(JLightweightFrame.class);
    }
    return jLightweightFrameAccessor;
  }
  
  public static void setRepaintManagerAccessor(RepaintManagerAccessor paramRepaintManagerAccessor)
  {
    repaintManagerAccessor = paramRepaintManagerAccessor;
  }
  
  public static RepaintManagerAccessor getRepaintManagerAccessor()
  {
    if (repaintManagerAccessor == null) {
      unsafe.ensureClassInitialized(RepaintManager.class);
    }
    return repaintManagerAccessor;
  }
  
  public static abstract interface JLightweightFrameAccessor
  {
    public abstract void updateCursor(JLightweightFrame paramJLightweightFrame);
  }
  
  public static abstract interface JTextComponentAccessor
  {
    public abstract TransferHandler.DropLocation dropLocationForPoint(JTextComponent paramJTextComponent, Point paramPoint);
    
    public abstract Object setDropLocation(JTextComponent paramJTextComponent, TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean);
  }
  
  public static abstract interface RepaintManagerAccessor
  {
    public abstract void addRepaintListener(RepaintManager paramRepaintManager, SwingUtilities2.RepaintListener paramRepaintListener);
    
    public abstract void removeRepaintListener(RepaintManager paramRepaintManager, SwingUtilities2.RepaintListener paramRepaintListener);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\SwingAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */