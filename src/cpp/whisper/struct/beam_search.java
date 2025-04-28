package cpp.whisper.struct;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class beam_search extends Structure {
    protected beam_search(final Pointer p) {
        super(p);
        setAutoSynch(true);
    }
    public static class ByValue extends whisper_context_params implements Structure.ByValue {
        public ByValue(final Pointer p) { super(p); }
    }
    public static class ByReference extends whisper_context_params implements Structure.ByReference {
        public ByReference(final Pointer p) { super(p); }
    }
	public beam_search() {
	}

    public int beam_size;
    public float patience;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("beam_size", "patience");
    }
}
