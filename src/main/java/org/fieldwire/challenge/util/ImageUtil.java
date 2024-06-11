package org.fieldwire.challenge.util;

import lombok.experimental.UtilityClass;
import org.fieldwire.challenge.exception.ErrorType;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Floorplan;
import org.springframework.core.io.ByteArrayResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@UtilityClass
public class ImageUtil {

	public byte[] resizeOriginal(byte[] original, int width, int height) {
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(original);) {
			BufferedImage originalFloorplan = ImageIO.read(byteArrayInputStream);
			BufferedImage resizedFloorplan = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			resizedFloorplan
					.createGraphics()
					.drawImage(originalFloorplan, 0, 0, width, height, null);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(resizedFloorplan, "jpg", byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "cannot resize floorplan");
		}
	}

	public ByteArrayResource createZip(Floorplan floorplan) {
		try (
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)
		) {
			addFileToZip(zipOutputStream, floorplan.getName().concat("_original.jpg"), floorplan.getOriginal());
			addFileToZip(zipOutputStream, floorplan.getName().concat("_thumb.jpg"), floorplan.getThumb());
			addFileToZip(zipOutputStream, floorplan.getName().concat("_large.jpg"), floorplan.getLarge());

			return new ByteArrayResource(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "cannot download file");
		}
	}

	private void addFileToZip(ZipOutputStream zipOutputStream, String filename, byte[] data) throws IOException {
		ZipEntry zipEntry = new ZipEntry(filename);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.write(data);
		zipOutputStream.closeEntry();
	}
}
