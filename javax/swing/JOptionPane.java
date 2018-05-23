package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;

public class JOptionPane
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "OptionPaneUI";
  public static final Object UNINITIALIZED_VALUE = "uninitializedValue";
  public static final int DEFAULT_OPTION = -1;
  public static final int YES_NO_OPTION = 0;
  public static final int YES_NO_CANCEL_OPTION = 1;
  public static final int OK_CANCEL_OPTION = 2;
  public static final int YES_OPTION = 0;
  public static final int NO_OPTION = 1;
  public static final int CANCEL_OPTION = 2;
  public static final int OK_OPTION = 0;
  public static final int CLOSED_OPTION = -1;
  public static final int ERROR_MESSAGE = 0;
  public static final int INFORMATION_MESSAGE = 1;
  public static final int WARNING_MESSAGE = 2;
  public static final int QUESTION_MESSAGE = 3;
  public static final int PLAIN_MESSAGE = -1;
  public static final String ICON_PROPERTY = "icon";
  public static final String MESSAGE_PROPERTY = "message";
  public static final String VALUE_PROPERTY = "value";
  public static final String OPTIONS_PROPERTY = "options";
  public static final String INITIAL_VALUE_PROPERTY = "initialValue";
  public static final String MESSAGE_TYPE_PROPERTY = "messageType";
  public static final String OPTION_TYPE_PROPERTY = "optionType";
  public static final String SELECTION_VALUES_PROPERTY = "selectionValues";
  public static final String INITIAL_SELECTION_VALUE_PROPERTY = "initialSelectionValue";
  public static final String INPUT_VALUE_PROPERTY = "inputValue";
  public static final String WANTS_INPUT_PROPERTY = "wantsInput";
  protected transient Icon icon;
  protected transient Object message;
  protected transient Object[] options;
  protected transient Object initialValue;
  protected int messageType;
  protected int optionType;
  protected transient Object value;
  protected transient Object[] selectionValues;
  protected transient Object inputValue;
  protected transient Object initialSelectionValue;
  protected boolean wantsInput;
  private static final Object sharedFrameKey = JOptionPane.class;
  
  public static String showInputDialog(Object paramObject)
    throws HeadlessException
  {
    return showInputDialog(null, paramObject);
  }
  
  public static String showInputDialog(Object paramObject1, Object paramObject2)
  {
    return showInputDialog(null, paramObject1, paramObject2);
  }
  
  public static String showInputDialog(Component paramComponent, Object paramObject)
    throws HeadlessException
  {
    return showInputDialog(paramComponent, paramObject, UIManager.getString("OptionPane.inputDialogTitle", paramComponent), 3);
  }
  
  public static String showInputDialog(Component paramComponent, Object paramObject1, Object paramObject2)
  {
    return (String)showInputDialog(paramComponent, paramObject1, UIManager.getString("OptionPane.inputDialogTitle", paramComponent), 3, null, null, paramObject2);
  }
  
  public static String showInputDialog(Component paramComponent, Object paramObject, String paramString, int paramInt)
    throws HeadlessException
  {
    return (String)showInputDialog(paramComponent, paramObject, paramString, paramInt, null, null, null);
  }
  
  public static Object showInputDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2)
    throws HeadlessException
  {
    JOptionPane localJOptionPane = new JOptionPane(paramObject1, paramInt, 2, paramIcon, null, null);
    localJOptionPane.setWantsInput(true);
    localJOptionPane.setSelectionValues(paramArrayOfObject);
    localJOptionPane.setInitialSelectionValue(paramObject2);
    localJOptionPane.setComponentOrientation((paramComponent == null ? getRootFrame() : paramComponent).getComponentOrientation());
    int i = styleFromMessageType(paramInt);
    JDialog localJDialog = localJOptionPane.createDialog(paramComponent, paramString, i);
    localJOptionPane.selectInitialValue();
    localJDialog.show();
    localJDialog.dispose();
    Object localObject = localJOptionPane.getInputValue();
    if (localObject == UNINITIALIZED_VALUE) {
      return null;
    }
    return localObject;
  }
  
  public static void showMessageDialog(Component paramComponent, Object paramObject)
    throws HeadlessException
  {
    showMessageDialog(paramComponent, paramObject, UIManager.getString("OptionPane.messageDialogTitle", paramComponent), 1);
  }
  
  public static void showMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt)
    throws HeadlessException
  {
    showMessageDialog(paramComponent, paramObject, paramString, paramInt, null);
  }
  
  public static void showMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt, Icon paramIcon)
    throws HeadlessException
  {
    showOptionDialog(paramComponent, paramObject, paramString, -1, paramInt, paramIcon, null, null);
  }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject)
    throws HeadlessException
  {
    return showConfirmDialog(paramComponent, paramObject, UIManager.getString("OptionPane.titleText"), 1);
  }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt)
    throws HeadlessException
  {
    return showConfirmDialog(paramComponent, paramObject, paramString, paramInt, 3);
  }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2)
    throws HeadlessException
  {
    return showConfirmDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, null);
  }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, Icon paramIcon)
    throws HeadlessException
  {
    return showOptionDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, paramIcon, null, null);
  }
  
  public static int showOptionDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2)
    throws HeadlessException
  {
    JOptionPane localJOptionPane = new JOptionPane(paramObject1, paramInt2, paramInt1, paramIcon, paramArrayOfObject, paramObject2);
    localJOptionPane.setInitialValue(paramObject2);
    localJOptionPane.setComponentOrientation((paramComponent == null ? getRootFrame() : paramComponent).getComponentOrientation());
    int i = styleFromMessageType(paramInt2);
    JDialog localJDialog = localJOptionPane.createDialog(paramComponent, paramString, i);
    localJOptionPane.selectInitialValue();
    localJDialog.show();
    localJDialog.dispose();
    Object localObject = localJOptionPane.getValue();
    if (localObject == null) {
      return -1;
    }
    if (paramArrayOfObject == null)
    {
      if ((localObject instanceof Integer)) {
        return ((Integer)localObject).intValue();
      }
      return -1;
    }
    int j = 0;
    int k = paramArrayOfObject.length;
    while (j < k)
    {
      if (paramArrayOfObject[j].equals(localObject)) {
        return j;
      }
      j++;
    }
    return -1;
  }
  
  public JDialog createDialog(Component paramComponent, String paramString)
    throws HeadlessException
  {
    int i = styleFromMessageType(getMessageType());
    return createDialog(paramComponent, paramString, i);
  }
  
  public JDialog createDialog(String paramString)
    throws HeadlessException
  {
    int i = styleFromMessageType(getMessageType());
    JDialog localJDialog = new JDialog((Dialog)null, paramString, true);
    initDialog(localJDialog, i, null);
    return localJDialog;
  }
  
  private JDialog createDialog(Component paramComponent, String paramString, int paramInt)
    throws HeadlessException
  {
    Window localWindow = getWindowForComponent(paramComponent);
    JDialog localJDialog;
    if ((localWindow instanceof Frame)) {
      localJDialog = new JDialog((Frame)localWindow, paramString, true);
    } else {
      localJDialog = new JDialog((Dialog)localWindow, paramString, true);
    }
    if ((localWindow instanceof SwingUtilities.SharedOwnerFrame))
    {
      WindowListener localWindowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      localJDialog.addWindowListener(localWindowListener);
    }
    initDialog(localJDialog, paramInt, paramComponent);
    return localJDialog;
  }
  
  private void initDialog(final JDialog paramJDialog, int paramInt, Component paramComponent)
  {
    paramJDialog.setComponentOrientation(getComponentOrientation());
    Container localContainer = paramJDialog.getContentPane();
    localContainer.setLayout(new BorderLayout());
    localContainer.add(this, "Center");
    paramJDialog.setResizable(false);
    if (JDialog.isDefaultLookAndFeelDecorated())
    {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool)
      {
        paramJDialog.setUndecorated(true);
        getRootPane().setWindowDecorationStyle(paramInt);
      }
    }
    paramJDialog.pack();
    paramJDialog.setLocationRelativeTo(paramComponent);
    final PropertyChangeListener local1 = new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        if ((paramJDialog.isVisible()) && (paramAnonymousPropertyChangeEvent.getSource() == JOptionPane.this) && (paramAnonymousPropertyChangeEvent.getPropertyName().equals("value")) && (paramAnonymousPropertyChangeEvent.getNewValue() != null) && (paramAnonymousPropertyChangeEvent.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)) {
          paramJDialog.setVisible(false);
        }
      }
    };
    WindowAdapter local2 = new WindowAdapter()
    {
      private boolean gotFocus = false;
      
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        setValue(null);
      }
      
      public void windowClosed(WindowEvent paramAnonymousWindowEvent)
      {
        removePropertyChangeListener(local1);
        paramJDialog.getContentPane().removeAll();
      }
      
      public void windowGainedFocus(WindowEvent paramAnonymousWindowEvent)
      {
        if (!gotFocus)
        {
          selectInitialValue();
          gotFocus = true;
        }
      }
    };
    paramJDialog.addWindowListener(local2);
    paramJDialog.addWindowFocusListener(local2);
    paramJDialog.addComponentListener(new ComponentAdapter()
    {
      public void componentShown(ComponentEvent paramAnonymousComponentEvent)
      {
        setValue(JOptionPane.UNINITIALIZED_VALUE);
      }
    });
    addPropertyChangeListener(local1);
  }
  
  public static void showInternalMessageDialog(Component paramComponent, Object paramObject)
  {
    showInternalMessageDialog(paramComponent, paramObject, UIManager.getString("OptionPane.messageDialogTitle", paramComponent), 1);
  }
  
  public static void showInternalMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt)
  {
    showInternalMessageDialog(paramComponent, paramObject, paramString, paramInt, null);
  }
  
  public static void showInternalMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt, Icon paramIcon)
  {
    showInternalOptionDialog(paramComponent, paramObject, paramString, -1, paramInt, paramIcon, null, null);
  }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject)
  {
    return showInternalConfirmDialog(paramComponent, paramObject, UIManager.getString("OptionPane.titleText"), 1);
  }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt)
  {
    return showInternalConfirmDialog(paramComponent, paramObject, paramString, paramInt, 3);
  }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2)
  {
    return showInternalConfirmDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, null);
  }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, Icon paramIcon)
  {
    return showInternalOptionDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, paramIcon, null, null);
  }
  
  public static int showInternalOptionDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2)
  {
    JOptionPane localJOptionPane = new JOptionPane(paramObject1, paramInt2, paramInt1, paramIcon, paramArrayOfObject, paramObject2);
    localJOptionPane.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
    Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    localJOptionPane.setInitialValue(paramObject2);
    JInternalFrame localJInternalFrame = localJOptionPane.createInternalFrame(paramComponent, paramString);
    localJOptionPane.selectInitialValue();
    localJInternalFrame.setVisible(true);
    Object localObject1;
    if ((localJInternalFrame.isVisible()) && (!localJInternalFrame.isShowing())) {
      for (localObject1 = localJInternalFrame.getParent(); localObject1 != null; localObject1 = ((Container)localObject1).getParent()) {
        if (!((Container)localObject1).isVisible()) {
          ((Container)localObject1).setVisible(true);
        }
      }
    }
    try
    {
      localObject1 = (Method)AccessController.doPrivileged(new ModalPrivilegedAction(Container.class, "startLWModal"));
      if (localObject1 != null) {
        ((Method)localObject1).invoke(localJInternalFrame, (Object[])null);
      }
    }
    catch (IllegalAccessException localIllegalAccessException) {}catch (IllegalArgumentException localIllegalArgumentException) {}catch (InvocationTargetException localInvocationTargetException) {}
    if ((paramComponent instanceof JInternalFrame)) {
      try
      {
        ((JInternalFrame)paramComponent).setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
    }
    Object localObject2 = localJOptionPane.getValue();
    if ((localComponent != null) && (localComponent.isShowing())) {
      localComponent.requestFocus();
    }
    if (localObject2 == null) {
      return -1;
    }
    if (paramArrayOfObject == null)
    {
      if ((localObject2 instanceof Integer)) {
        return ((Integer)localObject2).intValue();
      }
      return -1;
    }
    int i = 0;
    int j = paramArrayOfObject.length;
    while (i < j)
    {
      if (paramArrayOfObject[i].equals(localObject2)) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  public static String showInternalInputDialog(Component paramComponent, Object paramObject)
  {
    return showInternalInputDialog(paramComponent, paramObject, UIManager.getString("OptionPane.inputDialogTitle", paramComponent), 3);
  }
  
  public static String showInternalInputDialog(Component paramComponent, Object paramObject, String paramString, int paramInt)
  {
    return (String)showInternalInputDialog(paramComponent, paramObject, paramString, paramInt, null, null, null);
  }
  
  public static Object showInternalInputDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2)
  {
    JOptionPane localJOptionPane = new JOptionPane(paramObject1, paramInt, 2, paramIcon, null, null);
    localJOptionPane.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
    Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    localJOptionPane.setWantsInput(true);
    localJOptionPane.setSelectionValues(paramArrayOfObject);
    localJOptionPane.setInitialSelectionValue(paramObject2);
    JInternalFrame localJInternalFrame = localJOptionPane.createInternalFrame(paramComponent, paramString);
    localJOptionPane.selectInitialValue();
    localJInternalFrame.setVisible(true);
    Object localObject1;
    if ((localJInternalFrame.isVisible()) && (!localJInternalFrame.isShowing())) {
      for (localObject1 = localJInternalFrame.getParent(); localObject1 != null; localObject1 = ((Container)localObject1).getParent()) {
        if (!((Container)localObject1).isVisible()) {
          ((Container)localObject1).setVisible(true);
        }
      }
    }
    try
    {
      localObject1 = (Method)AccessController.doPrivileged(new ModalPrivilegedAction(Container.class, "startLWModal"));
      if (localObject1 != null) {
        ((Method)localObject1).invoke(localJInternalFrame, (Object[])null);
      }
    }
    catch (IllegalAccessException localIllegalAccessException) {}catch (IllegalArgumentException localIllegalArgumentException) {}catch (InvocationTargetException localInvocationTargetException) {}
    if ((paramComponent instanceof JInternalFrame)) {
      try
      {
        ((JInternalFrame)paramComponent).setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
    }
    if ((localComponent != null) && (localComponent.isShowing())) {
      localComponent.requestFocus();
    }
    Object localObject2 = localJOptionPane.getInputValue();
    if (localObject2 == UNINITIALIZED_VALUE) {
      return null;
    }
    return localObject2;
  }
  
  public JInternalFrame createInternalFrame(Component paramComponent, String paramString)
  {
    Object localObject = getDesktopPaneForComponent(paramComponent);
    if ((localObject == null) && ((paramComponent == null) || ((localObject = paramComponent.getParent()) == null))) {
      throw new RuntimeException("JOptionPane: parentComponent does not have a valid parent");
    }
    final JInternalFrame localJInternalFrame = new JInternalFrame(paramString, false, true, false, false);
    localJInternalFrame.putClientProperty("JInternalFrame.frameType", "optionDialog");
    localJInternalFrame.putClientProperty("JInternalFrame.messageType", Integer.valueOf(getMessageType()));
    localJInternalFrame.addInternalFrameListener(new InternalFrameAdapter()
    {
      public void internalFrameClosing(InternalFrameEvent paramAnonymousInternalFrameEvent)
      {
        if (getValue() == JOptionPane.UNINITIALIZED_VALUE) {
          setValue(null);
        }
      }
    });
    addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        if ((localJInternalFrame.isVisible()) && (paramAnonymousPropertyChangeEvent.getSource() == JOptionPane.this) && (paramAnonymousPropertyChangeEvent.getPropertyName().equals("value")))
        {
          try
          {
            Method localMethod = (Method)AccessController.doPrivileged(new JOptionPane.ModalPrivilegedAction(Container.class, "stopLWModal"));
            if (localMethod != null) {
              localMethod.invoke(localJInternalFrame, (Object[])null);
            }
          }
          catch (IllegalAccessException localIllegalAccessException) {}catch (IllegalArgumentException localIllegalArgumentException) {}catch (InvocationTargetException localInvocationTargetException) {}
          try
          {
            localJInternalFrame.setClosed(true);
          }
          catch (PropertyVetoException localPropertyVetoException) {}
          localJInternalFrame.setVisible(false);
        }
      }
    });
    localJInternalFrame.getContentPane().add(this, "Center");
    if ((localObject instanceof JDesktopPane)) {
      ((Container)localObject).add(localJInternalFrame, JLayeredPane.MODAL_LAYER);
    } else {
      ((Container)localObject).add(localJInternalFrame, "Center");
    }
    Dimension localDimension1 = localJInternalFrame.getPreferredSize();
    Dimension localDimension2 = ((Container)localObject).getSize();
    Dimension localDimension3 = paramComponent.getSize();
    localJInternalFrame.setBounds((width - width) / 2, (height - height) / 2, width, height);
    Point localPoint = SwingUtilities.convertPoint(paramComponent, 0, 0, (Component)localObject);
    int i = (width - width) / 2 + x;
    int j = (height - height) / 2 + y;
    int k = i + width - width;
    int m = j + height - height;
    i = Math.max(k > 0 ? i - k : i, 0);
    j = Math.max(m > 0 ? j - m : j, 0);
    localJInternalFrame.setBounds(i, j, width, height);
    ((Container)localObject).validate();
    try
    {
      localJInternalFrame.setSelected(true);
    }
    catch (PropertyVetoException localPropertyVetoException) {}
    return localJInternalFrame;
  }
  
  public static Frame getFrameForComponent(Component paramComponent)
    throws HeadlessException
  {
    if (paramComponent == null) {
      return getRootFrame();
    }
    if ((paramComponent instanceof Frame)) {
      return (Frame)paramComponent;
    }
    return getFrameForComponent(paramComponent.getParent());
  }
  
  static Window getWindowForComponent(Component paramComponent)
    throws HeadlessException
  {
    if (paramComponent == null) {
      return getRootFrame();
    }
    if (((paramComponent instanceof Frame)) || ((paramComponent instanceof Dialog))) {
      return (Window)paramComponent;
    }
    return getWindowForComponent(paramComponent.getParent());
  }
  
  public static JDesktopPane getDesktopPaneForComponent(Component paramComponent)
  {
    if (paramComponent == null) {
      return null;
    }
    if ((paramComponent instanceof JDesktopPane)) {
      return (JDesktopPane)paramComponent;
    }
    return getDesktopPaneForComponent(paramComponent.getParent());
  }
  
  public static void setRootFrame(Frame paramFrame)
  {
    if (paramFrame != null) {
      SwingUtilities.appContextPut(sharedFrameKey, paramFrame);
    } else {
      SwingUtilities.appContextRemove(sharedFrameKey);
    }
  }
  
  public static Frame getRootFrame()
    throws HeadlessException
  {
    Frame localFrame = (Frame)SwingUtilities.appContextGet(sharedFrameKey);
    if (localFrame == null)
    {
      localFrame = SwingUtilities.getSharedOwnerFrame();
      SwingUtilities.appContextPut(sharedFrameKey, localFrame);
    }
    return localFrame;
  }
  
  public JOptionPane()
  {
    this("JOptionPane message");
  }
  
  public JOptionPane(Object paramObject)
  {
    this(paramObject, -1);
  }
  
  public JOptionPane(Object paramObject, int paramInt)
  {
    this(paramObject, paramInt, -1);
  }
  
  public JOptionPane(Object paramObject, int paramInt1, int paramInt2)
  {
    this(paramObject, paramInt1, paramInt2, null);
  }
  
  public JOptionPane(Object paramObject, int paramInt1, int paramInt2, Icon paramIcon)
  {
    this(paramObject, paramInt1, paramInt2, paramIcon, null);
  }
  
  public JOptionPane(Object paramObject, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject)
  {
    this(paramObject, paramInt1, paramInt2, paramIcon, paramArrayOfObject, null);
  }
  
  public JOptionPane(Object paramObject1, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2)
  {
    message = paramObject1;
    options = paramArrayOfObject;
    initialValue = paramObject2;
    icon = paramIcon;
    setMessageType(paramInt1);
    setOptionType(paramInt2);
    value = UNINITIALIZED_VALUE;
    inputValue = UNINITIALIZED_VALUE;
    updateUI();
  }
  
  public void setUI(OptionPaneUI paramOptionPaneUI)
  {
    if (ui != paramOptionPaneUI)
    {
      super.setUI(paramOptionPaneUI);
      invalidate();
    }
  }
  
  public OptionPaneUI getUI()
  {
    return (OptionPaneUI)ui;
  }
  
  public void updateUI()
  {
    setUI((OptionPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "OptionPaneUI";
  }
  
  public void setMessage(Object paramObject)
  {
    Object localObject = message;
    message = paramObject;
    firePropertyChange("message", localObject, message);
  }
  
  public Object getMessage()
  {
    return message;
  }
  
  public void setIcon(Icon paramIcon)
  {
    Icon localIcon = icon;
    icon = paramIcon;
    firePropertyChange("icon", localIcon, icon);
  }
  
  public Icon getIcon()
  {
    return icon;
  }
  
  public void setValue(Object paramObject)
  {
    Object localObject = value;
    value = paramObject;
    firePropertyChange("value", localObject, value);
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public void setOptions(Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = options;
    options = paramArrayOfObject;
    firePropertyChange("options", arrayOfObject, options);
  }
  
  public Object[] getOptions()
  {
    if (options != null)
    {
      int i = options.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(options, 0, arrayOfObject, 0, i);
      return arrayOfObject;
    }
    return options;
  }
  
  public void setInitialValue(Object paramObject)
  {
    Object localObject = initialValue;
    initialValue = paramObject;
    firePropertyChange("initialValue", localObject, initialValue);
  }
  
  public Object getInitialValue()
  {
    return initialValue;
  }
  
  public void setMessageType(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2) && (paramInt != 3) && (paramInt != -1)) {
      throw new RuntimeException("JOptionPane: type must be one of JOptionPane.ERROR_MESSAGE, JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE, JOptionPane.QUESTION_MESSAGE or JOptionPane.PLAIN_MESSAGE");
    }
    int i = messageType;
    messageType = paramInt;
    firePropertyChange("messageType", i, messageType);
  }
  
  public int getMessageType()
  {
    return messageType;
  }
  
  public void setOptionType(int paramInt)
  {
    if ((paramInt != -1) && (paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new RuntimeException("JOptionPane: option type must be one of JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION or JOptionPane.OK_CANCEL_OPTION");
    }
    int i = optionType;
    optionType = paramInt;
    firePropertyChange("optionType", i, optionType);
  }
  
  public int getOptionType()
  {
    return optionType;
  }
  
  public void setSelectionValues(Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = selectionValues;
    selectionValues = paramArrayOfObject;
    firePropertyChange("selectionValues", arrayOfObject, paramArrayOfObject);
    if (selectionValues != null) {
      setWantsInput(true);
    }
  }
  
  public Object[] getSelectionValues()
  {
    return selectionValues;
  }
  
  public void setInitialSelectionValue(Object paramObject)
  {
    Object localObject = initialSelectionValue;
    initialSelectionValue = paramObject;
    firePropertyChange("initialSelectionValue", localObject, paramObject);
  }
  
  public Object getInitialSelectionValue()
  {
    return initialSelectionValue;
  }
  
  public void setInputValue(Object paramObject)
  {
    Object localObject = inputValue;
    inputValue = paramObject;
    firePropertyChange("inputValue", localObject, paramObject);
  }
  
  public Object getInputValue()
  {
    return inputValue;
  }
  
  public int getMaxCharactersPerLineCount()
  {
    return Integer.MAX_VALUE;
  }
  
  public void setWantsInput(boolean paramBoolean)
  {
    boolean bool = wantsInput;
    wantsInput = paramBoolean;
    firePropertyChange("wantsInput", bool, paramBoolean);
  }
  
  public boolean getWantsInput()
  {
    return wantsInput;
  }
  
  public void selectInitialValue()
  {
    OptionPaneUI localOptionPaneUI = getUI();
    if (localOptionPaneUI != null) {
      localOptionPaneUI.selectInitialValue(this);
    }
  }
  
  private static int styleFromMessageType(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 4;
    case 3: 
      return 7;
    case 2: 
      return 8;
    case 1: 
      return 3;
    }
    return 2;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector1 = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if ((icon != null) && ((icon instanceof Serializable)))
    {
      localVector1.addElement("icon");
      localVector1.addElement(icon);
    }
    if ((message != null) && ((message instanceof Serializable)))
    {
      localVector1.addElement("message");
      localVector1.addElement(message);
    }
    int j;
    if (options != null)
    {
      Vector localVector2 = new Vector();
      j = 0;
      int k = options.length;
      while (j < k)
      {
        if ((options[j] instanceof Serializable)) {
          localVector2.addElement(options[j]);
        }
        j++;
      }
      if (localVector2.size() > 0)
      {
        j = localVector2.size();
        Object[] arrayOfObject = new Object[j];
        localVector2.copyInto(arrayOfObject);
        localVector1.addElement("options");
        localVector1.addElement(arrayOfObject);
      }
    }
    if ((initialValue != null) && ((initialValue instanceof Serializable)))
    {
      localVector1.addElement("initialValue");
      localVector1.addElement(initialValue);
    }
    if ((value != null) && ((value instanceof Serializable)))
    {
      localVector1.addElement("value");
      localVector1.addElement(value);
    }
    if (selectionValues != null)
    {
      int i = 1;
      j = 0;
      int m = selectionValues.length;
      while (j < m)
      {
        if ((selectionValues[j] != null) && (!(selectionValues[j] instanceof Serializable)))
        {
          i = 0;
          break;
        }
        j++;
      }
      if (i != 0)
      {
        localVector1.addElement("selectionValues");
        localVector1.addElement(selectionValues);
      }
    }
    if ((inputValue != null) && ((inputValue instanceof Serializable)))
    {
      localVector1.addElement("inputValue");
      localVector1.addElement(inputValue);
    }
    if ((initialSelectionValue != null) && ((initialSelectionValue instanceof Serializable)))
    {
      localVector1.addElement("initialSelectionValue");
      localVector1.addElement(initialSelectionValue);
    }
    paramObjectOutputStream.writeObject(localVector1);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Vector localVector = (Vector)paramObjectInputStream.readObject();
    int i = 0;
    int j = localVector.size();
    if ((i < j) && (localVector.elementAt(i).equals("icon")))
    {
      icon = ((Icon)localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("message")))
    {
      message = localVector.elementAt(++i);
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("options")))
    {
      options = ((Object[])localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("initialValue")))
    {
      initialValue = localVector.elementAt(++i);
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("value")))
    {
      value = localVector.elementAt(++i);
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("selectionValues")))
    {
      selectionValues = ((Object[])localVector.elementAt(++i));
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("inputValue")))
    {
      inputValue = localVector.elementAt(++i);
      i++;
    }
    if ((i < j) && (localVector.elementAt(i).equals("initialSelectionValue")))
    {
      initialSelectionValue = localVector.elementAt(++i);
      i++;
    }
    if (getUIClassID().equals("OptionPaneUI"))
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
    String str1 = icon != null ? icon.toString() : "";
    String str2 = initialValue != null ? initialValue.toString() : "";
    String str3 = message != null ? message.toString() : "";
    String str4;
    if (messageType == 0) {
      str4 = "ERROR_MESSAGE";
    } else if (messageType == 1) {
      str4 = "INFORMATION_MESSAGE";
    } else if (messageType == 2) {
      str4 = "WARNING_MESSAGE";
    } else if (messageType == 3) {
      str4 = "QUESTION_MESSAGE";
    } else if (messageType == -1) {
      str4 = "PLAIN_MESSAGE";
    } else {
      str4 = "";
    }
    String str5;
    if (optionType == -1) {
      str5 = "DEFAULT_OPTION";
    } else if (optionType == 0) {
      str5 = "YES_NO_OPTION";
    } else if (optionType == 1) {
      str5 = "YES_NO_CANCEL_OPTION";
    } else if (optionType == 2) {
      str5 = "OK_CANCEL_OPTION";
    } else {
      str5 = "";
    }
    String str6 = wantsInput ? "true" : "false";
    return super.paramString() + ",icon=" + str1 + ",initialValue=" + str2 + ",message=" + str3 + ",messageType=" + str4 + ",optionType=" + str5 + ",wantsInput=" + str6;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJOptionPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJOptionPane
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJOptionPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      switch (messageType)
      {
      case 0: 
      case 1: 
      case 2: 
        return AccessibleRole.ALERT;
      }
      return AccessibleRole.OPTION_PANE;
    }
  }
  
  private static class ModalPrivilegedAction
    implements PrivilegedAction<Method>
  {
    private Class<?> clazz;
    private String methodName;
    
    public ModalPrivilegedAction(Class<?> paramClass, String paramString)
    {
      clazz = paramClass;
      methodName = paramString;
    }
    
    public Method run()
    {
      Method localMethod = null;
      try
      {
        localMethod = clazz.getDeclaredMethod(methodName, (Class[])null);
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
      if (localMethod != null) {
        localMethod.setAccessible(true);
      }
      return localMethod;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JOptionPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */