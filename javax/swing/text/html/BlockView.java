package javax.swing.text.html;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class BlockView
  extends BoxView
{
  private AttributeSet attr;
  private StyleSheet.BoxPainter painter;
  private CSS.LengthValue cssWidth;
  private CSS.LengthValue cssHeight;
  
  public BlockView(Element paramElement, int paramInt)
  {
    super(paramElement, paramInt);
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if (paramView != null) {
      setPropertiesFromAttributes();
    }
  }
  
  protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    if (!spanSetFromAttributes(paramInt, paramSizeRequirements, cssWidth, cssHeight))
    {
      paramSizeRequirements = super.calculateMajorAxisRequirements(paramInt, paramSizeRequirements);
    }
    else
    {
      SizeRequirements localSizeRequirements = super.calculateMajorAxisRequirements(paramInt, null);
      int i = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
      minimum -= i;
      preferred -= i;
      maximum -= i;
      constrainSize(paramInt, paramSizeRequirements, localSizeRequirements);
    }
    return paramSizeRequirements;
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    Object localObject;
    if (!spanSetFromAttributes(paramInt, paramSizeRequirements, cssWidth, cssHeight))
    {
      paramSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
    }
    else
    {
      localObject = super.calculateMinorAxisRequirements(paramInt, null);
      int i = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
      minimum -= i;
      preferred -= i;
      maximum -= i;
      constrainSize(paramInt, paramSizeRequirements, (SizeRequirements)localObject);
    }
    if (paramInt == 0)
    {
      localObject = getAttributes().getAttribute(CSS.Attribute.TEXT_ALIGN);
      if (localObject != null)
      {
        String str = localObject.toString();
        if (str.equals("center")) {
          alignment = 0.5F;
        } else if (str.equals("right")) {
          alignment = 1.0F;
        } else {
          alignment = 0.0F;
        }
      }
    }
    return paramSizeRequirements;
  }
  
  boolean isPercentage(int paramInt, AttributeSet paramAttributeSet)
  {
    if (paramInt == 0)
    {
      if (cssWidth != null) {
        return cssWidth.isPercentage();
      }
    }
    else if (cssHeight != null) {
      return cssHeight.isPercentage();
    }
    return false;
  }
  
  static boolean spanSetFromAttributes(int paramInt, SizeRequirements paramSizeRequirements, CSS.LengthValue paramLengthValue1, CSS.LengthValue paramLengthValue2)
  {
    if (paramInt == 0)
    {
      if ((paramLengthValue1 != null) && (!paramLengthValue1.isPercentage()))
      {
        minimum = (preferred = maximum = (int)paramLengthValue1.getValue());
        return true;
      }
    }
    else if ((paramLengthValue2 != null) && (!paramLengthValue2.isPercentage()))
    {
      minimum = (preferred = maximum = (int)paramLengthValue2.getValue());
      return true;
    }
    return false;
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int i = getViewCount();
    CSS.Attribute localAttribute = paramInt2 == 0 ? CSS.Attribute.WIDTH : CSS.Attribute.HEIGHT;
    for (int j = 0; j < i; j++)
    {
      View localView = getView(j);
      int k = (int)localView.getMinimumSpan(paramInt2);
      AttributeSet localAttributeSet = localView.getAttributes();
      CSS.LengthValue localLengthValue = (CSS.LengthValue)localAttributeSet.getAttribute(localAttribute);
      int m;
      if ((localLengthValue != null) && (localLengthValue.isPercentage()))
      {
        k = Math.max((int)localLengthValue.getValue(paramInt1), k);
        m = k;
      }
      else
      {
        m = (int)localView.getMaximumSpan(paramInt2);
      }
      if (m < paramInt1)
      {
        float f = localView.getAlignment(paramInt2);
        paramArrayOfInt1[j] = ((int)((paramInt1 - m) * f));
        paramArrayOfInt2[j] = m;
      }
      else
      {
        paramArrayOfInt1[j] = 0;
        paramArrayOfInt2[j] = Math.max(k, paramInt1);
      }
    }
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    painter.paint(paramGraphics, x, y, width, height, this);
    super.paint(paramGraphics, localRectangle);
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
  
  public int getResizeWeight(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 1;
    case 1: 
      return 0;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 0.0F;
    case 1: 
      if (getViewCount() == 0) {
        return 0.0F;
      }
      float f1 = getPreferredSpan(1);
      View localView = getView(0);
      float f2 = localView.getPreferredSpan(1);
      float f3 = (int)f1 != 0 ? f2 * localView.getAlignment(1) / f1 : 0.0F;
      return f3;
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    int i = paramDocumentEvent.getOffset();
    if ((i <= getStartOffset()) && (i + paramDocumentEvent.getLength() >= getEndOffset())) {
      setPropertiesFromAttributes();
    }
  }
  
  public float getPreferredSpan(int paramInt)
  {
    return super.getPreferredSpan(paramInt);
  }
  
  public float getMinimumSpan(int paramInt)
  {
    return super.getMinimumSpan(paramInt);
  }
  
  public float getMaximumSpan(int paramInt)
  {
    return super.getMaximumSpan(paramInt);
  }
  
  protected void setPropertiesFromAttributes()
  {
    StyleSheet localStyleSheet = getStyleSheet();
    attr = localStyleSheet.getViewAttributes(this);
    painter = localStyleSheet.getBoxPainter(attr);
    if (attr != null) {
      setInsets((short)(int)painter.getInset(1, this), (short)(int)painter.getInset(2, this), (short)(int)painter.getInset(3, this), (short)(int)painter.getInset(4, this));
    }
    cssWidth = ((CSS.LengthValue)attr.getAttribute(CSS.Attribute.WIDTH));
    cssHeight = ((CSS.LengthValue)attr.getAttribute(CSS.Attribute.HEIGHT));
  }
  
  protected StyleSheet getStyleSheet()
  {
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    return localHTMLDocument.getStyleSheet();
  }
  
  private void constrainSize(int paramInt, SizeRequirements paramSizeRequirements1, SizeRequirements paramSizeRequirements2)
  {
    if (minimum > minimum)
    {
      minimum = (preferred = minimum);
      maximum = Math.max(maximum, maximum);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\BlockView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */