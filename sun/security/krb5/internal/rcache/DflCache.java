package sun.security.krb5.internal.rcache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.ReplayCache;

public class DflCache
  extends ReplayCache
{
  private static final int KRB5_RV_VNO = 1281;
  private static final int EXCESSREPS = 30;
  private final String source;
  private static int uid;
  
  public DflCache(String paramString)
  {
    source = paramString;
  }
  
  private static String defaultPath()
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir"));
  }
  
  private static String defaultFile(String paramString)
  {
    int i = paramString.indexOf('/');
    if (i == -1) {
      i = paramString.indexOf('@');
    }
    if (i != -1) {
      paramString = paramString.substring(0, i);
    }
    if (uid != -1) {
      paramString = paramString + "_" + uid;
    }
    return paramString;
  }
  
  private static Path getFileName(String paramString1, String paramString2)
  {
    String str1;
    String str2;
    if (paramString1.equals("dfl"))
    {
      str1 = defaultPath();
      str2 = defaultFile(paramString2);
    }
    else if (paramString1.startsWith("dfl:"))
    {
      paramString1 = paramString1.substring(4);
      int i = paramString1.lastIndexOf('/');
      int j = paramString1.lastIndexOf('\\');
      if (j > i) {
        i = j;
      }
      if (i == -1)
      {
        str1 = defaultPath();
        str2 = paramString1;
      }
      else if (new File(paramString1).isDirectory())
      {
        str1 = paramString1;
        str2 = defaultFile(paramString2);
      }
      else
      {
        str1 = null;
        str2 = paramString1;
      }
    }
    else
    {
      throw new IllegalArgumentException();
    }
    return new File(str1, str2).toPath();
  }
  
  public void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash)
    throws KrbApErrException
  {
    try
    {
      checkAndStore0(paramKerberosTime, paramAuthTimeWithHash);
    }
    catch (IOException localIOException)
    {
      KrbApErrException localKrbApErrException = new KrbApErrException(60);
      localKrbApErrException.initCause(localIOException);
      throw localKrbApErrException;
    }
  }
  
  private synchronized void checkAndStore0(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash)
    throws IOException, KrbApErrException
  {
    Path localPath = getFileName(source, server);
    int i = 0;
    Storage localStorage = new Storage(null);
    Object localObject1 = null;
    try
    {
      try
      {
        i = localStorage.loadAndCheck(localPath, paramAuthTimeWithHash, paramKerberosTime);
      }
      catch (IOException localIOException)
      {
        Storage.create(localPath);
        i = localStorage.loadAndCheck(localPath, paramAuthTimeWithHash, paramKerberosTime);
      }
      localStorage.append(paramAuthTimeWithHash);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localStorage != null) {
        if (localObject1 != null) {
          try
          {
            localStorage.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localStorage.close();
        }
      }
    }
    if (i > 30) {
      Storage.expunge(localPath, paramKerberosTime);
    }
  }
  
  static
  {
    try
    {
      Class localClass = Class.forName("com.sun.security.auth.module.UnixSystem");
      uid = (int)((Long)localClass.getMethod("getUid", new Class[0]).invoke(localClass.newInstance(), new Object[0])).longValue();
    }
    catch (Exception localException)
    {
      uid = -1;
    }
  }
  
  private static class Storage
    implements Closeable
  {
    SeekableByteChannel chan;
    
    private Storage() {}
    
    private static void create(Path paramPath)
      throws IOException
    {
      SeekableByteChannel localSeekableByteChannel = createNoClose(paramPath);
      Object localObject = null;
      if (localSeekableByteChannel != null) {
        if (localObject != null) {
          try
          {
            localSeekableByteChannel.close();
          }
          catch (Throwable localThrowable)
          {
            ((Throwable)localObject).addSuppressed(localThrowable);
          }
        } else {
          localSeekableByteChannel.close();
        }
      }
      makeMine(paramPath);
    }
    
    private static void makeMine(Path paramPath)
      throws IOException
    {
      try
      {
        HashSet localHashSet = new HashSet();
        localHashSet.add(PosixFilePermission.OWNER_READ);
        localHashSet.add(PosixFilePermission.OWNER_WRITE);
        Files.setPosixFilePermissions(paramPath, localHashSet);
      }
      catch (UnsupportedOperationException localUnsupportedOperationException) {}
    }
    
    private static SeekableByteChannel createNoClose(Path paramPath)
      throws IOException
    {
      SeekableByteChannel localSeekableByteChannel = Files.newByteChannel(paramPath, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE });
      ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
      localByteBuffer.putShort((short)1281);
      localByteBuffer.order(ByteOrder.nativeOrder());
      localByteBuffer.putInt(KerberosTime.getDefaultSkew());
      localByteBuffer.flip();
      localSeekableByteChannel.write(localByteBuffer);
      return localSeekableByteChannel;
    }
    
    private static void expunge(Path paramPath, KerberosTime paramKerberosTime)
      throws IOException
    {
      Path localPath = Files.createTempFile(paramPath.getParent(), "rcache", null, new FileAttribute[0]);
      SeekableByteChannel localSeekableByteChannel1 = Files.newByteChannel(paramPath, new OpenOption[0]);
      Object localObject1 = null;
      try
      {
        SeekableByteChannel localSeekableByteChannel2 = createNoClose(localPath);
        Object localObject2 = null;
        try
        {
          long l = paramKerberosTime.getSeconds() - readHeader(localSeekableByteChannel1);
          try
          {
            for (;;)
            {
              AuthTime localAuthTime = AuthTime.readFrom(localSeekableByteChannel1);
              if (ctime > l)
              {
                ByteBuffer localByteBuffer = ByteBuffer.wrap(localAuthTime.encode(true));
                localSeekableByteChannel2.write(localByteBuffer);
              }
            }
            if (localObject2 == null) {
              break label129;
            }
          }
          catch (BufferUnderflowException localBufferUnderflowException)
          {
            if (localSeekableByteChannel2 == null) {
              break label192;
            }
          }
          try
          {
            localSeekableByteChannel2.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject2).addSuppressed(localThrowable3);
          }
          label129:
          localSeekableByteChannel2.close();
        }
        catch (Throwable localThrowable4)
        {
          localObject2 = localThrowable4;
          throw localThrowable4;
        }
        finally {}
      }
      catch (Throwable localThrowable2)
      {
        label192:
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localSeekableByteChannel1 != null) {
          if (localObject1 != null) {
            try
            {
              localSeekableByteChannel1.close();
            }
            catch (Throwable localThrowable6)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable6);
            }
          } else {
            localSeekableByteChannel1.close();
          }
        }
      }
      makeMine(localPath);
      Files.move(localPath, paramPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE });
    }
    
    private int loadAndCheck(Path paramPath, AuthTimeWithHash paramAuthTimeWithHash, KerberosTime paramKerberosTime)
      throws IOException, KrbApErrException
    {
      i = 0;
      if (Files.isSymbolicLink(paramPath)) {
        throw new IOException("Symlink not accepted");
      }
      try
      {
        Set localSet = Files.getPosixFilePermissions(paramPath, new LinkOption[0]);
        if ((DflCache.uid != -1) && (((Integer)Files.getAttribute(paramPath, "unix:uid", new LinkOption[0])).intValue() != DflCache.uid)) {
          throw new IOException("Not mine");
        }
        if ((localSet.contains(PosixFilePermission.GROUP_READ)) || (localSet.contains(PosixFilePermission.GROUP_WRITE)) || (localSet.contains(PosixFilePermission.GROUP_EXECUTE)) || (localSet.contains(PosixFilePermission.OTHERS_READ)) || (localSet.contains(PosixFilePermission.OTHERS_WRITE)) || (localSet.contains(PosixFilePermission.OTHERS_EXECUTE))) {
          throw new IOException("Accessible by someone else");
        }
      }
      catch (UnsupportedOperationException localUnsupportedOperationException) {}
      chan = Files.newByteChannel(paramPath, new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.READ });
      long l1 = paramKerberosTime.getSeconds() - readHeader(chan);
      long l2 = 0L;
      int j = 0;
      try
      {
        for (;;)
        {
          l2 = chan.position();
          AuthTime localAuthTime = AuthTime.readFrom(chan);
          if ((localAuthTime instanceof AuthTimeWithHash))
          {
            if (paramAuthTimeWithHash.equals(localAuthTime)) {
              throw new KrbApErrException(34);
            }
            if (paramAuthTimeWithHash.isSameIgnoresHash(localAuthTime)) {
              j = 1;
            }
          }
          else if ((paramAuthTimeWithHash.isSameIgnoresHash(localAuthTime)) && (j == 0))
          {
            throw new KrbApErrException(34);
          }
          if (ctime < l1) {
            i++;
          } else {
            i--;
          }
        }
        return i;
      }
      catch (BufferUnderflowException localBufferUnderflowException)
      {
        chan.position(l2);
      }
    }
    
    private static int readHeader(SeekableByteChannel paramSeekableByteChannel)
      throws IOException
    {
      ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
      paramSeekableByteChannel.read(localByteBuffer);
      if (localByteBuffer.getShort(0) != 1281) {
        throw new IOException("Not correct rcache version");
      }
      localByteBuffer.order(ByteOrder.nativeOrder());
      return localByteBuffer.getInt(2);
    }
    
    private void append(AuthTimeWithHash paramAuthTimeWithHash)
      throws IOException
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(paramAuthTimeWithHash.encode(true));
      chan.write(localByteBuffer);
      localByteBuffer = ByteBuffer.wrap(paramAuthTimeWithHash.encode(false));
      chan.write(localByteBuffer);
    }
    
    public void close()
      throws IOException
    {
      if (chan != null) {
        chan.close();
      }
      chan = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\rcache\DflCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */