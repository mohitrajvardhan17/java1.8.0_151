package sun.nio.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sun.misc.Unsafe;

class WindowsUserDefinedFileAttributeView
  extends AbstractUserDefinedFileAttributeView
{
  private static final Unsafe unsafe = ;
  private final WindowsPath file;
  private final boolean followLinks;
  
  private String join(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      throw new NullPointerException("'name' is null");
    }
    return paramString1 + ":" + paramString2;
  }
  
  private String join(WindowsPath paramWindowsPath, String paramString)
    throws WindowsException
  {
    return join(paramWindowsPath.getPathForWin32Calls(), paramString);
  }
  
  WindowsUserDefinedFileAttributeView(WindowsPath paramWindowsPath, boolean paramBoolean)
  {
    file = paramWindowsPath;
    followLinks = paramBoolean;
  }
  
  private List<String> listUsingStreamEnumeration()
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      WindowsNativeDispatcher.FirstStream localFirstStream = WindowsNativeDispatcher.FindFirstStream(file.getPathForWin32Calls());
      if (localFirstStream != null)
      {
        long l = localFirstStream.handle();
        try
        {
          String str = localFirstStream.name();
          String[] arrayOfString;
          if (!str.equals("::$DATA"))
          {
            arrayOfString = str.split(":");
            localArrayList.add(arrayOfString[1]);
          }
          while ((str = WindowsNativeDispatcher.FindNextStream(l)) != null)
          {
            arrayOfString = str.split(":");
            localArrayList.add(arrayOfString[1]);
          }
        }
        finally
        {
          WindowsNativeDispatcher.FindClose(l);
        }
      }
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(file);
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private List<String> listUsingBackupRead()
    throws IOException
  {
    long l1 = -1L;
    try
    {
      int i = 33554432;
      if ((!followLinks) && (file.getFileSystem().supportsLinks())) {
        i |= 0x200000;
      }
      l1 = WindowsNativeDispatcher.CreateFile(file.getPathForWin32Calls(), Integer.MIN_VALUE, 1, 3, i);
    }
    catch (WindowsException localWindowsException1)
    {
      localWindowsException1.rethrowAsIOException(file);
    }
    NativeBuffer localNativeBuffer = null;
    ArrayList localArrayList = new ArrayList();
    try
    {
      localNativeBuffer = NativeBuffers.getNativeBuffer(4096);
      long l2 = localNativeBuffer.address();
      long l3 = 0L;
      try
      {
        for (;;)
        {
          WindowsNativeDispatcher.BackupResult localBackupResult = WindowsNativeDispatcher.BackupRead(l1, l2, 20, false, l3);
          l3 = localBackupResult.context();
          if (localBackupResult.bytesTransferred() == 0) {
            break;
          }
          int j = unsafe.getInt(l2 + 0L);
          long l4 = unsafe.getLong(l2 + 8L);
          int k = unsafe.getInt(l2 + 16L);
          if (k > 0)
          {
            localBackupResult = WindowsNativeDispatcher.BackupRead(l1, l2, k, false, l3);
            if (localBackupResult.bytesTransferred() != k) {
              break;
            }
          }
          if (j == 4)
          {
            char[] arrayOfChar = new char[k / 2];
            unsafe.copyMemory(null, l2, arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, k);
            String[] arrayOfString = new String(arrayOfChar).split(":");
            if (arrayOfString.length == 3) {
              localArrayList.add(arrayOfString[1]);
            }
          }
          if (j == 9) {
            throw new IOException("Spare blocks not handled");
          }
          if (l4 > 0L) {
            WindowsNativeDispatcher.BackupSeek(l1, l4, l3);
          }
        }
        if (l3 != 0L) {
          try
          {
            WindowsNativeDispatcher.BackupRead(l1, 0L, 0, true, l3);
          }
          catch (WindowsException localWindowsException2) {}
        }
        if (localNativeBuffer == null) {
          break label371;
        }
      }
      catch (WindowsException localWindowsException3)
      {
        throw new IOException(localWindowsException3.errorString());
      }
      finally
      {
        if (l3 != 0L) {
          try
          {
            WindowsNativeDispatcher.BackupRead(l1, 0L, 0, true, l3);
          }
          catch (WindowsException localWindowsException4) {}
        }
      }
      localNativeBuffer.release();
      label371:
      WindowsNativeDispatcher.CloseHandle(l1);
    }
    finally
    {
      if (localNativeBuffer != null) {
        localNativeBuffer.release();
      }
      WindowsNativeDispatcher.CloseHandle(l1);
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public List<String> list()
    throws IOException
  {
    if (System.getSecurityManager() != null) {
      checkAccess(file.getPathForPermissionCheck(), true, false);
    }
    if (file.getFileSystem().supportsStreamEnumeration()) {
      return listUsingStreamEnumeration();
    }
    return listUsingBackupRead();
  }
  
  public int size(String paramString)
    throws IOException
  {
    if (System.getSecurityManager() != null) {
      checkAccess(file.getPathForPermissionCheck(), true, false);
    }
    FileChannel localFileChannel = null;
    try
    {
      HashSet localHashSet = new HashSet();
      localHashSet.add(StandardOpenOption.READ);
      if (!followLinks) {
        localHashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
      }
      localFileChannel = WindowsChannelFactory.newFileChannel(join(file, paramString), null, localHashSet, 0L);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(join(file.getPathForPermissionCheck(), paramString));
    }
    try
    {
      long l = localFileChannel.size();
      if (l > 2147483647L) {
        throw new ArithmeticException("Stream too large");
      }
      int i = (int)l;
      return i;
    }
    finally
    {
      localFileChannel.close();
    }
  }
  
  public int read(String paramString, ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (System.getSecurityManager() != null) {
      checkAccess(file.getPathForPermissionCheck(), true, false);
    }
    FileChannel localFileChannel = null;
    try
    {
      HashSet localHashSet = new HashSet();
      localHashSet.add(StandardOpenOption.READ);
      if (!followLinks) {
        localHashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
      }
      localFileChannel = WindowsChannelFactory.newFileChannel(join(file, paramString), null, localHashSet, 0L);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(join(file.getPathForPermissionCheck(), paramString));
    }
    try
    {
      if (localFileChannel.size() > paramByteBuffer.remaining()) {
        throw new IOException("Stream too large");
      }
      int i = 0;
      while (paramByteBuffer.hasRemaining())
      {
        j = localFileChannel.read(paramByteBuffer);
        if (j < 0) {
          break;
        }
        i += j;
      }
      int j = i;
      return j;
    }
    finally
    {
      localFileChannel.close();
    }
  }
  
  /* Error */
  public int write(String paramString, ByteBuffer paramByteBuffer)
    throws IOException
  {
    // Byte code:
    //   0: invokestatic 260	java/lang/System:getSecurityManager	()Ljava/lang/SecurityManager;
    //   3: ifnull +16 -> 19
    //   6: aload_0
    //   7: aload_0
    //   8: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
    //   11: invokevirtual 297	sun/nio/fs/WindowsPath:getPathForPermissionCheck	()Ljava/lang/String;
    //   14: iconst_0
    //   15: iconst_1
    //   16: invokevirtual 300	sun/nio/fs/WindowsUserDefinedFileAttributeView:checkAccess	(Ljava/lang/String;ZZ)V
    //   19: ldc2_w 120
    //   22: lstore_3
    //   23: ldc 3
    //   25: istore 5
    //   27: aload_0
    //   28: getfield 248	sun/nio/fs/WindowsUserDefinedFileAttributeView:followLinks	Z
    //   31: ifne +10 -> 41
    //   34: iload 5
    //   36: ldc 2
    //   38: ior
    //   39: istore 5
    //   41: aload_0
    //   42: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
    //   45: invokevirtual 298	sun/nio/fs/WindowsPath:getPathForWin32Calls	()Ljava/lang/String;
    //   48: ldc 1
    //   50: bipush 7
    //   52: iconst_3
    //   53: iload 5
    //   55: invokestatic 290	sun/nio/fs/WindowsNativeDispatcher:CreateFile	(Ljava/lang/String;IIII)J
    //   58: lstore_3
    //   59: goto +14 -> 73
    //   62: astore 5
    //   64: aload 5
    //   66: aload_0
    //   67: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
    //   70: invokevirtual 281	sun/nio/fs/WindowsException:rethrowAsIOException	(Lsun/nio/fs/WindowsPath;)V
    //   73: new 140	java/util/HashSet
    //   76: dup
    //   77: invokespecial 269	java/util/HashSet:<init>	()V
    //   80: astore 5
    //   82: aload_0
    //   83: getfield 248	sun/nio/fs/WindowsUserDefinedFileAttributeView:followLinks	Z
    //   86: ifne +14 -> 100
    //   89: aload 5
    //   91: getstatic 247	sun/nio/fs/WindowsChannelFactory:OPEN_REPARSE_POINT	Ljava/nio/file/OpenOption;
    //   94: invokeinterface 306 2 0
    //   99: pop
    //   100: aload 5
    //   102: getstatic 242	java/nio/file/StandardOpenOption:CREATE	Ljava/nio/file/StandardOpenOption;
    //   105: invokeinterface 306 2 0
    //   110: pop
    //   111: aload 5
    //   113: getstatic 245	java/nio/file/StandardOpenOption:WRITE	Ljava/nio/file/StandardOpenOption;
    //   116: invokeinterface 306 2 0
    //   121: pop
    //   122: aload 5
    //   124: getstatic 244	java/nio/file/StandardOpenOption:TRUNCATE_EXISTING	Ljava/nio/file/StandardOpenOption;
    //   127: invokeinterface 306 2 0
    //   132: pop
    //   133: aconst_null
    //   134: astore 6
    //   136: aload_0
    //   137: aload_0
    //   138: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
    //   141: aload_1
    //   142: invokespecial 304	sun/nio/fs/WindowsUserDefinedFileAttributeView:join	(Lsun/nio/fs/WindowsPath;Ljava/lang/String;)Ljava/lang/String;
    //   145: aconst_null
    //   146: aload 5
    //   148: lconst_0
    //   149: invokestatic 278	sun/nio/fs/WindowsChannelFactory:newFileChannel	(Ljava/lang/String;Ljava/lang/String;Ljava/util/Set;J)Ljava/nio/channels/FileChannel;
    //   152: astore 6
    //   154: goto +22 -> 176
    //   157: astore 7
    //   159: aload 7
    //   161: aload_0
    //   162: aload_0
    //   163: getfield 250	sun/nio/fs/WindowsUserDefinedFileAttributeView:file	Lsun/nio/fs/WindowsPath;
    //   166: invokevirtual 297	sun/nio/fs/WindowsPath:getPathForPermissionCheck	()Ljava/lang/String;
    //   169: aload_1
    //   170: invokespecial 303	sun/nio/fs/WindowsUserDefinedFileAttributeView:join	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   173: invokevirtual 280	sun/nio/fs/WindowsException:rethrowAsIOException	(Ljava/lang/String;)V
    //   176: aload_2
    //   177: invokevirtual 261	java/nio/ByteBuffer:remaining	()I
    //   180: istore 7
    //   182: aload_2
    //   183: invokevirtual 262	java/nio/ByteBuffer:hasRemaining	()Z
    //   186: ifeq +13 -> 199
    //   189: aload 6
    //   191: aload_2
    //   192: invokevirtual 266	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;)I
    //   195: pop
    //   196: goto -14 -> 182
    //   199: iload 7
    //   201: istore 8
    //   203: aload 6
    //   205: invokevirtual 264	java/nio/channels/FileChannel:close	()V
    //   208: lload_3
    //   209: invokestatic 285	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
    //   212: iload 8
    //   214: ireturn
    //   215: astore 9
    //   217: aload 6
    //   219: invokevirtual 264	java/nio/channels/FileChannel:close	()V
    //   222: aload 9
    //   224: athrow
    //   225: astore 10
    //   227: lload_3
    //   228: invokestatic 285	sun/nio/fs/WindowsNativeDispatcher:CloseHandle	(J)V
    //   231: aload 10
    //   233: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	234	0	this	WindowsUserDefinedFileAttributeView
    //   0	234	1	paramString	String
    //   0	234	2	paramByteBuffer	ByteBuffer
    //   22	206	3	l	long
    //   25	29	5	i	int
    //   62	3	5	localWindowsException1	WindowsException
    //   80	67	5	localHashSet	HashSet
    //   134	84	6	localFileChannel	FileChannel
    //   157	3	7	localWindowsException2	WindowsException
    //   180	20	7	j	int
    //   201	12	8	k	int
    //   215	8	9	localObject1	Object
    //   225	7	10	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   23	59	62	sun/nio/fs/WindowsException
    //   136	154	157	sun/nio/fs/WindowsException
    //   176	203	215	finally
    //   215	217	215	finally
    //   73	208	225	finally
    //   215	227	225	finally
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (System.getSecurityManager() != null) {
      checkAccess(file.getPathForPermissionCheck(), false, true);
    }
    String str1 = WindowsLinkSupport.getFinalPath(file, followLinks);
    String str2 = join(str1, paramString);
    try
    {
      WindowsNativeDispatcher.DeleteFile(str2);
    }
    catch (WindowsException localWindowsException)
    {
      localWindowsException.rethrowAsIOException(str2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsUserDefinedFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */