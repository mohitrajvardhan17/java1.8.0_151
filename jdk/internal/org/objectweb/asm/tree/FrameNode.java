package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class FrameNode
  extends AbstractInsnNode
{
  public int type;
  public List<Object> local;
  public List<Object> stack;
  
  private FrameNode()
  {
    super(-1);
  }
  
  public FrameNode(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    super(-1);
    type = paramInt1;
    switch (paramInt1)
    {
    case -1: 
    case 0: 
      local = asList(paramInt2, paramArrayOfObject1);
      stack = asList(paramInt3, paramArrayOfObject2);
      break;
    case 1: 
      local = asList(paramInt2, paramArrayOfObject1);
      break;
    case 2: 
      local = Arrays.asList(new Object[paramInt2]);
      break;
    case 3: 
      break;
    case 4: 
      stack = asList(1, paramArrayOfObject2);
    }
  }
  
  public int getType()
  {
    return 14;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    switch (type)
    {
    case -1: 
    case 0: 
      paramMethodVisitor.visitFrame(type, local.size(), asArray(local), stack.size(), asArray(stack));
      break;
    case 1: 
      paramMethodVisitor.visitFrame(type, local.size(), asArray(local), 0, null);
      break;
    case 2: 
      paramMethodVisitor.visitFrame(type, local.size(), null, 0, null);
      break;
    case 3: 
      paramMethodVisitor.visitFrame(type, 0, null, 0, null);
      break;
    case 4: 
      paramMethodVisitor.visitFrame(type, 0, null, 1, asArray(stack));
    }
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    FrameNode localFrameNode = new FrameNode();
    type = type;
    int i;
    Object localObject;
    if (local != null)
    {
      local = new ArrayList();
      for (i = 0; i < local.size(); i++)
      {
        localObject = local.get(i);
        if ((localObject instanceof LabelNode)) {
          localObject = paramMap.get(localObject);
        }
        local.add(localObject);
      }
    }
    if (stack != null)
    {
      stack = new ArrayList();
      for (i = 0; i < stack.size(); i++)
      {
        localObject = stack.get(i);
        if ((localObject instanceof LabelNode)) {
          localObject = paramMap.get(localObject);
        }
        stack.add(localObject);
      }
    }
    return localFrameNode;
  }
  
  private static List<Object> asList(int paramInt, Object[] paramArrayOfObject)
  {
    return Arrays.asList(paramArrayOfObject).subList(0, paramInt);
  }
  
  private static Object[] asArray(List<Object> paramList)
  {
    Object[] arrayOfObject = new Object[paramList.size()];
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      Object localObject = paramList.get(i);
      if ((localObject instanceof LabelNode)) {
        localObject = ((LabelNode)localObject).getLabel();
      }
      arrayOfObject[i] = localObject;
    }
    return arrayOfObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\FrameNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */