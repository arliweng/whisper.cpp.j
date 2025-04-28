package cpp.whisper.struct;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class whisper_full_params extends Structure {
	protected whisper_full_params(final Pointer p) {
        super(p);
        setAutoSynch(false);
    }
    public static class ByValue extends whisper_full_params implements Structure.ByValue {
        public ByValue(final Pointer p) { super(p); }
    }
    public static class ByReference extends whisper_full_params implements Structure.ByReference {
        public ByReference(final Pointer p) { super(p); }
    }

    /* see whisper.cpp/include/whisper.h */
    public int strategy;
    public int n_threads;
    public int n_max_text_ctx;
    public int offset_ms;
    public int duration_ms;

    public boolC99 translate;
    public boolC99 no_context;
    public boolC99 no_timestamps;
    public boolC99 single_segment;
    public boolC99 print_special;
    public boolC99 print_progress;
    public boolC99 print_realtime;
    public boolC99 print_timestamps;

    public boolC99 token_timestamps;
    public float thold_pt;
    public float thold_ptsum;
    public int max_len;
    public boolC99 split_on_word;
    public int max_tokens;

    public boolC99 debug_mode;
    public int audio_ctx;

    public boolC99 tdrz_enable;

    public String suppress_regex;

    public String initial_prompt;
    public Pointer prompt_tokens;
    public int prompt_n_tokens;

    public String language;
    public boolC99 detect_language;

    public boolC99 suppress_blank;
    public boolC99 suppress_nst;

    public float temperature;
    public float max_initial_ts;
    public float length_penalty;

    public float temperature_inc;
    public float entropy_thold;
    public float logprob_thold;
    public float no_speech_thold;

    public greedy greedy;
    public beam_search beam_search;

    /*
     * WARNING by arli weng, 20240416
     *
     * see com.sun.jna.Callback.METHOD_NAME
     * it mean, the method name of callback NOT overload.
     *  because to do that, need for each the parameters, and cache them.
     *
     * so callback below can't implements more in one class but same method name.
     * for example:
     *
     * interface callback1 extends Callback {
     *  void callback(String a);
     * }
     * interface callback2 extends Callback {
     *  void callback(int b);
     * }
     *
     * class c implements callback1, callback2 {
     * 	void callback(String a) {}
     *  void callback(int b) {}
     * }
     *
     * the callback2 never get call, always callback1,
     * and will read int the b as String the a if callback2.
     *
     * so, need change the method in next callback to another name.
     * for example:
     *
     * interface callback2 extends Callback {
     *  void method2(int b);
     * }
     *
     * in JNA, when callback only one method, whatever name its, call it.
     *
     * BUT may java compiler or obfuscater change the name as one,
     *  so don't implements them all in same one class.
     */
    /**
     * the new segment callback
     * @see cpp.whisper.callbacks.new_segment_callback
     */
    public Pointer new_segment_callback;
    /**
     * the data for {@link #new_segment_callback}.<br>
     * keep null here, should use Java Object is managed,
     *  not native object, it need native access twice.
     */
    public Pointer new_segment_callback_user_data;
    /**
     * the progress callback
     * @see cpp.whisper.callbacks.progress_callback
     */
    public Pointer progress_callback;
    public Pointer progress_callback_user_data;
    public Pointer encoder_begin_callback;
    public Pointer encoder_begin_callback_user_data;
    /**
     * the abort callback
     * @see cpp.whisper.callbacks.abort_callback
     */
    public Pointer abort_callback;
    public Pointer abort_callback_user_data;
    public Pointer logits_filter_callback;
    public Pointer logits_filter_callback_user_data;

    public Pointer grammar_rules;
    public NativeLong n_grammar_rules;
    public NativeLong i_start_rule;
    public float grammar_penalty;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
        		"strategy", "n_threads", "n_max_text_ctx", "offset_ms", "duration_ms",
        		"translate", "no_context", "no_timestamps", "single_segment",
        		"print_special", "print_progress", "print_realtime", "print_timestamps",
                "token_timestamps", "thold_pt", "thold_ptsum", "max_len", "split_on_word", "max_tokens",
                "debug_mode", "audio_ctx",
                "tdrz_enable",
                "suppress_regex",
                "initial_prompt", "prompt_tokens", "prompt_n_tokens",
                "language", "detect_language",
                "suppress_blank", "suppress_nst",
                "temperature",  "max_initial_ts", "length_penalty",
                "temperature_inc", "entropy_thold", "logprob_thold", "no_speech_thold",
                "greedy", "beam_search",
                "new_segment_callback", "new_segment_callback_user_data",
                "progress_callback", "progress_callback_user_data",
                "encoder_begin_callback", "encoder_begin_callback_user_data",
                "abort_callback", "abort_callback_user_data",
                "logits_filter_callback", "logits_filter_callback_user_data",
                "grammar_rules", "n_grammar_rules", "i_start_rule", "grammar_penalty"
            );
    }

    public void prompt_tokens(final int[] tokens) {
        final Memory mem = new Memory(4L * tokens.length);
        mem.write(0, tokens, 0, tokens.length);
        prompt_tokens = mem;
    }
}
