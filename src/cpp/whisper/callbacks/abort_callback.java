package cpp.whisper.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import cpp.whisper.struct.boolC99;

public interface abort_callback extends Callback {
	boolC99 answer_abort(Pointer data);
}
