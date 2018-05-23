package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.UIResource;

public class JToolBar
  extends JComponent
  implements SwingConstants, Accessible
{
  private static final String uiClassID = "ToolBarUI";
  private boolean paintBorder = true;
  private Insets margin = null;
  private boolean floatable = true;
  private int orientation = 0;
  
  public JToolBar()
  {
    this(0);
  }
  
  public JToolBar(int paramInt)
  {
    this(null, paramInt);
  }
  
  public JToolBar(String paramString)
  {
    this(paramString, 0);
  }
  
  public JToolBar(String paramString, int paramInt)
  {
    setName(paramString);
    checkOrientation(paramInt);
    orientation = paramInt;
    DefaultToolBarLayout localDefaultToolBarLayout = new DefaultToolBarLayout(paramInt);
    setLayout(localDefaultToolBarLayout);
    addPropertyChangeListener(localDefaultToolBarLayout);
    updateUI();
  }
  
  public ToolBarUI getUI()
  {
    return (ToolBarUI)ui;
  }
  
  public void setUI(ToolBarUI paramToolBarUI)
  {
    super.setUI(paramToolBarUI);
  }
  
  public void updateUI()
  {
    setUI((ToolBarUI)UIManager.getUI(this));
    if (getLayout() == null) {
      setLayout(new DefaultToolBarLayout(getOrientation()));
    }
    invalidate();
  }
  
  public String getUIClassID()
  {
    return "ToolBarUI";
  }
  
  public int getComponentIndex(Component paramComponent)
  {
    int i = getComponentCount();
    Component[] arrayOfComponent = getComponents();
    for (int j = 0; j < i; j++)
    {
      Component localComponent = arrayOfComponent[j];
      if (localComponent == paramComponent) {
        return j;
      }
    }
    return -1;
  }
  
  public Component getComponentAtIndex(int paramInt)
  {
    int i = getComponentCount();
    if ((paramInt >= 0) && (paramInt < i))
    {
      Component[] arrayOfComponent = getComponents();
      return arrayOfComponent[paramInt];
    }
    return null;
  }
  
  public void setMargin(Insets paramInsets)
  {
    Insets localInsets = margin;
    margin = paramInsets;
    firePropertyChange("margin", localInsets, paramInsets);
    revalidate();
    repaint();
  }
  
  public Insets getMargin()
  {
    if (margin == null) {
      return new Insets(0, 0, 0, 0);
    }
    return margin;
  }
  
  public boolean isBorderPainted()
  {
    return paintBorder;
  }
  
  public void setBorderPainted(boolean paramBoolean)
  {
    if (paintBorder != paramBoolean)
    {
      boolean bool = paintBorder;
      paintBorder = paramBoolean;
      firePropertyChange("borderPainted", bool, paramBoolean);
      revalidate();
      repaint();
    }
  }
  
  protected void paintBorder(Graphics paramGraphics)
  {
    if (isBorderPainted()) {
      super.paintBorder(paramGraphics);
    }
  }
  
  public boolean isFloatable()
  {
    return floatable;
  }
  
  public void setFloatable(boolean paramBoolean)
  {
    if (floatable != paramBoolean)
    {
      boolean bool = floatable;
      floatable = paramBoolean;
      firePropertyChange("floatable", bool, paramBoolean);
      revalidate();
      repaint();
    }
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    checkOrientation(paramInt);
    if (orientation != paramInt)
    {
      int i = orientation;
      orientation = paramInt;
      firePropertyChange("orientation", i, paramInt);
      revalidate();
      repaint();
    }
  }
  
  public void setRollover(boolean paramBoolean)
  {
    putClientProperty("JToolBar.isRollover", paramBoolean ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public boolean isRollover()
  {
    Boolean localBoolean = (Boolean)getClientProperty("JToolBar.isRollover");
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return false;
  }
  
  private void checkOrientation(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
      break;
    default: 
      throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
    }
  }
  
  public void addSeparator()
  {
    addSeparator(null);
  }
  
  public void addSeparator(Dimension paramDimension)
  {
    Separator localSeparator = new Separator(paramDimension);
    add(localSeparator);
  }
  
  public JButton add(Action paramAction)
  {
    JButton localJButton = createActionComponent(paramAction);
    localJButton.setAction(paramAction);
    add(localJButton);
    return localJButton;
  }
  
  protected JButton createActionComponent(Action paramAction)
  {
    JButton local1 = new JButton()
    {
      protected PropertyChangeListener createActionPropertyChangeListener(Action paramAnonymousAction)
      {
        PropertyChangeListener localPropertyChangeListener = createActionChangeListener(this);
        if (localPropertyChangeListener == null) {
          localPropertyChangeListener = super.createActionPropertyChangeListener(paramAnonymousAction);
        }
        return localPropertyChangeListener;
      }
    };
    if ((paramAction != null) && ((paramAction.getValue("SmallIcon") != null) || (paramAction.getValue("SwingLargeIconKey") != null))) {
      local1.setHideActionText(true);
    }
    local1.setHorizontalTextPosition(0);
    local1.setVerticalTextPosition(3);
    return local1;
  }
  
  protected PropertyChangeListener createActionChangeListener(JButton paramJButton)
  {
    return null;
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    if ((paramComponent instanceof Separator)) {
      if (getOrientation() == 1) {
        ((Separator)paramComponent).setOrientation(0);
      } else {
        ((Separator)paramComponent).setOrientation(1);
      }
    }
    super.addImpl(paramComponent, paramObject, paramInt);
    if ((paramComponent instanceof JButton)) {
      ((JButton)paramComponent).setDefaultCapable(false);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ToolBarUI"))
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
    String str1 = paintBorder ? "true" : "false";
    String str2 = margin != null ? margin.toString() : "";
    String str3 = floatable ? "true" : "false";
    String str4 = orientation == 0 ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",floatable=" + str3 + ",margin=" + str2 + ",orientation=" + str4 + ",paintBorder=" + str1;
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    LayoutManager localLayoutManager = getLayout();
    if ((localLayoutManager instanceof PropertyChangeListener)) {
      removePropertyChangeListener((PropertyChangeListener)localLayoutManager);
    }
    super.setLayout(paramLayoutManager);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJToolBar();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJToolBar
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJToolBar()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TOOL_BAR;
    }
  }
  
  private class DefaultToolBarLayout
    implements LayoutManager2, Serializable, PropertyChangeListener, UIResource
  {
    BoxLayout lm;
    
    DefaultToolBarLayout(int paramInt)
    {
      if (paramInt == 1) {
        lm = new BoxLayout(JToolBar.this, 3);
      } else {
        lm = new BoxLayout(JToolBar.this, 2);
      }
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      lm.addLayoutComponent(paramString, paramComponent);
    }
    
    public void addLayoutComponent(Component paramComponent, Object paramObject)
    {
      lm.addLayoutComponent(paramComponent, paramObject);
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      lm.removeLayoutComponent(paramComponent);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return lm.preferredLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return lm.minimumLayoutSize(paramContainer);
    }
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      return lm.maximumLayoutSize(paramContainer);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      lm.layoutContainer(paramContainer);
    }
    
    public float getLayoutAlignmentX(Container paramContainer)
    {
      return lm.getLayoutAlignmentX(paramContainer);
    }
    
    public float getLayoutAlignmentY(Container paramContainer)
    {
      return lm.getLayoutAlignmentY(paramContainer);
    }
    
    public void invalidateLayout(Container paramContainer)
    {
      lm.invalidateLayout(paramContainer);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str.equals("orientation"))
      {
        int i = ((Integer)paramPropertyChangeEvent.getNewValue()).intValue();
        if (i == 1) {
          lm = new BoxLayout(JToolBar.this, 3);
        } else {
          lm = new BoxLayout(JToolBar.this, 2);
        }
      }
    }
  }
  
  public static class Separator
    extends JSeparator
  {
    private Dimension separatorSize;
    
    public Separator()
    {
      this(null);
    }
    
    public Separator(Dimension paramDimension)
    {
      super();
      setSeparatorSize(paramDimension);
    }
    
    public String getUIClassID()
    {
      return "ToolBarSeparatorUI";
    }
    
    public void setSeparatorSize(Dimension paramDimension)
    {
      if (paramDimension != null) {
        separatorSize = paramDimension;
      } else {
        super.updateUI();
      }
      invalidate();
    }
    
    public Dimension getSeparatorSize()
    {
      return separatorSize;
    }
    
    public Dimension getMinimumSize()
    {
      if (separatorSize != null) {
        return separatorSize.getSize();
      }
      return super.getMinimumSize();
    }
    
    public Dimension getMaximumSize()
    {
      if (separatorSize != null) {
        return separatorSize.getSize();
      }
      return super.getMaximumSize();
    }
    
    public Dimension getPreferredSize()
    {
      if (separatorSize != null) {
        return separatorSize.getSize();
      }
      return super.getPreferredSize();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JToolBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */