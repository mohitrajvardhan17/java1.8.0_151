package sun.nio.fs;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class WindowsNativeDispatcher
{
  private static final Unsafe unsafe = ;
  
  private WindowsNativeDispatcher() {}
  
  static native long CreateEvent(boolean paramBoolean1, boolean paramBoolean2)
    throws WindowsException;
  
  static long CreateFile(String paramString, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      long l = CreateFile0(localNativeBuffer.address(), paramInt1, paramInt2, paramLong, paramInt3, paramInt4);
      return l;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  static long CreateFile(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws WindowsException
  {
    return CreateFile(paramString, paramInt1, paramInt2, 0L, paramInt3, paramInt4);
  }
  
  private static native long CreateFile0(long paramLong1, int paramInt1, int paramInt2, long paramLong2, int paramInt3, int paramInt4)
    throws WindowsException;
  
  static native void CloseHandle(long paramLong);
  
  /* Error */
  static void DeleteFile(String paramString)
    throws WindowsException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 322	sun/nio/fs/WindowsNativeDispatcher:asNativeBuffer	(Ljava/lang/String;)Lsun/nio/fs/NativeBuffer;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 286	sun/nio/fs/NativeBuffer:address	()J
    //   9: invokestatic 297	sun/nio/fs/WindowsNativeDispatcher:DeleteFile0	(J)V
    //   12: aload_1
    //   13: invokevirtual 287	sun/nio/fs/NativeBuffer:release	()V
    //   16: goto +10 -> 26
    //   19: astore_2
    //   20: aload_1
    //   21: invokevirtual 287	sun/nio/fs/NativeBuffer:release	()V
    //   24: aload_2
    //   25: athrow
    //   26: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	paramString	String
    //   4	17	1	localNativeBuffer	NativeBuffer
    //   19	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	12	19	finally
  }
  
  private static native void DeleteFile0(long paramLong)
    throws WindowsException;
  
  static void CreateDirectory(String paramString, long paramLong)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      CreateDirectory0(localNativeBuffer.address(), paramLong);
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native void CreateDirectory0(long paramLong1, long paramLong2)
    throws WindowsException;
  
  /* Error */
  static void RemoveDirectory(String paramString)
    throws WindowsException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 322	sun/nio/fs/WindowsNativeDispatcher:asNativeBuffer	(Ljava/lang/String;)Lsun/nio/fs/NativeBuffer;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 286	sun/nio/fs/NativeBuffer:address	()J
    //   9: invokestatic 298	sun/nio/fs/WindowsNativeDispatcher:RemoveDirectory0	(J)V
    //   12: aload_1
    //   13: invokevirtual 287	sun/nio/fs/NativeBuffer:release	()V
    //   16: goto +10 -> 26
    //   19: astore_2
    //   20: aload_1
    //   21: invokevirtual 287	sun/nio/fs/NativeBuffer:release	()V
    //   24: aload_2
    //   25: athrow
    //   26: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	paramString	String
    //   4	17	1	localNativeBuffer	NativeBuffer
    //   19	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	12	19	finally
  }
  
  private static native void RemoveDirectory0(long paramLong)
    throws WindowsException;
  
  static native void DeviceIoControlSetSparse(long paramLong)
    throws WindowsException;
  
  static native void DeviceIoControlGetReparsePoint(long paramLong1, long paramLong2, int paramInt)
    throws WindowsException;
  
  static FirstFile FindFirstFile(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      FirstFile localFirstFile1 = new FirstFile(null);
      FindFirstFile0(localNativeBuffer.address(), localFirstFile1);
      FirstFile localFirstFile2 = localFirstFile1;
      return localFirstFile2;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native void FindFirstFile0(long paramLong, FirstFile paramFirstFile)
    throws WindowsException;
  
  static long FindFirstFile(String paramString, long paramLong)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      long l = FindFirstFile1(localNativeBuffer.address(), paramLong);
      return l;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native long FindFirstFile1(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static native String FindNextFile(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static FirstStream FindFirstStream(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      FirstStream localFirstStream1 = new FirstStream(null);
      FindFirstStream0(localNativeBuffer.address(), localFirstStream1);
      if (localFirstStream1.handle() == -1L)
      {
        localFirstStream2 = null;
        return localFirstStream2;
      }
      FirstStream localFirstStream2 = localFirstStream1;
      return localFirstStream2;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native void FindFirstStream0(long paramLong, FirstStream paramFirstStream)
    throws WindowsException;
  
  static native String FindNextStream(long paramLong)
    throws WindowsException;
  
  static native void FindClose(long paramLong)
    throws WindowsException;
  
  static native void GetFileInformationByHandle(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static void CopyFileEx(String paramString1, String paramString2, int paramInt, long paramLong)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
    NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
    try
    {
      CopyFileEx0(localNativeBuffer1.address(), localNativeBuffer2.address(), paramInt, paramLong);
    }
    finally
    {
      localNativeBuffer2.release();
      localNativeBuffer1.release();
    }
  }
  
  private static native void CopyFileEx0(long paramLong1, long paramLong2, int paramInt, long paramLong3)
    throws WindowsException;
  
  static void MoveFileEx(String paramString1, String paramString2, int paramInt)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
    NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
    try
    {
      MoveFileEx0(localNativeBuffer1.address(), localNativeBuffer2.address(), paramInt);
    }
    finally
    {
      localNativeBuffer2.release();
      localNativeBuffer1.release();
    }
  }
  
  private static native void MoveFileEx0(long paramLong1, long paramLong2, int paramInt)
    throws WindowsException;
  
  static int GetFileAttributes(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      int i = GetFileAttributes0(localNativeBuffer.address());
      return i;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native int GetFileAttributes0(long paramLong)
    throws WindowsException;
  
  /* Error */
  static void SetFileAttributes(String paramString, int paramInt)
    throws WindowsException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 322	sun/nio/fs/WindowsNativeDispatcher:asNativeBuffer	(Ljava/lang/String;)Lsun/nio/fs/NativeBuffer;
    //   4: astore_2
    //   5: aload_2
    //   6: invokevirtual 286	sun/nio/fs/NativeBuffer:address	()J
    //   9: iload_1
    //   10: invokestatic 299	sun/nio/fs/WindowsNativeDispatcher:SetFileAttributes0	(JI)V
    //   13: aload_2
    //   14: invokevirtual 287	sun/nio/fs/NativeBuffer:release	()V
    //   17: goto +10 -> 27
    //   20: astore_3
    //   21: aload_2
    //   22: invokevirtual 287	sun/nio/fs/NativeBuffer:release	()V
    //   25: aload_3
    //   26: athrow
    //   27: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	paramString	String
    //   0	28	1	paramInt	int
    //   4	18	2	localNativeBuffer	NativeBuffer
    //   20	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	13	20	finally
  }
  
  private static native void SetFileAttributes0(long paramLong, int paramInt)
    throws WindowsException;
  
  static void GetFileAttributesEx(String paramString, long paramLong)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      GetFileAttributesEx0(localNativeBuffer.address(), paramLong);
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native void GetFileAttributesEx0(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static native void SetFileTime(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    throws WindowsException;
  
  static native void SetEndOfFile(long paramLong)
    throws WindowsException;
  
  static native int GetLogicalDrives()
    throws WindowsException;
  
  static VolumeInformation GetVolumeInformation(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      VolumeInformation localVolumeInformation1 = new VolumeInformation(null);
      GetVolumeInformation0(localNativeBuffer.address(), localVolumeInformation1);
      VolumeInformation localVolumeInformation2 = localVolumeInformation1;
      return localVolumeInformation2;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native void GetVolumeInformation0(long paramLong, VolumeInformation paramVolumeInformation)
    throws WindowsException;
  
  static int GetDriveType(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      int i = GetDriveType0(localNativeBuffer.address());
      return i;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native int GetDriveType0(long paramLong)
    throws WindowsException;
  
  static DiskFreeSpace GetDiskFreeSpaceEx(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      DiskFreeSpace localDiskFreeSpace1 = new DiskFreeSpace(null);
      GetDiskFreeSpaceEx0(localNativeBuffer.address(), localDiskFreeSpace1);
      DiskFreeSpace localDiskFreeSpace2 = localDiskFreeSpace1;
      return localDiskFreeSpace2;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native void GetDiskFreeSpaceEx0(long paramLong, DiskFreeSpace paramDiskFreeSpace)
    throws WindowsException;
  
  static String GetVolumePathName(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      String str = GetVolumePathName0(localNativeBuffer.address());
      return str;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native String GetVolumePathName0(long paramLong)
    throws WindowsException;
  
  static native void InitializeSecurityDescriptor(long paramLong)
    throws WindowsException;
  
  static native void InitializeAcl(long paramLong, int paramInt)
    throws WindowsException;
  
  static int GetFileSecurity(String paramString, int paramInt1, long paramLong, int paramInt2)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      int i = GetFileSecurity0(localNativeBuffer.address(), paramInt1, paramLong, paramInt2);
      return i;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native int GetFileSecurity0(long paramLong1, int paramInt1, long paramLong2, int paramInt2)
    throws WindowsException;
  
  static void SetFileSecurity(String paramString, int paramInt, long paramLong)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      SetFileSecurity0(localNativeBuffer.address(), paramInt, paramLong);
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  static native void SetFileSecurity0(long paramLong1, int paramInt, long paramLong2)
    throws WindowsException;
  
  static native long GetSecurityDescriptorOwner(long paramLong)
    throws WindowsException;
  
  static native void SetSecurityDescriptorOwner(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static native long GetSecurityDescriptorDacl(long paramLong);
  
  static native void SetSecurityDescriptorDacl(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static AclInformation GetAclInformation(long paramLong)
  {
    AclInformation localAclInformation = new AclInformation(null);
    GetAclInformation0(paramLong, localAclInformation);
    return localAclInformation;
  }
  
  private static native void GetAclInformation0(long paramLong, AclInformation paramAclInformation);
  
  static native long GetAce(long paramLong, int paramInt);
  
  static native void AddAccessAllowedAceEx(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
    throws WindowsException;
  
  static native void AddAccessDeniedAceEx(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
    throws WindowsException;
  
  static Account LookupAccountSid(long paramLong)
    throws WindowsException
  {
    Account localAccount = new Account(null);
    LookupAccountSid0(paramLong, localAccount);
    return localAccount;
  }
  
  private static native void LookupAccountSid0(long paramLong, Account paramAccount)
    throws WindowsException;
  
  static int LookupAccountName(String paramString, long paramLong, int paramInt)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      int i = LookupAccountName0(localNativeBuffer.address(), paramLong, paramInt);
      return i;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native int LookupAccountName0(long paramLong1, long paramLong2, int paramInt)
    throws WindowsException;
  
  static native int GetLengthSid(long paramLong);
  
  static native String ConvertSidToStringSid(long paramLong)
    throws WindowsException;
  
  static long ConvertStringSidToSid(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      long l = ConvertStringSidToSid0(localNativeBuffer.address());
      return l;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native long ConvertStringSidToSid0(long paramLong)
    throws WindowsException;
  
  static native long GetCurrentProcess();
  
  static native long GetCurrentThread();
  
  static native long OpenProcessToken(long paramLong, int paramInt)
    throws WindowsException;
  
  static native long OpenThreadToken(long paramLong, int paramInt, boolean paramBoolean)
    throws WindowsException;
  
  static native long DuplicateTokenEx(long paramLong, int paramInt)
    throws WindowsException;
  
  static native void SetThreadToken(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static native int GetTokenInformation(long paramLong1, int paramInt1, long paramLong2, int paramInt2)
    throws WindowsException;
  
  static native void AdjustTokenPrivileges(long paramLong1, long paramLong2, int paramInt)
    throws WindowsException;
  
  static native boolean AccessCheck(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws WindowsException;
  
  static long LookupPrivilegeValue(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      long l = LookupPrivilegeValue0(localNativeBuffer.address());
      return l;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native long LookupPrivilegeValue0(long paramLong)
    throws WindowsException;
  
  static void CreateSymbolicLink(String paramString1, String paramString2, int paramInt)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
    NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
    try
    {
      CreateSymbolicLink0(localNativeBuffer1.address(), localNativeBuffer2.address(), paramInt);
    }
    finally
    {
      localNativeBuffer2.release();
      localNativeBuffer1.release();
    }
  }
  
  private static native void CreateSymbolicLink0(long paramLong1, long paramLong2, int paramInt)
    throws WindowsException;
  
  static void CreateHardLink(String paramString1, String paramString2)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer1 = asNativeBuffer(paramString1);
    NativeBuffer localNativeBuffer2 = asNativeBuffer(paramString2);
    try
    {
      CreateHardLink0(localNativeBuffer1.address(), localNativeBuffer2.address());
    }
    finally
    {
      localNativeBuffer2.release();
      localNativeBuffer1.release();
    }
  }
  
  private static native void CreateHardLink0(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static String GetFullPathName(String paramString)
    throws WindowsException
  {
    NativeBuffer localNativeBuffer = asNativeBuffer(paramString);
    try
    {
      String str = GetFullPathName0(localNativeBuffer.address());
      return str;
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  private static native String GetFullPathName0(long paramLong)
    throws WindowsException;
  
  static native String GetFinalPathNameByHandle(long paramLong)
    throws WindowsException;
  
  static native String FormatMessage(int paramInt);
  
  static native void LocalFree(long paramLong);
  
  static native long CreateIoCompletionPort(long paramLong1, long paramLong2, long paramLong3)
    throws WindowsException;
  
  static CompletionStatus GetQueuedCompletionStatus(long paramLong)
    throws WindowsException
  {
    CompletionStatus localCompletionStatus = new CompletionStatus(null);
    GetQueuedCompletionStatus0(paramLong, localCompletionStatus);
    return localCompletionStatus;
  }
  
  private static native void GetQueuedCompletionStatus0(long paramLong, CompletionStatus paramCompletionStatus)
    throws WindowsException;
  
  static native void PostQueuedCompletionStatus(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static native void ReadDirectoryChangesW(long paramLong1, long paramLong2, int paramInt1, boolean paramBoolean, int paramInt2, long paramLong3, long paramLong4)
    throws WindowsException;
  
  static native void CancelIo(long paramLong)
    throws WindowsException;
  
  static native int GetOverlappedResult(long paramLong1, long paramLong2)
    throws WindowsException;
  
  static BackupResult BackupRead(long paramLong1, long paramLong2, int paramInt, boolean paramBoolean, long paramLong3)
    throws WindowsException
  {
    BackupResult localBackupResult = new BackupResult(null);
    BackupRead0(paramLong1, paramLong2, paramInt, paramBoolean, paramLong3, localBackupResult);
    return localBackupResult;
  }
  
  private static native void BackupRead0(long paramLong1, long paramLong2, int paramInt, boolean paramBoolean, long paramLong3, BackupResult paramBackupResult)
    throws WindowsException;
  
  static native void BackupSeek(long paramLong1, long paramLong2, long paramLong3)
    throws WindowsException;
  
  static NativeBuffer asNativeBuffer(String paramString)
  {
    int i = paramString.length() << 1;
    int j = i + 2;
    NativeBuffer localNativeBuffer = NativeBuffers.getNativeBufferFromCache(j);
    if (localNativeBuffer == null) {
      localNativeBuffer = NativeBuffers.allocNativeBuffer(j);
    } else if (localNativeBuffer.owner() == paramString) {
      return localNativeBuffer;
    }
    char[] arrayOfChar = paramString.toCharArray();
    unsafe.copyMemory(arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, null, localNativeBuffer.address(), i);
    unsafe.putChar(localNativeBuffer.address() + i, '\000');
    localNativeBuffer.setOwner(paramString);
    return localNativeBuffer;
  }
  
  private static native void initIDs();
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        System.loadLibrary("nio");
        return null;
      }
    });
    initIDs();
  }
  
  static class Account
  {
    private String domain;
    private String name;
    private int use;
    
    private Account() {}
    
    public String domain()
    {
      return domain;
    }
    
    public String name()
    {
      return name;
    }
    
    public int use()
    {
      return use;
    }
  }
  
  static class AclInformation
  {
    private int aceCount;
    
    private AclInformation() {}
    
    public int aceCount()
    {
      return aceCount;
    }
  }
  
  static class BackupResult
  {
    private int bytesTransferred;
    private long context;
    
    private BackupResult() {}
    
    int bytesTransferred()
    {
      return bytesTransferred;
    }
    
    long context()
    {
      return context;
    }
  }
  
  static class CompletionStatus
  {
    private int error;
    private int bytesTransferred;
    private long completionKey;
    
    private CompletionStatus() {}
    
    int error()
    {
      return error;
    }
    
    int bytesTransferred()
    {
      return bytesTransferred;
    }
    
    long completionKey()
    {
      return completionKey;
    }
  }
  
  static class DiskFreeSpace
  {
    private long freeBytesAvailable;
    private long totalNumberOfBytes;
    private long totalNumberOfFreeBytes;
    
    private DiskFreeSpace() {}
    
    public long freeBytesAvailable()
    {
      return freeBytesAvailable;
    }
    
    public long totalNumberOfBytes()
    {
      return totalNumberOfBytes;
    }
    
    public long totalNumberOfFreeBytes()
    {
      return totalNumberOfFreeBytes;
    }
  }
  
  static class FirstFile
  {
    private long handle;
    private String name;
    private int attributes;
    
    private FirstFile() {}
    
    public long handle()
    {
      return handle;
    }
    
    public String name()
    {
      return name;
    }
    
    public int attributes()
    {
      return attributes;
    }
  }
  
  static class FirstStream
  {
    private long handle;
    private String name;
    
    private FirstStream() {}
    
    public long handle()
    {
      return handle;
    }
    
    public String name()
    {
      return name;
    }
  }
  
  static class VolumeInformation
  {
    private String fileSystemName;
    private String volumeName;
    private int volumeSerialNumber;
    private int flags;
    
    private VolumeInformation() {}
    
    public String fileSystemName()
    {
      return fileSystemName;
    }
    
    public String volumeName()
    {
      return volumeName;
    }
    
    public int volumeSerialNumber()
    {
      return volumeSerialNumber;
    }
    
    public int flags()
    {
      return flags;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsNativeDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */