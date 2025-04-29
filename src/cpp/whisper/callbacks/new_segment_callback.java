package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import cpp.whisper.API_whisper;

public interface new_segment_callback extends Callback {
	/**
	 * call back each segment.
	 * @param ctx the p_ctx
	 * @param state the p_state by {@link API_whisper#whisper_init_state(Pointer)}
	 * @param n_new the new index
	 * @param user_data the user data
	 */
	void on_segment(Pointer ctx, Pointer state, int n_new, Pointer user_data);
}
