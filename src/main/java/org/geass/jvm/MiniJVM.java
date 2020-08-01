package org.geass.jvm;

import org.geass.classpy.classfile.ClassFile;
import org.geass.classpy.classfile.ClassFileParser;
import org.geass.classpy.classfile.MethodInfo;
import org.geass.classpy.classfile.bytecode.Bipush;
import org.geass.classpy.classfile.bytecode.Instruction;
import org.geass.classpy.classfile.bytecode.InstructionCp2;
import org.geass.classpy.classfile.constant.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * @Description: 一个用来学习的JVM
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午1:59
 */
public class MiniJVM {

    /**
     * 启动主类文件
     */
    private String mainClass;

    /**
     * java -cp classPath[]
     */
    private String[] classPathEntries;

    /**
     * 使用指定的classPath,mainClass创建JVM
     *
     * @param classPath 启动时的classpath,使用{@link java.io.File#pathSeparator}分隔符,支持文件夹不支持压缩包
     */
    public MiniJVM(String classPath, String mainClass) {
        this.mainClass = mainClass;
        this.classPathEntries = classPath.split(File.pathSeparator);
    }

    /**
     * 启动并运行Mini-JVM
     */
    public void start() {
        ClassFile mainClassFile = loadClassFromClassPath(mainClass);
        //简单起见忽略校验部分
        MethodInfo mainMethod = mainClassFile.getMethod("main").get(0);
        Stack<StackFrame> methodStack = new Stack<>();
        //局部变量长度由栈最大深度决定
        Object[] localVariablesForMainStrackFrame = new Object[mainMethod.getMaxStack()];
        localVariablesForMainStrackFrame[0] = null;
        //将main方法推入栈顶
        methodStack.push(new StackFrame(localVariablesForMainStrackFrame, mainMethod, mainClassFile));
        PCRegister pcRegister = new PCRegister(methodStack);
        while (true) {
            Instruction instruction = pcRegister.getNextInstruction();
            if (instruction == null) {
                //不存在下一条指令时退出
                break;
            }
            //pcRegister.methodStack.get(0).getMethodInfo().getCode()
            switch (instruction.getOpcode()) {
                case getstatic:  //#2->java/lang/System.out:
                    getstaticImpl(mainClassFile, pcRegister, instruction);
                    break;
                case invokestatic:  //#3->org/geass/jvm/SimpleClass.foo:
                    invokestaticImpl(methodStack, pcRegister, instruction);
                    break;
                case bipush:
                    bipushImpl(pcRegister, (Bipush) instruction);
                    break;
                case ireturn:
                    ireturnImpl(pcRegister);
                    break;
                case invokevirtual:  //#4->java/io/PrintStream.println:
                    invokevirtualImpl(pcRegister, instruction);
                    break;
                case _return:
                    voidReturnImpl(pcRegister);
                    break;
                default:
                    throw new IllegalStateException("Opcode " + instruction + " not implemented yet!");
            }
        }
    }

    private void voidReturnImpl(PCRegister pcRegister) {
        pcRegister.popFrameFromMethodStack();
    }

    private void invokevirtualImpl(PCRegister pcRegister, Instruction instruction) {
        ConstantPool constantPool = pcRegister.getTopFrameClassConstantPool();
        String className = getClassNameFromInvokeInstruction(instruction, constantPool);
        String methodName = getMethodNameFromInvokeInstruction(instruction, constantPool);
        if ("java/io/PrintStream".equals(className) && "println".equals(methodName)) {
            //此处采用简便的方式去实现调用打印方法,避免过度复杂的反射调用
            Object param = pcRegister.getTopFrame().popFromOperandStack();
            //从操作数上弹出this指针
            pcRegister.getTopFrame().popFromOperandStack();
            System.out.println(param);
        } else {
            throw new IllegalStateException("Not implemented yet!");
        }
    }

    private void ireturnImpl(PCRegister pcRegister) {
        Object returnValue = pcRegister.getTopFrame().popFromOperandStack();
        //弹出当前方法栈的栈顶
        pcRegister.popFrameFromMethodStack();
        pcRegister.getTopFrame().pushObjectToOperandStack(returnValue);
    }

    private void bipushImpl(PCRegister pcRegister, Bipush bipush) {
        pcRegister.getTopFrame().pushObjectToOperandStack(bipush.getOperand());
    }

    private void invokestaticImpl(Stack<StackFrame> methodStack, PCRegister pcRegister, Instruction instruction) {
        ConstantPool constantPool = pcRegister.getTopFrameClassConstantPool();
        String className = getClassNameFromInvokeInstruction(instruction, constantPool);
        String methodName = getMethodNameFromInvokeInstruction(instruction, constantPool);
//        int methodIndex = InstructionCp2.class.cast(instruction).getTargetMethodIndex();
//        ConstantMethodrefInfo methodrefInfo = constantPool.getMethodrefInfo(methodIndex);
//        ConstantClassInfo classInfo = methodrefInfo.getClassInfo(constantPool);
//        String className = constantPool.getUtf8String(classInfo.getNameIndex());
//        String methodName = methodrefInfo.getMethodNameAndType(constantPool).getName(constantPool);
        ClassFile classFile = loadClassFromClassPath(className);
        MethodInfo targetMethodInfo = classFile.getMethod(methodName).get(0);

        Object[] localVariables = new Object[targetMethodInfo.getMaxLocals()];
        // TODO 应该分析方法的参数，从操作数栈上弹出对应数量的参数放在新栈帧的局部变量表中
        StackFrame newFrame = new StackFrame(localVariables, targetMethodInfo, classFile);
        methodStack.push(newFrame);
    }

    private void getstaticImpl(ClassFile mainClassFile, PCRegister pcRegister, Instruction instruction) {
        //拿到System.out的指令
        int fieldIndex = InstructionCp2.class.cast(instruction).getTargetFieldIndex();
        ConstantPool constantPool = pcRegister.getTopFrameClassConstantPool();
        //从常量池中找到需要的Field
        ConstantFieldrefInfo fieldrefInfo = constantPool.getFieldrefInfo(fieldIndex);
        //获取到Filed的ClassName and FieldName
        ConstantClassInfo classInfo = fieldrefInfo.getClassInfo(constantPool);
        ConstantNameAndTypeInfo fieldNameAndTypeInfo = fieldrefInfo.getFieldNameAndTypeInfo(constantPool);
        //从常量池获取到ClassName
        String className = constantPool.getUtf8String(classInfo.getNameIndex());
        String fieldName = fieldNameAndTypeInfo.getName(constantPool);
        String fieldType = fieldNameAndTypeInfo.getType(constantPool);
        if ("java/lang/System".equals(className) && "out".equals(fieldName)) {
            Object field = System.out;
            pcRegister.getTopFrame().pushObjectToOperandStack(field);
        } else {
            throw new IllegalStateException("Not implemented yet!");
        }
    }

    private String getClassNameFromInvokeInstruction(Instruction instruction, ConstantPool constantPool) {
        int methodIndex = InstructionCp2.class.cast(instruction).getTargetMethodIndex();
        ConstantMethodrefInfo methodrefInfo = constantPool.getMethodrefInfo(methodIndex);
        ConstantClassInfo classInfo = methodrefInfo.getClassInfo(constantPool);
        return constantPool.getUtf8String(classInfo.getNameIndex());
    }

    private String getMethodNameFromInvokeInstruction(Instruction instruction, ConstantPool constantPool) {
        int methodIndex = InstructionCp2.class.cast(instruction).getTargetMethodIndex();
        ConstantMethodrefInfo methodrefInfo = constantPool.getMethodrefInfo(methodIndex);
        ConstantClassInfo classInfo = methodrefInfo.getClassInfo(constantPool);
        return methodrefInfo.getMethodNameAndType(constantPool).getName(constantPool);
    }

    /**
     * 从classpath上加载class
     *
     * @param fqcn {fullQualifiedClassName}全限定类名
     * @return class字节码文件
     */
    private ClassFile loadClassFromClassPath(String fqcn) {
        return (ClassFile) Stream.of(classPathEntries)
                .map(entry -> tryLoad(entry, fqcn))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(new ClassNotFoundException(fqcn)));
    }

    private Object tryLoad(String entry, String fqcn) {
        try {
            byte[] bytes = Files.readAllBytes(new File(entry, fqcn.replace('.', '/') + ".class").toPath());
            //把字节码中所有字节读取出来生成一个ClassFile
            return new ClassFileParser().parse(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new MiniJVM("target/classes", "org.geass.jvm.SimpleClass").start();
    }
}
