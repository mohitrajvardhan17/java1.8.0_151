package sun.security.tools.policytool;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import sun.security.action.GetPropertyAction;

class ToolWindow
  extends JFrame
{
  private static final long serialVersionUID = 5682568601210376777L;
  static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
  public static final Insets TOP_PADDING = new Insets(25, 0, 0, 0);
  public static final Insets BOTTOM_PADDING = new Insets(0, 0, 25, 0);
  public static final Insets LITE_BOTTOM_PADDING = new Insets(0, 0, 10, 0);
  public static final Insets LR_PADDING = new Insets(0, 10, 0, 10);
  public static final Insets TOP_BOTTOM_PADDING = new Insets(15, 0, 15, 0);
  public static final Insets L_TOP_BOTTOM_PADDING = new Insets(5, 10, 15, 0);
  public static final Insets LR_TOP_BOTTOM_PADDING = new Insets(15, 4, 15, 4);
  public static final Insets LR_BOTTOM_PADDING = new Insets(0, 10, 5, 10);
  public static final Insets L_BOTTOM_PADDING = new Insets(0, 10, 5, 0);
  public static final Insets R_BOTTOM_PADDING = new Insets(0, 0, 25, 5);
  public static final Insets R_PADDING = new Insets(0, 0, 0, 5);
  public static final String NEW_POLICY_FILE = "New";
  public static final String OPEN_POLICY_FILE = "Open";
  public static final String SAVE_POLICY_FILE = "Save";
  public static final String SAVE_AS_POLICY_FILE = "Save.As";
  public static final String VIEW_WARNINGS = "View.Warning.Log";
  public static final String QUIT = "Exit";
  public static final String ADD_POLICY_ENTRY = "Add.Policy.Entry";
  public static final String EDIT_POLICY_ENTRY = "Edit.Policy.Entry";
  public static final String REMOVE_POLICY_ENTRY = "Remove.Policy.Entry";
  public static final String EDIT_KEYSTORE = "Edit";
  public static final String ADD_PUBKEY_ALIAS = "Add.Public.Key.Alias";
  public static final String REMOVE_PUBKEY_ALIAS = "Remove.Public.Key.Alias";
  public static final int MW_FILENAME_LABEL = 0;
  public static final int MW_FILENAME_TEXTFIELD = 1;
  public static final int MW_PANEL = 2;
  public static final int MW_ADD_BUTTON = 0;
  public static final int MW_EDIT_BUTTON = 1;
  public static final int MW_REMOVE_BUTTON = 2;
  public static final int MW_POLICY_LIST = 3;
  static final int TEXTFIELD_HEIGHT = JComboBoxgetPreferredSizeheight;
  private PolicyTool tool;
  private int shortCutModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  
  ToolWindow(PolicyTool paramPolicyTool)
  {
    tool = paramPolicyTool;
  }
  
  public Component getComponent(int paramInt)
  {
    Component localComponent = getContentPane().getComponent(paramInt);
    if ((localComponent instanceof JScrollPane)) {
      localComponent = ((JScrollPane)localComponent).getViewport().getView();
    }
    return localComponent;
  }
  
  private void initWindow()
  {
    setDefaultCloseOperation(0);
    JMenuBar localJMenuBar = new JMenuBar();
    JMenu localJMenu = new JMenu();
    configureButton(localJMenu, "File");
    Object localObject1 = new FileMenuListener(tool, this);
    addMenuItem(localJMenu, "New", (ActionListener)localObject1, "N");
    addMenuItem(localJMenu, "Open", (ActionListener)localObject1, "O");
    addMenuItem(localJMenu, "Save", (ActionListener)localObject1, "S");
    addMenuItem(localJMenu, "Save.As", (ActionListener)localObject1, null);
    addMenuItem(localJMenu, "View.Warning.Log", (ActionListener)localObject1, null);
    addMenuItem(localJMenu, "Exit", (ActionListener)localObject1, null);
    localJMenuBar.add(localJMenu);
    localJMenu = new JMenu();
    configureButton(localJMenu, "KeyStore");
    localObject1 = new MainWindowListener(tool, this);
    addMenuItem(localJMenu, "Edit", (ActionListener)localObject1, null);
    localJMenuBar.add(localJMenu);
    setJMenuBar(localJMenuBar);
    ((JPanel)getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
    JLabel localJLabel = new JLabel(PolicyTool.getMessage("Policy.File."));
    addNewComponent(this, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
    JTextField localJTextField = new JTextField(50);
    localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    localJTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Policy.File."));
    localJTextField.setEditable(false);
    addNewComponent(this, localJTextField, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new GridBagLayout());
    JButton localJButton = new JButton();
    configureButton(localJButton, "Add.Policy.Entry");
    localJButton.addActionListener(new MainWindowListener(tool, this));
    addNewComponent(localJPanel, localJButton, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
    localJButton = new JButton();
    configureButton(localJButton, "Edit.Policy.Entry");
    localJButton.addActionListener(new MainWindowListener(tool, this));
    addNewComponent(localJPanel, localJButton, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
    localJButton = new JButton();
    configureButton(localJButton, "Remove.Policy.Entry");
    localJButton.addActionListener(new MainWindowListener(tool, this));
    addNewComponent(localJPanel, localJButton, 2, 2, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
    addNewComponent(this, localJPanel, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
    String str = tool.getPolicyFileName();
    Object localObject2;
    if (str == null)
    {
      localObject2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
      str = (String)localObject2 + File.separatorChar + ".java.policy";
    }
    try
    {
      tool.openPolicy(str);
      localObject2 = new DefaultListModel();
      localJList = new JList((ListModel)localObject2);
      localJList.setVisibleRowCount(15);
      localJList.setSelectionMode(0);
      localJList.addMouseListener(new PolicyListListener(tool, this));
      localObject3 = tool.getEntry();
      if (localObject3 != null) {
        for (int i = 0; i < localObject3.length; i++) {
          ((DefaultListModel)localObject2).addElement(localObject3[i].headerToString());
        }
      }
      localObject4 = (JTextField)getComponent(1);
      ((JTextField)localObject4).setText(str);
      initPolicyList(localJList);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localJList = new JList(new DefaultListModel());
      localJList.setVisibleRowCount(15);
      localJList.setSelectionMode(0);
      localJList.addMouseListener(new PolicyListListener(tool, this));
      initPolicyList(localJList);
      tool.setPolicyFileName(null);
      tool.modified = false;
      tool.warnings.addElement(localFileNotFoundException.toString());
    }
    catch (Exception localException)
    {
      JList localJList = new JList(new DefaultListModel());
      localJList.setVisibleRowCount(15);
      localJList.setSelectionMode(0);
      localJList.addMouseListener(new PolicyListListener(tool, this));
      initPolicyList(localJList);
      tool.setPolicyFileName(null);
      tool.modified = false;
      Object localObject3 = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
      Object localObject4 = { str, localException.toString() };
      displayErrorDialog(null, ((MessageFormat)localObject3).format(localObject4));
    }
  }
  
  private void addMenuItem(JMenu paramJMenu, String paramString1, ActionListener paramActionListener, String paramString2)
  {
    JMenuItem localJMenuItem = new JMenuItem();
    configureButton(localJMenuItem, paramString1);
    if (PolicyTool.rb.containsKey(paramString1 + ".accelerator")) {
      paramString2 = PolicyTool.getMessage(paramString1 + ".accelerator");
    }
    if ((paramString2 != null) && (!paramString2.isEmpty()))
    {
      KeyStroke localKeyStroke;
      if (paramString2.length() == 1) {
        localKeyStroke = KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(paramString2.charAt(0)), shortCutModifier);
      } else {
        localKeyStroke = KeyStroke.getKeyStroke(paramString2);
      }
      localJMenuItem.setAccelerator(localKeyStroke);
    }
    localJMenuItem.addActionListener(paramActionListener);
    paramJMenu.add(localJMenuItem);
  }
  
  static void configureButton(AbstractButton paramAbstractButton, String paramString)
  {
    paramAbstractButton.setText(PolicyTool.getMessage(paramString));
    paramAbstractButton.setActionCommand(paramString);
    int i = PolicyTool.getMnemonicInt(paramString);
    if (i > 0)
    {
      paramAbstractButton.setMnemonic(i);
      paramAbstractButton.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(paramString));
    }
  }
  
  static void configureLabelFor(JLabel paramJLabel, JComponent paramJComponent, String paramString)
  {
    paramJLabel.setText(PolicyTool.getMessage(paramString));
    paramJLabel.setLabelFor(paramJComponent);
    int i = PolicyTool.getMnemonicInt(paramString);
    if (i > 0)
    {
      paramJLabel.setDisplayedMnemonic(i);
      paramJLabel.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(paramString));
    }
  }
  
  void addNewComponent(Container paramContainer, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, int paramInt6, Insets paramInsets)
  {
    if ((paramContainer instanceof JFrame)) {
      paramContainer = ((JFrame)paramContainer).getContentPane();
    } else if ((paramContainer instanceof JDialog)) {
      paramContainer = ((JDialog)paramContainer).getContentPane();
    }
    paramContainer.add(paramJComponent, paramInt1);
    GridBagLayout localGridBagLayout = (GridBagLayout)paramContainer.getLayout();
    GridBagConstraints localGridBagConstraints = new GridBagConstraints();
    gridx = paramInt2;
    gridy = paramInt3;
    gridwidth = paramInt4;
    gridheight = paramInt5;
    weightx = paramDouble1;
    weighty = paramDouble2;
    fill = paramInt6;
    if (paramInsets != null) {
      insets = paramInsets;
    }
    localGridBagLayout.setConstraints(paramJComponent, localGridBagConstraints);
  }
  
  void addNewComponent(Container paramContainer, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, int paramInt6)
  {
    addNewComponent(paramContainer, paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramInt6, null);
  }
  
  void initPolicyList(JList paramJList)
  {
    JScrollPane localJScrollPane = new JScrollPane(paramJList);
    addNewComponent(this, localJScrollPane, 3, 0, 3, 2, 1, 1.0D, 1.0D, 1);
  }
  
  void replacePolicyList(JList paramJList)
  {
    JList localJList = (JList)getComponent(3);
    localJList.setModel(paramJList.getModel());
  }
  
  void displayToolWindow(String[] paramArrayOfString)
  {
    setTitle(PolicyTool.getMessage("Policy.Tool"));
    setResizable(true);
    addWindowListener(new ToolWindowListener(tool, this));
    getContentPane().setLayout(new GridBagLayout());
    initWindow();
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
    if (tool.newWarning == true) {
      displayStatusDialog(this, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
    }
  }
  
  void displayErrorDialog(Window paramWindow, String paramString)
  {
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Error"), tool, this, true);
    Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    localToolDialog.setLayout(new GridBagLayout());
    JLabel localJLabel = new JLabel(paramString);
    addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
    JButton localJButton = new JButton(PolicyTool.getMessage("OK"));
    ErrorOKButtonListener localErrorOKButtonListener = new ErrorOKButtonListener(localToolDialog);
    localJButton.addActionListener(localErrorOKButtonListener);
    addNewComponent(localToolDialog, localJButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
    localToolDialog.getRootPane().setDefaultButton(localJButton);
    localToolDialog.getRootPane().registerKeyboardAction(localErrorOKButtonListener, escKey, 2);
    localToolDialog.pack();
    localToolDialog.setLocationRelativeTo(paramWindow);
    localToolDialog.setVisible(true);
  }
  
  void displayErrorDialog(Window paramWindow, Throwable paramThrowable)
  {
    if ((paramThrowable instanceof NoDisplayException)) {
      return;
    }
    displayErrorDialog(paramWindow, paramThrowable.toString());
  }
  
  void displayStatusDialog(Window paramWindow, String paramString)
  {
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Status"), tool, this, true);
    Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    localToolDialog.setLayout(new GridBagLayout());
    JLabel localJLabel = new JLabel(paramString);
    addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
    JButton localJButton = new JButton(PolicyTool.getMessage("OK"));
    StatusOKButtonListener localStatusOKButtonListener = new StatusOKButtonListener(localToolDialog);
    localJButton.addActionListener(localStatusOKButtonListener);
    addNewComponent(localToolDialog, localJButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
    localToolDialog.getRootPane().setDefaultButton(localJButton);
    localToolDialog.getRootPane().registerKeyboardAction(localStatusOKButtonListener, escKey, 2);
    localToolDialog.pack();
    localToolDialog.setLocationRelativeTo(paramWindow);
    localToolDialog.setVisible(true);
  }
  
  void displayWarningLog(Window paramWindow)
  {
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Warning"), tool, this, true);
    Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    localToolDialog.setLayout(new GridBagLayout());
    JTextArea localJTextArea = new JTextArea();
    localJTextArea.setEditable(false);
    for (int i = 0; i < tool.warnings.size(); i++)
    {
      localJTextArea.append((String)tool.warnings.elementAt(i));
      localJTextArea.append(PolicyTool.getMessage("NEWLINE"));
    }
    addNewComponent(localToolDialog, localJTextArea, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
    localJTextArea.setFocusable(false);
    JButton localJButton = new JButton(PolicyTool.getMessage("OK"));
    CancelButtonListener localCancelButtonListener = new CancelButtonListener(localToolDialog);
    localJButton.addActionListener(localCancelButtonListener);
    addNewComponent(localToolDialog, localJButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
    localToolDialog.getRootPane().setDefaultButton(localJButton);
    localToolDialog.getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
    localToolDialog.pack();
    localToolDialog.setLocationRelativeTo(paramWindow);
    localToolDialog.setVisible(true);
  }
  
  char displayYesNoDialog(Window paramWindow, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    final ToolDialog localToolDialog = new ToolDialog(paramString1, tool, this, true);
    Point localPoint = paramWindow == null ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    localToolDialog.setLayout(new GridBagLayout());
    JTextArea localJTextArea = new JTextArea(paramString2, 10, 50);
    localJTextArea.setEditable(false);
    localJTextArea.setLineWrap(true);
    localJTextArea.setWrapStyleWord(true);
    JScrollPane localJScrollPane = new JScrollPane(localJTextArea, 20, 31);
    addNewComponent(localToolDialog, localJScrollPane, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
    localJTextArea.setFocusable(false);
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new GridBagLayout());
    final StringBuffer localStringBuffer = new StringBuffer();
    JButton localJButton = new JButton(paramString3);
    localJButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        localStringBuffer.append('Y');
        localToolDialog.setVisible(false);
        localToolDialog.dispose();
      }
    });
    addNewComponent(localJPanel, localJButton, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
    localJButton = new JButton(paramString4);
    localJButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        localStringBuffer.append('N');
        localToolDialog.setVisible(false);
        localToolDialog.dispose();
      }
    });
    addNewComponent(localJPanel, localJButton, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
    addNewComponent(localToolDialog, localJPanel, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
    localToolDialog.pack();
    localToolDialog.setLocationRelativeTo(paramWindow);
    localToolDialog.setVisible(true);
    if (localStringBuffer.length() > 0) {
      return localStringBuffer.charAt(0);
    }
    return 'N';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ToolWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */