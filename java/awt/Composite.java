package java.awt;

import java.awt.image.ColorModel;

public abstract interface Composite
{
  public abstract CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Composite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */