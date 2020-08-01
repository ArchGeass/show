package org.geass.classpy.classfile;


import org.geass.classpy.classfile.constant.ConstantPool;
import org.geass.classpy.common.FileParser;
import org.geass.classpy.common.FilePart;

public class ClassFileParser implements FileParser {

    public ClassFile parse(byte[] data) {
        ClassFile cf = new ClassFile();
        cf.read(new ClassFileReader(data));
        postRead(cf, cf.getConstantPool());
        return cf;
    }

    private static void postRead(ClassFilePart fc, ConstantPool cp) {
        for (FilePart c : fc.getParts()) {
            postRead((ClassFilePart) c, cp);
        }
        fc.postRead(cp);
    }

}
