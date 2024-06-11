package org.fieldwire.challenge.util;

import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@UtilityClass
public class TestUtil {

	public byte[] getImageBytes(String imageName) {
		try (
				InputStream inputStream = TestUtil.class.getResourceAsStream(imageName);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		) {
			BufferedImage image = ImageIO.read(inputStream);
			ImageIO.write(image, "jpg", outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
}
