package javax.swing;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.AccessibleJTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.TextAction;

public class JTextField
  extends JTextComponent
  implements SwingConstants
{
  private Action action;
  private PropertyChangeListener actionPropertyChangeListener;
  public static final String notifyAction = "notify-field-accept";
  private BoundedRangeModel visibility;
  private int horizontalAlignment = 10;
  private int columns;
  private int columnWidth;
  private String command;
  private static final Action[] defaultActions = { new NotifyAction() };
  private static final String uiClassID = "TextFieldUI";
  
  public JTextField()
  {
    this(null, null, 0);
  }
  
  public JTextField(String paramString)
  {
    this(null, paramString, 0);
  }
  
  public JTextField(int paramInt)
  {
    this(null, null, paramInt);
  }
  
  public JTextField(String paramString, int paramInt)
  {
    this(null, paramString, paramInt);
  }
  
  public JTextField(Document paramDocument, String paramString, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("columns less than zero.");
    }
    visibility = new DefaultBoundedRangeModel();
    visibility.addChangeListener(new ScrollRepainter());
    columns = paramInt;
    if (paramDocument == null) {
      paramDocument = createDefaultModel();
    }
    setDocument(paramDocument);
    if (paramString != null) {
      setText(paramString);
    }
  }
  
  public String getUIClassID()
  {
    return "TextFieldUI";
  }
  
  public void setDocument(Document paramDocument)
  {
    if (paramDocument != null) {
      paramDocument.putProperty("filterNewlines", Boolean.TRUE);
    }
    super.setDocument(paramDocument);
  }
  
  public boolean isValidateRoot()
  {
    return !(SwingUtilities.getUnwrappedParent(this) instanceof JViewport);
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
    if ((paramInt == 2) || (paramInt == 0) || (paramInt == 4) || (paramInt == 10) || (paramInt == 11)) {
      horizontalAlignment = paramInt;
    } else {
      throw new IllegalArgumentException("horizontalAlignment");
    }
    firePropertyChange("horizontalAlignment", i, horizontalAlignment);
    invalidate();
    repaint();
  }
  
  protected Document createDefaultModel()
  {
    return new PlainDocument();
  }
  
  public int getColumns()
  {
    return columns;
  }
  
  public void setColumns(int paramInt)
  {
    int i = columns;
    if (paramInt < 0) {
      throw new IllegalArgumentException("columns less than zero.");
    }
    if (paramInt != i)
    {
      columns = paramInt;
      invalidate();
    }
  }
  
  protected int getColumnWidth()
  {
    if (columnWidth == 0)
    {
      FontMetrics localFontMetrics = getFontMetrics(getFont());
      columnWidth = localFontMetrics.charWidth('m');
    }
    return columnWidth;
  }
  
  public Dimension getPreferredSize()
  {
    Dimension localDimension = super.getPreferredSize();
    if (columns != 0)
    {
      Insets localInsets = getInsets();
      width = (columns * getColumnWidth() + left + right);
    }
    return localDimension;
  }
  
  public void setFont(Font paramFont)
  {
    super.setFont(paramFont);
    columnWidth = 0;
  }
  
  public synchronized void addActionListener(ActionListener paramActionListener)
  {
    listenerList.add(ActionListener.class, paramActionListener);
  }
  
  public synchronized void removeActionListener(ActionListener paramActionListener)
  {
    if ((paramActionListener != null) && (getAction() == paramActionListener)) {
      setAction(null);
    } else {
      listenerList.remove(ActionListener.class, paramActionListener);
    }
  }
  
  public synchronized ActionListener[] getActionListeners()
  {
    return (ActionListener[])listenerList.getListeners(ActionListener.class);
  }
  
  protected void fireActionPerformed()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    int i = 0;
    AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
    if ((localAWTEvent instanceof InputEvent)) {
      i = ((InputEvent)localAWTEvent).getModifiers();
    } else if ((localAWTEvent instanceof ActionEvent)) {
      i = ((ActionEvent)localAWTEvent).getModifiers();
    }
    ActionEvent localActionEvent = new ActionEvent(this, 1001, command != null ? command : getText(), EventQueue.getMostRecentEventTime(), i);
    for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
      if (arrayOfObject[j] == ActionListener.class) {
        ((ActionListener)arrayOfObject[(j + 1)]).actionPerformed(localActionEvent);
      }
    }
  }
  
  public void setActionCommand(String paramString)
  {
    command = paramString;
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
    AbstractAction.setEnabledFromAction(this, paramAction);
    AbstractAction.setToolTipTextFromAction(this, paramAction);
    setActionCommandFromAction(paramAction);
  }
  
  protected void actionPropertyChanged(Action paramAction, String paramString)
  {
    if (paramString == "ActionCommandKey") {
      setActionCommandFromAction(paramAction);
    } else if (paramString == "enabled") {
      AbstractAction.setEnabledFromAction(this, paramAction);
    } else if (paramString == "ShortDescription") {
      AbstractAction.setToolTipTextFromAction(this, paramAction);
    }
  }
  
  private void setActionCommandFromAction(Action paramAction)
  {
    setActionCommand(paramAction == null ? null : (String)paramAction.getValue("ActionCommandKey"));
  }
  
  protected PropertyChangeListener createActionPropertyChangeListener(Action paramAction)
  {
    return new TextFieldActionPropertyChangeListener(this, paramAction);
  }
  
  public Action[] getActions()
  {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  
  public void postActionEvent()
  {
    fireActionPerformed();
  }
  
  public BoundedRangeModel getHorizontalVisibility()
  {
    return visibility;
  }
  
  public int getScrollOffset()
  {
    return visibility.getValue();
  }
  
  public void setScrollOffset(int paramInt)
  {
    visibility.setValue(paramInt);
  }
  
  public void scrollRectToVisible(Rectangle paramRectangle)
  {
    Insets localInsets = getInsets();
    int i = x + visibility.getValue() - left;
    int j = i + width;
    if (i < visibility.getValue()) {
      visibility.setValue(i);
    } else if (j > visibility.getValue() + visibility.getExtent()) {
      visibility.setValue(j - visibility.getExtent());
    }
  }
  
  boolean hasActionListener()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class) {
        return true;
      }
    }
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TextFieldUI"))
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
    String str1;
    if (horizontalAlignment == 2) {
      str1 = "LEFT";
    } else if (horizontalAlignment == 0) {
      str1 = "CENTER";
    } else if (horizontalAlignment == 4) {
      str1 = "RIGHT";
    } else if (horizontalAlignment == 10) {
      str1 = "LEADING";
    } else if (horizontalAlignment == 11) {
      str1 = "TRAILING";
    } else {
      str1 = "";
    }
    String str2 = command != null ? command : "";
    return super.paramString() + ",columns=" + columns + ",columnWidth=" + columnWidth + ",command=" + str2 + ",horizontalAlignment=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJTextField();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJTextField
    extends JTextComponent.AccessibleJTextComponent
  {
    protected AccessibleJTextField()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.SINGLE_LINE);
      return localAccessibleStateSet;
    }
  }
  
  static class NotifyAction
    extends TextAction
  {
    NotifyAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTextComponent localJTextComponent = getFocusedComponent();
      if ((localJTextComponent instanceof JTextField))
      {
        JTextField localJTextField = (JTextField)localJTextComponent;
        localJTextField.postActionEvent();
      }
    }
    
    public boolean isEnabled()
    {
      JTextComponent localJTextComponent = getFocusedComponent();
      if ((localJTextComponent instanceof JTextField)) {
        return ((JTextField)localJTextComponent).hasActionListener();
      }
      return false;
    }
  }
  
  class ScrollRepainter
    implements ChangeListener, Serializable
  {
    ScrollRepainter() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      repaint();
    }
  }
  
  private static class TextFieldActionPropertyChangeListener
    extends ActionPropertyChangeListener<JTextField>
  {
    TextFieldActionPropertyChangeListener(JTextField paramJTextField, Action paramAction)
    {
      super(paramAction);
    }
    
    protected void actionPropertyChanged(JTextField paramJTextField, Action paramAction, PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (AbstractAction.shouldReconfigure(paramPropertyChangeEvent)) {
        paramJTextField.configurePropertiesFromAction(paramAction);
      } else {
        paramJTextField.actionPropertyChanged(paramAction, paramPropertyChangeEvent.getPropertyName());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JTextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */