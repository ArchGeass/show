package org.geass.classpy.classfile.datatype;


import org.geass.classpy.classfile.ClassFilePart;
import org.geass.classpy.classfile.ClassFileReader;
import org.geass.classpy.classfile.attribute.AttributeFactory;
import org.geass.classpy.classfile.attribute.AttributeInfo;
import org.geass.classpy.classfile.constant.ConstantPool;
import org.geass.classpy.common.FilePart;
import org.geass.classpy.common.ParseException;
import org.geass.classpy.helper.StringHelper;

/**
 * Array of class parts.
 */
public class Table extends ClassFilePart {

    private final UInt length;
    private final Class<? extends ClassFilePart> entryClass;

    public Table(UInt length, Class<? extends ClassFilePart> entryClass) {
        this.length = length;
        this.entryClass = entryClass;
    }
    
    @Override
    protected void readContent(ClassFileReader reader) {
        try {
            for (int i = 0; i < length.getValue(); i++) {
                super.add(readEntry(reader));
            }
        } catch (ReflectiveOperationException e) {
            throw new ParseException(e);
        }
    }

    private ClassFilePart readEntry(ClassFileReader reader) throws ReflectiveOperationException {
        if (entryClass == AttributeInfo.class) {
            return readAttributeInfo(reader);
        } else {
            ClassFilePart c = entryClass.newInstance();
            c.read(reader);
            return c;
        }
    }
    
    private AttributeInfo readAttributeInfo(ClassFileReader reader) {
        int attrNameIndex = reader.getShort(reader.getPosition());
        String attrName = reader.getConstantPool().getUtf8String(attrNameIndex);
        
        AttributeInfo attr = AttributeFactory.create(attrName);
        attr.setName(attrName);
        attr.read(reader);
        
        return attr;
    }

    @Override
    protected void postRead(ConstantPool cp) {
        int i = 0;
        for (FilePart entry : super.getParts()) {
            String newName = StringHelper.formatIndex(length.getValue(), i++);
            String oldName = entry.getName();
            if (oldName != null) {
                newName += " (" + oldName + ")";
            }
            entry.setName(newName);
        }
    }

}
