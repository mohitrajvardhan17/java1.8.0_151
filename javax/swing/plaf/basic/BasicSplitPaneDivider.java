package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import sun.swing.DefaultLookup;

public class BasicSplitPaneDivider
  extends Container
  implements PropertyChangeListener
{
  protected static final int ONE_TOUCH_SIZE = 6;
  protected static final int ONE_TOUCH_OFFSET = 2;
  protected DragController dragger;
  protected BasicSplitPaneUI splitPaneUI;
  protected int dividerSize = 0;
  protected Component hiddenDivider;
  protected JSplitPane splitPane;
  protected MouseHandler mouseHandler;
  protected int orientation;
  protected JButton leftButton;
  protected JButton rightButton;
  private Border border;
  private boolean mouseOver;
  private int oneTouchSize;
  private int oneTouchOffset;
  private boolean centerOneTouchButtons;
  
  public BasicSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    oneTouchSize = DefaultLookup.getInt(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.oneTouchButtonSize", 6);
    oneTouchOffset = DefaultLookup.getInt(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.oneTouchButtonOffset", 2);
    centerOneTouchButtons = DefaultLookup.getBoolean(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.centerOneTouchButtons", true);
    setLayout(new DividerLayout());
    setBasicSplitPaneUI(paramBasicSplitPaneUI);
    orientation = splitPane.getOrientation();
    setCursor(orientation == 1 ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
    setBackground(UIManager.getColor("SplitPane.background"));
  }
  
  private void revalidateSplitPane()
  {
    invalidate();
    if (splitPane != null) {
      splitPane.revalidate();
    }
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
          mouseHandler = new MouseHandler();
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
  
  public BasicSplitPaneUI getBasicSplitPaneUI()
  {
    return splitPaneUI;
  }
  
  public void setDividerSize(int paramInt)
  {
    dividerSize = paramInt;
  }
  
  public int getDividerSize()
  {
    return dividerSize;
  }
  
  public void setBorder(Border paramBorder)
  {
    Border localBorder = border;
    border = paramBorder;
  }
  
  public Border getBorder()
  {
    return border;
  }
  
  public Insets getInsets()
  {
    Border localBorder = getBorder();
    if (localBorder != null) {
      return localBorder.getBorderInsets(this);
    }
    return super.getInsets();
  }
  
  protected void setMouseOver(boolean paramBoolean)
  {
    mouseOver = paramBoolean;
  }
  
  public boolean isMouseOver()
  {
    return mouseOver;
  }
  
  public Dimension getPreferredSize()
  {
    if (orientation == 1) {
      return new Dimension(getDividerSize(), 1);
    }
    return new Dimension(1, getDividerSize());
  }
  
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramPropertyChangeEvent.getSource() == splitPane) {
      if (paramPropertyChangeEvent.getPropertyName() == "orientation")
      {
        orientation = splitPane.getOrientation();
        setCursor(orientation == 1 ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
        revalidateSplitPane();
      }
      else if (paramPropertyChangeEvent.getPropertyName() == "oneTouchExpandable")
      {
        oneTouchExpandableChanged();
      }
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
    Border localBorder = getBorder();
    if (localBorder != null)
    {
      Dimension localDimension = getSize();
      localBorder.paintBorder(this, paramGraphics, 0, 0, width, height);
    }
  }
  
  protected void oneTouchExpandableChanged()
  {
    if (!DefaultLookup.getBoolean(splitPane, splitPaneUI, "SplitPane.supportsOneTouchButtons", true)) {
      return;
    }
    if ((splitPane.isOneTouchExpandable()) && (leftButton == null) && (rightButton == null))
    {
      leftButton = createLeftOneTouchButton();
      if (leftButton != null) {
        leftButton.addActionListener(new OneTouchActionHandler(true));
      }
      rightButton = createRightOneTouchButton();
      if (rightButton != null) {
        rightButton.addActionListener(new OneTouchActionHandler(false));
      }
      if ((leftButton != null) && (rightButton != null))
      {
        add(leftButton);
        add(rightButton);
      }
    }
    revalidateSplitPane();
  }
  
  protected JButton createLeftOneTouchButton()
  {
    JButton local1 = new JButton()
    {
      public void setBorder(Border paramAnonymousBorder) {}
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        if (splitPane != null)
        {
          int[] arrayOfInt1 = new int[3];
          int[] arrayOfInt2 = new int[3];
          paramAnonymousGraphics.setColor(getBackground());
          paramAnonymousGraphics.fillRect(0, 0, getWidth(), getHeight());
          paramAnonymousGraphics.setColor(Color.black);
          int i;
          if (orientation == 0)
          {
            i = Math.min(getHeight(), oneTouchSize);
            arrayOfInt1[0] = i;
            arrayOfInt1[1] = 0;
            arrayOfInt1[2] = (i << 1);
            arrayOfInt2[0] = 0;
            arrayOfInt2[1] = (arrayOfInt2[2] = i);
            paramAnonymousGraphics.drawPolygon(arrayOfInt1, arrayOfInt2, 3);
          }
          else
          {
            i = Math.min(getWidth(), oneTouchSize);
            arrayOfInt1[0] = (arrayOfInt1[2] = i);
            arrayOfInt1[1] = 0;
            arrayOfInt2[0] = 0;
            arrayOfInt2[1] = i;
            arrayOfInt2[2] = (i << 1);
          }
          paramAnonymousGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
        }
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
    };
    local1.setMinimumSize(new Dimension(oneTouchSize, oneTouchSize));
    local1.setCursor(Cursor.getPredefinedCursor(0));
    local1.setFocusPainted(false);
    local1.setBorderPainted(false);
    local1.setRequestFocusEnabled(false);
    return local1;
  }
  
  protected JButton createRightOneTouchButton()
  {
    JButton local2 = new JButton()
    {
      public void setBorder(Border paramAnonymousBorder) {}
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        if (splitPane != null)
        {
          int[] arrayOfInt1 = new int[3];
          int[] arrayOfInt2 = new int[3];
          paramAnonymousGraphics.setColor(getBackground());
          paramAnonymousGraphics.fillRect(0, 0, getWidth(), getHeight());
          int i;
          if (orientation == 0)
          {
            i = Math.min(getHeight(), oneTouchSize);
            arrayOfInt1[0] = i;
            arrayOfInt1[1] = (i << 1);
            arrayOfInt1[2] = 0;
            arrayOfInt2[0] = i;
            arrayOfInt2[1] = (arrayOfInt2[2] = 0);
          }
          else
          {
            i = Math.min(getWidth(), oneTouchSize);
            arrayOfInt1[0] = (arrayOfInt1[2] = 0);
            arrayOfInt1[1] = i;
            arrayOfInt2[0] = 0;
            arrayOfInt2[1] = i;
            arrayOfInt2[2] = (i << 1);
          }
          paramAnonymousGraphics.setColor(Color.black);
          paramAnonymousGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
        }
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
    };
    local2.setMinimumSize(new Dimension(oneTouchSize, oneTouchSize));
    local2.setCursor(Cursor.getPredefinedCursor(0));
    local2.setFocusPainted(false);
    local2.setBorderPainted(false);
    local2.setRequestFocusEnabled(false);
    return local2;
  }
  
  protected void prepareForDragging()
  {
    splitPaneUI.startDragging();
  }
  
  protected void dragDividerTo(int paramInt)
  {
    splitPaneUI.dragDividerTo(paramInt);
  }
  
  protected void finishDraggingTo(int paramInt)
  {
    splitPaneUI.finishDraggingTo(paramInt);
  }
  
  protected class DividerLayout
    implements LayoutManager
  {
    protected DividerLayout() {}
    
    public void layoutContainer(Container paramContainer)
    {
      if ((leftButton != null) && (rightButton != null) && (paramContainer == BasicSplitPaneDivider.this)) {
        if (splitPane.isOneTouchExpandable())
        {
          Insets localInsets = getInsets();
          int i;
          int j;
          int k;
          if (orientation == 0)
          {
            i = localInsets != null ? left : 0;
            j = getHeight();
            if (localInsets != null)
            {
              j -= top + bottom;
              j = Math.max(j, 0);
            }
            j = Math.min(j, oneTouchSize);
            k = (getSizeheight - j) / 2;
            if (!centerOneTouchButtons)
            {
              k = localInsets != null ? top : 0;
              i = 0;
            }
            leftButton.setBounds(i + oneTouchOffset, k, j * 2, j);
            rightButton.setBounds(i + oneTouchOffset + oneTouchSize * 2, k, j * 2, j);
          }
          else
          {
            i = localInsets != null ? top : 0;
            j = getWidth();
            if (localInsets != null)
            {
              j -= left + right;
              j = Math.max(j, 0);
            }
            j = Math.min(j, oneTouchSize);
            k = (getSizewidth - j) / 2;
            if (!centerOneTouchButtons)
            {
              k = localInsets != null ? left : 0;
              i = 0;
            }
            leftButton.setBounds(k, i + oneTouchOffset, j, j * 2);
            rightButton.setBounds(k, i + oneTouchOffset + oneTouchSize * 2, j, j * 2);
          }
        }
        else
        {
          leftButton.setBounds(-5, -5, 1, 1);
          rightButton.setBounds(-5, -5, 1, 1);
        }
      }
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      if ((paramContainer != BasicSplitPaneDivider.this) || (splitPane == null)) {
        return new Dimension(0, 0);
      }
      Dimension localDimension = null;
      if ((splitPane.isOneTouchExpandable()) && (leftButton != null)) {
        localDimension = leftButton.getMinimumSize();
      }
      Insets localInsets = getInsets();
      int i = getDividerSize();
      int j = i;
      int k;
      if (orientation == 0)
      {
        if (localDimension != null)
        {
          k = height;
          if (localInsets != null) {
            k += top + bottom;
          }
          j = Math.max(j, k);
        }
        i = 1;
      }
      else
      {
        if (localDimension != null)
        {
          k = width;
          if (localInsets != null) {
            k += left + right;
          }
          i = Math.max(i, k);
        }
        j = 1;
      }
      return new Dimension(i, j);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
  }
  
  protected class DragController
  {
    int initialX;
    int maxX;
    int minX;
    int offset;
    
    protected DragController(MouseEvent paramMouseEvent)
    {
      JSplitPane localJSplitPane = splitPaneUI.getSplitPane();
      Component localComponent1 = localJSplitPane.getLeftComponent();
      Component localComponent2 = localJSplitPane.getRightComponent();
      initialX = getLocation().x;
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        offset = paramMouseEvent.getX();
      } else {
        offset = (paramMouseEvent.getX() - initialX);
      }
      if ((localComponent1 == null) || (localComponent2 == null) || (offset < -1) || (offset >= getSize().width))
      {
        maxX = -1;
      }
      else
      {
        Insets localInsets = localJSplitPane.getInsets();
        if (localComponent1.isVisible())
        {
          minX = getMinimumSizewidth;
          if (localInsets != null) {
            minX += left;
          }
        }
        else
        {
          minX = 0;
        }
        int i;
        if (localComponent2.isVisible())
        {
          i = localInsets != null ? right : 0;
          maxX = Math.max(0, getSizewidth - (getSize().width + i) - getMinimumSizewidth);
        }
        else
        {
          i = localInsets != null ? right : 0;
          maxX = Math.max(0, getSizewidth - (getSize().width + i));
        }
        if (maxX < minX) {
          minX = (maxX = 0);
        }
      }
    }
    
    protected boolean isValid()
    {
      return maxX > 0;
    }
    
    protected int positionForMouseEvent(MouseEvent paramMouseEvent)
    {
      int i = paramMouseEvent.getSource() == BasicSplitPaneDivider.this ? paramMouseEvent.getX() + getLocation().x : paramMouseEvent.getX();
      i = Math.min(maxX, Math.max(minX, i - offset));
      return i;
    }
    
    protected int getNeededLocation(int paramInt1, int paramInt2)
    {
      int i = Math.min(maxX, Math.max(minX, paramInt1 - offset));
      return i;
    }
    
    protected void continueDrag(int paramInt1, int paramInt2)
    {
      dragDividerTo(getNeededLocation(paramInt1, paramInt2));
    }
    
    protected void continueDrag(MouseEvent paramMouseEvent)
    {
      dragDividerTo(positionForMouseEvent(paramMouseEvent));
    }
    
    protected void completeDrag(int paramInt1, int paramInt2)
    {
      finishDraggingTo(getNeededLocation(paramInt1, paramInt2));
    }
    
    protected void completeDrag(MouseEvent paramMouseEvent)
    {
      finishDraggingTo(positionForMouseEvent(paramMouseEvent));
    }
  }
  
  protected class MouseHandler
    extends MouseAdapter
    implements MouseMotionListener
  {
    protected MouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (((paramMouseEvent.getSource() == BasicSplitPaneDivider.this) || (paramMouseEvent.getSource() == splitPane)) && (dragger == null) && (splitPane.isEnabled()))
      {
        Component localComponent = splitPaneUI.getNonContinuousLayoutDivider();
        if (hiddenDivider != localComponent)
        {
          if (hiddenDivider != null)
          {
            hiddenDivider.removeMouseListener(this);
            hiddenDivider.removeMouseMotionListener(this);
          }
          hiddenDivider = localComponent;
          if (hiddenDivider != null)
          {
            hiddenDivider.addMouseMotionListener(this);
            hiddenDivider.addMouseListener(this);
          }
        }
        if ((splitPane.getLeftComponent() != null) && (splitPane.getRightComponent() != null))
        {
          if (orientation == 1) {
            dragger = new BasicSplitPaneDivider.DragController(BasicSplitPaneDivider.this, paramMouseEvent);
          } else {
            dragger = new BasicSplitPaneDivider.VerticalDragController(BasicSplitPaneDivider.this, paramMouseEvent);
          }
          if (!dragger.isValid())
          {
            dragger = null;
          }
          else
          {
            prepareForDragging();
            dragger.continueDrag(paramMouseEvent);
          }
        }
        paramMouseEvent.consume();
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (dragger != null)
      {
        if (paramMouseEvent.getSource() == splitPane)
        {
          dragger.completeDrag(paramMouseEvent.getX(), paramMouseEvent.getY());
        }
        else
        {
          Point localPoint;
          if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this)
          {
            localPoint = getLocation();
            dragger.completeDrag(paramMouseEvent.getX() + x, paramMouseEvent.getY() + y);
          }
          else if (paramMouseEvent.getSource() == hiddenDivider)
          {
            localPoint = hiddenDivider.getLocation();
            int i = paramMouseEvent.getX() + x;
            int j = paramMouseEvent.getY() + y;
            dragger.completeDrag(i, j);
          }
        }
        dragger = null;
        paramMouseEvent.consume();
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (dragger != null)
      {
        if (paramMouseEvent.getSource() == splitPane)
        {
          dragger.continueDrag(paramMouseEvent.getX(), paramMouseEvent.getY());
        }
        else
        {
          Point localPoint;
          if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this)
          {
            localPoint = getLocation();
            dragger.continueDrag(paramMouseEvent.getX() + x, paramMouseEvent.getY() + y);
          }
          else if (paramMouseEvent.getSource() == hiddenDivider)
          {
            localPoint = hiddenDivider.getLocation();
            int i = paramMouseEvent.getX() + x;
            int j = paramMouseEvent.getY() + y;
            dragger.continueDrag(i, j);
          }
        }
        paramMouseEvent.consume();
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        setMouseOver(true);
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        setMouseOver(false);
      }
    }
  }
  
  private class OneTouchActionHandler
    implements ActionListener
  {
    private boolean toMinimum;
    
    OneTouchActionHandler(boolean paramBoolean)
    {
      toMinimum = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Insets localInsets = splitPane.getInsets();
      int i = splitPane.getLastDividerLocation();
      int j = splitPaneUI.getDividerLocation(splitPane);
      int m;
      int k;
      if (toMinimum)
      {
        if (orientation == 0)
        {
          if (j >= splitPane.getHeight() - bottom - getHeight())
          {
            m = splitPane.getMaximumDividerLocation();
            k = Math.min(i, m);
            splitPaneUI.setKeepHidden(false);
          }
          else
          {
            k = top;
            splitPaneUI.setKeepHidden(true);
          }
        }
        else if (j >= splitPane.getWidth() - right - getWidth())
        {
          m = splitPane.getMaximumDividerLocation();
          k = Math.min(i, m);
          splitPaneUI.setKeepHidden(false);
        }
        else
        {
          k = left;
          splitPaneUI.setKeepHidden(true);
        }
      }
      else if (orientation == 0)
      {
        if (j == top)
        {
          m = splitPane.getMaximumDividerLocation();
          k = Math.min(i, m);
          splitPaneUI.setKeepHidden(false);
        }
        else
        {
          k = splitPane.getHeight() - getHeight() - top;
          splitPaneUI.setKeepHidden(true);
        }
      }
      else if (j == left)
      {
        m = splitPane.getMaximumDividerLocation();
        k = Math.min(i, m);
        splitPaneUI.setKeepHidden(false);
      }
      else
      {
        k = splitPane.getWidth() - getWidth() - left;
        splitPaneUI.setKeepHidden(true);
      }
      if (j != k)
      {
        splitPane.setDividerLocation(k);
        splitPane.setLastDividerLocation(j);
      }
    }
  }
  
  protected class VerticalDragController
    extends BasicSplitPaneDivider.DragController
  {
    protected VerticalDragController(MouseEvent paramMouseEvent)
    {
      super(paramMouseEvent);
      JSplitPane localJSplitPane = splitPaneUI.getSplitPane();
      Component localComponent1 = localJSplitPane.getLeftComponent();
      Component localComponent2 = localJSplitPane.getRightComponent();
      initialX = getLocation().y;
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        offset = paramMouseEvent.getY();
      } else {
        offset = (paramMouseEvent.getY() - initialX);
      }
      if ((localComponent1 == null) || (localComponent2 == null) || (offset < -1) || (offset > getSize().height))
      {
        maxX = -1;
      }
      else
      {
        Insets localInsets = localJSplitPane.getInsets();
        if (localComponent1.isVisible())
        {
          minX = getMinimumSizeheight;
          if (localInsets != null) {
            minX += top;
          }
        }
        else
        {
          minX = 0;
        }
        int i;
        if (localComponent2.isVisible())
        {
          i = localInsets != null ? bottom : 0;
          maxX = Math.max(0, getSizeheight - (getSize().height + i) - getMinimumSizeheight);
        }
        else
        {
          i = localInsets != null ? bottom : 0;
          maxX = Math.max(0, getSizeheight - (getSize().height + i));
        }
        if (maxX < minX) {
          minX = (maxX = 0);
        }
      }
    }
    
    protected int getNeededLocation(int paramInt1, int paramInt2)
    {
      int i = Math.min(maxX, Math.max(minX, paramInt2 - offset));
      return i;
    }
    
    protected int positionForMouseEvent(MouseEvent paramMouseEvent)
    {
      int i = paramMouseEvent.getSource() == BasicSplitPaneDivider.this ? paramMouseEvent.getY() + getLocation().y : paramMouseEvent.getY();
      i = Math.min(maxX, Math.max(minX, i - offset));
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */