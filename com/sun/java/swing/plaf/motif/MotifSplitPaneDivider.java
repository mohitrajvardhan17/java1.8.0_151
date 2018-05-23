package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneDivider.DragController;
import javax.swing.plaf.basic.BasicSplitPaneDivider.MouseHandler;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MotifSplitPaneDivider
  extends BasicSplitPaneDivider
{
  private static final Cursor defaultCursor = Cursor.getPredefinedCursor(0);
  public static final int minimumThumbSize = 6;
  public static final int defaultDividerSize = 18;
  protected static final int pad = 6;
  private int hThumbOffset = 30;
  private int vThumbOffset = 40;
  protected int hThumbWidth = 12;
  protected int hThumbHeight = 18;
  protected int vThumbWidth = 18;
  protected int vThumbHeight = 12;
  protected Color highlightColor = UIManager.getColor("SplitPane.highlight");
  protected Color shadowColor = UIManager.getColor("SplitPane.shadow");
  protected Color focusedColor = UIManager.getColor("SplitPane.activeThumb");
  
  public MotifSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    super(paramBasicSplitPaneUI);
    setDividerSize(hThumbWidth + 6);
  }
  
  public void setDividerSize(int paramInt)
  {
    Insets localInsets = getInsets();
    int i = 0;
    if (getBasicSplitPaneUI().getOrientation() == 1)
    {
      if (localInsets != null) {
        i = left + right;
      }
    }
    else if (localInsets != null) {
      i = top + bottom;
    }
    if (paramInt < 12 + i)
    {
      setDividerSize(12 + i);
    }
    else
    {
      vThumbHeight = (hThumbWidth = paramInt - 6 - i);
      super.setDividerSize(paramInt);
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    Color localColor = getBackground();
    Dimension localDimension = getSize();
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(0, 0, width, height);
    int i;
    int j;
    int k;
    if (getBasicSplitPaneUI().getOrientation() == 1)
    {
      i = width / 2;
      j = i - hThumbWidth / 2;
      k = hThumbOffset;
      paramGraphics.setColor(shadowColor);
      paramGraphics.drawLine(i - 1, 0, i - 1, height);
      paramGraphics.setColor(highlightColor);
      paramGraphics.drawLine(i, 0, i, height);
      paramGraphics.setColor(splitPane.hasFocus() ? focusedColor : getBackground());
      paramGraphics.fillRect(j + 1, k + 1, hThumbWidth - 2, hThumbHeight - 1);
      paramGraphics.setColor(highlightColor);
      paramGraphics.drawLine(j, k, j + hThumbWidth - 1, k);
      paramGraphics.drawLine(j, k + 1, j, k + hThumbHeight - 1);
      paramGraphics.setColor(shadowColor);
      paramGraphics.drawLine(j + 1, k + hThumbHeight - 1, j + hThumbWidth - 1, k + hThumbHeight - 1);
      paramGraphics.drawLine(j + hThumbWidth - 1, k + 1, j + hThumbWidth - 1, k + hThumbHeight - 2);
    }
    else
    {
      i = height / 2;
      j = width - vThumbOffset;
      k = height / 2 - vThumbHeight / 2;
      paramGraphics.setColor(shadowColor);
      paramGraphics.drawLine(0, i - 1, width, i - 1);
      paramGraphics.setColor(highlightColor);
      paramGraphics.drawLine(0, i, width, i);
      paramGraphics.setColor(splitPane.hasFocus() ? focusedColor : getBackground());
      paramGraphics.fillRect(j + 1, k + 1, vThumbWidth - 1, vThumbHeight - 1);
      paramGraphics.setColor(highlightColor);
      paramGraphics.drawLine(j, k, j + vThumbWidth, k);
      paramGraphics.drawLine(j, k + 1, j, k + vThumbHeight);
      paramGraphics.setColor(shadowColor);
      paramGraphics.drawLine(j + 1, k + vThumbHeight, j + vThumbWidth, k + vThumbHeight);
      paramGraphics.drawLine(j + vThumbWidth, k + 1, j + vThumbWidth, k + vThumbHeight - 1);
    }
    super.paint(paramGraphics);
  }
  
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }
  
  public void setBasicSplitPaneUI(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    if (splitPane != null)
    {
      splitPane.removePropertyChangeListener(this);
      if (mouseHandler != null)
      {
        splitPane.removeMouseListener(mouseHandler);
        splitPane.removeMouseMotionListener(mouseHandler);
        removeMouseListener(mouseHandler);
        removeMouseMotionListener(mouseHandler);
        mouseHandler = null;
      }
    }
    splitPaneUI = paramBasicSplitPaneUI;
    if (paramBasicSplitPaneUI != null)
    {
      splitPane = paramBasicSplitPaneUI.getSplitPane();
      if (splitPane != null)
      {
        if (mouseHandler == null) {
          mouseHandler = new MotifMouseHandler(null);
        }
        splitPane.addMouseListener(mouseHandler);
        splitPane.addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        splitPane.addPropertyChangeListener(this);
        if (splitPane.isOneTouchExpandable()) {
          oneTouchExpandableChanged();
        }
      }
    }
    else
    {
      splitPane = null;
    }
  }
  
  private boolean isInThumb(int paramInt1, int paramInt2)
  {
    Dimension localDimension = getSize();
    int n;
    int i;
    int j;
    int k;
    int m;
    if (getBasicSplitPaneUI().getOrientation() == 1)
    {
      n = width / 2;
      i = n - hThumbWidth / 2;
      j = hThumbOffset;
      k = hThumbWidth;
      m = hThumbHeight;
    }
    else
    {
      n = height / 2;
      i = width - vThumbOffset;
      j = height / 2 - vThumbHeight / 2;
      k = vThumbWidth;
      m = vThumbHeight;
    }
    return (paramInt1 >= i) && (paramInt1 < i + k) && (paramInt2 >= j) && (paramInt2 < j + m);
  }
  
  private BasicSplitPaneDivider.DragController getDragger()
  {
    return dragger;
  }
  
  private JSplitPane getSplitPane()
  {
    return splitPane;
  }
  
  private class MotifMouseHandler
    extends BasicSplitPaneDivider.MouseHandler
  {
    private MotifMouseHandler()
    {
      super();
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.getSource() == MotifSplitPaneDivider.this) && (MotifSplitPaneDivider.this.getDragger() == null) && (MotifSplitPaneDivider.this.getSplitPane().isEnabled()) && (MotifSplitPaneDivider.this.isInThumb(paramMouseEvent.getX(), paramMouseEvent.getY()))) {
        super.mousePressed(paramMouseEvent);
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (MotifSplitPaneDivider.this.getDragger() != null) {
        return;
      }
      if (!MotifSplitPaneDivider.this.isInThumb(paramMouseEvent.getX(), paramMouseEvent.getY()))
      {
        if (getCursor() != MotifSplitPaneDivider.defaultCursor) {
          setCursor(MotifSplitPaneDivider.defaultCursor);
        }
        return;
      }
      super.mouseMoved(paramMouseEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */