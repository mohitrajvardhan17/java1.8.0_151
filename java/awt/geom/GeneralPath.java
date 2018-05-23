package java.awt.geom;

import java.awt.Shape;

public final class GeneralPath
  extends Path2D.Float
{
  private static final long serialVersionUID = -8327096662768731142L;
  
  public GeneralPath()
  {
    super(1, 20);
  }
  
  public GeneralPath(int paramInt)
  {
    super(paramInt, 20);
  }
  
  public GeneralPath(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public GeneralPath(Shape paramShape)
  {
    super(paramShape, null);
  }
  
  GeneralPath(int paramInt1, byte[] paramArrayOfByte, int paramInt2, float[] paramArrayOfFloat, int paramInt3)
  {
    windingRule = paramInt1;
    pointTypes = paramArrayOfByte;
    numTypes = paramInt2;
    floatCoords = paramArrayOfFloat;
    numCoords = paramInt3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\GeneralPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */