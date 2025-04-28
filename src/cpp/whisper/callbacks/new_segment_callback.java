package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface new_segment_callback extends Callback {
	void on_segment(Pointer ctx, Pointer state, int n_new, Pointer user_data);
}
