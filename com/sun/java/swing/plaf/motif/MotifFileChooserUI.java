package com.sun.java.swing.plaf.motif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.SwingUtilities2;

public class MotifFileChooserUI
  extends BasicFileChooserUI
{
  private FilterComboBoxModel filterComboBoxModel;
  protected JList<File> directoryList = null;
  protected JList<File> fileList = null;
  protected JTextField pathField = null;
  protected JComboBox<FileFilter> filterComboBox = null;
  protected JTextField filenameTextField = null;
  private static final Dimension hstrut10 = new Dimension(10, 1);
  private static final Dimension vstrut10 = new Dimension(1, 10);
  private static final Insets insets = new Insets(10, 10, 10, 10);
  private static Dimension prefListSize = new Dimension(75, 150);
  private static Dimension WITH_ACCELERATOR_PREF_SIZE = new Dimension(650, 450);
  private static Dimension PREF_SIZE = new Dimension(350, 450);
  private static final int MIN_WIDTH = 200;
  private static final int MIN_HEIGHT = 300;
  private static Dimension PREF_ACC_SIZE = new Dimension(10, 10);
  private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);
  private static Dimension MAX_SIZE = new Dimension(32767, 32767);
  private static final Insets buttonMargin = new Insets(3, 3, 3, 3);
  private JPanel bottomPanel;
  protected JButton approveButton;
  private String enterFolderNameLabelText = null;
  private int enterFolderNameLabelMnemonic = 0;
  private String enterFileNameLabelText = null;
  private int enterFileNameLabelMnemonic = 0;
  private String filesLabelText = null;
  private int filesLabelMnemonic = 0;
  private String foldersLabelText = null;
  private int foldersLabelMnemonic = 0;
  private String pathLabelText = null;
  private int pathLabelMnemonic = 0;
  private String filterLabelText = null;
  private int filterLabelMnemonic = 0;
  private JLabel fileNameLabel;
  
  private void populateFileNameLabel()
  {
    if (getFileChooser().getFileSelectionMode() == 1)
    {
      fileNameLabel.setText(enterFolderNameLabelText);
      fileNameLabel.setDisplayedMnemonic(enterFolderNameLabelMnemonic);
    }
    else
    {
      fileNameLabel.setText(enterFileNameLabelText);
      fileNameLabel.setDisplayedMnemonic(enterFileNameLabelMnemonic);
    }
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
  
  public MotifFileChooserUI(JFileChooser paramJFileChooser)
  {
    super(paramJFileChooser);
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
  
  public String getDirectoryName()
  {
    return pathField.getText();
  }
  
  public void setDirectoryName(String paramString)
  {
    pathField.setText(paramString);
  }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {}
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser)
  {
    getModel().validateFileCache();
  }
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser)
  {
    new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        String str = paramAnonymousPropertyChangeEvent.getPropertyName();
        Object localObject1;
        if (str.equals("SelectedFileChangedProperty"))
        {
          localObject1 = (File)paramAnonymousPropertyChangeEvent.getNewValue();
          if (localObject1 != null) {
            setFileName(getFileChooser().getName((File)localObject1));
          }
        }
        else
        {
          Object localObject2;
          if (str.equals("SelectedFilesChangedProperty"))
          {
            localObject1 = (File[])paramAnonymousPropertyChangeEvent.getNewValue();
            localObject2 = getFileChooser();
            if ((localObject1 != null) && (localObject1.length > 0) && ((localObject1.length > 1) || (((JFileChooser)localObject2).isDirectorySelectionEnabled()) || (!localObject1[0].isDirectory()))) {
              setFileName(MotifFileChooserUI.this.fileNameString((File[])localObject1));
            }
          }
          else if (str.equals("fileFilterChanged"))
          {
            fileList.clearSelection();
          }
          else if (str.equals("directoryChanged"))
          {
            directoryList.clearSelection();
            localObject1 = directoryList.getSelectionModel();
            if ((localObject1 instanceof DefaultListSelectionModel))
            {
              ((DefaultListSelectionModel)localObject1).moveLeadSelectionIndex(0);
              ((ListSelectionModel)localObject1).setAnchorSelectionIndex(0);
            }
            fileList.clearSelection();
            localObject1 = fileList.getSelectionModel();
            if ((localObject1 instanceof DefaultListSelectionModel))
            {
              ((DefaultListSelectionModel)localObject1).moveLeadSelectionIndex(0);
              ((ListSelectionModel)localObject1).setAnchorSelectionIndex(0);
            }
            localObject2 = getFileChooser().getCurrentDirectory();
            if (localObject2 != null)
            {
              try
              {
                setDirectoryName(ShellFolder.getNormalizedFile((File)paramAnonymousPropertyChangeEvent.getNewValue()).getPath());
              }
              catch (IOException localIOException)
              {
                setDirectoryName(((File)paramAnonymousPropertyChangeEvent.getNewValue()).getAbsolutePath());
              }
              if ((getFileChooser().getFileSelectionMode() == 1) && (!getFileChooser().isMultiSelectionEnabled())) {
                setFileName(getDirectoryName());
              }
            }
          }
          else if (str.equals("fileSelectionChanged"))
          {
            if (fileNameLabel != null) {
              MotifFileChooserUI.this.populateFileNameLabel();
            }
            directoryList.clearSelection();
          }
          else if (str.equals("MultiSelectionEnabledChangedProperty"))
          {
            if (getFileChooser().isMultiSelectionEnabled())
            {
              fileList.setSelectionMode(2);
            }
            else
            {
              fileList.setSelectionMode(0);
              fileList.clearSelection();
              getFileChooser().setSelectedFiles(null);
            }
          }
          else if (str.equals("AccessoryChangedProperty"))
          {
            if (getAccessoryPanel() != null)
            {
              if (paramAnonymousPropertyChangeEvent.getOldValue() != null) {
                getAccessoryPanel().remove((JComponent)paramAnonymousPropertyChangeEvent.getOldValue());
              }
              localObject1 = (JComponent)paramAnonymousPropertyChangeEvent.getNewValue();
              if (localObject1 != null)
              {
                getAccessoryPanel().add((Component)localObject1, "Center");
                getAccessoryPanel().setPreferredSize(MotifFileChooserUI.PREF_ACC_SIZE);
                getAccessoryPanel().setMaximumSize(MotifFileChooserUI.MAX_SIZE);
              }
              else
              {
                getAccessoryPanel().setPreferredSize(MotifFileChooserUI.ZERO_ACC_SIZE);
                getAccessoryPanel().setMaximumSize(MotifFileChooserUI.ZERO_ACC_SIZE);
              }
            }
          }
          else if ((str.equals("ApproveButtonTextChangedProperty")) || (str.equals("ApproveButtonToolTipTextChangedProperty")) || (str.equals("DialogTypeChangedProperty")))
          {
            approveButton.setText(getApproveButtonText(getFileChooser()));
            approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
          }
          else if (str.equals("ControlButtonsAreShownChangedProperty"))
          {
            MotifFileChooserUI.this.doControlButtonsChanged(paramAnonymousPropertyChangeEvent);
          }
          else if (str.equals("componentOrientation"))
          {
            localObject1 = (ComponentOrientation)paramAnonymousPropertyChangeEvent.getNewValue();
            localObject2 = (JFileChooser)paramAnonymousPropertyChangeEvent.getSource();
            if (localObject1 != (ComponentOrientation)paramAnonymousPropertyChangeEvent.getOldValue()) {
              ((JFileChooser)localObject2).applyComponentOrientation((ComponentOrientation)localObject1);
            }
          }
        }
      }
    };
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifFileChooserUI((JFileChooser)paramJComponent);
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(filterComboBoxModel);
    approveButton.removeActionListener(getApproveSelectionAction());
    filenameTextField.removeActionListener(getApproveSelectionAction());
    super.uninstallUI(paramJComponent);
  }
  
  public void installComponents(JFileChooser paramJFileChooser)
  {
    paramJFileChooser.setLayout(new BorderLayout(10, 10));
    paramJFileChooser.setAlignmentX(0.5F);
    JPanel local2 = new JPanel()
    {
      public Insets getInsets()
      {
        return MotifFileChooserUI.insets;
      }
    };
    local2.setInheritsPopupMenu(true);
    align(local2);
    local2.setLayout(new BoxLayout(local2, 3));
    paramJFileChooser.add(local2, "Center");
    JLabel localJLabel = new JLabel(pathLabelText);
    localJLabel.setDisplayedMnemonic(pathLabelMnemonic);
    align(localJLabel);
    local2.add(localJLabel);
    File localFile = paramJFileChooser.getCurrentDirectory();
    String str = null;
    if (localFile != null) {
      str = localFile.getPath();
    }
    pathField = new JTextField(str)
    {
      public Dimension getMaximumSize()
      {
        Dimension localDimension = super.getMaximumSize();
        height = getPreferredSizeheight;
        return localDimension;
      }
    };
    pathField.setInheritsPopupMenu(true);
    localJLabel.setLabelFor(pathField);
    align(pathField);
    pathField.addActionListener(getUpdateAction());
    local2.add(pathField);
    local2.add(Box.createRigidArea(vstrut10));
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setLayout(new BoxLayout(localJPanel1, 2));
    align(localJPanel1);
    JPanel localJPanel2 = new JPanel();
    localJPanel2.setLayout(new BoxLayout(localJPanel2, 3));
    align(localJPanel2);
    localJLabel = new JLabel(filterLabelText);
    localJLabel.setDisplayedMnemonic(filterLabelMnemonic);
    align(localJLabel);
    localJPanel2.add(localJLabel);
    filterComboBox = new JComboBox()
    {
      public Dimension getMaximumSize()
      {
        Dimension localDimension = super.getMaximumSize();
        height = getPreferredSizeheight;
        return localDimension;
      }
    };
    filterComboBox.setInheritsPopupMenu(true);
    localJLabel.setLabelFor(filterComboBox);
    filterComboBoxModel = createFilterComboBoxModel();
    filterComboBox.setModel(filterComboBoxModel);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    paramJFileChooser.addPropertyChangeListener(filterComboBoxModel);
    align(filterComboBox);
    localJPanel2.add(filterComboBox);
    localJLabel = new JLabel(foldersLabelText);
    localJLabel.setDisplayedMnemonic(foldersLabelMnemonic);
    align(localJLabel);
    localJPanel2.add(localJLabel);
    JScrollPane localJScrollPane = createDirectoryList();
    localJScrollPane.getVerticalScrollBar().setFocusable(false);
    localJScrollPane.getHorizontalScrollBar().setFocusable(false);
    localJScrollPane.setInheritsPopupMenu(true);
    localJLabel.setLabelFor(localJScrollPane.getViewport().getView());
    localJPanel2.add(localJScrollPane);
    localJPanel2.setInheritsPopupMenu(true);
    JPanel localJPanel3 = new JPanel();
    align(localJPanel3);
    localJPanel3.setLayout(new BoxLayout(localJPanel3, 3));
    localJPanel3.setInheritsPopupMenu(true);
    localJLabel = new JLabel(filesLabelText);
    localJLabel.setDisplayedMnemonic(filesLabelMnemonic);
    align(localJLabel);
    localJPanel3.add(localJLabel);
    localJScrollPane = createFilesList();
    localJLabel.setLabelFor(localJScrollPane.getViewport().getView());
    localJPanel3.add(localJScrollPane);
    localJScrollPane.setInheritsPopupMenu(true);
    localJPanel1.add(localJPanel2);
    localJPanel1.add(Box.createRigidArea(hstrut10));
    localJPanel1.add(localJPanel3);
    localJPanel1.setInheritsPopupMenu(true);
    JPanel localJPanel4 = getAccessoryPanel();
    JComponent localJComponent = paramJFileChooser.getAccessory();
    if (localJPanel4 != null)
    {
      if (localJComponent == null)
      {
        localJPanel4.setPreferredSize(ZERO_ACC_SIZE);
        localJPanel4.setMaximumSize(ZERO_ACC_SIZE);
      }
      else
      {
        getAccessoryPanel().add(localJComponent, "Center");
        localJPanel4.setPreferredSize(PREF_ACC_SIZE);
        localJPanel4.setMaximumSize(MAX_SIZE);
      }
      align(localJPanel4);
      localJPanel1.add(localJPanel4);
      localJPanel4.setInheritsPopupMenu(true);
    }
    local2.add(localJPanel1);
    local2.add(Box.createRigidArea(vstrut10));
    fileNameLabel = new JLabel();
    populateFileNameLabel();
    align(fileNameLabel);
    local2.add(fileNameLabel);
    filenameTextField = new JTextField()
    {
      public Dimension getMaximumSize()
      {
        Dimension localDimension = super.getMaximumSize();
        height = getPreferredSizeheight;
        return localDimension;
      }
    };
    filenameTextField.setInheritsPopupMenu(true);
    fileNameLabel.setLabelFor(filenameTextField);
    filenameTextField.addActionListener(getApproveSelectionAction());
    align(filenameTextField);
    filenameTextField.setAlignmentX(0.0F);
    local2.add(filenameTextField);
    bottomPanel = getBottomPanel();
    bottomPanel.add(new JSeparator(), "North");
    JPanel localJPanel5 = new JPanel();
    align(localJPanel5);
    localJPanel5.setLayout(new BoxLayout(localJPanel5, 2));
    localJPanel5.add(Box.createGlue());
    approveButton = new JButton(getApproveButtonText(paramJFileChooser))
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(MAX_SIZEwidth, getPreferredSizeheight);
      }
    };
    approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    approveButton.setInheritsPopupMenu(true);
    align(approveButton);
    approveButton.setMargin(buttonMargin);
    approveButton.addActionListener(getApproveSelectionAction());
    localJPanel5.add(approveButton);
    localJPanel5.add(Box.createGlue());
    JButton local7 = new JButton(updateButtonText)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(MAX_SIZEwidth, getPreferredSizeheight);
      }
    };
    local7.setMnemonic(updateButtonMnemonic);
    local7.setToolTipText(updateButtonToolTipText);
    local7.setInheritsPopupMenu(true);
    align(local7);
    local7.setMargin(buttonMargin);
    local7.addActionListener(getUpdateAction());
    localJPanel5.add(local7);
    localJPanel5.add(Box.createGlue());
    JButton local8 = new JButton(cancelButtonText)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(MAX_SIZEwidth, getPreferredSizeheight);
      }
    };
    local8.setMnemonic(cancelButtonMnemonic);
    local8.setToolTipText(cancelButtonToolTipText);
    local8.setInheritsPopupMenu(true);
    align(local8);
    local8.setMargin(buttonMargin);
    local8.addActionListener(getCancelSelectionAction());
    localJPanel5.add(local8);
    localJPanel5.add(Box.createGlue());
    JButton local9 = new JButton(helpButtonText)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(MAX_SIZEwidth, getPreferredSizeheight);
      }
    };
    local9.setMnemonic(helpButtonMnemonic);
    local9.setToolTipText(helpButtonToolTipText);
    align(local9);
    local9.setMargin(buttonMargin);
    local9.setEnabled(false);
    local9.setInheritsPopupMenu(true);
    localJPanel5.add(local9);
    localJPanel5.add(Box.createGlue());
    localJPanel5.setInheritsPopupMenu(true);
    bottomPanel.add(localJPanel5, "South");
    bottomPanel.setInheritsPopupMenu(true);
    if (paramJFileChooser.getControlButtonsAreShown()) {
      paramJFileChooser.add(bottomPanel, "South");
    }
  }
  
  protected JPanel getBottomPanel()
  {
    if (bottomPanel == null) {
      bottomPanel = new JPanel(new BorderLayout(0, 4));
    }
    return bottomPanel;
  }
  
  private void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (getFileChooser().getControlButtonsAreShown()) {
      getFileChooser().add(bottomPanel, "South");
    } else {
      getFileChooser().remove(getBottomPanel());
    }
  }
  
  public void uninstallComponents(JFileChooser paramJFileChooser)
  {
    paramJFileChooser.removeAll();
    bottomPanel = null;
    if (filterComboBoxModel != null) {
      paramJFileChooser.removePropertyChangeListener(filterComboBoxModel);
    }
  }
  
  protected void installStrings(JFileChooser paramJFileChooser)
  {
    super.installStrings(paramJFileChooser);
    Locale localLocale = paramJFileChooser.getLocale();
    enterFolderNameLabelText = UIManager.getString("FileChooser.enterFolderNameLabelText", localLocale);
    enterFolderNameLabelMnemonic = getMnemonic("FileChooser.enterFolderNameLabelMnemonic", localLocale).intValue();
    enterFileNameLabelText = UIManager.getString("FileChooser.enterFileNameLabelText", localLocale);
    enterFileNameLabelMnemonic = getMnemonic("FileChooser.enterFileNameLabelMnemonic", localLocale).intValue();
    filesLabelText = UIManager.getString("FileChooser.filesLabelText", localLocale);
    filesLabelMnemonic = getMnemonic("FileChooser.filesLabelMnemonic", localLocale).intValue();
    foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", localLocale);
    foldersLabelMnemonic = getMnemonic("FileChooser.foldersLabelMnemonic", localLocale).intValue();
    pathLabelText = UIManager.getString("FileChooser.pathLabelText", localLocale);
    pathLabelMnemonic = getMnemonic("FileChooser.pathLabelMnemonic", localLocale).intValue();
    filterLabelText = UIManager.getString("FileChooser.filterLabelText", localLocale);
    filterLabelMnemonic = getMnemonic("FileChooser.filterLabelMnemonic", localLocale).intValue();
  }
  
  private Integer getMnemonic(String paramString, Locale paramLocale)
  {
    return Integer.valueOf(SwingUtilities2.getUIDefaultsInt(paramString, paramLocale));
  }
  
  protected void installIcons(JFileChooser paramJFileChooser) {}
  
  protected void uninstallIcons(JFileChooser paramJFileChooser) {}
  
  protected JScrollPane createFilesList()
  {
    fileList = new JList();
    if (getFileChooser().isMultiSelectionEnabled()) {
      fileList.setSelectionMode(2);
    } else {
      fileList.setSelectionMode(0);
    }
    fileList.setModel(new MotifFileListModel());
    fileList.getSelectionModel().removeSelectionInterval(0, 0);
    fileList.setCellRenderer(new FileCellRenderer());
    fileList.addListSelectionListener(createListSelectionListener(getFileChooser()));
    fileList.addMouseListener(createDoubleClickListener(getFileChooser(), fileList));
    fileList.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent paramAnonymousMouseEvent)
      {
        JFileChooser localJFileChooser = getFileChooser();
        if ((SwingUtilities.isLeftMouseButton(paramAnonymousMouseEvent)) && (!localJFileChooser.isMultiSelectionEnabled()))
        {
          int i = SwingUtilities2.loc2IndexFileList(fileList, paramAnonymousMouseEvent.getPoint());
          if (i >= 0)
          {
            File localFile = (File)fileList.getModel().getElementAt(i);
            setFileName(localJFileChooser.getName(localFile));
          }
        }
      }
    });
    align(fileList);
    JScrollPane localJScrollPane = new JScrollPane(fileList);
    localJScrollPane.setPreferredSize(prefListSize);
    localJScrollPane.setMaximumSize(MAX_SIZE);
    align(localJScrollPane);
    fileList.setInheritsPopupMenu(true);
    localJScrollPane.setInheritsPopupMenu(true);
    return localJScrollPane;
  }
  
  protected JScrollPane createDirectoryList()
  {
    directoryList = new JList();
    align(directoryList);
    directoryList.setCellRenderer(new DirectoryCellRenderer());
    directoryList.setModel(new MotifDirectoryListModel());
    directoryList.getSelectionModel().removeSelectionInterval(0, 0);
    directoryList.addMouseListener(createDoubleClickListener(getFileChooser(), directoryList));
    directoryList.addListSelectionListener(createListSelectionListener(getFileChooser()));
    directoryList.setInheritsPopupMenu(true);
    JScrollPane localJScrollPane = new JScrollPane(directoryList);
    localJScrollPane.setMaximumSize(MAX_SIZE);
    localJScrollPane.setPreferredSize(prefListSize);
    localJScrollPane.setInheritsPopupMenu(true);
    align(localJScrollPane);
    return localJScrollPane;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension1 = getFileChooser().getAccessory() != null ? WITH_ACCELERATOR_PREF_SIZE : PREF_SIZE;
    Dimension localDimension2 = paramJComponent.getLayout().preferredLayoutSize(paramJComponent);
    if (localDimension2 != null) {
      return new Dimension(width < width ? width : width, height < height ? height : height);
    }
    return localDimension1;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return new Dimension(200, 300);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  protected void align(JComponent paramJComponent)
  {
    paramJComponent.setAlignmentX(0.0F);
    paramJComponent.setAlignmentY(0.0F);
  }
  
  protected FilterComboBoxModel createFilterComboBoxModel()
  {
    return new FilterComboBoxModel();
  }
  
  protected FilterComboBoxRenderer createFilterComboBoxRenderer()
  {
    return new FilterComboBoxRenderer();
  }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser)
  {
    return approveButton;
  }
  
  protected class DirectoryCellRenderer
    extends DefaultListCellRenderer
  {
    protected DirectoryCellRenderer() {}
    
    public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
      setText(getFileChooser().getName((File)paramObject));
      setInheritsPopupMenu(true);
      return this;
    }
  }
  
  protected class FileCellRenderer
    extends DefaultListCellRenderer
  {
    protected FileCellRenderer() {}
    
    public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
      setText(getFileChooser().getName((File)paramObject));
      setInheritsPopupMenu(true);
      return this;
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
      if (str.equals("ChoosableFileFilterChangedProperty"))
      {
        filters = ((FileFilter[])paramPropertyChangeEvent.getNewValue());
        fireContentsChanged(this, -1, -1);
      }
      else if (str.equals("fileFilterChanged"))
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
  
  protected class MotifDirectoryListModel
    extends AbstractListModel<File>
    implements ListDataListener
  {
    public MotifDirectoryListModel()
    {
      getModel().addListDataListener(this);
    }
    
    public int getSize()
    {
      return getModel().getDirectories().size();
    }
    
    public File getElementAt(int paramInt)
    {
      return (File)getModel().getDirectories().elementAt(paramInt);
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      fireIntervalAdded(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      fireIntervalRemoved(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
    }
    
    public void fireContentsChanged()
    {
      fireContentsChanged(this, 0, getModel().getDirectories().size() - 1);
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      fireContentsChanged();
    }
  }
  
  protected class MotifFileListModel
    extends AbstractListModel<File>
    implements ListDataListener
  {
    public MotifFileListModel()
    {
      getModel().addListDataListener(this);
    }
    
    public int getSize()
    {
      return getModel().getFiles().size();
    }
    
    public boolean contains(Object paramObject)
    {
      return getModel().getFiles().contains(paramObject);
    }
    
    public int indexOf(Object paramObject)
    {
      return getModel().getFiles().indexOf(paramObject);
    }
    
    public File getElementAt(int paramInt)
    {
      return (File)getModel().getFiles().elementAt(paramInt);
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      fireIntervalAdded(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      fireIntervalRemoved(this, paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
    }
    
    public void fireContentsChanged()
    {
      fireContentsChanged(this, 0, getModel().getFiles().size() - 1);
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      fireContentsChanged();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */