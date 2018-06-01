package pl.mbassara.jnapi.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import pl.mbassara.jnapi.Global;

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
	 * Creates empty LinkLabel
	 */
	public LinkLabel() {
		this(null, "", CENTER);
	}

	/**
	 * Creates LinkLabel for given target URI
	 * 
	 * @param target
	 *            Target site address which has to be opened in web browser.
	 */
	public LinkLabel(URI target) {
		this(target, target.toString(), CENTER);
	}

	/**
	 * Creates LinkLabel for given target URI and text
	 * 
	 * @param target
	 *            Target site address which has to be opened in web browser.
	 * @param text
	 *            Text which appears on this label.
	 */
	public LinkLabel(URI target, String text, int textAlignment) {
		super(text);
		this.target = target;
		setEnabled(target != null);
		setText(text);
		setHorizontalAlignment(textAlignment);
		setBorderPainted(false);
		setOpaque(false);
		setContentAreaFilled(false);
		setToolTipText((target != null ? target.toString() : ""));
		addActionListener(this);
		setFocusable(false);
	}

	public void setTarget(URI target) {
		setEnabled(target != null);
		this.target = target;
	}

	@Override
	public void setText(String text) {
		super.setText("<HTML><FONT color=\"#000099\"><U>" + text
				+ "</U></FONT></HTML>");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(target);
			} catch (IOException e) {
				Global.getInstance().getLogger().warning(e.toString());
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this,
					"Can't open this link in web browser.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}