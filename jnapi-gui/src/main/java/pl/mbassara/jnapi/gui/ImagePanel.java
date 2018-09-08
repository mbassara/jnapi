package pl.mbassara.jnapi.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -1623285541442018663L;
	private InnerImagePanel imagePanel;

	public ImagePanel() {
		this(null);
	}

	public ImagePanel(byte[] imageData) {
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));

		if (imageData != null)
			setImage(imageData);
	}

	public void setImage(byte[] imageData) {
		if (this.imagePanel != null)
			remove(this.imagePanel);

		this.imagePanel = new InnerImagePanel(imageData);
		add(imagePanel);
	}

	private class InnerImagePanel extends JPanel {
		private static final long serialVersionUID = -6263483934125729696L;
		private BufferedImage image;

		public InnerImagePanel(byte[] imageData) {
			setPreferredSize(new Dimension(150, 200));
			try {
				image = ImageIO.read(new ByteArrayInputStream(imageData));
			} catch (IOException ex) {
				Global.getInstance().getLogger().warning(ex.toString());
				Global.getInstance().getLogger().warning(ex.toString());
				ex.printStackTrace();
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}

	}
}