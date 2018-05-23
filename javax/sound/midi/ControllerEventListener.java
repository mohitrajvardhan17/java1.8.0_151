package javax.sound.midi;

import java.util.EventListener;

public abstract interface ControllerEventListener
  extends EventListener
{
  public abstract void controlChange(ShortMessage paramShortMessage);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\ControllerEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */