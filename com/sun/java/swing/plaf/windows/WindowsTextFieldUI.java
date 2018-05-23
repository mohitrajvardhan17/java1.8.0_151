package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.Position.Bias;

public class WindowsTextFieldUI
  extends BasicTextFieldUI
{
  public WindowsTextFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTextFieldUI();
  }
  
  protected void paintBackground(Graphics paramGraphics)
  {
    super.paintBackground(paramGraphics);
  }
  
  protected Caret createCaret()
  {
    return new WindowsFieldCaret();
  }
  
  static class WindowsFieldCaret
    extends DefaultCaret
    implements UIResource
  {
    public WindowsFieldCaret() {}
    
    protected void adjustVisibility(Rectangle paramRectangle)
    {
      SwingUtilities.invokeLater(new SafeScroller(paramRectangle));
    }
    
    protected Highlighter.HighlightPainter getSelectionPainter()
    {
      return WindowsTextUI.WindowsPainter;
    }
    
    private class SafeScroller
      implements Runnable
    {
      private Rectangle r;
      
      SafeScroller(Rectangle paramRectangle)
      {
        r = paramRectangle;
      }
      
      public void run()
      {
        JTextField localJTextField = (JTextField)getComponent();
        if (localJTextField != null)
        {
          TextUI localTextUI = localJTextField.getUI();
          int i = getDot();
          Position.Bias localBias = Position.Bias.Forward;
          Rectangle localRectangle1 = null;
          try
          {
            localRectangle1 = localTextUI.modelToView(localJTextField, i, localBias);
          }
          catch (BadLocationException localBadLocationException1) {}
          Insets localInsets = localJTextField.getInsets();
          BoundedRangeModel localBoundedRangeModel = localJTextField.getHorizontalVisibility();
          int j = r.x + localBoundedRangeModel.getValue() - left;
          int k = localBoundedRangeModel.getExtent() / 4;
          if (r.x < left) {
            localBoundedRangeModel.setValue(j - k);
          } else if (r.x + r.width > left + localBoundedRangeModel.getExtent()) {
            localBoundedRangeModel.setValue(j - 3 * k);
          }
          if (localRectangle1 != null) {
            try
            {
              Rectangle localRectangle2 = localTextUI.modelToView(localJTextField, i, localBias);
              if ((localRectangle2 != null) && (!localRectangle2.equals(localRectangle1))) {
                damage(localRectangle2);
              }
            }
            catch (BadLocationException localBadLocationException2) {}
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */