package com.sun.java.swing.plaf.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI.BasicFileView;
import javax.swing.plaf.basic.BasicFileChooserUI.NewFolderAction;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.FilePane.FileChooserUIAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.WindowsPlacesBar;

public class WindowsFileChooserUI
  extends BasicFileChooserUI
{
  private JPanel centerPanel;
  private JLabel lookInLabel;
  private JComboBox<File> directoryComboBox;
  private DirectoryComboBoxModel directoryComboBoxModel;
  private ActionListener directoryComboBoxAction = new DirectoryComboBoxAction();
  private FilterComboBoxModel filterComboBoxModel;
  private JTextField filenameTextField;
  private FilePane filePane;
  private WindowsPlacesBar placesBar;
  private JButton approveButton;
  private JButton cancelButton;
  private JPanel buttonPanel;
  private JPanel bottomPanel;
  private JComboBox<FileFilter> filterComboBox;
  private static final Dimension hstrut10 = new Dimension(10, 1);
  private static final Dimension vstrut4 = new Dimension(1, 4);
  private static final Dimension vstrut6 = new Dimension(1, 6);
  private static final Dimension vstrut8 = new Dimension(1, 8);
  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
  private static int PREF_WIDTH = 425;
  private static int PREF_HEIGHT = 245;
  private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
  private static int MIN_WIDTH = 425;
  private static int MIN_HEIGHT = 245;
  private static int LIST_PREF_WIDTH = 444;
  private static int LIST_PREF_HEIGHT = 138;
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
  private String newFolderToolTipText = null;
  private String newFolderAccessibleName = null;
  private String viewMenuButtonToolTipText = null;
  private String viewMenuButtonAccessibleName = null;
  private BasicFileChooserUI.BasicFileView fileView = new WindowsFileView();
  private JLabel fileNameLabel;
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
    return new WindowsFileChooserUI((JFileChooser)paramJComponent);
  }
  
  public WindowsFileChooserUI(JFileChooser paramJFileChooser)
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
  }
  
  public void installComponents(JFileChooser paramJFileChooser)
  {
    filePane = new FilePane(new WindowsFileChooserUIAccessor(null));
    paramJFileChooser.addPropertyChangeListener(filePane);
    FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
    paramJFileChooser.setBorder(new EmptyBorder(4, 10, 10, 10));
    paramJFileChooser.setLayout(new BorderLayout(8, 8));
    updateUseShellFolder();
    JToolBar localJToolBar = new JToolBar();
    localJToolBar.setFloatable(false);
    localJToolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    paramJFileChooser.add(localJToolBar, "North");
    lookInLabel = new JLabel(lookInLabelText, 11)
    {
      public Dimension getPreferredSize()
      {
        return getMinimumSize();
      }
      
      public Dimension getMinimumSize()
      {
        Dimension localDimension = super.getPreferredSize();
        if (placesBar != null) {
          width = Math.max(width, placesBar.getWidth());
        }
        return localDimension;
      }
    };
    lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
    lookInLabel.setAlignmentX(0.0F);
    lookInLabel.setAlignmentY(0.5F);
    localJToolBar.add(lookInLabel);
    localJToolBar.add(Box.createRigidArea(new Dimension(8, 0)));
    directoryComboBox = new JComboBox()
    {
      public Dimension getMinimumSize()
      {
        Dimension localDimension = super.getMinimumSize();
        width = 60;
        return localDimension;
      }
      
      public Dimension getPreferredSize()
      {
        Dimension localDimension = super.getPreferredSize();
        width = 150;
        return localDimension;
      }
    };
    directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
    lookInLabel.setLabelFor(directoryComboBox);
    directoryComboBoxModel = createDirectoryComboBoxModel(paramJFileChooser);
    directoryComboBox.setModel(directoryComboBoxModel);
    directoryComboBox.addActionListener(directoryComboBoxAction);
    directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(paramJFileChooser));
    directoryComboBox.setAlignmentX(0.0F);
    directoryComboBox.setAlignmentY(0.5F);
    directoryComboBox.setMaximumRowCount(8);
    localJToolBar.add(directoryComboBox);
    localJToolBar.add(Box.createRigidArea(hstrut10));
    JButton localJButton1 = createToolButton(getChangeToParentDirectoryAction(), upFolderIcon, upFolderToolTipText, upFolderAccessibleName);
    localJToolBar.add(localJButton1);
    if (!UIManager.getBoolean("FileChooser.readOnly"))
    {
      localObject1 = createToolButton(filePane.getNewFolderAction(), newFolderIcon, newFolderToolTipText, newFolderAccessibleName);
      localJToolBar.add((Component)localObject1);
    }
    Object localObject1 = new ButtonGroup();
    final JPopupMenu localJPopupMenu = new JPopupMenu();
    final JRadioButtonMenuItem localJRadioButtonMenuItem1 = new JRadioButtonMenuItem(filePane.getViewTypeAction(0));
    localJRadioButtonMenuItem1.setSelected(filePane.getViewType() == 0);
    localJPopupMenu.add(localJRadioButtonMenuItem1);
    ((ButtonGroup)localObject1).add(localJRadioButtonMenuItem1);
    final JRadioButtonMenuItem localJRadioButtonMenuItem2 = new JRadioButtonMenuItem(filePane.getViewTypeAction(1));
    localJRadioButtonMenuItem2.setSelected(filePane.getViewType() == 1);
    localJPopupMenu.add(localJRadioButtonMenuItem2);
    ((ButtonGroup)localObject1).add(localJRadioButtonMenuItem2);
    BufferedImage localBufferedImage = new BufferedImage(viewMenuIcon.getIconWidth() + 7, viewMenuIcon.getIconHeight(), 2);
    Graphics localGraphics = localBufferedImage.getGraphics();
    viewMenuIcon.paintIcon(filePane, localGraphics, 0, 0);
    int i = localBufferedImage.getWidth() - 5;
    int j = localBufferedImage.getHeight() / 2 - 1;
    localGraphics.setColor(Color.BLACK);
    localGraphics.fillPolygon(new int[] { i, i + 5, i + 2 }, new int[] { j, j, j + 3 }, 3);
    final JButton localJButton2 = createToolButton(null, new ImageIcon(localBufferedImage), viewMenuButtonToolTipText, viewMenuButtonAccessibleName);
    localJButton2.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent paramAnonymousMouseEvent)
      {
        if ((SwingUtilities.isLeftMouseButton(paramAnonymousMouseEvent)) && (!localJButton2.isSelected()))
        {
          localJButton2.setSelected(true);
          localJPopupMenu.show(localJButton2, 0, localJButton2.getHeight());
        }
      }
    });
    localJButton2.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousKeyEvent.getKeyCode() == 32) && (localJButton2.getModel().isRollover()))
        {
          localJButton2.setSelected(true);
          localJPopupMenu.show(localJButton2, 0, localJButton2.getHeight());
        }
      }
    });
    localJPopupMenu.addPopupMenuListener(new PopupMenuListener()
    {
      public void popupMenuWillBecomeVisible(PopupMenuEvent paramAnonymousPopupMenuEvent) {}
      
      public void popupMenuWillBecomeInvisible(PopupMenuEvent paramAnonymousPopupMenuEvent)
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            val$viewMenuButton.setSelected(false);
          }
        });
      }
      
      public void popupMenuCanceled(PopupMenuEvent paramAnonymousPopupMenuEvent) {}
    });
    localJToolBar.add(localJButton2);
    localJToolBar.add(Box.createRigidArea(new Dimension(80, 0)));
    filePane.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        if ("viewType".equals(paramAnonymousPropertyChangeEvent.getPropertyName())) {
          switch (filePane.getViewType())
          {
          case 0: 
            localJRadioButtonMenuItem1.setSelected(true);
            break;
          case 1: 
            localJRadioButtonMenuItem2.setSelected(true);
          }
        }
      }
    });
    centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(getAccessoryPanel(), "After");
    JComponent localJComponent = paramJFileChooser.getAccessory();
    if (localJComponent != null) {
      getAccessoryPanel().add(localJComponent);
    }
    filePane.setPreferredSize(LIST_PREF_SIZE);
    centerPanel.add(filePane, "Center");
    paramJFileChooser.add(centerPanel, "Center");
    getBottomPanel().setLayout(new BoxLayout(getBottomPanel(), 2));
    centerPanel.add(getBottomPanel(), "South");
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setLayout(new BoxLayout(localJPanel1, 3));
    localJPanel1.add(Box.createRigidArea(vstrut4));
    fileNameLabel = new JLabel();
    populateFileNameLabel();
    fileNameLabel.setAlignmentY(0.0F);
    localJPanel1.add(fileNameLabel);
    localJPanel1.add(Box.createRigidArea(new Dimension(1, 12)));
    JLabel localJLabel = new JLabel(filesOfTypeLabelText);
    localJLabel.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
    localJPanel1.add(localJLabel);
    getBottomPanel().add(localJPanel1);
    getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));
    JPanel localJPanel2 = new JPanel();
    localJPanel2.add(Box.createRigidArea(vstrut8));
    localJPanel2.setLayout(new BoxLayout(localJPanel2, 1));
    filenameTextField = new JTextField(35)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(32767, getPreferredSizeheight);
      }
    };
    fileNameLabel.setLabelFor(filenameTextField);
    filenameTextField.addFocusListener(new FocusAdapter()
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
    localJPanel2.add(filenameTextField);
    localJPanel2.add(Box.createRigidArea(vstrut8));
    filterComboBoxModel = createFilterComboBoxModel();
    paramJFileChooser.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    localJLabel.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    localJPanel2.add(filterComboBox);
    getBottomPanel().add(localJPanel2);
    getBottomPanel().add(Box.createRigidArea(new Dimension(30, 0)));
    getButtonPanel().setLayout(new BoxLayout(getButtonPanel(), 1));
    approveButton = new JButton(getApproveButtonText(paramJFileChooser))
    {
      public Dimension getMaximumSize()
      {
        return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ? approveButton.getPreferredSize() : cancelButton.getPreferredSize();
      }
    };
    Object localObject2 = approveButton.getMargin();
    localObject2 = new InsetsUIResource(top, left + 5, bottom, right + 5);
    approveButton.setMargin((Insets)localObject2);
    approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
    approveButton.addActionListener(getApproveSelectionAction());
    approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    getButtonPanel().add(Box.createRigidArea(vstrut6));
    getButtonPanel().add(approveButton);
    getButtonPanel().add(Box.createRigidArea(vstrut4));
    cancelButton = new JButton(cancelButtonText)
    {
      public Dimension getMaximumSize()
      {
        return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ? approveButton.getPreferredSize() : cancelButton.getPreferredSize();
      }
    };
    cancelButton.setMargin((Insets)localObject2);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());
    getButtonPanel().add(cancelButton);
    if (paramJFileChooser.getControlButtonsAreShown()) {
      addControlButtons();
    }
  }
  
  private void updateUseShellFolder()
  {
    JFileChooser localJFileChooser = getFileChooser();
    if (FilePane.usesShellFolder(localJFileChooser))
    {
      if ((placesBar == null) && (!UIManager.getBoolean("FileChooser.noPlacesBar")))
      {
        placesBar = new WindowsPlacesBar(localJFileChooser, XPStyle.getXP() != null);
        localJFileChooser.add(placesBar, "Before");
        localJFileChooser.addPropertyChangeListener(placesBar);
      }
    }
    else if (placesBar != null)
    {
      localJFileChooser.remove(placesBar);
      localJFileChooser.removePropertyChangeListener(placesBar);
      placesBar = null;
    }
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
    newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", localLocale);
    newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", localLocale);
    viewMenuButtonToolTipText = UIManager.getString("FileChooser.viewMenuButtonToolTipText", localLocale);
    viewMenuButtonAccessibleName = UIManager.getString("FileChooser.viewMenuButtonAccessibleName", localLocale);
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
    if (placesBar != null) {
      paramJComponent.removePropertyChangeListener(placesBar);
    }
    cancelButton.removeActionListener(getCancelSelectionAction());
    approveButton.removeActionListener(getApproveSelectionAction());
    filenameTextField.removeActionListener(getApproveSelectionAction());
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
    approveButton.setMnemonic(getApproveButtonMnemonic(localJFileChooser));
  }
  
  private void doDialogTypeChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JFileChooser localJFileChooser = getFileChooser();
    approveButton.setText(getApproveButtonText(localJFileChooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
    approveButton.setMnemonic(getApproveButtonMnemonic(localJFileChooser));
    if (localJFileChooser.getDialogType() == 1) {
      lookInLabel.setText(saveInLabelText);
    } else {
      lookInLabel.setText(lookInLabelText);
    }
  }
  
  private void doApproveButtonMnemonicChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
  }
  
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
          WindowsFileChooserUI.this.doSelectedFileChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("SelectedFilesChangedProperty"))
        {
          WindowsFileChooserUI.this.doSelectedFilesChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("directoryChanged"))
        {
          WindowsFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("fileFilterChanged"))
        {
          WindowsFileChooserUI.this.doFilterChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("fileSelectionChanged"))
        {
          WindowsFileChooserUI.this.doFileSelectionModeChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("AccessoryChangedProperty"))
        {
          WindowsFileChooserUI.this.doAccessoryChanged(paramAnonymousPropertyChangeEvent);
        }
        else if ((str.equals("ApproveButtonTextChangedProperty")) || (str.equals("ApproveButtonToolTipTextChangedProperty")))
        {
          WindowsFileChooserUI.this.doApproveButtonTextChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("DialogTypeChangedProperty"))
        {
          WindowsFileChooserUI.this.doDialogTypeChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("ApproveButtonMnemonicChangedProperty"))
        {
          WindowsFileChooserUI.this.doApproveButtonMnemonicChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("ControlButtonsAreShownChangedProperty"))
        {
          WindowsFileChooserUI.this.doControlButtonsChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str == "FileChooser.useShellFolder")
        {
          WindowsFileChooserUI.this.updateUseShellFolder();
          WindowsFileChooserUI.this.doDirectoryChanged(paramAnonymousPropertyChangeEvent);
        }
        else if (str.equals("componentOrientation"))
        {
          ComponentOrientation localComponentOrientation = (ComponentOrientation)paramAnonymousPropertyChangeEvent.getNewValue();
          JFileChooser localJFileChooser = (JFileChooser)paramAnonymousPropertyChangeEvent.getSource();
          if (localComponentOrientation != paramAnonymousPropertyChangeEvent.getOldValue()) {
            localJFileChooser.applyComponentOrientation(localComponentOrientation);
          }
        }
        else if ((str.equals("ancestor")) && (paramAnonymousPropertyChangeEvent.getOldValue() == null) && (paramAnonymousPropertyChangeEvent.getNewValue() != null))
        {
          filenameTextField.selectAll();
          filenameTextField.requestFocus();
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
    if (filenameTextField != null) {
      return filenameTextField.getText();
    }
    return null;
  }
  
  public void setFileName(String paramString)
  {
    if (filenameTextField != null) {
      filenameTextField.setText(paramString);
    }
  }
  
  protected void setDirectorySelected(boolean paramBoolean)
  {
    super.setDirectorySelected(paramBoolean);
    JFileChooser localJFileChooser = getFileChooser();
    if (paramBoolean)
    {
      approveButton.setText(directoryOpenButtonText);
      approveButton.setToolTipText(directoryOpenButtonToolTipText);
      approveButton.setMnemonic(directoryOpenButtonMnemonic);
    }
    else
    {
      approveButton.setText(getApproveButtonText(localJFileChooser));
      approveButton.setToolTipText(getApproveButtonToolTipText(localJFileChooser));
      approveButton.setMnemonic(getApproveButtonMnemonic(localJFileChooser));
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
  
  private static JButton createToolButton(Action paramAction, Icon paramIcon, String paramString1, String paramString2)
  {
    JButton localJButton = new JButton(paramAction);
    localJButton.setText(null);
    localJButton.setIcon(paramIcon);
    localJButton.setToolTipText(paramString1);
    localJButton.setRequestFocusEnabled(false);
    localJButton.putClientProperty("AccessibleName", paramString2);
    localJButton.putClientProperty(WindowsLookAndFeel.HI_RES_DISABLED_ICON_CLIENT_KEY, Boolean.TRUE);
    localJButton.setAlignmentX(0.0F);
    localJButton.setAlignmentY(0.5F);
    localJButton.setMargin(shrinkwrap);
    localJButton.setFocusPainted(false);
    localJButton.setModel(new DefaultButtonModel()
    {
      public void setPressed(boolean paramAnonymousBoolean)
      {
        if ((!paramAnonymousBoolean) || (isRollover())) {
          super.setPressed(paramAnonymousBoolean);
        }
      }
      
      public void setRollover(boolean paramAnonymousBoolean)
      {
        if ((paramAnonymousBoolean) && (!isRollover())) {
          for (Component localComponent : val$result.getParent().getComponents()) {
            if (((localComponent instanceof JButton)) && (localComponent != val$result)) {
              ((JButton)localComponent).getModel().setRollover(false);
            }
          }
        }
        super.setRollover(paramAnonymousBoolean);
      }
      
      public void setSelected(boolean paramAnonymousBoolean)
      {
        super.setSelected(paramAnonymousBoolean);
        if (paramAnonymousBoolean) {
          stateMask |= 0x5;
        } else {
          stateMask &= 0xFFFFFFFA;
        }
      }
    });
    localJButton.addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        val$result.getModel().setRollover(true);
      }
      
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        val$result.getModel().setRollover(false);
      }
    });
    return localJButton;
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
  
  public FileView getFileView(JFileChooser paramJFileChooser)
  {
    return fileView;
  }
  
  protected class DirectoryComboBoxAction
    implements ActionListener
  {
    protected DirectoryComboBoxAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      File localFile = (File)directoryComboBox.getSelectedItem();
      getFileChooser().setCurrentDirectory(localFile);
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
        localFile1 = paramFile.getCanonicalFile();
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
    
    public File getElementAt(int paramInt)
    {
      return (File)directories.elementAt(paramInt);
    }
  }
  
  class DirectoryComboBoxRenderer
    extends DefaultListCellRenderer
  {
    WindowsFileChooserUI.IndentIcon ii = new WindowsFileChooserUI.IndentIcon(WindowsFileChooserUI.this);
    
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
  
  protected class SingleClickListener
    extends MouseAdapter
  {
    protected SingleClickListener() {}
  }
  
  private class WindowsFileChooserUIAccessor
    implements FilePane.FileChooserUIAccessor
  {
    private WindowsFileChooserUIAccessor() {}
    
    public JFileChooser getFileChooser()
    {
      return WindowsFileChooserUI.this.getFileChooser();
    }
    
    public BasicDirectoryModel getModel()
    {
      return WindowsFileChooserUI.this.getModel();
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
      return WindowsFileChooserUI.this.isDirectorySelected();
    }
    
    public File getDirectory()
    {
      return WindowsFileChooserUI.this.getDirectory();
    }
    
    public Action getChangeToParentDirectoryAction()
    {
      return WindowsFileChooserUI.this.getChangeToParentDirectoryAction();
    }
    
    public Action getApproveSelectionAction()
    {
      return WindowsFileChooserUI.this.getApproveSelectionAction();
    }
    
    public Action getNewFolderAction()
    {
      return WindowsFileChooserUI.this.getNewFolderAction();
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
  
  protected class WindowsFileView
    extends BasicFileChooserUI.BasicFileView
  {
    protected WindowsFileView()
    {
      super();
    }
    
    public Icon getIcon(File paramFile)
    {
      Icon localIcon = getCachedIcon(paramFile);
      if (localIcon != null) {
        return localIcon;
      }
      if (paramFile != null) {
        localIcon = getFileChooser().getFileSystemView().getSystemIcon(paramFile);
      }
      if (localIcon == null) {
        localIcon = super.getIcon(paramFile);
      }
      cacheIcon(paramFile, localIcon);
      return localIcon;
    }
  }
  
  protected class WindowsNewFolderAction
    extends BasicFileChooserUI.NewFolderAction
  {
    protected WindowsNewFolderAction()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */