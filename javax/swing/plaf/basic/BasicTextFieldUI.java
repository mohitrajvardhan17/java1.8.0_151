package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class BasicTextFieldUI
  extends BasicTextUI
{
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTextFieldUI();
  }
  
  public BasicTextFieldUI() {}
  
  protected String getPropertyPrefix()
  {
    return "TextField";
  }
  
  public View create(Element paramElement)
  {
    Document localDocument = paramElement.getDocument();
    Object localObject = localDocument.getProperty("i18n");
    if (Boolean.TRUE.equals(localObject))
    {
      String str = paramElement.getName();
      if (str != null)
      {
        if (str.equals("content")) {
          return new GlyphView(paramElement);
        }
        if (str.equals("paragraph")) {
          return new I18nFieldView(paramElement);
        }
      }
    }
    return new FieldView(paramElement);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    View localView1 = getRootView((JTextComponent)paramJComponent);
    if (localView1.getViewCount() > 0)
    {
      Insets localInsets = paramJComponent.getInsets();
      paramInt2 = paramInt2 - top - bottom;
      if (paramInt2 > 0)
      {
        int i = top;
        View localView2 = localView1.getView(0);
        int j = (int)localView2.getPreferredSpan(1);
        int k;
        if (paramInt2 != j)
        {
          k = paramInt2 - j;
          i += k / 2;
        }
        if ((localView2 instanceof I18nFieldView))
        {
          k = BasicHTML.getBaseline(localView2, paramInt1 - left - right, paramInt2);
          if (k < 0) {
            return -1;
          }
          i += k;
        }
        else
        {
          FontMetrics localFontMetrics = paramJComponent.getFontMetrics(paramJComponent.getFont());
          i += localFontMetrics.getAscent();
        }
        return i;
      }
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CENTER_OFFSET;
  }
  
  static class I18nFieldView
    extends ParagraphView
  {
    I18nFieldView(Element paramElement)
    {
      super();
    }
    
    public int getFlowSpan(int paramInt)
    {
      return Integer.MAX_VALUE;
    }
    
    protected void setJustification(int paramInt) {}
    
    static boolean isLeftToRight(Component paramComponent)
    {
      return paramComponent.getComponentOrientation().isLeftToRight();
    }
    
    Shape adjustAllocation(Shape paramShape)
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
            if (isLeftToRight(localContainer))
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
      super.paint(paramGraphics, adjustAllocation(paramShape));
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
    
    public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape)
      throws BadLocationException
    {
      return super.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, adjustAllocation(paramShape));
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */