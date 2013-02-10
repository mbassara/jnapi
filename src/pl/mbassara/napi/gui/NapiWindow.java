package pl.mbassara.napi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.mbassara.napi.utils.NapiResult;
import pl.mbassara.napi.utils.Napiprojekt;
import pl.mbassara.napi.utils.Napiprojekt.Mode;

public class NapiWindow extends JFrame {

	private static final long serialVersionUID = 5577019952587334306L;
	private final NapiWindow thisReference = this;

	private final JTextField filePathTextField = new JTextField();
	private final JButton openFileButton = new JButton("Select Movie File");
	private final JPanel mainPanel = new JPanel(new BorderLayout());
	private final JPanel fileSelectionPanel = new JPanel(new FlowLayout(
			FlowLayout.CENTER));
	private final JButton startButton = new JButton("Start");

	private final NapiResult[] napiResult = new NapiResult[1];

	public NapiWindow() {
		initialize();
	}

	private void initialize() {
		setSize(new Dimension(600, 500));
		setResizable(false);
		setLayout(new BorderLayout());
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(mainPanel, BorderLayout.CENTER);

		filePathTextField.setPreferredSize(new Dimension(350, 25));
		filePathTextField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setApproveButtonText("Select");
				int result = chooser.showOpenDialog(thisReference);

				if (result != JFileChooser.APPROVE_OPTION)
					return;

				try {
					filePathTextField.setText(chooser.getSelectedFile()
							.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		fileSelectionPanel.add(new Label("File path:"));
		fileSelectionPanel.add(filePathTextField);
		fileSelectionPanel.add(openFileButton);
		mainPanel.add(fileSelectionPanel, BorderLayout.NORTH);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					napiResult[0] = Napiprojekt.request(new File(
							filePathTextField.getText()), Mode.SUBS_COVER);

					JOptionPane.showMessageDialog(thisReference,
							napiResult[0].getTitle());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(thisReference,
							"File with given path doesn't exists!",
							"File not found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mainPanel.add(startButton, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {
		new NapiWindow().setVisible(true);
	}

}
