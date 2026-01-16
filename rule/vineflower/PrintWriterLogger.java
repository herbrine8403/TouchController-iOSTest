package top.fifthlight.fabazel.decompiler;

import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.util.TextUtil;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintWriterLogger extends IFernflowerLogger {
    private final PrintWriter writer;
    private final AtomicInteger indent;

    public PrintWriterLogger(PrintWriter printWriter) {
        writer = printWriter;
        indent = new AtomicInteger(0);
    }

    @Override
    public void writeMessage(String message, Severity severity) {
        if (accepts(severity)) {
            writer.println(severity.prefix + TextUtil.getIndentString(indent.get()) + message);
        }
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        if (accepts(severity)) {
            writeMessage(message, severity);
            t.printStackTrace(writer);
        }
    }

    public void startProcessingClass(String className) {
        if (accepts(Severity.INFO)) {
            writeMessage("Preprocessing class " + className, Severity.INFO);
            indent.incrementAndGet();
        }
    }

    @Override
    public void endProcessingClass() {
        if (accepts(Severity.INFO)) {
            indent.decrementAndGet();
            writeMessage("... done", Severity.INFO);
        }
    }

    @Override
    public void startReadingClass(String className) {
        if (accepts(Severity.INFO)) {
            writeMessage("Decompiling class " + className, Severity.INFO);
            indent.incrementAndGet();
        }
    }

    @Override
    public void endReadingClass() {
        if (accepts(Severity.INFO)) {
            indent.decrementAndGet();
            writeMessage("... done", Severity.INFO);
        }
    }

    @Override
    public void startClass(String className) {
        if (accepts(Severity.INFO)) {
            writeMessage("Processing class " + className, Severity.TRACE);
            indent.incrementAndGet();
        }
    }

    @Override
    public void endClass() {
        if (accepts(Severity.INFO)) {
            indent.decrementAndGet();
            writeMessage("... proceeded", Severity.TRACE);
        }
    }

    @Override
    public void startMethod(String methodName) {
        if (accepts(Severity.INFO)) {
            writeMessage("Processing method " + methodName, Severity.TRACE);
            indent.incrementAndGet();
        }
    }

    @Override
    public void endMethod() {
        if (accepts(Severity.INFO)) {
            indent.decrementAndGet();
            writeMessage("... proceeded", Severity.TRACE);
        }
    }

    @Override
    public void startWriteClass(String className) {
        if (accepts(Severity.INFO)) {
            writeMessage("Writing class " + className, Severity.TRACE);
            indent.incrementAndGet();
        }
    }

    @Override
    public void endWriteClass() {
        if (accepts(Severity.INFO)) {
            indent.decrementAndGet();
            writeMessage("... written", Severity.TRACE);
        }
    }
}
