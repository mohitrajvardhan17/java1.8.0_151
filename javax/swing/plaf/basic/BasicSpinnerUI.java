package javax.swing.plaf.basic;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat.Field;
import java.text.Format;
import java.text.Format.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Document;
import javax.swing.text.InternationalFormatter;
import sun.swing.DefaultLookup;

public class BasicSpinnerUI
  extends SpinnerUI
{
  protected JSpinner spinner;
  private Handler handler;
  private static final ArrowButtonHandler nextButtonHandler = new ArrowButtonHandler("increment", true);
  private static final ArrowButtonHandler previousButtonHandler = new ArrowButtonHandler("decrement", false);
  private PropertyChangeListener propertyChangeListener;
  private static final Dimension zeroSize = new Dimension(0, 0);
  
  public BasicSpinnerUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicSpinnerUI();
  }
  
  private void maybeAdd(Component paramComponent, String paramString)
  {
    if (paramComponent != null) {
      spinner.add(paramComponent, paramString);
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    spinner = ((JSpinner)paramJComponent);
    installDefaults();
    installListeners();
    maybeAdd(createNextButton(), "Next");
    maybeAdd(createPreviousButton(), "Previous");
    maybeAdd(createEditor(), "Editor");
    updateEnabledState();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallListeners();
    spinner = null;
    paramJComponent.removeAll();
  }
  
  protected void installListeners()
  {
    propertyChangeListener = createPropertyChangeListener();
    spinner.addPropertyChangeListener(propertyChangeListener);
    if (DefaultLookup.getBoolean(spinner, this, "Spinner.disableOnBoundaryValues", false)) {
      spinner.addChangeListener(getHandler());
    }
    JComponent localJComponent = spinner.getEditor();
    if ((localJComponent != null) && ((localJComponent instanceof JSpinner.DefaultEditor)))
    {
      JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)localJComponent).getTextField();
      if (localJFormattedTextField != null)
      {
        localJFormattedTextField.addFocusListener(nextButtonHandler);
        localJFormattedTextField.addFocusListener(previousButtonHandler);
      }
    }
  }
  
  protected void uninstallListeners()
  {
    spinner.removePropertyChangeListener(propertyChangeListener);
    spinner.removeChangeListener(handler);
    JComponent localJComponent = spinner.getEditor();
    removeEditorBorderListener(localJComponent);
    if ((localJComponent instanceof JSpinner.DefaultEditor))
    {
      JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)localJComponent).getTextField();
      if (localJFormattedTextField != null)
      {
        localJFormattedTextField.removeFocusListener(nextButtonHandler);
        localJFormattedTextField.removeFocusListener(previousButtonHandler);
      }
    }
    propertyChangeListener = null;
    handler = null;
  }
  
  protected void installDefaults()
  {
    spinner.setLayout(createLayout());
    LookAndFeel.installBorder(spinner, "Spinner.border");
    LookAndFeel.installColorsAndFont(spinner, "Spinner.background", "Spinner.foreground", "Spinner.font");
    LookAndFeel.installProperty(spinner, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults()
  {
    spinner.setLayout(null);
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected void installNextButtonListeners(Component paramComponent)
  {
    installButtonListeners(paramComponent, nextButtonHandler);
  }
  
  protected void installPreviousButtonListeners(Component paramComponent)
  {
    installButtonListeners(paramComponent, previousButtonHandler);
  }
  
  private void installButtonListeners(Component paramComponent, ArrowButtonHandler paramArrowButtonHandler)
  {
    if ((paramComponent instanceof JButton)) {
      ((JButton)paramComponent).addActionListener(paramArrowButtonHandler);
    }
    paramComponent.addMouseListener(paramArrowButtonHandler);
  }
  
  protected LayoutManager createLayout()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected Component createPreviousButton()
  {
    Component localComponent = createArrowButton(5);
    localComponent.setName("Spinner.previousButton");
    installPreviousButtonListeners(localComponent);
    return localComponent;
  }
  
  protected Component createNextButton()
  {
    Component localComponent = createArrowButton(1);
    localComponent.setName("Spinner.nextButton");
    installNextButtonListeners(localComponent);
    return localComponent;
  }
  
  private Component createArrowButton(int paramInt)
  {
    BasicArrowButton localBasicArrowButton = new BasicArrowButton(paramInt);
    Border localBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
    if ((localBorder instanceof UIResource)) {
      localBasicArrowButton.setBorder(new CompoundBorder(localBorder, null));
    } else {
      localBasicArrowButton.setBorder(localBorder);
    }
    localBasicArrowButton.setInheritsPopupMenu(true);
    return localBasicArrowButton;
  }
  
  protected JComponent createEditor()
  {
    JComponent localJComponent = spinner.getEditor();
    maybeRemoveEditorBorder(localJComponent);
    installEditorBorderListener(localJComponent);
    localJComponent.setInheritsPopupMenu(true);
    updateEditorAlignment(localJComponent);
    return localJComponent;
  }
  
  protected void replaceEditor(JComponent paramJComponent1, JComponent paramJComponent2)
  {
    spinner.remove(paramJComponent1);
    maybeRemoveEditorBorder(paramJComponent2);
    installEditorBorderListener(paramJComponent2);
    paramJComponent2.setInheritsPopupMenu(true);
    spinner.add(paramJComponent2, "Editor");
  }
  
  private void updateEditorAlignment(JComponent paramJComponent)
  {
    if ((paramJComponent instanceof JSpinner.DefaultEditor))
    {
      int i = UIManager.getInt("Spinner.editorAlignment");
      JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent).getTextField();
      localJFormattedTextField.setHorizontalAlignment(i);
    }
  }
  
  private void maybeRemoveEditorBorder(JComponent paramJComponent)
  {
    if (!UIManager.getBoolean("Spinner.editorBorderPainted"))
    {
      if (((paramJComponent instanceof JPanel)) && (paramJComponent.getBorder() == null) && (paramJComponent.getComponentCount() > 0)) {
        paramJComponent = (JComponent)paramJComponent.getComponent(0);
      }
      if ((paramJComponent != null) && ((paramJComponent.getBorder() instanceof UIResource))) {
        paramJComponent.setBorder(null);
      }
    }
  }
  
  private void installEditorBorderListener(JComponent paramJComponent)
  {
    if (!UIManager.getBoolean("Spinner.editorBorderPainted"))
    {
      if (((paramJComponent instanceof JPanel)) && (paramJComponent.getBorder() == null) && (paramJComponent.getComponentCount() > 0)) {
        paramJComponent = (JComponent)paramJComponent.getComponent(0);
      }
      if ((paramJComponent != null) && ((paramJComponent.getBorder() == null) || ((paramJComponent.getBorder() instanceof UIResource)))) {
        paramJComponent.addPropertyChangeListener(getHandler());
      }
    }
  }
  
  private void removeEditorBorderListener(JComponent paramJComponent)
  {
    if (!UIManager.getBoolean("Spinner.editorBorderPainted"))
    {
      if (((paramJComponent instanceof JPanel)) && (paramJComponent.getComponentCount() > 0)) {
        paramJComponent = (JComponent)paramJComponent.getComponent(0);
      }
      if (paramJComponent != null) {
        paramJComponent.removePropertyChangeListener(getHandler());
      }
    }
  }
  
  private void updateEnabledState()
  {
    updateEnabledState(spinner, spinner.isEnabled());
  }
  
  private void updateEnabledState(Container paramContainer, boolean paramBoolean)
  {
    for (int i = paramContainer.getComponentCount() - 1; i >= 0; i--)
    {
      Component localComponent = paramContainer.getComponent(i);
      if (DefaultLookup.getBoolean(spinner, this, "Spinner.disableOnBoundaryValues", false))
      {
        SpinnerModel localSpinnerModel = spinner.getModel();
        if ((localComponent.getName() == "Spinner.nextButton") && (localSpinnerModel.getNextValue() == null)) {
          localComponent.setEnabled(false);
        } else if ((localComponent.getName() == "Spinner.previousButton") && (localSpinnerModel.getPreviousValue() == null)) {
          localComponent.setEnabled(false);
        } else {
          localComponent.setEnabled(paramBoolean);
        }
      }
      else
      {
        localComponent.setEnabled(paramBoolean);
      }
      if ((localComponent instanceof Container)) {
        updateEnabledState((Container)localComponent, paramBoolean);
      }
    }
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(spinner, 1, localInputMap);
    LazyActionMap.installLazyActionMap(spinner, BasicSpinnerUI.class, "Spinner.actionMap");
  }
  
  private InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(spinner, this, "Spinner.ancestorInputMap");
    }
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put("increment", nextButtonHandler);
    paramLazyActionMap.put("decrement", previousButtonHandler);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    JComponent localJComponent = spinner.getEditor();
    Insets localInsets = spinner.getInsets();
    paramInt1 = paramInt1 - left - right;
    paramInt2 = paramInt2 - top - bottom;
    if ((paramInt1 >= 0) && (paramInt2 >= 0))
    {
      int i = localJComponent.getBaseline(paramInt1, paramInt2);
      if (i >= 0) {
        return top + i;
      }
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return spinner.getEditor().getBaselineResizeBehavior();
  }
  
  private static class ArrowButtonHandler
    extends AbstractAction
    implements FocusListener, MouseListener, UIResource
  {
    final Timer autoRepeatTimer;
    final boolean isNext;
    JSpinner spinner = null;
    JButton arrowButton = null;
    
    ArrowButtonHandler(String paramString, boolean paramBoolean)
    {
      super();
      isNext = paramBoolean;
      autoRepeatTimer = new Timer(60, this);
      autoRepeatTimer.setInitialDelay(300);
    }
    
    private JSpinner eventToSpinner(AWTEvent paramAWTEvent)
    {
      for (Object localObject = paramAWTEvent.getSource(); ((localObject instanceof Component)) && (!(localObject instanceof JSpinner)); localObject = ((Component)localObject).getParent()) {}
      return (localObject instanceof JSpinner) ? (JSpinner)localObject : null;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JSpinner localJSpinner = spinner;
      if (!(paramActionEvent.getSource() instanceof Timer))
      {
        localJSpinner = eventToSpinner(paramActionEvent);
        if ((paramActionEvent.getSource() instanceof JButton)) {
          arrowButton = ((JButton)paramActionEvent.getSource());
        }
      }
      else if ((arrowButton != null) && (!arrowButton.getModel().isPressed()) && (autoRepeatTimer.isRunning()))
      {
        autoRepeatTimer.stop();
        localJSpinner = null;
        arrowButton = null;
      }
      if (localJSpinner != null) {
        try
        {
          int i = getCalendarField(localJSpinner);
          localJSpinner.commitEdit();
          if (i != -1) {
            ((SpinnerDateModel)localJSpinner.getModel()).setCalendarField(i);
          }
          Object localObject = isNext ? localJSpinner.getNextValue() : localJSpinner.getPreviousValue();
          if (localObject != null)
          {
            localJSpinner.setValue(localObject);
            select(localJSpinner);
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJSpinner);
        }
        catch (ParseException localParseException)
        {
          UIManager.getLookAndFeel().provideErrorFeedback(localJSpinner);
        }
      }
    }
    
    private void select(JSpinner paramJSpinner)
    {
      JComponent localJComponent = paramJSpinner.getEditor();
      if ((localJComponent instanceof JSpinner.DateEditor))
      {
        JSpinner.DateEditor localDateEditor = (JSpinner.DateEditor)localJComponent;
        JFormattedTextField localJFormattedTextField = localDateEditor.getTextField();
        SimpleDateFormat localSimpleDateFormat = localDateEditor.getFormat();
        Object localObject;
        if ((localSimpleDateFormat != null) && ((localObject = paramJSpinner.getValue()) != null))
        {
          SpinnerDateModel localSpinnerDateModel = localDateEditor.getModel();
          DateFormat.Field localField = DateFormat.Field.ofCalendarField(localSpinnerDateModel.getCalendarField());
          if (localField != null) {
            try
            {
              AttributedCharacterIterator localAttributedCharacterIterator = localSimpleDateFormat.formatToCharacterIterator(localObject);
              if ((!select(localJFormattedTextField, localAttributedCharacterIterator, localField)) && (localField == DateFormat.Field.HOUR0)) {
                select(localJFormattedTextField, localAttributedCharacterIterator, DateFormat.Field.HOUR1);
              }
            }
            catch (IllegalArgumentException localIllegalArgumentException) {}
          }
        }
      }
    }
    
    private boolean select(JFormattedTextField paramJFormattedTextField, AttributedCharacterIterator paramAttributedCharacterIterator, DateFormat.Field paramField)
    {
      int i = paramJFormattedTextField.getDocument().getLength();
      paramAttributedCharacterIterator.first();
      do
      {
        Map localMap = paramAttributedCharacterIterator.getAttributes();
        if ((localMap != null) && (localMap.containsKey(paramField)))
        {
          int j = paramAttributedCharacterIterator.getRunStart(paramField);
          int k = paramAttributedCharacterIterator.getRunLimit(paramField);
          if ((j != -1) && (k != -1) && (j <= i) && (k <= i)) {
            paramJFormattedTextField.select(j, k);
          }
          return true;
        }
      } while (paramAttributedCharacterIterator.next() != 65535);
      return false;
    }
    
    private int getCalendarField(JSpinner paramJSpinner)
    {
      JComponent localJComponent = paramJSpinner.getEditor();
      if ((localJComponent instanceof JSpinner.DateEditor))
      {
        JSpinner.DateEditor localDateEditor = (JSpinner.DateEditor)localJComponent;
        JFormattedTextField localJFormattedTextField = localDateEditor.getTextField();
        int i = localJFormattedTextField.getSelectionStart();
        JFormattedTextField.AbstractFormatter localAbstractFormatter = localJFormattedTextField.getFormatter();
        if ((localAbstractFormatter instanceof InternationalFormatter))
        {
          Format.Field[] arrayOfField = ((InternationalFormatter)localAbstractFormatter).getFields(i);
          for (int j = 0; j < arrayOfField.length; j++) {
            if ((arrayOfField[j] instanceof DateFormat.Field))
            {
              int k;
              if (arrayOfField[j] == DateFormat.Field.HOUR1) {
                k = 10;
              } else {
                k = ((DateFormat.Field)arrayOfField[j]).getCalendarField();
              }
              if (k != -1) {
                return k;
              }
            }
          }
        }
      }
      return -1;
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if ((SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (paramMouseEvent.getComponent().isEnabled()))
      {
        spinner = eventToSpinner(paramMouseEvent);
        autoRepeatTimer.start();
        focusSpinnerIfNecessary();
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      autoRepeatTimer.stop();
      arrowButton = null;
      spinner = null;
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if ((spinner != null) && (!autoRepeatTimer.isRunning()) && (spinner == eventToSpinner(paramMouseEvent))) {
        autoRepeatTimer.start();
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (autoRepeatTimer.isRunning()) {
        autoRepeatTimer.stop();
      }
    }
    
    private void focusSpinnerIfNecessary()
    {
      Component localComponent1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      if ((spinner.isRequestFocusEnabled()) && ((localComponent1 == null) || (!SwingUtilities.isDescendingFrom(localComponent1, spinner))))
      {
        Object localObject = spinner;
        if (!((Container)localObject).isFocusCycleRoot()) {
          localObject = ((Container)localObject).getFocusCycleRootAncestor();
        }
        if (localObject != null)
        {
          FocusTraversalPolicy localFocusTraversalPolicy = ((Container)localObject).getFocusTraversalPolicy();
          Component localComponent2 = localFocusTraversalPolicy.getComponentAfter((Container)localObject, spinner);
          if ((localComponent2 != null) && (SwingUtilities.isDescendingFrom(localComponent2, spinner))) {
            localComponent2.requestFocus();
          }
        }
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent) {}
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      if (spinner == eventToSpinner(paramFocusEvent))
      {
        if (autoRepeatTimer.isRunning()) {
          autoRepeatTimer.stop();
        }
        spinner = null;
        if (arrowButton != null)
        {
          ButtonModel localButtonModel = arrowButton.getModel();
          localButtonModel.setPressed(false);
          localButtonModel.setArmed(false);
          arrowButton = null;
        }
      }
    }
  }
  
  private static class Handler
    implements LayoutManager, PropertyChangeListener, ChangeListener
  {
    private Component nextButton = null;
    private Component previousButton = null;
    private Component editor = null;
    
    private Handler() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      if ("Next".equals(paramString)) {
        nextButton = paramComponent;
      } else if ("Previous".equals(paramString)) {
        previousButton = paramComponent;
      } else if ("Editor".equals(paramString)) {
        editor = paramComponent;
      }
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      if (paramComponent == nextButton) {
        nextButton = null;
      } else if (paramComponent == previousButton) {
        previousButton = null;
      } else if (paramComponent == editor) {
        editor = null;
      }
    }
    
    private Dimension preferredSize(Component paramComponent)
    {
      return paramComponent == null ? BasicSpinnerUI.zeroSize : paramComponent.getPreferredSize();
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      Dimension localDimension1 = preferredSize(nextButton);
      Dimension localDimension2 = preferredSize(previousButton);
      Dimension localDimension3 = preferredSize(editor);
      height = ((height + 1) / 2 * 2);
      Dimension localDimension4 = new Dimension(width, height);
      width += Math.max(width, width);
      Insets localInsets = paramContainer.getInsets();
      width += left + right;
      height += top + bottom;
      return localDimension4;
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return preferredLayoutSize(paramContainer);
    }
    
    private void setBounds(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramComponent != null) {
        paramComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void layoutContainer(Container paramContainer)
    {
      int i = paramContainer.getWidth();
      int j = paramContainer.getHeight();
      Insets localInsets1 = paramContainer.getInsets();
      if ((nextButton == null) && (previousButton == null))
      {
        setBounds(editor, left, top, i - left - right, j - top - bottom);
        return;
      }
      Dimension localDimension1 = preferredSize(nextButton);
      Dimension localDimension2 = preferredSize(previousButton);
      int k = Math.max(width, width);
      int m = j - (top + bottom);
      Insets localInsets2 = UIManager.getInsets("Spinner.arrowButtonInsets");
      if (localInsets2 == null) {
        localInsets2 = localInsets1;
      }
      int n;
      int i1;
      int i2;
      if (paramContainer.getComponentOrientation().isLeftToRight())
      {
        n = left;
        i1 = i - left - k - right;
        i2 = i - k - right;
      }
      else
      {
        i2 = left;
        n = i2 + k;
        i1 = i - left - k - right;
      }
      int i3 = top;
      int i4 = j / 2 + j % 2 - i3;
      int i5 = top + i4;
      int i6 = j - i5 - bottom;
      setBounds(editor, n, top, i1, m);
      setBounds(nextButton, i2, i3, k, i4);
      setBounds(previousButton, i2, i5, k, i6);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      if ((paramPropertyChangeEvent.getSource() instanceof JSpinner))
      {
        localObject1 = (JSpinner)paramPropertyChangeEvent.getSource();
        localObject2 = ((JSpinner)localObject1).getUI();
        if ((localObject2 instanceof BasicSpinnerUI))
        {
          localObject3 = (BasicSpinnerUI)localObject2;
          Object localObject5;
          if ("editor".equals(str))
          {
            localObject4 = (JComponent)paramPropertyChangeEvent.getOldValue();
            localObject5 = (JComponent)paramPropertyChangeEvent.getNewValue();
            ((BasicSpinnerUI)localObject3).replaceEditor((JComponent)localObject4, (JComponent)localObject5);
            ((BasicSpinnerUI)localObject3).updateEnabledState();
            JFormattedTextField localJFormattedTextField;
            if ((localObject4 instanceof JSpinner.DefaultEditor))
            {
              localJFormattedTextField = ((JSpinner.DefaultEditor)localObject4).getTextField();
              if (localJFormattedTextField != null)
              {
                localJFormattedTextField.removeFocusListener(BasicSpinnerUI.nextButtonHandler);
                localJFormattedTextField.removeFocusListener(BasicSpinnerUI.previousButtonHandler);
              }
            }
            if ((localObject5 instanceof JSpinner.DefaultEditor))
            {
              localJFormattedTextField = ((JSpinner.DefaultEditor)localObject5).getTextField();
              if (localJFormattedTextField != null)
              {
                if ((localJFormattedTextField.getFont() instanceof UIResource)) {
                  localJFormattedTextField.setFont(((JSpinner)localObject1).getFont());
                }
                localJFormattedTextField.addFocusListener(BasicSpinnerUI.nextButtonHandler);
                localJFormattedTextField.addFocusListener(BasicSpinnerUI.previousButtonHandler);
              }
            }
          }
          else if (("enabled".equals(str)) || ("model".equals(str)))
          {
            ((BasicSpinnerUI)localObject3).updateEnabledState();
          }
          else if ("font".equals(str))
          {
            localObject4 = ((JSpinner)localObject1).getEditor();
            if ((localObject4 != null) && ((localObject4 instanceof JSpinner.DefaultEditor)))
            {
              localObject5 = ((JSpinner.DefaultEditor)localObject4).getTextField();
              if ((localObject5 != null) && ((((JTextField)localObject5).getFont() instanceof UIResource))) {
                ((JTextField)localObject5).setFont(((JSpinner)localObject1).getFont());
              }
            }
          }
          else if ("ToolTipText".equals(str))
          {
            updateToolTipTextForChildren((JComponent)localObject1);
          }
        }
      }
      else if ((paramPropertyChangeEvent.getSource() instanceof JComponent))
      {
        localObject1 = (JComponent)paramPropertyChangeEvent.getSource();
        if (((((JComponent)localObject1).getParent() instanceof JPanel)) && ((((JComponent)localObject1).getParent().getParent() instanceof JSpinner)) && ("border".equals(str)))
        {
          localObject2 = (JSpinner)((JComponent)localObject1).getParent().getParent();
          localObject3 = ((JSpinner)localObject2).getUI();
          if ((localObject3 instanceof BasicSpinnerUI))
          {
            localObject4 = (BasicSpinnerUI)localObject3;
            ((BasicSpinnerUI)localObject4).maybeRemoveEditorBorder((JComponent)localObject1);
          }
        }
      }
    }
    
    private void updateToolTipTextForChildren(JComponent paramJComponent)
    {
      String str = paramJComponent.getToolTipText();
      Component[] arrayOfComponent = paramJComponent.getComponents();
      for (int i = 0; i < arrayOfComponent.length; i++) {
        if ((arrayOfComponent[i] instanceof JSpinner.DefaultEditor))
        {
          JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)arrayOfComponent[i]).getTextField();
          if (localJFormattedTextField != null) {
            localJFormattedTextField.setToolTipText(str);
          }
        }
        else if ((arrayOfComponent[i] instanceof JComponent))
        {
          ((JComponent)arrayOfComponent[i]).setToolTipText(paramJComponent.getToolTipText());
        }
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if ((paramChangeEvent.getSource() instanceof JSpinner))
      {
        JSpinner localJSpinner = (JSpinner)paramChangeEvent.getSource();
        SpinnerUI localSpinnerUI = localJSpinner.getUI();
        if ((DefaultLookup.getBoolean(localJSpinner, localSpinnerUI, "Spinner.disableOnBoundaryValues", false)) && ((localSpinnerUI instanceof BasicSpinnerUI)))
        {
          BasicSpinnerUI localBasicSpinnerUI = (BasicSpinnerUI)localSpinnerUI;
          localBasicSpinnerUI.updateEnabledState();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicSpinnerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */