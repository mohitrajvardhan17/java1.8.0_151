package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;

public class FieldView
  extends PlainView
{
  public FieldView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected FontMetrics getFontMetrics()
  {
    Container localContainer = getContainer();
    return localContainer.getFontMetrics(localContainer.getFont());
  }
  
  protected Shape adjustAllocation(Shape paramShape)
  {
    if (paramShape != null)
    {
      Rectangle localRectangle = paramShape.getBounds();
      int i = (int)getPreferredSpan(1);
      int j = (int)getPreferredSpan(0);
      if (height != i)
      {
        int k = height - i;
        y += k / 2;
        height -= k;
      }
      Container localContainer = getContainer();
      if ((localContainer instanceof JTextField))
      {
        JTextField localJTextField = (JTextField)localContainer;
        BoundedRangeModel localBoundedRangeModel = localJTextField.getHorizontalVisibility();
        int m = Math.max(j, width);
        int n = localBoundedRangeModel.getValue();
        int i1 = Math.min(m, width - 1);
        if (n + i1 > m) {
          n = m - i1;
        }
        localBoundedRangeModel.setRangeProperties(n, i1, localBoundedRangeModel.getMinimum(), m, false);
        if (j < width)
        {
          int i2 = width - 1 - j;
          int i3 = ((JTextField)localContainer).getHorizontalAlignment();
          if (Utilities.isLeftToRight(localContainer))
          {
            if (i3 == 10) {
              i3 = 2;
            } else if (i3 == 11) {
              i3 = 4;
            }
          }
          else if (i3 == 10) {
            i3 = 4;
          } else if (i3 == 11) {
            i3 = 2;
          }
          switch (i3)
          {
          case 0: 
            x += i2 / 2;
            width -= i2;
            break;
          case 4: 
            x += i2;
            width -= i2;
          }
        }
        else
        {
          width = j;
          x -= localBoundedRangeModel.getValue();
        }
      }
      return localRectangle;
    }
    return null;
  }
  
  void updateVisibilityModel()
  {
    Container localContainer = getContainer();
    if ((localContainer instanceof JTextField))
    {
      JTextField localJTextField = (JTextField)localContainer;
      BoundedRangeModel localBoundedRangeModel = localJTextField.getHorizontalVisibility();
      int i = (int)getPreferredSpan(0);
      int j = localBoundedRangeModel.getExtent();
      int k = Math.max(i, j);
      j = j == 0 ? k : j;
      int m = k - j;
      int n = localBoundedRangeModel.getValue();
      if (n + j > k) {
        n = k - j;
      }
      m = Math.max(0, Math.min(m, n));
      localBoundedRangeModel.setRangeProperties(m, j, 0, k, false);
    }
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    paramGraphics.clipRect(x, y, width, height);
    super.paint(paramGraphics, paramShape);
  }
  
  Shape adjustPaintRegion(Shape paramShape)
  {
    return adjustAllocation(paramShape);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      Segment localSegment = SegmentCache.getSharedSegment();
      Document localDocument = getDocument();
      int i;
      try
      {
        FontMetrics localFontMetrics = getFontMetrics();
        localDocument.getText(0, localDocument.getLength(), localSegment);
        i = Utilities.getTabbedTextWidth(localSegment, localFontMetrics, 0, this, 0);
        if (count > 0)
        {
          Container localContainer = getContainer();
          firstLineOffset = SwingUtilities2.getLeftSideBearing((localContainer instanceof JComponent) ? (JComponent)localContainer : null, localFontMetrics, array[offset]);
          firstLineOffset = Math.max(0, -firstLineOffset);
        }
        else
        {
          firstLineOffset = 0;
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        i = 0;
      }
      SegmentCache.releaseSharedSegment(localSegment);
      return i + firstLineOffset;
    }
    return super.getPreferredSpan(paramInt);
  }
  
  public int getResizeWeight(int paramInt)
  {
    if (paramInt == 0) {
      return 1;
    }
    return 0;
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    return super.modelToView(paramInt, adjustAllocation(paramShape), paramBias);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    return super.viewToModel(paramFloat1, paramFloat2, adjustAllocation(paramShape), paramArrayOfBias);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.insertUpdate(paramDocumentEvent, adjustAllocation(paramShape), paramViewFactory);
    updateVisibilityModel();
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.removeUpdate(paramDocumentEvent, adjustAllocation(paramShape), paramViewFactory);
    updateVisibilityModel();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\FieldView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */