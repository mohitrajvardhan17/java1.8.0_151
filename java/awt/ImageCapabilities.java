package java.awt;

public class ImageCapabilities
  implements Cloneable
{
  private boolean accelerated = false;
  
  public ImageCapabilities(boolean paramBoolean)
  {
    accelerated = paramBoolean;
  }
  
  public boolean isAccelerated()
  {
    return accelerated;
  }
  
  public boolean isTrueVolatile()
  {
    return false;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ImageCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */