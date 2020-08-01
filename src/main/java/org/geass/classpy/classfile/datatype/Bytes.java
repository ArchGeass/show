package org.geass.classpy.classfile.datatype;


import org.geass.classpy.classfile.ClassFilePart;
import org.geass.classpy.classfile.ClassFileReader;

/**
 * Unparsed bytes.
 */
public class Bytes extends ClassFilePart {

    private UInt count;

    public Bytes(UInt count) {
        this.count = count;
    }

    @Override
    protected void readContent(ClassFileReader reader) {
        reader.skipBytes(count.getValue());
    }

}
