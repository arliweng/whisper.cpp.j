package cpp.whisper;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

import cpp.whisper.callbacks.ggml_log_callback;
import cpp.whisper.struct.whisper_context_params;
import cpp.whisper.struct.whisper_full_params;

/**
 * the whisper.cpp API
 * @see whisper.cpp/whisper.cpp/include/whisper.h
 * @author arliweng@outlook.com
 */
public interface API_whisper extends Library {
    void whisper_log_set(ggml_log_callback cb, Pointer user_data);

    Pointer whisper_context_default_params_by_ref();
    void whisper_free_context_params(Pointer paramms_context);
    Pointer whisper_init_from_file_with_params(String path_model, whisper_context_params.ByValue paramms_context);
    void whisper_free(Pointer ctx);

    Pointer whisper_full_default_params_by_ref(int strategy);
    void whisper_free_params(Pointer full_params);

    int whisper_full(Pointer ctx, whisper_full_params.ByValue params, final float[] samples, int n_samples);
    int whisper_full_n_segments(Pointer ctx);
    long whisper_full_get_segment_t0(Pointer ctx, int i_segment);
    long whisper_full_get_segment_t1(Pointer ctx, int i_segment);
    String whisper_full_get_segment_text(Pointer ctx, int i_segment);

    /* the model data */
    int whisper_model_n_vocab(Pointer ctx);
    int whisper_model_n_audio_ctx(Pointer ctx);
    int whisper_model_n_audio_state(Pointer ctx);
    int whisper_model_n_audio_head(Pointer ctx);
    int whisper_model_n_audio_layer(Pointer ctx);
    int whisper_model_n_text_ctx(Pointer ctx);
    int whisper_model_n_text_state(Pointer ctx);
    int whisper_model_n_text_head(Pointer ctx);
    int whisper_model_n_text_layer(Pointer ctx);
    int whisper_model_n_mels(Pointer ctx);
    int whisper_model_ftype(Pointer ctx);
    int whisper_model_type(Pointer ctx);

    /* language id detected if auto, after whisper_full() */
    int whisper_full_lang_id(Pointer ctx);

    /* state way, split part by part but context keep to next part, but may break the character order */
    Pointer whisper_init_state(Pointer ctx);
    int whisper_full_with_state(Pointer ctx, Pointer state, whisper_full_params.ByValue params, float[] samples, int n_samples);
    int whisper_full_n_segments_from_state(Pointer state);
    long whisper_full_get_segment_t0_from_state(Pointer state, int i_segment);
    long whisper_full_get_segment_t1_from_state(Pointer state, int i_segment);
    String whisper_full_get_segment_text_from_state(Pointer state, int i_segment);
}
