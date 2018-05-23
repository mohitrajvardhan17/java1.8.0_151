package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

public class ParagraphView
  extends javax.swing.text.ParagraphView
{
  private AttributeSet attr;
  private StyleSheet.BoxPainter painter;
  private CSS.LengthValue cssWidth;
  private CSS.LengthValue cssHeight;
  
  public ParagraphView(Element paramElement)
  {
    super(paramElement);
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if (paramView != null) {
      setPropertiesFromAttributes();
    }
  }
  
  public AttributeSet getAttributes()
  {
    if (attr == null)
    {
      StyleSheet localStyleSheet = getStyleSheet();
      attr = localStyleSheet.getViewAttributes(this);
    }
    return attr;
  }
  
  protected void setPropertiesFromAttributes()
  {
    StyleSheet localStyleSheet = getStyleSheet();
    attr = localStyleSheet.getViewAttributes(this);
    painter = localStyleSheet.getBoxPainter(attr);
    if (attr != null)
    {
      super.setPropertiesFromAttributes();
      setInsets((short)(int)painter.getInset(1, this), (short)(int)painter.getInset(2, this), (short)(int)painter.getInset(3, this), (short)(int)painter.getInset(4, this));
      Object localObject = attr.getAttribute(CSS.Attribute.TEXT_ALIGN);
      if (localObject != null)
      {
        String str = localObject.toString();
        if (str.equals("left")) {
          setJustification(0);
        } else if (str.equals("center")) {
          setJustification(1);
        } else if (str.equals("right")) {
          setJustification(2);
        } else if (str.equals("justify")) {
          setJustification(3);
        }
      }
      cssWidth = ((CSS.LengthValue)attr.getAttribute(CSS.Attribute.WIDTH));
      cssHeight = ((CSS.LengthValue)attr.getAttribute(CSS.Attribute.HEIGHT));
    }
  }
  
  protected StyleSheet getStyleSheet()
  {
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    return localHTMLDocument.getStyleSheet();
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    paramSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
    if (BlockView.spanSetFromAttributes(paramInt, paramSizeRequirements, cssWidth, cssHeight))
    {
      int i = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
      minimum -= i;
      preferred -= i;
      maximum -= i;
    }
    return paramSizeRequirements;
  }
  
  public boolean isVisible()
  {
    int i = getLayoutViewCount() - 1;
    Object localObject;
    for (int j = 0; j < i; j++)
    {
      localObject = getLayoutView(j);
      if (((View)localObject).isVisible()) {
        return true;
      }
    }
    if (i > 0)
    {
      View localView = getLayoutView(i);
      if (localView.getEndOffset() - localView.getStartOffset() == 1) {
        return false;
      }
    }
    if (getStartOffset() == getDocument().getLength())
    {
      boolean bool = false;
      localObject = getContainer();
      if ((localObject instanceof JTextComponent)) {
        bool = ((JTextComponent)localObject).isEditable();
      }
      if (!bool) {
        return false;
      }
    }
    return true;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    if (paramShape == null) {
      return;
    }
    Rectangle localRectangle;
    if ((paramShape instanceof Rectangle)) {
      localRectangle = (Rectangle)paramShape;
    } else {
      localRectangle = paramShape.getBounds();
    }
    painter.paint(paramGraphics, x, y, width, height, this);
    super.paint(paramGraphics, paramShape);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    if (!isVisible()) {
      return 0.0F;
    }
    return super.getPreferredSpan(paramInt);
  }
  
  public float getMinimumSpan(int paramInt)
  {
    if (!isVisible()) {
      return 0.0F;
    }
    return super.getMinimumSpan(paramInt);
  }
  
  public float getMaximumSpan(int paramInt)
  {
    if (!isVisible()) {
      return 0.0F;
    }
    return super.getMaximumSpan(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\ParagraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */