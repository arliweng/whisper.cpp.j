package cpp.whisper;

import java.io.Closeable;
import java.nio.FloatBuffer;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import cpp.whisper.callbacks.ggml_log_callback;
import cpp.whisper.enums.whisper_sampling_strategy;
import cpp.whisper.struct.boolC99;
import cpp.whisper.struct.whisper_context_params;
import cpp.whisper.struct.whisper_full_params;

/**
 * the whisper.cpp java JNA bindings.<br>
 * com.sun.jna is required, without JNI.
 * @author arliweng@outlook.com
 * @see https://github.com/ggml-org/whisper.cpp
 * @see https://github.com/java-native-access/jna
 * @since Java 7
 */
public class WhisperCJ implements Closeable {
	/**
	 * the audio samples per second, should convert to before whisper.
	 */
	public static final int AUDIO_SAMPLE_RATE = 16000;
	/**
	 * the audio samples per millisecond
	 */
	public static final int SAMPLES_PER_MILLISECONDS = AUDIO_SAMPLE_RATE / 1000;
	/** segment callback */
	public static interface SEGMENT_CALLBACK {
		/**
		 * call back each segment
		 * @param id the index, from 0
		 * @param start the time start in milliseconds
		 * @param end the time end in milliseconds
		 * @param text the content
		 */
		void on_segment(int id, long start, long end, String text);
	}
	/** parameters callback */
	public static interface PARAMS_CALLBACK {
		/**
		 * call back to modify the parameters
		 * @param params the parameters
		 */
		void on_modify_params(final whisper_full_params params);
	}

	private static API_whisper api;
	private static API_ggml_vulkan api_ggml_vulkan;
	/**
	 * load the library files.<br>
	 * call this before all.
	 * @param cpu true load the CPU module, build with GGML_CPU=ON required.
	 * @param vulkan true load the vulkan module, build with GGML_VULKAN=ON required.
	 * @param cuda true load the CUDA module, build with GGML_CUDA=ON required.
	 * @throws UnsatisfiedLinkError if load error, including not found.
	 */
	public static void driver(final boolean cpu, final boolean vulkan, final boolean cuda) throws UnsatisfiedLinkError {
		if (api == null) {
			NativeLibrary.getInstance("ggml-base");
			if (cpu) NativeLibrary.getInstance("ggml-cpu");
			if (vulkan) api_ggml_vulkan = Native.load("ggml-vulkan", API_ggml_vulkan.class);
			if (cuda) NativeLibrary.getInstance("ggml-cuda");
			NativeLibrary.getInstance("ggml");
			api = Native.load("whisper", API_whisper.class);
		}
	}

	public static API_whisper api() {
		return api;
	}
	public static API_ggml_vulkan api_ggml_vulkan() {
		return api_ggml_vulkan;
	}

	/**
	 * redirect the log to target callback
	 * @param cbl the log callback, no null
	 */
	public static void log_set(final ggml_log_callback cbl) {
		api.whisper_log_set(cbl, null);
	}

	/**
	 * get the total vulkan
	 * @return the count value
	 * @throws NullPointerException if vulkan not load
	 */
	public static int vulkan_count() throws NullPointerException {
		return api_ggml_vulkan.ggml_backend_vk_get_device_count();
	}
	/**
	 * get the vulkan memory
	 * @param index the index, from 0 to {@link #vulkan_count()}
	 * @return the memory size in bytes
	 * @throws NullPointerException if vulkan not load
	 */
	public static long vulkan_memory(final int index) throws NullPointerException {
		try (final Memory free = new Memory(8); Memory total = new Memory(8)) {
			api_ggml_vulkan.ggml_backend_vk_get_device_memory(index, free, total);
			/*
			 * ggml_backend_vk_get_device_memory in whisper.cpp/ggml/src/ggml-vulkan/ggml-vulkan.cpp
			 *
			 *	*total = heap.size;
			 *	*free = heap.size;
			 *
			 * so free = total, skip
			 */
			return total.getLong(0);
		}
	}
	/**
	 * get the vulkan device description
	 * @param index the index, from 0 to {@link #vulkan_count()}
	 * @return the device description, such NVIDIA GeForce GTX 9900
	 * @throws NullPointerException if vulkan not load
	 */
	public static String vulkan_description(final int index) throws NullPointerException {
		try (final Memory buffer = new Memory(1024)) {
			/*
			 * for example locale char set UTF-8.
			 *
			 * this call fill the char* by UTF-8,
			 * but Java char is UTF-16BE,
			 *  so send char[] but keep UTF-8 bytes in memory, to show is wrong value,
			 *  need decode by UTF-16BE, then encode to UTF-8.
			 * so do in memory way.
			 */
			api_ggml_vulkan.ggml_backend_vk_get_device_description(index, buffer, new NativeLong(buffer.size()));
			return buffer.getString(0);
		}
	}

	private Pointer p_wcp, p_ctx, p_params;
	private static whisper_full_params.ByValue params;
	public WhisperCJ() {}

	/**
	 * open the whisper
	 * @param gpu true use GPU
	 * @param model_file the model file use to, no null
	 * @param strategy the strategy, such {@link whisper_sampling_strategy#WHISPER_SAMPLING_BEAM_SEARCH}
	 * @param language the whisper language, "auto" for auto-detect, "en" by default
	 * @param cbp the callback to modify params, or null
	 * @param cbv the callback to get progress, or null
	 * @return state code, 0 succeed, -1 or less failed, -2 another opened but no close yet.
	 */
	public int open(final boolean gpu, final String model_file,
		final int strategy, final String language, final PARAMS_CALLBACK cbp
	) {
		if (null != params) return -2;
		p_wcp = api.whisper_context_default_params_by_ref();
		final whisper_context_params.ByValue wcp = new whisper_context_params.ByValue(p_wcp);
		wcp.read();
		wcp.use_gpu = (gpu ? boolC99.TRUE : boolC99.FALSE);
		wcp.write();

		p_ctx = api.whisper_init_from_file_with_params(model_file, wcp);
		if (p_ctx == null) return -1;

		/* example create whisper_context and state
		whisper_context.ByValue ctx = new whisper_context.ByValue(p_ctx);
		ctx.read();
		ctx.state = api.whisper_init_state(p_ctx);
		 */

		p_params = api.whisper_full_default_params_by_ref(strategy);
		params = new whisper_full_params.ByValue(p_params);
		params.read();
		params.strategy = strategy;
		params.language = language;
		if (null != cbp) {
			cbp.on_modify_params(params);
		}
		//in some JVM with stack delay problem, u may need call this twice.
		params.write();

		return 0;
	}

	/**
	 * whisper the audio
	 * @param samples the audio samples data, no null, use from 0 to it {@link FloatBuffer#position()}.
	 * @return state code, -1 or less failed.
	 * @throws NullPointerException if open() failed or never call
	 */
	public int whisper(final FloatBuffer samples) throws NullPointerException {
		return api.whisper_full(p_ctx, params, samples.array(), samples.position());
	}

	/**
	 * VAD the segment
	 * @param cbs the segment callback
	 * @param index the index number
	 * @param start_ms the start in milliseconds
	 * @param end_ms the end in milliseconds
	 * @param time_offset the time offset
	 * @param text the text
	 */
	protected void vad_segment(final FloatBuffer samples, final SEGMENT_CALLBACK cbs,
		final int index, final int index_offset, final long start_ms,
		final long end_ms, final long time_offset, final String text
	) {
		/* check the samples for voice, change the segment times here if need */
		cbs.on_segment(index_offset + index, time_offset + start_ms, time_offset + end_ms, text);
	}

	/**
	 * get the segments
	 * @param samples the samples use to call {@link #whisper(FloatBuffer)} before,
	 *  or null if never use in {@link #vad_segment(FloatBuffer, SEGMENT_CALLBACK, int, long, long, long, String)}
	 * @param cbs the callback each segment, no null
	 * @param time_offset the segment times offset in milliseconds or 0
	 * @return the language id
	 * @throws NullPointerException if open() failed or never call or cbs null
	 */
	public int segments(final FloatBuffer samples, final SEGMENT_CALLBACK cbs, final int index_offset, final long time_offset) throws NullPointerException {
		long s, e; String text;
		for (int i=0; i<api.whisper_full_n_segments(p_ctx); i++) {
			s = api.whisper_full_get_segment_t0(p_ctx, i) * 10;
			e = api.whisper_full_get_segment_t1(p_ctx, i) * 10;
			text = api.whisper_full_get_segment_text(p_ctx, i);
			vad_segment(samples, cbs, i, index_offset, s, e, time_offset, text);
		}
		return api.whisper_full_lang_id(p_ctx);
	}

	@Override
	public void close() {
		if (null != p_ctx) api.whisper_free(p_ctx);
		if (null != p_wcp) api.whisper_free_context_params(p_wcp);
		if (null != p_params) api.whisper_free_params(p_params);
		params = null;
	}
}
