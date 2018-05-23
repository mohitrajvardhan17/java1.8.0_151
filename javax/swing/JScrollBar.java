package javax.swing;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

public class JScrollBar
  extends JComponent
  implements Adjustable, Accessible
{
  private static final String uiClassID = "ScrollBarUI";
  private ChangeListener fwdAdjustmentEvents = new ModelListener(null);
  protected BoundedRangeModel model;
  protected int orientation;
  protected int unitIncrement;
  protected int blockIncrement;
  
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
  
  public JScrollBar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    checkOrientation(paramInt1);
    unitIncrement = 1;
    blockIncrement = (paramInt3 == 0 ? 1 : paramInt3);
    orientation = paramInt1;
    model = new DefaultBoundedRangeModel(paramInt2, paramInt3, paramInt4, paramInt5);
    model.addChangeListener(fwdAdjustmentEvents);
    setRequestFocusEnabled(false);
    updateUI();
  }
  
  public JScrollBar(int paramInt)
  {
    this(paramInt, 0, 10, 0, 100);
  }
  
  public JScrollBar()
  {
    this(1);
  }
  
  public void setUI(ScrollBarUI paramScrollBarUI)
  {
    super.setUI(paramScrollBarUI);
  }
  
  public ScrollBarUI getUI()
  {
    return (ScrollBarUI)ui;
  }
  
  public void updateUI()
  {
    setUI((ScrollBarUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ScrollBarUI";
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    checkOrientation(paramInt);
    int i = orientation;
    orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
    if ((i != paramInt) && (accessibleContext != null)) {
      accessibleContext.firePropertyChange("AccessibleState", i == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, paramInt == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
    }
    if (paramInt != i) {
      revalidate();
    }
  }
  
  public BoundedRangeModel getModel()
  {
    return model;
  }
  
  public void setModel(BoundedRangeModel paramBoundedRangeModel)
  {
    Integer localInteger = null;
    BoundedRangeModel localBoundedRangeModel = model;
    if (model != null)
    {
      model.removeChangeListener(fwdAdjustmentEvents);
      localInteger = Integer.valueOf(model.getValue());
    }
    model = paramBoundedRangeModel;
    if (model != null) {
      model.addChangeListener(fwdAdjustmentEvents);
    }
    firePropertyChange("model", localBoundedRangeModel, model);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleValue", localInteger, new Integer(model.getValue()));
    }
  }
  
  public int getUnitIncrement(int paramInt)
  {
    return unitIncrement;
  }
  
  public void setUnitIncrement(int paramInt)
  {
    int i = unitIncrement;
    unitIncrement = paramInt;
    firePropertyChange("unitIncrement", i, paramInt);
  }
  
  public int getBlockIncrement(int paramInt)
  {
    return blockIncrement;
  }
  
  public void setBlockIncrement(int paramInt)
  {
    int i = blockIncrement;
    blockIncrement = paramInt;
    firePropertyChange("blockIncrement", i, paramInt);
  }
  
  public int getUnitIncrement()
  {
    return unitIncrement;
  }
  
  public int getBlockIncrement()
  {
    return blockIncrement;
  }
  
  public int getValue()
  {
    return getModel().getValue();
  }
  
  public void setValue(int paramInt)
  {
    BoundedRangeModel localBoundedRangeModel = getModel();
    int i = localBoundedRangeModel.getValue();
    localBoundedRangeModel.setValue(paramInt);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(localBoundedRangeModel.getValue()));
    }
  }
  
  public int getVisibleAmount()
  {
    return getModel().getExtent();
  }
  
  public void setVisibleAmount(int paramInt)
  {
    getModel().setExtent(paramInt);
  }
  
  public int getMinimum()
  {
    return getModel().getMinimum();
  }
  
  public void setMinimum(int paramInt)
  {
    getModel().setMinimum(paramInt);
  }
  
  public int getMaximum()
  {
    return getModel().getMaximum();
  }
  
  public void setMaximum(int paramInt)
  {
    getModel().setMaximum(paramInt);
  }
  
  public boolean getValueIsAdjusting()
  {
    return getModel().getValueIsAdjusting();
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    BoundedRangeModel localBoundedRangeModel = getModel();
    boolean bool = localBoundedRangeModel.getValueIsAdjusting();
    localBoundedRangeModel.setValueIsAdjusting(paramBoolean);
    if ((bool != paramBoolean) && (accessibleContext != null)) {
      accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.BUSY : null, paramBoolean ? AccessibleState.BUSY : null);
    }
  }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    BoundedRangeModel localBoundedRangeModel = getModel();
    int i = localBoundedRangeModel.getValue();
    localBoundedRangeModel.setRangeProperties(paramInt1, paramInt2, paramInt3, paramInt4, localBoundedRangeModel.getValueIsAdjusting());
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(localBoundedRangeModel.getValue()));
    }
  }
  
  public void addAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    listenerList.add(AdjustmentListener.class, paramAdjustmentListener);
  }
  
  public void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    listenerList.remove(AdjustmentListener.class, paramAdjustmentListener);
  }
  
  public AdjustmentListener[] getAdjustmentListeners()
  {
    return (AdjustmentListener[])listenerList.getListeners(AdjustmentListener.class);
  }
  
  protected void fireAdjustmentValueChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    fireAdjustmentValueChanged(paramInt1, paramInt2, paramInt3, getValueIsAdjusting());
  }
  
  private void fireAdjustmentValueChanged(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    AdjustmentEvent localAdjustmentEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AdjustmentListener.class)
      {
        if (localAdjustmentEvent == null) {
          localAdjustmentEvent = new AdjustmentEvent(this, paramInt1, paramInt2, paramInt3, paramBoolean);
        }
        ((AdjustmentListener)arrayOfObject[(i + 1)]).adjustmentValueChanged(localAdjustmentEvent);
      }
    }
  }
  
  public Dimension getMinimumSize()
  {
    Dimension localDimension = getPreferredSize();
    if (orientation == 1) {
      return new Dimension(width, 5);
    }
    return new Dimension(5, height);
  }
  
  public Dimension getMaximumSize()
  {
    Dimension localDimension = getPreferredSize();
    if (getOrientation() == 1) {
      return new Dimension(width, 32767);
    }
    return new Dimension(32767, height);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    Component[] arrayOfComponent1 = getComponents();
    for (Component localComponent : arrayOfComponent1) {
      localComponent.setEnabled(paramBoolean);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ScrollBarUI"))
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
    String str = orientation == 0 ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",blockIncrement=" + blockIncrement + ",orientation=" + str + ",unitIncrement=" + unitIncrement;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJScrollBar();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJScrollBar
    extends JComponent.AccessibleJComponent
    implements AccessibleValue
  {
    protected AccessibleJScrollBar()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getValueIsAdjusting()) {
        localAccessibleStateSet.add(AccessibleState.BUSY);
      }
      if (getOrientation() == 1) {
        localAccessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        localAccessibleStateSet.add(AccessibleState.HORIZONTAL);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SCROLL_BAR;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return Integer.valueOf(getValue());
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      if (paramNumber == null) {
        return false;
      }
      setValue(paramNumber.intValue());
      return true;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(getMinimum());
    }
    
    public Number getMaximumAccessibleValue()
    {
      return new Integer(model.getMaximum() - model.getExtent());
    }
  }
  
  private class ModelListener
    implements ChangeListener, Serializable
  {
    private ModelListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      Object localObject = paramChangeEvent.getSource();
      if ((localObject instanceof BoundedRangeModel))
      {
        int i = 601;
        int j = 5;
        BoundedRangeModel localBoundedRangeModel = (BoundedRangeModel)localObject;
        int k = localBoundedRangeModel.getValue();
        boolean bool = localBoundedRangeModel.getValueIsAdjusting();
        JScrollBar.this.fireAdjustmentValueChanged(i, j, k, bool);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JScrollBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */