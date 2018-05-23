package java.nio.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

class CopyMoveHelper
{
  private CopyMoveHelper() {}
  
  private static CopyOption[] convertMoveToCopyOptions(CopyOption... paramVarArgs)
    throws AtomicMoveNotSupportedException
  {
    int i = paramVarArgs.length;
    CopyOption[] arrayOfCopyOption = new CopyOption[i + 2];
    for (int j = 0; j < i; j++)
    {
      CopyOption localCopyOption = paramVarArgs[j];
      if (localCopyOption == StandardCopyOption.ATOMIC_MOVE) {
        throw new AtomicMoveNotSupportedException(null, null, "Atomic move between providers is not supported");
      }
      arrayOfCopyOption[j] = localCopyOption;
    }
    arrayOfCopyOption[i] = LinkOption.NOFOLLOW_LINKS;
    arrayOfCopyOption[(i + 1)] = StandardCopyOption.COPY_ATTRIBUTES;
    return arrayOfCopyOption;
  }
  
  static void copyToForeignTarget(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
    throws IOException
  {
    CopyOptions localCopyOptions = CopyOptions.parse(paramVarArgs);
    LinkOption[] arrayOfLinkOption = { followLinks ? new LinkOption[0] : LinkOption.NOFOLLOW_LINKS };
    BasicFileAttributes localBasicFileAttributes = Files.readAttributes(paramPath1, BasicFileAttributes.class, arrayOfLinkOption);
    if (localBasicFileAttributes.isSymbolicLink()) {
      throw new IOException("Copying of symbolic links not supported");
    }
    if (replaceExisting) {
      Files.deleteIfExists(paramPath2);
    } else if (Files.exists(paramPath2, new LinkOption[0])) {
      throw new FileAlreadyExistsException(paramPath2.toString());
    }
    Object localObject1;
    if (localBasicFileAttributes.isDirectory())
    {
      Files.createDirectory(paramPath2, new FileAttribute[0]);
    }
    else
    {
      localObject1 = Files.newInputStream(paramPath1, new OpenOption[0]);
      Object localObject2 = null;
      try
      {
        Files.copy((InputStream)localObject1, paramPath2, new CopyOption[0]);
      }
      catch (Throwable localThrowable3)
      {
        localObject2 = localThrowable3;
        throw localThrowable3;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((InputStream)localObject1).close();
            }
            catch (Throwable localThrowable5)
            {
              ((Throwable)localObject2).addSuppressed(localThrowable5);
            }
          } else {
            ((InputStream)localObject1).close();
          }
        }
      }
    }
    if (copyAttributes)
    {
      localObject1 = (BasicFileAttributeView)Files.getFileAttributeView(paramPath2, BasicFileAttributeView.class, new LinkOption[0]);
      try
      {
        ((BasicFileAttributeView)localObject1).setTimes(localBasicFileAttributes.lastModifiedTime(), localBasicFileAttributes.lastAccessTime(), localBasicFileAttributes.creationTime());
      }
      catch (Throwable localThrowable1)
      {
        try
        {
          Files.delete(paramPath2);
        }
        catch (Throwable localThrowable4)
        {
          localThrowable1.addSuppressed(localThrowable4);
        }
        throw localThrowable1;
      }
    }
  }
  
  static void moveToForeignTarget(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs)
    throws IOException
  {
    copyToForeignTarget(paramPath1, paramPath2, convertMoveToCopyOptions(paramVarArgs));
    Files.delete(paramPath1);
  }
  
  private static class CopyOptions
  {
    boolean replaceExisting = false;
    boolean copyAttributes = false;
    boolean followLinks = true;
    
    private CopyOptions() {}
    
    static CopyOptions parse(CopyOption... paramVarArgs)
    {
      CopyOptions localCopyOptions = new CopyOptions();
      for (CopyOption localCopyOption : paramVarArgs) {
        if (localCopyOption == StandardCopyOption.REPLACE_EXISTING)
        {
          replaceExisting = true;
        }
        else if (localCopyOption == LinkOption.NOFOLLOW_LINKS)
        {
          followLinks = false;
        }
        else if (localCopyOption == StandardCopyOption.COPY_ATTRIBUTES)
        {
          copyAttributes = true;
        }
        else
        {
          if (localCopyOption == null) {
            throw new NullPointerException();
          }
          throw new UnsupportedOperationException("'" + localCopyOption + "' is not a recognized copy option");
        }
      }
      return localCopyOptions;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\CopyMoveHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */