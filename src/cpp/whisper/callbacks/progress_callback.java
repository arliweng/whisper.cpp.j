package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface progress_callback extends Callback {
    void on_progress(Pointer ctx, Pointer state, int progress, Pointer user_data);
}
