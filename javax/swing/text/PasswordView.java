package javax.swing.text;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.JPasswordField;
import sun.swing.SwingUtilities2;

public class PasswordView
  extends FieldView
{
  static char[] ONE = new char[1];
  
  public PasswordView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected int drawUnselectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    Container localContainer = getContainer();
    if ((localContainer instanceof JPasswordField))
    {
      JPasswordField localJPasswordField = (JPasswordField)localContainer;
      if (!localJPasswordField.echoCharIsSet()) {
        return super.drawUnselectedText(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      if (localJPasswordField.isEnabled()) {
        paramGraphics.setColor(localJPasswordField.getForeground());
      } else {
        paramGraphics.setColor(localJPasswordField.getDisabledTextColor());
      }
      char c = localJPasswordField.getEchoChar();
      int i = paramInt4 - paramInt3;
      for (int j = 0; j < i; j++) {
        paramInt1 = drawEchoCharacter(paramGraphics, paramInt1, paramInt2, c);
      }
    }
    return paramInt1;
  }
  
  protected int drawSelectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws BadLocationException
  {
    paramGraphics.setColor(selected);
    Container localContainer = getContainer();
    if ((localContainer instanceof JPasswordField))
    {
      JPasswordField localJPasswordField = (JPasswordField)localContainer;
      if (!localJPasswordField.echoCharIsSet()) {
        return super.drawSelectedText(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      char c = localJPasswordField.getEchoChar();
      int i = paramInt4 - paramInt3;
      for (int j = 0; j < i; j++) {
        paramInt1 = drawEchoCharacter(paramGraphics, paramInt1, paramInt2, c);
      }
    }
    return paramInt1;
  }
  
  protected int drawEchoCharacter(Graphics paramGraphics, int paramInt1, int paramInt2, char paramChar)
  {
    ONE[0] = paramChar;
    SwingUtilities2.drawChars(Utilities.getJComponent(this), paramGraphics, ONE, 0, 1, paramInt1, paramInt2);
    return paramInt1 + paramGraphics.getFontMetrics().charWidth(paramChar);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    Container localContainer = getContainer();
    if ((localContainer instanceof JPasswordField))
    {
      JPasswordField localJPasswordField = (JPasswordField)localContainer;
      if (!localJPasswordField.echoCharIsSet()) {
        return super.modelToView(paramInt, paramShape, paramBias);
      }
      char c = localJPasswordField.getEchoChar();
      FontMetrics localFontMetrics = localJPasswordField.getFontMetrics(localJPasswordField.getFont());
      Rectangle localRectangle = adjustAllocation(paramShape).getBounds();
      int i = (paramInt - getStartOffset()) * localFontMetrics.charWidth(c);
      x += i;
      width = 1;
      return localRectangle;
    }
    return null;
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    paramArrayOfBias[0] = Position.Bias.Forward;
    int i = 0;
    Container localContainer = getContainer();
    if ((localContainer instanceof JPasswordField))
    {
      JPasswordField localJPasswordField = (JPasswordField)localContainer;
      if (!localJPasswordField.echoCharIsSet()) {
        return super.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
      }
      char c = localJPasswordField.getEchoChar();
      int j = localJPasswordField.getFontMetrics(localJPasswordField.getFont()).charWidth(c);
      paramShape = adjustAllocation(paramShape);
      Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      i = j > 0 ? ((int)paramFloat1 - x) / j : Integer.MAX_VALUE;
      if (i < 0) {
        i = 0;
      } else if (i > getStartOffset() + getDocument().getLength()) {
        i = getDocument().getLength() - getStartOffset();
      }
    }
    return getStartOffset() + i;
  }
  
  public float getPreferredSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      Container localContainer = getContainer();
      if ((localContainer instanceof JPasswordField))
      {
        JPasswordField localJPasswordField = (JPasswordField)localContainer;
        if (localJPasswordField.echoCharIsSet())
        {
          char c = localJPasswordField.getEchoChar();
          FontMetrics localFontMetrics = localJPasswordField.getFontMetrics(localJPasswordField.getFont());
          Document localDocument = getDocument();
          return localFontMetrics.charWidth(c) * getDocument().getLength();
        }
      }
      break;
    }
    return super.getPreferredSpan(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\PasswordView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */