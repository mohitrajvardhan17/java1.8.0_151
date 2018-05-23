package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import sun.awt.geom.PathConsumer2D;
import sun.security.action.GetPropertyAction;

public abstract class RenderingEngine
{
  private static RenderingEngine reImpl;
  
  public RenderingEngine() {}
  
  public static synchronized RenderingEngine getInstance()
  {
    if (reImpl != null) {
      return reImpl;
    }
    reImpl = (RenderingEngine)AccessController.doPrivileged(new PrivilegedAction()
    {
      public RenderingEngine run()
      {
        String str = System.getProperty("sun.java2d.renderer", "sun.dc.DuctusRenderingEngine");
        if (str.equals("sun.dc.DuctusRenderingEngine")) {
          try
          {
            Class localClass = Class.forName("sun.dc.DuctusRenderingEngine");
            return (RenderingEngine)localClass.newInstance();
          }
          catch (ReflectiveOperationException localReflectiveOperationException) {}
        }
        ServiceLoader localServiceLoader = ServiceLoader.loadInstalled(RenderingEngine.class);
        Object localObject = null;
        Iterator localIterator = localServiceLoader.iterator();
        while (localIterator.hasNext())
        {
          RenderingEngine localRenderingEngine = (RenderingEngine)localIterator.next();
          localObject = localRenderingEngine;
          if (localRenderingEngine.getClass().getName().equals(str)) {
            break;
          }
        }
        return (RenderingEngine)localObject;
      }
    });
    if (reImpl == null) {
      throw new InternalError("No RenderingEngine module found");
    }
    GetPropertyAction localGetPropertyAction = new GetPropertyAction("sun.java2d.renderer.trace");
    String str = (String)AccessController.doPrivileged(localGetPropertyAction);
    if (str != null) {
      reImpl = new Tracer(reImpl);
    }
    return reImpl;
  }
  
  public abstract Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3);
  
  public abstract void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D);
  
  public abstract AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt);
  
  public abstract AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt);
  
  public abstract float getMinimumAAPenSize();
  
  public static void feedConsumer(PathIterator paramPathIterator, PathConsumer2D paramPathConsumer2D)
  {
    float[] arrayOfFloat = new float[6];
    while (!paramPathIterator.isDone())
    {
      switch (paramPathIterator.currentSegment(arrayOfFloat))
      {
      case 0: 
        paramPathConsumer2D.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 1: 
        paramPathConsumer2D.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 2: 
        paramPathConsumer2D.quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
        break;
      case 3: 
        paramPathConsumer2D.curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
        break;
      case 4: 
        paramPathConsumer2D.closePath();
      }
      paramPathIterator.next();
    }
  }
  
  static class Tracer
    extends RenderingEngine
  {
    RenderingEngine target;
    String name;
    
    public Tracer(RenderingEngine paramRenderingEngine)
    {
      target = paramRenderingEngine;
      name = paramRenderingEngine.getClass().getName();
    }
    
    public Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3)
    {
      System.out.println(name + ".createStrokedShape(" + paramShape.getClass().getName() + ", width = " + paramFloat1 + ", caps = " + paramInt1 + ", join = " + paramInt2 + ", miter = " + paramFloat2 + ", dashes = " + paramArrayOfFloat + ", dashphase = " + paramFloat3 + ")");
      return target.createStrokedShape(paramShape, paramFloat1, paramInt1, paramInt2, paramFloat2, paramArrayOfFloat, paramFloat3);
    }
    
    public void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D)
    {
      System.out.println(name + ".strokeTo(" + paramShape.getClass().getName() + ", " + paramAffineTransform + ", " + paramBasicStroke + ", " + (paramBoolean1 ? "thin" : "wide") + ", " + (paramBoolean2 ? "normalized" : "pure") + ", " + (paramBoolean3 ? "AA" : "non-AA") + ", " + paramPathConsumer2D.getClass().getName() + ")");
      target.strokeTo(paramShape, paramAffineTransform, paramBasicStroke, paramBoolean1, paramBoolean2, paramBoolean3, paramPathConsumer2D);
    }
    
    public float getMinimumAAPenSize()
    {
      System.out.println(name + ".getMinimumAAPenSize()");
      return target.getMinimumAAPenSize();
    }
    
    public AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt)
    {
      System.out.println(name + ".getAATileGenerator(" + paramShape.getClass().getName() + ", " + paramAffineTransform + ", " + paramRegion + ", " + paramBasicStroke + ", " + (paramBoolean1 ? "thin" : "wide") + ", " + (paramBoolean2 ? "normalized" : "pure") + ")");
      return target.getAATileGenerator(paramShape, paramAffineTransform, paramRegion, paramBasicStroke, paramBoolean1, paramBoolean2, paramArrayOfInt);
    }
    
    public AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt)
    {
      System.out.println(name + ".getAATileGenerator(" + paramDouble1 + ", " + paramDouble2 + ", " + paramDouble3 + ", " + paramDouble4 + ", " + paramDouble5 + ", " + paramDouble6 + ", " + paramDouble7 + ", " + paramDouble8 + ", " + paramRegion + ")");
      return target.getAATileGenerator(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramRegion, paramArrayOfInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\RenderingEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */