package java.lang.management;

public enum MemoryType
{
  HEAP("Heap memory"),  NON_HEAP("Non-heap memory");
  
  private final String description;
  private static final long serialVersionUID = 6992337162326171013L;
  
  private MemoryType(String paramString)
  {
    description = paramString;
  }
  
  public String toString()
  {
    return description;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\MemoryType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */