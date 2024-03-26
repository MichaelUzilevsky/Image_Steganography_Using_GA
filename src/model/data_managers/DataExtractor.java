package model.data_managers;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.data_managers.image_metedate.ImageMetadata;
import model.data_managers.image_metedate.MetadataSerializer;
import model.utils.ConstantsClass;

public class DataExtractor {
    private final Image stegoImage;

    public DataExtractor(Image stegoImage) {
        this.stegoImage = stegoImage;
    }
    private BitArray extractBitsFromImage(int totalBitsToExtract) {
        int width = (int) stegoImage.getWidth();
        int height = (int) stegoImage.getHeight();
        BitArray extractedBits = new BitArray(totalBitsToExtract);

        int extractedBitCount = 0;
        for (int y = 0; y < height && extractedBitCount < totalBitsToExtract; y++) {
            for (int x = 0; x < width && extractedBitCount < totalBitsToExtract; x++) {
                Color color = stegoImage.getPixelReader().getColor(x, y);
                // Extracting bits from each color component, in the order red, green, blue
                int[] colors = {(int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255)};

                for (int colorIndex = 0; colorIndex < ConstantsClass.BYTES_IN_PIXEL; colorIndex++) {
                    for (int bitIndex = 0; bitIndex < ConstantsClass.BITS_REPLACED_PER_BYTE; bitIndex++) {
                        // Mask the LSBs of the color component to extract the bit
                        boolean bit = (colors[colorIndex] & (1 << bitIndex)) != 0;
                        if (extractedBitCount < totalBitsToExtract) {
                            extractedBits.set(extractedBitCount++, bit);
                        }
                    }
                }
            }
        }

        return extractedBits;
    }

    // Extract and deserialize metadata
    public ImageMetadata extractMetadata() {
        int metadataSize = calculateMetadataSize(); // Calculate the size of metadata based on image dimensions
        BitArray metadataBits = extractBitsFromImage(metadataSize);
        return MetadataSerializer.deserialize(metadataBits, (int) stegoImage.getWidth(), (int) stegoImage.getHeight());
    }

    private int calculateMetadataSize() {
        return ImageMetadata.getSizeInBits((int)stegoImage.getWidth(), (int)stegoImage.getHeight());
    }

    public BitArray extractData(ImageMetadata metadata) {
        int metadataSizeWithPadding = calculateMetadataSize() +
                (ConstantsClass.ROUND_BITARRAY_TO - (calculateMetadataSize() % ConstantsClass.ROUND_BITARRAY_TO)) %
                        ConstantsClass.ROUND_BITARRAY_TO;

        // Calculate the total bits to extract, which should ideally come from metadata or be known a priori
        int totalBitsToExtract = calculateTotalBitsToExtract(metadata);
        BitArray allBits = extractBitsFromImage(totalBitsToExtract);

        // Extract just the data, starting after the metadata and its padding
        int dataSize = metadata.getDataLength(); // Assuming this directly gives the data size without padding
        BitArray dataBits = new BitArray(dataSize);

        for (int i = 0; i < dataSize; i++) {
            boolean bit = allBits.get(metadataSizeWithPadding + i); // Start right after metadata and its padding
            dataBits.set(i, bit);
        }

        return dataBits;
    }

    private int calculateTotalBitsToExtract(ImageMetadata metadata) {

        return calculateMetadataSize() +
                (ConstantsClass.ROUND_BITARRAY_TO - (calculateMetadataSize() % ConstantsClass.ROUND_BITARRAY_TO)) %
                ConstantsClass.ROUND_BITARRAY_TO +
                metadata.getDataLength() +
                (ConstantsClass.ROUND_BITARRAY_TO - (metadata.getDataLength() % ConstantsClass.ROUND_BITARRAY_TO)) %
                        ConstantsClass.ROUND_BITARRAY_TO;
    }

}
