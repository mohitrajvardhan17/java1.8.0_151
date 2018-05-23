package javax.swing.text.html;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

class LineView
  extends ParagraphView
{
  int tabBase;
  
  public LineView(Element paramElement)
  {
    super(paramElement);
  }
  
  public boolean isVisible()
  {
    return true;
  }
  
  public float getMinimumSpan(int paramInt)
  {
    return getPreferredSpan(paramInt);
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
    if (paramInt == 0) {
      return 0.0F;
    }
    return super.getAlignment(paramInt);
  }
  
  protected void layout(int paramInt1, int paramInt2)
  {
    super.layout(2147483646, paramInt2);
  }
  
  public float nextTabStop(float paramFloat, int paramInt)
  {
    if ((getTabSet() == null) && (StyleConstants.getAlignment(getAttributes()) == 0)) {
      return getPreTab(paramFloat, paramInt);
    }
    return super.nextTabStop(paramFloat, paramInt);
  }
  
  protected float getPreTab(float paramFloat, int paramInt)
  {
    Document localDocument = getDocument();
    View localView = getViewAtPosition(paramInt, null);
    if (((localDocument instanceof StyledDocument)) && (localView != null))
    {
      Font localFont = ((StyledDocument)localDocument).getFont(localView.getAttributes());
      Container localContainer = getContainer();
      FontMetrics localFontMetrics = localContainer != null ? localContainer.getFontMetrics(localFont) : Toolkit.getDefaultToolkit().getFontMetrics(localFont);
      int i = getCharactersPerTab() * localFontMetrics.charWidth('W');
      int j = (int)getTabBase();
      return (((int)paramFloat - j) / i + 1) * i + j;
    }
    return 10.0F + paramFloat;
  }
  
  protected int getCharactersPerTab()
  {
    return 8;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\LineView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */