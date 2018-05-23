package javax.swing.plaf;

import java.awt.Color;
import java.beans.ConstructorProperties;

public class ColorUIResource
  extends Color
  implements UIResource
{
  @ConstructorProperties({"red", "green", "blue"})
  public ColorUIResource(int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramInt1, paramInt2, paramInt3);
  }
  
  public ColorUIResource(int paramInt)
  {
    super(paramInt);
  }
  
  public ColorUIResource(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    super(paramFloat1, paramFloat2, paramFloat3);
  }
  
  public ColorUIResource(Color paramColor)
  {
    super(paramColor.getRGB(), (paramColor.getRGB() & 0xFF000000) != -16777216);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\ColorUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */