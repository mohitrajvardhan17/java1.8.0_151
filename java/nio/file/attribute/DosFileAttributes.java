package java.nio.file.attribute;

public abstract interface DosFileAttributes
  extends BasicFileAttributes
{
  public abstract boolean isReadOnly();
  
  public abstract boolean isHidden();
  
  public abstract boolean isArchive();
  
  public abstract boolean isSystem();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\DosFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */