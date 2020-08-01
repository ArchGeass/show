package org.geass.classpy.classfile.jvm;

import java.io.*;

public class Mutf8Decoder {
    
    /**
     * Decode modified UTF-8 string from byte[].
     * todo: optimize
     * 
     * @param bytes
     * @return 
     * @throws IOException
     */
    public static String decodeMutf8(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length + 2);
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(bytes.length);
        dos.write(bytes);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream dis = new DataInputStream(bais);
        return dis.readUTF();
    }
    
}
