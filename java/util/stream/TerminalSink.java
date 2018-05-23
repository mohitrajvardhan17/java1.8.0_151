package java.util.stream;

import java.util.function.Supplier;

abstract interface TerminalSink<T, R>
  extends Sink<T>, Supplier<R>
{}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\stream\TerminalSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */