package org.geass.jvm;

import org.geass.classpy.classfile.ClassFile;
import org.geass.classpy.classfile.MethodInfo;
import org.geass.classpy.classfile.bytecode.Instruction;

import java.util.Stack;

/**
 * @Description: 当前方法栈
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午3:09
 */
public class StackFrame {
    /**
     * 局部变量表
     */
    private Object[] localVariables;

    /**
     * 操作数栈
     */
    private Stack<Object> operandStack = new Stack<>();

    /**
     * 方法信息
     */
    private MethodInfo methodInfo;

    /**
     * 指令编号
     */
    private int currentInstructionIndex;

    /**
     * 运行的Class文件
     */
//    private ClassFile classFile;
    private MiniJVMClass klass;

    public StackFrame(Object[] localVariables, MethodInfo methodInfo, MiniJVMClass klass) {
        this.localVariables = localVariables;
        this.methodInfo = methodInfo;
        this.klass = klass;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public Stack<Object> getOperandStack() {
        return operandStack;
    }

    public MiniJVMClass getKlass() {
        return klass;
    }

    public ClassFile getClassFile() {
        return klass.getClassFile();
    }

    public Instruction getNextInstruction() {
        return methodInfo.getCode().get(currentInstructionIndex++);
    }

    /**
     * 将一个对象推入栈顶
     */
    public void pushObjectToOperandStack(Object object) {
        operandStack.push(object);
    }

    public Object popFromOperandStack() {
        return operandStack.pop();
    }

    public Object[] getLocalVariables() {
        return localVariables;
    }

    public void setCurrentInstructionIndex(int currentInstructionIndex) {
        this.currentInstructionIndex = currentInstructionIndex;
    }

    public Object peekOperandStack() {
        return operandStack.peek();
    }

    public void astore(int i) {
        localVariables[i] = operandStack.pop();
    }

    public void aload(int i) {
        operandStack.push(localVariables[i]);
    }
}
