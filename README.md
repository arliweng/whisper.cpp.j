# whisper.cpp for java

base on [JNA](https://github.com/java-native-access/jna) bindings for [whisper.cpp](https://github.com/ggml-org/whisper.cpp), use Dynamic Library without JNI. 

```java
try (final WhisperCJ wcj = new WhisperCJ()) {
	wcj.open(true, "../ggml-small.bin",
		whisper_sampling_strategy.WHISPER_SAMPLING_BEAM_SEARCH,
		"en", new WhisperCJ.PARAMS_CALLBACK<Float>() {
			@Override
			public void on_modify_params(final whisper_full_params params, final Float v) {
				params.no_speech_thold = v;
			}
		}, 0.5f
	);

	final FloatBuffer samples = read_audio();
	wcj.whisper(samples);
	wcj.segments(samples, new WhisperCJ.SEGMENT_CALLBACK<Object, RuntimeException>() {
		@Override
		public void on_segment(final int id, final long start, final long end, final String text, final Object v) {
			System.out.println("id " + id + "," + start + "," + end + " " + text);
		}
	}, null, 0, 0);
}
```

for more see [examples/Demo1.java](https://github.com/arliweng/whisper.cpp.j/blob/main/src/cpp/whisper/examples/Demo1.java)
