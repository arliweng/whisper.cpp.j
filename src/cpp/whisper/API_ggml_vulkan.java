package cpp.whisper;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;

/**
 * the GGML vulkan API
 * @see whisper.cpp/ggml/include/ggml-vulkan.h
 * @author arliweng@outlook.com
 */
public interface API_ggml_vulkan extends Library {
	/**
	 * get the device size.
	 * @return 0 or 1 current enabled?
	 */
	int ggml_backend_vk_get_device_count();
	/**
	 * get the device description.<br>
	 * fill the description to buffer, UTF-8 but Java is UTF-16BE.
	 * @param device the device index
	 * @param description the buffer
	 * @param description_size the size of buffer
	 */
	void ggml_backend_vk_get_device_description(final int device, Memory description, NativeLong description_size);
	/**
	 * get the device memory.<br>
	 * use Memory total = new Memory(8), then total.getLong(0)
	 * @param device the device index
	 * @param free free value fill to
	 * @param total total value fill to
	 */
	void ggml_backend_vk_get_device_memory(int device, Memory free, Memory total);
}
