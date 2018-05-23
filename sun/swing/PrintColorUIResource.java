package sun.swing;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;

public class PrintColorUIResource
  extends ColorUIResource
{
  private Color printColor;
  
  public PrintColorUIResource(int paramInt, Color paramColor)
  {
    super(paramInt);
    printColor = paramColor;
  }
  
  public Color getPrintColor()
  {
    return printColor != null ? printColor : this;
  }
  
  private Object writeReplace()
  {
    return new ColorUIResource(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\PrintColorUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */