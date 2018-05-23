package java.awt;

public abstract interface Transparency
{
  public static final int OPAQUE = 1;
  public static final int BITMASK = 2;
  public static final int TRANSLUCENT = 3;
  
  public abstract int getTransparency();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Transparency.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */