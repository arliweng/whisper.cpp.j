package cpp.whisper.examples;

import java.io.File;
import java.nio.FloatBuffer;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sun.jna.Pointer;

import cpp.whisper.WhisperCJ;
import cpp.whisper.callbacks.abort_callback;
import cpp.whisper.callbacks.ggml_log_callback;
import cpp.whisper.callbacks.new_segment_callback;
import cpp.whisper.callbacks.progress_callback;
import cpp.whisper.enums.ggml_log_level;
import cpp.whisper.enums.whisper_sampling_strategy;
import cpp.whisper.struct.boolC99;
import cpp.whisper.struct.whisper_full_params;

/**
 * the whisper.cpp vulkan example.
 * @author arliweng@outlook.com
 */
public class Demo1 implements ggml_log_callback, WhisperCJ.PARAMS_CALLBACK<String, RuntimeException>, WhisperCJ.SEGMENT_CALLBACK<String, RuntimeException> {
	private final SimpleDateFormat sdf_srt;
	protected Demo1() {
		sdf_srt = new SimpleDateFormat("HH:mm:ss,SSS", Locale.ENGLISH);
		sdf_srt.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
	}

	private long duration_ms;
	private FloatBuffer read_samples(final String wav_file) throws Exception {
		/*
		 * just the example, should replace your decoder, such FFmpeg,
		 *  the audio frame data from avcodec.avcodec_receive_frame()
		 * here AudioSystem in most VM only support WAV,AU etc..
		 */
		try (AudioInputStream _ais = AudioSystem.getAudioInputStream(new File(wav_file))) {
			/*
			 * about duration milliseconds here:
			 * seconds	= total bytes / bytes per second
			 *        	= (frame size * bytes per frame) / (sample rate * bytes per sample)
			 *        	= (frame size * (bits /8)) / (sample rate * (bits /8))
			 *        	= frame size * (bits /8) / sample rate / (bits /8)
			 *        	= frame size / sample rate
			 * milliseconds = 1000 * seconds
			 *             	= 1000 * frame size / sample rate
			 */
			duration_ms = (long) Math.ceil(1000 * _ais.getFrameLength() / _ais.getFormat().getSampleRate());
			//convert to 16KHz, 32 bits, mono, signed
			try (AudioInputStream ais = AudioSystem.getAudioInputStream(new AudioFormat(
				WhisperCJ.AUDIO_SAMPLE_RATE, 32, 1, true, true
			), _ais)) {
				//the locale frame buffer, AudioInputStream only
				final byte[] bb = new byte[ais.getFormat().getFrameSize()];
				//the samples, them all
				final FloatBuffer fb = FloatBuffer.allocate((int) Math.ceil(duration_ms * WhisperCJ.SAMPLES_PER_MILLISECONDS));
				for (int v;;) {
					if (ais.read(bb) <= 0) break;
					//bigEndian set above
					v = ((0xff & bb[0]) << 24);
					v |= ((0xff & bb[1]) << 16);
					v |= ((0xff & bb[2]) << 8);
					v |= ((0xff & bb[3]) << 0);
					//integer 32bits to float 32bits
					fb.put((float) ((double)v / Integer.MAX_VALUE));
				}
				return fb;
			}
		}
	}

	protected long duration_ms() {
		return duration_ms;
	}

	@Override
	public void on_log(final int level, final String text, final Pointer user_data) {
		switch (level) {
			case ggml_log_level.GGML_LOG_LEVEL_CONT:
			case ggml_log_level.GGML_LOG_LEVEL_ERROR:
			default:
				System.err.print(Integer.toHexString(level));
				System.err.print(' ');
				System.err.println(text.trim());
				break;
		}
	}

	private volatile boolC99 abort = boolC99.FALSE;
	@SuppressWarnings("unused")
	@Override
	public void on_modify_params(final whisper_full_params params, final String model_file) {
		params.no_speech_thold = 0.5f;
		//remove this if unneeded, is inside loop many ask
		params.abort_callback = new abort_callback() {
			@Override
			public boolC99 answer_abort(final Pointer data) {
				return abort;
			}
		};
		//the progress callback, read whisper_full_params about warning, always as new class
		params.progress_callback = new progress_callback() {
			@Override
			public void on_progress(final Pointer ctx, final Pointer state, final int progress, final Pointer user_data) {
				System.out.println(progress + "%");
				if (progress == 100) {
					System.out.println("--- srt coming");
				}
				if (">(-_-!)<" == "ami bad guy? make true here to abort") {
					abort = boolC99.TRUE;
				}
			}
		};

		if ("need segment immediate not after" == "make this true") { //just example, never hit
			params.new_segment_callback = new new_segment_callback() {
				@Override
				public void on_segment(final Pointer ctx, final Pointer state, final int n_new, final Pointer user_data) {
					if ("whisper_full_with_state" == "call before, not whisper_full") {
					 	long s, e; String text;
					 	final int size = WhisperCJ.api().whisper_full_n_segments_from_state(state);
					 	for (int i = size -n_new; i < size; i++) {
					 		s = WhisperCJ.api().whisper_full_get_segment_t0_from_state(state, i) * 10;
					 		e = WhisperCJ.api().whisper_full_get_segment_t1_from_state(state, i) * 10;
					 		text = WhisperCJ.api().whisper_full_get_segment_text_from_state(state, i);
					 		Demo1.this.on_segment(i, s, e, text, null);
					 	}
					} else {
					 	long s, e; String text;
					 	final int size = WhisperCJ.api().whisper_full_n_segments(ctx);
					 	for (int i = size -n_new; i < size; i++) {
					 		s = WhisperCJ.api().whisper_full_get_segment_t0(ctx, i) * 10;
					 		e = WhisperCJ.api().whisper_full_get_segment_t1(ctx, i) * 10;
					 		text = WhisperCJ.api().whisper_full_get_segment_text(ctx, i);
					 		Demo1.this.on_segment(i, s, e, text, null);
					 	}
					}
				}
			};
		}
	}

	private final StringBuffer sb_sdf_srt = new StringBuffer(16);
	private final FieldPosition fp_sdf_srt = new FieldPosition(0);
	@Override
	public void on_segment(final int id, final long start, final long end, final String text, final String model_file) {
		//example SRT format, should replace your code, without SimpleDateFormat.
		System.out.println(id +1);
		sdf_srt.format(start, sb_sdf_srt, fp_sdf_srt);
		System.out.print(sb_sdf_srt);
			sb_sdf_srt.setLength(0);
			System.out.print(" --> ");
			sdf_srt.format(end, sb_sdf_srt, fp_sdf_srt);
			System.out.println(sb_sdf_srt);
			sb_sdf_srt.setLength(0);
		System.out.println(text);
		System.out.println();
	}

	public static void main(final String[] args) throws Exception {
		final String wav_file = "/home/arli/Downloads/demo.wav"; //the wav file
		final String model_file = "/home/arli/Downloads/ggml-small.bin"; //the model file
		final String lib_directory = "/home/arli/Downloads/lib/"; //libwhisper.* libggml*.*

		System.setProperty("jna.library.path", lib_directory);
		//example for CPU, vulkan, or CUDA if exist
		WhisperCJ.driver(true, true, false);

		final Demo1 d = new Demo1();
		WhisperCJ.log_set(d);

		try (final WhisperCJ wcj = new WhisperCJ()) {
			//open
			wcj.open(true, model_file, whisper_sampling_strategy.WHISPER_SAMPLING_BEAM_SEARCH, "en", d, model_file);
			//get the audio samples
			final FloatBuffer samples = d.read_samples(wav_file);
			//whisper
			wcj.whisper(samples);
			wcj.segments(samples, d, model_file, 0, 0);
		}
	}
}
