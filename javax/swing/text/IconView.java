package javax.swing.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.Icon;

public class IconView
  extends View
{
  private Icon c;
  
  public IconView(Element paramElement)
  {
    super(paramElement);
    AttributeSet localAttributeSet = paramElement.getAttributes();
    c = StyleConstants.getIcon(localAttributeSet);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = paramShape.getBounds();
    c.paintIcon(getContainer(), paramGraphics, x, y);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return c.getIconWidth();
    case 1: 
      return c.getIconHeight();
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return 1.0F;
    }
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    int i = getStartOffset();
    int j = getEndOffset();
    if ((paramInt >= i) && (paramInt <= j))
    {
      Rectangle localRectangle = paramShape.getBounds();
      if (paramInt == j) {
        x += width;
      }
      width = 0;
      return localRectangle;
    }
    throw new BadLocationException(paramInt + " not in range " + i + "," + j, paramInt);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    if (paramFloat1 < x + width / 2)
    {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    }
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\IconView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */