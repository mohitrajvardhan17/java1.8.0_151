package javax.swing.text.html;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class HRuleView
  extends View
{
  private float topMargin;
  private float bottomMargin;
  private float leftMargin;
  private float rightMargin;
  private int alignment = 1;
  private String noshade = null;
  private int size = 0;
  private CSS.LengthValue widthValue;
  private static final int SPACE_ABOVE = 3;
  private static final int SPACE_BELOW = 3;
  private AttributeSet attr;
  
  public HRuleView(Element paramElement)
  {
    super(paramElement);
    setPropertiesFromAttributes();
  }
  
  protected void setPropertiesFromAttributes()
  {
    StyleSheet localStyleSheet = ((HTMLDocument)getDocument()).getStyleSheet();
    AttributeSet localAttributeSet = getElement().getAttributes();
    attr = localStyleSheet.getViewAttributes(this);
    alignment = 1;
    size = 0;
    noshade = null;
    widthValue = null;
    if (attr != null)
    {
      if (attr.getAttribute(StyleConstants.Alignment) != null) {
        alignment = StyleConstants.getAlignment(attr);
      }
      noshade = ((String)localAttributeSet.getAttribute(HTML.Attribute.NOSHADE));
      Object localObject = localAttributeSet.getAttribute(HTML.Attribute.SIZE);
      if ((localObject != null) && ((localObject instanceof String))) {
        try
        {
          size = Integer.parseInt((String)localObject);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          size = 1;
        }
      }
      localObject = attr.getAttribute(CSS.Attribute.WIDTH);
      if ((localObject != null) && ((localObject instanceof CSS.LengthValue))) {
        widthValue = ((CSS.LengthValue)localObject);
      }
      topMargin = getLength(CSS.Attribute.MARGIN_TOP, attr);
      bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, attr);
      leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, attr);
      rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, attr);
    }
    else
    {
      topMargin = (bottomMargin = leftMargin = rightMargin = 0.0F);
    }
    size = Math.max(2, size);
  }
  
  private float getLength(CSS.Attribute paramAttribute, AttributeSet paramAttributeSet)
  {
    CSS.LengthValue localLengthValue = (CSS.LengthValue)paramAttributeSet.getAttribute(paramAttribute);
    float f = localLengthValue != null ? localLengthValue.getValue() : 0.0F;
    return f;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = 0;
    int j = y + 3 + (int)topMargin;
    int k = width - (int)(leftMargin + rightMargin);
    if (widthValue != null) {
      k = (int)widthValue.getValue(k);
    }
    int m = height - (6 + (int)topMargin + (int)bottomMargin);
    if (size > 0) {
      m = size;
    }
    switch (alignment)
    {
    case 1: 
      i = x + width / 2 - k / 2;
      break;
    case 2: 
      i = x + width - k - (int)rightMargin;
      break;
    case 0: 
    default: 
      i = x + (int)leftMargin;
    }
    if (noshade != null)
    {
      paramGraphics.setColor(Color.black);
      paramGraphics.fillRect(i, j, k, m);
    }
    else
    {
      Color localColor1 = getContainer().getBackground();
      Color localColor3;
      Color localColor2;
      if ((localColor1 == null) || (localColor1.equals(Color.white)))
      {
        localColor3 = Color.darkGray;
        localColor2 = Color.lightGray;
      }
      else
      {
        localColor3 = Color.darkGray;
        localColor2 = Color.white;
      }
      paramGraphics.setColor(localColor2);
      paramGraphics.drawLine(i + k - 1, j, i + k - 1, j + m - 1);
      paramGraphics.drawLine(i, j + m - 1, i + k - 1, j + m - 1);
      paramGraphics.setColor(localColor3);
      paramGraphics.drawLine(i, j, i + k - 1, j);
      paramGraphics.drawLine(i, j, i, j + m - 1);
    }
  }
  
  public float getPreferredSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 1.0F;
    case 1: 
      if (size > 0) {
        return size + 3 + 3 + topMargin + bottomMargin;
      }
      if (noshade != null) {
        return 8.0F + topMargin + bottomMargin;
      }
      return 6.0F + topMargin + bottomMargin;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public int getResizeWeight(int paramInt)
  {
    if (paramInt == 0) {
      return 1;
    }
    if (paramInt == 1) {
      return 0;
    }
    return 0;
  }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramInt == 0) {
      return 3000;
    }
    return 0;
  }
  
  public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    return null;
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
    return null;
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
  
  public AttributeSet getAttributes()
  {
    return attr;
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    int i = paramDocumentEvent.getOffset();
    if ((i <= getStartOffset()) && (i + paramDocumentEvent.getLength() >= getEndOffset())) {
      setPropertiesFromAttributes();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\HRuleView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */