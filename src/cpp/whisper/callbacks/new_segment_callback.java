package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import cpp.whisper.API_whisper;
import cpp.whisper.WhisperCJ.PARAMS_CALLBACK;
import cpp.whisper.struct.whisper_full_params;

public interface new_segment_callback extends Callback {
	/**
	 * call back each segment.<br>
	 * set in {@link PARAMS_CALLBACK#on_modify_params(cpp.whisper.struct.whisper_full_params)},
	 * the {@link whisper_full_params#new_segment_callback}
	 * <pre>
	 * //example1:
	 * public void on_segment(final Pointer ctx, final Pointer state, final int n_new, final Pointer user_data) {
	 * 	long s, e; String text;
	 * 	final int size = api.whisper_full_n_segments(ctx);
	 * 	for (int i = size -n_new; i < size; i++) {
	 * 		s = api.whisper_full_get_segment_t0(ctx, i) * 10;
	 * 		e = api.whisper_full_get_segment_t1(ctx, i) * 10;
	 * 		text = api.whisper_full_get_segment_text(ctx, i);
	 * 		callback_user.on_segment(i, s, e, text);
	 * 	}
	 * }
	 * //example2 with state:
	 * public void on_segment(final Pointer ctx, final Pointer state, final int n_new, final Pointer user_data) {
	 * 	long s, e; String text;
	 * 	final int size = api.whisper_full_n_segments_from_state(state);
	 * 	for (int i = size -n_new; i < size; i++) {
	 * 		s = api.whisper_full_get_segment_t0_from_state(state, i) * 10;
	 * 		e = api.whisper_full_get_segment_t1_from_state(state, i) * 10;
	 * 		text = api.whisper_full_get_segment_text_from_state(state, i);
	 * 		callback_user.on_segment(i, s, e, text);
	 * 	}
	 * }
	 * </pre>
	 * @param ctx the p_ctx
	 * @param state the p_state by {@link API_whisper#whisper_init_state(Pointer)}
	 * @param n_new the new index
	 * @param user_data the user data
	 */
	void on_segment(Pointer ctx, Pointer state, int n_new, Pointer user_data);
}
