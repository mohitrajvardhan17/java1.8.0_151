package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.Element;

public class ListView
  extends BlockView
{
  private StyleSheet.ListPainter listPainter;
  
  public ListView(Element paramElement)
  {
    super(paramElement, 1);
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 0.5F;
    case 1: 
      return 0.5F;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    super.paint(paramGraphics, paramShape);
    Rectangle localRectangle1 = paramShape.getBounds();
    Rectangle localRectangle2 = paramGraphics.getClipBounds();
    if (x + width < x + getLeftInset())
    {
      Rectangle localRectangle3 = localRectangle1;
      localRectangle1 = getInsideAllocation(paramShape);
      int i = getViewCount();
      int j = y + height;
      for (int k = 0; k < i; k++)
      {
        localRectangle3.setBounds(localRectangle1);
        childAllocation(k, localRectangle3);
        if (y >= j) {
          break;
        }
        if (y + height >= y) {
          listPainter.paint(paramGraphics, x, y, width, height, this, k);
        }
      }
    }
  }
  
  protected void paintChild(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    listPainter.paint(paramGraphics, x, y, width, height, this, paramInt);
    super.paintChild(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void setPropertiesFromAttributes()
  {
    super.setPropertiesFromAttributes();
    listPainter = getStyleSheet().getListPainter(getAttributes());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\ListView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */