package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import jdk.internal.org.objectweb.asm.tree.LabelNode;

class Subroutine
{
  LabelNode start;
  boolean[] access;
  List<JumpInsnNode> callers;
  
  private Subroutine() {}
  
  Subroutine(LabelNode paramLabelNode, int paramInt, JumpInsnNode paramJumpInsnNode)
  {
    start = paramLabelNode;
    access = new boolean[paramInt];
    callers = new ArrayList();
    callers.add(paramJumpInsnNode);
  }
  
  public Subroutine copy()
  {
    Subroutine localSubroutine = new Subroutine();
    start = start;
    access = new boolean[access.length];
    System.arraycopy(access, 0, access, 0, access.length);
    callers = new ArrayList(callers);
    return localSubroutine;
  }
  
  public boolean merge(Subroutine paramSubroutine)
    throws AnalyzerException
  {
    boolean bool = false;
    for (int i = 0; i < access.length; i++) {
      if ((access[i] != 0) && (access[i] == 0))
      {
        access[i] = true;
        bool = true;
      }
    }
    if (start == start) {
      for (i = 0; i < callers.size(); i++)
      {
        JumpInsnNode localJumpInsnNode = (JumpInsnNode)callers.get(i);
        if (!callers.contains(localJumpInsnNode))
        {
          callers.add(localJumpInsnNode);
          bool = true;
        }
      }
    }
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\Subroutine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */