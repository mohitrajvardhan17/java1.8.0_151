package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D.Float;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.Serializable;
import java.text.BreakIterator;
import java.util.Enumeration;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

public abstract class AbstractButton
  extends JComponent
  implements ItemSelectable, SwingConstants
{
  public static final String MODEL_CHANGED_PROPERTY = "model";
  public static final String TEXT_CHANGED_PROPERTY = "text";
  public static final String MNEMONIC_CHANGED_PROPERTY = "mnemonic";
  public static final String MARGIN_CHANGED_PROPERTY = "margin";
  public static final String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment";
  public static final String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment";
  public static final String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition";
  public static final String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition";
  public static final String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
  public static final String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
  public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
  public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";
  public static final String ICON_CHANGED_PROPERTY = "icon";
  public static final String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
  public static final String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";
  public static final String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
  public static final String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";
  public static final String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
  public static final String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon";
  protected ButtonModel model = null;
  private String text = "";
  private Insets margin = null;
  private Insets defaultMargin = null;
  private Icon defaultIcon = null;
  private Icon pressedIcon = null;
  private Icon disabledIcon = null;
  private Icon selectedIcon = null;
  private Icon disabledSelectedIcon = null;
  private Icon rolloverIcon = null;
  private Icon rolloverSelectedIcon = null;
  private boolean paintBorder = true;
  private boolean paintFocus = true;
  private boolean rolloverEnabled = false;
  private boolean contentAreaFilled = true;
  private int verticalAlignment = 0;
  private int horizontalAlignment = 0;
  private int verticalTextPosition = 0;
  private int horizontalTextPosition = 11;
  private int iconTextGap = 4;
  private int mnemonic;
  private int mnemonicIndex = -1;
  private long multiClickThreshhold = 0L;
  private boolean borderPaintedSet = false;
  private boolean rolloverEnabledSet = false;
  private boolean iconTextGapSet = false;
  private boolean contentAreaFilledSet = false;
  private boolean setLayout = false;
  boolean defaultCapable = true;
  private Handler handler;
  protected ChangeListener changeListener = null;
  protected ActionListener actionListener = null;
  protected ItemListener itemListener = null;
  protected transient ChangeEvent changeEvent;
  private boolean hideActionText = false;
  private Action action;
  private PropertyChangeListener actionPropertyChangeListener;
  
  public AbstractButton() {}
  
  public void setHideActionText(boolean paramBoolean)
  {
    if (paramBoolean != hideActionText)
    {
      hideActionText = paramBoolean;
      if (getAction() != null) {
        setTextFromAction(getAction(), false);
      }
      firePropertyChange("hideActionText", !paramBoolean, paramBoolean);
    }
  }
  
  public boolean getHideActionText()
  {
    return hideActionText;
  }
  
  public String getText()
  {
    return text;
  }
  
  public void setText(String paramString)
  {
    String str = text;
    text = paramString;
    firePropertyChange("text", str, paramString);
    updateDisplayedMnemonicIndex(paramString, getMnemonic());
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString);
    }
    if ((paramString == null) || (str == null) || (!paramString.equals(str)))
    {
      revalidate();
      repaint();
    }
  }
  
  public boolean isSelected()
  {
    return model.isSelected();
  }
  
  public void setSelected(boolean paramBoolean)
  {
    boolean bool = isSelected();
    model.setSelected(paramBoolean);
  }
  
  public void doClick()
  {
    doClick(68);
  }
  
  public void doClick(int paramInt)
  {
    Dimension localDimension = getSize();
    model.setArmed(true);
    model.setPressed(true);
    paintImmediately(new Rectangle(0, 0, width, height));
    try
    {
      Thread.currentThread();
      Thread.sleep(paramInt);
    }
    catch (InterruptedException localInterruptedException) {}
    model.setPressed(false);
    model.setArmed(false);
  }
  
  public void setMargin(Insets paramInsets)
  {
    if ((paramInsets instanceof UIResource)) {
      defaultMargin = paramInsets;
    } else if ((margin instanceof UIResource)) {
      defaultMargin = margin;
    }
    if ((paramInsets == null) && (defaultMargin != null)) {
      paramInsets = defaultMargin;
    }
    Insets localInsets = margin;
    margin = paramInsets;
    firePropertyChange("margin", localInsets, paramInsets);
    if ((localInsets == null) || (!localInsets.equals(paramInsets)))
    {
      revalidate();
      repaint();
    }
  }
  
  public Insets getMargin()
  {
    return margin == null ? null : (Insets)margin.clone();
  }
  
  public Icon getIcon()
  {
    return defaultIcon;
  }
  
  public void setIcon(Icon paramIcon)
  {
    Icon localIcon = defaultIcon;
    defaultIcon = paramIcon;
    if ((paramIcon != localIcon) && ((disabledIcon instanceof UIResource))) {
      disabledIcon = null;
    }
    firePropertyChange("icon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    if (paramIcon != localIcon)
    {
      if ((paramIcon == null) || (localIcon == null) || (paramIcon.getIconWidth() != localIcon.getIconWidth()) || (paramIcon.getIconHeight() != localIcon.getIconHeight())) {
        revalidate();
      }
      repaint();
    }
  }
  
  public Icon getPressedIcon()
  {
    return pressedIcon;
  }
  
  public void setPressedIcon(Icon paramIcon)
  {
    Icon localIcon = pressedIcon;
    pressedIcon = paramIcon;
    firePropertyChange("pressedIcon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    if ((paramIcon != localIcon) && (getModel().isPressed())) {
      repaint();
    }
  }
  
  public Icon getSelectedIcon()
  {
    return selectedIcon;
  }
  
  public void setSelectedIcon(Icon paramIcon)
  {
    Icon localIcon = selectedIcon;
    selectedIcon = paramIcon;
    if ((paramIcon != localIcon) && ((disabledSelectedIcon instanceof UIResource))) {
      disabledSelectedIcon = null;
    }
    firePropertyChange("selectedIcon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    if ((paramIcon != localIcon) && (isSelected())) {
      repaint();
    }
  }
  
  public Icon getRolloverIcon()
  {
    return rolloverIcon;
  }
  
  public void setRolloverIcon(Icon paramIcon)
  {
    Icon localIcon = rolloverIcon;
    rolloverIcon = paramIcon;
    firePropertyChange("rolloverIcon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    setRolloverEnabled(true);
    if (paramIcon != localIcon) {
      repaint();
    }
  }
  
  public Icon getRolloverSelectedIcon()
  {
    return rolloverSelectedIcon;
  }
  
  public void setRolloverSelectedIcon(Icon paramIcon)
  {
    Icon localIcon = rolloverSelectedIcon;
    rolloverSelectedIcon = paramIcon;
    firePropertyChange("rolloverSelectedIcon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    setRolloverEnabled(true);
    if ((paramIcon != localIcon) && (isSelected())) {
      repaint();
    }
  }
  
  @Transient
  public Icon getDisabledIcon()
  {
    if (disabledIcon == null)
    {
      disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, getIcon());
      if (disabledIcon != null) {
        firePropertyChange("disabledIcon", null, disabledIcon);
      }
    }
    return disabledIcon;
  }
  
  public void setDisabledIcon(Icon paramIcon)
  {
    Icon localIcon = disabledIcon;
    disabledIcon = paramIcon;
    firePropertyChange("disabledIcon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    if ((paramIcon != localIcon) && (!isEnabled())) {
      repaint();
    }
  }
  
  public Icon getDisabledSelectedIcon()
  {
    if (disabledSelectedIcon == null) {
      if (selectedIcon != null) {
        disabledSelectedIcon = UIManager.getLookAndFeel().getDisabledSelectedIcon(this, getSelectedIcon());
      } else {
        return getDisabledIcon();
      }
    }
    return disabledSelectedIcon;
  }
  
  public void setDisabledSelectedIcon(Icon paramIcon)
  {
    Icon localIcon = disabledSelectedIcon;
    disabledSelectedIcon = paramIcon;
    firePropertyChange("disabledSelectedIcon", localIcon, paramIcon);
    if (accessibleContext != null) {
      accessibleContext.firePropertyChange("AccessibleVisibleData", localIcon, paramIcon);
    }
    if (paramIcon != localIcon)
    {
      if ((paramIcon == null) || (localIcon == null) || (paramIcon.getIconWidth() != localIcon.getIconWidth()) || (paramIcon.getIconHeight() != localIcon.getIconHeight())) {
        revalidate();
      }
      if ((!isEnabled()) && (isSelected())) {
        repaint();
      }
    }
  }
  
  public int getVerticalAlignment()
  {
    return verticalAlignment;
  }
  
  public void setVerticalAlignment(int paramInt)
  {
    if (paramInt == verticalAlignment) {
      return;
    }
    int i = verticalAlignment;
    verticalAlignment = checkVerticalKey(paramInt, "verticalAlignment");
    firePropertyChange("verticalAlignment", i, verticalAlignment);
    repaint();
  }
  
  public int getHorizontalAlignment()
  {
    return horizontalAlignment;
  }
  
  public void setHorizontalAlignment(int paramInt)
  {
    if (paramInt == horizontalAlignment) {
      return;
    }
    int i = horizontalAlignment;
    horizontalAlignment = checkHorizontalKey(paramInt, "horizontalAlignment");
    firePropertyChange("horizontalAlignment", i, horizontalAlignment);
    repaint();
  }
  
  public int getVerticalTextPosition()
  {
    return verticalTextPosition;
  }
  
  public void setVerticalTextPosition(int paramInt)
  {
    if (paramInt == verticalTextPosition) {
      return;
    }
    int i = verticalTextPosition;
    verticalTextPosition = checkVerticalKey(paramInt, "verticalTextPosition");
    firePropertyChange("verticalTextPosition", i, verticalTextPosition);
    revalidate();
    repaint();
  }
  
  public int getHorizontalTextPosition()
  {
    return horizontalTextPosition;
  }
  
  public void setHorizontalTextPosition(int paramInt)
  {
    if (paramInt == horizontalTextPosition) {
      return;
    }
    int i = horizontalTextPosition;
    horizontalTextPosition = checkHorizontalKey(paramInt, "horizontalTextPosition");
    firePropertyChange("horizontalTextPosition", i, horizontalTextPosition);
    revalidate();
    repaint();
  }
  
  public int getIconTextGap()
  {
    return iconTextGap;
  }
  
  public void setIconTextGap(int paramInt)
  {
    int i = iconTextGap;
    iconTextGap = paramInt;
    iconTextGapSet = true;
    firePropertyChange("iconTextGap", i, paramInt);
    if (paramInt != i)
    {
      revalidate();
      repaint();
    }
  }
  
  protected int checkHorizontalKey(int paramInt, String paramString)
  {
    if ((paramInt == 2) || (paramInt == 0) || (paramInt == 4) || (paramInt == 10) || (paramInt == 11)) {
      return paramInt;
    }
    throw new IllegalArgumentException(paramString);
  }
  
  protected int checkVerticalKey(int paramInt, String paramString)
  {
    if ((paramInt == 1) || (paramInt == 0) || (paramInt == 3)) {
      return paramInt;
    }
    throw new IllegalArgumentException(paramString);
  }
  
  public void removeNotify()
  {
    super.removeNotify();
    if (isRolloverEnabled()) {
      getModel().setRollover(false);
    }
  }
  
  public void setActionCommand(String paramString)
  {
    getModel().setActionCommand(paramString);
  }
  
  public String getActionCommand()
  {
    String str = getModel().getActionCommand();
    if (str == null) {
      str = getText();
    }
    return str;
  }
  
  public void setAction(Action paramAction)
  {
    Action localAction = getAction();
    if ((action == null) || (!action.equals(paramAction)))
    {
      action = paramAction;
      if (localAction != null)
      {
        removeActionListener(localAction);
        localAction.removePropertyChangeListener(actionPropertyChangeListener);
        actionPropertyChangeListener = null;
      }
      configurePropertiesFromAction(action);
      if (action != null)
      {
        if (!isListener(ActionListener.class, action)) {
          addActionListener(action);
        }
        actionPropertyChangeListener = createActionPropertyChangeListener(action);
        action.addPropertyChangeListener(actionPropertyChangeListener);
      }
      firePropertyChange("action", localAction, action);
    }
  }
  
  private boolean isListener(Class paramClass, ActionListener paramActionListener)
  {
    boolean bool = false;
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if ((arrayOfObject[i] == paramClass) && (arrayOfObject[(i + 1)] == paramActionListener)) {
        bool = true;
      }
    }
    return bool;
  }
  
  public Action getAction()
  {
    return action;
  }
  
  protected void configurePropertiesFromAction(Action paramAction)
  {
    setMnemonicFromAction(paramAction);
    setTextFromAction(paramAction, false);
    AbstractAction.setToolTipTextFromAction(this, paramAction);
    setIconFromAction(paramAction);
    setActionCommandFromAction(paramAction);
    AbstractAction.setEnabledFromAction(this, paramAction);
    if ((AbstractAction.hasSelectedKey(paramAction)) && (shouldUpdateSelectedStateFromAction())) {
      setSelectedFromAction(paramAction);
    }
    setDisplayedMnemonicIndexFromAction(paramAction, false);
  }
  
  void clientPropertyChanged(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if (paramObject1 == "hideActionText")
    {
      boolean bool = (paramObject3 instanceof Boolean) ? ((Boolean)paramObject3).booleanValue() : false;
      if (getHideActionText() != bool) {
        setHideActionText(bool);
      }
    }
  }
  
  boolean shouldUpdateSelectedStateFromAction()
  {
    return false;
  }
  
  protected void actionPropertyChanged(Action paramAction, String paramString)
  {
    if (paramString == "Name") {
      setTextFromAction(paramAction, true);
    } else if (paramString == "enabled") {
      AbstractAction.setEnabledFromAction(this, paramAction);
    } else if (paramString == "ShortDescription") {
      AbstractAction.setToolTipTextFromAction(this, paramAction);
    } else if (paramString == "SmallIcon") {
      smallIconChanged(paramAction);
    } else if (paramString == "MnemonicKey") {
      setMnemonicFromAction(paramAction);
    } else if (paramString == "ActionCommandKey") {
      setActionCommandFromAction(paramAction);
    } else if ((paramString == "SwingSelectedKey") && (AbstractAction.hasSelectedKey(paramAction)) && (shouldUpdateSelectedStateFromAction())) {
      setSelectedFromAction(paramAction);
    } else if (paramString == "SwingDisplayedMnemonicIndexKey") {
      setDisplayedMnemonicIndexFromAction(paramAction, true);
    } else if (paramString == "SwingLargeIconKey") {
      largeIconChanged(paramAction);
    }
  }
  
  private void setDisplayedMnemonicIndexFromAction(Action paramAction, boolean paramBoolean)
  {
    Integer localInteger = paramAction == null ? null : (Integer)paramAction.getValue("SwingDisplayedMnemonicIndexKey");
    if ((paramBoolean) || (localInteger != null))
    {
      int i;
      if (localInteger == null)
      {
        i = -1;
      }
      else
      {
        i = localInteger.intValue();
        String str = getText();
        if ((str == null) || (i >= str.length())) {
          i = -1;
        }
      }
      setDisplayedMnemonicIndex(i);
    }
  }
  
  private void setMnemonicFromAction(Action paramAction)
  {
    Integer localInteger = paramAction == null ? null : (Integer)paramAction.getValue("MnemonicKey");
    setMnemonic(localInteger == null ? 0 : localInteger.intValue());
  }
  
  private void setTextFromAction(Action paramAction, boolean paramBoolean)
  {
    boolean bool = getHideActionText();
    if (!paramBoolean) {
      setText((paramAction != null) && (!bool) ? (String)paramAction.getValue("Name") : null);
    } else if (!bool) {
      setText((String)paramAction.getValue("Name"));
    }
  }
  
  void setIconFromAction(Action paramAction)
  {
    Icon localIcon = null;
    if (paramAction != null)
    {
      localIcon = (Icon)paramAction.getValue("SwingLargeIconKey");
      if (localIcon == null) {
        localIcon = (Icon)paramAction.getValue("SmallIcon");
      }
    }
    setIcon(localIcon);
  }
  
  void smallIconChanged(Action paramAction)
  {
    if (paramAction.getValue("SwingLargeIconKey") == null) {
      setIconFromAction(paramAction);
    }
  }
  
  void largeIconChanged(Action paramAction)
  {
    setIconFromAction(paramAction);
  }
  
  private void setActionCommandFromAction(Action paramAction)
  {
    setActionCommand(paramAction != null ? (String)paramAction.getValue("ActionCommandKey") : null);
  }
  
  private void setSelectedFromAction(Action paramAction)
  {
    boolean bool = false;
    if (paramAction != null) {
      bool = AbstractAction.isSelected(paramAction);
    }
    if (bool != isSelected())
    {
      setSelected(bool);
      if ((!bool) && (isSelected()) && ((getModel() instanceof DefaultButtonModel)))
      {
        ButtonGroup localButtonGroup = ((DefaultButtonModel)getModel()).getGroup();
        if (localButtonGroup != null) {
          localButtonGroup.clearSelection();
        }
      }
    }
  }
  
  protected PropertyChangeListener createActionPropertyChangeListener(Action paramAction)
  {
    return createActionPropertyChangeListener0(paramAction);
  }
  
  PropertyChangeListener createActionPropertyChangeListener0(Action paramAction)
  {
    return new ButtonActionPropertyChangeListener(this, paramAction);
  }
  
  public boolean isBorderPainted()
  {
    return paintBorder;
  }
  
  public void setBorderPainted(boolean paramBoolean)
  {
    boolean bool = paintBorder;
    paintBorder = paramBoolean;
    borderPaintedSet = true;
    firePropertyChange("borderPainted", bool, paintBorder);
    if (paramBoolean != bool)
    {
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
  
  public boolean isFocusPainted()
  {
    return paintFocus;
  }
  
  public void setFocusPainted(boolean paramBoolean)
  {
    boolean bool = paintFocus;
    paintFocus = paramBoolean;
    firePropertyChange("focusPainted", bool, paintFocus);
    if ((paramBoolean != bool) && (isFocusOwner()))
    {
      revalidate();
      repaint();
    }
  }
  
  public boolean isContentAreaFilled()
  {
    return contentAreaFilled;
  }
  
  public void setContentAreaFilled(boolean paramBoolean)
  {
    boolean bool = contentAreaFilled;
    contentAreaFilled = paramBoolean;
    contentAreaFilledSet = true;
    firePropertyChange("contentAreaFilled", bool, contentAreaFilled);
    if (paramBoolean != bool) {
      repaint();
    }
  }
  
  public boolean isRolloverEnabled()
  {
    return rolloverEnabled;
  }
  
  public void setRolloverEnabled(boolean paramBoolean)
  {
    boolean bool = rolloverEnabled;
    rolloverEnabled = paramBoolean;
    rolloverEnabledSet = true;
    firePropertyChange("rolloverEnabled", bool, rolloverEnabled);
    if (paramBoolean != bool) {
      repaint();
    }
  }
  
  public int getMnemonic()
  {
    return mnemonic;
  }
  
  public void setMnemonic(int paramInt)
  {
    int i = getMnemonic();
    model.setMnemonic(paramInt);
    updateMnemonicProperties();
  }
  
  public void setMnemonic(char paramChar)
  {
    int i = paramChar;
    if ((i >= 97) && (i <= 122)) {
      i -= 32;
    }
    setMnemonic(i);
  }
  
  public void setDisplayedMnemonicIndex(int paramInt)
    throws IllegalArgumentException
  {
    int i = mnemonicIndex;
    if (paramInt == -1)
    {
      mnemonicIndex = -1;
    }
    else
    {
      String str = getText();
      int j = str == null ? 0 : str.length();
      if ((paramInt < -1) || (paramInt >= j)) {
        throw new IllegalArgumentException("index == " + paramInt);
      }
    }
    mnemonicIndex = paramInt;
    firePropertyChange("displayedMnemonicIndex", i, paramInt);
    if (paramInt != i)
    {
      revalidate();
      repaint();
    }
  }
  
  public int getDisplayedMnemonicIndex()
  {
    return mnemonicIndex;
  }
  
  private void updateDisplayedMnemonicIndex(String paramString, int paramInt)
  {
    setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(paramString, paramInt));
  }
  
  private void updateMnemonicProperties()
  {
    int i = model.getMnemonic();
    if (mnemonic != i)
    {
      int j = mnemonic;
      mnemonic = i;
      firePropertyChange("mnemonic", j, mnemonic);
      updateDisplayedMnemonicIndex(getText(), mnemonic);
      revalidate();
      repaint();
    }
  }
  
  public void setMultiClickThreshhold(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("threshhold must be >= 0");
    }
    multiClickThreshhold = paramLong;
  }
  
  public long getMultiClickThreshhold()
  {
    return multiClickThreshhold;
  }
  
  public ButtonModel getModel()
  {
    return model;
  }
  
  public void setModel(ButtonModel paramButtonModel)
  {
    ButtonModel localButtonModel = getModel();
    if (localButtonModel != null)
    {
      localButtonModel.removeChangeListener(changeListener);
      localButtonModel.removeActionListener(actionListener);
      localButtonModel.removeItemListener(itemListener);
      changeListener = null;
      actionListener = null;
      itemListener = null;
    }
    model = paramButtonModel;
    if (paramButtonModel != null)
    {
      changeListener = createChangeListener();
      actionListener = createActionListener();
      itemListener = createItemListener();
      paramButtonModel.addChangeListener(changeListener);
      paramButtonModel.addActionListener(actionListener);
      paramButtonModel.addItemListener(itemListener);
      updateMnemonicProperties();
      super.setEnabled(paramButtonModel.isEnabled());
    }
    else
    {
      mnemonic = 0;
    }
    updateDisplayedMnemonicIndex(getText(), mnemonic);
    firePropertyChange("model", localButtonModel, paramButtonModel);
    if (paramButtonModel != localButtonModel)
    {
      revalidate();
      repaint();
    }
  }
  
  public ButtonUI getUI()
  {
    return (ButtonUI)ui;
  }
  
  public void setUI(ButtonUI paramButtonUI)
  {
    super.setUI(paramButtonUI);
    if ((disabledIcon instanceof UIResource)) {
      setDisabledIcon(null);
    }
    if ((disabledSelectedIcon instanceof UIResource)) {
      setDisabledSelectedIcon(null);
    }
  }
  
  public void updateUI() {}
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    if (!setLayout) {
      setLayout(new OverlayLayout(this));
    }
    super.addImpl(paramComponent, paramObject, paramInt);
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    setLayout = true;
    super.setLayout(paramLayoutManager);
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
  
  public void addActionListener(ActionListener paramActionListener)
  {
    listenerList.add(ActionListener.class, paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener)
  {
    if ((paramActionListener != null) && (getAction() == paramActionListener)) {
      setAction(null);
    } else {
      listenerList.remove(ActionListener.class, paramActionListener);
    }
  }
  
  public ActionListener[] getActionListeners()
  {
    return (ActionListener[])listenerList.getListeners(ActionListener.class);
  }
  
  protected ChangeListener createChangeListener()
  {
    return getHandler();
  }
  
  protected void fireActionPerformed(ActionEvent paramActionEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ActionEvent localActionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class)
      {
        if (localActionEvent == null)
        {
          String str = paramActionEvent.getActionCommand();
          if (str == null) {
            str = getActionCommand();
          }
          localActionEvent = new ActionEvent(this, 1001, str, paramActionEvent.getWhen(), paramActionEvent.getModifiers());
        }
        ((ActionListener)arrayOfObject[(i + 1)]).actionPerformed(localActionEvent);
      }
    }
  }
  
  protected void fireItemStateChanged(ItemEvent paramItemEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ItemEvent localItemEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ItemListener.class)
      {
        if (localItemEvent == null) {
          localItemEvent = new ItemEvent(this, 701, this, paramItemEvent.getStateChange());
        }
        ((ItemListener)arrayOfObject[(i + 1)]).itemStateChanged(localItemEvent);
      }
    }
    if (accessibleContext != null) {
      if (paramItemEvent.getStateChange() == 1)
      {
        accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED);
        accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(0), Integer.valueOf(1));
      }
      else
      {
        accessibleContext.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
        accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(1), Integer.valueOf(0));
      }
    }
  }
  
  protected ActionListener createActionListener()
  {
    return getHandler();
  }
  
  protected ItemListener createItemListener()
  {
    return getHandler();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if ((!paramBoolean) && (model.isRollover())) {
      model.setRollover(false);
    }
    super.setEnabled(paramBoolean);
    model.setEnabled(paramBoolean);
  }
  
  @Deprecated
  public String getLabel()
  {
    return getText();
  }
  
  @Deprecated
  public void setLabel(String paramString)
  {
    setText(paramString);
  }
  
  public void addItemListener(ItemListener paramItemListener)
  {
    listenerList.add(ItemListener.class, paramItemListener);
  }
  
  public void removeItemListener(ItemListener paramItemListener)
  {
    listenerList.remove(ItemListener.class, paramItemListener);
  }
  
  public ItemListener[] getItemListeners()
  {
    return (ItemListener[])listenerList.getListeners(ItemListener.class);
  }
  
  public Object[] getSelectedObjects()
  {
    if (!isSelected()) {
      return null;
    }
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = getText();
    return arrayOfObject;
  }
  
  protected void init(String paramString, Icon paramIcon)
  {
    if (paramString != null) {
      setText(paramString);
    }
    if (paramIcon != null) {
      setIcon(paramIcon);
    }
    updateUI();
    setAlignmentX(0.0F);
    setAlignmentY(0.5F);
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Icon localIcon = null;
    if (!model.isEnabled())
    {
      if (model.isSelected()) {
        localIcon = getDisabledSelectedIcon();
      } else {
        localIcon = getDisabledIcon();
      }
    }
    else if ((model.isPressed()) && (model.isArmed())) {
      localIcon = getPressedIcon();
    } else if ((isRolloverEnabled()) && (model.isRollover()))
    {
      if (model.isSelected()) {
        localIcon = getRolloverSelectedIcon();
      } else {
        localIcon = getRolloverIcon();
      }
    }
    else if (model.isSelected()) {
      localIcon = getSelectedIcon();
    }
    if (localIcon == null) {
      localIcon = getIcon();
    }
    if ((localIcon == null) || (!SwingUtilities.doesIconReferenceImage(localIcon, paramImage))) {
      return false;
    }
    return super.imageUpdate(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void setUIProperty(String paramString, Object paramObject)
  {
    if (paramString == "borderPainted")
    {
      if (!borderPaintedSet)
      {
        setBorderPainted(((Boolean)paramObject).booleanValue());
        borderPaintedSet = false;
      }
    }
    else if (paramString == "rolloverEnabled")
    {
      if (!rolloverEnabledSet)
      {
        setRolloverEnabled(((Boolean)paramObject).booleanValue());
        rolloverEnabledSet = false;
      }
    }
    else if (paramString == "iconTextGap")
    {
      if (!iconTextGapSet)
      {
        setIconTextGap(((Number)paramObject).intValue());
        iconTextGapSet = false;
      }
    }
    else if (paramString == "contentAreaFilled")
    {
      if (!contentAreaFilledSet)
      {
        setContentAreaFilled(((Boolean)paramObject).booleanValue());
        contentAreaFilledSet = false;
      }
    }
    else {
      super.setUIProperty(paramString, paramObject);
    }
  }
  
  protected String paramString()
  {
    String str1 = (defaultIcon != null) && (defaultIcon != this) ? defaultIcon.toString() : "";
    String str2 = (pressedIcon != null) && (pressedIcon != this) ? pressedIcon.toString() : "";
    String str3 = (disabledIcon != null) && (disabledIcon != this) ? disabledIcon.toString() : "";
    String str4 = (selectedIcon != null) && (selectedIcon != this) ? selectedIcon.toString() : "";
    String str5 = (disabledSelectedIcon != null) && (disabledSelectedIcon != this) ? disabledSelectedIcon.toString() : "";
    String str6 = (rolloverIcon != null) && (rolloverIcon != this) ? rolloverIcon.toString() : "";
    String str7 = (rolloverSelectedIcon != null) && (rolloverSelectedIcon != this) ? rolloverSelectedIcon.toString() : "";
    String str8 = paintBorder ? "true" : "false";
    String str9 = paintFocus ? "true" : "false";
    String str10 = rolloverEnabled ? "true" : "false";
    return super.paramString() + ",defaultIcon=" + str1 + ",disabledIcon=" + str3 + ",disabledSelectedIcon=" + str5 + ",margin=" + margin + ",paintBorder=" + str8 + ",paintFocus=" + str9 + ",pressedIcon=" + str2 + ",rolloverEnabled=" + str10 + ",rolloverIcon=" + str6 + ",rolloverSelectedIcon=" + str7 + ",selectedIcon=" + str4 + ",text=" + text;
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }
  
  protected abstract class AccessibleAbstractButton
    extends JComponent.AccessibleJComponent
    implements AccessibleAction, AccessibleValue, AccessibleText, AccessibleExtendedComponent
  {
    protected AccessibleAbstractButton()
    {
      super();
    }
    
    public String getAccessibleName()
    {
      String str = accessibleName;
      if (str == null) {
        str = (String)getClientProperty("AccessibleName");
      }
      if (str == null) {
        str = getText();
      }
      if (str == null) {
        str = super.getAccessibleName();
      }
      return str;
    }
    
    public AccessibleIcon[] getAccessibleIcon()
    {
      Icon localIcon = getIcon();
      if ((localIcon instanceof Accessible))
      {
        AccessibleContext localAccessibleContext = ((Accessible)localIcon).getAccessibleContext();
        if ((localAccessibleContext != null) && ((localAccessibleContext instanceof AccessibleIcon))) {
          return new AccessibleIcon[] { (AccessibleIcon)localAccessibleContext };
        }
      }
      return null;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getModel().isArmed()) {
        localAccessibleStateSet.add(AccessibleState.ARMED);
      }
      if (isFocusOwner()) {
        localAccessibleStateSet.add(AccessibleState.FOCUSED);
      }
      if (getModel().isPressed()) {
        localAccessibleStateSet.add(AccessibleState.PRESSED);
      }
      if (isSelected()) {
        localAccessibleStateSet.add(AccessibleState.CHECKED);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRelationSet getAccessibleRelationSet()
    {
      AccessibleRelationSet localAccessibleRelationSet = super.getAccessibleRelationSet();
      if (!localAccessibleRelationSet.contains(AccessibleRelation.MEMBER_OF))
      {
        ButtonModel localButtonModel = getModel();
        if ((localButtonModel != null) && ((localButtonModel instanceof DefaultButtonModel)))
        {
          ButtonGroup localButtonGroup = ((DefaultButtonModel)localButtonModel).getGroup();
          if (localButtonGroup != null)
          {
            int i = localButtonGroup.getButtonCount();
            Object[] arrayOfObject = new Object[i];
            Enumeration localEnumeration = localButtonGroup.getElements();
            for (int j = 0; j < i; j++) {
              if (localEnumeration.hasMoreElements()) {
                arrayOfObject[j] = localEnumeration.nextElement();
              }
            }
            AccessibleRelation localAccessibleRelation = new AccessibleRelation(AccessibleRelation.MEMBER_OF);
            localAccessibleRelation.setTarget(arrayOfObject);
            localAccessibleRelationSet.add(localAccessibleRelation);
          }
        }
      }
      return localAccessibleRelationSet;
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public int getAccessibleActionCount()
    {
      return 1;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      if (paramInt == 0) {
        return UIManager.getString("AbstractButton.clickText");
      }
      return null;
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      if (paramInt == 0)
      {
        doClick();
        return true;
      }
      return false;
    }
    
    public Number getCurrentAccessibleValue()
    {
      if (isSelected()) {
        return Integer.valueOf(1);
      }
      return Integer.valueOf(0);
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      if (paramNumber == null) {
        return false;
      }
      int i = paramNumber.intValue();
      if (i == 0) {
        setSelected(false);
      } else {
        setSelected(true);
      }
      return true;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(0);
    }
    
    public Number getMaximumAccessibleValue()
    {
      return Integer.valueOf(1);
    }
    
    public AccessibleText getAccessibleText()
    {
      View localView = (View)getClientProperty("html");
      if (localView != null) {
        return this;
      }
      return null;
    }
    
    public int getIndexAtPoint(Point paramPoint)
    {
      View localView = (View)getClientProperty("html");
      if (localView != null)
      {
        Rectangle localRectangle = getTextRectangle();
        if (localRectangle == null) {
          return -1;
        }
        Rectangle2D.Float localFloat = new Rectangle2D.Float(x, y, width, height);
        Position.Bias[] arrayOfBias = new Position.Bias[1];
        return localView.viewToModel(x, y, localFloat, arrayOfBias);
      }
      return -1;
    }
    
    public Rectangle getCharacterBounds(int paramInt)
    {
      View localView = (View)getClientProperty("html");
      if (localView != null)
      {
        Rectangle localRectangle = getTextRectangle();
        if (localRectangle == null) {
          return null;
        }
        Rectangle2D.Float localFloat = new Rectangle2D.Float(x, y, width, height);
        try
        {
          Shape localShape = localView.modelToView(paramInt, localFloat, Position.Bias.Forward);
          return localShape.getBounds();
        }
        catch (BadLocationException localBadLocationException)
        {
          return null;
        }
      }
      return null;
    }
    
    public int getCharCount()
    {
      View localView = (View)getClientProperty("html");
      if (localView != null)
      {
        Document localDocument = localView.getDocument();
        if ((localDocument instanceof StyledDocument))
        {
          StyledDocument localStyledDocument = (StyledDocument)localDocument;
          return localStyledDocument.getLength();
        }
      }
      return accessibleContext.getAccessibleName().length();
    }
    
    public int getCaretPosition()
    {
      return -1;
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 >= getCharCount())) {
        return null;
      }
      BreakIterator localBreakIterator;
      int i;
      switch (paramInt1)
      {
      case 1: 
        try
        {
          return getText(paramInt2, 1);
        }
        catch (BadLocationException localBadLocationException1)
        {
          return null;
        }
      case 2: 
        try
        {
          String str1 = getText(0, getCharCount());
          localBreakIterator = BreakIterator.getWordInstance(getLocale());
          localBreakIterator.setText(str1);
          i = localBreakIterator.following(paramInt2);
          return str1.substring(localBreakIterator.previous(), i);
        }
        catch (BadLocationException localBadLocationException2)
        {
          return null;
        }
      case 3: 
        try
        {
          String str2 = getText(0, getCharCount());
          localBreakIterator = BreakIterator.getSentenceInstance(getLocale());
          localBreakIterator.setText(str2);
          i = localBreakIterator.following(paramInt2);
          return str2.substring(localBreakIterator.previous(), i);
        }
        catch (BadLocationException localBadLocationException3)
        {
          return null;
        }
      }
      return null;
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 >= getCharCount())) {
        return null;
      }
      BreakIterator localBreakIterator;
      int i;
      int j;
      switch (paramInt1)
      {
      case 1: 
        if (paramInt2 + 1 >= getCharCount()) {
          return null;
        }
        try
        {
          return getText(paramInt2 + 1, 1);
        }
        catch (BadLocationException localBadLocationException1)
        {
          return null;
        }
      case 2: 
        try
        {
          String str1 = getText(0, getCharCount());
          localBreakIterator = BreakIterator.getWordInstance(getLocale());
          localBreakIterator.setText(str1);
          i = localBreakIterator.following(paramInt2);
          if ((i == -1) || (i >= str1.length())) {
            return null;
          }
          j = localBreakIterator.following(i);
          if ((j == -1) || (j >= str1.length())) {
            return null;
          }
          return str1.substring(i, j);
        }
        catch (BadLocationException localBadLocationException2)
        {
          return null;
        }
      case 3: 
        try
        {
          String str2 = getText(0, getCharCount());
          localBreakIterator = BreakIterator.getSentenceInstance(getLocale());
          localBreakIterator.setText(str2);
          i = localBreakIterator.following(paramInt2);
          if ((i == -1) || (i > str2.length())) {
            return null;
          }
          j = localBreakIterator.following(i);
          if ((j == -1) || (j > str2.length())) {
            return null;
          }
          return str2.substring(i, j);
        }
        catch (BadLocationException localBadLocationException3)
        {
          return null;
        }
      }
      return null;
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 > getCharCount() - 1)) {
        return null;
      }
      BreakIterator localBreakIterator;
      int i;
      int j;
      switch (paramInt1)
      {
      case 1: 
        if (paramInt2 == 0) {
          return null;
        }
        try
        {
          return getText(paramInt2 - 1, 1);
        }
        catch (BadLocationException localBadLocationException1)
        {
          return null;
        }
      case 2: 
        try
        {
          String str1 = getText(0, getCharCount());
          localBreakIterator = BreakIterator.getWordInstance(getLocale());
          localBreakIterator.setText(str1);
          i = localBreakIterator.following(paramInt2);
          i = localBreakIterator.previous();
          j = localBreakIterator.previous();
          if (j == -1) {
            return null;
          }
          return str1.substring(j, i);
        }
        catch (BadLocationException localBadLocationException2)
        {
          return null;
        }
      case 3: 
        try
        {
          String str2 = getText(0, getCharCount());
          localBreakIterator = BreakIterator.getSentenceInstance(getLocale());
          localBreakIterator.setText(str2);
          i = localBreakIterator.following(paramInt2);
          i = localBreakIterator.previous();
          j = localBreakIterator.previous();
          if (j == -1) {
            return null;
          }
          return str2.substring(j, i);
        }
        catch (BadLocationException localBadLocationException3)
        {
          return null;
        }
      }
      return null;
    }
    
    public AttributeSet getCharacterAttribute(int paramInt)
    {
      View localView = (View)getClientProperty("html");
      if (localView != null)
      {
        Document localDocument = localView.getDocument();
        if ((localDocument instanceof StyledDocument))
        {
          StyledDocument localStyledDocument = (StyledDocument)localDocument;
          Element localElement = localStyledDocument.getCharacterElement(paramInt);
          if (localElement != null) {
            return localElement.getAttributes();
          }
        }
      }
      return null;
    }
    
    public int getSelectionStart()
    {
      return -1;
    }
    
    public int getSelectionEnd()
    {
      return -1;
    }
    
    public String getSelectedText()
    {
      return null;
    }
    
    private String getText(int paramInt1, int paramInt2)
      throws BadLocationException
    {
      View localView = (View)getClientProperty("html");
      if (localView != null)
      {
        Document localDocument = localView.getDocument();
        if ((localDocument instanceof StyledDocument))
        {
          StyledDocument localStyledDocument = (StyledDocument)localDocument;
          return localStyledDocument.getText(paramInt1, paramInt2);
        }
      }
      return null;
    }
    
    private Rectangle getTextRectangle()
    {
      String str1 = getText();
      Icon localIcon = isEnabled() ? getIcon() : getDisabledIcon();
      if ((localIcon == null) && (str1 == null)) {
        return null;
      }
      Rectangle localRectangle1 = new Rectangle();
      Rectangle localRectangle2 = new Rectangle();
      Rectangle localRectangle3 = new Rectangle();
      Insets localInsets = new Insets(0, 0, 0, 0);
      localInsets = getInsets(localInsets);
      x = left;
      y = top;
      width = (getWidth() - (left + right));
      height = (getHeight() - (top + bottom));
      String str2 = SwingUtilities.layoutCompoundLabel(AbstractButton.this, getFontMetrics(getFont()), str1, localIcon, getVerticalAlignment(), getHorizontalAlignment(), getVerticalTextPosition(), getHorizontalTextPosition(), localRectangle3, localRectangle1, localRectangle2, 0);
      return localRectangle2;
    }
    
    AccessibleExtendedComponent getAccessibleExtendedComponent()
    {
      return this;
    }
    
    public String getToolTipText()
    {
      return AbstractButton.this.getToolTipText();
    }
    
    public String getTitledBorderText()
    {
      return super.getTitledBorderText();
    }
    
    public AccessibleKeyBinding getAccessibleKeyBinding()
    {
      int i = getMnemonic();
      if (i == 0) {
        return null;
      }
      return new ButtonKeyBinding(i);
    }
    
    class ButtonKeyBinding
      implements AccessibleKeyBinding
    {
      int mnemonic;
      
      ButtonKeyBinding(int paramInt)
      {
        mnemonic = paramInt;
      }
      
      public int getAccessibleKeyBindingCount()
      {
        return 1;
      }
      
      public Object getAccessibleKeyBinding(int paramInt)
      {
        if (paramInt != 0) {
          throw new IllegalArgumentException();
        }
        return KeyStroke.getKeyStroke(mnemonic, 0);
      }
    }
  }
  
  private static class ButtonActionPropertyChangeListener
    extends ActionPropertyChangeListener<AbstractButton>
  {
    ButtonActionPropertyChangeListener(AbstractButton paramAbstractButton, Action paramAction)
    {
      super(paramAction);
    }
    
    protected void actionPropertyChanged(AbstractButton paramAbstractButton, Action paramAction, PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (AbstractAction.shouldReconfigure(paramPropertyChangeEvent)) {
        paramAbstractButton.configurePropertiesFromAction(paramAction);
      } else {
        paramAbstractButton.actionPropertyChanged(paramAction, paramPropertyChangeEvent.getPropertyName());
      }
    }
  }
  
  protected class ButtonChangeListener
    implements ChangeListener, Serializable
  {
    ButtonChangeListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      AbstractButton.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  class Handler
    implements ActionListener, ChangeListener, ItemListener, Serializable
  {
    Handler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      Object localObject = paramChangeEvent.getSource();
      AbstractButton.this.updateMnemonicProperties();
      if (isEnabled() != model.isEnabled()) {
        setEnabled(model.isEnabled());
      }
      fireStateChanged();
      repaint();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      fireActionPerformed(paramActionEvent);
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      fireItemStateChanged(paramItemEvent);
      if (shouldUpdateSelectedStateFromAction())
      {
        Action localAction = getAction();
        if ((localAction != null) && (AbstractAction.hasSelectedKey(localAction)))
        {
          boolean bool1 = isSelected();
          boolean bool2 = AbstractAction.isSelected(localAction);
          if (bool2 != bool1) {
            localAction.putValue("SwingSelectedKey", Boolean.valueOf(bool1));
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\AbstractButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */