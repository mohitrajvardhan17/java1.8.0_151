package javax.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.IllegalComponentStateException;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.ComboPopup;

public class JComboBox<E>
  extends JComponent
  implements ItemSelectable, ListDataListener, ActionListener, Accessible
{
  private static final String uiClassID = "ComboBoxUI";
  protected ComboBoxModel<E> dataModel;
  protected ListCellRenderer<? super E> renderer;
  protected ComboBoxEditor editor;
  protected int maximumRowCount = 8;
  protected boolean isEditable = false;
  protected KeySelectionManager keySelectionManager = null;
  protected String actionCommand = "comboBoxChanged";
  protected boolean lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
  protected Object selectedItemReminder = null;
  private E prototypeDisplayValue;
  private boolean firingActionEvent = false;
  private boolean selectingItem = false;
  private Action action;
  private PropertyChangeListener actionPropertyChangeListener;
  
  public JComboBox(ComboBoxModel<E> paramComboBoxModel)
  {
    setModel(paramComboBoxModel);
    init();
  }
  
  public JComboBox(E[] paramArrayOfE)
  {
    setModel(new DefaultComboBoxModel(paramArrayOfE));
    init();
  }
  
  public JComboBox(Vector<E> paramVector)
  {
    setModel(new DefaultComboBoxModel(paramVector));
    init();
  }
  
  public JComboBox()
  {
    setModel(new DefaultComboBoxModel());
    init();
  }
  
  private void init()
  {
    installAncestorListener();
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
  }
  
  protected void installAncestorListener()
  {
    addAncestorListener(new AncestorListener()
    {
      public void ancestorAdded(AncestorEvent paramAnonymousAncestorEvent)
      {
        hidePopup();
      }
      
      public void ancestorRemoved(AncestorEvent paramAnonymousAncestorEvent)
      {
        hidePopup();
      }
      
      public void ancestorMoved(AncestorEvent paramAnonymousAncestorEvent)
      {
        if (paramAnonymousAncestorEvent.getSource() != JComboBox.this) {
          hidePopup();
        }
      }
    });
  }
  
  public void setUI(ComboBoxUI paramComboBoxUI)
  {
    super.setUI(paramComboBoxUI);
  }
  
  public void updateUI()
  {
    setUI((ComboBoxUI)UIManager.getUI(this));
    ListCellRenderer localListCellRenderer = getRenderer();
    if ((localListCellRenderer instanceof Component)) {
      SwingUtilities.updateComponentTreeUI((Component)localListCellRenderer);
    }
  }
  
  public String getUIClassID()
  {
    return "ComboBoxUI";
  }
  
  public ComboBoxUI getUI()
  {
    return (ComboBoxUI)ui;
  }
  
  public void setModel(ComboBoxModel<E> paramComboBoxModel)
  {
    ComboBoxModel localComboBoxModel = dataModel;
    if (localComboBoxModel != null) {
      localComboBoxModel.removeListDataListener(this);
    }
    dataModel = paramComboBoxModel;
    dataModel.addListDataListener(this);
    selectedItemReminder = dataModel.getSelectedItem();
    firePropertyChange("model", localComboBoxModel, dataModel);
  }
  
  public ComboBoxModel<E> getModel()
  {
    return dataModel;
  }
  
  public void setLightWeightPopupEnabled(boolean paramBoolean)
  {
    boolean bool = lightWeightPopupEnabled;
    lightWeightPopupEnabled = paramBoolean;
    firePropertyChange("lightWeightPopupEnabled", bool, lightWeightPopupEnabled);
  }
  
  public boolean isLightWeightPopupEnabled()
  {
    return lightWeightPopupEnabled;
  }
  
  public void setEditable(boolean paramBoolean)
  {
    boolean bool = isEditable;
    isEditable = paramBoolean;
    firePropertyChange("editable", bool, isEditable);
  }
  
  public boolean isEditable()
  {
    return isEditable;
  }
  
  public void setMaximumRowCount(int paramInt)
  {
    int i = maximumRowCount;
    maximumRowCount = paramInt;
    firePropertyChange("maximumRowCount", i, maximumRowCount);
  }
  
  public int getMaximumRowCount()
  {
    return maximumRowCount;
  }
  
  public void setRenderer(ListCellRenderer<? super E> paramListCellRenderer)
  {
    ListCellRenderer localListCellRenderer = renderer;
    renderer = paramListCellRenderer;
    firePropertyChange("renderer", localListCellRenderer, renderer);
    invalidate();
  }
  
  public ListCellRenderer<? super E> getRenderer()
  {
    return renderer;
  }
  
  public void setEditor(ComboBoxEditor paramComboBoxEditor)
  {
    ComboBoxEditor localComboBoxEditor = editor;
    if (editor != null) {
      editor.removeActionListener(this);
    }
    editor = paramComboBoxEditor;
    if (editor != null) {
      editor.addActionListener(this);
    }
    firePropertyChange("editor", localComboBoxEditor, editor);
  }
  
  public ComboBoxEditor getEditor()
  {
    return editor;
  }
  
  public void setSelectedItem(Object paramObject)
  {
    Object localObject1 = selectedItemReminder;
    Object localObject2 = paramObject;
    if ((localObject1 == null) || (!localObject1.equals(paramObject)))
    {
      if ((paramObject != null) && (!isEditable()))
      {
        int i = 0;
        for (int j = 0; j < dataModel.getSize(); j++)
        {
          Object localObject3 = dataModel.getElementAt(j);
          if (paramObject.equals(localObject3))
          {
            i = 1;
            localObject2 = localObject3;
            break;
          }
        }
        if (i == 0) {
          return;
        }
      }
      selectingItem = true;
      dataModel.setSelectedItem(localObject2);
      selectingItem = false;
      if (selectedItemReminder != dataModel.getSelectedItem()) {
        selectedItemChanged();
      }
    }
    fireActionEvent();
  }
  
  public Object getSelectedItem()
  {
    return dataModel.getSelectedItem();
  }
  
  public void setSelectedIndex(int paramInt)
  {
    int i = dataModel.getSize();
    if (paramInt == -1)
    {
      setSelectedItem(null);
    }
    else
    {
      if ((paramInt < -1) || (paramInt >= i)) {
        throw new IllegalArgumentException("setSelectedIndex: " + paramInt + " out of bounds");
      }
      setSelectedItem(dataModel.getElementAt(paramInt));
    }
  }
  
  @Transient
  public int getSelectedIndex()
  {
    Object localObject1 = dataModel.getSelectedItem();
    int i = 0;
    int j = dataModel.getSize();
    while (i < j)
    {
      Object localObject2 = dataModel.getElementAt(i);
      if ((localObject2 != null) && (localObject2.equals(localObject1))) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  public E getPrototypeDisplayValue()
  {
    return (E)prototypeDisplayValue;
  }
  
  public void setPrototypeDisplayValue(E paramE)
  {
    Object localObject = prototypeDisplayValue;
    prototypeDisplayValue = paramE;
    firePropertyChange("prototypeDisplayValue", localObject, paramE);
  }
  
  public void addItem(E paramE)
  {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)dataModel).addElement(paramE);
  }
  
  public void insertItemAt(E paramE, int paramInt)
  {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)dataModel).insertElementAt(paramE, paramInt);
  }
  
  public void removeItem(Object paramObject)
  {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)dataModel).removeElement(paramObject);
  }
  
  public void removeItemAt(int paramInt)
  {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)dataModel).removeElementAt(paramInt);
  }
  
  public void removeAllItems()
  {
    checkMutableComboBoxModel();
    MutableComboBoxModel localMutableComboBoxModel = (MutableComboBoxModel)dataModel;
    int i = localMutableComboBoxModel.getSize();
    if ((localMutableComboBoxModel instanceof DefaultComboBoxModel)) {
      ((DefaultComboBoxModel)localMutableComboBoxModel).removeAllElements();
    } else {
      for (int j = 0; j < i; j++)
      {
        Object localObject = localMutableComboBoxModel.getElementAt(0);
        localMutableComboBoxModel.removeElement(localObject);
      }
    }
    selectedItemReminder = null;
    if (isEditable()) {
      editor.setItem(null);
    }
  }
  
  void checkMutableComboBoxModel()
  {
    if (!(dataModel instanceof MutableComboBoxModel)) {
      throw new RuntimeException("Cannot use this method with a non-Mutable data model.");
    }
  }
  
  public void showPopup()
  {
    setPopupVisible(true);
  }
  
  public void hidePopup()
  {
    setPopupVisible(false);
  }
  
  public void setPopupVisible(boolean paramBoolean)
  {
    getUI().setPopupVisible(this, paramBoolean);
  }
  
  public boolean isPopupVisible()
  {
    return getUI().isPopupVisible(this);
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
  
  public void addPopupMenuListener(PopupMenuListener paramPopupMenuListener)
  {
    listenerList.add(PopupMenuListener.class, paramPopupMenuListener);
  }
  
  public void removePopupMenuListener(PopupMenuListener paramPopupMenuListener)
  {
    listenerList.remove(PopupMenuListener.class, paramPopupMenuListener);
  }
  
  public PopupMenuListener[] getPopupMenuListeners()
  {
    return (PopupMenuListener[])listenerList.getListeners(PopupMenuListener.class);
  }
  
  public void firePopupMenuWillBecomeVisible()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    PopupMenuEvent localPopupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class)
      {
        if (localPopupMenuEvent == null) {
          localPopupMenuEvent = new PopupMenuEvent(this);
        }
        ((PopupMenuListener)arrayOfObject[(i + 1)]).popupMenuWillBecomeVisible(localPopupMenuEvent);
      }
    }
  }
  
  public void firePopupMenuWillBecomeInvisible()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    PopupMenuEvent localPopupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class)
      {
        if (localPopupMenuEvent == null) {
          localPopupMenuEvent = new PopupMenuEvent(this);
        }
        ((PopupMenuListener)arrayOfObject[(i + 1)]).popupMenuWillBecomeInvisible(localPopupMenuEvent);
      }
    }
  }
  
  public void firePopupMenuCanceled()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    PopupMenuEvent localPopupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class)
      {
        if (localPopupMenuEvent == null) {
          localPopupMenuEvent = new PopupMenuEvent(this);
        }
        ((PopupMenuListener)arrayOfObject[(i + 1)]).popupMenuCanceled(localPopupMenuEvent);
      }
    }
  }
  
  public void setActionCommand(String paramString)
  {
    actionCommand = paramString;
  }
  
  public String getActionCommand()
  {
    return actionCommand;
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
  
  protected PropertyChangeListener createActionPropertyChangeListener(Action paramAction)
  {
    return new ComboBoxActionPropertyChangeListener(this, paramAction);
  }
  
  protected void actionPropertyChanged(Action paramAction, String paramString)
  {
    if (paramString == "ActionCommandKey") {
      setActionCommandFromAction(paramAction);
    } else if (paramString == "enabled") {
      AbstractAction.setEnabledFromAction(this, paramAction);
    } else if ("ShortDescription" == paramString) {
      AbstractAction.setToolTipTextFromAction(this, paramAction);
    }
  }
  
  private void setActionCommandFromAction(Action paramAction)
  {
    setActionCommand(paramAction != null ? (String)paramAction.getValue("ActionCommandKey") : null);
  }
  
  protected void fireItemStateChanged(ItemEvent paramItemEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ItemListener.class) {
        ((ItemListener)arrayOfObject[(i + 1)]).itemStateChanged(paramItemEvent);
      }
    }
  }
  
  protected void fireActionEvent()
  {
    if (!firingActionEvent)
    {
      firingActionEvent = true;
      ActionEvent localActionEvent = null;
      Object[] arrayOfObject = listenerList.getListenerList();
      long l = EventQueue.getMostRecentEventTime();
      int i = 0;
      AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
      if ((localAWTEvent instanceof InputEvent)) {
        i = ((InputEvent)localAWTEvent).getModifiers();
      } else if ((localAWTEvent instanceof ActionEvent)) {
        i = ((ActionEvent)localAWTEvent).getModifiers();
      }
      for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
        if (arrayOfObject[j] == ActionListener.class)
        {
          if (localActionEvent == null) {
            localActionEvent = new ActionEvent(this, 1001, getActionCommand(), l, i);
          }
          ((ActionListener)arrayOfObject[(j + 1)]).actionPerformed(localActionEvent);
        }
      }
      firingActionEvent = false;
    }
  }
  
  protected void selectedItemChanged()
  {
    if (selectedItemReminder != null) {
      fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder, 2));
    }
    selectedItemReminder = dataModel.getSelectedItem();
    if (selectedItemReminder != null) {
      fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder, 1));
    }
  }
  
  public Object[] getSelectedObjects()
  {
    Object localObject = getSelectedItem();
    if (localObject == null) {
      return new Object[0];
    }
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = localObject;
    return arrayOfObject;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    ComboBoxEditor localComboBoxEditor = getEditor();
    if ((localComboBoxEditor != null) && (paramActionEvent != null) && ((localComboBoxEditor == paramActionEvent.getSource()) || (localComboBoxEditor.getEditorComponent() == paramActionEvent.getSource())))
    {
      setPopupVisible(false);
      getModel().setSelectedItem(localComboBoxEditor.getItem());
      String str = getActionCommand();
      setActionCommand("comboBoxEdited");
      fireActionEvent();
      setActionCommand(str);
    }
  }
  
  public void contentsChanged(ListDataEvent paramListDataEvent)
  {
    Object localObject1 = selectedItemReminder;
    Object localObject2 = dataModel.getSelectedItem();
    if ((localObject1 == null) || (!localObject1.equals(localObject2)))
    {
      selectedItemChanged();
      if (!selectingItem) {
        fireActionEvent();
      }
    }
  }
  
  public void intervalAdded(ListDataEvent paramListDataEvent)
  {
    if (selectedItemReminder != dataModel.getSelectedItem()) {
      selectedItemChanged();
    }
  }
  
  public void intervalRemoved(ListDataEvent paramListDataEvent)
  {
    contentsChanged(paramListDataEvent);
  }
  
  public boolean selectWithKeyChar(char paramChar)
  {
    if (keySelectionManager == null) {
      keySelectionManager = createDefaultKeySelectionManager();
    }
    int i = keySelectionManager.selectionForKey(paramChar, getModel());
    if (i != -1)
    {
      setSelectedIndex(i);
      return true;
    }
    return false;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    firePropertyChange("enabled", !isEnabled(), isEnabled());
  }
  
  public void configureEditor(ComboBoxEditor paramComboBoxEditor, Object paramObject)
  {
    paramComboBoxEditor.setItem(paramObject);
  }
  
  public void processKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getKeyCode() == 9) {
      hidePopup();
    }
    super.processKeyEvent(paramKeyEvent);
  }
  
  protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean)
  {
    if (super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean)) {
      return true;
    }
    if ((!isEditable()) || (paramInt != 0) || (getEditor() == null) || (!Boolean.TRUE.equals(getClientProperty("JComboBox.isTableCellEditor")))) {
      return false;
    }
    Component localComponent = getEditor().getEditorComponent();
    if ((localComponent instanceof JComponent))
    {
      JComponent localJComponent = (JComponent)localComponent;
      return localJComponent.processKeyBinding(paramKeyStroke, paramKeyEvent, 0, paramBoolean);
    }
    return false;
  }
  
  public void setKeySelectionManager(KeySelectionManager paramKeySelectionManager)
  {
    keySelectionManager = paramKeySelectionManager;
  }
  
  public KeySelectionManager getKeySelectionManager()
  {
    return keySelectionManager;
  }
  
  public int getItemCount()
  {
    return dataModel.getSize();
  }
  
  public E getItemAt(int paramInt)
  {
    return (E)dataModel.getElementAt(paramInt);
  }
  
  protected KeySelectionManager createDefaultKeySelectionManager()
  {
    return new DefaultKeySelectionManager();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ComboBoxUI"))
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
    String str1 = selectedItemReminder != null ? selectedItemReminder.toString() : "";
    String str2 = isEditable ? "true" : "false";
    String str3 = lightWeightPopupEnabled ? "true" : "false";
    return super.paramString() + ",isEditable=" + str2 + ",lightWeightPopupEnabled=" + str3 + ",maximumRowCount=" + maximumRowCount + ",selectedItemReminder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJComboBox();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJComboBox
    extends JComponent.AccessibleJComponent
    implements AccessibleAction, AccessibleSelection
  {
    private JList popupList;
    private Accessible previousSelectedAccessible = null;
    private JComboBox<E>.AccessibleJComboBox.EditorAccessibleContext editorAccessibleContext = null;
    
    public AccessibleJComboBox()
    {
      super();
      addPropertyChangeListener(new AccessibleJComboBoxPropertyChangeListener(null));
      setEditorNameAndDescription();
      Accessible localAccessible = getUI().getAccessibleChild(JComboBox.this, 0);
      if ((localAccessible instanceof ComboPopup))
      {
        popupList = ((ComboPopup)localAccessible).getList();
        popupList.addListSelectionListener(new AccessibleJComboBoxListSelectionListener(null));
      }
      addPopupMenuListener(new AccessibleJComboBoxPopupMenuListener(null));
    }
    
    private void setEditorNameAndDescription()
    {
      ComboBoxEditor localComboBoxEditor = getEditor();
      if (localComboBoxEditor != null)
      {
        Component localComponent = localComboBoxEditor.getEditorComponent();
        if ((localComponent instanceof Accessible))
        {
          AccessibleContext localAccessibleContext = localComponent.getAccessibleContext();
          if (localAccessibleContext != null)
          {
            localAccessibleContext.setAccessibleName(getAccessibleName());
            localAccessibleContext.setAccessibleDescription(getAccessibleDescription());
          }
        }
      }
    }
    
    public int getAccessibleChildrenCount()
    {
      if (ui != null) {
        return ui.getAccessibleChildrenCount(JComboBox.this);
      }
      return super.getAccessibleChildrenCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if (ui != null) {
        return ui.getAccessibleChild(JComboBox.this, paramInt);
      }
      return super.getAccessibleChild(paramInt);
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.COMBO_BOX;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (localAccessibleStateSet == null) {
        localAccessibleStateSet = new AccessibleStateSet();
      }
      if (isPopupVisible()) {
        localAccessibleStateSet.add(AccessibleState.EXPANDED);
      } else {
        localAccessibleStateSet.add(AccessibleState.COLLAPSED);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      if (paramInt == 0) {
        return UIManager.getString("ComboBox.togglePopupText");
      }
      return null;
    }
    
    public int getAccessibleActionCount()
    {
      return 1;
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      if (paramInt == 0)
      {
        setPopupVisible(!isPopupVisible());
        return true;
      }
      return false;
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      Object localObject = getSelectedItem();
      if (localObject != null) {
        return 1;
      }
      return 0;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      Accessible localAccessible = getUI().getAccessibleChild(JComboBox.this, 0);
      if ((localAccessible != null) && ((localAccessible instanceof ComboPopup)))
      {
        JList localJList = ((ComboPopup)localAccessible).getList();
        AccessibleContext localAccessibleContext = localJList.getAccessibleContext();
        if (localAccessibleContext != null)
        {
          AccessibleSelection localAccessibleSelection = localAccessibleContext.getAccessibleSelection();
          if (localAccessibleSelection != null) {
            return localAccessibleSelection.getAccessibleSelection(paramInt);
          }
        }
      }
      return null;
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return getSelectedIndex() == paramInt;
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      clearAccessibleSelection();
      setSelectedIndex(paramInt);
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      if (getSelectedIndex() == paramInt) {
        clearAccessibleSelection();
      }
    }
    
    public void clearAccessibleSelection()
    {
      setSelectedIndex(-1);
    }
    
    public void selectAllAccessibleSelection() {}
    
    private class AccessibleEditor
      implements Accessible
    {
      private AccessibleEditor() {}
      
      public AccessibleContext getAccessibleContext()
      {
        if (editorAccessibleContext == null)
        {
          Component localComponent = getEditor().getEditorComponent();
          if ((localComponent instanceof Accessible)) {
            editorAccessibleContext = new JComboBox.AccessibleJComboBox.EditorAccessibleContext(JComboBox.AccessibleJComboBox.this, (Accessible)localComponent);
          }
        }
        return editorAccessibleContext;
      }
    }
    
    private class AccessibleJComboBoxListSelectionListener
      implements ListSelectionListener
    {
      private AccessibleJComboBoxListSelectionListener() {}
      
      public void valueChanged(ListSelectionEvent paramListSelectionEvent)
      {
        if (popupList == null) {
          return;
        }
        int i = popupList.getSelectedIndex();
        if (i < 0) {
          return;
        }
        Accessible localAccessible = popupList.getAccessibleContext().getAccessibleChild(i);
        if (localAccessible == null) {
          return;
        }
        if (previousSelectedAccessible != null)
        {
          localPropertyChangeEvent = new PropertyChangeEvent(previousSelectedAccessible, "AccessibleState", AccessibleState.FOCUSED, null);
          firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
        }
        PropertyChangeEvent localPropertyChangeEvent = new PropertyChangeEvent(localAccessible, "AccessibleState", null, AccessibleState.FOCUSED);
        firePropertyChange("AccessibleState", null, localPropertyChangeEvent);
        firePropertyChange("AccessibleActiveDescendant", previousSelectedAccessible, localAccessible);
        previousSelectedAccessible = localAccessible;
      }
    }
    
    private class AccessibleJComboBoxPopupMenuListener
      implements PopupMenuListener
    {
      private AccessibleJComboBoxPopupMenuListener() {}
      
      public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent)
      {
        if (popupList == null) {
          return;
        }
        int i = popupList.getSelectedIndex();
        if (i < 0) {
          return;
        }
        previousSelectedAccessible = popupList.getAccessibleContext().getAccessibleChild(i);
      }
      
      public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {}
      
      public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent) {}
    }
    
    private class AccessibleJComboBoxPropertyChangeListener
      implements PropertyChangeListener
    {
      private AccessibleJComboBoxPropertyChangeListener() {}
      
      public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
      {
        if (paramPropertyChangeEvent.getPropertyName() == "editor") {
          JComboBox.AccessibleJComboBox.this.setEditorNameAndDescription();
        }
      }
    }
    
    private class EditorAccessibleContext
      extends AccessibleContext
    {
      private AccessibleContext ac;
      
      private EditorAccessibleContext() {}
      
      EditorAccessibleContext(Accessible paramAccessible)
      {
        ac = paramAccessible.getAccessibleContext();
      }
      
      public String getAccessibleName()
      {
        return ac.getAccessibleName();
      }
      
      public void setAccessibleName(String paramString)
      {
        ac.setAccessibleName(paramString);
      }
      
      public String getAccessibleDescription()
      {
        return ac.getAccessibleDescription();
      }
      
      public void setAccessibleDescription(String paramString)
      {
        ac.setAccessibleDescription(paramString);
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return ac.getAccessibleRole();
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        return ac.getAccessibleStateSet();
      }
      
      public Accessible getAccessibleParent()
      {
        return ac.getAccessibleParent();
      }
      
      public void setAccessibleParent(Accessible paramAccessible)
      {
        ac.setAccessibleParent(paramAccessible);
      }
      
      public int getAccessibleIndexInParent()
      {
        return getSelectedIndex();
      }
      
      public int getAccessibleChildrenCount()
      {
        return ac.getAccessibleChildrenCount();
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        return ac.getAccessibleChild(paramInt);
      }
      
      public Locale getLocale()
        throws IllegalComponentStateException
      {
        return ac.getLocale();
      }
      
      public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        ac.addPropertyChangeListener(paramPropertyChangeListener);
      }
      
      public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        ac.removePropertyChangeListener(paramPropertyChangeListener);
      }
      
      public AccessibleAction getAccessibleAction()
      {
        return ac.getAccessibleAction();
      }
      
      public AccessibleComponent getAccessibleComponent()
      {
        return ac.getAccessibleComponent();
      }
      
      public AccessibleSelection getAccessibleSelection()
      {
        return ac.getAccessibleSelection();
      }
      
      public AccessibleText getAccessibleText()
      {
        return ac.getAccessibleText();
      }
      
      public AccessibleEditableText getAccessibleEditableText()
      {
        return ac.getAccessibleEditableText();
      }
      
      public AccessibleValue getAccessibleValue()
      {
        return ac.getAccessibleValue();
      }
      
      public AccessibleIcon[] getAccessibleIcon()
      {
        return ac.getAccessibleIcon();
      }
      
      public AccessibleRelationSet getAccessibleRelationSet()
      {
        return ac.getAccessibleRelationSet();
      }
      
      public AccessibleTable getAccessibleTable()
      {
        return ac.getAccessibleTable();
      }
      
      public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
      {
        ac.firePropertyChange(paramString, paramObject1, paramObject2);
      }
    }
  }
  
  private static class ComboBoxActionPropertyChangeListener
    extends ActionPropertyChangeListener<JComboBox<?>>
  {
    ComboBoxActionPropertyChangeListener(JComboBox<?> paramJComboBox, Action paramAction)
    {
      super(paramAction);
    }
    
    protected void actionPropertyChanged(JComboBox<?> paramJComboBox, Action paramAction, PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (AbstractAction.shouldReconfigure(paramPropertyChangeEvent)) {
        paramJComboBox.configurePropertiesFromAction(paramAction);
      } else {
        paramJComboBox.actionPropertyChanged(paramAction, paramPropertyChangeEvent.getPropertyName());
      }
    }
  }
  
  class DefaultKeySelectionManager
    implements JComboBox.KeySelectionManager, Serializable
  {
    DefaultKeySelectionManager() {}
    
    public int selectionForKey(char paramChar, ComboBoxModel paramComboBoxModel)
    {
      int k = -1;
      Object localObject1 = paramComboBoxModel.getSelectedItem();
      if (localObject1 != null)
      {
        i = 0;
        j = paramComboBoxModel.getSize();
        while (i < j)
        {
          if (localObject1 == paramComboBoxModel.getElementAt(i))
          {
            k = i;
            break;
          }
          i++;
        }
      }
      String str2 = ("" + paramChar).toLowerCase();
      paramChar = str2.charAt(0);
      k++;
      int i = k;
      int j = paramComboBoxModel.getSize();
      Object localObject2;
      String str1;
      while (i < j)
      {
        localObject2 = paramComboBoxModel.getElementAt(i);
        if ((localObject2 != null) && (localObject2.toString() != null))
        {
          str1 = localObject2.toString().toLowerCase();
          if ((str1.length() > 0) && (str1.charAt(0) == paramChar)) {
            return i;
          }
        }
        i++;
      }
      for (i = 0; i < k; i++)
      {
        localObject2 = paramComboBoxModel.getElementAt(i);
        if ((localObject2 != null) && (localObject2.toString() != null))
        {
          str1 = localObject2.toString().toLowerCase();
          if ((str1.length() > 0) && (str1.charAt(0) == paramChar)) {
            return i;
          }
        }
      }
      return -1;
    }
  }
  
  public static abstract interface KeySelectionManager
  {
    public abstract int selectionForKey(char paramChar, ComboBoxModel paramComboBoxModel);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JComboBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */