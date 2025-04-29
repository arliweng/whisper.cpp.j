package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import cpp.whisper.struct.boolC99;
import cpp.whisper.struct.whisper_context;

public interface encoder_begin_callback extends Callback {
	/**
	 * called each time before the encoder starts
	 * @param ctx the p_ctx
	 * @param state the p_state
	 * @param user_data the user data
	 * @return true continue, false abort the computation.
	 */
	boolC99 on_encoder_begin(whisper_context ctx, Pointer state, Pointer user_data);
}
