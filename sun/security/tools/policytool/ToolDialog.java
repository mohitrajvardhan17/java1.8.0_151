package sun.security.tools.policytool;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import sun.security.provider.PolicyParser.GrantEntry;
import sun.security.provider.PolicyParser.PermissionEntry;
import sun.security.provider.PolicyParser.PrincipalEntry;

class ToolDialog
  extends JDialog
{
  private static final long serialVersionUID = -372244357011301190L;
  static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
  public static final int NOACTION = 0;
  public static final int QUIT = 1;
  public static final int NEW = 2;
  public static final int OPEN = 3;
  public static final String ALL_PERM_CLASS = "java.security.AllPermission";
  public static final String FILE_PERM_CLASS = "java.io.FilePermission";
  public static final String X500_PRIN_CLASS = "javax.security.auth.x500.X500Principal";
  public static final String PERM = PolicyTool.getMessage("Permission.");
  public static final String PRIN_TYPE = PolicyTool.getMessage("Principal.Type.");
  public static final String PRIN_NAME = PolicyTool.getMessage("Principal.Name.");
  public static final String PERM_NAME = PolicyTool.getMessage("Target.Name.");
  public static final String PERM_ACTIONS = PolicyTool.getMessage("Actions.");
  public static final int PE_CODEBASE_LABEL = 0;
  public static final int PE_CODEBASE_TEXTFIELD = 1;
  public static final int PE_SIGNEDBY_LABEL = 2;
  public static final int PE_SIGNEDBY_TEXTFIELD = 3;
  public static final int PE_PANEL0 = 4;
  public static final int PE_ADD_PRIN_BUTTON = 0;
  public static final int PE_EDIT_PRIN_BUTTON = 1;
  public static final int PE_REMOVE_PRIN_BUTTON = 2;
  public static final int PE_PRIN_LABEL = 5;
  public static final int PE_PRIN_LIST = 6;
  public static final int PE_PANEL1 = 7;
  public static final int PE_ADD_PERM_BUTTON = 0;
  public static final int PE_EDIT_PERM_BUTTON = 1;
  public static final int PE_REMOVE_PERM_BUTTON = 2;
  public static final int PE_PERM_LIST = 8;
  public static final int PE_PANEL2 = 9;
  public static final int PE_CANCEL_BUTTON = 1;
  public static final int PE_DONE_BUTTON = 0;
  public static final int PRD_DESC_LABEL = 0;
  public static final int PRD_PRIN_CHOICE = 1;
  public static final int PRD_PRIN_TEXTFIELD = 2;
  public static final int PRD_NAME_LABEL = 3;
  public static final int PRD_NAME_TEXTFIELD = 4;
  public static final int PRD_CANCEL_BUTTON = 6;
  public static final int PRD_OK_BUTTON = 5;
  public static final int PD_DESC_LABEL = 0;
  public static final int PD_PERM_CHOICE = 1;
  public static final int PD_PERM_TEXTFIELD = 2;
  public static final int PD_NAME_CHOICE = 3;
  public static final int PD_NAME_TEXTFIELD = 4;
  public static final int PD_ACTIONS_CHOICE = 5;
  public static final int PD_ACTIONS_TEXTFIELD = 6;
  public static final int PD_SIGNEDBY_LABEL = 7;
  public static final int PD_SIGNEDBY_TEXTFIELD = 8;
  public static final int PD_CANCEL_BUTTON = 10;
  public static final int PD_OK_BUTTON = 9;
  public static final int EDIT_KEYSTORE = 0;
  public static final int KSD_NAME_LABEL = 0;
  public static final int KSD_NAME_TEXTFIELD = 1;
  public static final int KSD_TYPE_LABEL = 2;
  public static final int KSD_TYPE_TEXTFIELD = 3;
  public static final int KSD_PROVIDER_LABEL = 4;
  public static final int KSD_PROVIDER_TEXTFIELD = 5;
  public static final int KSD_PWD_URL_LABEL = 6;
  public static final int KSD_PWD_URL_TEXTFIELD = 7;
  public static final int KSD_CANCEL_BUTTON = 9;
  public static final int KSD_OK_BUTTON = 8;
  public static final int USC_LABEL = 0;
  public static final int USC_PANEL = 1;
  public static final int USC_YES_BUTTON = 0;
  public static final int USC_NO_BUTTON = 1;
  public static final int USC_CANCEL_BUTTON = 2;
  public static final int CRPE_LABEL1 = 0;
  public static final int CRPE_LABEL2 = 1;
  public static final int CRPE_PANEL = 2;
  public static final int CRPE_PANEL_OK = 0;
  public static final int CRPE_PANEL_CANCEL = 1;
  private static final int PERMISSION = 0;
  private static final int PERMISSION_NAME = 1;
  private static final int PERMISSION_ACTIONS = 2;
  private static final int PERMISSION_SIGNEDBY = 3;
  private static final int PRINCIPAL_TYPE = 4;
  private static final int PRINCIPAL_NAME = 5;
  static final int TEXTFIELD_HEIGHT = JComboBoxgetPreferredSizeheight;
  public static ArrayList<Perm> PERM_ARRAY = new ArrayList();
  public static ArrayList<Prin> PRIN_ARRAY;
  PolicyTool tool;
  ToolWindow tw;
  
  ToolDialog(String paramString, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, boolean paramBoolean)
  {
    super(paramToolWindow, paramBoolean);
    setTitle(paramString);
    tool = paramPolicyTool;
    tw = paramToolWindow;
    addWindowListener(new ChildWindowListener(this));
    ((JPanel)getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
  }
  
  public Component getComponent(int paramInt)
  {
    Component localComponent = getContentPane().getComponent(paramInt);
    if ((localComponent instanceof JScrollPane)) {
      localComponent = ((JScrollPane)localComponent).getViewport().getView();
    }
    return localComponent;
  }
  
  static Perm getPerm(String paramString, boolean paramBoolean)
  {
    for (int i = 0; i < PERM_ARRAY.size(); i++)
    {
      Perm localPerm = (Perm)PERM_ARRAY.get(i);
      if (paramBoolean)
      {
        if (FULL_CLASS.equals(paramString)) {
          return localPerm;
        }
      }
      else if (CLASS.equals(paramString)) {
        return localPerm;
      }
    }
    return null;
  }
  
  static Prin getPrin(String paramString, boolean paramBoolean)
  {
    for (int i = 0; i < PRIN_ARRAY.size(); i++)
    {
      Prin localPrin = (Prin)PRIN_ARRAY.get(i);
      if (paramBoolean)
      {
        if (FULL_CLASS.equals(paramString)) {
          return localPrin;
        }
      }
      else if (CLASS.equals(paramString)) {
        return localPrin;
      }
    }
    return null;
  }
  
  void displayPolicyEntryDialog(boolean paramBoolean)
  {
    int i = 0;
    PolicyEntry[] arrayOfPolicyEntry = null;
    TaggedList localTaggedList1 = new TaggedList(3, false);
    localTaggedList1.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Principal.List"));
    localTaggedList1.addMouseListener(new EditPrinButtonListener(tool, tw, this, paramBoolean));
    TaggedList localTaggedList2 = new TaggedList(10, false);
    localTaggedList2.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Permission.List"));
    localTaggedList2.addMouseListener(new EditPermButtonListener(tool, tw, this, paramBoolean));
    Point localPoint = tw.getLocationOnScreen();
    setLayout(new GridBagLayout());
    setResizable(true);
    if (paramBoolean)
    {
      arrayOfPolicyEntry = tool.getEntry();
      localObject1 = (JList)tw.getComponent(3);
      i = ((JList)localObject1).getSelectedIndex();
      localObject2 = getGrantEntryprincipals;
      for (int j = 0; j < ((LinkedList)localObject2).size(); j++)
      {
        Object localObject4 = null;
        localObject5 = (PolicyParser.PrincipalEntry)((LinkedList)localObject2).get(j);
        localTaggedList1.addTaggedItem(PrincipalEntryToUserFriendlyString((PolicyParser.PrincipalEntry)localObject5), localObject5);
      }
      localObject3 = getGrantEntrypermissionEntries;
      for (int k = 0; k < ((Vector)localObject3).size(); k++)
      {
        localObject5 = null;
        localObject6 = (PolicyParser.PermissionEntry)((Vector)localObject3).elementAt(k);
        localTaggedList2.addTaggedItem(PermissionEntryToUserFriendlyString((PolicyParser.PermissionEntry)localObject6), localObject6);
      }
    }
    Object localObject1 = new JLabel();
    tw.addNewComponent(this, (JComponent)localObject1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
    Object localObject2 = paramBoolean ? new JTextField(getGrantEntrycodeBase) : new JTextField();
    ToolWindow.configureLabelFor((JLabel)localObject1, (JComponent)localObject2, "CodeBase.");
    ((JTextField)localObject2).setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    ((JTextField)localObject2).getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Code.Base"));
    tw.addNewComponent(this, (JComponent)localObject2, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1);
    localObject1 = new JLabel();
    tw.addNewComponent(this, (JComponent)localObject1, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
    localObject2 = paramBoolean ? new JTextField(getGrantEntrysignedBy) : new JTextField();
    ToolWindow.configureLabelFor((JLabel)localObject1, (JComponent)localObject2, "SignedBy.");
    ((JTextField)localObject2).setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    ((JTextField)localObject2).getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
    tw.addNewComponent(this, (JComponent)localObject2, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1);
    Object localObject3 = new JPanel();
    ((JPanel)localObject3).setLayout(new GridBagLayout());
    JButton localJButton1 = new JButton();
    ToolWindow.configureButton(localJButton1, "Add.Principal");
    localJButton1.addActionListener(new AddPrinButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, localJButton1, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
    localJButton1 = new JButton();
    ToolWindow.configureButton(localJButton1, "Edit.Principal");
    localJButton1.addActionListener(new EditPrinButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, localJButton1, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
    localJButton1 = new JButton();
    ToolWindow.configureButton(localJButton1, "Remove.Principal");
    localJButton1.addActionListener(new RemovePrinButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, localJButton1, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
    tw.addNewComponent(this, (JComponent)localObject3, 4, 1, 2, 1, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
    localObject1 = new JLabel();
    tw.addNewComponent(this, (JComponent)localObject1, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
    Object localObject5 = new JScrollPane(localTaggedList1);
    ToolWindow.configureLabelFor((JLabel)localObject1, (JComponent)localObject5, "Principals.");
    tw.addNewComponent(this, (JComponent)localObject5, 6, 1, 3, 3, 1, 0.0D, localTaggedList1.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
    localObject3 = new JPanel();
    ((JPanel)localObject3).setLayout(new GridBagLayout());
    localJButton1 = new JButton();
    ToolWindow.configureButton(localJButton1, ".Add.Permission");
    localJButton1.addActionListener(new AddPermButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, localJButton1, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
    localJButton1 = new JButton();
    ToolWindow.configureButton(localJButton1, ".Edit.Permission");
    localJButton1.addActionListener(new EditPermButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, localJButton1, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
    localJButton1 = new JButton();
    ToolWindow.configureButton(localJButton1, "Remove.Permission");
    localJButton1.addActionListener(new RemovePermButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, localJButton1, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
    tw.addNewComponent(this, (JComponent)localObject3, 7, 0, 4, 2, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
    localObject5 = new JScrollPane(localTaggedList2);
    tw.addNewComponent(this, (JComponent)localObject5, 8, 0, 5, 3, 1, 0.0D, localTaggedList2.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
    localObject3 = new JPanel();
    ((JPanel)localObject3).setLayout(new GridBagLayout());
    Object localObject6 = new JButton(PolicyTool.getMessage("Done"));
    ((JButton)localObject6).addActionListener(new AddEntryDoneButtonListener(tool, tw, this, paramBoolean));
    tw.addNewComponent((Container)localObject3, (JComponent)localObject6, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    JButton localJButton2 = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
    localJButton2.addActionListener(localCancelButtonListener);
    tw.addNewComponent((Container)localObject3, localJButton2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    tw.addNewComponent(this, (JComponent)localObject3, 9, 0, 6, 2, 1, 0.0D, 0.0D, 3);
    getRootPane().setDefaultButton((JButton)localObject6);
    getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
    pack();
    setLocationRelativeTo(tw);
    setVisible(true);
  }
  
  PolicyEntry getPolicyEntryFromDialog()
    throws InvalidParameterException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, Exception
  {
    JTextField localJTextField = (JTextField)getComponent(1);
    String str1 = null;
    if (!localJTextField.getText().trim().equals("")) {
      str1 = new String(localJTextField.getText().trim());
    }
    localJTextField = (JTextField)getComponent(3);
    String str2 = null;
    if (!localJTextField.getText().trim().equals("")) {
      str2 = new String(localJTextField.getText().trim());
    }
    PolicyParser.GrantEntry localGrantEntry = new PolicyParser.GrantEntry(str2, str1);
    LinkedList localLinkedList = new LinkedList();
    TaggedList localTaggedList1 = (TaggedList)getComponent(6);
    for (int i = 0; i < localTaggedList1.getModel().getSize(); i++) {
      localLinkedList.add((PolicyParser.PrincipalEntry)localTaggedList1.getObject(i));
    }
    principals = localLinkedList;
    Vector localVector = new Vector();
    TaggedList localTaggedList2 = (TaggedList)getComponent(8);
    for (int j = 0; j < localTaggedList2.getModel().getSize(); j++) {
      localVector.addElement((PolicyParser.PermissionEntry)localTaggedList2.getObject(j));
    }
    permissionEntries = localVector;
    PolicyEntry localPolicyEntry = new PolicyEntry(tool, localGrantEntry);
    return localPolicyEntry;
  }
  
  void keyStoreDialog(int paramInt)
  {
    Point localPoint = tw.getLocationOnScreen();
    setLayout(new GridBagLayout());
    if (paramInt == 0)
    {
      JLabel localJLabel = new JLabel();
      tw.addNewComponent(this, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      JTextField localJTextField = new JTextField(tool.getKeyStoreName(), 30);
      ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.URL.");
      localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
      localJTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.U.R.L."));
      tw.addNewComponent(this, localJTextField, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      localJLabel = new JLabel();
      tw.addNewComponent(this, localJLabel, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      localJTextField = new JTextField(tool.getKeyStoreType(), 30);
      ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.Type.");
      localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
      localJTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Type."));
      tw.addNewComponent(this, localJTextField, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      localJLabel = new JLabel();
      tw.addNewComponent(this, localJLabel, 4, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      localJTextField = new JTextField(tool.getKeyStoreProvider(), 30);
      ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.Provider.");
      localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
      localJTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Provider."));
      tw.addNewComponent(this, localJTextField, 5, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      localJLabel = new JLabel();
      tw.addNewComponent(this, localJLabel, 6, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      localJTextField = new JTextField(tool.getKeyStorePwdURL(), 30);
      ToolWindow.configureLabelFor(localJLabel, localJTextField, "KeyStore.Password.URL.");
      localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
      localJTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Password.U.R.L."));
      tw.addNewComponent(this, localJTextField, 7, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      JButton localJButton1 = new JButton(PolicyTool.getMessage("OK"));
      localJButton1.addActionListener(new ChangeKeyStoreOKButtonListener(tool, tw, this));
      tw.addNewComponent(this, localJButton1, 8, 0, 4, 1, 1, 0.0D, 0.0D, 3);
      JButton localJButton2 = new JButton(PolicyTool.getMessage("Cancel"));
      CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
      localJButton2.addActionListener(localCancelButtonListener);
      tw.addNewComponent(this, localJButton2, 9, 1, 4, 1, 1, 0.0D, 0.0D, 3);
      getRootPane().setDefaultButton(localJButton1);
      getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
    }
    pack();
    setLocationRelativeTo(tw);
    setVisible(true);
  }
  
  void displayPrincipalDialog(boolean paramBoolean1, boolean paramBoolean2)
  {
    PolicyParser.PrincipalEntry localPrincipalEntry = null;
    TaggedList localTaggedList = (TaggedList)getComponent(6);
    int i = localTaggedList.getSelectedIndex();
    if (paramBoolean2) {
      localPrincipalEntry = (PolicyParser.PrincipalEntry)localTaggedList.getObject(i);
    }
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Principals"), tool, tw, true);
    localToolDialog.addWindowListener(new ChildWindowListener(localToolDialog));
    Point localPoint = getLocationOnScreen();
    localToolDialog.setLayout(new GridBagLayout());
    localToolDialog.setResizable(true);
    JLabel localJLabel = paramBoolean2 ? new JLabel(PolicyTool.getMessage(".Edit.Principal.")) : new JLabel(PolicyTool.getMessage(".Add.New.Principal."));
    tw.addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
    JComboBox localJComboBox = new JComboBox();
    localJComboBox.addItem(PRIN_TYPE);
    localJComboBox.getAccessibleContext().setAccessibleName(PRIN_TYPE);
    for (int j = 0; j < PRIN_ARRAY.size(); j++)
    {
      localObject2 = (Prin)PRIN_ARRAY.get(j);
      localJComboBox.addItem(CLASS);
    }
    if (paramBoolean2) {
      if ("WILDCARD_PRINCIPAL_CLASS".equals(localPrincipalEntry.getPrincipalClass()))
      {
        localJComboBox.setSelectedItem(PRIN_TYPE);
      }
      else
      {
        localObject1 = getPrin(localPrincipalEntry.getPrincipalClass(), true);
        if (localObject1 != null) {
          localJComboBox.setSelectedItem(CLASS);
        }
      }
    }
    localJComboBox.addItemListener(new PrincipalTypeMenuListener(localToolDialog));
    tw.addNewComponent(localToolDialog, localJComboBox, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    Object localObject1 = paramBoolean2 ? new JTextField(localPrincipalEntry.getDisplayClass(), 30) : new JTextField(30);
    ((JTextField)localObject1).setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    ((JTextField)localObject1).getAccessibleContext().setAccessibleName(PRIN_TYPE);
    tw.addNewComponent(localToolDialog, (JComponent)localObject1, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    localJLabel = new JLabel(PRIN_NAME);
    localObject1 = paramBoolean2 ? new JTextField(localPrincipalEntry.getDisplayName(), 40) : new JTextField(40);
    ((JTextField)localObject1).setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    ((JTextField)localObject1).getAccessibleContext().setAccessibleName(PRIN_NAME);
    tw.addNewComponent(localToolDialog, localJLabel, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    tw.addNewComponent(localToolDialog, (JComponent)localObject1, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    Object localObject2 = new JButton(PolicyTool.getMessage("OK"));
    ((JButton)localObject2).addActionListener(new NewPolicyPrinOKButtonListener(tool, tw, this, localToolDialog, paramBoolean2));
    tw.addNewComponent(localToolDialog, (JComponent)localObject2, 5, 0, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    JButton localJButton = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener localCancelButtonListener = new CancelButtonListener(localToolDialog);
    localJButton.addActionListener(localCancelButtonListener);
    tw.addNewComponent(localToolDialog, localJButton, 6, 1, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    localToolDialog.getRootPane().setDefaultButton((JButton)localObject2);
    localToolDialog.getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
    localToolDialog.pack();
    localToolDialog.setLocationRelativeTo(tw);
    localToolDialog.setVisible(true);
  }
  
  void displayPermissionDialog(boolean paramBoolean1, boolean paramBoolean2)
  {
    PolicyParser.PermissionEntry localPermissionEntry = null;
    TaggedList localTaggedList = (TaggedList)getComponent(8);
    int i = localTaggedList.getSelectedIndex();
    if (paramBoolean2) {
      localPermissionEntry = (PolicyParser.PermissionEntry)localTaggedList.getObject(i);
    }
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Permissions"), tool, tw, true);
    localToolDialog.addWindowListener(new ChildWindowListener(localToolDialog));
    Point localPoint = getLocationOnScreen();
    localToolDialog.setLayout(new GridBagLayout());
    localToolDialog.setResizable(true);
    JLabel localJLabel = paramBoolean2 ? new JLabel(PolicyTool.getMessage(".Edit.Permission.")) : new JLabel(PolicyTool.getMessage(".Add.New.Permission."));
    tw.addNewComponent(localToolDialog, localJLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
    JComboBox localJComboBox = new JComboBox();
    localJComboBox.addItem(PERM);
    localJComboBox.getAccessibleContext().setAccessibleName(PERM);
    for (int j = 0; j < PERM_ARRAY.size(); j++)
    {
      localObject = (Perm)PERM_ARRAY.get(j);
      localJComboBox.addItem(CLASS);
    }
    tw.addNewComponent(localToolDialog, localJComboBox, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    JTextField localJTextField = paramBoolean2 ? new JTextField(permission, 30) : new JTextField(30);
    localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    localJTextField.getAccessibleContext().setAccessibleName(PERM);
    if (paramBoolean2)
    {
      localObject = getPerm(permission, true);
      if (localObject != null) {
        localJComboBox.setSelectedItem(CLASS);
      }
    }
    tw.addNewComponent(localToolDialog, localJTextField, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    localJComboBox.addItemListener(new PermissionMenuListener(localToolDialog));
    localJComboBox = new JComboBox();
    localJComboBox.addItem(PERM_NAME);
    localJComboBox.getAccessibleContext().setAccessibleName(PERM_NAME);
    localJTextField = paramBoolean2 ? new JTextField(name, 40) : new JTextField(40);
    localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    localJTextField.getAccessibleContext().setAccessibleName(PERM_NAME);
    if (paramBoolean2) {
      setPermissionNames(getPerm(permission, true), localJComboBox, localJTextField);
    }
    tw.addNewComponent(localToolDialog, localJComboBox, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    tw.addNewComponent(localToolDialog, localJTextField, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    localJComboBox.addItemListener(new PermissionNameMenuListener(localToolDialog));
    localJComboBox = new JComboBox();
    localJComboBox.addItem(PERM_ACTIONS);
    localJComboBox.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
    localJTextField = paramBoolean2 ? new JTextField(action, 40) : new JTextField(40);
    localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    localJTextField.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
    if (paramBoolean2) {
      setPermissionActions(getPerm(permission, true), localJComboBox, localJTextField);
    }
    tw.addNewComponent(localToolDialog, localJComboBox, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    tw.addNewComponent(localToolDialog, localJTextField, 6, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    localJComboBox.addItemListener(new PermissionActionsMenuListener(localToolDialog));
    localJLabel = new JLabel(PolicyTool.getMessage("Signed.By."));
    tw.addNewComponent(localToolDialog, localJLabel, 7, 0, 4, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    localJTextField = paramBoolean2 ? new JTextField(signedBy, 40) : new JTextField(40);
    localJTextField.setPreferredSize(new Dimension(getPreferredSizewidth, TEXTFIELD_HEIGHT));
    localJTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
    tw.addNewComponent(localToolDialog, localJTextField, 8, 1, 4, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    Object localObject = new JButton(PolicyTool.getMessage("OK"));
    ((JButton)localObject).addActionListener(new NewPolicyPermOKButtonListener(tool, tw, this, localToolDialog, paramBoolean2));
    tw.addNewComponent(localToolDialog, (JComponent)localObject, 9, 0, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    JButton localJButton = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener localCancelButtonListener = new CancelButtonListener(localToolDialog);
    localJButton.addActionListener(localCancelButtonListener);
    tw.addNewComponent(localToolDialog, localJButton, 10, 1, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    localToolDialog.getRootPane().setDefaultButton((JButton)localObject);
    localToolDialog.getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
    localToolDialog.pack();
    localToolDialog.setLocationRelativeTo(tw);
    localToolDialog.setVisible(true);
  }
  
  PolicyParser.PrincipalEntry getPrinFromDialog()
    throws Exception
  {
    JTextField localJTextField = (JTextField)getComponent(2);
    String str1 = new String(localJTextField.getText().trim());
    localJTextField = (JTextField)getComponent(4);
    String str2 = new String(localJTextField.getText().trim());
    if (str1.equals("*")) {
      str1 = "WILDCARD_PRINCIPAL_CLASS";
    }
    if (str2.equals("*")) {
      str2 = "WILDCARD_PRINCIPAL_NAME";
    }
    Object localObject = null;
    if ((str1.equals("WILDCARD_PRINCIPAL_CLASS")) && (!str2.equals("WILDCARD_PRINCIPAL_NAME"))) {
      throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name"));
    }
    if (str2.equals("")) {
      throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.without.a.Name"));
    }
    if (str1.equals(""))
    {
      str1 = "PolicyParser.REPLACE_NAME";
      tool.warnings.addElement("Warning: Principal name '" + str2 + "' specified without a Principal class.\n\t'" + str2 + "' will be interpreted as a key store alias.\n\tThe final principal class will be " + "javax.security.auth.x500.X500Principal" + ".\n\tThe final principal name will be determined by the following:\n\n\tIf the key store entry identified by '" + str2 + "'\n\tis a key entry, then the principal name will be\n\tthe subject distinguished name from the first\n\tcertificate in the entry's certificate chain.\n\n\tIf the key store entry identified by '" + str2 + "'\n\tis a trusted certificate entry, then the\n\tprincipal name will be the subject distinguished\n\tname from the trusted public key certificate.");
      tw.displayStatusDialog(this, "'" + str2 + "' will be interpreted as a key store alias.  View Warning Log for details.");
    }
    return new PolicyParser.PrincipalEntry(str1, str2);
  }
  
  PolicyParser.PermissionEntry getPermFromDialog()
  {
    JTextField localJTextField = (JTextField)getComponent(2);
    String str1 = new String(localJTextField.getText().trim());
    localJTextField = (JTextField)getComponent(4);
    String str2 = null;
    if (!localJTextField.getText().trim().equals("")) {
      str2 = new String(localJTextField.getText().trim());
    }
    if ((str1.equals("")) || ((!str1.equals("java.security.AllPermission")) && (str2 == null))) {
      throw new InvalidParameterException(PolicyTool.getMessage("Permission.and.Target.Name.must.have.a.value"));
    }
    if ((str1.equals("java.io.FilePermission")) && (str2.lastIndexOf("\\\\") > 0))
    {
      int i = tw.displayYesNoDialog(this, PolicyTool.getMessage("Warning"), PolicyTool.getMessage("Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes"), PolicyTool.getMessage("Retain"), PolicyTool.getMessage("Edit"));
      if (i != 89) {
        throw new NoDisplayException();
      }
    }
    localJTextField = (JTextField)getComponent(6);
    String str3 = null;
    if (!localJTextField.getText().trim().equals("")) {
      str3 = new String(localJTextField.getText().trim());
    }
    localJTextField = (JTextField)getComponent(8);
    String str4 = null;
    if (!localJTextField.getText().trim().equals("")) {
      str4 = new String(localJTextField.getText().trim());
    }
    PolicyParser.PermissionEntry localPermissionEntry = new PolicyParser.PermissionEntry(str1, str2, str3);
    signedBy = str4;
    if (str4 != null)
    {
      String[] arrayOfString = tool.parseSigners(signedBy);
      for (int j = 0; j < arrayOfString.length; j++) {
        try
        {
          PublicKey localPublicKey = tool.getPublicKeyAlias(arrayOfString[j]);
          if (localPublicKey == null)
          {
            MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
            Object[] arrayOfObject = { arrayOfString[j] };
            tool.warnings.addElement(localMessageFormat.format(arrayOfObject));
            tw.displayStatusDialog(this, localMessageFormat.format(arrayOfObject));
          }
        }
        catch (Exception localException)
        {
          tw.displayErrorDialog(this, localException);
        }
      }
    }
    return localPermissionEntry;
  }
  
  void displayConfirmRemovePolicyEntry()
  {
    JList localJList = (JList)tw.getComponent(3);
    int i = localJList.getSelectedIndex();
    PolicyEntry[] arrayOfPolicyEntry = tool.getEntry();
    Point localPoint = tw.getLocationOnScreen();
    setLayout(new GridBagLayout());
    JLabel localJLabel = new JLabel(PolicyTool.getMessage("Remove.this.Policy.Entry."));
    tw.addNewComponent(this, localJLabel, 0, 0, 0, 2, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
    localJLabel = new JLabel(arrayOfPolicyEntry[i].codebaseToString());
    tw.addNewComponent(this, localJLabel, 1, 0, 1, 2, 1, 0.0D, 0.0D, 1);
    localJLabel = new JLabel(arrayOfPolicyEntry[i].principalsToString().trim());
    tw.addNewComponent(this, localJLabel, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1);
    Vector localVector = getGrantEntrypermissionEntries;
    for (int j = 0; j < localVector.size(); j++)
    {
      localObject1 = (PolicyParser.PermissionEntry)localVector.elementAt(j);
      localObject2 = PermissionEntryToUserFriendlyString((PolicyParser.PermissionEntry)localObject1);
      localJLabel = new JLabel("    " + (String)localObject2);
      if (j == localVector.size() - 1) {
        tw.addNewComponent(this, localJLabel, 3 + j, 1, 3 + j, 1, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      } else {
        tw.addNewComponent(this, localJLabel, 3 + j, 1, 3 + j, 1, 1, 0.0D, 0.0D, 1);
      }
    }
    JPanel localJPanel = new JPanel();
    localJPanel.setLayout(new GridBagLayout());
    Object localObject1 = new JButton(PolicyTool.getMessage("OK"));
    ((JButton)localObject1).addActionListener(new ConfirmRemovePolicyEntryOKButtonListener(tool, tw, this));
    tw.addNewComponent(localJPanel, (JComponent)localObject1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    Object localObject2 = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
    ((JButton)localObject2).addActionListener(localCancelButtonListener);
    tw.addNewComponent(localJPanel, (JComponent)localObject2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    tw.addNewComponent(this, localJPanel, 3 + localVector.size(), 0, 3 + localVector.size(), 2, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    getRootPane().setDefaultButton((JButton)localObject1);
    getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
    pack();
    setLocationRelativeTo(tw);
    setVisible(true);
  }
  
  void displaySaveAsDialog(int paramInt)
  {
    FileDialog localFileDialog = new FileDialog(tw, PolicyTool.getMessage("Save.As"), 1);
    localFileDialog.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        paramAnonymousWindowEvent.getWindow().setVisible(false);
      }
    });
    localFileDialog.setVisible(true);
    if ((localFileDialog.getFile() == null) || (localFileDialog.getFile().equals(""))) {
      return;
    }
    File localFile = new File(localFileDialog.getDirectory(), localFileDialog.getFile());
    String str = localFile.getPath();
    localFileDialog.dispose();
    try
    {
      tool.savePolicy(str);
      MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
      Object[] arrayOfObject = { str };
      tw.displayStatusDialog(null, localMessageFormat.format(arrayOfObject));
      JTextField localJTextField = (JTextField)tw.getComponent(1);
      localJTextField.setText(str);
      tw.setVisible(true);
      userSaveContinue(tool, tw, this, paramInt);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      if ((str == null) || (str.equals(""))) {
        tw.displayErrorDialog(null, new FileNotFoundException(PolicyTool.getMessage("null.filename")));
      } else {
        tw.displayErrorDialog(null, localFileNotFoundException);
      }
    }
    catch (Exception localException)
    {
      tw.displayErrorDialog(null, localException);
    }
  }
  
  void displayUserSave(int paramInt)
  {
    if (tool.modified == true)
    {
      Point localPoint = tw.getLocationOnScreen();
      setLayout(new GridBagLayout());
      JLabel localJLabel = new JLabel(PolicyTool.getMessage("Save.changes."));
      tw.addNewComponent(this, localJLabel, 0, 0, 0, 3, 1, 0.0D, 0.0D, 1, ToolWindow.L_TOP_BOTTOM_PADDING);
      JPanel localJPanel = new JPanel();
      localJPanel.setLayout(new GridBagLayout());
      JButton localJButton1 = new JButton();
      ToolWindow.configureButton(localJButton1, "Yes");
      localJButton1.addActionListener(new UserSaveYesButtonListener(this, tool, tw, paramInt));
      tw.addNewComponent(localJPanel, localJButton1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
      JButton localJButton2 = new JButton();
      ToolWindow.configureButton(localJButton2, "No");
      localJButton2.addActionListener(new UserSaveNoButtonListener(this, tool, tw, paramInt));
      tw.addNewComponent(localJPanel, localJButton2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
      JButton localJButton3 = new JButton();
      ToolWindow.configureButton(localJButton3, "Cancel");
      CancelButtonListener localCancelButtonListener = new CancelButtonListener(this);
      localJButton3.addActionListener(localCancelButtonListener);
      tw.addNewComponent(localJPanel, localJButton3, 2, 2, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
      tw.addNewComponent(this, localJPanel, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1);
      getRootPane().registerKeyboardAction(localCancelButtonListener, escKey, 2);
      pack();
      setLocationRelativeTo(tw);
      setVisible(true);
    }
    else
    {
      userSaveContinue(tool, tw, this, paramInt);
    }
  }
  
  void userSaveContinue(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, int paramInt)
  {
    JList localJList;
    JTextField localJTextField;
    switch (paramInt)
    {
    case 1: 
      paramToolWindow.setVisible(false);
      paramToolWindow.dispose();
      System.exit(0);
    case 2: 
      try
      {
        paramPolicyTool.openPolicy(null);
      }
      catch (Exception localException1)
      {
        modified = false;
        paramToolWindow.displayErrorDialog(null, localException1);
      }
      localJList = new JList(new DefaultListModel());
      localJList.setVisibleRowCount(15);
      localJList.setSelectionMode(0);
      localJList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
      paramToolWindow.replacePolicyList(localJList);
      localJTextField = (JTextField)paramToolWindow.getComponent(1);
      localJTextField.setText("");
      paramToolWindow.setVisible(true);
      break;
    case 3: 
      FileDialog localFileDialog = new FileDialog(paramToolWindow, PolicyTool.getMessage("Open"), 0);
      localFileDialog.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent paramAnonymousWindowEvent)
        {
          paramAnonymousWindowEvent.getWindow().setVisible(false);
        }
      });
      localFileDialog.setVisible(true);
      if ((localFileDialog.getFile() == null) || (localFileDialog.getFile().equals(""))) {
        return;
      }
      String str = new File(localFileDialog.getDirectory(), localFileDialog.getFile()).getPath();
      try
      {
        paramPolicyTool.openPolicy(str);
        DefaultListModel localDefaultListModel = new DefaultListModel();
        localJList = new JList(localDefaultListModel);
        localJList.setVisibleRowCount(15);
        localJList.setSelectionMode(0);
        localJList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
        localObject = paramPolicyTool.getEntry();
        if (localObject != null) {
          for (int i = 0; i < localObject.length; i++) {
            localDefaultListModel.addElement(localObject[i].headerToString());
          }
        }
        paramToolWindow.replacePolicyList(localJList);
        modified = false;
        localJTextField = (JTextField)paramToolWindow.getComponent(1);
        localJTextField.setText(str);
        paramToolWindow.setVisible(true);
        if (newWarning == true) {
          paramToolWindow.displayStatusDialog(null, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
        }
      }
      catch (Exception localException2)
      {
        localJList = new JList(new DefaultListModel());
        localJList.setVisibleRowCount(15);
        localJList.setSelectionMode(0);
        localJList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
        paramToolWindow.replacePolicyList(localJList);
        paramPolicyTool.setPolicyFileName(null);
        modified = false;
        localJTextField = (JTextField)paramToolWindow.getComponent(1);
        localJTextField.setText("");
        paramToolWindow.setVisible(true);
        Object localObject = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
        Object[] arrayOfObject = { str, localException2.toString() };
        paramToolWindow.displayErrorDialog(null, ((MessageFormat)localObject).format(arrayOfObject));
      }
    }
  }
  
  void setPermissionNames(Perm paramPerm, JComboBox paramJComboBox, JTextField paramJTextField)
  {
    paramJComboBox.removeAllItems();
    paramJComboBox.addItem(PERM_NAME);
    if (paramPerm == null)
    {
      paramJTextField.setEditable(true);
    }
    else if (TARGETS == null)
    {
      paramJTextField.setEditable(false);
    }
    else
    {
      paramJTextField.setEditable(true);
      for (int i = 0; i < TARGETS.length; i++) {
        paramJComboBox.addItem(TARGETS[i]);
      }
    }
  }
  
  void setPermissionActions(Perm paramPerm, JComboBox paramJComboBox, JTextField paramJTextField)
  {
    paramJComboBox.removeAllItems();
    paramJComboBox.addItem(PERM_ACTIONS);
    if (paramPerm == null)
    {
      paramJTextField.setEditable(true);
    }
    else if (ACTIONS == null)
    {
      paramJTextField.setEditable(false);
    }
    else
    {
      paramJTextField.setEditable(true);
      for (int i = 0; i < ACTIONS.length; i++) {
        paramJComboBox.addItem(ACTIONS[i]);
      }
    }
  }
  
  static String PermissionEntryToUserFriendlyString(PolicyParser.PermissionEntry paramPermissionEntry)
  {
    String str = permission;
    if (name != null) {
      str = str + " " + name;
    }
    if (action != null) {
      str = str + ", \"" + action + "\"";
    }
    if (signedBy != null) {
      str = str + ", signedBy " + signedBy;
    }
    return str;
  }
  
  static String PrincipalEntryToUserFriendlyString(PolicyParser.PrincipalEntry paramPrincipalEntry)
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
    paramPrincipalEntry.write(localPrintWriter);
    return localStringWriter.toString();
  }
  
  static
  {
    PERM_ARRAY.add(new AllPerm());
    PERM_ARRAY.add(new AudioPerm());
    PERM_ARRAY.add(new AuthPerm());
    PERM_ARRAY.add(new AWTPerm());
    PERM_ARRAY.add(new DelegationPerm());
    PERM_ARRAY.add(new FilePerm());
    PERM_ARRAY.add(new URLPerm());
    PERM_ARRAY.add(new InqSecContextPerm());
    PERM_ARRAY.add(new LogPerm());
    PERM_ARRAY.add(new MgmtPerm());
    PERM_ARRAY.add(new MBeanPerm());
    PERM_ARRAY.add(new MBeanSvrPerm());
    PERM_ARRAY.add(new MBeanTrustPerm());
    PERM_ARRAY.add(new NetPerm());
    PERM_ARRAY.add(new PrivCredPerm());
    PERM_ARRAY.add(new PropPerm());
    PERM_ARRAY.add(new ReflectPerm());
    PERM_ARRAY.add(new RuntimePerm());
    PERM_ARRAY.add(new SecurityPerm());
    PERM_ARRAY.add(new SerialPerm());
    PERM_ARRAY.add(new ServicePerm());
    PERM_ARRAY.add(new SocketPerm());
    PERM_ARRAY.add(new SQLPerm());
    PERM_ARRAY.add(new SSLPerm());
    PERM_ARRAY.add(new SubjDelegPerm());
    PRIN_ARRAY = new ArrayList();
    PRIN_ARRAY.add(new KrbPrin());
    PRIN_ARRAY.add(new X500Prin());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ToolDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */