package com.sun.java_cup.internal.runtime;

import java.util.Stack;

public class virtual_parse_stack
{
  protected Stack real_stack;
  protected int real_next;
  protected Stack vstack;
  
  public virtual_parse_stack(Stack paramStack)
    throws Exception
  {
    if (paramStack == null) {
      throw new Exception("Internal parser error: attempt to create null virtual stack");
    }
    real_stack = paramStack;
    vstack = new Stack();
    real_next = 0;
    get_from_real();
  }
  
  protected void get_from_real()
  {
    if (real_next >= real_stack.size()) {
      return;
    }
    Symbol localSymbol = (Symbol)real_stack.elementAt(real_stack.size() - 1 - real_next);
    real_next += 1;
    vstack.push(new Integer(parse_state));
  }
  
  public boolean empty()
  {
    return vstack.empty();
  }
  
  public int top()
    throws Exception
  {
    if (vstack.empty()) {
      throw new Exception("Internal parser error: top() called on empty virtual stack");
    }
    return ((Integer)vstack.peek()).intValue();
  }
  
  public void pop()
    throws Exception
  {
    if (vstack.empty()) {
      throw new Exception("Internal parser error: pop from empty virtual stack");
    }
    vstack.pop();
    if (vstack.empty()) {
      get_from_real();
    }
  }
  
  public void push(int paramInt)
  {
    vstack.push(new Integer(paramInt));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java_cup\internal\runtime\virtual_parse_stack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */