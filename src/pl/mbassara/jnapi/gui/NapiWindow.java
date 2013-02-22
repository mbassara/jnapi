package pl.mbassara.jnapi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import pl.mbassara.jnapi.mediainfo.MediaInfo;
import pl.mbassara.jnapi.model.Subtitles;
import pl.mbassara.jnapi.model.parsers.MPL2Parser;
import pl.mbassara.jnapi.model.parsers.MicroDVDParser;
import pl.mbassara.jnapi.model.parsers.SubRipParser;
import pl.mbassara.jnapi.model.parsers.TMPlayerParser;
import pl.mbassara.jnapi.model.parsers.WrongSubtitlesFormatException;
import pl.mbassara.jnapi.services.FileHelper;
import pl.mbassara.jnapi.services.napiprojekt.NapiResult;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt.Lang;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt.Mode;

public class NapiWindow extends JFrame {

	private static final long serialVersionUID = 5577019952587334306L;
	private final NapiWindow thisReference = this;

	private final int HEIGHT = 220;
	private final int WIDTH = 500;

	private final JTextField filePathTextField = new JTextField();
	private final JButton openFileButton = new JButton("Select Movie File");
	private final JPanel mainPanel = new JPanel(new BorderLayout());
	private final JPanel fileSelectionPanel = new JPanel(new FlowLayout(
			FlowLayout.CENTER));
	private final JPanel northPanel = new JPanel(new BorderLayout());
	private final JButton startButton = new JButton("Fetch data");
	private final JButton saveButton = new JButton("Save subtitles");
	private final JCheckBox saveCoverCheckBox = new JCheckBox("Save cover");
	private final JComboBox charsetComboBox = new JComboBox(new String[] {
			"windows-1250", "ISO-8859-2", "UTF-8" });
	private final JComboBox formatComboBox = new JComboBox(new String[] {
			"MicroDVD", "SubRip", "MPL2", "TMPlayer" });
	private final JLabel formatLabel = new JLabel("Format:");
	private final JLabel charsetLabel = new JLabel("Charset:");
	private final JComboBox langComboBox = new JComboBox(new String[] {
			"Polish", "English" });
	private final NapiprojektMovieInfoPanel napiprojektInfoPanel = NapiprojektMovieInfoPanel
			.getInstance();

	private final NapiResult[] napiResult = new NapiResult[1];
	private final MediaInfo mediaInfo = new MediaInfo();
	private final File[] lastUsedDirectory = new File[1];

	public NapiWindow() {
		initialize();
		initializeNorthPanel();
		initializeOptionsPanel();
		pack();
	}

	private void initialize() {
		setResizable(false);
		setContentPane(new JPanel(new BorderLayout()));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setTitle("JNapi v0.1");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(mainPanel, BorderLayout.CENTER);
		add(napiprojektInfoPanel, BorderLayout.SOUTH);
		napiprojektInfoPanel.setEnabled(false);
		setPreferredSize(new Dimension(WIDTH, HEIGHT
				+ napiprojektInfoPanel.getHeight()));

		try {
			Image icon = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("icon.png"));
			setIconImage(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeNorthPanel() {
		mainPanel.add(northPanel, BorderLayout.NORTH);

		filePathTextField.setPreferredSize(new Dimension(250, 25));
		filePathTextField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		filePathTextField.setEditable(false);
		filePathTextField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				boolean enabled = filePathTextField.getText().length() > 0;
				startButton.setEnabled(enabled);
				langComboBox.setEnabled(enabled);
			}
		});
		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser(lastUsedDirectory[0]);
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setApproveButtonText("Select");
				chooser.setDialogTitle("Open video file");
				chooser.setFileFilter(new MediaFileFilter());
				int result = chooser.showOpenDialog(thisReference);

				if (result != JFileChooser.APPROVE_OPTION)
					return;

				lastUsedDirectory[0] = chooser.getSelectedFile()
						.getParentFile();

				try {
					mediaInfo.open(chooser.getSelectedFile());
					filePathTextField.setText(chooser.getSelectedFile()
							.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}

				setOptionsEnabled(false);
				napiprojektInfoPanel.setContent(null);
			}
		});

		fileSelectionPanel.add(new Label("File path:"));
		fileSelectionPanel.add(filePathTextField);
		fileSelectionPanel.add(openFileButton);
		northPanel.add(fileSelectionPanel, BorderLayout.NORTH);
		JPanel fetchPanel = new JPanel(new GridLayout(1, 2));
		northPanel.add(fetchPanel, BorderLayout.SOUTH);

		startButton.setEnabled(false);
		langComboBox.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					setOptionsEnabled(false);
					napiprojektInfoPanel.setContent(null);

					Lang lang = langComboBox.getSelectedItem().toString()
							.equals("English") ? Lang.ENG : Lang.PL;

					napiResult[0] = Napiprojekt
							.request(new File(filePathTextField.getText()),
									Mode.SUBS_COVER, lang);

					if (!napiResult[0].isStatus()) {
						JOptionPane
								.showMessageDialog(
										thisReference,
										"Can't find suitable subtitles in napiprojekt database.",
										"Subtitles not found",
										JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					setOptionsEnabled(true);

					if (!napiResult[0].isCoverStatus())
						return;

					napiprojektInfoPanel.setContent(napiResult[0]);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(thisReference,
							"File with given path doesn't exists!",
							"File not found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel startButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		fetchPanel.add(langPanel);
		fetchPanel.add(startButtonPanel);
		langPanel.add(new JLabel("Language:"));
		langPanel.add(langComboBox);
		startButtonPanel.add(startButton);
	}

	private void initializeOptionsPanel() {
		JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
		mainPanel.add(optionsPanel, BorderLayout.CENTER);
		setOptionsEnabled(false);

		JPanel charsetPanel = new JPanel(new GridLayout(2, 1));
		optionsPanel.add(charsetPanel);
		charsetPanel.add(charsetLabel);
		charsetPanel.add(charsetComboBox);

		JPanel formatPanel = new JPanel(new GridLayout(2, 1));
		optionsPanel.add(formatPanel);
		formatPanel.add(formatLabel);
		formatPanel.add(formatComboBox);

		JPanel savingPanel = new JPanel(new GridLayout(2, 1));
		optionsPanel.add(savingPanel);
		savingPanel.add(saveCoverCheckBox);
		savingPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String proposedPath = filePathTextField.getText();
				proposedPath = proposedPath.substring(0,
						proposedPath.lastIndexOf("."))
						+ ".txt";
				JFileChooser chooser = new JFileChooser(lastUsedDirectory[0]);
				chooser.setSelectedFile(new File(proposedPath));
				chooser.setDialogTitle("Save subtitles");
				int result = chooser.showSaveDialog(thisReference);

				if (result != JFileChooser.APPROVE_OPTION)
					return;

				lastUsedDirectory[0] = chooser.getSelectedFile()
						.getParentFile();

				BufferedOutputStream coverStream = null;
				byte[] data = FileHelper.base64ToByteArray(napiResult[0]
						.getSubsAsciiBin());

				double fps = Double.parseDouble(mediaInfo.get(
						MediaInfo.StreamKind.Video, 0, "FrameRate"));
				Subtitles subtitles = null;
				try {
					subtitles = new MicroDVDParser().parse(data,
							"windows-1250", fps);
				} catch (WrongSubtitlesFormatException e) {
					System.out.println("Error in line: "
							+ e.getWrongLine()
							+ "\nParsing with MicroDVDParser failed. Trying SubRip.\n");
				}
				if (subtitles == null) {
					try {
						subtitles = new SubRipParser().parse(data,
								"windows-1250", fps);
					} catch (WrongSubtitlesFormatException e) {
						System.out.println("Error in line: "
								+ e.getWrongLine()
								+ "\nParsing with SubRipParser failed. Trying MPL2.\n");
					}
				}
				if (subtitles == null) {
					try {
						subtitles = new MPL2Parser().parse(data,
								"windows-1250", fps);
					} catch (WrongSubtitlesFormatException e) {
						System.out.println("Error in line: "
								+ e.getWrongLine()
								+ "\nParsing with MPL2Parser failed. Trying TMPlayer.\n");
					}
				}
				if (subtitles == null) {
					try {
						subtitles = new TMPlayerParser().parse(data,
								"windows-1250", fps);
					} catch (WrongSubtitlesFormatException e) {
						System.out.println("Error in line: "
								+ e.getWrongLine()
								+ "\nParsing with TMPlayerParser failed. Can't save this file.\n");
					}
				}
				if (subtitles == null) {
					JOptionPane
							.showMessageDialog(
									thisReference,
									"Cannot save, downloaded subtitles are in unsupported format.",
									"Cannot save", JOptionPane.ERROR_MESSAGE);
					return;
				}

				subtitles.save(formatComboBox.getSelectedItem().toString(),
						chooser.getSelectedFile(), charsetComboBox
								.getSelectedItem().toString());

				JOptionPane.showMessageDialog(thisReference,
						"Subtitles saved as: "
								+ chooser.getSelectedFile().getName(),
						"Subtitles saved", JOptionPane.INFORMATION_MESSAGE);

				if (!saveCoverCheckBox.isSelected())
					return;

				try {
					coverStream = new BufferedOutputStream(
							new FileOutputStream(new File(chooser
									.getSelectedFile().getParent()
									+ File.separator + "folder.jpg")));

					data = FileHelper.base64ToByteArray(napiResult[0]
							.getCoverAsciiBin());
					coverStream.write(data);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (coverStream != null) {
							coverStream.flush();
							coverStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void setOptionsEnabled(boolean enabled) {
		formatLabel.setEnabled(enabled);
		charsetLabel.setEnabled(enabled);
		charsetComboBox.setEnabled(enabled);
		formatComboBox.setEnabled(enabled);
		saveCoverCheckBox.setEnabled(enabled);
		saveButton.setEnabled(enabled);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new NapiWindow().setVisible(true);
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null,
					"mediainfo.dll library is not found!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
