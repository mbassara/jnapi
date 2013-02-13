package pl.mbassara.napi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.mbassara.napi.connections.NapiFileHelper;
import pl.mbassara.napi.connections.NapiResult;
import pl.mbassara.napi.connections.Napiprojekt;
import pl.mbassara.napi.connections.Napiprojekt.Mode;
import pl.mbassara.napi.mediainfo.MediaInfo;
import pl.mbassara.napi.model.Subtitles;
import pl.mbassara.napi.model.parsers.MPL2Parser;
import pl.mbassara.napi.model.parsers.MicroDVDParser;
import pl.mbassara.napi.model.parsers.SubRipParser;
import pl.mbassara.napi.model.parsers.TMPlayerParser;
import pl.mbassara.napi.model.parsers.WrongSubtitlesFormatException;

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
	private final JPanel southPanel = new JPanel(new FlowLayout(
			FlowLayout.RIGHT));
	private final MovieInfoPanel[] infoPanel = new MovieInfoPanel[1];
	private final JPanel startButtonPanel = new JPanel(new FlowLayout(
			FlowLayout.CENTER));
	private final JButton startButton = new JButton("Fetch data");
	private final JButton saveButton = new JButton("Save subtitles");
	private final JCheckBox showInfoCheckBox = new JCheckBox("Show movie info");
	private final JCheckBox saveCoverCheckBox = new JCheckBox("Save cover");
	private final JComboBox charsetComboBox = new JComboBox(new String[] {
			"ISO-8859-2", "UTF-8", "windows-1250" });
	private final JComboBox formatComboBox = new JComboBox(new String[] {
			"SubRip", "MicroDVD", "MPL2", "TMPlayer" });
	private final JLabel formatLabel = new JLabel("Format:");
	private final JLabel charsetLabel = new JLabel("Charset:");

	private final NapiResult[] napiResult = new NapiResult[1];
	private final MediaInfo mediaInfo = new MediaInfo();

	public NapiWindow() {
		initialize();
		initializeNorthPanel();
		initializeSouthPanel();
		initializeOptionsPanel();
		pack();
	}

	private void initialize() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setResizable(false);
		setContentPane(new JPanel(new BorderLayout()));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setTitle("JNapi v0.1");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(mainPanel, BorderLayout.CENTER);

		setIconImage(new ImageIcon("./icon.png").getImage());
	}

	private void initializeNorthPanel() {
		mainPanel.add(northPanel, BorderLayout.NORTH);

		filePathTextField.setPreferredSize(new Dimension(250, 25));
		filePathTextField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
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
					mediaInfo.open(chooser.getSelectedFile());
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
		northPanel.add(fileSelectionPanel, BorderLayout.NORTH);
		northPanel.add(startButtonPanel, BorderLayout.SOUTH);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (infoPanel[0] != null) {
						if (showInfoCheckBox.isSelected())
							showInfoCheckBox.doClick();
						showInfoCheckBox.setEnabled(false);
						thisReference.remove(infoPanel[0]);
					}
					napiResult[0] = Napiprojekt.request(new File(
							filePathTextField.getText()), Mode.SUBS_COVER);

					if (!napiResult[0].isStatus())
						JOptionPane
								.showMessageDialog(
										thisReference,
										"Can't find suitable subtitles in napiprojekt database.",
										"Subtitles not found",
										JOptionPane.INFORMATION_MESSAGE);

					setOptionsEnabled(true);

					if (!napiResult[0].isCoverStatus())
						return;

					infoPanel[0] = new MovieInfoPanel(napiResult[0]);
					infoPanel[0].setVisible(false);
					thisReference.add(infoPanel[0], BorderLayout.SOUTH);
					showInfoCheckBox.setEnabled(true);
					showInfoCheckBox.doClick();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(thisReference,
							"File with given path doesn't exists!",
							"File not found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		startButtonPanel.add(startButton);
	}

	private void initializeSouthPanel() {
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		showInfoCheckBox.setEnabled(false);
		southPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
		southPanel.add(showInfoCheckBox);
		showInfoCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (infoPanel[0] == null)
					return;

				if (showInfoCheckBox.isSelected()) {
					thisReference.setPreferredSize(new Dimension(WIDTH, HEIGHT
							+ infoPanel[0].getHeight()));
					infoPanel[0].setVisible(true);
					thisReference.doLayout();
					thisReference.pack();
				} else {
					infoPanel[0].setVisible(false);
					thisReference
							.setPreferredSize(new Dimension(WIDTH, HEIGHT));
					thisReference.doLayout();
					thisReference.pack();
				}
			}
		});
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
				JFileChooser chooser = new JFileChooser();
				int result = chooser.showSaveDialog(thisReference);

				if (result == JFileChooser.APPROVE_OPTION) {
					BufferedOutputStream coverStream = null;
					byte[] data = NapiFileHelper
							.base64ToByteArray(napiResult[0].getSubsAsciiBin());

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
										"Cannot save",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					subtitles.save(formatComboBox.getSelectedItem().toString(),
							chooser.getSelectedFile(), charsetComboBox
									.getSelectedItem().toString());

					if (!saveCoverCheckBox.isSelected())
						return;

					try {
						coverStream = new BufferedOutputStream(
								new FileOutputStream(new File(chooser
										.getSelectedFile().getParent()
										+ File.separator + "folder.jpg")));

						data = NapiFileHelper.base64ToByteArray(napiResult[0]
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
		new NapiWindow().setVisible(true);
	}

}
