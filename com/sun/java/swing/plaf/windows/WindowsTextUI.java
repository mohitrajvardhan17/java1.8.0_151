package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter.LayerPainter;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public abstract class WindowsTextUI
  extends BasicTextUI
{
  static LayeredHighlighter.LayerPainter WindowsPainter = new WindowsHighlightPainter(null);
  
  public WindowsTextUI() {}
  
  protected Caret createCaret()
  {
    return new WindowsCaret();
  }
  
  static class WindowsCaret
    extends DefaultCaret
    implements UIResource
  {
    WindowsCaret() {}
    
    protected Highlighter.HighlightPainter getSelectionPainter()
    {
      return WindowsTextUI.WindowsPainter;
    }
  }
  
  static class WindowsHighlightPainter
    extends DefaultHighlighter.DefaultHighlightPainter
  {
    WindowsHighlightPainter(Color paramColor)
    {
      super();
    }
    
    public void paint(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent)
    {
      Rectangle localRectangle1 = paramShape.getBounds();
      try
      {
        TextUI localTextUI = paramJTextComponent.getUI();
        Rectangle localRectangle2 = localTextUI.modelToView(paramJTextComponent, paramInt1);
        Rectangle localRectangle3 = localTextUI.modelToView(paramJTextComponent, paramInt2);
        Color localColor = getColor();
        if (localColor == null) {
          paramGraphics.setColor(paramJTextComponent.getSelectionColor());
        } else {
          paramGraphics.setColor(localColor);
        }
        int i = 0;
        int j = 0;
        if (paramJTextComponent.isEditable())
        {
          int k = paramJTextComponent.getCaretPosition();
          i = paramInt1 == k ? 1 : 0;
          j = paramInt2 == k ? 1 : 0;
        }
        if (y == y)
        {
          Rectangle localRectangle4 = localRectangle2.union(localRectangle3);
          if (width > 0) {
            if (i != 0)
            {
              x += 1;
              width -= 1;
            }
            else if (j != 0)
            {
              width -= 1;
            }
          }
          paramGraphics.fillRect(x, y, width, height);
        }
        else
        {
          int m = x + width - x;
          if ((i != 0) && (m > 0))
          {
            x += 1;
            m--;
          }
          paramGraphics.fillRect(x, y, m, height);
          if (y + height != y) {
            paramGraphics.fillRect(x, y + height, width, y - (y + height));
          }
          if ((j != 0) && (x > x)) {
            x -= 1;
          }
          paramGraphics.fillRect(x, y, x - x, height);
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
    
    public Shape paintLayer(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView)
    {
      Color localColor = getColor();
      if (localColor == null) {
        paramGraphics.setColor(paramJTextComponent.getSelectionColor());
      } else {
        paramGraphics.setColor(localColor);
      }
      int i = 0;
      int j = 0;
      if (paramJTextComponent.isEditable())
      {
        int k = paramJTextComponent.getCaretPosition();
        i = paramInt1 == k ? 1 : 0;
        j = paramInt2 == k ? 1 : 0;
      }
      Object localObject;
      if ((paramInt1 == paramView.getStartOffset()) && (paramInt2 == paramView.getEndOffset()))
      {
        if ((paramShape instanceof Rectangle)) {
          localObject = (Rectangle)paramShape;
        } else {
          localObject = paramShape.getBounds();
        }
        if ((i != 0) && (width > 0)) {
          paramGraphics.fillRect(x + 1, y, width - 1, height);
        } else if ((j != 0) && (width > 0)) {
          paramGraphics.fillRect(x, y, width - 1, height);
        } else {
          paramGraphics.fillRect(x, y, width, height);
        }
        return (Shape)localObject;
      }
      try
      {
        localObject = paramView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, paramShape);
        Rectangle localRectangle = (localObject instanceof Rectangle) ? (Rectangle)localObject : ((Shape)localObject).getBounds();
        if ((i != 0) && (width > 0)) {
          paramGraphics.fillRect(x + 1, y, width - 1, height);
        } else if ((j != 0) && (width > 0)) {
          paramGraphics.fillRect(x, y, width - 1, height);
        } else {
          paramGraphics.fillRect(x, y, width, height);
        }
        return localRectangle;
      }
      catch (BadLocationException localBadLocationException) {}
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */