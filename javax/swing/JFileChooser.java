package javax.swing;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;

public class JFileChooser
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "FileChooserUI";
  public static final int OPEN_DIALOG = 0;
  public static final int SAVE_DIALOG = 1;
  public static final int CUSTOM_DIALOG = 2;
  public static final int CANCEL_OPTION = 1;
  public static final int APPROVE_OPTION = 0;
  public static final int ERROR_OPTION = -1;
  public static final int FILES_ONLY = 0;
  public static final int DIRECTORIES_ONLY = 1;
  public static final int FILES_AND_DIRECTORIES = 2;
  public static final String CANCEL_SELECTION = "CancelSelection";
  public static final String APPROVE_SELECTION = "ApproveSelection";
  public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";
  public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";
  public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";
  public static final String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";
  public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";
  public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";
  public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";
  public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
  public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";
  public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";
  public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";
  public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";
  public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";
  public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";
  public static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";
  public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";
  public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";
  public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";
  private String dialogTitle = null;
  private String approveButtonText = null;
  private String approveButtonToolTipText = null;
  private int approveButtonMnemonic = 0;
  private Vector<FileFilter> filters = new Vector(5);
  private JDialog dialog = null;
  private int dialogType = 0;
  private int returnValue = -1;
  private JComponent accessory = null;
  private FileView fileView = null;
  private boolean controlsShown = true;
  private boolean useFileHiding = true;
  private static final String SHOW_HIDDEN_PROP = "awt.file.showHiddenFiles";
  private transient PropertyChangeListener showFilesListener = null;
  private int fileSelectionMode = 0;
  private boolean multiSelectionEnabled = false;
  private boolean useAcceptAllFileFilter = true;
  private boolean dragEnabled = false;
  private FileFilter fileFilter = null;
  private FileSystemView fileSystemView = null;
  private File currentDirectory = null;
  private File selectedFile = null;
  private File[] selectedFiles;
  protected AccessibleContext accessibleContext = null;
  
  public JFileChooser()
  {
    this((File)null, (FileSystemView)null);
  }
  
  public JFileChooser(String paramString)
  {
    this(paramString, (FileSystemView)null);
  }
  
  public JFileChooser(File paramFile)
  {
    this(paramFile, (FileSystemView)null);
  }
  
  public JFileChooser(FileSystemView paramFileSystemView)
  {
    this((File)null, paramFileSystemView);
  }
  
  public JFileChooser(File paramFile, FileSystemView paramFileSystemView)
  {
    setup(paramFileSystemView);
    setCurrentDirectory(paramFile);
  }
  
  public JFileChooser(String paramString, FileSystemView paramFileSystemView)
  {
    setup(paramFileSystemView);
    if (paramString == null) {
      setCurrentDirectory(null);
    } else {
      setCurrentDirectory(fileSystemView.createFileObject(paramString));
    }
  }
  
  protected void setup(FileSystemView paramFileSystemView)
  {
    installShowFilesListener();
    installHierarchyListener();
    if (paramFileSystemView == null) {
      paramFileSystemView = FileSystemView.getFileSystemView();
    }
    setFileSystemView(paramFileSystemView);
    updateUI();
    if (isAcceptAllFileFilterUsed()) {
      setFileFilter(getAcceptAllFileFilter());
    }
    enableEvents(16L);
  }
  
  private void installHierarchyListener()
  {
    addHierarchyListener(new HierarchyListener()
    {
      public void hierarchyChanged(HierarchyEvent paramAnonymousHierarchyEvent)
      {
        if ((paramAnonymousHierarchyEvent.getChangeFlags() & 1L) == 1L)
        {
          JFileChooser localJFileChooser = JFileChooser.this;
          JRootPane localJRootPane = SwingUtilities.getRootPane(localJFileChooser);
          if (localJRootPane != null) {
            localJRootPane.setDefaultButton(localJFileChooser.getUI().getDefaultButton(localJFileChooser));
          }
        }
      }
    });
  }
  
  private void installShowFilesListener()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    Object localObject = localToolkit.getDesktopProperty("awt.file.showHiddenFiles");
    if ((localObject instanceof Boolean))
    {
      useFileHiding = (!((Boolean)localObject).booleanValue());
      showFilesListener = new WeakPCL(this);
      localToolkit.addPropertyChangeListener("awt.file.showHiddenFiles", showFilesListener);
    }
  }
  
  public void setDragEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
      throw new HeadlessException();
    }
    dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled()
  {
    return dragEnabled;
  }
  
  public File getSelectedFile()
  {
    return selectedFile;
  }
  
  public void setSelectedFile(File paramFile)
  {
    File localFile = selectedFile;
    selectedFile = paramFile;
    if (selectedFile != null)
    {
      if ((paramFile.isAbsolute()) && (!getFileSystemView().isParent(getCurrentDirectory(), selectedFile))) {
        setCurrentDirectory(selectedFile.getParentFile());
      }
      if ((!isMultiSelectionEnabled()) || (selectedFiles == null) || (selectedFiles.length == 1)) {
        ensureFileIsVisible(selectedFile);
      }
    }
    firePropertyChange("SelectedFileChangedProperty", localFile, selectedFile);
  }
  
  public File[] getSelectedFiles()
  {
    if (selectedFiles == null) {
      return new File[0];
    }
    return (File[])selectedFiles.clone();
  }
  
  public void setSelectedFiles(File[] paramArrayOfFile)
  {
    File[] arrayOfFile = selectedFiles;
    if ((paramArrayOfFile == null) || (paramArrayOfFile.length == 0))
    {
      paramArrayOfFile = null;
      selectedFiles = null;
      setSelectedFile(null);
    }
    else
    {
      selectedFiles = ((File[])paramArrayOfFile.clone());
      setSelectedFile(selectedFiles[0]);
    }
    firePropertyChange("SelectedFilesChangedProperty", arrayOfFile, paramArrayOfFile);
  }
  
  public File getCurrentDirectory()
  {
    return currentDirectory;
  }
  
  public void setCurrentDirectory(File paramFile)
  {
    File localFile1 = currentDirectory;
    if ((paramFile != null) && (!paramFile.exists())) {
      paramFile = currentDirectory;
    }
    if (paramFile == null) {
      paramFile = getFileSystemView().getDefaultDirectory();
    }
    if ((currentDirectory != null) && (currentDirectory.equals(paramFile))) {
      return;
    }
    File localFile2 = null;
    while ((!isTraversable(paramFile)) && (localFile2 != paramFile))
    {
      localFile2 = paramFile;
      paramFile = getFileSystemView().getParentDirectory(paramFile);
    }
    currentDirectory = paramFile;
    firePropertyChange("directoryChanged", localFile1, currentDirectory);
  }
  
  public void changeToParentDirectory()
  {
    selectedFile = null;
    File localFile = getCurrentDirectory();
    setCurrentDirectory(getFileSystemView().getParentDirectory(localFile));
  }
  
  public void rescanCurrentDirectory()
  {
    getUI().rescanCurrentDirectory(this);
  }
  
  public void ensureFileIsVisible(File paramFile)
  {
    getUI().ensureFileIsVisible(this, paramFile);
  }
  
  public int showOpenDialog(Component paramComponent)
    throws HeadlessException
  {
    setDialogType(0);
    return showDialog(paramComponent, null);
  }
  
  public int showSaveDialog(Component paramComponent)
    throws HeadlessException
  {
    setDialogType(1);
    return showDialog(paramComponent, null);
  }
  
  public int showDialog(Component paramComponent, String paramString)
    throws HeadlessException
  {
    if (dialog != null) {
      return -1;
    }
    if (paramString != null)
    {
      setApproveButtonText(paramString);
      setDialogType(2);
    }
    dialog = createDialog(paramComponent);
    dialog.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        returnValue = 1;
      }
    });
    returnValue = -1;
    rescanCurrentDirectory();
    dialog.show();
    firePropertyChange("JFileChooserDialogIsClosingProperty", dialog, null);
    dialog.getContentPane().removeAll();
    dialog.dispose();
    dialog = null;
    return returnValue;
  }
  
  protected JDialog createDialog(Component paramComponent)
    throws HeadlessException
  {
    FileChooserUI localFileChooserUI = getUI();
    String str = localFileChooserUI.getDialogTitle(this);
    putClientProperty("AccessibleDescription", str);
    Window localWindow = JOptionPane.getWindowForComponent(paramComponent);
    JDialog localJDialog;
    if ((localWindow instanceof Frame)) {
      localJDialog = new JDialog((Frame)localWindow, str, true);
    } else {
      localJDialog = new JDialog((Dialog)localWindow, str, true);
    }
    localJDialog.setComponentOrientation(getComponentOrientation());
    Container localContainer = localJDialog.getContentPane();
    localContainer.setLayout(new BorderLayout());
    localContainer.add(this, "Center");
    if (JDialog.isDefaultLookAndFeelDecorated())
    {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool) {
        localJDialog.getRootPane().setWindowDecorationStyle(6);
      }
    }
    localJDialog.pack();
    localJDialog.setLocationRelativeTo(paramComponent);
    return localJDialog;
  }
  
  public boolean getControlButtonsAreShown()
  {
    return controlsShown;
  }
  
  public void setControlButtonsAreShown(boolean paramBoolean)
  {
    if (controlsShown == paramBoolean) {
      return;
    }
    boolean bool = controlsShown;
    controlsShown = paramBoolean;
    firePropertyChange("ControlButtonsAreShownChangedProperty", bool, controlsShown);
  }
  
  public int getDialogType()
  {
    return dialogType;
  }
  
  public void setDialogType(int paramInt)
  {
    if (dialogType == paramInt) {
      return;
    }
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("Incorrect Dialog Type: " + paramInt);
    }
    int i = dialogType;
    dialogType = paramInt;
    if ((paramInt == 0) || (paramInt == 1)) {
      setApproveButtonText(null);
    }
    firePropertyChange("DialogTypeChangedProperty", i, paramInt);
  }
  
  public void setDialogTitle(String paramString)
  {
    String str = dialogTitle;
    dialogTitle = paramString;
    if (dialog != null) {
      dialog.setTitle(paramString);
    }
    firePropertyChange("DialogTitleChangedProperty", str, paramString);
  }
  
  public String getDialogTitle()
  {
    return dialogTitle;
  }
  
  public void setApproveButtonToolTipText(String paramString)
  {
    if (approveButtonToolTipText == paramString) {
      return;
    }
    String str = approveButtonToolTipText;
    approveButtonToolTipText = paramString;
    firePropertyChange("ApproveButtonToolTipTextChangedProperty", str, approveButtonToolTipText);
  }
  
  public String getApproveButtonToolTipText()
  {
    return approveButtonToolTipText;
  }
  
  public int getApproveButtonMnemonic()
  {
    return approveButtonMnemonic;
  }
  
  public void setApproveButtonMnemonic(int paramInt)
  {
    if (approveButtonMnemonic == paramInt) {
      return;
    }
    int i = approveButtonMnemonic;
    approveButtonMnemonic = paramInt;
    firePropertyChange("ApproveButtonMnemonicChangedProperty", i, approveButtonMnemonic);
  }
  
  public void setApproveButtonMnemonic(char paramChar)
  {
    int i = paramChar;
    if ((i >= 97) && (i <= 122)) {
      i -= 32;
    }
    setApproveButtonMnemonic(i);
  }
  
  public void setApproveButtonText(String paramString)
  {
    if (approveButtonText == paramString) {
      return;
    }
    String str = approveButtonText;
    approveButtonText = paramString;
    firePropertyChange("ApproveButtonTextChangedProperty", str, paramString);
  }
  
  public String getApproveButtonText()
  {
    return approveButtonText;
  }
  
  public FileFilter[] getChoosableFileFilters()
  {
    FileFilter[] arrayOfFileFilter = new FileFilter[filters.size()];
    filters.copyInto(arrayOfFileFilter);
    return arrayOfFileFilter;
  }
  
  public void addChoosableFileFilter(FileFilter paramFileFilter)
  {
    if ((paramFileFilter != null) && (!filters.contains(paramFileFilter)))
    {
      FileFilter[] arrayOfFileFilter = getChoosableFileFilters();
      filters.addElement(paramFileFilter);
      firePropertyChange("ChoosableFileFilterChangedProperty", arrayOfFileFilter, getChoosableFileFilters());
      if ((fileFilter == null) && (filters.size() == 1)) {
        setFileFilter(paramFileFilter);
      }
    }
  }
  
  public boolean removeChoosableFileFilter(FileFilter paramFileFilter)
  {
    int i = filters.indexOf(paramFileFilter);
    if (i >= 0)
    {
      if (getFileFilter() == paramFileFilter)
      {
        localObject = getAcceptAllFileFilter();
        if ((isAcceptAllFileFilterUsed()) && (localObject != paramFileFilter)) {
          setFileFilter((FileFilter)localObject);
        } else if (i > 0) {
          setFileFilter((FileFilter)filters.get(0));
        } else if (filters.size() > 1) {
          setFileFilter((FileFilter)filters.get(1));
        } else {
          setFileFilter(null);
        }
      }
      Object localObject = getChoosableFileFilters();
      filters.removeElement(paramFileFilter);
      firePropertyChange("ChoosableFileFilterChangedProperty", localObject, getChoosableFileFilters());
      return true;
    }
    return false;
  }
  
  public void resetChoosableFileFilters()
  {
    FileFilter[] arrayOfFileFilter = getChoosableFileFilters();
    setFileFilter(null);
    filters.removeAllElements();
    if (isAcceptAllFileFilterUsed()) {
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
    firePropertyChange("ChoosableFileFilterChangedProperty", arrayOfFileFilter, getChoosableFileFilters());
  }
  
  public FileFilter getAcceptAllFileFilter()
  {
    FileFilter localFileFilter = null;
    if (getUI() != null) {
      localFileFilter = getUI().getAcceptAllFileFilter(this);
    }
    return localFileFilter;
  }
  
  public boolean isAcceptAllFileFilterUsed()
  {
    return useAcceptAllFileFilter;
  }
  
  public void setAcceptAllFileFilterUsed(boolean paramBoolean)
  {
    boolean bool = useAcceptAllFileFilter;
    useAcceptAllFileFilter = paramBoolean;
    if (!paramBoolean)
    {
      removeChoosableFileFilter(getAcceptAllFileFilter());
    }
    else
    {
      removeChoosableFileFilter(getAcceptAllFileFilter());
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
    firePropertyChange("acceptAllFileFilterUsedChanged", bool, useAcceptAllFileFilter);
  }
  
  public JComponent getAccessory()
  {
    return accessory;
  }
  
  public void setAccessory(JComponent paramJComponent)
  {
    JComponent localJComponent = accessory;
    accessory = paramJComponent;
    firePropertyChange("AccessoryChangedProperty", localJComponent, accessory);
  }
  
  public void setFileSelectionMode(int paramInt)
  {
    if (fileSelectionMode == paramInt) {
      return;
    }
    if ((paramInt == 0) || (paramInt == 1) || (paramInt == 2))
    {
      int i = fileSelectionMode;
      fileSelectionMode = paramInt;
      firePropertyChange("fileSelectionChanged", i, fileSelectionMode);
    }
    else
    {
      throw new IllegalArgumentException("Incorrect Mode for file selection: " + paramInt);
    }
  }
  
  public int getFileSelectionMode()
  {
    return fileSelectionMode;
  }
  
  public boolean isFileSelectionEnabled()
  {
    return (fileSelectionMode == 0) || (fileSelectionMode == 2);
  }
  
  public boolean isDirectorySelectionEnabled()
  {
    return (fileSelectionMode == 1) || (fileSelectionMode == 2);
  }
  
  public void setMultiSelectionEnabled(boolean paramBoolean)
  {
    if (multiSelectionEnabled == paramBoolean) {
      return;
    }
    boolean bool = multiSelectionEnabled;
    multiSelectionEnabled = paramBoolean;
    firePropertyChange("MultiSelectionEnabledChangedProperty", bool, multiSelectionEnabled);
  }
  
  public boolean isMultiSelectionEnabled()
  {
    return multiSelectionEnabled;
  }
  
  public boolean isFileHidingEnabled()
  {
    return useFileHiding;
  }
  
  public void setFileHidingEnabled(boolean paramBoolean)
  {
    if (showFilesListener != null)
    {
      Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", showFilesListener);
      showFilesListener = null;
    }
    boolean bool = useFileHiding;
    useFileHiding = paramBoolean;
    firePropertyChange("FileHidingChanged", bool, useFileHiding);
  }
  
  public void setFileFilter(FileFilter paramFileFilter)
  {
    FileFilter localFileFilter = fileFilter;
    fileFilter = paramFileFilter;
    if (paramFileFilter != null) {
      if ((isMultiSelectionEnabled()) && (selectedFiles != null) && (selectedFiles.length > 0))
      {
        Vector localVector = new Vector();
        int i = 0;
        for (File localFile : selectedFiles) {
          if (paramFileFilter.accept(localFile)) {
            localVector.add(localFile);
          } else {
            i = 1;
          }
        }
        if (i != 0) {
          setSelectedFiles(localVector.size() == 0 ? null : (File[])localVector.toArray(new File[localVector.size()]));
        }
      }
      else if ((selectedFile != null) && (!paramFileFilter.accept(selectedFile)))
      {
        setSelectedFile(null);
      }
    }
    firePropertyChange("fileFilterChanged", localFileFilter, fileFilter);
  }
  
  public FileFilter getFileFilter()
  {
    return fileFilter;
  }
  
  public void setFileView(FileView paramFileView)
  {
    FileView localFileView = fileView;
    fileView = paramFileView;
    firePropertyChange("fileViewChanged", localFileView, paramFileView);
  }
  
  public FileView getFileView()
  {
    return fileView;
  }
  
  public String getName(File paramFile)
  {
    String str = null;
    if (paramFile != null)
    {
      if (getFileView() != null) {
        str = getFileView().getName(paramFile);
      }
      FileView localFileView = getUI().getFileView(this);
      if ((str == null) && (localFileView != null)) {
        str = localFileView.getName(paramFile);
      }
    }
    return str;
  }
  
  public String getDescription(File paramFile)
  {
    String str = null;
    if (paramFile != null)
    {
      if (getFileView() != null) {
        str = getFileView().getDescription(paramFile);
      }
      FileView localFileView = getUI().getFileView(this);
      if ((str == null) && (localFileView != null)) {
        str = localFileView.getDescription(paramFile);
      }
    }
    return str;
  }
  
  public String getTypeDescription(File paramFile)
  {
    String str = null;
    if (paramFile != null)
    {
      if (getFileView() != null) {
        str = getFileView().getTypeDescription(paramFile);
      }
      FileView localFileView = getUI().getFileView(this);
      if ((str == null) && (localFileView != null)) {
        str = localFileView.getTypeDescription(paramFile);
      }
    }
    return str;
  }
  
  public Icon getIcon(File paramFile)
  {
    Icon localIcon = null;
    if (paramFile != null)
    {
      if (getFileView() != null) {
        localIcon = getFileView().getIcon(paramFile);
      }
      FileView localFileView = getUI().getFileView(this);
      if ((localIcon == null) && (localFileView != null)) {
        localIcon = localFileView.getIcon(paramFile);
      }
    }
    return localIcon;
  }
  
  public boolean isTraversable(File paramFile)
  {
    Boolean localBoolean = null;
    if (paramFile != null)
    {
      if (getFileView() != null) {
        localBoolean = getFileView().isTraversable(paramFile);
      }
      FileView localFileView = getUI().getFileView(this);
      if ((localBoolean == null) && (localFileView != null)) {
        localBoolean = localFileView.isTraversable(paramFile);
      }
      if (localBoolean == null) {
        localBoolean = getFileSystemView().isTraversable(paramFile);
      }
    }
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public boolean accept(File paramFile)
  {
    boolean bool = true;
    if ((paramFile != null) && (fileFilter != null)) {
      bool = fileFilter.accept(paramFile);
    }
    return bool;
  }
  
  public void setFileSystemView(FileSystemView paramFileSystemView)
  {
    FileSystemView localFileSystemView = fileSystemView;
    fileSystemView = paramFileSystemView;
    firePropertyChange("FileSystemViewChanged", localFileSystemView, fileSystemView);
  }
  
  public FileSystemView getFileSystemView()
  {
    return fileSystemView;
  }
  
  public void approveSelection()
  {
    returnValue = 0;
    if (dialog != null) {
      dialog.setVisible(false);
    }
    fireActionPerformed("ApproveSelection");
  }
  
  public void cancelSelection()
  {
    returnValue = 1;
    if (dialog != null) {
      dialog.setVisible(false);
    }
    fireActionPerformed("CancelSelection");
  }
  
  public void addActionListener(ActionListener paramActionListener)
  {
    listenerList.add(ActionListener.class, paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener)
  {
    listenerList.remove(ActionListener.class, paramActionListener);
  }
  
  public ActionListener[] getActionListeners()
  {
    return (ActionListener[])listenerList.getListeners(ActionListener.class);
  }
  
  protected void fireActionPerformed(String paramString)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    long l = EventQueue.getMostRecentEventTime();
    int i = 0;
    AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
    if ((localAWTEvent instanceof InputEvent)) {
      i = ((InputEvent)localAWTEvent).getModifiers();
    } else if ((localAWTEvent instanceof ActionEvent)) {
      i = ((ActionEvent)localAWTEvent).getModifiers();
    }
    ActionEvent localActionEvent = null;
    for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
      if (arrayOfObject[j] == ActionListener.class)
      {
        if (localActionEvent == null) {
          localActionEvent = new ActionEvent(this, 1001, paramString, l, i);
        }
        ((ActionListener)arrayOfObject[(j + 1)]).actionPerformed(localActionEvent);
      }
    }
  }
  
  public void updateUI()
  {
    if (isAcceptAllFileFilterUsed()) {
      removeChoosableFileFilter(getAcceptAllFileFilter());
    }
    FileChooserUI localFileChooserUI = (FileChooserUI)UIManager.getUI(this);
    if (fileSystemView == null) {
      setFileSystemView(FileSystemView.getFileSystemView());
    }
    setUI(localFileChooserUI);
    if (isAcceptAllFileFilterUsed()) {
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
  }
  
  public String getUIClassID()
  {
    return "FileChooserUI";
  }
  
  public FileChooserUI getUI()
  {
    return (FileChooserUI)ui;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    installShowFilesListener();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    FileSystemView localFileSystemView = null;
    if (isAcceptAllFileFilterUsed()) {
      removeChoosableFileFilter(getAcceptAllFileFilter());
    }
    if (fileSystemView.equals(FileSystemView.getFileSystemView()))
    {
      localFileSystemView = fileSystemView;
      fileSystemView = null;
    }
    paramObjectOutputStream.defaultWriteObject();
    if (localFileSystemView != null) {
      fileSystemView = localFileSystemView;
    }
    if (isAcceptAllFileFilterUsed()) {
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
    if (getUIClassID().equals("FileChooserUI"))
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
    String str1 = approveButtonText != null ? approveButtonText : "";
    String str2 = dialogTitle != null ? dialogTitle : "";
    String str3;
    if (dialogType == 0) {
      str3 = "OPEN_DIALOG";
    } else if (dialogType == 1) {
      str3 = "SAVE_DIALOG";
    } else if (dialogType == 2) {
      str3 = "CUSTOM_DIALOG";
    } else {
      str3 = "";
    }
    String str4;
    if (returnValue == 1) {
      str4 = "CANCEL_OPTION";
    } else if (returnValue == 0) {
      str4 = "APPROVE_OPTION";
    } else if (returnValue == -1) {
      str4 = "ERROR_OPTION";
    } else {
      str4 = "";
    }
    String str5 = useFileHiding ? "true" : "false";
    String str6;
    if (fileSelectionMode == 0) {
      str6 = "FILES_ONLY";
    } else if (fileSelectionMode == 1) {
      str6 = "DIRECTORIES_ONLY";
    } else if (fileSelectionMode == 2) {
      str6 = "FILES_AND_DIRECTORIES";
    } else {
      str6 = "";
    }
    String str7 = currentDirectory != null ? currentDirectory.toString() : "";
    String str8 = selectedFile != null ? selectedFile.toString() : "";
    return super.paramString() + ",approveButtonText=" + str1 + ",currentDirectory=" + str7 + ",dialogTitle=" + str2 + ",dialogType=" + str3 + ",fileSelectionMode=" + str6 + ",returnValue=" + str4 + ",selectedFile=" + str8 + ",useFileHiding=" + str5;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJFileChooser();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJFileChooser
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJFileChooser()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.FILE_CHOOSER;
    }
  }
  
  private static class WeakPCL
    implements PropertyChangeListener
  {
    WeakReference<JFileChooser> jfcRef;
    
    public WeakPCL(JFileChooser paramJFileChooser)
    {
      jfcRef = new WeakReference(paramJFileChooser);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      assert (paramPropertyChangeEvent.getPropertyName().equals("awt.file.showHiddenFiles"));
      JFileChooser localJFileChooser = (JFileChooser)jfcRef.get();
      if (localJFileChooser == null)
      {
        Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this);
      }
      else
      {
        boolean bool = useFileHiding;
        useFileHiding = (!((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
        localJFileChooser.firePropertyChange("FileHidingChanged", bool, useFileHiding);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JFileChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */