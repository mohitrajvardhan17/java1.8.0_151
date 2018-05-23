package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollPanePeer;
import java.io.Serializable;

class PeerFixer
  implements AdjustmentListener, Serializable
{
  private static final long serialVersionUID = 7051237413532574756L;
  private ScrollPane scroller;
  
  PeerFixer(ScrollPane paramScrollPane)
  {
    scroller = paramScrollPane;
  }
  
  public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent)
  {
    Adjustable localAdjustable = paramAdjustmentEvent.getAdjustable();
    int i = paramAdjustmentEvent.getValue();
    ScrollPanePeer localScrollPanePeer = (ScrollPanePeer)scroller.peer;
    if (localScrollPanePeer != null) {
      localScrollPanePeer.setValue(localAdjustable, i);
    }
    Component localComponent = scroller.getComponent(0);
    switch (localAdjustable.getOrientation())
    {
    case 1: 
      localComponent.move(getLocationx, -i);
      break;
    case 0: 
      localComponent.move(-i, getLocationy);
      break;
    default: 
      throw new IllegalArgumentException("Illegal adjustable orientation");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\PeerFixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */