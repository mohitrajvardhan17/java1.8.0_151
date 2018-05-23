package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public abstract interface IIOReadWarningListener
  extends EventListener
{
  public abstract void warningOccurred(ImageReader paramImageReader, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\event\IIOReadWarningListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */