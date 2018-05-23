package org.w3c.dom;

public abstract interface UserDataHandler
{
  public static final short NODE_CLONED = 1;
  public static final short NODE_IMPORTED = 2;
  public static final short NODE_DELETED = 3;
  public static final short NODE_RENAMED = 4;
  public static final short NODE_ADOPTED = 5;
  
  public abstract void handle(short paramShort, String paramString, Object paramObject, Node paramNode1, Node paramNode2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\UserDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */