package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;

public abstract interface IIOWriteProgressListener
  extends EventListener
{
  public abstract void imageStarted(ImageWriter paramImageWriter, int paramInt);
  
  public abstract void imageProgress(ImageWriter paramImageWriter, float paramFloat);
  
  public abstract void imageComplete(ImageWriter paramImageWriter);
  
  public abstract void thumbnailStarted(ImageWriter paramImageWriter, int paramInt1, int paramInt2);
  
  public abstract void thumbnailProgress(ImageWriter paramImageWriter, float paramFloat);
  
  public abstract void thumbnailComplete(ImageWriter paramImageWriter);
  
  public abstract void writeAborted(ImageWriter paramImageWriter);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\event\IIOWriteProgressListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */