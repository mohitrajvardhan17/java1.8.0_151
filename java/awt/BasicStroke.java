package java.awt;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import sun.java2d.pipe.RenderingEngine;

public class BasicStroke
  implements Stroke
{
  public static final int JOIN_MITER = 0;
  public static final int JOIN_ROUND = 1;
  public static final int JOIN_BEVEL = 2;
  public static final int CAP_BUTT = 0;
  public static final int CAP_ROUND = 1;
  public static final int CAP_SQUARE = 2;
  float width;
  int join;
  int cap;
  float miterlimit;
  float[] dash;
  float dash_phase;
  
  @ConstructorProperties({"lineWidth", "endCap", "lineJoin", "miterLimit", "dashArray", "dashPhase"})
  public BasicStroke(float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3)
  {
    if (paramFloat1 < 0.0F) {
      throw new IllegalArgumentException("negative width");
    }
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 2)) {
      throw new IllegalArgumentException("illegal end cap value");
    }
    if (paramInt2 == 0)
    {
      if (paramFloat2 < 1.0F) {
        throw new IllegalArgumentException("miter limit < 1");
      }
    }
    else if ((paramInt2 != 1) && (paramInt2 != 2)) {
      throw new IllegalArgumentException("illegal line join value");
    }
    if (paramArrayOfFloat != null)
    {
      if (paramFloat3 < 0.0F) {
        throw new IllegalArgumentException("negative dash phase");
      }
      int i = 1;
      for (int j = 0; j < paramArrayOfFloat.length; j++)
      {
        float f = paramArrayOfFloat[j];
        if (f > 0.0D) {
          i = 0;
        } else if (f < 0.0D) {
          throw new IllegalArgumentException("negative dash length");
        }
      }
      if (i != 0) {
        throw new IllegalArgumentException("dash lengths all zero");
      }
    }
    width = paramFloat1;
    cap = paramInt1;
    join = paramInt2;
    miterlimit = paramFloat2;
    if (paramArrayOfFloat != null) {
      dash = ((float[])paramArrayOfFloat.clone());
    }
    dash_phase = paramFloat3;
  }
  
  public BasicStroke(float paramFloat1, int paramInt1, int paramInt2, float paramFloat2)
  {
    this(paramFloat1, paramInt1, paramInt2, paramFloat2, null, 0.0F);
  }
  
  public BasicStroke(float paramFloat, int paramInt1, int paramInt2)
  {
    this(paramFloat, paramInt1, paramInt2, 10.0F, null, 0.0F);
  }
  
  public BasicStroke(float paramFloat)
  {
    this(paramFloat, 2, 0, 10.0F, null, 0.0F);
  }
  
  public BasicStroke()
  {
    this(1.0F, 2, 0, 10.0F, null, 0.0F);
  }
  
  public Shape createStrokedShape(Shape paramShape)
  {
    RenderingEngine localRenderingEngine = RenderingEngine.getInstance();
    return localRenderingEngine.createStrokedShape(paramShape, width, cap, join, miterlimit, dash, dash_phase);
  }
  
  public float getLineWidth()
  {
    return width;
  }
  
  public int getEndCap()
  {
    return cap;
  }
  
  public int getLineJoin()
  {
    return join;
  }
  
  public float getMiterLimit()
  {
    return miterlimit;
  }
  
  public float[] getDashArray()
  {
    if (dash == null) {
      return null;
    }
    return (float[])dash.clone();
  }
  
  public float getDashPhase()
  {
    return dash_phase;
  }
  
  public int hashCode()
  {
    int i = Float.floatToIntBits(width);
    i = i * 31 + join;
    i = i * 31 + cap;
    i = i * 31 + Float.floatToIntBits(miterlimit);
    if (dash != null)
    {
      i = i * 31 + Float.floatToIntBits(dash_phase);
      for (int j = 0; j < dash.length; j++) {
        i = i * 31 + Float.floatToIntBits(dash[j]);
      }
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof BasicStroke)) {
      return false;
    }
    BasicStroke localBasicStroke = (BasicStroke)paramObject;
    if (width != width) {
      return false;
    }
    if (join != join) {
      return false;
    }
    if (cap != cap) {
      return false;
    }
    if (miterlimit != miterlimit) {
      return false;
    }
    if (dash != null)
    {
      if (dash_phase != dash_phase) {
        return false;
      }
      if (!Arrays.equals(dash, dash)) {
        return false;
      }
    }
    else if (dash != null)
    {
      return false;
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\BasicStroke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */