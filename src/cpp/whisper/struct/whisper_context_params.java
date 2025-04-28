package cpp.whisper.struct;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class whisper_context_params extends Structure {
    protected whisper_context_params(final Pointer p) {
        super(p);
        setAutoSynch(false);
    }
    public static class ByValue extends whisper_context_params implements Structure.ByValue {
        public ByValue(final Pointer p) { super(p); }
    }
    public static class ByReference extends whisper_context_params implements Structure.ByReference {
        public ByReference(final Pointer p) { super(p); }
    }

    public boolC99 use_gpu;
    public boolC99 flash_attn;
    public int gpu_device;
    public boolC99 dtw_token_timestamps;
    public int dtw_aheads_preset;
    public int dtw_n_top;
    public Pointer dtw_aheads;
    public NativeLong dtw_mem_size;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "use_gpu", "flash_attn", "gpu_device",
            "dtw_token_timestamps",  "dtw_aheads_preset",
            "dtw_n_top", "dtw_aheads", "dtw_mem_size"
        );
    }
}
