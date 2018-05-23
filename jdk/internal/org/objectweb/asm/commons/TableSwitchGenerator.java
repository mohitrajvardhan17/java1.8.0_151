package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Label;

public abstract interface TableSwitchGenerator
{
  public abstract void generateCase(int paramInt, Label paramLabel);
  
  public abstract void generateDefault();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\TableSwitchGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */