package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;

public class JSplitPane
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "SplitPaneUI";
  public static final int VERTICAL_SPLIT = 0;
  public static final int HORIZONTAL_SPLIT = 1;
  public static final String LEFT = "left";
  public static final String RIGHT = "right";
  public static final String TOP = "top";
  public static final String BOTTOM = "bottom";
  public static final String DIVIDER = "divider";
  public static final String ORIENTATION_PROPERTY = "orientation";
  public static final String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";
  public static final String DIVIDER_SIZE_PROPERTY = "dividerSize";
  public static final String ONE_TOUCH_EXPANDABLE_PROPERTY = "oneTouchExpandable";
  public static final String LAST_DIVIDER_LOCATION_PROPERTY = "lastDividerLocation";
  public static final String DIVIDER_LOCATION_PROPERTY = "dividerLocation";
  public static final String RESIZE_WEIGHT_PROPERTY = "resizeWeight";
  protected int orientation;
  protected boolean continuousLayout;
  protected Component leftComponent;
  protected Component rightComponent;
  protected int dividerSize;
  private boolean dividerSizeSet = false;
  protected boolean oneTouchExpandable;
  private boolean oneTouchExpandableSet;
  protected int lastDividerLocation;
  private double resizeWeight;
  private int dividerLocation = -1;
  
  public JSplitPane()
  {
    this(1, UIManager.getBoolean("SplitPane.continuousLayout"), new JButton(UIManager.getString("SplitPane.leftButtonText")), new JButton(UIManager.getString("SplitPane.rightButtonText")));
  }
  
  @ConstructorProperties({"orientation"})
  public JSplitPane(int paramInt)
  {
    this(paramInt, UIManager.getBoolean("SplitPane.continuousLayout"));
  }
  
  public JSplitPane(int paramInt, boolean paramBoolean)
  {
    this(paramInt, paramBoolean, null, null);
  }
  
  public JSplitPane(int paramInt, Component paramComponent1, Component paramComponent2)
  {
    this(paramInt, UIManager.getBoolean("SplitPane.continuousLayout"), paramComponent1, paramComponent2);
  }
  
  public JSplitPane(int paramInt, boolean paramBoolean, Component paramComponent1, Component paramComponent2)
  {
    setLayout(null);
    setUIProperty("opaque", Boolean.TRUE);
    orientation = paramInt;
    if ((orientation != 1) && (orientation != 0)) {
      throw new IllegalArgumentException("cannot create JSplitPane, orientation must be one of JSplitPane.HORIZONTAL_SPLIT or JSplitPane.VERTICAL_SPLIT");
    }
    continuousLayout = paramBoolean;
    if (paramComponent1 != null) {
      setLeftComponent(paramComponent1);
    }
    if (paramComponent2 != null) {
      setRightComponent(paramComponent2);
    }
    updateUI();
  }
  
  public void setUI(SplitPaneUI paramSplitPaneUI)
  {
    if ((SplitPaneUI)ui != paramSplitPaneUI)
    {
      super.setUI(paramSplitPaneUI);
      revalidate();
    }
  }
  
  public SplitPaneUI getUI()
  {
    return (SplitPaneUI)ui;
  }
  
  public void updateUI()
  {
    setUI((SplitPaneUI)UIManager.getUI(this));
    revalidate();
  }
  
  public String getUIClassID()
  {
    return "SplitPaneUI";
  }
  
  public void setDividerSize(int paramInt)
  {
    int i = dividerSize;
    dividerSizeSet = true;
    if (i != paramInt)
    {
      dividerSize = paramInt;
      firePropertyChange("dividerSize", i, paramInt);
    }
  }
  
  public int getDividerSize()
  {
    return dividerSize;
  }
  
  public void setLeftComponent(Component paramComponent)
  {
    if (paramComponent == null)
    {
      if (leftComponent != null)
      {
        remove(leftComponent);
        leftComponent = null;
      }
    }
    else {
      add(paramComponent, "left");
    }
  }
  
  public Component getLeftComponent()
  {
    return leftComponent;
  }
  
  public void setTopComponent(Component paramComponent)
  {
    setLeftComponent(paramComponent);
  }
  
  public Component getTopComponent()
  {
    return leftComponent;
  }
  
  public void setRightComponent(Component paramComponent)
  {
    if (paramComponent == null)
    {
      if (rightComponent != null)
      {
        remove(rightComponent);
        rightComponent = null;
      }
    }
    else {
      add(paramComponent, "right");
    }
  }
  
  public Component getRightComponent()
  {
    return rightComponent;
  }
  
  public void setBottomComponent(Component paramComponent)
  {
    setRightComponent(paramComponent);
  }
  
  public Component getBottomComponent()
  {
    return rightComponent;
  }
  
  public void setOneTouchExpandable(boolean paramBoolean)
  {
    boolean bool = oneTouchExpandable;
    oneTouchExpandable = paramBoolean;
    oneTouchExpandableSet = true;
    firePropertyChange("oneTouchExpandable", bool, paramBoolean);
    repaint();
  }
  
  public boolean isOneTouchExpandable()
  {
    return oneTouchExpandable;
  }
  
  public void setLastDividerLocation(int paramInt)
  {
    int i = lastDividerLocation;
    lastDividerLocation = paramInt;
    firePropertyChange("lastDividerLocation", i, paramInt);
  }
  
  public int getLastDividerLocation()
  {
    return lastDividerLocation;
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("JSplitPane: orientation must be one of JSplitPane.VERTICAL_SPLIT or JSplitPane.HORIZONTAL_SPLIT");
    }
    int i = orientation;
    orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setContinuousLayout(boolean paramBoolean)
  {
    boolean bool = continuousLayout;
    continuousLayout = paramBoolean;
    firePropertyChange("continuousLayout", bool, paramBoolean);
  }
  
  public boolean isContinuousLayout()
  {
    return continuousLayout;
  }
  
  public void setResizeWeight(double paramDouble)
  {
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException("JSplitPane weight must be between 0 and 1");
    }
    double d = resizeWeight;
    resizeWeight = paramDouble;
    firePropertyChange("resizeWeight", d, paramDouble);
  }
  
  public double getResizeWeight()
  {
    return resizeWeight;
  }
  
  public void resetToPreferredSizes()
  {
    SplitPaneUI localSplitPaneUI = getUI();
    if (localSplitPaneUI != null) {
      localSplitPaneUI.resetToPreferredSizes(this);
    }
  }
  
  public void setDividerLocation(double paramDouble)
  {
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException("proportional location must be between 0.0 and 1.0.");
    }
    if (getOrientation() == 0) {
      setDividerLocation((int)((getHeight() - getDividerSize()) * paramDouble));
    } else {
      setDividerLocation((int)((getWidth() - getDividerSize()) * paramDouble));
    }
  }
  
  public void setDividerLocation(int paramInt)
  {
    int i = dividerLocation;
    dividerLocation = paramInt;
    SplitPaneUI localSplitPaneUI = getUI();
    if (localSplitPaneUI != null) {
      localSplitPaneUI.setDividerLocation(this, paramInt);
    }
    firePropertyChange("dividerLocation", i, paramInt);
    setLastDividerLocation(i);
  }
  
  public int getDividerLocation()
  {
    return dividerLocation;
  }
  
  public int getMinimumDividerLocation()
  {
    SplitPaneUI localSplitPaneUI = getUI();
    if (localSplitPaneUI != null) {
      return localSplitPaneUI.getMinimumDividerLocation(this);
    }
    return -1;
  }
  
  public int getMaximumDividerLocation()
  {
    SplitPaneUI localSplitPaneUI = getUI();
    if (localSplitPaneUI != null) {
      return localSplitPaneUI.getMaximumDividerLocation(this);
    }
    return -1;
  }
  
  public void remove(Component paramComponent)
  {
    if (paramComponent == leftComponent) {
      leftComponent = null;
    } else if (paramComponent == rightComponent) {
      rightComponent = null;
    }
    super.remove(paramComponent);
    revalidate();
    repaint();
  }
  
  public void remove(int paramInt)
  {
    Component localComponent = getComponent(paramInt);
    if (localComponent == leftComponent) {
      leftComponent = null;
    } else if (localComponent == rightComponent) {
      rightComponent = null;
    }
    super.remove(paramInt);
    revalidate();
    repaint();
  }
  
  public void removeAll()
  {
    leftComponent = (rightComponent = null);
    super.removeAll();
    revalidate();
    repaint();
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    if ((paramObject != null) && (!(paramObject instanceof String))) {
      throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
    }
    if (paramObject == null) {
      if (getLeftComponent() == null) {
        paramObject = "left";
      } else if (getRightComponent() == null) {
        paramObject = "right";
      }
    }
    Component localComponent;
    if ((paramObject != null) && ((paramObject.equals("left")) || (paramObject.equals("top"))))
    {
      localComponent = getLeftComponent();
      if (localComponent != null) {
        remove(localComponent);
      }
      leftComponent = paramComponent;
      paramInt = -1;
    }
    else if ((paramObject != null) && ((paramObject.equals("right")) || (paramObject.equals("bottom"))))
    {
      localComponent = getRightComponent();
      if (localComponent != null) {
        remove(localComponent);
      }
      rightComponent = paramComponent;
      paramInt = -1;
    }
    else if ((paramObject != null) && (paramObject.equals("divider")))
    {
      paramInt = -1;
    }
    super.addImpl(paramComponent, paramObject, paramInt);
    revalidate();
    repaint();
  }
  
  protected void paintChildren(Graphics paramGraphics)
  {
    super.paintChildren(paramGraphics);
    SplitPaneUI localSplitPaneUI = getUI();
    if (localSplitPaneUI != null)
    {
      Graphics localGraphics = paramGraphics.create();
      localSplitPaneUI.finishedPaintingChildren(this, localGraphics);
      localGraphics.dispose();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SplitPaneUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  void setUIProperty(String paramString, Object paramObject)
  {
    if (paramString == "dividerSize")
    {
      if (!dividerSizeSet)
      {
        setDividerSize(((Number)paramObject).intValue());
        dividerSizeSet = false;
      }
    }
    else if (paramString == "oneTouchExpandable")
    {
      if (!oneTouchExpandableSet)
      {
        setOneTouchExpandable(((Boolean)paramObject).booleanValue());
        oneTouchExpandableSet = false;
      }
    }
    else {
      super.setUIProperty(paramString, paramObject);
    }
  }
  
  protected String paramString()
  {
    String str1 = orientation == 1 ? "HORIZONTAL_SPLIT" : "VERTICAL_SPLIT";
    String str2 = continuousLayout ? "true" : "false";
    String str3 = oneTouchExpandable ? "true" : "false";
    return super.paramString() + ",continuousLayout=" + str2 + ",dividerSize=" + dividerSize + ",lastDividerLocation=" + lastDividerLocation + ",oneTouchExpandable=" + str3 + ",orientation=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJSplitPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJSplitPane
    extends JComponent.AccessibleJComponent
    implements AccessibleValue
  {
    protected AccessibleJSplitPane()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getOrientation() == 0) {
        localAccessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        localAccessibleStateSet.add(AccessibleState.HORIZONTAL);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return Integer.valueOf(getDividerLocation());
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      if (paramNumber == null) {
        return false;
      }
      setDividerLocation(paramNumber.intValue());
      return true;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(getUI().getMinimumDividerLocation(JSplitPane.this));
    }
    
    public Number getMaximumAccessibleValue()
    {
      return Integer.valueOf(getUI().getMaximumDividerLocation(JSplitPane.this));
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SPLIT_PANE;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JSplitPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */