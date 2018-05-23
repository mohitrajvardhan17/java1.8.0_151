package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class MotifGraphicsUtils
  implements SwingConstants
{
  private static final String MAX_ACC_WIDTH = "maxAccWidth";
  
  public MotifGraphicsUtils() {}
  
  static void drawPoint(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt2);
  }
  
  public static void drawGroove(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor1, Color paramColor2)
  {
    Color localColor = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(paramColor1);
    paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.setColor(paramColor2);
    paramGraphics.drawLine(1, paramInt4 - 3, 1, 1);
    paramGraphics.drawLine(1, 1, paramInt3 - 3, 1);
    paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.drawLine(paramInt3 - 1, paramInt4 - 1, paramInt3 - 1, 0);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(localColor);
  }
  
  public static void drawStringInRect(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    drawStringInRect(null, paramGraphics, paramString, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  static void drawStringInRect(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramGraphics.getFont() == null) {
      return;
    }
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics);
    if (localFontMetrics == null) {
      return;
    }
    int i;
    int j;
    if (paramInt5 == 0)
    {
      i = SwingUtilities2.stringWidth(paramJComponent, localFontMetrics, paramString);
      if (i > paramInt3) {
        i = paramInt3;
      }
      j = paramInt1 + (paramInt3 - i) / 2;
    }
    else if (paramInt5 == 4)
    {
      i = SwingUtilities2.stringWidth(paramJComponent, localFontMetrics, paramString);
      if (i > paramInt3) {
        i = paramInt3;
      }
      j = paramInt1 + paramInt3 - i;
    }
    else
    {
      j = paramInt1;
    }
    int m = (paramInt4 - localFontMetrics.getAscent() - localFontMetrics.getDescent()) / 2;
    if (m < 0) {
      m = 0;
    }
    int k = paramInt2 + paramInt4 - m - localFontMetrics.getDescent();
    SwingUtilities2.drawString(paramJComponent, paramGraphics, paramString, j, k);
  }
  
  public static void paintMenuItem(Graphics paramGraphics, JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, Color paramColor1, Color paramColor2, int paramInt)
  {
    JMenuItem localJMenuItem = (JMenuItem)paramJComponent;
    ButtonModel localButtonModel = localJMenuItem.getModel();
    Dimension localDimension = localJMenuItem.getSize();
    Insets localInsets = paramJComponent.getInsets();
    Rectangle localRectangle1 = new Rectangle(localDimension);
    x += left;
    y += top;
    width -= right + x;
    height -= bottom + y;
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = new Rectangle();
    Rectangle localRectangle4 = new Rectangle();
    Rectangle localRectangle5 = new Rectangle();
    Rectangle localRectangle6 = new Rectangle();
    Font localFont1 = paramGraphics.getFont();
    Font localFont2 = paramJComponent.getFont();
    paramGraphics.setFont(localFont2);
    FontMetrics localFontMetrics1 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, localFont2);
    FontMetrics localFontMetrics2 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, UIManager.getFont("MenuItem.acceleratorFont"));
    if (paramJComponent.isOpaque())
    {
      if ((localButtonModel.isArmed()) || (((paramJComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(paramColor1);
      } else {
        paramGraphics.setColor(paramJComponent.getBackground());
      }
      paramGraphics.fillRect(0, 0, width, height);
    }
    KeyStroke localKeyStroke = localJMenuItem.getAccelerator();
    String str1 = "";
    if (localKeyStroke != null)
    {
      int i = localKeyStroke.getModifiers();
      if (i > 0)
      {
        str1 = KeyEvent.getKeyModifiersText(i);
        str1 = str1 + "+";
      }
      str1 = str1 + KeyEvent.getKeyText(localKeyStroke.getKeyCode());
    }
    String str2 = layoutMenuItem(paramJComponent, localFontMetrics1, localJMenuItem.getText(), localFontMetrics2, str1, localJMenuItem.getIcon(), paramIcon1, paramIcon2, localJMenuItem.getVerticalAlignment(), localJMenuItem.getHorizontalAlignment(), localJMenuItem.getVerticalTextPosition(), localJMenuItem.getHorizontalTextPosition(), localRectangle1, localRectangle2, localRectangle3, localRectangle4, localRectangle5, localRectangle6, localJMenuItem.getText() == null ? 0 : paramInt, paramInt);
    Color localColor = paramGraphics.getColor();
    if (paramIcon1 != null)
    {
      if ((localButtonModel.isArmed()) || (((paramJComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(paramColor2);
      }
      paramIcon1.paintIcon(paramJComponent, paramGraphics, x, y);
      paramGraphics.setColor(localColor);
    }
    Object localObject;
    if (localJMenuItem.getIcon() != null)
    {
      if (!localButtonModel.isEnabled())
      {
        localObject = localJMenuItem.getDisabledIcon();
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localObject = localJMenuItem.getPressedIcon();
        if (localObject == null) {
          localObject = localJMenuItem.getIcon();
        }
      }
      else
      {
        localObject = localJMenuItem.getIcon();
      }
      if (localObject != null) {
        ((Icon)localObject).paintIcon(paramJComponent, paramGraphics, x, y);
      }
    }
    if ((str2 != null) && (!str2.equals("")))
    {
      localObject = (View)paramJComponent.getClientProperty("html");
      if (localObject != null)
      {
        ((View)localObject).paint(paramGraphics, localRectangle3);
      }
      else
      {
        int k = localJMenuItem.getDisplayedMnemonicIndex();
        if (!localButtonModel.isEnabled())
        {
          paramGraphics.setColor(localJMenuItem.getBackground().brighter());
          SwingUtilities2.drawStringUnderlineCharAt(localJMenuItem, paramGraphics, str2, k, x, y + localFontMetrics2.getAscent());
          paramGraphics.setColor(localJMenuItem.getBackground().darker());
          SwingUtilities2.drawStringUnderlineCharAt(localJMenuItem, paramGraphics, str2, k, x - 1, y + localFontMetrics2.getAscent() - 1);
        }
        else
        {
          if ((localButtonModel.isArmed()) || (((paramJComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
            paramGraphics.setColor(paramColor2);
          } else {
            paramGraphics.setColor(localJMenuItem.getForeground());
          }
          SwingUtilities2.drawStringUnderlineCharAt(localJMenuItem, paramGraphics, str2, k, x, y + localFontMetrics1.getAscent());
        }
      }
    }
    if ((str1 != null) && (!str1.equals("")))
    {
      int j = 0;
      Container localContainer = localJMenuItem.getParent();
      if ((localContainer != null) && ((localContainer instanceof JComponent)))
      {
        JComponent localJComponent = (JComponent)localContainer;
        Integer localInteger = (Integer)localJComponent.getClientProperty("maxAccWidth");
        int m = localInteger != null ? localInteger.intValue() : width;
        j = m - width;
      }
      paramGraphics.setFont(UIManager.getFont("MenuItem.acceleratorFont"));
      if (!localButtonModel.isEnabled())
      {
        paramGraphics.setColor(localJMenuItem.getBackground().brighter());
        SwingUtilities2.drawString(paramJComponent, paramGraphics, str1, x - j, y + localFontMetrics1.getAscent());
        paramGraphics.setColor(localJMenuItem.getBackground().darker());
        SwingUtilities2.drawString(paramJComponent, paramGraphics, str1, x - j - 1, y + localFontMetrics1.getAscent() - 1);
      }
      else
      {
        if ((localButtonModel.isArmed()) || (((paramJComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
          paramGraphics.setColor(paramColor2);
        } else {
          paramGraphics.setColor(localJMenuItem.getForeground());
        }
        SwingUtilities2.drawString(paramJComponent, paramGraphics, str1, x - j, y + localFontMetrics2.getAscent());
      }
    }
    if (paramIcon2 != null)
    {
      if ((localButtonModel.isArmed()) || (((paramJComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(paramColor2);
      }
      if (!(localJMenuItem.getParent() instanceof JMenuBar)) {
        paramIcon2.paintIcon(paramJComponent, paramGraphics, x, y);
      }
    }
    paramGraphics.setColor(localColor);
    paramGraphics.setFont(localFont1);
  }
  
  private static String layoutMenuItem(JComponent paramJComponent, FontMetrics paramFontMetrics1, String paramString1, FontMetrics paramFontMetrics2, String paramString2, Icon paramIcon1, Icon paramIcon2, Icon paramIcon3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, Rectangle paramRectangle4, Rectangle paramRectangle5, Rectangle paramRectangle6, int paramInt5, int paramInt6)
  {
    SwingUtilities.layoutCompoundLabel(paramJComponent, paramFontMetrics1, paramString1, paramIcon1, paramInt1, paramInt2, paramInt3, paramInt4, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
    if ((paramString2 == null) || (paramString2.equals("")))
    {
      width = (height = 0);
      paramString2 = "";
    }
    else
    {
      width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics2, paramString2);
      height = paramFontMetrics2.getHeight();
    }
    if (paramIcon2 != null)
    {
      width = paramIcon2.getIconWidth();
      height = paramIcon2.getIconHeight();
    }
    else
    {
      width = (height = 0);
    }
    if (paramIcon3 != null)
    {
      width = paramIcon3.getIconWidth();
      height = paramIcon3.getIconHeight();
    }
    else
    {
      width = (height = 0);
    }
    Rectangle localRectangle = paramRectangle2.union(paramRectangle3);
    if (isLeftToRight(paramJComponent))
    {
      x += width + paramInt6;
      x += width + paramInt6;
      x = (x + width - width - paramInt6 - width);
      x = x;
      x = (x + width - paramInt6 - width);
    }
    else
    {
      x -= width + paramInt6;
      x -= width + paramInt6;
      x = (x + width + paramInt6);
      x = (x + width - width);
      x += paramInt6;
    }
    y = (y + height / 2 - height / 2);
    y = (y + height / 2 - height / 2);
    y = (y + height / 2 - height / 2);
    return paramString1;
  }
  
  private static void drawMenuBezel(Graphics paramGraphics, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.setColor(paramColor);
    paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
    paramGraphics.setColor(paramColor.brighter().brighter());
    paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
    paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 2, paramInt1 + paramInt3 - 1, paramInt2 + 1);
    paramGraphics.setColor(paramColor.darker().darker());
    paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + paramInt3 - 2, paramInt2);
    paramGraphics.drawLine(paramInt1, paramInt2 + 1, paramInt1, paramInt2 + paramInt4 - 2);
  }
  
  static boolean isLeftToRight(Component paramComponent)
  {
    return paramComponent.getComponentOrientation().isLeftToRight();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifGraphicsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */