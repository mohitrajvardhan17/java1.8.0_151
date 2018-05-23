package javax.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.im.InputContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.NumberFormatter;
import javax.swing.text.TextAction;

public class JFormattedTextField
  extends JTextField
{
  private static final String uiClassID = "FormattedTextFieldUI";
  private static final Action[] defaultActions = { new CommitAction(), new CancelAction() };
  public static final int COMMIT = 0;
  public static final int COMMIT_OR_REVERT = 1;
  public static final int REVERT = 2;
  public static final int PERSIST = 3;
  private AbstractFormatterFactory factory;
  private AbstractFormatter format;
  private Object value;
  private boolean editValid;
  private int focusLostBehavior;
  private boolean edited;
  private DocumentListener documentListener;
  private Object mask;
  private ActionMap textFormatterActionMap;
  private boolean composedTextExists = false;
  private FocusLostHandler focusLostHandler;
  
  public JFormattedTextField()
  {
    enableEvents(4L);
    setFocusLostBehavior(1);
  }
  
  public JFormattedTextField(Object paramObject)
  {
    this();
    setValue(paramObject);
  }
  
  public JFormattedTextField(Format paramFormat)
  {
    this();
    setFormatterFactory(getDefaultFormatterFactory(paramFormat));
  }
  
  public JFormattedTextField(AbstractFormatter paramAbstractFormatter)
  {
    this(new DefaultFormatterFactory(paramAbstractFormatter));
  }
  
  public JFormattedTextField(AbstractFormatterFactory paramAbstractFormatterFactory)
  {
    this();
    setFormatterFactory(paramAbstractFormatterFactory);
  }
  
  public JFormattedTextField(AbstractFormatterFactory paramAbstractFormatterFactory, Object paramObject)
  {
    this(paramObject);
    setFormatterFactory(paramAbstractFormatterFactory);
  }
  
  public void setFocusLostBehavior(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 3) && (paramInt != 2)) {
      throw new IllegalArgumentException("setFocusLostBehavior must be one of: JFormattedTextField.COMMIT, JFormattedTextField.COMMIT_OR_REVERT, JFormattedTextField.PERSIST or JFormattedTextField.REVERT");
    }
    focusLostBehavior = paramInt;
  }
  
  public int getFocusLostBehavior()
  {
    return focusLostBehavior;
  }
  
  public void setFormatterFactory(AbstractFormatterFactory paramAbstractFormatterFactory)
  {
    AbstractFormatterFactory localAbstractFormatterFactory = factory;
    factory = paramAbstractFormatterFactory;
    firePropertyChange("formatterFactory", localAbstractFormatterFactory, paramAbstractFormatterFactory);
    setValue(getValue(), true, false);
  }
  
  public AbstractFormatterFactory getFormatterFactory()
  {
    return factory;
  }
  
  protected void setFormatter(AbstractFormatter paramAbstractFormatter)
  {
    AbstractFormatter localAbstractFormatter = format;
    if (localAbstractFormatter != null) {
      localAbstractFormatter.uninstall();
    }
    setEditValid(true);
    format = paramAbstractFormatter;
    if (paramAbstractFormatter != null) {
      paramAbstractFormatter.install(this);
    }
    setEdited(false);
    firePropertyChange("textFormatter", localAbstractFormatter, paramAbstractFormatter);
  }
  
  public AbstractFormatter getFormatter()
  {
    return format;
  }
  
  public void setValue(Object paramObject)
  {
    if ((paramObject != null) && (getFormatterFactory() == null)) {
      setFormatterFactory(getDefaultFormatterFactory(paramObject));
    }
    setValue(paramObject, true, true);
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public void commitEdit()
    throws ParseException
  {
    AbstractFormatter localAbstractFormatter = getFormatter();
    if (localAbstractFormatter != null) {
      setValue(localAbstractFormatter.stringToValue(getText()), false, true);
    }
  }
  
  private void setEditValid(boolean paramBoolean)
  {
    if (paramBoolean != editValid)
    {
      editValid = paramBoolean;
      firePropertyChange("editValid", Boolean.valueOf(!paramBoolean), Boolean.valueOf(paramBoolean));
    }
  }
  
  public boolean isEditValid()
  {
    return editValid;
  }
  
  protected void invalidEdit()
  {
    UIManager.getLookAndFeel().provideErrorFeedback(this);
  }
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent)
  {
    AttributedCharacterIterator localAttributedCharacterIterator = paramInputMethodEvent.getText();
    int i = paramInputMethodEvent.getCommittedCharacterCount();
    if (localAttributedCharacterIterator != null)
    {
      int j = localAttributedCharacterIterator.getBeginIndex();
      int k = localAttributedCharacterIterator.getEndIndex();
      composedTextExists = (k - j > i);
    }
    else
    {
      composedTextExists = false;
    }
    super.processInputMethodEvent(paramInputMethodEvent);
  }
  
  protected void processFocusEvent(FocusEvent paramFocusEvent)
  {
    super.processFocusEvent(paramFocusEvent);
    if (paramFocusEvent.isTemporary()) {
      return;
    }
    if ((isEdited()) && (paramFocusEvent.getID() == 1005))
    {
      InputContext localInputContext = getInputContext();
      if (focusLostHandler == null) {
        focusLostHandler = new FocusLostHandler(null);
      }
      if ((localInputContext != null) && (composedTextExists))
      {
        localInputContext.endComposition();
        EventQueue.invokeLater(focusLostHandler);
      }
      else
      {
        focusLostHandler.run();
      }
    }
    else if (!isEdited())
    {
      setValue(getValue(), true, true);
    }
  }
  
  public Action[] getActions()
  {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  
  public String getUIClassID()
  {
    return "FormattedTextFieldUI";
  }
  
  public void setDocument(Document paramDocument)
  {
    if ((documentListener != null) && (getDocument() != null)) {
      getDocument().removeDocumentListener(documentListener);
    }
    super.setDocument(paramDocument);
    if (documentListener == null) {
      documentListener = new DocumentHandler(null);
    }
    paramDocument.addDocumentListener(documentListener);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("FormattedTextFieldUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  private void setFormatterActions(Action[] paramArrayOfAction)
  {
    if (paramArrayOfAction == null)
    {
      if (textFormatterActionMap != null) {
        textFormatterActionMap.clear();
      }
    }
    else
    {
      Object localObject2;
      if (textFormatterActionMap == null)
      {
        Object localObject1 = getActionMap();
        textFormatterActionMap = new ActionMap();
        while (localObject1 != null)
        {
          localObject2 = ((ActionMap)localObject1).getParent();
          if (((localObject2 instanceof UIResource)) || (localObject2 == null))
          {
            ((ActionMap)localObject1).setParent(textFormatterActionMap);
            textFormatterActionMap.setParent((ActionMap)localObject2);
            break;
          }
          localObject1 = localObject2;
        }
      }
      for (int i = paramArrayOfAction.length - 1; i >= 0; i--)
      {
        localObject2 = paramArrayOfAction[i].getValue("Name");
        if (localObject2 != null) {
          textFormatterActionMap.put(localObject2, paramArrayOfAction[i]);
        }
      }
    }
  }
  
  private void setValue(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject = value;
    value = paramObject;
    if (paramBoolean1)
    {
      AbstractFormatterFactory localAbstractFormatterFactory = getFormatterFactory();
      AbstractFormatter localAbstractFormatter;
      if (localAbstractFormatterFactory != null) {
        localAbstractFormatter = localAbstractFormatterFactory.getFormatter(this);
      } else {
        localAbstractFormatter = null;
      }
      setFormatter(localAbstractFormatter);
    }
    else
    {
      setEditValid(true);
    }
    setEdited(false);
    if (paramBoolean2) {
      firePropertyChange("value", localObject, paramObject);
    }
  }
  
  private void setEdited(boolean paramBoolean)
  {
    edited = paramBoolean;
  }
  
  private boolean isEdited()
  {
    return edited;
  }
  
  private AbstractFormatterFactory getDefaultFormatterFactory(Object paramObject)
  {
    if ((paramObject instanceof DateFormat)) {
      return new DefaultFormatterFactory(new DateFormatter((DateFormat)paramObject));
    }
    if ((paramObject instanceof NumberFormat)) {
      return new DefaultFormatterFactory(new NumberFormatter((NumberFormat)paramObject));
    }
    if ((paramObject instanceof Format)) {
      return new DefaultFormatterFactory(new InternationalFormatter((Format)paramObject));
    }
    if ((paramObject instanceof Date)) {
      return new DefaultFormatterFactory(new DateFormatter());
    }
    if ((paramObject instanceof Number))
    {
      NumberFormatter localNumberFormatter1 = new NumberFormatter();
      ((NumberFormatter)localNumberFormatter1).setValueClass(paramObject.getClass());
      NumberFormatter localNumberFormatter2 = new NumberFormatter(new DecimalFormat("#.#"));
      ((NumberFormatter)localNumberFormatter2).setValueClass(paramObject.getClass());
      return new DefaultFormatterFactory(localNumberFormatter1, localNumberFormatter1, localNumberFormatter2);
    }
    return new DefaultFormatterFactory(new DefaultFormatter());
  }
  
  public static abstract class AbstractFormatter
    implements Serializable
  {
    private JFormattedTextField ftf;
    
    public AbstractFormatter() {}
    
    public void install(JFormattedTextField paramJFormattedTextField)
    {
      if (ftf != null) {
        uninstall();
      }
      ftf = paramJFormattedTextField;
      if (paramJFormattedTextField != null)
      {
        try
        {
          paramJFormattedTextField.setText(valueToString(paramJFormattedTextField.getValue()));
        }
        catch (ParseException localParseException)
        {
          paramJFormattedTextField.setText("");
          setEditValid(false);
        }
        installDocumentFilter(getDocumentFilter());
        paramJFormattedTextField.setNavigationFilter(getNavigationFilter());
        paramJFormattedTextField.setFormatterActions(getActions());
      }
    }
    
    public void uninstall()
    {
      if (ftf != null)
      {
        installDocumentFilter(null);
        ftf.setNavigationFilter(null);
        ftf.setFormatterActions(null);
      }
    }
    
    public abstract Object stringToValue(String paramString)
      throws ParseException;
    
    public abstract String valueToString(Object paramObject)
      throws ParseException;
    
    protected JFormattedTextField getFormattedTextField()
    {
      return ftf;
    }
    
    protected void invalidEdit()
    {
      JFormattedTextField localJFormattedTextField = getFormattedTextField();
      if (localJFormattedTextField != null) {
        localJFormattedTextField.invalidEdit();
      }
    }
    
    protected void setEditValid(boolean paramBoolean)
    {
      JFormattedTextField localJFormattedTextField = getFormattedTextField();
      if (localJFormattedTextField != null) {
        localJFormattedTextField.setEditValid(paramBoolean);
      }
    }
    
    protected Action[] getActions()
    {
      return null;
    }
    
    protected DocumentFilter getDocumentFilter()
    {
      return null;
    }
    
    protected NavigationFilter getNavigationFilter()
    {
      return null;
    }
    
    protected Object clone()
      throws CloneNotSupportedException
    {
      AbstractFormatter localAbstractFormatter = (AbstractFormatter)super.clone();
      ftf = null;
      return localAbstractFormatter;
    }
    
    private void installDocumentFilter(DocumentFilter paramDocumentFilter)
    {
      JFormattedTextField localJFormattedTextField = getFormattedTextField();
      if (localJFormattedTextField != null)
      {
        Document localDocument = localJFormattedTextField.getDocument();
        if ((localDocument instanceof AbstractDocument)) {
          ((AbstractDocument)localDocument).setDocumentFilter(paramDocumentFilter);
        }
        localDocument.putProperty(DocumentFilter.class, null);
      }
    }
  }
  
  public static abstract class AbstractFormatterFactory
  {
    public AbstractFormatterFactory() {}
    
    public abstract JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField paramJFormattedTextField);
  }
  
  private static class CancelAction
    extends TextAction
  {
    public CancelAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getFocusedComponent();
      if ((localJTextComponent instanceof JFormattedTextField))
      {
        JFormattedTextField localJFormattedTextField = (JFormattedTextField)localJTextComponent;
        localJFormattedTextField.setValue(localJFormattedTextField.getValue());
      }
    }
    
    public boolean isEnabled()
    {
      JTextComponent localJTextComponent = getFocusedComponent();
      if ((localJTextComponent instanceof JFormattedTextField))
      {
        JFormattedTextField localJFormattedTextField = (JFormattedTextField)localJTextComponent;
        return localJFormattedTextField.isEdited();
      }
      return super.isEnabled();
    }
  }
  
  static class CommitAction
    extends JTextField.NotifyAction
  {
    CommitAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getFocusedComponent();
      if ((localJTextComponent instanceof JFormattedTextField)) {
        try
        {
          ((JFormattedTextField)localJTextComponent).commitEdit();
        }
        catch (ParseException localParseException)
        {
          ((JFormattedTextField)localJTextComponent).invalidEdit();
          return;
        }
      }
      super.actionPerformed(paramActionEvent);
    }
    
    public boolean isEnabled()
    {
      JTextComponent localJTextComponent = getFocusedComponent();
      if ((localJTextComponent instanceof JFormattedTextField))
      {
        JFormattedTextField localJFormattedTextField = (JFormattedTextField)localJTextComponent;
        return localJFormattedTextField.isEdited();
      }
      return super.isEnabled();
    }
  }
  
  private class DocumentHandler
    implements DocumentListener, Serializable
  {
    private DocumentHandler() {}
    
    public void insertUpdate(DocumentEvent paramDocumentEvent)
    {
      JFormattedTextField.this.setEdited(true);
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent)
    {
      JFormattedTextField.this.setEdited(true);
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent) {}
  }
  
  private class FocusLostHandler
    implements Runnable, Serializable
  {
    private FocusLostHandler() {}
    
    public void run()
    {
      int i = getFocusLostBehavior();
      if ((i == 0) || (i == 1)) {
        try
        {
          commitEdit();
          JFormattedTextField.this.setValue(getValue(), true, true);
        }
        catch (ParseException localParseException)
        {
          if (i == 1) {
            JFormattedTextField.this.setValue(getValue(), true, true);
          }
        }
      } else if (i == 2) {
        JFormattedTextField.this.setValue(getValue(), true, true);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JFormattedTextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */