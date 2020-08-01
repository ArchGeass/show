package org.geass.classpy.common;

public interface FileParser {

    FileParser NOP = data -> new FilePart() {};

    FilePart parse(byte[] data);

}
