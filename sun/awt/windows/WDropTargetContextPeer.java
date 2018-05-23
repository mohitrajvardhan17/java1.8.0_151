package sun.awt.windows;

import java.io.FileInputStream;
import java.io.IOException;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetContextPeer;
import sun.awt.dnd.SunDropTargetContextPeer.EventDispatcher;
import sun.awt.dnd.SunDropTargetEvent;

final class WDropTargetContextPeer
  extends SunDropTargetContextPeer
{
  static WDropTargetContextPeer getWDropTargetContextPeer()
  {
    return new WDropTargetContextPeer();
  }
  
  private WDropTargetContextPeer() {}
  
  private static FileInputStream getFileStream(String paramString, long paramLong)
    throws IOException
  {
    return new WDropTargetContextPeerFileStream(paramString, paramLong);
  }
  
  private static Object getIStream(long paramLong)
    throws IOException
  {
    return new WDropTargetContextPeerIStream(paramLong);
  }
  
  protected Object getNativeData(long paramLong)
  {
    return getData(getNativeDragContext(), paramLong);
  }
  
  protected void doDropDone(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    dropDone(getNativeDragContext(), paramBoolean1, paramInt);
  }
  
  protected void eventPosted(final SunDropTargetEvent paramSunDropTargetEvent)
  {
    if (paramSunDropTargetEvent.getID() != 502)
    {
      Runnable local1 = new Runnable()
      {
        public void run()
        {
          paramSunDropTargetEvent.getDispatcher().unregisterAllEvents();
        }
      };
      PeerEvent localPeerEvent = new PeerEvent(paramSunDropTargetEvent.getSource(), local1, 0L);
      SunToolkit.executeOnEventHandlerThread(localPeerEvent);
    }
  }
  
  private native Object getData(long paramLong1, long paramLong2);
  
  private native void dropDone(long paramLong, boolean paramBoolean, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */