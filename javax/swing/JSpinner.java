package javax.swing;

import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.NumberFormatter;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public class JSpinner
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "SpinnerUI";
  private static final Action DISABLED_ACTION = new DisabledAction(null);
  private SpinnerModel model;
  private JComponent editor;
  private ChangeListener modelListener;
  private transient ChangeEvent changeEvent;
  private boolean editorExplicitlySet = false;
  
  public JSpinner(SpinnerModel paramSpinnerModel)
  {
    if (paramSpinnerModel == null) {
      throw new NullPointerException("model cannot be null");
    }
    model = paramSpinnerModel;
    editor = createEditor(paramSpinnerModel);
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
  }
  
  public JSpinner()
  {
    this(new SpinnerNumberModel());
  }
  
  public SpinnerUI getUI()
  {
    return (SpinnerUI)ui;
  }
  
  public void setUI(SpinnerUI paramSpinnerUI)
  {
    super.setUI(paramSpinnerUI);
  }
  
  public String getUIClassID()
  {
    return "SpinnerUI";
  }
  
  public void updateUI()
  {
    setUI((SpinnerUI)UIManager.getUI(this));
    invalidate();
  }
  
  protected JComponent createEditor(SpinnerModel paramSpinnerModel)
  {
    if ((paramSpinnerModel instanceof SpinnerDateModel)) {
      return new DateEditor(this);
    }
    if ((paramSpinnerModel instanceof SpinnerListModel)) {
      return new ListEditor(this);
    }
    if ((paramSpinnerModel instanceof SpinnerNumberModel)) {
      return new NumberEditor(this);
    }
    return new DefaultEditor(this);
  }
  
  public void setModel(SpinnerModel paramSpinnerModel)
  {
    if (paramSpinnerModel == null) {
      throw new IllegalArgumentException("null model");
    }
    if (!paramSpinnerModel.equals(model))
    {
      SpinnerModel localSpinnerModel = model;
      model = paramSpinnerModel;
      if (modelListener != null)
      {
        localSpinnerModel.removeChangeListener(modelListener);
        model.addChangeListener(modelListener);
      }
      firePropertyChange("model", localSpinnerModel, paramSpinnerModel);
      if (!editorExplicitlySet)
      {
        setEditor(createEditor(paramSpinnerModel));
        editorExplicitlySet = false;
      }
      repaint();
      revalidate();
    }
  }
  
  public SpinnerModel getModel()
  {
    return model;
  }
  
  public Object getValue()
  {
    return getModel().getValue();
  }
  
  public void setValue(Object paramObject)
  {
    getModel().setValue(paramObject);
  }
  
  public Object getNextValue()
  {
    return getModel().getNextValue();
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    if (modelListener == null)
    {
      modelListener = new ModelListener(null);
      getModel().addChangeListener(modelListener);
    }
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
  
  public Object getPreviousValue()
  {
    return getModel().getPreviousValue();
  }
  
  public void setEditor(JComponent paramJComponent)
  {
    if (paramJComponent == null) {
      throw new IllegalArgumentException("null editor");
    }
    if (!paramJComponent.equals(editor))
    {
      JComponent localJComponent = editor;
      editor = paramJComponent;
      if ((localJComponent instanceof DefaultEditor)) {
        ((DefaultEditor)localJComponent).dismiss(this);
      }
      editorExplicitlySet = true;
      firePropertyChange("editor", localJComponent, paramJComponent);
      revalidate();
      repaint();
    }
  }
  
  public JComponent getEditor()
  {
    return editor;
  }
  
  public void commitEdit()
    throws ParseException
  {
    JComponent localJComponent = getEditor();
    if ((localJComponent instanceof DefaultEditor)) {
      ((DefaultEditor)localJComponent).commitEdit();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SpinnerUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJSpinner();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJSpinner
    extends JComponent.AccessibleJComponent
    implements AccessibleValue, AccessibleAction, AccessibleText, AccessibleEditableText, ChangeListener
  {
    private Object oldModelValue = null;
    
    protected AccessibleJSpinner()
    {
      super();
      addChangeListener(this);
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (paramChangeEvent == null) {
        throw new NullPointerException();
      }
      Object localObject = model.getValue();
      firePropertyChange("AccessibleValue", oldModelValue, localObject);
      firePropertyChange("AccessibleText", null, Integer.valueOf(0));
      oldModelValue = localObject;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SPIN_BOX;
    }
    
    public int getAccessibleChildrenCount()
    {
      if (editor.getAccessibleContext() != null) {
        return 1;
      }
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if (paramInt != 0) {
        return null;
      }
      if (editor.getAccessibleContext() != null) {
        return (Accessible)editor;
      }
      return null;
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public AccessibleText getAccessibleText()
    {
      return this;
    }
    
    private AccessibleContext getEditorAccessibleContext()
    {
      if ((editor instanceof JSpinner.DefaultEditor))
      {
        JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)editor).getTextField();
        if (localJFormattedTextField != null) {
          return localJFormattedTextField.getAccessibleContext();
        }
      }
      else if ((editor instanceof Accessible))
      {
        return editor.getAccessibleContext();
      }
      return null;
    }
    
    private AccessibleText getEditorAccessibleText()
    {
      AccessibleContext localAccessibleContext = getEditorAccessibleContext();
      if (localAccessibleContext != null) {
        return localAccessibleContext.getAccessibleText();
      }
      return null;
    }
    
    private AccessibleEditableText getEditorAccessibleEditableText()
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if ((localAccessibleText instanceof AccessibleEditableText)) {
        return (AccessibleEditableText)localAccessibleText;
      }
      return null;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public Number getCurrentAccessibleValue()
    {
      Object localObject = model.getValue();
      if ((localObject instanceof Number)) {
        return (Number)localObject;
      }
      return null;
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      try
      {
        model.setValue(paramNumber);
        return true;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return false;
    }
    
    public Number getMinimumAccessibleValue()
    {
      if ((model instanceof SpinnerNumberModel))
      {
        SpinnerNumberModel localSpinnerNumberModel = (SpinnerNumberModel)model;
        Comparable localComparable = localSpinnerNumberModel.getMinimum();
        if ((localComparable instanceof Number)) {
          return (Number)localComparable;
        }
      }
      return null;
    }
    
    public Number getMaximumAccessibleValue()
    {
      if ((model instanceof SpinnerNumberModel))
      {
        SpinnerNumberModel localSpinnerNumberModel = (SpinnerNumberModel)model;
        Comparable localComparable = localSpinnerNumberModel.getMaximum();
        if ((localComparable instanceof Number)) {
          return (Number)localComparable;
        }
      }
      return null;
    }
    
    public int getAccessibleActionCount()
    {
      return 2;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      if (paramInt == 0) {
        return AccessibleAction.INCREMENT;
      }
      if (paramInt == 1) {
        return AccessibleAction.DECREMENT;
      }
      return null;
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 1)) {
        return false;
      }
      Object localObject;
      if (paramInt == 0) {
        localObject = getNextValue();
      } else {
        localObject = getPreviousValue();
      }
      try
      {
        model.setValue(localObject);
        return true;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return false;
    }
    
    private boolean sameWindowAncestor(Component paramComponent1, Component paramComponent2)
    {
      if ((paramComponent1 == null) || (paramComponent2 == null)) {
        return false;
      }
      return SwingUtilities.getWindowAncestor(paramComponent1) == SwingUtilities.getWindowAncestor(paramComponent2);
    }
    
    public int getIndexAtPoint(Point paramPoint)
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if ((localAccessibleText != null) && (sameWindowAncestor(JSpinner.this, editor)))
      {
        Point localPoint = SwingUtilities.convertPoint(JSpinner.this, paramPoint, editor);
        if (localPoint != null) {
          return localAccessibleText.getIndexAtPoint(localPoint);
        }
      }
      return -1;
    }
    
    public Rectangle getCharacterBounds(int paramInt)
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null)
      {
        Rectangle localRectangle = localAccessibleText.getCharacterBounds(paramInt);
        if ((localRectangle != null) && (sameWindowAncestor(JSpinner.this, editor))) {
          return SwingUtilities.convertRectangle(editor, localRectangle, JSpinner.this);
        }
      }
      return null;
    }
    
    public int getCharCount()
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCharCount();
      }
      return -1;
    }
    
    public int getCaretPosition()
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCaretPosition();
      }
      return -1;
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getAtIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getAfterIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getBeforeIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public AttributeSet getCharacterAttribute(int paramInt)
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCharacterAttribute(paramInt);
      }
      return null;
    }
    
    public int getSelectionStart()
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectionStart();
      }
      return -1;
    }
    
    public int getSelectionEnd()
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectionEnd();
      }
      return -1;
    }
    
    public String getSelectedText()
    {
      AccessibleText localAccessibleText = getEditorAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectedText();
      }
      return null;
    }
    
    public void setTextContents(String paramString)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.setTextContents(paramString);
      }
    }
    
    public void insertTextAtIndex(int paramInt, String paramString)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.insertTextAtIndex(paramInt, paramString);
      }
    }
    
    public String getTextRange(int paramInt1, int paramInt2)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        return localAccessibleEditableText.getTextRange(paramInt1, paramInt2);
      }
      return null;
    }
    
    public void delete(int paramInt1, int paramInt2)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.delete(paramInt1, paramInt2);
      }
    }
    
    public void cut(int paramInt1, int paramInt2)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.cut(paramInt1, paramInt2);
      }
    }
    
    public void paste(int paramInt)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.paste(paramInt);
      }
    }
    
    public void replaceText(int paramInt1, int paramInt2, String paramString)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.replaceText(paramInt1, paramInt2, paramString);
      }
    }
    
    public void selectText(int paramInt1, int paramInt2)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.selectText(paramInt1, paramInt2);
      }
    }
    
    public void setAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet)
    {
      AccessibleEditableText localAccessibleEditableText = getEditorAccessibleEditableText();
      if (localAccessibleEditableText != null) {
        localAccessibleEditableText.setAttributes(paramInt1, paramInt2, paramAttributeSet);
      }
    }
  }
  
  public static class DateEditor
    extends JSpinner.DefaultEditor
  {
    private static String getDefaultPattern(Locale paramLocale)
    {
      LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatProvider.class, paramLocale);
      LocaleResources localLocaleResources = localLocaleProviderAdapter.getLocaleResources(paramLocale);
      if (localLocaleResources == null) {
        localLocaleResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
      }
      return localLocaleResources.getDateTimePattern(3, 3, null);
    }
    
    public DateEditor(JSpinner paramJSpinner)
    {
      this(paramJSpinner, getDefaultPattern(paramJSpinner.getLocale()));
    }
    
    public DateEditor(JSpinner paramJSpinner, String paramString)
    {
      this(paramJSpinner, new SimpleDateFormat(paramString, paramJSpinner.getLocale()));
    }
    
    private DateEditor(JSpinner paramJSpinner, DateFormat paramDateFormat)
    {
      super();
      if (!(paramJSpinner.getModel() instanceof SpinnerDateModel)) {
        throw new IllegalArgumentException("model not a SpinnerDateModel");
      }
      SpinnerDateModel localSpinnerDateModel = (SpinnerDateModel)paramJSpinner.getModel();
      JSpinner.DateEditorFormatter localDateEditorFormatter = new JSpinner.DateEditorFormatter(localSpinnerDateModel, paramDateFormat);
      DefaultFormatterFactory localDefaultFormatterFactory = new DefaultFormatterFactory(localDateEditorFormatter);
      JFormattedTextField localJFormattedTextField = getTextField();
      localJFormattedTextField.setEditable(true);
      localJFormattedTextField.setFormatterFactory(localDefaultFormatterFactory);
      try
      {
        String str1 = localDateEditorFormatter.valueToString(localSpinnerDateModel.getStart());
        String str2 = localDateEditorFormatter.valueToString(localSpinnerDateModel.getEnd());
        localJFormattedTextField.setColumns(Math.max(str1.length(), str2.length()));
      }
      catch (ParseException localParseException) {}
    }
    
    public SimpleDateFormat getFormat()
    {
      return (SimpleDateFormat)((DateFormatter)getTextField().getFormatter()).getFormat();
    }
    
    public SpinnerDateModel getModel()
    {
      return (SpinnerDateModel)getSpinner().getModel();
    }
  }
  
  private static class DateEditorFormatter
    extends DateFormatter
  {
    private final SpinnerDateModel model;
    
    DateEditorFormatter(SpinnerDateModel paramSpinnerDateModel, DateFormat paramDateFormat)
    {
      super();
      model = paramSpinnerDateModel;
    }
    
    public void setMinimum(Comparable paramComparable)
    {
      model.setStart(paramComparable);
    }
    
    public Comparable getMinimum()
    {
      return model.getStart();
    }
    
    public void setMaximum(Comparable paramComparable)
    {
      model.setEnd(paramComparable);
    }
    
    public Comparable getMaximum()
    {
      return model.getEnd();
    }
  }
  
  public static class DefaultEditor
    extends JPanel
    implements ChangeListener, PropertyChangeListener, LayoutManager
  {
    public DefaultEditor(JSpinner paramJSpinner)
    {
      super();
      JFormattedTextField localJFormattedTextField = new JFormattedTextField();
      localJFormattedTextField.setName("Spinner.formattedTextField");
      localJFormattedTextField.setValue(paramJSpinner.getValue());
      localJFormattedTextField.addPropertyChangeListener(this);
      localJFormattedTextField.setEditable(false);
      localJFormattedTextField.setInheritsPopupMenu(true);
      String str = paramJSpinner.getToolTipText();
      if (str != null) {
        localJFormattedTextField.setToolTipText(str);
      }
      add(localJFormattedTextField);
      setLayout(this);
      paramJSpinner.addChangeListener(this);
      ActionMap localActionMap = localJFormattedTextField.getActionMap();
      if (localActionMap != null)
      {
        localActionMap.put("increment", JSpinner.DISABLED_ACTION);
        localActionMap.put("decrement", JSpinner.DISABLED_ACTION);
      }
    }
    
    public void dismiss(JSpinner paramJSpinner)
    {
      paramJSpinner.removeChangeListener(this);
    }
    
    public JSpinner getSpinner()
    {
      for (Object localObject = this; localObject != null; localObject = ((Component)localObject).getParent()) {
        if ((localObject instanceof JSpinner)) {
          return (JSpinner)localObject;
        }
      }
      return null;
    }
    
    public JFormattedTextField getTextField()
    {
      return (JFormattedTextField)getComponent(0);
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      JSpinner localJSpinner = (JSpinner)paramChangeEvent.getSource();
      getTextField().setValue(localJSpinner.getValue());
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      JSpinner localJSpinner = getSpinner();
      if (localJSpinner == null) {
        return;
      }
      Object localObject1 = paramPropertyChangeEvent.getSource();
      String str = paramPropertyChangeEvent.getPropertyName();
      if (((localObject1 instanceof JFormattedTextField)) && ("value".equals(str)))
      {
        Object localObject2 = localJSpinner.getValue();
        try
        {
          localJSpinner.setValue(getTextField().getValue());
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          try
          {
            ((JFormattedTextField)localObject1).setValue(localObject2);
          }
          catch (IllegalArgumentException localIllegalArgumentException2) {}
        }
      }
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    private Dimension insetSize(Container paramContainer)
    {
      Insets localInsets = paramContainer.getInsets();
      int i = left + right;
      int j = top + bottom;
      return new Dimension(i, j);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      Dimension localDimension1 = insetSize(paramContainer);
      if (paramContainer.getComponentCount() > 0)
      {
        Dimension localDimension2 = getComponent(0).getPreferredSize();
        width += width;
        height += height;
      }
      return localDimension1;
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      Dimension localDimension1 = insetSize(paramContainer);
      if (paramContainer.getComponentCount() > 0)
      {
        Dimension localDimension2 = getComponent(0).getMinimumSize();
        width += width;
        height += height;
      }
      return localDimension1;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      if (paramContainer.getComponentCount() > 0)
      {
        Insets localInsets = paramContainer.getInsets();
        int i = paramContainer.getWidth() - (left + right);
        int j = paramContainer.getHeight() - (top + bottom);
        getComponent(0).setBounds(left, top, i, j);
      }
    }
    
    public void commitEdit()
      throws ParseException
    {
      JFormattedTextField localJFormattedTextField = getTextField();
      localJFormattedTextField.commitEdit();
    }
    
    public int getBaseline(int paramInt1, int paramInt2)
    {
      super.getBaseline(paramInt1, paramInt2);
      Insets localInsets = getInsets();
      paramInt1 = paramInt1 - left - right;
      paramInt2 = paramInt2 - top - bottom;
      int i = getComponent(0).getBaseline(paramInt1, paramInt2);
      if (i >= 0) {
        return i + top;
      }
      return -1;
    }
    
    public Component.BaselineResizeBehavior getBaselineResizeBehavior()
    {
      return getComponent(0).getBaselineResizeBehavior();
    }
  }
  
  private static class DisabledAction
    implements Action
  {
    private DisabledAction() {}
    
    public Object getValue(String paramString)
    {
      return null;
    }
    
    public void putValue(String paramString, Object paramObject) {}
    
    public void setEnabled(boolean paramBoolean) {}
    
    public boolean isEnabled()
    {
      return false;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
    
    public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
  }
  
  public static class ListEditor
    extends JSpinner.DefaultEditor
  {
    public ListEditor(JSpinner paramJSpinner)
    {
      super();
      if (!(paramJSpinner.getModel() instanceof SpinnerListModel)) {
        throw new IllegalArgumentException("model not a SpinnerListModel");
      }
      getTextField().setEditable(true);
      getTextField().setFormatterFactory(new DefaultFormatterFactory(new ListFormatter(null)));
    }
    
    public SpinnerListModel getModel()
    {
      return (SpinnerListModel)getSpinner().getModel();
    }
    
    private class ListFormatter
      extends JFormattedTextField.AbstractFormatter
    {
      private DocumentFilter filter;
      
      private ListFormatter() {}
      
      public String valueToString(Object paramObject)
        throws ParseException
      {
        if (paramObject == null) {
          return "";
        }
        return paramObject.toString();
      }
      
      public Object stringToValue(String paramString)
        throws ParseException
      {
        return paramString;
      }
      
      protected DocumentFilter getDocumentFilter()
      {
        if (filter == null) {
          filter = new Filter(null);
        }
        return filter;
      }
      
      private class Filter
        extends DocumentFilter
      {
        private Filter() {}
        
        public void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
          throws BadLocationException
        {
          if ((paramString != null) && (paramInt1 + paramInt2 == paramFilterBypass.getDocument().getLength()))
          {
            Object localObject = getModel().findNextMatch(paramFilterBypass.getDocument().getText(0, paramInt1) + paramString);
            String str = localObject != null ? localObject.toString() : null;
            if (str != null)
            {
              paramFilterBypass.remove(0, paramInt1 + paramInt2);
              paramFilterBypass.insertString(0, str, null);
              getFormattedTextField().select(paramInt1 + paramString.length(), str.length());
              return;
            }
          }
          super.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
        }
        
        public void insertString(DocumentFilter.FilterBypass paramFilterBypass, int paramInt, String paramString, AttributeSet paramAttributeSet)
          throws BadLocationException
        {
          replace(paramFilterBypass, paramInt, 0, paramString, paramAttributeSet);
        }
      }
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
  
  public static class NumberEditor
    extends JSpinner.DefaultEditor
  {
    private static String getDefaultPattern(Locale paramLocale)
    {
      LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, paramLocale);
      LocaleResources localLocaleResources = localLocaleProviderAdapter.getLocaleResources(paramLocale);
      if (localLocaleResources == null) {
        localLocaleResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
      }
      String[] arrayOfString = localLocaleResources.getNumberPatterns();
      return arrayOfString[0];
    }
    
    public NumberEditor(JSpinner paramJSpinner)
    {
      this(paramJSpinner, getDefaultPattern(paramJSpinner.getLocale()));
    }
    
    public NumberEditor(JSpinner paramJSpinner, String paramString)
    {
      this(paramJSpinner, new DecimalFormat(paramString));
    }
    
    private NumberEditor(JSpinner paramJSpinner, DecimalFormat paramDecimalFormat)
    {
      super();
      if (!(paramJSpinner.getModel() instanceof SpinnerNumberModel)) {
        throw new IllegalArgumentException("model not a SpinnerNumberModel");
      }
      SpinnerNumberModel localSpinnerNumberModel = (SpinnerNumberModel)paramJSpinner.getModel();
      JSpinner.NumberEditorFormatter localNumberEditorFormatter = new JSpinner.NumberEditorFormatter(localSpinnerNumberModel, paramDecimalFormat);
      DefaultFormatterFactory localDefaultFormatterFactory = new DefaultFormatterFactory(localNumberEditorFormatter);
      JFormattedTextField localJFormattedTextField = getTextField();
      localJFormattedTextField.setEditable(true);
      localJFormattedTextField.setFormatterFactory(localDefaultFormatterFactory);
      localJFormattedTextField.setHorizontalAlignment(4);
      try
      {
        String str1 = localNumberEditorFormatter.valueToString(localSpinnerNumberModel.getMinimum());
        String str2 = localNumberEditorFormatter.valueToString(localSpinnerNumberModel.getMaximum());
        localJFormattedTextField.setColumns(Math.max(str1.length(), str2.length()));
      }
      catch (ParseException localParseException) {}
    }
    
    public DecimalFormat getFormat()
    {
      return (DecimalFormat)((NumberFormatter)getTextField().getFormatter()).getFormat();
    }
    
    public SpinnerNumberModel getModel()
    {
      return (SpinnerNumberModel)getSpinner().getModel();
    }
  }
  
  private static class NumberEditorFormatter
    extends NumberFormatter
  {
    private final SpinnerNumberModel model;
    
    NumberEditorFormatter(SpinnerNumberModel paramSpinnerNumberModel, NumberFormat paramNumberFormat)
    {
      super();
      model = paramSpinnerNumberModel;
      setValueClass(paramSpinnerNumberModel.getValue().getClass());
    }
    
    public void setMinimum(Comparable paramComparable)
    {
      model.setMinimum(paramComparable);
    }
    
    public Comparable getMinimum()
    {
      return model.getMinimum();
    }
    
    public void setMaximum(Comparable paramComparable)
    {
      model.setMaximum(paramComparable);
    }
    
    public Comparable getMaximum()
    {
      return model.getMaximum();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JSpinner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */