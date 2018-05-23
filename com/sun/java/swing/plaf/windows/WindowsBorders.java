package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;

public class WindowsBorders
{
  public WindowsBorders() {}
  
  public static Border getProgressBarBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(new ProgressBarBorder(localUIDefaults.getColor("ProgressBar.shadow"), localUIDefaults.getColor("ProgressBar.highlight")), new EmptyBorder(1, 1, 1, 1));
    return localCompoundBorderUIResource;
  }
  
  public static Border getToolBarBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    ToolBarBorder localToolBarBorder = new ToolBarBorder(localUIDefaults.getColor("ToolBar.shadow"), localUIDefaults.getColor("ToolBar.highlight"));
    return localToolBarBorder;
  }
  
  public static Border getFocusCellHighlightBorder()
  {
    return new ComplementDashedBorder();
  }
  
  public static Border getTableHeaderBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(new BasicBorders.ButtonBorder(localUIDefaults.getColor("Table.shadow"), localUIDefaults.getColor("Table.darkShadow"), localUIDefaults.getColor("Table.light"), localUIDefaults.getColor("Table.highlight")), new BasicBorders.MarginBorder());
    return localCompoundBorderUIResource;
  }
  
  public static Border getInternalFrameBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(BorderFactory.createBevelBorder(0, localUIDefaults.getColor("InternalFrame.borderColor"), localUIDefaults.getColor("InternalFrame.borderHighlight"), localUIDefaults.getColor("InternalFrame.borderDarkShadow"), localUIDefaults.getColor("InternalFrame.borderShadow")), new InternalFrameLineBorder(localUIDefaults.getColor("InternalFrame.activeBorderColor"), localUIDefaults.getColor("InternalFrame.inactiveBorderColor"), localUIDefaults.getInt("InternalFrame.borderWidth")));
    return localCompoundBorderUIResource;
  }
  
  static class ComplementDashedBorder
    extends LineBorder
    implements UIResource
  {
    private Color origColor;
    private Color paintColor;
    
    public ComplementDashedBorder()
    {
      super();
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Color localColor = paramComponent.getBackground();
      if (origColor != localColor)
      {
        origColor = localColor;
        paintColor = new Color(origColor.getRGB() ^ 0xFFFFFFFF);
      }
      paramGraphics.setColor(paintColor);
      BasicGraphicsUtils.drawDashedRect(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public static class DashedBorder
    extends LineBorder
    implements UIResource
  {
    public DashedBorder(Color paramColor)
    {
      super();
    }
    
    public DashedBorder(Color paramColor, int paramInt)
    {
      super(paramInt);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Color localColor = paramGraphics.getColor();
      paramGraphics.setColor(lineColor);
      for (int i = 0; i < thickness; i++) {
        BasicGraphicsUtils.drawDashedRect(paramGraphics, paramInt1 + i, paramInt2 + i, paramInt3 - i - i, paramInt4 - i - i);
      }
      paramGraphics.setColor(localColor);
    }
  }
  
  public static class InternalFrameLineBorder
    extends LineBorder
    implements UIResource
  {
    protected Color activeColor;
    protected Color inactiveColor;
    
    public InternalFrameLineBorder(Color paramColor1, Color paramColor2, int paramInt)
    {
      super(paramInt);
      activeColor = paramColor1;
      inactiveColor = paramColor2;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      JInternalFrame localJInternalFrame = null;
      if ((paramComponent instanceof JInternalFrame)) {
        localJInternalFrame = (JInternalFrame)paramComponent;
      } else if ((paramComponent instanceof JInternalFrame.JDesktopIcon)) {
        localJInternalFrame = ((JInternalFrame.JDesktopIcon)paramComponent).getInternalFrame();
      } else {
        return;
      }
      if (localJInternalFrame.isSelected())
      {
        lineColor = activeColor;
        super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
      else
      {
        lineColor = inactiveColor;
        super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
  }
  
  public static class ProgressBarBorder
    extends AbstractBorder
    implements UIResource
  {
    protected Color shadow;
    protected Color highlight;
    
    public ProgressBarBorder(Color paramColor1, Color paramColor2)
    {
      highlight = paramColor2;
      shadow = paramColor1;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt3 - 1, paramInt2);
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt4 - 1);
      paramGraphics.setColor(highlight);
      paramGraphics.drawLine(paramInt1, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(paramInt3 - 1, paramInt2, paramInt3 - 1, paramInt4 - 1);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(1, 1, 1, 1);
      return paramInsets;
    }
  }
  
  public static class ToolBarBorder
    extends AbstractBorder
    implements UIResource, SwingConstants
  {
    protected Color shadow;
    protected Color highlight;
    
    public ToolBarBorder(Color paramColor1, Color paramColor2)
    {
      highlight = paramColor2;
      shadow = paramColor1;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JToolBar)) {
        return;
      }
      paramGraphics.translate(paramInt1, paramInt2);
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null)
      {
        Border localBorder = localXPStyle.getBorder(paramComponent, TMSchema.Part.TP_TOOLBAR);
        if (localBorder != null) {
          localBorder.paintBorder(paramComponent, paramGraphics, 0, 0, paramInt3, paramInt4);
        }
      }
      if (((JToolBar)paramComponent).isFloatable())
      {
        int i = ((JToolBar)paramComponent).getOrientation() == 1 ? 1 : 0;
        if (localXPStyle != null)
        {
          TMSchema.Part localPart = i != 0 ? TMSchema.Part.RP_GRIPPERVERT : TMSchema.Part.RP_GRIPPER;
          XPStyle.Skin localSkin = localXPStyle.getSkin(paramComponent, localPart);
          int j;
          int k;
          int m;
          int n;
          if (i != 0)
          {
            j = 0;
            k = 2;
            m = paramInt3 - 1;
            n = localSkin.getHeight();
          }
          else
          {
            m = localSkin.getWidth();
            n = paramInt4 - 1;
            j = paramComponent.getComponentOrientation().isLeftToRight() ? 2 : paramInt3 - m - 2;
            k = 0;
          }
          localSkin.paintSkin(paramGraphics, j, k, m, n, TMSchema.State.NORMAL);
        }
        else if (i == 0)
        {
          if (paramComponent.getComponentOrientation().isLeftToRight())
          {
            paramGraphics.setColor(shadow);
            paramGraphics.drawLine(4, 3, 4, paramInt4 - 4);
            paramGraphics.drawLine(4, paramInt4 - 4, 2, paramInt4 - 4);
            paramGraphics.setColor(highlight);
            paramGraphics.drawLine(2, 3, 3, 3);
            paramGraphics.drawLine(2, 3, 2, paramInt4 - 5);
          }
          else
          {
            paramGraphics.setColor(shadow);
            paramGraphics.drawLine(paramInt3 - 3, 3, paramInt3 - 3, paramInt4 - 4);
            paramGraphics.drawLine(paramInt3 - 4, paramInt4 - 4, paramInt3 - 4, paramInt4 - 4);
            paramGraphics.setColor(highlight);
            paramGraphics.drawLine(paramInt3 - 5, 3, paramInt3 - 4, 3);
            paramGraphics.drawLine(paramInt3 - 5, 3, paramInt3 - 5, paramInt4 - 5);
          }
        }
        else
        {
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(3, 4, paramInt3 - 4, 4);
          paramGraphics.drawLine(paramInt3 - 4, 2, paramInt3 - 4, 4);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(3, 2, paramInt3 - 4, 2);
          paramGraphics.drawLine(3, 2, 3, 3);
        }
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(1, 1, 1, 1);
      if (!(paramComponent instanceof JToolBar)) {
        return paramInsets;
      }
      if (((JToolBar)paramComponent).isFloatable())
      {
        int i = XPStyle.getXP() != null ? 12 : 9;
        if (((JToolBar)paramComponent).getOrientation() == 0)
        {
          if (paramComponent.getComponentOrientation().isLeftToRight()) {
            left = i;
          } else {
            right = i;
          }
        }
        else {
          top = i;
        }
      }
      return paramInsets;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */