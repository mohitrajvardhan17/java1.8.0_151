package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.UIResource;

public class JScrollPane
  extends JComponent
  implements ScrollPaneConstants, Accessible
{
  private Border viewportBorder;
  private static final String uiClassID = "ScrollPaneUI";
  protected int verticalScrollBarPolicy = 20;
  protected int horizontalScrollBarPolicy = 30;
  protected JViewport viewport;
  protected JScrollBar verticalScrollBar;
  protected JScrollBar horizontalScrollBar;
  protected JViewport rowHeader;
  protected JViewport columnHeader;
  protected Component lowerLeft;
  protected Component lowerRight;
  protected Component upperLeft;
  protected Component upperRight;
  private boolean wheelScrollState = true;
  
  public JScrollPane(Component paramComponent, int paramInt1, int paramInt2)
  {
    setLayout(new ScrollPaneLayout.UIResource());
    setVerticalScrollBarPolicy(paramInt1);
    setHorizontalScrollBarPolicy(paramInt2);
    setViewport(createViewport());
    setVerticalScrollBar(createVerticalScrollBar());
    setHorizontalScrollBar(createHorizontalScrollBar());
    if (paramComponent != null) {
      setViewportView(paramComponent);
    }
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
    if (!getComponentOrientation().isLeftToRight()) {
      viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
    }
  }
  
  public JScrollPane(Component paramComponent)
  {
    this(paramComponent, 20, 30);
  }
  
  public JScrollPane(int paramInt1, int paramInt2)
  {
    this(null, paramInt1, paramInt2);
  }
  
  public JScrollPane()
  {
    this(null, 20, 30);
  }
  
  public ScrollPaneUI getUI()
  {
    return (ScrollPaneUI)ui;
  }
  
  public void setUI(ScrollPaneUI paramScrollPaneUI)
  {
    super.setUI(paramScrollPaneUI);
  }
  
  public void updateUI()
  {
    setUI((ScrollPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ScrollPaneUI";
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    if ((paramLayoutManager instanceof ScrollPaneLayout))
    {
      super.setLayout(paramLayoutManager);
      ((ScrollPaneLayout)paramLayoutManager).syncWithScrollPane(this);
    }
    else if (paramLayoutManager == null)
    {
      super.setLayout(paramLayoutManager);
    }
    else
    {
      String str = "layout of JScrollPane must be a ScrollPaneLayout";
      throw new ClassCastException(str);
    }
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  public int getVerticalScrollBarPolicy()
  {
    return verticalScrollBarPolicy;
  }
  
  public void setVerticalScrollBarPolicy(int paramInt)
  {
    switch (paramInt)
    {
    case 20: 
    case 21: 
    case 22: 
      break;
    default: 
      throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
    }
    int i = verticalScrollBarPolicy;
    verticalScrollBarPolicy = paramInt;
    firePropertyChange("verticalScrollBarPolicy", i, paramInt);
    revalidate();
    repaint();
  }
  
  public int getHorizontalScrollBarPolicy()
  {
    return horizontalScrollBarPolicy;
  }
  
  public void setHorizontalScrollBarPolicy(int paramInt)
  {
    switch (paramInt)
    {
    case 30: 
    case 31: 
    case 32: 
      break;
    default: 
      throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
    }
    int i = horizontalScrollBarPolicy;
    horizontalScrollBarPolicy = paramInt;
    firePropertyChange("horizontalScrollBarPolicy", i, paramInt);
    revalidate();
    repaint();
  }
  
  public Border getViewportBorder()
  {
    return viewportBorder;
  }
  
  public void setViewportBorder(Border paramBorder)
  {
    Border localBorder = viewportBorder;
    viewportBorder = paramBorder;
    firePropertyChange("viewportBorder", localBorder, paramBorder);
  }
  
  public Rectangle getViewportBorderBounds()
  {
    Rectangle localRectangle = new Rectangle(getSize());
    Insets localInsets = getInsets();
    x = left;
    y = top;
    width -= left + right;
    height -= top + bottom;
    boolean bool = SwingUtilities.isLeftToRight(this);
    JViewport localJViewport1 = getColumnHeader();
    if ((localJViewport1 != null) && (localJViewport1.isVisible()))
    {
      int i = localJViewport1.getHeight();
      y += i;
      height -= i;
    }
    JViewport localJViewport2 = getRowHeader();
    if ((localJViewport2 != null) && (localJViewport2.isVisible()))
    {
      int j = localJViewport2.getWidth();
      if (bool) {
        x += j;
      }
      width -= j;
    }
    JScrollBar localJScrollBar1 = getVerticalScrollBar();
    if ((localJScrollBar1 != null) && (localJScrollBar1.isVisible()))
    {
      int k = localJScrollBar1.getWidth();
      if (!bool) {
        x += k;
      }
      width -= k;
    }
    JScrollBar localJScrollBar2 = getHorizontalScrollBar();
    if ((localJScrollBar2 != null) && (localJScrollBar2.isVisible())) {
      height -= localJScrollBar2.getHeight();
    }
    return localRectangle;
  }
  
  public JScrollBar createHorizontalScrollBar()
  {
    return new ScrollBar(0);
  }
  
  @Transient
  public JScrollBar getHorizontalScrollBar()
  {
    return horizontalScrollBar;
  }
  
  public void setHorizontalScrollBar(JScrollBar paramJScrollBar)
  {
    JScrollBar localJScrollBar = getHorizontalScrollBar();
    horizontalScrollBar = paramJScrollBar;
    if (paramJScrollBar != null) {
      add(paramJScrollBar, "HORIZONTAL_SCROLLBAR");
    } else if (localJScrollBar != null) {
      remove(localJScrollBar);
    }
    firePropertyChange("horizontalScrollBar", localJScrollBar, paramJScrollBar);
    revalidate();
    repaint();
  }
  
  public JScrollBar createVerticalScrollBar()
  {
    return new ScrollBar(1);
  }
  
  @Transient
  public JScrollBar getVerticalScrollBar()
  {
    return verticalScrollBar;
  }
  
  public void setVerticalScrollBar(JScrollBar paramJScrollBar)
  {
    JScrollBar localJScrollBar = getVerticalScrollBar();
    verticalScrollBar = paramJScrollBar;
    add(paramJScrollBar, "VERTICAL_SCROLLBAR");
    firePropertyChange("verticalScrollBar", localJScrollBar, paramJScrollBar);
    revalidate();
    repaint();
  }
  
  protected JViewport createViewport()
  {
    return new JViewport();
  }
  
  public JViewport getViewport()
  {
    return viewport;
  }
  
  public void setViewport(JViewport paramJViewport)
  {
    JViewport localJViewport = getViewport();
    viewport = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "VIEWPORT");
    } else if (localJViewport != null) {
      remove(localJViewport);
    }
    firePropertyChange("viewport", localJViewport, paramJViewport);
    if (accessibleContext != null) {
      ((AccessibleJScrollPane)accessibleContext).resetViewPort();
    }
    revalidate();
    repaint();
  }
  
  public void setViewportView(Component paramComponent)
  {
    if (getViewport() == null) {
      setViewport(createViewport());
    }
    getViewport().setView(paramComponent);
  }
  
  @Transient
  public JViewport getRowHeader()
  {
    return rowHeader;
  }
  
  public void setRowHeader(JViewport paramJViewport)
  {
    JViewport localJViewport = getRowHeader();
    rowHeader = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "ROW_HEADER");
    } else if (localJViewport != null) {
      remove(localJViewport);
    }
    firePropertyChange("rowHeader", localJViewport, paramJViewport);
    revalidate();
    repaint();
  }
  
  public void setRowHeaderView(Component paramComponent)
  {
    if (getRowHeader() == null) {
      setRowHeader(createViewport());
    }
    getRowHeader().setView(paramComponent);
  }
  
  @Transient
  public JViewport getColumnHeader()
  {
    return columnHeader;
  }
  
  public void setColumnHeader(JViewport paramJViewport)
  {
    JViewport localJViewport = getColumnHeader();
    columnHeader = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "COLUMN_HEADER");
    } else if (localJViewport != null) {
      remove(localJViewport);
    }
    firePropertyChange("columnHeader", localJViewport, paramJViewport);
    revalidate();
    repaint();
  }
  
  public void setColumnHeaderView(Component paramComponent)
  {
    if (getColumnHeader() == null) {
      setColumnHeader(createViewport());
    }
    getColumnHeader().setView(paramComponent);
  }
  
  public Component getCorner(String paramString)
  {
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramString.equals("LOWER_LEADING_CORNER")) {
      paramString = bool ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
    } else if (paramString.equals("LOWER_TRAILING_CORNER")) {
      paramString = bool ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    } else if (paramString.equals("UPPER_LEADING_CORNER")) {
      paramString = bool ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
    } else if (paramString.equals("UPPER_TRAILING_CORNER")) {
      paramString = bool ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
    }
    if (paramString.equals("LOWER_LEFT_CORNER")) {
      return lowerLeft;
    }
    if (paramString.equals("LOWER_RIGHT_CORNER")) {
      return lowerRight;
    }
    if (paramString.equals("UPPER_LEFT_CORNER")) {
      return upperLeft;
    }
    if (paramString.equals("UPPER_RIGHT_CORNER")) {
      return upperRight;
    }
    return null;
  }
  
  public void setCorner(String paramString, Component paramComponent)
  {
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramString.equals("LOWER_LEADING_CORNER")) {
      paramString = bool ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
    } else if (paramString.equals("LOWER_TRAILING_CORNER")) {
      paramString = bool ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    } else if (paramString.equals("UPPER_LEADING_CORNER")) {
      paramString = bool ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
    } else if (paramString.equals("UPPER_TRAILING_CORNER")) {
      paramString = bool ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
    }
    Component localComponent;
    if (paramString.equals("LOWER_LEFT_CORNER"))
    {
      localComponent = lowerLeft;
      lowerLeft = paramComponent;
    }
    else if (paramString.equals("LOWER_RIGHT_CORNER"))
    {
      localComponent = lowerRight;
      lowerRight = paramComponent;
    }
    else if (paramString.equals("UPPER_LEFT_CORNER"))
    {
      localComponent = upperLeft;
      upperLeft = paramComponent;
    }
    else if (paramString.equals("UPPER_RIGHT_CORNER"))
    {
      localComponent = upperRight;
      upperRight = paramComponent;
    }
    else
    {
      throw new IllegalArgumentException("invalid corner key");
    }
    if (localComponent != null) {
      remove(localComponent);
    }
    if (paramComponent != null) {
      add(paramComponent, paramString);
    }
    firePropertyChange(paramString, localComponent, paramComponent);
    revalidate();
    repaint();
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    super.setComponentOrientation(paramComponentOrientation);
    if (verticalScrollBar != null) {
      verticalScrollBar.setComponentOrientation(paramComponentOrientation);
    }
    if (horizontalScrollBar != null) {
      horizontalScrollBar.setComponentOrientation(paramComponentOrientation);
    }
  }
  
  public boolean isWheelScrollingEnabled()
  {
    return wheelScrollState;
  }
  
  public void setWheelScrollingEnabled(boolean paramBoolean)
  {
    boolean bool = wheelScrollState;
    wheelScrollState = paramBoolean;
    firePropertyChange("wheelScrollingEnabled", bool, paramBoolean);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ScrollPaneUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    String str1 = viewportBorder != null ? viewportBorder.toString() : "";
    String str2 = viewport != null ? viewport.toString() : "";
    String str3;
    if (verticalScrollBarPolicy == 20) {
      str3 = "VERTICAL_SCROLLBAR_AS_NEEDED";
    } else if (verticalScrollBarPolicy == 21) {
      str3 = "VERTICAL_SCROLLBAR_NEVER";
    } else if (verticalScrollBarPolicy == 22) {
      str3 = "VERTICAL_SCROLLBAR_ALWAYS";
    } else {
      str3 = "";
    }
    String str4;
    if (horizontalScrollBarPolicy == 30) {
      str4 = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
    } else if (horizontalScrollBarPolicy == 31) {
      str4 = "HORIZONTAL_SCROLLBAR_NEVER";
    } else if (horizontalScrollBarPolicy == 32) {
      str4 = "HORIZONTAL_SCROLLBAR_ALWAYS";
    } else {
      str4 = "";
    }
    String str5 = horizontalScrollBar != null ? horizontalScrollBar.toString() : "";
    String str6 = verticalScrollBar != null ? verticalScrollBar.toString() : "";
    String str7 = columnHeader != null ? columnHeader.toString() : "";
    String str8 = rowHeader != null ? rowHeader.toString() : "";
    String str9 = lowerLeft != null ? lowerLeft.toString() : "";
    String str10 = lowerRight != null ? lowerRight.toString() : "";
    String str11 = upperLeft != null ? upperLeft.toString() : "";
    String str12 = upperRight != null ? upperRight.toString() : "";
    return super.paramString() + ",columnHeader=" + str7 + ",horizontalScrollBar=" + str5 + ",horizontalScrollBarPolicy=" + str4 + ",lowerLeft=" + str9 + ",lowerRight=" + str10 + ",rowHeader=" + str8 + ",upperLeft=" + str11 + ",upperRight=" + str12 + ",verticalScrollBar=" + str6 + ",verticalScrollBarPolicy=" + str3 + ",viewport=" + str2 + ",viewportBorder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJScrollPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJScrollPane
    extends JComponent.AccessibleJComponent
    implements ChangeListener, PropertyChangeListener
  {
    protected JViewport viewPort = null;
    
    public void resetViewPort()
    {
      if (viewPort != null)
      {
        viewPort.removeChangeListener(this);
        viewPort.removePropertyChangeListener(this);
      }
      viewPort = getViewport();
      if (viewPort != null)
      {
        viewPort.addChangeListener(this);
        viewPort.addPropertyChangeListener(this);
      }
    }
    
    public AccessibleJScrollPane()
    {
      super();
      resetViewPort();
      JScrollBar localJScrollBar = getHorizontalScrollBar();
      if (localJScrollBar != null) {
        setScrollBarRelations(localJScrollBar);
      }
      localJScrollBar = getVerticalScrollBar();
      if (localJScrollBar != null) {
        setScrollBarRelations(localJScrollBar);
      }
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SCROLL_PANE;
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (paramChangeEvent == null) {
        throw new NullPointerException();
      }
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (((str == "horizontalScrollBar") || (str == "verticalScrollBar")) && ((paramPropertyChangeEvent.getNewValue() instanceof JScrollBar))) {
        setScrollBarRelations((JScrollBar)paramPropertyChangeEvent.getNewValue());
      }
    }
    
    void setScrollBarRelations(JScrollBar paramJScrollBar)
    {
      AccessibleRelation localAccessibleRelation1 = new AccessibleRelation(AccessibleRelation.CONTROLLED_BY, paramJScrollBar);
      AccessibleRelation localAccessibleRelation2 = new AccessibleRelation(AccessibleRelation.CONTROLLER_FOR, JScrollPane.this);
      AccessibleContext localAccessibleContext = paramJScrollBar.getAccessibleContext();
      localAccessibleContext.getAccessibleRelationSet().add(localAccessibleRelation2);
      getAccessibleRelationSet().add(localAccessibleRelation1);
    }
  }
  
  protected class ScrollBar
    extends JScrollBar
    implements UIResource
  {
    private boolean unitIncrementSet;
    private boolean blockIncrementSet;
    
    public ScrollBar(int paramInt)
    {
      super();
      putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
    }
    
    public void setUnitIncrement(int paramInt)
    {
      unitIncrementSet = true;
      putClientProperty("JScrollBar.fastWheelScrolling", null);
      super.setUnitIncrement(paramInt);
    }
    
    public int getUnitIncrement(int paramInt)
    {
      JViewport localJViewport = getViewport();
      if ((!unitIncrementSet) && (localJViewport != null) && ((localJViewport.getView() instanceof Scrollable)))
      {
        Scrollable localScrollable = (Scrollable)localJViewport.getView();
        Rectangle localRectangle = localJViewport.getViewRect();
        return localScrollable.getScrollableUnitIncrement(localRectangle, getOrientation(), paramInt);
      }
      return super.getUnitIncrement(paramInt);
    }
    
    public void setBlockIncrement(int paramInt)
    {
      blockIncrementSet = true;
      putClientProperty("JScrollBar.fastWheelScrolling", null);
      super.setBlockIncrement(paramInt);
    }
    
    public int getBlockIncrement(int paramInt)
    {
      JViewport localJViewport = getViewport();
      if ((blockIncrementSet) || (localJViewport == null)) {
        return super.getBlockIncrement(paramInt);
      }
      if ((localJViewport.getView() instanceof Scrollable))
      {
        Scrollable localScrollable = (Scrollable)localJViewport.getView();
        Rectangle localRectangle = localJViewport.getViewRect();
        return localScrollable.getScrollableBlockIncrement(localRectangle, getOrientation(), paramInt);
      }
      if (getOrientation() == 1) {
        return getExtentSizeheight;
      }
      return getExtentSizewidth;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JScrollPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */