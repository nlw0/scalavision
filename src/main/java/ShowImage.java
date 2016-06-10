import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ShowImage extends Panel {
    BufferedImage image;

    public static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        byte[] data = new byte[in.rows() * in.cols() * (int) in.elemSize()];
        int type;
        in.get(0, 0, data);

        type = in.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        out = new BufferedImage(in.cols(), in.rows(), type);

        out.getRaster().setDataElements(0, 0, in.cols(), in.rows(), data);
        return out;
    }

    public ShowImage(Mat im) {
        image = mat2Img(im);
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public void update(Mat input) {
        byte[] data = new byte[input.rows() * input.cols() * (int) input.elemSize()];
        input.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, input.cols(), input.rows(), data);
    }
}