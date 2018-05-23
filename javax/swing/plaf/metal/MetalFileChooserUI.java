package javax.swing.plaf.metal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.FilePane.FileChooserUIAccessor;
import sun.swing.SwingUtilities2;

public class MetalFileChooserUI
  extends BasicFileChooserUI
{
  private JLabel lookInLabel;
  private JComboBox directoryComboBox;
  private DirectoryComboBoxModel directoryComboBoxModel;
  private Action directoryComboBoxAction = new DirectoryComboBoxAction();
  private FilterComboBoxModel filterComboBoxModel;
  private JTextField fileNameTextField;
  private FilePane filePane;
  private JToggleButton listViewButton;
  private JToggleButton detailsViewButton;
  private JButton approveButton;
  private JButton cancelButton;
  private JPanel buttonPanel;
  private JPanel bottomPanel;
  private JComboBox filterComboBox;
  private static final Dimension hstrut5 = new Dimension(5, 1);
  private static final Dimension hstrut11 = new Dimension(11, 1);
  private static final Dimension vstrut5 = new Dimension(1, 5);
  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
  private static int PREF_WIDTH = 500;
  private static int PREF_HEIGHT = 326;
  private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
  private static int MIN_WIDTH = 500;
  private static int MIN_HEIGHT = 326;
  private static int LIST_PREF_WIDTH = 405;
  private static int LIST_PREF_HEIGHT = 135;
  private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
  private int lookInLabelMnemonic = 0;
  private String lookInLabelText = null;
  private String saveInLabelText = null;
  private int fileNameLabelMnemonic = 0;
  private String fileNameLabelText = null;
  private int folderNameLabelMnemonic = 0;
  private String folderNameLabelText = null;
  private int filesOfTypeLabelMnemonic = 0;
  private String filesOfTypeLabelText = null;
  private String upFolderToolTipText = null;
  private String upFolderAccessibleName = null;
  private String homeFolderToolTipText = null;
  private String homeFolderAccessibleName = null;
  private String newFolderToolTipText = null;
  private String newFolderAccessibleName = null;
  private String listViewButtonToolTipText = null;
  private String listViewButtonAccessibleName = null;
  private String detailsViewButtonToolTipText = null;
  private String detailsViewButtonAccessibleName = null;
  private AlignedLabel fileNameLabel;
  static final int space = 10;
  
  private void populateFileNameLabel()
  {
    if (getFileChooser().getFileSelectionMode() == 1)
    {
      fileNameLabel.setText(folderNameLabelText);
      fileNameLabel.setDisplayedMnemonic(folderNameLabelMnemonic);
    }
    else
    {
      fileNameLabel.setText(fileNameLabelText);
      fileNameLabel.setDisplayedMnemonic(fileNameLabelMnemonic);
    }
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalFileChooserUI((JFileChooser)paramJComponent);
  }
  
  public MetalFileChooserUI(JFileChooser paramJFileChooser)
  {
    super(paramJFileChooser);
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
  }
  
  public void uninstallComponents(JFileChooser paramJFileChooser)
  {
    paramJFileChooser.removeAll();
    bottomPanel = null;
    buttonPanel = null;
  }
  
  public void installComponents(JFileChooser paramJFileChooser)
  {
    FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
    paramJFileChooser.setBorder(new EmptyBorder(12, 12, 11, 11));
    paramJFileChooser.setLayout(new BorderLayout(0, 11));
    filePane = new FilePane(new MetalFileChooserUIAccessor(null));
    paramJFileChooser.addPropertyChangeListener(filePane);
    JPanel localJPanel1 = new JPanel(new BorderLayout(11, 0));
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setLayout(new BoxLayout(localJPanel2, 2));
    localJPanel1.add(localJPanel2, "After");
    paramJFileChooser.add(localJPanel1, "North");
    lookInLabel = new JLabel(lookInLabelText);
    lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
    localJPanel1.add(lookInLabel, "Before");
    directoryComboBox = new JComboBox()
    {
      public Dimension getPreferredSize()
      {
        Dimension localDimension = super.getPreferredSize();
        width = 150;
        return localDimension;
      }
    };
    directoryComboBox.putClientProperty("AccessibleDescription", lookInLabelText);
    directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    lookInLabel.setLabelFor(directoryComboBox);
    directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
    directoryComboBox.setModel(directoryComboBoxModel);
    directoryComboBox.addActionListener(directoryComboBoxAction);
    directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
    directoryComboBox.setAlignmentX(0.0F);
    directoryComboBox.setAlignmentY(0.0F);
    directoryComboBox.setMaximumRowCount(8);
    localJPanel1.add(directoryComboBox, "Center");
    JButton localJButton1 = new JButton(getChangeToParentDirectoryAction());
    localJButton1.setText(null);
    localJButton1.setIcon(upFolderIcon);
    localJButton1.setToolTipText(upFolderToolTipText);
    localJButton1.putClientProperty("AccessibleName", upFolderAccessibleName);
    localJButton1.setAlignmentX(0.0F);
    localJButton1.setAlignmentY(0.5F);
    localJButton1.setMargin(shrinkwrap);
    localJPanel2.add(localJButton1);
    localJPanel2.add(Box.createRigidArea(hstrut5));
    File localFile = localFileSystemView.getHomeDirectory();
    String str = homeFolderToolTipText;
    if (localFileSystemView.isRoot(localFile)) {
      str = getFileView(paramJFileChooser).getName(localFile);
    }
    JButton localJButton2 = new JButton(homeFolderIcon);
    localJButton2.setToolTipText(str);
    localJButton2.putClientProperty("AccessibleName", homeFolderAccessibleName);
    localJButton2.setAlignmentX(0.0F);
    localJButton2.setAlignmentY(0.5F);
    localJButton2.setMargin(shrinkwrap);
    localJButton2.addActionListener(getGoHomeAction());
    localJPanel2.add(localJButton2);
    localJPanel2.add(Box.createRigidArea(hstrut5));
    if (!UIManager.getBoolean("FileChooser.readOnly"))
    {
      localJButton2 = new JButton(filePane.getNewFolderAction());
      localJButton2.setText(null);
      localJButton2.setIcon(newFolderIcon);
      localJButton2.setToolTipText(newFolderToolTipText);
      localJButton2.putClientProperty("AccessibleName", newFolderAccessibleName);
      localJButton2.setAlignmentX(0.0F);
      localJButton2.setAlignmentY(0.5F);
      localJButton2.setMargin(shrinkwrap);
    }
    localJPanel2.add(localJButton2);
    localJPanel2.add(Box.createRigidArea(hstrut5));
    ButtonGroup localButtonGroup = new ButtonGroup();
    listViewButton = new JToggleButton(listViewIcon);
    listViewButton.setToolTipText(listViewButtonToolTipText);
    listViewButton.putClientProperty("AccessibleName", listViewButtonAccessibleName);
    listViewButton.setSelected(true);
    listViewButton.setAlignmentX(0.0F);
    listViewButton.setAlignmentY(0.5F);
    listViewButton.setMargin(shrinkwrap);
    listViewButton.addActionListener(filePane.getViewTypeAction(0));
    localJPanel2.add(listViewButton);
    localButtonGroup.add(listViewButton);
    detailsViewButton = new JToggleButton(detailsViewIcon);
    detailsViewButton.setToolTipText(detailsViewButtonToolTipText);
    detailsViewButton.putClientProperty("AccessibleName", detailsViewButtonAccessibleName);
    detailsViewButton.setAlignmentX(0.0F);
    detailsViewButton.setAlignmentY(0.5F);
    detailsViewButton.setMargin(shrinkwrap);
    detailsViewButton.addActionListener(filePane.getViewTypeAction(1));
    localJPanel2.add(detailsViewButton);
    localButtonGroup.add(detailsViewButton);
    filePane.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        if ("viewType".equals(paramAnonymousPropertyChangeEvent.getPropertyName()))
        {
          int i = filePane.getViewType();
          switch (i)
          {
          case 0: 
            listViewButton.setSelected(true);
            break;
          case 1: 
            detailsViewButton.setSelected(true);
          }
        }
      }
    });
    paramJFileChooser.add(getAccessoryPanel(), "After");
    JComponent localJComponent = paramJFileChooser.getAccessory();
    if (localJComponent != null) {
      getAccessoryPanel().add(localJComponent);
    }
    filePane.setPreferredSize(LIST_PREF_SIZE);
    paramJFileChooser.add(filePane, "Center");
    JPanel localJPanel3 = getBottomPanel();
    localJPanel3.setLayout(new BoxLayout(localJPanel3, 1));
    paramJFileChooser.add(localJPanel3, "South");
    JPanel localJPanel4 = new JPanel();
    localJPanel4.setLayout(new BoxLayout(localJPanel4, 2));
    localJPanel3.add(localJPanel4);
    localJPanel3.add(Box.createRigidArea(vstrut5));
    fileNameLabel = new AlignedLabel();
    populateFileNameLabel();
    localJPanel4.add(fileNameLabel);
    fileNameTextField = new JTextField(35)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(32767, getPreferredSizeheight);
      }
    };
    localJPanel4.add(fileNameTextField);
    fileNameLabel.setLabelFor(fileNameTextField);
    fileNameTextField.addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        if (!getFileChooser().isMultiSelectionEnabled()) {
          filePane.clearSelection();
        }
      }
    });
    if (paramJFileChooser.isMultiSelectionEnabled()) {
      setFileName(fileNameString(paramJFileChooser.getSelectedFiles()));
    } else {
      setFileName(fileNameString(paramJFileChooser.getSelectedFile()));
    }
    JPanel localJPanel5 = new JPanel();
    localJPanel5.setLayout(new BoxLayout(localJPanel5, 2));
    localJPanel3.add(localJPanel5);
    AlignedLabel localAlignedLabel = new AlignedLabel(filesOfTypeLabelText);
    localAlignedLabel.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
    localJPanel5.add(localAlignedLabel);
    filterComboBoxModel = createFilterComboBoxModel();
    paramJFileChooser.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    filterComboBox.putClientProperty("AccessibleDescription", filesOfTypeLabelText);
    localAlignedLabel.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    localJPanel5.add(filterComboBox);
    getButtonPanel().setLayout(new ButtonAreaLayout(null));
    approveButton = new JButton(getApproveButtonText(paramJFileChooser));
    approveButton.addActionListener(getApproveSelectionAction());
    approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    getButtonPanel().add(approveButton);
    cancelButton = new JButton(cancelButtonText);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());
    getButtonPanel().add(cancelButton);
    if (paramJFileChooser.getControlButtonsAreShown()) {
      addControlButtons();
    }
    groupLabels(new AlignedLabel[] { fileNameLabel, localAlignedLabel });
  }
  
  protected JPanel getButtonPanel()
  {
    if (buttonPanel == null) {
      buttonPanel = new JPanel();
    }
    return buttonPanel;
  }
  
  protected JPanel getBottomPanel()
  {
    if (bottomPanel == null) {
      bottomPanel = new JPanel();
    }
    return bottomPanel;
  }
  
  protected void installStrings(JFileChooser paramJFileChooser)
  {
    super.installStrings(paramJFileChooser);
    Locale localLocale = paramJFileChooser.getLocale();
    lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", localLocale).intValue();
    lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", localLocale);
    saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", localLocale);
    fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", localLocale).intValue();
    fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", localLocale);
    folderNameLabelMnemonic = getMnemonic("FileChooser.folderNameLabelMnemonic", localLocale).intValue();
    folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", localLocale);
    filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", localLocale).intValue();
    filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", localLocale);
    upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", localLocale);
    upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", localLocale);
    homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", localLocale);
    homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", localLocale);
    newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", localLocale);
    newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", localLocale);
    listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", localLocale);
    listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", localLocale);
    detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", localLocale);
    detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", localLocale);
  }
  
  private Integer getMnemonic(String paramString, Locale paramLocale)
  {
    return Integer.valueOf(SwingUtilities2.getUIDefaultsInt(paramString, paramLocale));
  }
  
  protected void installListeners(JFileChooser paramJFileChooser)
  {
    super.installListeners(paramJFileChooser);
    ActionMap localActionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(paramJFileChooser, localActionMap);
  }
  
  protected ActionMap getActionMap()
  {
    return createActionMap();
  }
  
  protected ActionMap createActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    FilePane.addActionsToMap(localActionMapUIResource, filePane.getActions());
    return localActionMapUIResource;
  }
  
  protected JPanel createList(JFileChooser paramJFileChooser)
  {
    return filePane.createList();
  }
  
  protected JPanel createDetailsView(JFileChooser paramJFileChooser)
  {
    return filePane.createDetailsView();
  }
  
  public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser)
  {
    return super.createListSelectionListener(paramJFileChooser);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(filterComboBoxModel);
    paramJComponent.removePropertyChangeListener(filePane);
    cancelButton.removeActionListener(getCancelSelectionAction());
    approveButton.removeActionListener(getApproveSelectionAction());
    fileNameTextField.removeActionListener(getApproveSelectionAction());
    if (filePane != null)
    {
      filePane.uninstallUI();
      filePane = null;
    }
    super.uninstallUI(paramJComponent);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    int i = PREF_SIZEwidth;
    Dimension localDimension = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
    if (localDimension != null) {
      return new Dimension(width < i ? i : width, height < PREF_SIZEheight ? PREF_SIZEheight : height);
    }
    return new Dimension(i, PREF_SIZEheight);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return new Dimension(MIN_WIDTH, MIN_HEIGHT);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  private String fileNameString(File paramFile)
  {
    if (paramFile == null) {
      return null;
    }
    JFileChooser localJFileChooser = getFileChooser();
    if (((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) || ((localJFileChooser.isDirectorySelectionEnabled()) && (localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystemRoot(paramFile)))) {
      return paramFile.getPath();
    }
    return paramFile.getName();
  }
  
  private String fileNameString(File[] paramArrayOfFile)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; (paramArrayOfFile != null) && (i < paramArrayOfFile.length); i++)
    {
      if (i > 0) {
        localStringBuffer.append(" ");
      }
      if (paramArrayOfFile.length > 1) {
        localStringBuffer.append("\"");
      }
      localStringBuffer.append(fileNameString(paramArrayOfFile[i]));
      if (paramArrayOfFile.length > 1) {
        localStringBuffer.append("\"");
      }
    }
    return localStringBuffer.toString();
  }
  
  private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    File localFile = (File)paramPropertyChangeEvent.getNewValue();
    JFileChooser localJFileChooser = getFileChooser();
    if ((localFile != null) && (((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || ((localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled())))) {
      setFileName(fileNameString(localFile));
    }
  }
  
  private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
    JFileChooser localJFileChooser = getFileChooser();
    if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory()))) {
      setFileName(fileNameString(arrayOfFile));
    }
  }
  
  private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JFileChooser localJFileChooser = getFileChooser();
    FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
    clearIconCache();
    File localFile = localJFileChooser.getCurrentDirectory();
    if (localFile != null)
    {
      directoryComboBoxModel.addItem(localFile);
      if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) {
        if (localFileSystemView.isFileSystem(localFile)) {
          setFileName(localFile.getPath());
        } else {
          setFileName(null);
        }
      }
    }
  }
  
  private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    clearIconCache();
  }
  
  private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (fileNameLabel != null) {
      populateFileNameLabel();
    }
    clearIconCache();
    JFileChooser localJFileChooser = getFileChooser();
    File localFile = localJFileChooser.getCurrentDirectory();
    if ((localFile != null) && (localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystem(localFile))) {
      setFileName(localFile.getPath());
    } else {
      setFileName(null);
    }
  }
  
  private void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (getAccessoryPanel() != null)
    {
      if (paramPropertyChangeEvent.getOldValue() != null) {
        getAccessoryPanel().remove((JComponent)paramPropertyChangeEvent.getOldValue());
      }
      JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getNewValue();
      if (localJComponent != null) {
        getAccessoryPanel().add(localJComponent, "Center");
      }
    }
  }
  
  private void doApproveButtonTextChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JFileChooser localJFileChooser = getFileChooser();
    approveButton.setText(getApproveButtonText(localJFileChooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
  }
  
  private void doDialogTypeChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JFileChooser localJFileChooser = getFileChooser();
    approveButton.setText(getApproveButtonText(localJFileChooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
    if (localJFileChooser.getDialogType() == 1) {
      lookInLabel.setText(saveInLabelText);
    } else {
      lookInLabel.setText(lookInLabelText);
    }
  }
  
  private void doApproveButtonMnemonicChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (getFileChooser().getControlButtonsAreShown()) {
      addControlButtons();
    } else {
      removeControlButtons();
    }
  }
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser)
  {
    new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        String str = paramAnonymousPropertyChangeEvent.getPropertyName();
        if (str.equals("SelectedFileChangedProperty"))
        {
          MetalFileChooserUI.this.doSelectedFileChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("SelectedFilesChangedProperty"))
        {
          MetalFileChooserUI.this.doSelectedFilesChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("directoryChanged"))
        {
          MetalFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("fileFilterChanged"))
        {
          MetalFileChooserUI.this.doFilterChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("fileSelectionChanged"))
        {
          MetalFileChooserUI.this.doFileSelectionModeChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("AccessoryChangedProperty"))
        {
          MetalFileChooserUI.this.doAccessoryChanged(paramAnonymousPropertyChangeEvent);
        }
        else if ((str.equals("ApproveButtonTextChangedProperty")) || (str.equals("ApproveButtonToolTipTextChangedProperty")))
        {
          MetalFileChooserUI.this.doApproveButtonTextChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("DialogTypeChangedProperty"))
        {
          MetalFileChooserUI.this.doDialogTypeChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("ApproveButtonMnemonicChangedProperty"))
        {
          MetalFileChooserUI.this.doApproveButtonMnemonicChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("ControlButtonsAreShownChangedProperty"))
        {
          MetalFileChooserUI.this.doControlButtonsChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("componentOrientation"))
        {
          ComponentOrientation localComponentOrientation = (ComponentOrientation)paramAnonymousPropertyChangeEvent.getNewValue();
          JFileChooser localJFileChooser = (JFileChooser)paramAnonymousPropertyChangeEvent.getSource();
          if (localComponentOrientation != paramAnonymousPropertyChangeEvent.getOldValue()) {
            localJFileChooser.applyComponentOrientation(localComponentOrientation);
          }
        }
        else if (str == "FileChooser.useShellFolder")
        {
          MetalFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
        }
        else if ((str.equals("ancestor")) && (paramAnonymousPropertyChangeEvent.getOldValue() == null) && (paramAnonymousPropertyChangeEvent.getNewValue() != null))
        {
          fileNameTextField.selectAll();
          fileNameTextField.requestFocus();
        }
      }
    };
  }
  
  protected void removeControlButtons()
  {
    getBottomPanel().remove(getButtonPanel());
  }
  
  protected void addControlButtons()
  {
    getBottomPanel().add(getButtonPanel());
  }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile)
  {
    filePane.ensureFileIsVisible(paramJFileChooser, paramFile);
  }
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser)
  {
    filePane.rescanCurrentDirectory();
  }
  
  public String getFileName()
  {
    if (fileNameTextField != null) {
      return fileNameTextField.getText();
    }
    return null;
  }
  
  public void setFileName(String paramString)
  {
    if (fileNameTextField != null) {
      fileNameTextField.setText(paramString);
    }
  }
  
  protected void setDirectorySelected(boolean paramBoolean)
  {
    super.setDirectorySelected(paramBoolean);
    JFileChooser localJFileChooser = getFileChooser();
    if (paramBoolean)
    {
      if (approveButton != null)
      {
        approveButton.setText(directoryOpenButtonText);
        approveButton.setToolTipText(directoryOpenButtonToolTipText);
      }
    }
    else if (approveButton != null)
    {
      approveButton.setText(getApproveButtonText(localJFileChooser));
      approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
    }
  }
  
  public String getDirectoryName()
  {
    return null;
  }
  
  public void setDirectoryName(String paramString) {}
  
  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser)
  {
    return new DirectoryComboBoxRenderer();
  }
  
  protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser)
  {
    return new DirectoryComboBoxModel();
  }
  
  protected FilterComboBoxRenderer createFilterComboBoxRenderer()
  {
    return new FilterComboBoxRenderer();
  }
  
  protected FilterComboBoxModel createFilterComboBoxModel()
  {
    return new FilterComboBoxModel();
  }
  
  public void valueChanged(ListSelectionEvent paramListSelectionEvent)
  {
    JFileChooser localJFileChooser = getFileChooser();
    File localFile = localJFileChooser.getSelectedFile();
    if ((!paramListSelectionEvent.getValueIsAdjusting()) && (localFile != null) && (!getFileChooser().isTraversable(localFile))) {
      setFileName(fileNameString(localFile));
    }
  }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser)
  {
    return approveButton;
  }
  
  private static void groupLabels(AlignedLabel[] paramArrayOfAlignedLabel)
  {
    for (int i = 0; i < paramArrayOfAlignedLabel.length; i++) {
      group = paramArrayOfAlignedLabel;
    }
  }
  
  private class AlignedLabel
    extends JLabel
  {
    private AlignedLabel[] group;
    private int maxWidth = 0;
    
    AlignedLabel()
    {
      setAlignmentX(0.0F);
    }
    
    AlignedLabel(String paramString)
    {
      super();
      setAlignmentX(0.0F);
    }
    
    public Dimension getPreferredSize()
    {
      Dimension localDimension = super.getPreferredSize();
      return new Dimension(getMaxWidth() + 11, height);
    }
    
    private int getMaxWidth()
    {
      if ((maxWidth == 0) && (group != null))
      {
        int i = 0;
        for (int j = 0; j < group.length; j++) {
          i = Math.max(group[j].getSuperPreferredWidth(), i);
        }
        for (j = 0; j < group.length; j++) {
          group[j].maxWidth = i;
        }
      }
      return maxWidth;
    }
    
    private int getSuperPreferredWidth()
    {
      return getPreferredSizewidth;
    }
  }
  
  private static class ButtonAreaLayout
    implements LayoutManager
  {
    private int hGap = 5;
    private int topMargin = 17;
    
    private ButtonAreaLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void layoutContainer(Container paramContainer)
    {
      Component[] arrayOfComponent = paramContainer.getComponents();
      if ((arrayOfComponent != null) && (arrayOfComponent.length > 0))
      {
        int i = arrayOfComponent.length;
        Dimension[] arrayOfDimension = new Dimension[i];
        Insets localInsets = paramContainer.getInsets();
        int j = top + topMargin;
        int k = 0;
        for (int m = 0; m < i; m++)
        {
          arrayOfDimension[m] = arrayOfComponent[m].getPreferredSize();
          k = Math.max(k, width);
        }
        int n;
        if (paramContainer.getComponentOrientation().isLeftToRight())
        {
          m = getSizewidth - left - k;
          n = hGap + k;
        }
        else
        {
          m = left;
          n = -(hGap + k);
        }
        for (int i1 = i - 1; i1 >= 0; i1--)
        {
          arrayOfComponent[i1].setBounds(m, j, k, height);
          m -= n;
        }
      }
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      if (paramContainer != null)
      {
        Component[] arrayOfComponent = paramContainer.getComponents();
        if ((arrayOfComponent != null) && (arrayOfComponent.length > 0))
        {
          int i = arrayOfComponent.length;
          int j = 0;
          Insets localInsets = paramContainer.getInsets();
          int k = topMargin + top + bottom;
          int m = left + right;
          int n = 0;
          for (int i1 = 0; i1 < i; i1++)
          {
            Dimension localDimension = arrayOfComponent[i1].getPreferredSize();
            j = Math.max(j, height);
            n = Math.max(n, width);
          }
          return new Dimension(m + i * n + (i - 1) * hGap, k + j);
        }
      }
      return new Dimension(0, 0);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public void removeLayoutComponent(Component paramComponent) {}
  }
  
  protected class DirectoryComboBoxAction
    extends AbstractAction
  {
    protected DirectoryComboBoxAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      directoryComboBox.hidePopup();
      File localFile = (File)directoryComboBox.getSelectedItem();
      if (!getFileChooser().getCurrentDirectory().equals(localFile)) {
        getFileChooser().setCurrentDirectory(localFile);
      }
    }
  }
  
  protected class DirectoryComboBoxModel
    extends AbstractListModel<Object>
    implements ComboBoxModel<Object>
  {
    Vector<File> directories = new Vector();
    int[] depths = null;
    File selectedDirectory = null;
    JFileChooser chooser = getFileChooser();
    FileSystemView fsv = chooser.getFileSystemView();
    
    public DirectoryComboBoxModel()
    {
      File localFile = getFileChooser().getCurrentDirectory();
      if (localFile != null) {
        addItem(localFile);
      }
    }
    
    private void addItem(File paramFile)
    {
      if (paramFile == null) {
        return;
      }
      boolean bool = FilePane.usesShellFolder(chooser);
      directories.clear();
      File[] arrayOfFile = bool ? (File[])ShellFolder.get("fileChooserComboBoxFolders") : fsv.getRoots();
      directories.addAll(Arrays.asList(arrayOfFile));
      File localFile1;
      try
      {
        localFile1 = ShellFolder.getNormalizedFile(paramFile);
      }
      catch (IOException localIOException)
      {
        localFile1 = paramFile;
      }
      try
      {
        File localFile2 = bool ? ShellFolder.getShellFolder(localFile1) : localFile1;
        File localFile3 = localFile2;
        Vector localVector = new Vector(10);
        do
        {
          localVector.addElement(localFile3);
        } while ((localFile3 = localFile3.getParentFile()) != null);
        int i = localVector.size();
        for (int j = 0; j < i; j++)
        {
          localFile3 = (File)localVector.get(j);
          if (directories.contains(localFile3))
          {
            int k = directories.indexOf(localFile3);
            for (int m = j - 1; m >= 0; m--) {
              directories.insertElementAt(localVector.get(m), k + j - m);
            }
            break;
          }
        }
        calculateDepths();
        setSelectedItem(localFile2);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        calculateDepths();
      }
    }
    
    private void calculateDepths()
    {
      depths = new int[directories.size()];
      for (int i = 0; i < depths.length; i++)
      {
        File localFile1 = (File)directories.get(i);
        File localFile2 = localFile1.getParentFile();
        depths[i] = 0;
        if (localFile2 != null) {
          for (int j = i - 1; j >= 0; j--) {
            if (localFile2.equals(directories.get(j)))
            {
              depths[j] += 1;
              break;
            }
          }
        }
      }
    }
    
    public int getDepth(int paramInt)
    {
      return (depths != null) && (paramInt >= 0) && (paramInt < depths.length) ? depths[paramInt] : 0;
    }
    
    public void setSelectedItem(Object paramObject)
    {
      selectedDirectory = ((File)paramObject);
      fireContentsChanged(this, -1, -1);
    }
    
    public Object getSelectedItem()
    {
      return selectedDirectory;
    }
    
    public int getSize()
    {
      return directories.size();
    }
    
    public Object getElementAt(int paramInt)
    {
      return directories.elementAt(paramInt);
    }
  }
  
  class DirectoryComboBoxRenderer
    extends DefaultListCellRenderer
  {
    MetalFileChooserUI.IndentIcon ii = new MetalFileChooserUI.IndentIcon(MetalFileChooserUI.this);
    
    DirectoryComboBoxRenderer() {}
    
    public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
      if (paramObject == null)
      {
        setText("");
        return this;
      }
      File localFile = (File)paramObject;
      setText(getFileChooser().getName(localFile));
      Icon localIcon = getFileChooser().getIcon(localFile);
      ii.icon = localIcon;
      ii.depth = directoryComboBoxModel.getDepth(paramInt);
      setIcon(ii);
      return this;
    }
  }
  
  protected class FileRenderer
    extends DefaultListCellRenderer
  {
    protected FileRenderer() {}
  }
  
  protected class FilterComboBoxModel
    extends AbstractListModel<Object>
    implements ComboBoxModel<Object>, PropertyChangeListener
  {
    protected FileFilter[] filters = getFileChooser().getChoosableFileFilters();
    
    protected FilterComboBoxModel() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str == "ChoosableFileFilterChangedProperty")
      {
        filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
        fireContentsChanged(this, -1, -1);
      }
      else if (str == "fileFilterChanged")
      {
        fireContentsChanged(this, -1, -1);
      }
    }
    
    public void setSelectedItem(Object paramObject)
    {
      if (paramObject != null)
      {
        getFileChooser().setFileFilter((FileFilter)paramObject);
        fireContentsChanged(this, -1, -1);
      }
    }
    
    public Object getSelectedItem()
    {
      FileFilter localFileFilter1 = getFileChooser().getFileFilter();
      int i = 0;
      if (localFileFilter1 != null)
      {
        for (FileFilter localFileFilter2 : filters) {
          if (localFileFilter2 == localFileFilter1) {
            i = 1;
          }
        }
        if (i == 0) {
          getFileChooser().addChoosableFileFilter(localFileFilter1);
        }
      }
      return getFileChooser().getFileFilter();
    }
    
    public int getSize()
    {
      if (filters != null) {
        return filters.length;
      }
      return 0;
    }
    
    public Object getElementAt(int paramInt)
    {
      if (paramInt > getSize() - 1) {
        return getFileChooser().getFileFilter();
      }
      if (filters != null) {
        return filters[paramInt];
      }
      return null;
    }
  }
  
  public class FilterComboBoxRenderer
    extends DefaultListCellRenderer
  {
    public FilterComboBoxRenderer() {}
    
    public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
      if ((paramObject != null) && ((paramObject instanceof FileFilter))) {
        setText(((FileFilter)paramObject).getDescription());
      }
      return this;
    }
  }
  
  class IndentIcon
    implements Icon
  {
    Icon icon = null;
    int depth = 0;
    
    IndentIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (paramComponent.getComponentOrientation().isLeftToRight()) {
        icon.paintIcon(paramComponent, paramGraphics, paramInt1 + depth * 10, paramInt2);
      } else {
        icon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
      }
    }
    
    public int getIconWidth()
    {
      return icon.getIconWidth() + depth * 10;
    }
    
    public int getIconHeight()
    {
      return icon.getIconHeight();
    }
  }
  
  private class MetalFileChooserUIAccessor
    implements FilePane.FileChooserUIAccessor
  {
    private MetalFileChooserUIAccessor() {}
    
    public JFileChooser getFileChooser()
    {
      return MetalFileChooserUI.this.getFileChooser();
    }
    
    public BasicDirectoryModel getModel()
    {
      return MetalFileChooserUI.this.getModel();
    }
    
    public JPanel createList()
    {
      return createList(getFileChooser());
    }
    
    public JPanel createDetailsView()
    {
      return createDetailsView(getFileChooser());
    }
    
    public boolean isDirectorySelected()
    {
      return MetalFileChooserUI.this.isDirectorySelected();
    }
    
    public File getDirectory()
    {
      return MetalFileChooserUI.this.getDirectory();
    }
    
    public Action getChangeToParentDirectoryAction()
    {
      return MetalFileChooserUI.this.getChangeToParentDirectoryAction();
    }
    
    public Action getApproveSelectionAction()
    {
      return MetalFileChooserUI.this.getApproveSelectionAction();
    }
    
    public Action getNewFolderAction()
    {
      return MetalFileChooserUI.this.getNewFolderAction();
    }
    
    public MouseListener createDoubleClickListener(JList paramJList)
    {
      return createDoubleClickListener(getFileChooser(), paramJList);
    }
    
    public ListSelectionListener createListSelectionListener()
    {
      return createListSelectionListener(getFileChooser());
    }
  }
  
  protected class SingleClickListener
    extends MouseAdapter
  {
    public SingleClickListener(JList paramJList) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */