package cpp.whisper.struct;

import java.util.Collections;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class greedy extends Structure {
    protected greedy(final Pointer p) {
        super(p);
        setAutoSynch(true);
    }
    public static class ByValue extends whisper_context_params implements Structure.ByValue {
        public ByValue(final Pointer p) { super(p); }
    }
    public static class ByReference extends whisper_context_params implements Structure.ByReference {
        public ByReference(final Pointer p) { super(p); }
    }
	public greedy() {
	}

    public int best_of;

    @Override
    protected List<String> getFieldOrder() {
        return Collections.singletonList("best_of");
    }
}
