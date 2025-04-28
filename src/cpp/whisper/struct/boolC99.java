package cpp.whisper.struct;

import com.sun.jna.IntegerType;

@SuppressWarnings("serial")
public class boolC99 extends IntegerType {
    public static final boolC99 FALSE = new boolC99(0);
    public static final boolC99 TRUE = new boolC99(1);

    public boolC99(final long value) {
    	/*
    	 * JNA doesn't have c bool, only BOOL but Windows 4 bytes.
    	 * create 1 byte here.
    	 */
        super(1, value, true);
    }

    public boolC99() {
        this(0);
    }
}