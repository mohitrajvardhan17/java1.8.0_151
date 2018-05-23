package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

class MetalSplitPaneDivider
  extends BasicSplitPaneDivider
{
  private MetalBumps bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
  private MetalBumps focusBumps = new MetalBumps(10, 10, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), UIManager.getColor("SplitPane.dividerFocusColor"));
  private int inset = 2;
  private Color controlColor = MetalLookAndFeel.getControl();
  private Color primaryControlColor = UIManager.getColor("SplitPane.dividerFocusColor");
  
  public MetalSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    super(paramBasicSplitPaneUI);
  }
  
  public void paint(Graphics paramGraphics)
  {
    MetalBumps localMetalBumps;
    if (splitPane.hasFocus())
    {
      localMetalBumps = focusBumps;
      paramGraphics.setColor(primaryControlColor);
    }
    else
    {
      localMetalBumps = bumps;
      paramGraphics.setColor(controlColor);
    }
    Rectangle localRectangle = paramGraphics.getClipBounds();
    Insets localInsets = getInsets();
    paramGraphics.fillRect(x, y, width, height);
    Dimension localDimension = getSize();
    width -= inset * 2;
    height -= inset * 2;
    int i = inset;
    int j = inset;
    if (localInsets != null)
    {
      width -= left + right;
      height -= top + bottom;
      i += left;
      j += top;
    }
    localMetalBumps.setBumpArea(localDimension);
    localMetalBumps.paintIcon(this, paramGraphics, i, j);
    super.paint(paramGraphics);
  }
  
  protected JButton createLeftOneTouchButton()
  {
    JButton local1 = new JButton()
    {
      int[][] buffer = { { 0, 0, 0, 2, 2, 0, 0, 0, 0 }, { 0, 0, 2, 1, 1, 1, 0, 0, 0 }, { 0, 2, 1, 1, 1, 1, 1, 0, 0 }, { 2, 1, 1, 1, 1, 1, 1, 1, 0 }, { 0, 3, 3, 3, 3, 3, 3, 3, 3 } };
      
      public void setBorder(Border paramAnonymousBorder) {}
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        JSplitPane localJSplitPane = getSplitPaneFromSuper();
        if (localJSplitPane != null)
        {
          int i = getOneTouchSizeFromSuper();
          int j = getOrientationFromSuper();
          int k = Math.min(getDividerSize(), i);
          Color[] arrayOfColor = { getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };
          paramAnonymousGraphics.setColor(getBackground());
          if (isOpaque()) {
            paramAnonymousGraphics.fillRect(0, 0, getWidth(), getHeight());
          }
          if (getModel().isPressed()) {
            arrayOfColor[1] = arrayOfColor[2];
          }
          int m;
          int n;
          if (j == 0) {
            for (m = 1; m <= buffer[0].length; m++) {
              for (n = 1; n < k; n++) {
                if (buffer[(n - 1)][(m - 1)] != 0)
                {
                  paramAnonymousGraphics.setColor(arrayOfColor[buffer[(n - 1)][(m - 1)]]);
                  paramAnonymousGraphics.drawLine(m, n, m, n);
                }
              }
            }
          } else {
            for (m = 1; m <= buffer[0].length; m++) {
              for (n = 1; n < k; n++) {
                if (buffer[(n - 1)][(m - 1)] != 0)
                {
                  paramAnonymousGraphics.setColor(arrayOfColor[buffer[(n - 1)][(m - 1)]]);
                  paramAnonymousGraphics.drawLine(n, m, n, m);
                }
              }
            }
          }
        }
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
    };
    local1.setRequestFocusEnabled(false);
    local1.setCursor(Cursor.getPredefinedCursor(0));
    local1.setFocusPainted(false);
    local1.setBorderPainted(false);
    maybeMakeButtonOpaque(local1);
    return local1;
  }
  
  private void maybeMakeButtonOpaque(JComponent paramJComponent)
  {
    Object localObject = UIManager.get("SplitPane.oneTouchButtonsOpaque");
    if (localObject != null) {
      paramJComponent.setOpaque(((Boolean)localObject).booleanValue());
    }
  }
  
  protected JButton createRightOneTouchButton()
  {
    JButton local2 = new JButton()
    {
      int[][] buffer = { { 2, 2, 2, 2, 2, 2, 2, 2 }, { 0, 1, 1, 1, 1, 1, 1, 3 }, { 0, 0, 1, 1, 1, 1, 3, 0 }, { 0, 0, 0, 1, 1, 3, 0, 0 }, { 0, 0, 0, 0, 3, 0, 0, 0 } };
      
      public void setBorder(Border paramAnonymousBorder) {}
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        JSplitPane localJSplitPane = getSplitPaneFromSuper();
        if (localJSplitPane != null)
        {
          int i = getOneTouchSizeFromSuper();
          int j = getOrientationFromSuper();
          int k = Math.min(getDividerSize(), i);
          Color[] arrayOfColor = { getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };
          paramAnonymousGraphics.setColor(getBackground());
          if (isOpaque()) {
            paramAnonymousGraphics.fillRect(0, 0, getWidth(), getHeight());
          }
          if (getModel().isPressed()) {
            arrayOfColor[1] = arrayOfColor[2];
          }
          int m;
          int n;
          if (j == 0) {
            for (m = 1; m <= buffer[0].length; m++) {
              for (n = 1; n < k; n++) {
                if (buffer[(n - 1)][(m - 1)] != 0)
                {
                  paramAnonymousGraphics.setColor(arrayOfColor[buffer[(n - 1)][(m - 1)]]);
                  paramAnonymousGraphics.drawLine(m, n, m, n);
                }
              }
            }
          } else {
            for (m = 1; m <= buffer[0].length; m++) {
              for (n = 1; n < k; n++) {
                if (buffer[(n - 1)][(m - 1)] != 0)
                {
                  paramAnonymousGraphics.setColor(arrayOfColor[buffer[(n - 1)][(m - 1)]]);
                  paramAnonymousGraphics.drawLine(n, m, n, m);
                }
              }
            }
          }
        }
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
    };
    local2.setCursor(Cursor.getPredefinedCursor(0));
    local2.setFocusPainted(false);
    local2.setBorderPainted(false);
    local2.setRequestFocusEnabled(false);
    maybeMakeButtonOpaque(local2);
    return local2;
  }
  
  int getOneTouchSizeFromSuper()
  {
    return 6;
  }
  
  int getOneTouchOffsetFromSuper()
  {
    return 2;
  }
  
  int getOrientationFromSuper()
  {
    return orientation;
  }
  
  JSplitPane getSplitPaneFromSuper()
  {
    return splitPane;
  }
  
  JButton getLeftButtonFromSuper()
  {
    return leftButton;
  }
  
  JButton getRightButtonFromSuper()
  {
    return rightButton;
  }
  
  public class MetalDividerLayout
    implements LayoutManager
  {
    public MetalDividerLayout() {}
    
    public void layoutContainer(Container paramContainer)
    {
      JButton localJButton1 = getLeftButtonFromSuper();
      JButton localJButton2 = getRightButtonFromSuper();
      JSplitPane localJSplitPane = getSplitPaneFromSuper();
      int i = getOrientationFromSuper();
      int j = getOneTouchSizeFromSuper();
      int k = getOneTouchOffsetFromSuper();
      Insets localInsets = getInsets();
      if ((localJButton1 != null) && (localJButton2 != null) && (paramContainer == MetalSplitPaneDivider.this)) {
        if (localJSplitPane.isOneTouchExpandable())
        {
          int m;
          int n;
          if (i == 0)
          {
            m = localInsets != null ? top : 0;
            n = getDividerSize();
            if (localInsets != null) {
              n -= top + bottom;
            }
            n = Math.min(n, j);
            localJButton1.setBounds(k, m, n * 2, n);
            localJButton2.setBounds(k + j * 2, m, n * 2, n);
          }
          else
          {
            m = getDividerSize();
            n = localInsets != null ? left : 0;
            if (localInsets != null) {
              m -= left + right;
            }
            m = Math.min(m, j);
            localJButton1.setBounds(n, k, m, m * 2);
            localJButton2.setBounds(n, k + j * 2, m, m * 2);
          }
        }
        else
        {
          localJButton1.setBounds(-5, -5, 1, 1);
          localJButton2.setBounds(-5, -5, 1, 1);
        }
      }
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return new Dimension(0, 0);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return new Dimension(0, 0);
    }
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */