package sun.java2d.pipe.hw;

import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.ImageCapabilities;

public class ExtendedBufferCapabilities
  extends BufferCapabilities
{
  private VSyncType vsync;
  
  public ExtendedBufferCapabilities(BufferCapabilities paramBufferCapabilities)
  {
    super(paramBufferCapabilities.getFrontBufferCapabilities(), paramBufferCapabilities.getBackBufferCapabilities(), paramBufferCapabilities.getFlipContents());
    vsync = VSyncType.VSYNC_DEFAULT;
  }
  
  public ExtendedBufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, BufferCapabilities.FlipContents paramFlipContents)
  {
    super(paramImageCapabilities1, paramImageCapabilities2, paramFlipContents);
    vsync = VSyncType.VSYNC_DEFAULT;
  }
  
  public ExtendedBufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, BufferCapabilities.FlipContents paramFlipContents, VSyncType paramVSyncType)
  {
    super(paramImageCapabilities1, paramImageCapabilities2, paramFlipContents);
    vsync = paramVSyncType;
  }
  
  public ExtendedBufferCapabilities(BufferCapabilities paramBufferCapabilities, VSyncType paramVSyncType)
  {
    super(paramBufferCapabilities.getFrontBufferCapabilities(), paramBufferCapabilities.getBackBufferCapabilities(), paramBufferCapabilities.getFlipContents());
    vsync = paramVSyncType;
  }
  
  public ExtendedBufferCapabilities derive(VSyncType paramVSyncType)
  {
    return new ExtendedBufferCapabilities(this, paramVSyncType);
  }
  
  public VSyncType getVSync()
  {
    return vsync;
  }
  
  public final boolean isPageFlipping()
  {
    return true;
  }
  
  public static enum VSyncType
  {
    VSYNC_DEFAULT(0),  VSYNC_ON(1),  VSYNC_OFF(2);
    
    private int id;
    
    public int id()
    {
      return id;
    }
    
    private VSyncType(int paramInt)
    {
      id = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\hw\ExtendedBufferCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */