package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
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
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.UIResource;

public class JSlider
  extends JComponent
  implements SwingConstants, Accessible
{
  private static final String uiClassID = "SliderUI";
  private boolean paintTicks = false;
  private boolean paintTrack = true;
  private boolean paintLabels = false;
  private boolean isInverted = false;
  protected BoundedRangeModel sliderModel;
  protected int majorTickSpacing;
  protected int minorTickSpacing;
  protected boolean snapToTicks = false;
  boolean snapToValue = true;
  protected int orientation;
  private Dictionary labelTable;
  protected ChangeListener changeListener = createChangeListener();
  protected transient ChangeEvent changeEvent = null;
  
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
  
  public JSlider()
  {
    this(0, 0, 100, 50);
  }
  
  public JSlider(int paramInt)
  {
    this(paramInt, 0, 100, 50);
  }
  
  public JSlider(int paramInt1, int paramInt2)
  {
    this(0, paramInt1, paramInt2, (paramInt1 + paramInt2) / 2);
  }
  
  public JSlider(int paramInt1, int paramInt2, int paramInt3)
  {
    this(0, paramInt1, paramInt2, paramInt3);
  }
  
  public JSlider(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    checkOrientation(paramInt1);
    orientation = paramInt1;
    setModel(new DefaultBoundedRangeModel(paramInt4, 0, paramInt2, paramInt3));
    updateUI();
  }
  
  public JSlider(BoundedRangeModel paramBoundedRangeModel)
  {
    orientation = 0;
    setModel(paramBoundedRangeModel);
    updateUI();
  }
  
  public SliderUI getUI()
  {
    return (SliderUI)ui;
  }
  
  public void setUI(SliderUI paramSliderUI)
  {
    super.setUI(paramSliderUI);
  }
  
  public void updateUI()
  {
    setUI((SliderUI)UIManager.getUI(this));
    updateLabelUIs();
  }
  
  public String getUIClassID()
  {
    return "SliderUI";
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
    return sliderModel;
  }
  
  public void setModel(BoundedRangeModel paramBoundedRangeModel)
  {
    BoundedRangeModel localBoundedRangeModel = getModel();
    if (localBoundedRangeModel != null) {
      localBoundedRangeModel.removeChangeListener(changeListener);
    }
    sliderModel = paramBoundedRangeModel;
    if (paramBoundedRangeModel != null) {
      paramBoundedRangeModel.addChangeListener(changeListener);
    }
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleValue", localBoundedRangeModel == null ? null : Integer.valueOf(localBoundedRangeModel.getValue()), paramBoundedRangeModel == null ? null : Integer.valueOf(paramBoundedRangeModel.getValue()));
    }
    firePropertyChange("model", localBoundedRangeModel, sliderModel);
  }
  
  public int getValue()
  {
    return getModel().getValue();
  }
  
  public void setValue(int paramInt)
  {
    BoundedRangeModel localBoundedRangeModel = getModel();
    int i = localBoundedRangeModel.getValue();
    if (i == paramInt) {
      return;
    }
    localBoundedRangeModel.setValue(paramInt);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(localBoundedRangeModel.getValue()));
    }
  }
  
  public int getMinimum()
  {
    return getModel().getMinimum();
  }
  
  public void setMinimum(int paramInt)
  {
    int i = getModel().getMinimum();
    getModel().setMinimum(paramInt);
    firePropertyChange("minimum", Integer.valueOf(i), Integer.valueOf(paramInt));
  }
  
  public int getMaximum()
  {
    return getModel().getMaximum();
  }
  
  public void setMaximum(int paramInt)
  {
    int i = getModel().getMaximum();
    getModel().setMaximum(paramInt);
    firePropertyChange("maximum", Integer.valueOf(i), Integer.valueOf(paramInt));
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
  
  public int getExtent()
  {
    return getModel().getExtent();
  }
  
  public void setExtent(int paramInt)
  {
    getModel().setExtent(paramInt);
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
  
  public void setFont(Font paramFont)
  {
    super.setFont(paramFont);
    updateLabelSizes();
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (!isShowing()) {
      return false;
    }
    Enumeration localEnumeration = labelTable.elements();
    while (localEnumeration.hasMoreElements())
    {
      Component localComponent = (Component)localEnumeration.nextElement();
      if ((localComponent instanceof JLabel))
      {
        JLabel localJLabel = (JLabel)localComponent;
        if ((SwingUtilities.doesIconReferenceImage(localJLabel.getIcon(), paramImage)) || (SwingUtilities.doesIconReferenceImage(localJLabel.getDisabledIcon(), paramImage))) {
          return super.imageUpdate(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
        }
      }
    }
    return false;
  }
  
  public Dictionary getLabelTable()
  {
    return labelTable;
  }
  
  public void setLabelTable(Dictionary paramDictionary)
  {
    Dictionary localDictionary = labelTable;
    labelTable = paramDictionary;
    updateLabelUIs();
    firePropertyChange("labelTable", localDictionary, labelTable);
    if (paramDictionary != localDictionary)
    {
      revalidate();
      repaint();
    }
  }
  
  protected void updateLabelUIs()
  {
    Dictionary localDictionary = getLabelTable();
    if (localDictionary == null) {
      return;
    }
    Enumeration localEnumeration = localDictionary.keys();
    while (localEnumeration.hasMoreElements())
    {
      JComponent localJComponent = (JComponent)localDictionary.get(localEnumeration.nextElement());
      localJComponent.updateUI();
      localJComponent.setSize(localJComponent.getPreferredSize());
    }
  }
  
  private void updateLabelSizes()
  {
    Dictionary localDictionary = getLabelTable();
    if (localDictionary != null)
    {
      Enumeration localEnumeration = localDictionary.elements();
      while (localEnumeration.hasMoreElements())
      {
        JComponent localJComponent = (JComponent)localEnumeration.nextElement();
        localJComponent.setSize(localJComponent.getPreferredSize());
      }
    }
  }
  
  public Hashtable createStandardLabels(int paramInt)
  {
    return createStandardLabels(paramInt, getMinimum());
  }
  
  public Hashtable createStandardLabels(int paramInt1, int paramInt2)
  {
    if ((paramInt2 > getMaximum()) || (paramInt2 < getMinimum())) {
      throw new IllegalArgumentException("Slider label start point out of range.");
    }
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("Label incremement must be > 0");
    }
    Hashtable local1SmartHashtable = new Hashtable()
    {
      int increment = 0;
      int start = 0;
      boolean startAtMin = false;
      
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        if ((paramAnonymousPropertyChangeEvent.getPropertyName().equals("minimum")) && (startAtMin)) {
          start = getMinimum();
        }
        if ((paramAnonymousPropertyChangeEvent.getPropertyName().equals("minimum")) || (paramAnonymousPropertyChangeEvent.getPropertyName().equals("maximum")))
        {
          Enumeration localEnumeration = getLabelTable().keys();
          Hashtable localHashtable = new Hashtable();
          Object localObject1;
          while (localEnumeration.hasMoreElements())
          {
            localObject1 = localEnumeration.nextElement();
            Object localObject2 = labelTable.get(localObject1);
            if (!(localObject2 instanceof LabelUIResource)) {
              localHashtable.put(localObject1, localObject2);
            }
          }
          clear();
          createLabels();
          localEnumeration = localHashtable.keys();
          while (localEnumeration.hasMoreElements())
          {
            localObject1 = localEnumeration.nextElement();
            put(localObject1, localHashtable.get(localObject1));
          }
          ((JSlider)paramAnonymousPropertyChangeEvent.getSource()).setLabelTable(this);
        }
      }
      
      void createLabels()
      {
        int i = start;
        while (i <= getMaximum())
        {
          put(Integer.valueOf(i), new LabelUIResource("" + i, 0));
          i += increment;
        }
      }
      
      class LabelUIResource
        extends JLabel
        implements UIResource
      {
        public LabelUIResource(String paramString, int paramInt)
        {
          super(paramInt);
          setName("Slider.label");
        }
        
        public Font getFont()
        {
          Font localFont = super.getFont();
          if ((localFont != null) && (!(localFont instanceof UIResource))) {
            return localFont;
          }
          return JSlider.this.getFont();
        }
        
        public Color getForeground()
        {
          Color localColor = super.getForeground();
          if ((localColor != null) && (!(localColor instanceof UIResource))) {
            return localColor;
          }
          if (!(JSlider.this.getForeground() instanceof UIResource)) {
            return JSlider.this.getForeground();
          }
          return localColor;
        }
      }
    };
    Dictionary localDictionary = getLabelTable();
    if ((localDictionary != null) && ((localDictionary instanceof PropertyChangeListener))) {
      removePropertyChangeListener((PropertyChangeListener)localDictionary);
    }
    addPropertyChangeListener(local1SmartHashtable);
    return local1SmartHashtable;
  }
  
  public boolean getInverted()
  {
    return isInverted;
  }
  
  public void setInverted(boolean paramBoolean)
  {
    boolean bool = isInverted;
    isInverted = paramBoolean;
    firePropertyChange("inverted", bool, isInverted);
    if (paramBoolean != bool) {
      repaint();
    }
  }
  
  public int getMajorTickSpacing()
  {
    return majorTickSpacing;
  }
  
  public void setMajorTickSpacing(int paramInt)
  {
    int i = majorTickSpacing;
    majorTickSpacing = paramInt;
    if ((labelTable == null) && (getMajorTickSpacing() > 0) && (getPaintLabels())) {
      setLabelTable(createStandardLabels(getMajorTickSpacing()));
    }
    firePropertyChange("majorTickSpacing", i, majorTickSpacing);
    if ((majorTickSpacing != i) && (getPaintTicks())) {
      repaint();
    }
  }
  
  public int getMinorTickSpacing()
  {
    return minorTickSpacing;
  }
  
  public void setMinorTickSpacing(int paramInt)
  {
    int i = minorTickSpacing;
    minorTickSpacing = paramInt;
    firePropertyChange("minorTickSpacing", i, minorTickSpacing);
    if ((minorTickSpacing != i) && (getPaintTicks())) {
      repaint();
    }
  }
  
  public boolean getSnapToTicks()
  {
    return snapToTicks;
  }
  
  boolean getSnapToValue()
  {
    return snapToValue;
  }
  
  public void setSnapToTicks(boolean paramBoolean)
  {
    boolean bool = snapToTicks;
    snapToTicks = paramBoolean;
    firePropertyChange("snapToTicks", bool, snapToTicks);
  }
  
  void setSnapToValue(boolean paramBoolean)
  {
    boolean bool = snapToValue;
    snapToValue = paramBoolean;
    firePropertyChange("snapToValue", bool, snapToValue);
  }
  
  public boolean getPaintTicks()
  {
    return paintTicks;
  }
  
  public void setPaintTicks(boolean paramBoolean)
  {
    boolean bool = paintTicks;
    paintTicks = paramBoolean;
    firePropertyChange("paintTicks", bool, paintTicks);
    if (paintTicks != bool)
    {
      revalidate();
      repaint();
    }
  }
  
  public boolean getPaintTrack()
  {
    return paintTrack;
  }
  
  public void setPaintTrack(boolean paramBoolean)
  {
    boolean bool = paintTrack;
    paintTrack = paramBoolean;
    firePropertyChange("paintTrack", bool, paintTrack);
    if (paintTrack != bool) {
      repaint();
    }
  }
  
  public boolean getPaintLabels()
  {
    return paintLabels;
  }
  
  public void setPaintLabels(boolean paramBoolean)
  {
    boolean bool = paintLabels;
    paintLabels = paramBoolean;
    if ((labelTable == null) && (getMajorTickSpacing() > 0)) {
      setLabelTable(createStandardLabels(getMajorTickSpacing()));
    }
    firePropertyChange("paintLabels", bool, paintLabels);
    if (paintLabels != bool)
    {
      revalidate();
      repaint();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SliderUI"))
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
    String str1 = paintTicks ? "true" : "false";
    String str2 = paintTrack ? "true" : "false";
    String str3 = paintLabels ? "true" : "false";
    String str4 = isInverted ? "true" : "false";
    String str5 = snapToTicks ? "true" : "false";
    String str6 = snapToValue ? "true" : "false";
    String str7 = orientation == 0 ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",isInverted=" + str4 + ",majorTickSpacing=" + majorTickSpacing + ",minorTickSpacing=" + minorTickSpacing + ",orientation=" + str7 + ",paintLabels=" + str3 + ",paintTicks=" + str1 + ",paintTrack=" + str2 + ",snapToTicks=" + str5 + ",snapToValue=" + str6;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJSlider();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJSlider
    extends JComponent.AccessibleJComponent
    implements AccessibleValue
  {
    protected AccessibleJSlider()
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
      return AccessibleRole.SLIDER;
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
      BoundedRangeModel localBoundedRangeModel = getModel();
      return Integer.valueOf(localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getExtent());
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JSlider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */