package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface ggml_log_callback extends Callback {
	void on_log(int level, String text, Pointer user_data);
}
