package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface progress_callback extends Callback {
	/**
	 * called on each progress update
	 * @param ctx the p_ctx
	 * @param state the p_state
	 * @param progress the progress in percent 100.
	 * @param user_data the user data
	 */
    void on_progress(Pointer ctx, Pointer state, int progress, Pointer user_data);
}
