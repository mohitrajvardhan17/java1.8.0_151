package sun.dc.pr;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;

public class PathDasher
  implements PathConsumer
{
  private PathConsumer dest;
  private static final float TOP_MAX_MIN_RATIO = 100.0F;
  private long cData;
  
  public PathDasher(PathConsumer paramPathConsumer)
  {
    if (paramPathConsumer == null) {
      throw new InternalError("null dest for path");
    }
    dest = paramPathConsumer;
    cInitialize(paramPathConsumer);
    reset();
  }
  
  public native void dispose();
  
  protected static void classFinalize()
    throws Throwable
  {}
  
  public PathConsumer getConsumer()
  {
    return dest;
  }
  
  public native void setDash(float[] paramArrayOfFloat, float paramFloat)
    throws PRError;
  
  public native void setDashT4(float[] paramArrayOfFloat)
    throws PRError;
  
  public native void setOutputT6(float[] paramArrayOfFloat)
    throws PRError;
  
  public native void setOutputConsumer(PathConsumer paramPathConsumer)
    throws PRError;
  
  public native void reset();
  
  public native void beginPath()
    throws PathError;
  
  public native void beginSubpath(float paramFloat1, float paramFloat2)
    throws PathError;
  
  public native void appendLine(float paramFloat1, float paramFloat2)
    throws PathError;
  
  public native void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    throws PathError;
  
  public native void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    throws PathError;
  
  public native void closedSubpath()
    throws PathError;
  
  public native void endPath()
    throws PathError, PathException;
  
  public void useProxy(FastPathProducer paramFastPathProducer)
    throws PathError, PathException
  {
    paramFastPathProducer.sendTo(this);
  }
  
  public native long getCPathConsumer();
  
  private static native void cClassInitialize();
  
  private static native void cClassFinalize();
  
  private native void cInitialize(PathConsumer paramPathConsumer);
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("dcpr");
        return null;
      }
    });
    cClassInitialize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\dc\pr\PathDasher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */