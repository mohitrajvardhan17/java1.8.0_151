package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;

public abstract interface IIOWriteWarningListener
  extends EventListener
{
  public abstract void warningOccurred(ImageWriter paramImageWriter, int paramInt, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\event\IIOWriteWarningListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */