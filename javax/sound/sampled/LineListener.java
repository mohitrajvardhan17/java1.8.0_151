package javax.sound.sampled;

import java.util.EventListener;

public abstract interface LineListener
  extends EventListener
{
  public abstract void update(LineEvent paramLineEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\LineListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */