package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.filechooser.FileSystemView;
import sun.awt.shell.ShellFolder;

public class BasicDirectoryModel
  extends AbstractListModel<Object>
  implements PropertyChangeListener
{
  private JFileChooser filechooser = null;
  private Vector<File> fileCache = new Vector(50);
  private LoadFilesThread loadThread = null;
  private Vector<File> files = null;
  private Vector<File> directories = null;
  private int fetchID = 0;
  private PropertyChangeSupport changeSupport;
  private boolean busy = false;
  
  public BasicDirectoryModel(JFileChooser paramJFileChooser)
  {
    filechooser = paramJFileChooser;
    validateFileCache();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if ((str == "directoryChanged") || (str == "fileViewChanged") || (str == "fileFilterChanged") || (str == "FileHidingChanged") || (str == "fileSelectionChanged"))
    {
      validateFileCache();
    }
    else if ("UI".equals(str))
    {
      Object localObject = paramPropertyChangeEvent.getOldValue();
      if ((localObject instanceof BasicFileChooserUI))
      {
        BasicFileChooserUI localBasicFileChooserUI = (BasicFileChooserUI)localObject;
        BasicDirectoryModel localBasicDirectoryModel = localBasicFileChooserUI.getModel();
        if (localBasicDirectoryModel != null) {
          localBasicDirectoryModel.invalidateFileCache();
        }
      }
    }
    else if ("JFileChooserDialogIsClosingProperty".equals(str))
    {
      invalidateFileCache();
    }
  }
  
  public void invalidateFileCache()
  {
    if (loadThread != null)
    {
      loadThread.interrupt();
      loadThread.cancelRunnables();
      loadThread = null;
    }
  }
  
  public Vector<File> getDirectories()
  {
    synchronized (fileCache)
    {
      if (directories != null) {
        return directories;
      }
      Vector localVector = getFiles();
      return directories;
    }
  }
  
  public Vector<File> getFiles()
  {
    synchronized (fileCache)
    {
      if (files != null) {
        return files;
      }
      files = new Vector();
      directories = new Vector();
      directories.addElement(filechooser.getFileSystemView().createFileObject(filechooser.getCurrentDirectory(), ".."));
      for (int i = 0; i < getSize(); i++)
      {
        File localFile = (File)fileCache.get(i);
        if (filechooser.isTraversable(localFile)) {
          directories.add(localFile);
        } else {
          files.add(localFile);
        }
      }
      return files;
    }
  }
  
  public void validateFileCache()
  {
    File localFile = filechooser.getCurrentDirectory();
    if (localFile == null) {
      return;
    }
    if (loadThread != null)
    {
      loadThread.interrupt();
      loadThread.cancelRunnables();
    }
    setBusy(true, ++fetchID);
    loadThread = new LoadFilesThread(localFile, fetchID);
    loadThread.start();
  }
  
  public boolean renameFile(File paramFile1, File paramFile2)
  {
    synchronized (fileCache)
    {
      if (paramFile1.renameTo(paramFile2))
      {
        validateFileCache();
        return true;
      }
      return false;
    }
  }
  
  public void fireContentsChanged()
  {
    fireContentsChanged(this, 0, getSize() - 1);
  }
  
  public int getSize()
  {
    return fileCache.size();
  }
  
  public boolean contains(Object paramObject)
  {
    return fileCache.contains(paramObject);
  }
  
  public int indexOf(Object paramObject)
  {
    return fileCache.indexOf(paramObject);
  }
  
  public Object getElementAt(int paramInt)
  {
    return fileCache.get(paramInt);
  }
  
  public void intervalAdded(ListDataEvent paramListDataEvent) {}
  
  public void intervalRemoved(ListDataEvent paramListDataEvent) {}
  
  protected void sort(Vector<? extends File> paramVector)
  {
    ShellFolder.sort(paramVector);
  }
  
  protected boolean lt(File paramFile1, File paramFile2)
  {
    int i = paramFile1.getName().toLowerCase().compareTo(paramFile2.getName().toLowerCase());
    if (i != 0) {
      return i < 0;
    }
    return paramFile1.getName().compareTo(paramFile2.getName()) < 0;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport == null) {
      changeSupport = new PropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport != null) {
      changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if (changeSupport != null) {
      changeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
    }
  }
  
  private synchronized void setBusy(final boolean paramBoolean, int paramInt)
  {
    if (paramInt == fetchID)
    {
      boolean bool = busy;
      busy = paramBoolean;
      if ((changeSupport != null) && (paramBoolean != bool)) {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            firePropertyChange("busy", Boolean.valueOf(!paramBoolean), Boolean.valueOf(paramBoolean));
          }
        });
      }
    }
  }
  
  class DoChangeContents
    implements Runnable
  {
    private List<File> addFiles;
    private List<File> remFiles;
    private boolean doFire = true;
    private int fid;
    private int addStart = 0;
    private int remStart = 0;
    
    public DoChangeContents(int paramInt1, List<File> paramList, int paramInt2, int paramInt3)
    {
      addFiles = paramInt1;
      addStart = paramList;
      remFiles = paramInt2;
      remStart = paramInt3;
      int i;
      fid = i;
    }
    
    synchronized void cancel()
    {
      doFire = false;
    }
    
    public synchronized void run()
    {
      if ((fetchID == fid) && (doFire))
      {
        int i = remFiles == null ? 0 : remFiles.size();
        int j = addFiles == null ? 0 : addFiles.size();
        synchronized (fileCache)
        {
          if (i > 0) {
            fileCache.removeAll(remFiles);
          }
          if (j > 0) {
            fileCache.addAll(addStart, addFiles);
          }
          files = null;
          directories = null;
        }
        if ((i > 0) && (j == 0)) {
          fireIntervalRemoved(BasicDirectoryModel.this, remStart, remStart + i - 1);
        } else if ((j > 0) && (i == 0) && (addStart + j <= fileCache.size())) {
          fireIntervalAdded(BasicDirectoryModel.this, addStart, addStart + j - 1);
        } else {
          fireContentsChanged();
        }
      }
    }
  }
  
  class LoadFilesThread
    extends Thread
  {
    File currentDirectory = null;
    int fid;
    Vector<BasicDirectoryModel.DoChangeContents> runnables = new Vector(10);
    
    public LoadFilesThread(File paramFile, int paramInt)
    {
      super();
      currentDirectory = paramFile;
      fid = paramInt;
    }
    
    public void run()
    {
      run0();
      BasicDirectoryModel.this.setBusy(false, fid);
    }
    
    public void run0()
    {
      FileSystemView localFileSystemView = filechooser.getFileSystemView();
      if (isInterrupted()) {
        return;
      }
      File[] arrayOfFile = localFileSystemView.getFiles(currentDirectory, filechooser.isFileHidingEnabled());
      if (isInterrupted()) {
        return;
      }
      final Vector localVector1 = new Vector();
      Vector localVector2 = new Vector();
      for (File localFile : arrayOfFile) {
        if (filechooser.accept(localFile))
        {
          boolean bool = filechooser.isTraversable(localFile);
          if (bool) {
            localVector1.addElement(localFile);
          } else if (filechooser.isFileSelectionEnabled()) {
            localVector2.addElement(localFile);
          }
          if (isInterrupted()) {
            return;
          }
        }
      }
      sort(localVector1);
      sort(localVector2);
      localVector1.addAll(localVector2);
      ??? = (BasicDirectoryModel.DoChangeContents)ShellFolder.invoke(new Callable()
      {
        public BasicDirectoryModel.DoChangeContents call()
        {
          int i = localVector1.size();
          int j = fileCache.size();
          int k;
          int m;
          int n;
          if (i > j)
          {
            k = j;
            m = i;
            for (n = 0; n < j; n++) {
              if (!((File)localVector1.get(n)).equals(fileCache.get(n)))
              {
                k = n;
                for (int i1 = n; i1 < i; i1++) {
                  if (((File)localVector1.get(i1)).equals(fileCache.get(n)))
                  {
                    m = i1;
                    break;
                  }
                }
                break;
              }
            }
            if ((k >= 0) && (m > k) && (localVector1.subList(m, i).equals(fileCache.subList(k, j))))
            {
              if (isInterrupted()) {
                return null;
              }
              return new BasicDirectoryModel.DoChangeContents(BasicDirectoryModel.this, localVector1.subList(k, m), k, null, 0, fid);
            }
          }
          else if (i < j)
          {
            k = -1;
            m = -1;
            for (n = 0; n < i; n++) {
              if (!((File)localVector1.get(n)).equals(fileCache.get(n)))
              {
                k = n;
                m = n + j - i;
                break;
              }
            }
            if ((k >= 0) && (m > k) && (fileCache.subList(m, j).equals(localVector1.subList(k, i))))
            {
              if (isInterrupted()) {
                return null;
              }
              return new BasicDirectoryModel.DoChangeContents(BasicDirectoryModel.this, null, 0, new Vector(fileCache.subList(k, m)), k, fid);
            }
          }
          if (!fileCache.equals(localVector1))
          {
            if (isInterrupted()) {
              cancelRunnables(runnables);
            }
            return new BasicDirectoryModel.DoChangeContents(BasicDirectoryModel.this, localVector1, 0, fileCache, 0, fid);
          }
          return null;
        }
      });
      if (??? != null)
      {
        runnables.addElement(???);
        SwingUtilities.invokeLater((Runnable)???);
      }
    }
    
    public void cancelRunnables(Vector<BasicDirectoryModel.DoChangeContents> paramVector)
    {
      Iterator localIterator = paramVector.iterator();
      while (localIterator.hasNext())
      {
        BasicDirectoryModel.DoChangeContents localDoChangeContents = (BasicDirectoryModel.DoChangeContents)localIterator.next();
        localDoChangeContents.cancel();
      }
    }
    
    public void cancelRunnables()
    {
      cancelRunnables(runnables);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicDirectoryModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */