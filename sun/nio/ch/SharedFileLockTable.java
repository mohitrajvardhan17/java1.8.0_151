package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SharedFileLockTable
  extends FileLockTable
{
  private static ConcurrentHashMap<FileKey, List<FileLockReference>> lockMap = new ConcurrentHashMap();
  private static ReferenceQueue<FileLock> queue = new ReferenceQueue();
  private final Channel channel;
  private final FileKey fileKey;
  
  SharedFileLockTable(Channel paramChannel, FileDescriptor paramFileDescriptor)
    throws IOException
  {
    channel = paramChannel;
    fileKey = FileKey.create(paramFileDescriptor);
  }
  
  public void add(FileLock paramFileLock)
    throws OverlappingFileLockException
  {
    Object localObject1 = (List)lockMap.get(fileKey);
    for (;;)
    {
      if (localObject1 == null)
      {
        localObject1 = new ArrayList(2);
        List localList;
        synchronized (localObject1)
        {
          localList = (List)lockMap.putIfAbsent(fileKey, localObject1);
          if (localList == null)
          {
            ((List)localObject1).add(new FileLockReference(paramFileLock, queue, fileKey));
            break;
          }
        }
        localObject1 = localList;
      }
      synchronized (localObject1)
      {
        ??? = (List)lockMap.get(fileKey);
        if (localObject1 == ???)
        {
          checkList((List)localObject1, paramFileLock.position(), paramFileLock.size());
          ((List)localObject1).add(new FileLockReference(paramFileLock, queue, fileKey));
          break;
        }
        localObject1 = ???;
      }
    }
    removeStaleEntries();
  }
  
  private void removeKeyIfEmpty(FileKey paramFileKey, List<FileLockReference> paramList)
  {
    assert (Thread.holdsLock(paramList));
    assert (lockMap.get(paramFileKey) == paramList);
    if (paramList.isEmpty()) {
      lockMap.remove(paramFileKey);
    }
  }
  
  public void remove(FileLock paramFileLock)
  {
    assert (paramFileLock != null);
    List localList = (List)lockMap.get(fileKey);
    if (localList == null) {
      return;
    }
    synchronized (localList)
    {
      for (int i = 0; i < localList.size(); i++)
      {
        FileLockReference localFileLockReference = (FileLockReference)localList.get(i);
        FileLock localFileLock = (FileLock)localFileLockReference.get();
        if (localFileLock == paramFileLock)
        {
          assert ((localFileLock != null) && (localFileLock.acquiredBy() == channel));
          localFileLockReference.clear();
          localList.remove(i);
          break;
        }
      }
    }
  }
  
  public List<FileLock> removeAll()
  {
    ArrayList localArrayList = new ArrayList();
    List localList = (List)lockMap.get(fileKey);
    if (localList != null) {
      synchronized (localList)
      {
        int i = 0;
        while (i < localList.size())
        {
          FileLockReference localFileLockReference = (FileLockReference)localList.get(i);
          FileLock localFileLock = (FileLock)localFileLockReference.get();
          if ((localFileLock != null) && (localFileLock.acquiredBy() == channel))
          {
            localFileLockReference.clear();
            localList.remove(i);
            localArrayList.add(localFileLock);
          }
          else
          {
            i++;
          }
        }
        removeKeyIfEmpty(fileKey, localList);
      }
    }
    return localArrayList;
  }
  
  public void replace(FileLock paramFileLock1, FileLock paramFileLock2)
  {
    List localList = (List)lockMap.get(fileKey);
    assert (localList != null);
    synchronized (localList)
    {
      for (int i = 0; i < localList.size(); i++)
      {
        FileLockReference localFileLockReference = (FileLockReference)localList.get(i);
        FileLock localFileLock = (FileLock)localFileLockReference.get();
        if (localFileLock == paramFileLock1)
        {
          localFileLockReference.clear();
          localList.set(i, new FileLockReference(paramFileLock2, queue, fileKey));
          break;
        }
      }
    }
  }
  
  private void checkList(List<FileLockReference> paramList, long paramLong1, long paramLong2)
    throws OverlappingFileLockException
  {
    assert (Thread.holdsLock(paramList));
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      FileLockReference localFileLockReference = (FileLockReference)localIterator.next();
      FileLock localFileLock = (FileLock)localFileLockReference.get();
      if ((localFileLock != null) && (localFileLock.overlaps(paramLong1, paramLong2))) {
        throw new OverlappingFileLockException();
      }
    }
  }
  
  private void removeStaleEntries()
  {
    FileLockReference localFileLockReference;
    while ((localFileLockReference = (FileLockReference)queue.poll()) != null)
    {
      FileKey localFileKey = localFileLockReference.fileKey();
      List localList = (List)lockMap.get(localFileKey);
      if (localList != null) {
        synchronized (localList)
        {
          localList.remove(localFileLockReference);
          removeKeyIfEmpty(localFileKey, localList);
        }
      }
    }
  }
  
  private static class FileLockReference
    extends WeakReference<FileLock>
  {
    private FileKey fileKey;
    
    FileLockReference(FileLock paramFileLock, ReferenceQueue<FileLock> paramReferenceQueue, FileKey paramFileKey)
    {
      super(paramReferenceQueue);
      fileKey = paramFileKey;
    }
    
    FileKey fileKey()
    {
      return fileKey;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\SharedFileLockTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */