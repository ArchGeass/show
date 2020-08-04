package org.geass.jvm;

import org.geass.classpy.classfile.MethodInfo;
import org.geass.classpy.classfile.bytecode.Bipush;
import org.geass.classpy.classfile.bytecode.Instruction;
import org.geass.classpy.classfile.bytecode.InstructionCp1;
import org.geass.classpy.classfile.bytecode.InstructionCp2;
import org.geass.classpy.classfile.constant.*;
import org.geass.classpy.classfile.datatype.U1CpIndex;
import org.geass.classpy.common.FilePart;

import java.io.File;
import java.util.List;
import java.util.Stack;

import static org.geass.jvm.MiniJVMClassLoader.EXT_CLASSLOADER;

/**
 * @Description: 一个用来学习的JVM
 * @Author: ArchGeass
 * @Date: 2020/8/1,下午1:59
 */
public class MiniJVM {

    /**
     * 启动主类文件
     */
    private String mainClassName;

    /**
     * java -cp classPath[]
     */
    private MiniJVMClassLoader appClassLoader;

    /**
     * 使用指定的classPath,mainClass创建JVM
     *
     * @param classPath 启动时的classpath,使用{@link java.io.File#pathSeparator}分隔符,支持文件夹不支持压缩包
     */
    public MiniJVM(String classPath, String mainClass) {
        this.mainClassName = mainClass;
        this.appClassLoader =
                new MiniJVMClassLoader(classPath.split(File.pathSeparator),
                        EXT_CLASSLOADER);
    }

    /**
     * 启动并运行Mini-JVM
     */
    public void start() {
        //ClassFile mainClassFile = loadClassFromClassPath(mainClassName);//此处改用自己实现的classLoader
        MiniJVMClass mainClass = null;
        try {
            mainClass = appClassLoader.loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //简单起见忽略校验部分
        MethodInfo mainMethod = mainClass.getMethod("main").get(0);
        //创建main方法栈
        Stack<StackFrame> mainMethodStack = new Stack<>();
        //局部变量长度由栈最大深度决定
        Object[] localVariablesForMainStrackFrame = new Object[mainMethod.getMaxLocals()];
        localVariablesForMainStrackFrame[0] = null;
        //将main方法推入栈顶
        mainMethodStack.push(new StackFrame(localVariablesForMainStrackFrame, mainMethod, mainClass));
        PCRegister pcRegister = new PCRegister(mainMethodStack);
        while (true) {
            Instruction instruction = pcRegister.getNextInstruction();
            if (instruction == null) {
                //不存在下一条指令时退出
                break;
            }
            //pcRegister.methodStack.get(0).getMethodInfo().getCode()
            switch (instruction.getOpcode()) {
                case getstatic:  //#2->java/lang/System.out:
                    getstaticImpl(pcRegister, instruction);
                    break;
                case invokestatic:  //#3->org/geass/jvm/SimpleClass.foo:
                    invokestaticImpl(mainMethodStack, pcRegister, instruction);
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
                case iload_0:
                    iload0Impl(pcRegister);
                    break;
                case iconst_1:
                    iconstIImpl(pcRegister.getTopFrame(), 1);
                    break;
                case iconst_2:
                    iconstIImpl(pcRegister.getTopFrame(), 2);
                    break;
                case iconst_5:
                    iconstIImpl(pcRegister.getTopFrame(), 5);
                    break;
                case ifne:
                    ifneImpl(pcRegister, instruction);
                    break;
                case irem:
                    iremImpl(pcRegister);
                    break;
                case sipush:
                    sipushImpl(pcRegister, instruction);
                    break;
                case isub:
                    isubImpl(pcRegister);
                    break;
                case imul:
                    imulImpl(pcRegister);
                    break;
                case _new:
                    newImpl(pcRegister, instruction);
                    break;
                case dup:
                    dupImpl(pcRegister);
                    break;
                case invokespecial:
                    invokespecialImpl(pcRegister);
                    break;
                case astore_1:
                    astoreImpl(pcRegister, 1);
                    break;
                case astore_2:
                    astoreImpl(pcRegister, 2);
                    break;
                case aload_1:
                    aloadImpl(pcRegister, 1);
                    break;
                case aload_2:
                    aloadImpl(pcRegister, 2);
                    break;
                case ldc:
                    ldcImpl(pcRegister, instruction);
                    break;
                case checkcast:
                    checkcastImpl(pcRegister, instruction);
                    break;
                default:
                    throw new IllegalStateException("Opcode " + instruction + " not implemented yet!");
            }
        }
    }

    private void checkcastImpl(PCRegister pcRegister, Instruction instruction) {
        String className = getClassNameFromNewOrCheckcastInstruction(instruction, pcRegister.getTopFrameClassConstantPool())
                .replace('/', '.');
        StackFrame topFrame = pcRegister.getTopFrame();
        MiniJVMClass targetClass = null;
        try {
            targetClass = topFrame.getKlass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        MiniJVMObject objectOnStack = (MiniJVMObject) topFrame.peekOperandStack();
        if (!objectOnStack.getKlass().getName().equals(targetClass.getName())
                || objectOnStack.getKlass().getClassLoader() != targetClass.getClassLoader()) {
            throw new ClassCastException("Can't cast type " + objectOnStack.getKlass().getName() +
                    " to type " + targetClass.getName());
        }
    }

    private void ldcImpl(PCRegister pcRegister, Instruction instruction) {
        FilePart filePart = InstructionCp1.class.cast(instruction).getParts().get(1);
        int constantPoolIndex = ((U1CpIndex) filePart).getValue();
        String s = pcRegister.getTopFrameClassConstantPool().getConstantDesc(constantPoolIndex);
        pcRegister.getTopFrame().pushObjectToOperandStack(s);
    }

    private void aloadImpl(PCRegister pcRegister, int i) {
        pcRegister.getTopFrame().aload(i);
    }

    private void astoreImpl(PCRegister pcRegister, int i) {
        pcRegister.getTopFrame().astore(i);
    }

    private void invokespecialImpl(PCRegister pcRegister) {
        pcRegister.getTopFrame().popFromOperandStack();
    }

    private void dupImpl(PCRegister pcRegister) {
        StackFrame topFrame = pcRegister.getTopFrame();
        topFrame.pushObjectToOperandStack(topFrame.peekOperandStack());
    }

    private void newImpl(PCRegister pcRegister, Instruction instruction) {
        try {
            String className = getClassNameFromNewInstruction(instruction, pcRegister.getTopFrameClassConstantPool());
            MiniJVMClass klass = pcRegister.getTopFrame().getKlass().getClassLoader().loadClass(className);
            //此处简单期间去实现构造器的调用
            pcRegister.getTopFrame().pushObjectToOperandStack(klass.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getClassNameFromNewInstruction(Instruction instruction, ConstantPool constantPool) {
        int targetClassIndex = InstructionCp2.class.cast(instruction).getTargetClassIndex();
        ConstantClassInfo classInfo = constantPool.getClassInfo(targetClassIndex);
        return constantPool.getUtf8String(classInfo.getNameIndex());
    }

    /**
     * value1和value2都必须为int类型
     * 从操作数堆栈中弹出值
     * int结果为value1 * value2
     * 结果被压入操作数堆栈
     *
     * @param pcRegister 当前PC寄存器
     */
    private void imulImpl(PCRegister pcRegister) {
        StackFrame topFrame = pcRegister.getTopFrame();
        Integer value2 = (Integer) topFrame.popFromOperandStack();
        Integer value1 = (Integer) topFrame.popFromOperandStack();
        int result = value1 * value2;
        topFrame.pushObjectToOperandStack(result);
    }

    /**
     * value1和value2都必须为int类型
     * 从操作数堆栈中弹出值
     * int结果为value1-value2
     * 结果被压入操作数堆栈
     *
     * @param pcRegister 当前PC寄存器
     */
    private void isubImpl(PCRegister pcRegister) {
        StackFrame topFrame = pcRegister.getTopFrame();
        Integer value2 = (Integer) topFrame.popFromOperandStack();
        Integer value1 = (Integer) topFrame.popFromOperandStack();
        int result = value1 - value2;
        topFrame.pushObjectToOperandStack(result);
    }

    /**
     * 无符号立即数byte1和byte2的值组合成一个中间short
     * 其中short的值为（byte1 << 8）| 字节2
     * 然后将中间值符号扩展为int值
     * 该值被压入操作数堆栈
     *
     * @param pcRegister  当前PC寄存器
     * @param instruction 当前指令
     */
    private void sipushImpl(PCRegister pcRegister, Instruction instruction) {
        pcRegister.popFrameFromMethodStack();
        StackFrame topFrame = pcRegister.getTopFrame();
        Integer returnValue = Integer.parseInt(instruction.getDesc().split(" ")[1]);
        topFrame.pushObjectToOperandStack(returnValue);
    }

    /**
     * if条件分支指令调转
     *
     * @param pcRegister  当前PC寄存器
     * @param instruction 当前指令
     */
    private void ifneImpl(PCRegister pcRegister, Instruction instruction) {
        StackFrame topFrame = pcRegister.getTopFrame();
        MethodInfo methodInfo = topFrame.getMethodInfo();
        List<Instruction> code = methodInfo.getCode();
        int ifParam = (int) topFrame.popFromOperandStack();

        //此处相等执行下一条指令即可,若不等则调转到else对应的指令位置
        if (ifParam != 0) { //ifne指令的比较值固定是0
            //从instruction.getDesc获取到欲调转的指令号,使用空格进行分割
            int pc = Integer.parseInt(instruction.getDesc().split(" ")[1]);
            for (int i = 0; i < code.size(); i++) {
                if (pc == code.get(i).getPc()) {
                    topFrame.setCurrentInstructionIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * value1和value2都必须为int类型
     * 从操作数堆栈中弹出值
     * int结果为value1-（value1 / value2）* value2
     * 结果被压入操作数堆栈
     *
     * @param pcRegister 当前PC寄存器
     */
    private void iremImpl(PCRegister pcRegister) {
        StackFrame topFrame = pcRegister.getTopFrame();
        Integer value2 = (Integer) topFrame.popFromOperandStack();
        Integer value1 = (Integer) topFrame.popFromOperandStack();
        Integer result = value1 - (value1 / value2) * value2;
        topFrame.pushObjectToOperandStack(result);
    }

    /**
     * 将一个常量压入操作数栈
     *
     * @param topFrame 当前操作数栈
     * @param i        压入的常量值
     */
    private void iconstIImpl(StackFrame topFrame, Integer i) {
        topFrame.pushObjectToOperandStack(i);
    }

    /**
     * 从当前栈帧局部变量表中0号位置取int类型的数值加载到操作数栈
     */
    private void iload0Impl(PCRegister pcRegister) {
        StackFrame topFrame = pcRegister.getTopFrame();
        Object intValue = topFrame.getLocalVariables()[0];
                    /*if (Integer.class.isInstance(intValue)) {
                    //暂且忽略类型检查
                        throw new IllegalStateException(intValue + " is not int value!");
                    }*/
        topFrame.pushObjectToOperandStack(intValue);
    }

    /**
     * return void
     *
     * @param pcRegister 当前PC寄存器
     */
    private void voidReturnImpl(PCRegister pcRegister) {
        pcRegister.popFrameFromMethodStack();
    }

    /**
     * 实现打印方法
     *
     * @param pcRegister  当前PC寄存器
     * @param instruction 当前指令
     */
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
        } else if ("org/geass/jvm/MyClassLoader".equals(className)
                && "loadClass".equals(methodName)) {
            StackFrame topFrame = pcRegister.getTopFrame();
            String classNameParam = (String) topFrame.popFromOperandStack();
            MiniJVMObject thisObject = (MiniJVMObject) topFrame.popFromOperandStack();
            MiniJVMClass klass = null;
            try {
                klass = ((MyClassLoader) thisObject.getRealJavaObject()).loadClass(classNameParam);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            topFrame.pushObjectToOperandStack(klass);
        } else if ("org/geass/jvm/MiniJVMClass".equals(className)
                && "newInstance".equals(methodName)) {
            StackFrame topFrame = pcRegister.getTopFrame();
            MiniJVMClass thisObject = (MiniJVMClass) topFrame.popFromOperandStack();
            topFrame.pushObjectToOperandStack(thisObject.newInstance());
        } else {
            throw new IllegalStateException("Not implemented yet!");
        }
    }

    /**
     * 销毁但前方法栈针
     * 将返回值压入上层方法栈
     * return int
     *
     * @param pcRegister 当前PC寄存器
     */
    private void ireturnImpl(PCRegister pcRegister) {
        Object returnValue = pcRegister.getTopFrame().popFromOperandStack();
        //弹出当前方法栈的栈顶
        pcRegister.popFrameFromMethodStack();
        pcRegister.getTopFrame().pushObjectToOperandStack(returnValue);
    }

    private void bipushImpl(PCRegister pcRegister, Bipush bipush) {
        pcRegister.getTopFrame().pushObjectToOperandStack(bipush.getOperand());
    }

    /**
     * 用于调用类方法(static方法)
     *
     * @param mainMethodStack main方法栈
     * @param pcRegister      当前PC寄存器
     * @param instruction     当前指令
     */
    private void invokestaticImpl(Stack<StackFrame> mainMethodStack, PCRegister pcRegister, Instruction instruction) {
        ConstantPool constantPool = pcRegister.getTopFrameClassConstantPool();
        String className = getClassNameFromInvokeInstruction(instruction, constantPool);
        String methodName = getMethodNameFromInvokeInstruction(instruction, constantPool);
//        ClassFile classFile = loadClassFromClassPath(className);
        MiniJVMClass classFile = null;
        try {
            classFile = appClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        MethodInfo targetMethodInfo = classFile.getMethod(methodName).get(0);
        //targetMethodInfo.getMaxLocals()用于获取实际传参的长度
        Object[] localVariables = new Object[targetMethodInfo.getMaxLocals()];
        //targetMethodInfo.getParts().get(2).getDesc()
        if (targetMethodInfo.getMaxLocals() > 0) {
            //分析方法的参数,从操作数栈上弹出对应数量的参数放在新栈帧的局部变量表中
            for (int i = 0; i < targetMethodInfo.getMaxLocals(); i++) {
                Object param = pcRegister.getTopFrame().popFromOperandStack();//从栈上弹出一个需要的变量值
                localVariables[i] = param;
            }
        }
        StackFrame newFrame = new StackFrame(localVariables, targetMethodInfo, classFile);
        mainMethodStack.push(newFrame);
    }

    private void getstaticImpl(PCRegister pcRegister, Instruction instruction) {
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

    private String getClassNameFromNewOrCheckcastInstruction(Instruction instruction, ConstantPool constantPool) {
        int targetClassIndex = InstructionCp2.class.cast(instruction).getTargetClassIndex();
        ConstantClassInfo classInfo = constantPool.getClassInfo(targetClassIndex);
        return constantPool.getUtf8String(classInfo.getNameIndex());
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

    /*
     * 从classpath上加载class
     * 使用自定义classLoader就不需要了
     * @param fqcn {fullQualifiedClassName}全限定类名
     * @return class字节码文件
     *
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
    }*/
    public static void main(String[] args) {
/*
        new MiniJVM("target/classes", "org.geass.jvm.SimpleClass").start();
        new MiniJVM("target/classes", "org.geass.jvm.BranchClass").start();
        new MiniJVM("target/classes", "org.geass.jvm.RecursiveClass").start();
*/
        new MiniJVM("target/classes", "org.geass.jvm.SimpleClassLoaderClass").start();
    }
}
