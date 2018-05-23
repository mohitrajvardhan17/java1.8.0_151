package javax.swing;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.NumberFormat;
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
import javax.swing.plaf.ProgressBarUI;

public class JProgressBar
  extends JComponent
  implements SwingConstants, Accessible
{
  private static final String uiClassID = "ProgressBarUI";
  protected int orientation;
  protected boolean paintBorder;
  protected BoundedRangeModel model;
  protected String progressString;
  protected boolean paintString;
  private static final int defaultMinimum = 0;
  private static final int defaultMaximum = 100;
  private static final int defaultOrientation = 0;
  protected transient ChangeEvent changeEvent = null;
  protected ChangeListener changeListener = null;
  private transient Format format;
  private boolean indeterminate;
  
  public JProgressBar()
  {
    this(0);
  }
  
  public JProgressBar(int paramInt)
  {
    this(paramInt, 0, 100);
  }
  
  public JProgressBar(int paramInt1, int paramInt2)
  {
    this(0, paramInt1, paramInt2);
  }
  
  public JProgressBar(int paramInt1, int paramInt2, int paramInt3)
  {
    setModel(new DefaultBoundedRangeModel(paramInt2, 0, paramInt2, paramInt3));
    updateUI();
    setOrientation(paramInt1);
    setBorderPainted(true);
    setStringPainted(false);
    setString(null);
    setIndeterminate(false);
  }
  
  public JProgressBar(BoundedRangeModel paramBoundedRangeModel)
  {
    setModel(paramBoundedRangeModel);
    updateUI();
    setOrientation(0);
    setBorderPainted(true);
    setStringPainted(false);
    setString(null);
    setIndeterminate(false);
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    if (orientation != paramInt)
    {
      switch (paramInt)
      {
      case 0: 
      case 1: 
        int i = orientation;
        orientation = paramInt;
        firePropertyChange("orientation", i, paramInt);
        if (accessibleContext != null) {
          accessibleContext.firePropertyChange("AccessibleState", i == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, orientation == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
        }
        break;
      default: 
        throw new IllegalArgumentException(paramInt + " is not a legal orientation");
      }
      revalidate();
    }
  }
  
  public boolean isStringPainted()
  {
    return paintString;
  }
  
  public void setStringPainted(boolean paramBoolean)
  {
    boolean bool = paintString;
    paintString = paramBoolean;
    firePropertyChange("stringPainted", bool, paintString);
    if (paintString != bool)
    {
      revalidate();
      repaint();
    }
  }
  
  public String getString()
  {
    if (progressString != null) {
      return progressString;
    }
    if (format == null) {
      format = NumberFormat.getPercentInstance();
    }
    return format.format(new Double(getPercentComplete()));
  }
  
  public void setString(String paramString)
  {
    String str = progressString;
    progressString = paramString;
    firePropertyChange("string", str, progressString);
    if ((progressString == null) || (str == null) || (!progressString.equals(str))) {
      repaint();
    }
  }
  
  public double getPercentComplete()
  {
    long l = model.getMaximum() - model.getMinimum();
    double d1 = model.getValue();
    double d2 = (d1 - model.getMinimum()) / l;
    return d2;
  }
  
  public boolean isBorderPainted()
  {
    return paintBorder;
  }
  
  public void setBorderPainted(boolean paramBoolean)
  {
    boolean bool = paintBorder;
    paintBorder = paramBoolean;
    firePropertyChange("borderPainted", bool, paintBorder);
    if (paintBorder != bool) {
      repaint();
    }
  }
  
  protected void paintBorder(Graphics paramGraphics)
  {
    if (isBorderPainted()) {
      super.paintBorder(paramGraphics);
    }
  }
  
  public ProgressBarUI getUI()
  {
    return (ProgressBarUI)ui;
  }
  
  public void setUI(ProgressBarUI paramProgressBarUI)
  {
    super.setUI(paramProgressBarUI);
  }
  
  public void updateUI()
  {
    setUI((ProgressBarUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ProgressBarUI";
  }
  
  protected ChangeListener createChangeListener()
  {
    return new ModelListener(null);
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(changeEvent);
      }
    }
  }
  
  public BoundedRangeModel getModel()
  {
    return model;
  }
  
  public void setModel(BoundedRangeModel paramBoundedRangeModel)
  {
    BoundedRangeModel localBoundedRangeModel = getModel();
    if (paramBoundedRangeModel != localBoundedRangeModel)
    {
      if (localBoundedRangeModel != null)
      {
        localBoundedRangeModel.removeChangeListener(changeListener);
        changeListener = null;
      }
      model = paramBoundedRangeModel;
      if (paramBoundedRangeModel != null)
      {
        changeListener = createChangeListener();
        paramBoundedRangeModel.addChangeListener(changeListener);
      }
      if (accessibleContext != null) {
        accessibleContext.firePropertyChange("AccessibleValue", localBoundedRangeModel == null ? null : Integer.valueOf(localBoundedRangeModel.getValue()), paramBoundedRangeModel == null ? null : Integer.valueOf(paramBoundedRangeModel.getValue()));
      }
      if (model != null) {
        model.setExtent(0);
      }
      repaint();
    }
  }
  
  public int getValue()
  {
    return getModel().getValue();
  }
  
  public int getMinimum()
  {
    return getModel().getMinimum();
  }
  
  public int getMaximum()
  {
    return getModel().getMaximum();
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
  
  public void setMinimum(int paramInt)
  {
    getModel().setMinimum(paramInt);
  }
  
  public void setMaximum(int paramInt)
  {
    getModel().setMaximum(paramInt);
  }
  
  public void setIndeterminate(boolean paramBoolean)
  {
    boolean bool = indeterminate;
    indeterminate = paramBoolean;
    firePropertyChange("indeterminate", bool, indeterminate);
  }
  
  public boolean isIndeterminate()
  {
    return indeterminate;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ProgressBarUI"))
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
    String str1 = orientation == 0 ? "HORIZONTAL" : "VERTICAL";
    String str2 = paintBorder ? "true" : "false";
    String str3 = progressString != null ? progressString : "";
    String str4 = paintString ? "true" : "false";
    String str5 = indeterminate ? "true" : "false";
    return super.paramString() + ",orientation=" + str1 + ",paintBorder=" + str2 + ",paintString=" + str4 + ",progressString=" + str3 + ",indeterminateString=" + str5;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJProgressBar();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJProgressBar
    extends JComponent.AccessibleJComponent
    implements AccessibleValue
  {
    protected AccessibleJProgressBar()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getModel().getValueIsAdjusting()) {
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
      return AccessibleRole.PROGRESS_BAR;
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
      return Integer.valueOf(model.getMaximum() - model.getExtent());
    }
  }
  
  private class ModelListener
    implements ChangeListener, Serializable
  {
    private ModelListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      fireStateChanged();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JProgressBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */