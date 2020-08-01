package org.geass.jvm;

import org.geass.classpy.classfile.bytecode.Instruction;
import org.geass.classpy.classfile.constant.ConstantPool;

import java.util.Stack;

/**
 * @Description: 程序计数器/PC寄存器
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午3:55
 */
public class PCRegister {

    /**
     * 方法栈
     */
    private Stack<StackFrame> methodStack;

    public PCRegister(Stack<StackFrame> methodStack) {
        this.methodStack = methodStack;
    }

    /**
     * 获取下一条指令
     */
    public Instruction getNextInstruction() {
        if (methodStack.isEmpty()) {
            return null;
        }
        StackFrame frameAtTop = methodStack.peek();
        return frameAtTop.getNextInstruction();
    }

    /**
     * 获取当前方法栈
     */
    public StackFrame getTopFrame() {
        return methodStack.peek();
    }

    /**
     * 获得当前栈顶方法对应的常量池
     */
    public ConstantPool getTopFrameClassConstantPool() {
        return methodStack.peek().getClassFile().getConstantPool();
    }

    public void popFrameFromMethodStack() {
        methodStack.pop();
    }
}
