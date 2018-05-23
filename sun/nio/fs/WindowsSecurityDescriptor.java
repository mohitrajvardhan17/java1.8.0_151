package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntry.Builder;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.misc.Unsafe;

class WindowsSecurityDescriptor
{
  private static final Unsafe unsafe = ;
  private static final short SIZEOF_ACL = 8;
  private static final short SIZEOF_ACCESS_ALLOWED_ACE = 12;
  private static final short SIZEOF_ACCESS_DENIED_ACE = 12;
  private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
  private static final short OFFSETOF_TYPE = 0;
  private static final short OFFSETOF_FLAGS = 1;
  private static final short OFFSETOF_ACCESS_MASK = 4;
  private static final short OFFSETOF_SID = 8;
  private static final WindowsSecurityDescriptor NULL_DESCRIPTOR = new WindowsSecurityDescriptor();
  private final List<Long> sidList;
  private final NativeBuffer aclBuffer;
  private final NativeBuffer sdBuffer;
  
  private WindowsSecurityDescriptor()
  {
    sidList = null;
    aclBuffer = null;
    sdBuffer = null;
  }
  
  private WindowsSecurityDescriptor(List<AclEntry> paramList)
    throws IOException
  {
    int i = 0;
    paramList = new ArrayList(paramList);
    sidList = new ArrayList(paramList.size());
    try
    {
      int j = 8;
      Iterator localIterator = paramList.iterator();
      AclEntry localAclEntry;
      while (localIterator.hasNext())
      {
        localAclEntry = (AclEntry)localIterator.next();
        UserPrincipal localUserPrincipal = localAclEntry.principal();
        if (!(localUserPrincipal instanceof WindowsUserPrincipals.User)) {
          throw new ProviderMismatchException();
        }
        String str = ((WindowsUserPrincipals.User)localUserPrincipal).sidString();
        try
        {
          long l2 = WindowsNativeDispatcher.ConvertStringSidToSid(str);
          sidList.add(Long.valueOf(l2));
          j += WindowsNativeDispatcher.GetLengthSid(l2) + Math.max(12, 12);
        }
        catch (WindowsException localWindowsException2)
        {
          throw new IOException("Failed to get SID for " + localUserPrincipal.getName() + ": " + localWindowsException2.errorString());
        }
      }
      aclBuffer = NativeBuffers.getNativeBuffer(j);
      sdBuffer = NativeBuffers.getNativeBuffer(20);
      WindowsNativeDispatcher.InitializeAcl(aclBuffer.address(), j);
      for (int k = 0; k < paramList.size(); k++)
      {
        localAclEntry = (AclEntry)paramList.get(k);
        long l1 = ((Long)sidList.get(k)).longValue();
        try
        {
          encode(localAclEntry, l1, aclBuffer.address());
        }
        catch (WindowsException localWindowsException3)
        {
          throw new IOException("Failed to encode ACE: " + localWindowsException3.errorString());
        }
      }
      WindowsNativeDispatcher.InitializeSecurityDescriptor(sdBuffer.address());
      WindowsNativeDispatcher.SetSecurityDescriptorDacl(sdBuffer.address(), aclBuffer.address());
      i = 1;
    }
    catch (WindowsException localWindowsException1)
    {
      throw new IOException(localWindowsException1.getMessage());
    }
    finally
    {
      if (i == 0) {
        release();
      }
    }
  }
  
  void release()
  {
    if (sdBuffer != null) {
      sdBuffer.release();
    }
    if (aclBuffer != null) {
      aclBuffer.release();
    }
    if (sidList != null)
    {
      Iterator localIterator = sidList.iterator();
      while (localIterator.hasNext())
      {
        Long localLong = (Long)localIterator.next();
        WindowsNativeDispatcher.LocalFree(localLong.longValue());
      }
    }
  }
  
  long address()
  {
    return sdBuffer == null ? 0L : sdBuffer.address();
  }
  
  private static AclEntry decode(long paramLong)
    throws IOException
  {
    int i = unsafe.getByte(paramLong + 0L);
    if ((i != 0) && (i != 1)) {
      return null;
    }
    AclEntryType localAclEntryType;
    if (i == 0) {
      localAclEntryType = AclEntryType.ALLOW;
    } else {
      localAclEntryType = AclEntryType.DENY;
    }
    int j = unsafe.getByte(paramLong + 1L);
    EnumSet localEnumSet1 = EnumSet.noneOf(AclEntryFlag.class);
    if ((j & 0x1) != 0) {
      localEnumSet1.add(AclEntryFlag.FILE_INHERIT);
    }
    if ((j & 0x2) != 0) {
      localEnumSet1.add(AclEntryFlag.DIRECTORY_INHERIT);
    }
    if ((j & 0x4) != 0) {
      localEnumSet1.add(AclEntryFlag.NO_PROPAGATE_INHERIT);
    }
    if ((j & 0x8) != 0) {
      localEnumSet1.add(AclEntryFlag.INHERIT_ONLY);
    }
    int k = unsafe.getInt(paramLong + 4L);
    EnumSet localEnumSet2 = EnumSet.noneOf(AclEntryPermission.class);
    if ((k & 0x1) > 0) {
      localEnumSet2.add(AclEntryPermission.READ_DATA);
    }
    if ((k & 0x2) > 0) {
      localEnumSet2.add(AclEntryPermission.WRITE_DATA);
    }
    if ((k & 0x4) > 0) {
      localEnumSet2.add(AclEntryPermission.APPEND_DATA);
    }
    if ((k & 0x8) > 0) {
      localEnumSet2.add(AclEntryPermission.READ_NAMED_ATTRS);
    }
    if ((k & 0x10) > 0) {
      localEnumSet2.add(AclEntryPermission.WRITE_NAMED_ATTRS);
    }
    if ((k & 0x20) > 0) {
      localEnumSet2.add(AclEntryPermission.EXECUTE);
    }
    if ((k & 0x40) > 0) {
      localEnumSet2.add(AclEntryPermission.DELETE_CHILD);
    }
    if ((k & 0x80) > 0) {
      localEnumSet2.add(AclEntryPermission.READ_ATTRIBUTES);
    }
    if ((k & 0x100) > 0) {
      localEnumSet2.add(AclEntryPermission.WRITE_ATTRIBUTES);
    }
    if ((k & 0x10000) > 0) {
      localEnumSet2.add(AclEntryPermission.DELETE);
    }
    if ((k & 0x20000) > 0) {
      localEnumSet2.add(AclEntryPermission.READ_ACL);
    }
    if ((k & 0x40000) > 0) {
      localEnumSet2.add(AclEntryPermission.WRITE_ACL);
    }
    if ((k & 0x80000) > 0) {
      localEnumSet2.add(AclEntryPermission.WRITE_OWNER);
    }
    if ((k & 0x100000) > 0) {
      localEnumSet2.add(AclEntryPermission.SYNCHRONIZE);
    }
    long l = paramLong + 8L;
    UserPrincipal localUserPrincipal = WindowsUserPrincipals.fromSid(l);
    return AclEntry.newBuilder().setType(localAclEntryType).setPrincipal(localUserPrincipal).setFlags(localEnumSet1).setPermissions(localEnumSet2).build();
  }
  
  private static void encode(AclEntry paramAclEntry, long paramLong1, long paramLong2)
    throws WindowsException
  {
    if ((paramAclEntry.type() != AclEntryType.ALLOW) && (paramAclEntry.type() != AclEntryType.DENY)) {
      return;
    }
    int i = paramAclEntry.type() == AclEntryType.ALLOW ? 1 : 0;
    Set localSet1 = paramAclEntry.permissions();
    int j = 0;
    if (localSet1.contains(AclEntryPermission.READ_DATA)) {
      j |= 0x1;
    }
    if (localSet1.contains(AclEntryPermission.WRITE_DATA)) {
      j |= 0x2;
    }
    if (localSet1.contains(AclEntryPermission.APPEND_DATA)) {
      j |= 0x4;
    }
    if (localSet1.contains(AclEntryPermission.READ_NAMED_ATTRS)) {
      j |= 0x8;
    }
    if (localSet1.contains(AclEntryPermission.WRITE_NAMED_ATTRS)) {
      j |= 0x10;
    }
    if (localSet1.contains(AclEntryPermission.EXECUTE)) {
      j |= 0x20;
    }
    if (localSet1.contains(AclEntryPermission.DELETE_CHILD)) {
      j |= 0x40;
    }
    if (localSet1.contains(AclEntryPermission.READ_ATTRIBUTES)) {
      j |= 0x80;
    }
    if (localSet1.contains(AclEntryPermission.WRITE_ATTRIBUTES)) {
      j |= 0x100;
    }
    if (localSet1.contains(AclEntryPermission.DELETE)) {
      j |= 0x10000;
    }
    if (localSet1.contains(AclEntryPermission.READ_ACL)) {
      j |= 0x20000;
    }
    if (localSet1.contains(AclEntryPermission.WRITE_ACL)) {
      j |= 0x40000;
    }
    if (localSet1.contains(AclEntryPermission.WRITE_OWNER)) {
      j |= 0x80000;
    }
    if (localSet1.contains(AclEntryPermission.SYNCHRONIZE)) {
      j |= 0x100000;
    }
    Set localSet2 = paramAclEntry.flags();
    int k = 0;
    if (localSet2.contains(AclEntryFlag.FILE_INHERIT)) {
      k = (byte)(k | 0x1);
    }
    if (localSet2.contains(AclEntryFlag.DIRECTORY_INHERIT)) {
      k = (byte)(k | 0x2);
    }
    if (localSet2.contains(AclEntryFlag.NO_PROPAGATE_INHERIT)) {
      k = (byte)(k | 0x4);
    }
    if (localSet2.contains(AclEntryFlag.INHERIT_ONLY)) {
      k = (byte)(k | 0x8);
    }
    if (i != 0) {
      WindowsNativeDispatcher.AddAccessAllowedAceEx(paramLong2, k, j, paramLong1);
    } else {
      WindowsNativeDispatcher.AddAccessDeniedAceEx(paramLong2, k, j, paramLong1);
    }
  }
  
  static WindowsSecurityDescriptor create(List<AclEntry> paramList)
    throws IOException
  {
    return new WindowsSecurityDescriptor(paramList);
  }
  
  static WindowsSecurityDescriptor fromAttribute(FileAttribute<?>... paramVarArgs)
    throws IOException
  {
    WindowsSecurityDescriptor localWindowsSecurityDescriptor = NULL_DESCRIPTOR;
    for (FileAttribute<?> localFileAttribute : paramVarArgs)
    {
      if (localWindowsSecurityDescriptor != NULL_DESCRIPTOR) {
        localWindowsSecurityDescriptor.release();
      }
      if (localFileAttribute == null) {
        throw new NullPointerException();
      }
      if (localFileAttribute.name().equals("acl:acl"))
      {
        List localList = (List)localFileAttribute.value();
        localWindowsSecurityDescriptor = new WindowsSecurityDescriptor(localList);
      }
      else
      {
        throw new UnsupportedOperationException("'" + localFileAttribute.name() + "' not supported as initial attribute");
      }
    }
    return localWindowsSecurityDescriptor;
  }
  
  static List<AclEntry> getAcl(long paramLong)
    throws IOException
  {
    long l1 = WindowsNativeDispatcher.GetSecurityDescriptorDacl(paramLong);
    int i = 0;
    if (l1 == 0L)
    {
      i = 0;
    }
    else
    {
      localObject = WindowsNativeDispatcher.GetAclInformation(l1);
      i = ((WindowsNativeDispatcher.AclInformation)localObject).aceCount();
    }
    Object localObject = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      long l2 = WindowsNativeDispatcher.GetAce(l1, j);
      AclEntry localAclEntry = decode(l2);
      if (localAclEntry != null) {
        ((ArrayList)localObject).add(localAclEntry);
      }
    }
    return (List<AclEntry>)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsSecurityDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */