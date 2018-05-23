package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollPanePeer;
import java.io.Serializable;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ScrollPaneAdjustableAccessor;

public class ScrollPaneAdjustable
  implements Adjustable, Serializable
{
  private ScrollPane sp;
  private int orientation;
  private int value;
  private int minimum;
  private int maximum;
  private int visibleAmount;
  private transient boolean isAdjusting;
  private int unitIncrement = 1;
  private int blockIncrement = 1;
  private AdjustmentListener adjustmentListener;
  private static final String SCROLLPANE_ONLY = "Can be set by scrollpane only";
  private static final long serialVersionUID = -3359745691033257079L;
  
  private static native void initIDs();
  
  ScrollPaneAdjustable(ScrollPane paramScrollPane, AdjustmentListener paramAdjustmentListener, int paramInt)
  {
    sp = paramScrollPane;
    orientation = paramInt;
    addAdjustmentListener(paramAdjustmentListener);
  }
  
  void setSpan(int paramInt1, int paramInt2, int paramInt3)
  {
    minimum = paramInt1;
    maximum = Math.max(paramInt2, minimum + 1);
    visibleAmount = Math.min(paramInt3, maximum - minimum);
    visibleAmount = Math.max(visibleAmount, 1);
    blockIncrement = Math.max((int)(paramInt3 * 0.9D), 1);
    setValue(value);
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setMinimum(int paramInt)
  {
    throw new AWTError("Can be set by scrollpane only");
  }
  
  public int getMinimum()
  {
    return 0;
  }
  
  public void setMaximum(int paramInt)
  {
    throw new AWTError("Can be set by scrollpane only");
  }
  
  public int getMaximum()
  {
    return maximum;
  }
  
  public synchronized void setUnitIncrement(int paramInt)
  {
    if (paramInt != unitIncrement)
    {
      unitIncrement = paramInt;
      if (sp.peer != null)
      {
        ScrollPanePeer localScrollPanePeer = (ScrollPanePeer)sp.peer;
        localScrollPanePeer.setUnitIncrement(this, paramInt);
      }
    }
  }
  
  public int getUnitIncrement()
  {
    return unitIncrement;
  }
  
  public synchronized void setBlockIncrement(int paramInt)
  {
    blockIncrement = paramInt;
  }
  
  public int getBlockIncrement()
  {
    return blockIncrement;
  }
  
  public void setVisibleAmount(int paramInt)
  {
    throw new AWTError("Can be set by scrollpane only");
  }
  
  public int getVisibleAmount()
  {
    return visibleAmount;
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    if (isAdjusting != paramBoolean)
    {
      isAdjusting = paramBoolean;
      AdjustmentEvent localAdjustmentEvent = new AdjustmentEvent(this, 601, 5, value, paramBoolean);
      adjustmentListener.adjustmentValueChanged(localAdjustmentEvent);
    }
  }
  
  public boolean getValueIsAdjusting()
  {
    return isAdjusting;
  }
  
  public void setValue(int paramInt)
  {
    setTypedValue(paramInt, 5);
  }
  
  private void setTypedValue(int paramInt1, int paramInt2)
  {
    paramInt1 = Math.max(paramInt1, minimum);
    paramInt1 = Math.min(paramInt1, maximum - visibleAmount);
    if (paramInt1 != value)
    {
      value = paramInt1;
      AdjustmentEvent localAdjustmentEvent = new AdjustmentEvent(this, 601, paramInt2, value, isAdjusting);
      adjustmentListener.adjustmentValueChanged(localAdjustmentEvent);
    }
  }
  
  public int getValue()
  {
    return value;
  }
  
  public synchronized void addAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    if (paramAdjustmentListener == null) {
      return;
    }
    adjustmentListener = AWTEventMulticaster.add(adjustmentListener, paramAdjustmentListener);
  }
  
  public synchronized void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    if (paramAdjustmentListener == null) {
      return;
    }
    adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, paramAdjustmentListener);
  }
  
  public synchronized AdjustmentListener[] getAdjustmentListeners()
  {
    return (AdjustmentListener[])AWTEventMulticaster.getListeners(adjustmentListener, AdjustmentListener.class);
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + paramString() + "]";
  }
  
  public String paramString()
  {
    return (orientation == 1 ? "vertical," : "horizontal,") + "[0.." + maximum + "],val=" + value + ",vis=" + visibleAmount + ",unit=" + unitIncrement + ",block=" + blockIncrement + ",isAdjusting=" + isAdjusting;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setScrollPaneAdjustableAccessor(new AWTAccessor.ScrollPaneAdjustableAccessor()
    {
      public void setTypedValue(ScrollPaneAdjustable paramAnonymousScrollPaneAdjustable, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousScrollPaneAdjustable.setTypedValue(paramAnonymousInt1, paramAnonymousInt2);
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ScrollPaneAdjustable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */