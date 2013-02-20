package pl.mbassara.jnapi.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 * Label which provides hyperlink functionality. On click, given site opens in
 * web browser.
 * 
 * @author Maciek
 * 
 */
public class LinkLabel extends JButton implements ActionListener {

	private static final long serialVersionUID = -5569311370440891748L;

	private URI target;

	/**
	 * 
	 * @param target
	 *            Target site address which has to be opened in web browser.
	 */
	public LinkLabel(URI target) {
		this(target, target.toString());
	}

	/**
	 * 
	 * @param target
	 *            Target site address which has to be opened in web browser.
	 * @param text
	 *            Text which appears on this label.
	 */
	public LinkLabel(URI target, String text) {
		super(text);
		this.target = target;
		setText("<HTML><FONT color=\"#000099\"><U>" + text
				+ "</U></FONT></HTML>");
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorderPainted(false);
		setOpaque(false);
		setContentAreaFilled(false);
		setToolTipText(target.toString());
		addActionListener(this);
		setFocusable(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(target);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this,
					"Can't open this link in web browser.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}