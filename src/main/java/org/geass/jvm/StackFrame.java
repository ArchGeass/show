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
    private ClassFile classFile;

    public StackFrame(Object[] localVariables, MethodInfo methodInfo, ClassFile classFile) {
        this.localVariables = localVariables;
        this.methodInfo = methodInfo;
        this.classFile = classFile;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public Stack<Object> getOperandStack() {
        return operandStack;
    }

    public ClassFile getClassFile() {
        return classFile;
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
}
