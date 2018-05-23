package sun.awt.image;

import java.awt.Image;
import java.util.List;

public abstract interface MultiResolutionImage
{
  public abstract Image getResolutionVariant(int paramInt1, int paramInt2);
  
  public abstract List<Image> getResolutionVariants();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\MultiResolutionImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */