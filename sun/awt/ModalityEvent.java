package sun.awt;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;

public class ModalityEvent
  extends AWTEvent
  implements ActiveEvent
{
  public static final int MODALITY_PUSHED = 1300;
  public static final int MODALITY_POPPED = 1301;
  private ModalityListener listener;
  
  public ModalityEvent(Object paramObject, ModalityListener paramModalityListener, int paramInt)
  {
    super(paramObject, paramInt);
    listener = paramModalityListener;
  }
  
  public void dispatch()
  {
    switch (getID())
    {
    case 1300: 
      listener.modalityPushed(this);
      break;
    case 1301: 
      listener.modalityPopped(this);
      break;
    default: 
      throw new Error("Invalid event id.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\ModalityEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */