package java.awt;

public class PointerInfo
{
  private final GraphicsDevice device;
  private final Point location;
  
  PointerInfo(GraphicsDevice paramGraphicsDevice, Point paramPoint)
  {
    device = paramGraphicsDevice;
    location = paramPoint;
  }
  
  public GraphicsDevice getDevice()
  {
    return device;
  }
  
  public Point getLocation()
  {
    return location;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\PointerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */