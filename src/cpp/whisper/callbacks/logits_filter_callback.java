package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface logits_filter_callback extends Callback {
	/**
	 * called by each decoder to filter obtained logits
	 * @param ctx the p_ctx
	 * @param state the p_state
	 * @param tokens the tokens
	 * @param n_tokens the number of tokens
	 * @param logits the logits
	 * @param user_data the user data
	 */
	void on_logits_filter(Pointer ctx, Pointer state, Pointer tokens, int n_tokens, float logits, Pointer user_data);
}
