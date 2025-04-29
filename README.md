# whisper.cpp for java

base on [JNA](https://github.com/java-native-access/jna) bindings for [whisper.cpp](https://github.com/ggml-org/whisper.cpp), use Dynamic Library without JNI. 

```java
try (final WhisperCJ wcj = new WhisperCJ()) {
	wcj.open(true, "../ggml-small.bin",
		whisper_sampling_strategy.WHISPER_SAMPLING_BEAM_SEARCH,
		"en", new WhisperCJ.PARAMS_CALLBACK() {
			@Override
			public void on_modify_params(whisper_full_params params) {
				params.no_speech_thold = 0.5f;
			}
		}, null
	);

	final FloatBuffer samples = read_audio();
	wcj.whisper(samples);
	wcj.segments(samples, new WhisperCJ.SEGMENT_CALLBACK() {
		@Override
		public void on_segment(WhisperCJ inc, int id, long start, long end, String text) {
			System.out.println("id " + id + "," + start + "," + end + " " + text);
		}
	}, 0, 0);
}
```

for more see [examples/Demo1.java](https://github.com/arliweng/whisper.cpp.j/blob/main/src/cpp/whisper/examples/Demo1.java)
