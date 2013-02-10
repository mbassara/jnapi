package pl.mbassara.napi.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -6263483934125729696L;
	private BufferedImage image;

	public ImagePanel(byte[] imageData) {
		setPreferredSize(new Dimension(150, 200));
		try {
			image = ImageIO.read(new ByteArrayInputStream(imageData));
			System.out.println(image.getWidth() + "x" + image.getHeight());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}

}
