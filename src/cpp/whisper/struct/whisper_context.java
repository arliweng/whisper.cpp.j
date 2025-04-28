package cpp.whisper.struct;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import cpp.whisper.enums.ggml_type;

public class whisper_context extends Structure {
	protected whisper_context(final Pointer p) {
        super(p);
        setAutoSynch(false);
    }
    public static class ByValue extends whisper_context implements Structure.ByValue {
        public ByValue(final Pointer p) { super(p); }
    }
    public static class ByReference extends whisper_context implements Structure.ByReference {
        public ByReference(final Pointer p) { super(p); }
    }

    public NativeLong t_load_us;
    public NativeLong t_start_us;
    public int wtype = ggml_type.GGML_TYPE_F16;
    public int itype = ggml_type.GGML_TYPE_F16;
    public whisper_context_params.ByValue params;
    public Pointer model;
    public Pointer vocab;
    public Pointer state;
    public Pointer path_model;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
        		"t_load_us", "t_start_us", "wtype", "itype",
                "params", "model", "vocab", "state", "path_model"
            );
    }
}
