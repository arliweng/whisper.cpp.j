package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import cpp.whisper.enums.ggml_log_level;

public interface ggml_log_callback extends Callback {
	/**
	 * on log
	 * @param level the log level, the {@link ggml_log_level}
	 * @param text the text, end with \0
	 * @param user_data te user data
	 */
	void on_log(int level, String text, Pointer user_data);
}
