package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class MotifBorders
{
  public MotifBorders() {}
  
  public static void drawBezel(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
  {
    Color localColor = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    if (paramBoolean1)
    {
      if (paramBoolean2)
      {
        paramGraphics.setColor(paramColor4);
        paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
      }
      paramGraphics.setColor(paramColor1);
      paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
      paramGraphics.setColor(paramColor2);
      paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
      paramGraphics.drawLine(paramInt3 - 3, 2, paramInt3 - 3, paramInt4 - 4);
    }
    else
    {
      if (paramBoolean2)
      {
        paramGraphics.setColor(paramColor4);
        paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
        paramGraphics.setColor(paramColor2);
        paramGraphics.drawLine(1, 1, 1, paramInt4 - 3);
        paramGraphics.drawLine(2, 1, paramInt3 - 4, 1);
        paramGraphics.setColor(paramColor1);
        paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
        paramGraphics.drawLine(paramInt3 - 3, 1, paramInt3 - 3, paramInt4 - 4);
        paramGraphics.setColor(paramColor3);
        paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
        paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, 1);
      }
      else
      {
        paramGraphics.setColor(paramColor2);
        paramGraphics.drawLine(1, 1, 1, paramInt4 - 3);
        paramGraphics.drawLine(2, 1, paramInt3 - 4, 1);
        paramGraphics.setColor(paramColor1);
        paramGraphics.drawLine(2, paramInt4 - 3, paramInt3 - 3, paramInt4 - 3);
        paramGraphics.drawLine(paramInt3 - 3, 1, paramInt3 - 3, paramInt4 - 4);
        paramGraphics.setColor(paramColor3);
        paramGraphics.drawLine(1, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
        paramGraphics.drawLine(paramInt3 - 2, paramInt4 - 2, paramInt3 - 2, 0);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    paramGraphics.setColor(localColor);
  }
  
  public static class BevelBorder
    extends AbstractBorder
    implements UIResource
  {
    private Color darkShadow = UIManager.getColor("controlShadow");
    private Color lightShadow = UIManager.getColor("controlLtHighlight");
    private boolean isRaised;
    
    public BevelBorder(boolean paramBoolean, Color paramColor1, Color paramColor2)
    {
      isRaised = paramBoolean;
      darkShadow = paramColor1;
      lightShadow = paramColor2;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.setColor(isRaised ? lightShadow : darkShadow);
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
      paramGraphics.drawLine(paramInt1, paramInt2 + paramInt4 - 1, paramInt1, paramInt2 + 1);
      paramGraphics.setColor(isRaised ? darkShadow : lightShadow);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
      paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + 1);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(1, 1, 1, 1);
      return paramInsets;
    }
    
    public boolean isOpaque(Component paramComponent)
    {
      return true;
    }
  }
  
  public static class ButtonBorder
    extends AbstractBorder
    implements UIResource
  {
    protected Color focus = UIManager.getColor("activeCaptionBorder");
    protected Color shadow = UIManager.getColor("Button.shadow");
    protected Color highlight = UIManager.getColor("Button.light");
    protected Color darkShadow;
    
    public ButtonBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      shadow = paramColor1;
      highlight = paramColor2;
      darkShadow = paramColor3;
      focus = paramColor4;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = 0;
      int j = 0;
      boolean bool1 = false;
      boolean bool2 = false;
      if ((paramComponent instanceof AbstractButton))
      {
        AbstractButton localAbstractButton = (AbstractButton)paramComponent;
        ButtonModel localButtonModel = localAbstractButton.getModel();
        i = (localButtonModel.isArmed()) && (localButtonModel.isPressed()) ? 1 : 0;
        j = ((localButtonModel.isArmed()) && (i != 0)) || ((localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus())) ? 1 : 0;
        if ((localAbstractButton instanceof JButton))
        {
          bool1 = ((JButton)localAbstractButton).isDefaultCapable();
          bool2 = ((JButton)localAbstractButton).isDefaultButton();
        }
      }
      int k = paramInt1 + 1;
      int m = paramInt2 + 1;
      int n = paramInt1 + paramInt3 - 2;
      int i1 = paramInt2 + paramInt4 - 2;
      if (bool1)
      {
        if (bool2)
        {
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 3, paramInt1 + 3, paramInt2 + paramInt4 - 4);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 3, paramInt1 + paramInt3 - 4, paramInt2 + 3);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(paramInt1 + 4, paramInt2 + paramInt4 - 4, paramInt1 + paramInt3 - 4, paramInt2 + paramInt4 - 4);
          paramGraphics.drawLine(paramInt1 + paramInt3 - 4, paramInt2 + 3, paramInt1 + paramInt3 - 4, paramInt2 + paramInt4 - 4);
        }
        k += 6;
        m += 6;
        n -= 6;
        i1 -= 6;
      }
      if (j != 0)
      {
        paramGraphics.setColor(focus);
        if (bool2) {
          paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
        } else {
          paramGraphics.drawRect(k - 1, m - 1, n - k + 2, i1 - m + 2);
        }
      }
      paramGraphics.setColor(i != 0 ? shadow : highlight);
      paramGraphics.drawLine(k, m, n, m);
      paramGraphics.drawLine(k, m, k, i1);
      paramGraphics.setColor(i != 0 ? highlight : shadow);
      paramGraphics.drawLine(n, m + 1, n, i1);
      paramGraphics.drawLine(k + 1, i1, n, i1);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      int i = ((paramComponent instanceof JButton)) && (((JButton)paramComponent).isDefaultCapable()) ? 8 : 2;
      paramInsets.set(i, i, i, i);
      return paramInsets;
    }
  }
  
  public static class FocusBorder
    extends AbstractBorder
    implements UIResource
  {
    private Color focus;
    private Color control;
    
    public FocusBorder(Color paramColor1, Color paramColor2)
    {
      control = paramColor1;
      focus = paramColor2;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramComponent.hasFocus())
      {
        paramGraphics.setColor(focus);
        paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
      }
      else
      {
        paramGraphics.setColor(control);
        paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(1, 1, 1, 1);
      return paramInsets;
    }
  }
  
  public static class FrameBorder
    extends AbstractBorder
    implements UIResource
  {
    JComponent jcomp;
    Color frameHighlight;
    Color frameColor;
    Color frameShadow;
    public static final int BORDER_SIZE = 5;
    
    public FrameBorder(JComponent paramJComponent)
    {
      jcomp = paramJComponent;
    }
    
    public void setComponent(JComponent paramJComponent)
    {
      jcomp = paramJComponent;
    }
    
    public JComponent component()
    {
      return jcomp;
    }
    
    protected Color getFrameHighlight()
    {
      return frameHighlight;
    }
    
    protected Color getFrameColor()
    {
      return frameColor;
    }
    
    protected Color getFrameShadow()
    {
      return frameShadow;
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(5, 5, 5, 5);
      return paramInsets;
    }
    
    protected boolean drawTopBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle = new Rectangle(paramInt1, paramInt2, paramInt3, 5);
      if (!paramGraphics.getClipBounds().intersects(localRectangle)) {
        return false;
      }
      int i = paramInt3 - 1;
      int j = 4;
      paramGraphics.setColor(frameColor);
      paramGraphics.drawLine(paramInt1, paramInt2 + 2, i - 2, paramInt2 + 2);
      paramGraphics.drawLine(paramInt1, paramInt2 + 3, i - 2, paramInt2 + 3);
      paramGraphics.drawLine(paramInt1, paramInt2 + 4, i - 2, paramInt2 + 4);
      paramGraphics.setColor(frameHighlight);
      paramGraphics.drawLine(paramInt1, paramInt2, i, paramInt2);
      paramGraphics.drawLine(paramInt1, paramInt2 + 1, i, paramInt2 + 1);
      paramGraphics.drawLine(paramInt1, paramInt2 + 2, paramInt1, paramInt2 + 4);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 2, paramInt1 + 1, paramInt2 + 4);
      paramGraphics.setColor(frameShadow);
      paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 4, i - 4, paramInt2 + 4);
      paramGraphics.drawLine(i, paramInt2 + 1, i, j);
      paramGraphics.drawLine(i - 1, paramInt2 + 2, i - 1, j);
      return true;
    }
    
    protected boolean drawLeftBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle = new Rectangle(0, 0, getBorderInsetsleft, paramInt4);
      if (!paramGraphics.getClipBounds().intersects(localRectangle)) {
        return false;
      }
      int i = 5;
      paramGraphics.setColor(frameHighlight);
      paramGraphics.drawLine(paramInt1, i, paramInt1, paramInt4 - 1);
      paramGraphics.drawLine(paramInt1 + 1, i, paramInt1 + 1, paramInt4 - 2);
      paramGraphics.setColor(frameColor);
      paramGraphics.fillRect(paramInt1 + 2, i, paramInt1 + 2, paramInt4 - 3);
      paramGraphics.setColor(frameShadow);
      paramGraphics.drawLine(paramInt1 + 4, i, paramInt1 + 4, paramInt4 - 5);
      return true;
    }
    
    protected boolean drawRightBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle = new Rectangle(paramInt3 - getBorderInsetsright, 0, getBorderInsetsright, paramInt4);
      if (!paramGraphics.getClipBounds().intersects(localRectangle)) {
        return false;
      }
      int i = paramInt3 - getBorderInsetsright;
      int j = 5;
      paramGraphics.setColor(frameColor);
      paramGraphics.fillRect(i + 1, j, 2, paramInt4 - 1);
      paramGraphics.setColor(frameShadow);
      paramGraphics.fillRect(i + 3, j, 2, paramInt4 - 1);
      paramGraphics.setColor(frameHighlight);
      paramGraphics.drawLine(i, j, i, paramInt4 - 1);
      return true;
    }
    
    protected boolean drawBottomBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle = new Rectangle(0, paramInt4 - getBorderInsetsbottom, paramInt3, getBorderInsetsbottom);
      if (!paramGraphics.getClipBounds().intersects(localRectangle)) {
        return false;
      }
      int i = paramInt4 - getBorderInsetsbottom;
      paramGraphics.setColor(frameShadow);
      paramGraphics.drawLine(paramInt1 + 1, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(paramInt1 + 2, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
      paramGraphics.setColor(frameColor);
      paramGraphics.fillRect(paramInt1 + 2, i + 1, paramInt3 - 4, 2);
      paramGraphics.setColor(frameHighlight);
      paramGraphics.drawLine(paramInt1 + 5, i, paramInt3 - 5, i);
      return true;
    }
    
    protected boolean isActiveFrame()
    {
      return jcomp.hasFocus();
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (isActiveFrame()) {
        frameColor = UIManager.getColor("activeCaptionBorder");
      } else {
        frameColor = UIManager.getColor("inactiveCaptionBorder");
      }
      frameHighlight = frameColor.brighter();
      frameShadow = frameColor.darker().darker();
      drawTopBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      drawLeftBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      drawRightBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      drawBottomBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public static class InternalFrameBorder
    extends MotifBorders.FrameBorder
  {
    JInternalFrame frame;
    public static final int CORNER_SIZE = 24;
    
    public InternalFrameBorder(JInternalFrame paramJInternalFrame)
    {
      super();
      frame = paramJInternalFrame;
    }
    
    public void setFrame(JInternalFrame paramJInternalFrame)
    {
      frame = paramJInternalFrame;
    }
    
    public JInternalFrame frame()
    {
      return frame;
    }
    
    public int resizePartWidth()
    {
      if (!frame.isResizable()) {
        return 0;
      }
      return 5;
    }
    
    protected boolean drawTopBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((super.drawTopBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4)) && (frame.isResizable()))
      {
        paramGraphics.setColor(getFrameShadow());
        paramGraphics.drawLine(23, paramInt2 + 1, 23, paramInt2 + 4);
        paramGraphics.drawLine(paramInt3 - 24 - 1, paramInt2 + 1, paramInt3 - 24 - 1, paramInt2 + 4);
        paramGraphics.setColor(getFrameHighlight());
        paramGraphics.drawLine(24, paramInt2, 24, paramInt2 + 4);
        paramGraphics.drawLine(paramInt3 - 24, paramInt2, paramInt3 - 24, paramInt2 + 4);
        return true;
      }
      return false;
    }
    
    protected boolean drawLeftBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((super.drawLeftBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4)) && (frame.isResizable()))
      {
        paramGraphics.setColor(getFrameHighlight());
        int i = paramInt2 + 24;
        paramGraphics.drawLine(paramInt1, i, paramInt1 + 4, i);
        int j = paramInt4 - 24;
        paramGraphics.drawLine(paramInt1 + 1, j, paramInt1 + 5, j);
        paramGraphics.setColor(getFrameShadow());
        paramGraphics.drawLine(paramInt1 + 1, i - 1, paramInt1 + 5, i - 1);
        paramGraphics.drawLine(paramInt1 + 1, j - 1, paramInt1 + 5, j - 1);
        return true;
      }
      return false;
    }
    
    protected boolean drawRightBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((super.drawRightBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4)) && (frame.isResizable()))
      {
        int i = paramInt3 - getBorderInsetsright;
        paramGraphics.setColor(getFrameHighlight());
        int j = paramInt2 + 24;
        paramGraphics.drawLine(i, j, paramInt3 - 2, j);
        int k = paramInt4 - 24;
        paramGraphics.drawLine(i + 1, k, i + 3, k);
        paramGraphics.setColor(getFrameShadow());
        paramGraphics.drawLine(i + 1, j - 1, paramInt3 - 2, j - 1);
        paramGraphics.drawLine(i + 1, k - 1, i + 3, k - 1);
        return true;
      }
      return false;
    }
    
    protected boolean drawBottomBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((super.drawBottomBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4)) && (frame.isResizable()))
      {
        int i = paramInt4 - getBorderInsetsbottom;
        paramGraphics.setColor(getFrameShadow());
        paramGraphics.drawLine(23, i + 1, 23, paramInt4 - 1);
        paramGraphics.drawLine(paramInt3 - 24, i + 1, paramInt3 - 24, paramInt4 - 1);
        paramGraphics.setColor(getFrameHighlight());
        paramGraphics.drawLine(24, i, 24, paramInt4 - 2);
        paramGraphics.drawLine(paramInt3 - 24 + 1, i, paramInt3 - 24 + 1, paramInt4 - 2);
        return true;
      }
      return false;
    }
    
    protected boolean isActiveFrame()
    {
      return frame.isSelected();
    }
  }
  
  public static class MenuBarBorder
    extends MotifBorders.ButtonBorder
  {
    public MenuBarBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      super(paramColor2, paramColor3, paramColor4);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JMenuBar)) {
        return;
      }
      JMenuBar localJMenuBar = (JMenuBar)paramComponent;
      if (localJMenuBar.isBorderPainted() == true)
      {
        Dimension localDimension = localJMenuBar.getSize();
        MotifBorders.drawBezel(paramGraphics, paramInt1, paramInt2, width, height, false, false, shadow, highlight, darkShadow, focus);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(6, 6, 6, 6);
      return paramInsets;
    }
  }
  
  public static class MotifPopupMenuBorder
    extends AbstractBorder
    implements UIResource
  {
    protected Font font;
    protected Color background;
    protected Color foreground;
    protected Color shadowColor;
    protected Color highlightColor;
    protected static final int TEXT_SPACING = 2;
    protected static final int GROOVE_HEIGHT = 2;
    
    public MotifPopupMenuBorder(Font paramFont, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      font = paramFont;
      background = paramColor1;
      foreground = paramColor2;
      shadowColor = paramColor3;
      highlightColor = paramColor4;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JPopupMenu)) {
        return;
      }
      Font localFont = paramGraphics.getFont();
      Color localColor = paramGraphics.getColor();
      JPopupMenu localJPopupMenu = (JPopupMenu)paramComponent;
      String str = localJPopupMenu.getLabel();
      if (str == null) {
        return;
      }
      paramGraphics.setFont(font);
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localJPopupMenu, paramGraphics, font);
      int i = localFontMetrics.getHeight();
      int j = localFontMetrics.getDescent();
      int k = localFontMetrics.getAscent();
      Point localPoint = new Point();
      int m = SwingUtilities2.stringWidth(localJPopupMenu, localFontMetrics, str);
      y = (paramInt2 + k + 2);
      x = (paramInt1 + (paramInt3 - m) / 2);
      paramGraphics.setColor(background);
      paramGraphics.fillRect(x - 2, y - (i - j), m + 4, i - j);
      paramGraphics.setColor(foreground);
      SwingUtilities2.drawString(localJPopupMenu, paramGraphics, str, x, y);
      MotifGraphicsUtils.drawGroove(paramGraphics, paramInt1, y + 2, paramInt3, 2, shadowColor, highlightColor);
      paramGraphics.setFont(localFont);
      paramGraphics.setColor(localColor);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      if (!(paramComponent instanceof JPopupMenu)) {
        return paramInsets;
      }
      int i = 0;
      int j = 16;
      String str = ((JPopupMenu)paramComponent).getLabel();
      if (str == null)
      {
        left = (top = right = bottom = 0);
        return paramInsets;
      }
      FontMetrics localFontMetrics = paramComponent.getFontMetrics(font);
      if (localFontMetrics != null)
      {
        i = localFontMetrics.getDescent();
        j = localFontMetrics.getAscent();
      }
      top += j + i + 2 + 2;
      return paramInsets;
    }
  }
  
  public static class ToggleButtonBorder
    extends MotifBorders.ButtonBorder
  {
    public ToggleButtonBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      super(paramColor2, paramColor3, paramColor4);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((paramComponent instanceof AbstractButton))
      {
        AbstractButton localAbstractButton = (AbstractButton)paramComponent;
        ButtonModel localButtonModel = localAbstractButton.getModel();
        if (((localButtonModel.isArmed()) && (localButtonModel.isPressed())) || (localButtonModel.isSelected())) {
          MotifBorders.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, (localButtonModel.isPressed()) || (localButtonModel.isSelected()), (localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus()), shadow, highlight, darkShadow, focus);
        } else {
          MotifBorders.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false, (localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus()), shadow, highlight, darkShadow, focus);
        }
      }
      else
      {
        MotifBorders.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false, false, shadow, highlight, darkShadow, focus);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 3, 3);
      return paramInsets;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */