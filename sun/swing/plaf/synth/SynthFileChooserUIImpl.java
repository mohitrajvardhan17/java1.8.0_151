package sun.swing.plaf.synth;

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
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.synth.SynthContext;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.FilePane.FileChooserUIAccessor;
import sun.swing.SwingUtilities2;

public class SynthFileChooserUIImpl
  extends SynthFileChooserUI
{
  private JLabel lookInLabel;
  private JComboBox<File> directoryComboBox;
  private DirectoryComboBoxModel directoryComboBoxModel;
  private Action directoryComboBoxAction = new DirectoryComboBoxAction();
  private FilterComboBoxModel filterComboBoxModel;
  private JTextField fileNameTextField;
  private FilePane filePane;
  private JToggleButton listViewButton;
  private JToggleButton detailsViewButton;
  private boolean readOnly;
  private JPanel buttonPanel;
  private JPanel bottomPanel;
  private JComboBox<FileFilter> filterComboBox;
  private static final Dimension hstrut5 = new Dimension(5, 1);
  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
  private static Dimension LIST_PREF_SIZE = new Dimension(405, 135);
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
  private final PropertyChangeListener modeListener = new PropertyChangeListener()
  {
    public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
    {
      if (fileNameLabel != null) {
        SynthFileChooserUIImpl.this.populateFileNameLabel();
      }
    }
  };
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
  
  public SynthFileChooserUIImpl(JFileChooser paramJFileChooser)
  {
    super(paramJFileChooser);
  }
  
  protected void installDefaults(JFileChooser paramJFileChooser)
  {
    super.installDefaults(paramJFileChooser);
    readOnly = UIManager.getBoolean("FileChooser.readOnly");
  }
  
  public void installComponents(JFileChooser paramJFileChooser)
  {
    super.installComponents(paramJFileChooser);
    SynthContext localSynthContext = getContext(paramJFileChooser, 1);
    paramJFileChooser.setLayout(new BorderLayout(0, 11));
    JPanel localJPanel1 = new JPanel(new BorderLayout(11, 0));
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setLayout(new BoxLayout(localJPanel2, 2));
    localJPanel1.add(localJPanel2, "After");
    paramJFileChooser.add(localJPanel1, "North");
    lookInLabel = new JLabel(lookInLabelText);
    lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
    localJPanel1.add(lookInLabel, "Before");
    directoryComboBox = new JComboBox();
    directoryComboBox.getAccessibleContext().setAccessibleDescription(lookInLabelText);
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
    filePane = new FilePane(new SynthFileChooserUIAccessor(null));
    paramJFileChooser.addPropertyChangeListener(filePane);
    JPopupMenu localJPopupMenu = filePane.getComponentPopupMenu();
    if (localJPopupMenu != null)
    {
      localJPopupMenu.insert(getChangeToParentDirectoryAction(), 0);
      if (File.separatorChar == '/') {
        localJPopupMenu.insert(getGoHomeAction(), 1);
      }
    }
    FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
    JButton localJButton1 = new JButton(getChangeToParentDirectoryAction());
    localJButton1.setText(null);
    localJButton1.setIcon(upFolderIcon);
    localJButton1.setToolTipText(upFolderToolTipText);
    localJButton1.getAccessibleContext().setAccessibleName(upFolderAccessibleName);
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
    localJButton2.getAccessibleContext().setAccessibleName(homeFolderAccessibleName);
    localJButton2.setAlignmentX(0.0F);
    localJButton2.setAlignmentY(0.5F);
    localJButton2.setMargin(shrinkwrap);
    localJButton2.addActionListener(getGoHomeAction());
    localJPanel2.add(localJButton2);
    localJPanel2.add(Box.createRigidArea(hstrut5));
    if (!readOnly)
    {
      localJButton2 = new JButton(filePane.getNewFolderAction());
      localJButton2.setText(null);
      localJButton2.setIcon(newFolderIcon);
      localJButton2.setToolTipText(newFolderToolTipText);
      localJButton2.getAccessibleContext().setAccessibleName(newFolderAccessibleName);
      localJButton2.setAlignmentX(0.0F);
      localJButton2.setAlignmentY(0.5F);
      localJButton2.setMargin(shrinkwrap);
      localJPanel2.add(localJButton2);
      localJPanel2.add(Box.createRigidArea(hstrut5));
    }
    ButtonGroup localButtonGroup = new ButtonGroup();
    listViewButton = new JToggleButton(listViewIcon);
    listViewButton.setToolTipText(listViewButtonToolTipText);
    listViewButton.getAccessibleContext().setAccessibleName(listViewButtonAccessibleName);
    listViewButton.setSelected(true);
    listViewButton.setAlignmentX(0.0F);
    listViewButton.setAlignmentY(0.5F);
    listViewButton.setMargin(shrinkwrap);
    listViewButton.addActionListener(filePane.getViewTypeAction(0));
    localJPanel2.add(listViewButton);
    localButtonGroup.add(listViewButton);
    detailsViewButton = new JToggleButton(detailsViewIcon);
    detailsViewButton.setToolTipText(detailsViewButtonToolTipText);
    detailsViewButton.getAccessibleContext().setAccessibleName(detailsViewButtonAccessibleName);
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
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, 1));
    paramJFileChooser.add(bottomPanel, "South");
    JPanel localJPanel3 = new JPanel();
    localJPanel3.setLayout(new BoxLayout(localJPanel3, 2));
    bottomPanel.add(localJPanel3);
    bottomPanel.add(Box.createRigidArea(new Dimension(1, 5)));
    fileNameLabel = new AlignedLabel();
    populateFileNameLabel();
    localJPanel3.add(fileNameLabel);
    fileNameTextField = new JTextField(35)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(32767, getPreferredSizeheight);
      }
    };
    localJPanel3.add(fileNameTextField);
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
    JPanel localJPanel4 = new JPanel();
    localJPanel4.setLayout(new BoxLayout(localJPanel4, 2));
    bottomPanel.add(localJPanel4);
    AlignedLabel localAlignedLabel = new AlignedLabel(filesOfTypeLabelText);
    localAlignedLabel.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
    localJPanel4.add(localAlignedLabel);
    filterComboBoxModel = createFilterComboBoxModel();
    paramJFileChooser.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    filterComboBox.getAccessibleContext().setAccessibleDescription(filesOfTypeLabelText);
    localAlignedLabel.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    localJPanel4.add(filterComboBox);
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new ButtonAreaLayout(null));
    buttonPanel.add(getApproveButton(paramJFileChooser));
    buttonPanel.add(getCancelButton(paramJFileChooser));
    if (paramJFileChooser.getControlButtonsAreShown()) {
      addControlButtons();
    }
    groupLabels(new AlignedLabel[] { fileNameLabel, localAlignedLabel });
  }
  
  protected void installListeners(JFileChooser paramJFileChooser)
  {
    super.installListeners(paramJFileChooser);
    paramJFileChooser.addPropertyChangeListener("fileSelectionChanged", modeListener);
  }
  
  protected void uninstallListeners(JFileChooser paramJFileChooser)
  {
    paramJFileChooser.removePropertyChangeListener("fileSelectionChanged", modeListener);
    super.uninstallListeners(paramJFileChooser);
  }
  
  private String fileNameString(File paramFile)
  {
    if (paramFile == null) {
      return null;
    }
    JFileChooser localJFileChooser = getFileChooser();
    if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) {
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
  
  public void uninstallUI(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(filterComboBoxModel);
    paramJComponent.removePropertyChangeListener(filePane);
    if (filePane != null)
    {
      filePane.uninstallUI();
      filePane = null;
    }
    super.uninstallUI(paramJComponent);
  }
  
  protected void installStrings(JFileChooser paramJFileChooser)
  {
    super.installStrings(paramJFileChooser);
    Locale localLocale = paramJFileChooser.getLocale();
    lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", localLocale);
    lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", localLocale);
    saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", localLocale);
    fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", localLocale);
    fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", localLocale);
    folderNameLabelMnemonic = getMnemonic("FileChooser.folderNameLabelMnemonic", localLocale);
    folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", localLocale);
    filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", localLocale);
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
  
  private int getMnemonic(String paramString, Locale paramLocale)
  {
    return SwingUtilities2.getUIDefaultsInt(paramString, paramLocale);
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
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser)
  {
    filePane.rescanCurrentDirectory();
  }
  
  protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.doSelectedFileChanged(paramPropertyChangeEvent);
    File localFile = (File)paramPropertyChangeEvent.getNewValue();
    JFileChooser localJFileChooser = getFileChooser();
    if ((localFile != null) && (((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || ((localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled())))) {
      setFileName(fileNameString(localFile));
    }
  }
  
  protected void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.doSelectedFilesChanged(paramPropertyChangeEvent);
    File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
    JFileChooser localJFileChooser = getFileChooser();
    if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory()))) {
      setFileName(fileNameString(arrayOfFile));
    }
  }
  
  protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.doDirectoryChanged(paramPropertyChangeEvent);
    JFileChooser localJFileChooser = getFileChooser();
    FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
    File localFile = localJFileChooser.getCurrentDirectory();
    if ((!readOnly) && (localFile != null)) {
      getNewFolderAction().setEnabled(filePane.canWrite(localFile));
    }
    if (localFile != null)
    {
      JComponent localJComponent = getDirectoryComboBox();
      if ((localJComponent instanceof JComboBox))
      {
        ComboBoxModel localComboBoxModel = ((JComboBox)localJComponent).getModel();
        if ((localComboBoxModel instanceof DirectoryComboBoxModel)) {
          ((DirectoryComboBoxModel)localComboBoxModel).addItem(localFile);
        }
      }
      if ((localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled())) {
        if (localFileSystemView.isFileSystem(localFile)) {
          setFileName(localFile.getPath());
        } else {
          setFileName(null);
        }
      }
    }
  }
  
  protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.doFileSelectionModeChanged(paramPropertyChangeEvent);
    JFileChooser localJFileChooser = getFileChooser();
    File localFile = localJFileChooser.getCurrentDirectory();
    if ((localFile != null) && (localJFileChooser.isDirectorySelectionEnabled()) && (!localJFileChooser.isFileSelectionEnabled()) && (localJFileChooser.getFileSystemView().isFileSystem(localFile))) {
      setFileName(localFile.getPath());
    } else {
      setFileName(null);
    }
  }
  
  protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
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
  
  protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.doControlButtonsChanged(paramPropertyChangeEvent);
    if (getFileChooser().getControlButtonsAreShown()) {
      addControlButtons();
    } else {
      removeControlButtons();
    }
  }
  
  protected void addControlButtons()
  {
    if (bottomPanel != null) {
      bottomPanel.add(buttonPanel);
    }
  }
  
  protected void removeControlButtons()
  {
    if (bottomPanel != null) {
      bottomPanel.remove(buttonPanel);
    }
  }
  
  protected ActionMap createActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    FilePane.addActionsToMap(localActionMapUIResource, filePane.getActions());
    localActionMapUIResource.put("fileNameCompletion", getFileNameCompletionAction());
    return localActionMapUIResource;
  }
  
  protected JComponent getDirectoryComboBox()
  {
    return directoryComboBox;
  }
  
  protected Action getDirectoryComboBoxAction()
  {
    return directoryComboBoxAction;
  }
  
  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser paramJFileChooser)
  {
    return new DirectoryComboBoxRenderer(directoryComboBox.getRenderer(), null);
  }
  
  protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser paramJFileChooser)
  {
    return new DirectoryComboBoxModel();
  }
  
  protected FilterComboBoxRenderer createFilterComboBoxRenderer()
  {
    return new FilterComboBoxRenderer(filterComboBox.getRenderer(), null);
  }
  
  protected FilterComboBoxModel createFilterComboBoxModel()
  {
    return new FilterComboBoxModel();
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
      JComponent localJComponent = getDirectoryComboBox();
      if ((localJComponent instanceof JComboBox))
      {
        File localFile = (File)((JComboBox)localJComponent).getSelectedItem();
        getFileChooser().setCurrentDirectory(localFile);
      }
    }
  }
  
  protected class DirectoryComboBoxModel
    extends AbstractListModel<File>
    implements ComboBoxModel<File>
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
    
    public void addItem(File paramFile)
    {
      if (paramFile == null) {
        return;
      }
      boolean bool = FilePane.usesShellFolder(chooser);
      int i = directories.size();
      directories.clear();
      if (i > 0) {
        fireIntervalRemoved(this, 0, i);
      }
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
        int j = localVector.size();
        for (int k = 0; k < j; k++)
        {
          localFile3 = (File)localVector.get(k);
          if (directories.contains(localFile3))
          {
            int m = directories.indexOf(localFile3);
            for (int n = k - 1; n >= 0; n--) {
              directories.insertElementAt(localVector.get(n), m + k - n);
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
    
    public File getElementAt(int paramInt)
    {
      return (File)directories.elementAt(paramInt);
    }
  }
  
  private class DirectoryComboBoxRenderer
    implements ListCellRenderer<File>
  {
    private ListCellRenderer<? super File> delegate;
    SynthFileChooserUIImpl.IndentIcon ii = new SynthFileChooserUIImpl.IndentIcon(SynthFileChooserUIImpl.this);
    
    private DirectoryComboBoxRenderer()
    {
      ListCellRenderer localListCellRenderer;
      delegate = localListCellRenderer;
    }
    
    public Component getListCellRendererComponent(JList<? extends File> paramJList, File paramFile, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      Component localComponent = delegate.getListCellRendererComponent(paramJList, paramFile, paramInt, paramBoolean1, paramBoolean2);
      assert ((localComponent instanceof JLabel));
      JLabel localJLabel = (JLabel)localComponent;
      if (paramFile == null)
      {
        localJLabel.setText("");
        return localJLabel;
      }
      localJLabel.setText(getFileChooser().getName(paramFile));
      Icon localIcon = getFileChooser().getIcon(paramFile);
      ii.icon = localIcon;
      ii.depth = directoryComboBoxModel.getDepth(paramInt);
      localJLabel.setIcon(ii);
      return localJLabel;
    }
  }
  
  protected class FilterComboBoxModel
    extends AbstractListModel<FileFilter>
    implements ComboBoxModel<FileFilter>, PropertyChangeListener
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
    
    public FileFilter getElementAt(int paramInt)
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
    implements ListCellRenderer<FileFilter>
  {
    private ListCellRenderer<? super FileFilter> delegate;
    
    private FilterComboBoxRenderer()
    {
      ListCellRenderer localListCellRenderer;
      delegate = localListCellRenderer;
    }
    
    public Component getListCellRendererComponent(JList<? extends FileFilter> paramJList, FileFilter paramFileFilter, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      Component localComponent = delegate.getListCellRendererComponent(paramJList, paramFileFilter, paramInt, paramBoolean1, paramBoolean2);
      String str = null;
      if (paramFileFilter != null) {
        str = paramFileFilter.getDescription();
      }
      assert ((localComponent instanceof JLabel));
      if (str != null) {
        ((JLabel)localComponent).setText(str);
      }
      return localComponent;
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
      if (icon != null) {
        if (paramComponent.getComponentOrientation().isLeftToRight()) {
          icon.paintIcon(paramComponent, paramGraphics, paramInt1 + depth * 10, paramInt2);
        } else {
          icon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        }
      }
    }
    
    public int getIconWidth()
    {
      return (icon != null ? icon.getIconWidth() : 0) + depth * 10;
    }
    
    public int getIconHeight()
    {
      return icon != null ? icon.getIconHeight() : 0;
    }
  }
  
  private class SynthFileChooserUIAccessor
    implements FilePane.FileChooserUIAccessor
  {
    private SynthFileChooserUIAccessor() {}
    
    public JFileChooser getFileChooser()
    {
      return SynthFileChooserUIImpl.this.getFileChooser();
    }
    
    public BasicDirectoryModel getModel()
    {
      return SynthFileChooserUIImpl.this.getModel();
    }
    
    public JPanel createList()
    {
      return null;
    }
    
    public JPanel createDetailsView()
    {
      return null;
    }
    
    public boolean isDirectorySelected()
    {
      return SynthFileChooserUIImpl.this.isDirectorySelected();
    }
    
    public File getDirectory()
    {
      return SynthFileChooserUIImpl.this.getDirectory();
    }
    
    public Action getChangeToParentDirectoryAction()
    {
      return SynthFileChooserUIImpl.this.getChangeToParentDirectoryAction();
    }
    
    public Action getApproveSelectionAction()
    {
      return SynthFileChooserUIImpl.this.getApproveSelectionAction();
    }
    
    public Action getNewFolderAction()
    {
      return SynthFileChooserUIImpl.this.getNewFolderAction();
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\plaf\synth\SynthFileChooserUIImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */