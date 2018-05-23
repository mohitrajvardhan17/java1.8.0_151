package sun.nio.ch;

class OptionKey
{
  private int level;
  private int name;
  
  OptionKey(int paramInt1, int paramInt2)
  {
    level = paramInt1;
    name = paramInt2;
  }
  
  int level()
  {
    return level;
  }
  
  int name()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\OptionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */