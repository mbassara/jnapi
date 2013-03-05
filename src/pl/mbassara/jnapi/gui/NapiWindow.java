package pl.mbassara.jnapi.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import pl.mbassara.jnapi.Global;
import pl.mbassara.jnapi.mediainfo.MediaInfo;
import pl.mbassara.jnapi.model.Subtitles.Format;
import pl.mbassara.jnapi.services.ISubtitlesProvider;
import pl.mbassara.jnapi.services.Lang;
import pl.mbassara.jnapi.services.SubtitlesResult;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt;
import pl.mbassara.jnapi.services.opensubtitles.OpenSubtitles;

public class NapiWindow extends JFrame {

	private static final long serialVersionUID = 5577019952587334306L;
	private final NapiWindow thisReference = this;

	private final JTextField filePathTextField = new JTextField();
	private final JButton openFileButton = new JButton("Select Movie File");
	private final JPanel mainPane = new JPanel(new BorderLayout());
	private final JPanel fileSelectionPane = new JPanel(new FlowLayout(
			FlowLayout.CENTER));
	private final JPanel northPane = new JPanel(new GridLayout(2, 1));
	private final JComboBox charsetComboBox = new JComboBox(new String[] {
			"windows-1250", "ISO-8859-2", "UTF-8" });
	private final JComboBox formatComboBox = new JComboBox(new String[] {
			"MicroDVD", "SubRip", "MPL2", "TMPlayer" });
	private final JLabel formatLabel = new JLabel("Format:");
	private final JLabel charsetLabel = new JLabel("Charset:");
	private final JLabel langLabel = new JLabel("Language:");
	private final JComboBox langComboBox = new JComboBox(new String[] {
			"Polish", "English" });
	private final JCheckBox napiprojektCheckBox = new JCheckBox("Napiprojekt");
	private final JCheckBox opensubtitlesCheckBox = new JCheckBox(
			"OpenSubtitles.org");
	private final JScrollPane southPane = new JScrollPane();
	private final ResultsTable resultsTable = new ResultsTable(this);

	public NapiWindow() {
		initialize();
		initializeNorthPane();
		initializeOptionsPane();
		initializeSouthPane();
		pack();
	}

	private void initialize() {

		setResizable(false);
		setContentPane(new JPanel(new BorderLayout(5, 5)));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setTitle("JNapi v0.2");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);
		add(mainPane, BorderLayout.CENTER);

		setIconImage(Global.getInstance().getIcon());
	}

	private void initializeNorthPane() {
		mainPane.add(northPane, BorderLayout.NORTH);

		filePathTextField.setPreferredSize(new Dimension(250, 25));
		filePathTextField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		filePathTextField.setEditable(false);
		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser chooser = new JFileChooser(Global.getInstance()
						.getLastUsedDirectory());
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setApproveButtonText("Select");
				chooser.setDialogTitle("Open video file");
				chooser.setFileFilter(new MediaFileFilter());
				int option = chooser.showOpenDialog(thisReference);

				if (option != JFileChooser.APPROVE_OPTION)
					return;

				Global.getInstance().setLastUsedDirectory(
						chooser.getSelectedFile().getParentFile());

				resultsTable.clear();

				final Lang lang = Global.getInstance().getLang();

				final File selectedFile = chooser.getSelectedFile();

				setAllEnabled(false);
				thisReference.setCursor(Cursor
						.getPredefinedCursor(Cursor.WAIT_CURSOR));
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							boolean subtitlesFound = false;
							boolean napiTimeout = false;
							boolean opensubTimeout = false;
							ISubtitlesProvider subtitlesProvider;
							if (napiprojektCheckBox.isSelected()) {
								subtitlesProvider = new Napiprojekt();
								try {
									for (SubtitlesResult result : subtitlesProvider
											.downloadSubtitles(selectedFile,
													lang)) {
										resultsTable.addResult(result);
										subtitlesFound = true;
									}
								} catch (TimeoutException e) {
									Global.getInstance().getLogger()
											.warning(e.toString());
									e.printStackTrace();
									napiTimeout = true;
								}
							}
							if (opensubtitlesCheckBox.isSelected()) {
								subtitlesProvider = new OpenSubtitles();
								try {
									for (SubtitlesResult result : subtitlesProvider
											.downloadSubtitles(selectedFile,
													lang)) {
										resultsTable.addResult(result);
										subtitlesFound = true;
									}
								} catch (TimeoutException e) {
									Global.getInstance().getLogger()
											.warning(e.toString());
									e.printStackTrace();
									opensubTimeout = true;
								}
							}

							if (napiTimeout || opensubTimeout) {
								String serviceName = "", plural = "";
								if (napiTimeout && opensubTimeout) {
									serviceName = "both Napiprojekt and Opensubtitles";
									plural = "s";
								} else if (napiTimeout)
									serviceName = "Napiprojekt";
								else if (opensubTimeout)
									serviceName = "Opensubtitles";

								JOptionPane
										.showMessageDialog(
												thisReference,
												"Timeout occurred on "
														+ serviceName
														+ " server"
														+ plural
														+ ". It means that server"
														+ plural
														+ " could be temporarily unavailable.",
												"Timeout",
												JOptionPane.WARNING_MESSAGE);
								setAllEnabled(true);
							}
							if (subtitlesFound) {
								filePathTextField.setText(selectedFile
										.getName());

								Global.getInstance().setSelectedMovieFilePath(
										selectedFile.getCanonicalPath());
								setAllEnabled(true);
							} else if (!napiTimeout && !opensubTimeout) {
								JOptionPane
										.showMessageDialog(
												thisReference,
												"Can't find subtitles for this file in neither of selected databases.",
												"No results",
												JOptionPane.WARNING_MESSAGE);

								setAllEnabled(true);
							}

						} catch (FileNotFoundException e) {
							Global.getInstance().getLogger()
									.warning(e.toString());
							e.printStackTrace();
							JOptionPane
									.showMessageDialog(
											thisReference,
											"File with given path doesn't exists!",
											"File not found",
											JOptionPane.ERROR_MESSAGE);
							setAllEnabled(true);
						} catch (IOException e) {
							Global.getInstance().getLogger()
									.warning(e.toString());
							e.printStackTrace();
							setAllEnabled(true);
							setOptionsEnabled(false);
						} finally {
							thisReference
									.setCursor(Cursor
											.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}, "Subtitles searching Thread - NapiWindow").start();
			}
		});

		fileSelectionPane.add(new Label("File name:"));
		fileSelectionPane.add(filePathTextField);
		fileSelectionPane.add(openFileButton);
		northPane.add(fileSelectionPane);

		JPanel servicePane = new JPanel(new GridLayout(1, 2));
		JPanel napiprojektCheckBoxPane = new JPanel(new FlowLayout(
				FlowLayout.CENTER));
		JPanel opensubtitlesCheckBoxPane = new JPanel(new FlowLayout(
				FlowLayout.CENTER));

		northPane.add(servicePane);
		servicePane.add(napiprojektCheckBoxPane);
		servicePane.add(opensubtitlesCheckBoxPane);
		napiprojektCheckBoxPane.add(napiprojektCheckBox);
		opensubtitlesCheckBoxPane.add(opensubtitlesCheckBox);

		napiprojektCheckBox.setSelected(true);
		opensubtitlesCheckBox.setSelected(true);

	}

	private void initializeOptionsPane() {
		JPanel optionsPane = new JPanel(new GridLayout(1, 3, 20, 0));
		mainPane.add(optionsPane, BorderLayout.CENTER);
		setOptionsEnabled(false);

		JPanel charsetPane = new JPanel(new GridLayout(2, 1));
		optionsPane.add(charsetPane);
		charsetPane.add(charsetLabel);
		charsetPane.add(charsetComboBox);
		charsetComboBox.setSelectedItem(Global.getInstance()
				.getSubtitlesCharset().getValue());
		charsetComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Global.getInstance().setSubtitlesCharset(
						SubtitlesCharset.getValueOf(charsetComboBox
								.getSelectedItem().toString()));
			}
		});

		JPanel formatPane = new JPanel(new GridLayout(2, 1));
		optionsPane.add(formatPane);
		formatPane.add(formatLabel);
		formatPane.add(formatComboBox);
		formatComboBox.setSelectedItem(Global.getInstance().getFormat()
				.toString());
		formatComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Global.getInstance().setFormat(
						Format.valueOf(formatComboBox.getSelectedItem()
								.toString()));
			}
		});

		JPanel langPane = new JPanel(new GridLayout(2, 1));
		optionsPane.add(langPane);
		langPane.add(langLabel);
		langPane.add(langComboBox);
		langComboBox.setSelectedItem(Global.getInstance().getLang().getValue());
		langComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Global.getInstance().setLang(
						Lang.getValueOf(langComboBox.getSelectedItem()
								.toString()));
			}
		});

	}

	private void initializeSouthPane() {
		southPane.setViewportView(resultsTable);
		southPane.setPreferredSize(new Dimension(
				southPane.getPreferredSize().width, 150));
		// southPane.setBorder(BorderFactory.createEmptyBorder());
		add(southPane, BorderLayout.SOUTH);
	}

	private void setOptionsEnabled(boolean enabled) {
		formatLabel.setEnabled(enabled);
		charsetLabel.setEnabled(enabled);
		charsetComboBox.setEnabled(enabled);
		formatComboBox.setEnabled(enabled);
	}

	private void setAllEnabled(boolean enabled) {
		setOptionsEnabled(enabled);
		langLabel.setEnabled(enabled);
		langComboBox.setEnabled(enabled);
		openFileButton.setEnabled(enabled);
		napiprojektCheckBox.setEnabled(enabled);
		opensubtitlesCheckBox.setEnabled(enabled);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					setLookAndFeel();
					new MediaInfo();
					new NapiWindow().setVisible(true);
				} catch (UnsatisfiedLinkError e) {
					JOptionPane.showMessageDialog(null,
							"MediaInfo library is not found!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
