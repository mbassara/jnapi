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
import java.io.File;
import java.io.FileNotFoundException;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

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
	private final JPanel northPane = new JPanel(new GridLayout(3, 1));
	private final JButton startButton = new JButton("Fetch data");
	private final JComboBox charsetComboBox = new JComboBox(new String[] {
			"windows-1250", "ISO-8859-2", "UTF-8" });
	private final JComboBox formatComboBox = new JComboBox(new String[] {
			"MicroDVD", "SubRip", "MPL2", "TMPlayer" });
	private final JLabel formatLabel = new JLabel("Format:");
	private final JLabel charsetLabel = new JLabel("Charset:");
	private final JComboBox langComboBox = new JComboBox(new String[] {
			"Polish", "English" });
	private final JCheckBox napiprojektCheckBox = new JCheckBox("Napiprojekt");
	private final JCheckBox opensubtitlesCheckBox = new JCheckBox(
			"OpenSubtitles.org");
	private final JScrollPane southPane = new JScrollPane();
	private final ResultsTable resultsTable = new ResultsTable();

	private final MediaInfo mediaInfo = new MediaInfo();

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
		setTitle("JNapi v0.1");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(mainPane, BorderLayout.CENTER);

		try {
			Image icon = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("icon.png"));
			setIconImage(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				int result = chooser.showOpenDialog(thisReference);

				if (result != JFileChooser.APPROVE_OPTION)
					return;

				Global.getInstance().setLastUsedDirectory(
						chooser.getSelectedFile().getParentFile());

				try {
					mediaInfo.open(chooser.getSelectedFile());
					filePathTextField.setText(chooser.getSelectedFile()
							.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}

				setOptionsEnabled(true);
				resultsTable.clear();
			}
		});

		fileSelectionPane.add(new Label("File path:"));
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
		napiprojektCheckBox.setEnabled(false);
		opensubtitlesCheckBox.setEnabled(false);

		JPanel fetchPane = new JPanel(new GridLayout(1, 2));
		northPane.add(fetchPane);

		startButton.setEnabled(false);
		langComboBox.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					resultsTable.clear();

					Lang lang = Global.getInstance().getLang();

					File selectedFile = new File(filePathTextField.getText());

					ISubtitlesProvider subtitlesProvider;
					if (napiprojektCheckBox.isSelected()) {
						subtitlesProvider = new Napiprojekt();
						for (SubtitlesResult result : subtitlesProvider
								.downloadSubtitles(selectedFile, lang))
							resultsTable.addResult(result);
					}
					if (opensubtitlesCheckBox.isSelected()) {
						subtitlesProvider = new OpenSubtitles();
						for (SubtitlesResult result : subtitlesProvider
								.downloadSubtitles(selectedFile, lang))
							resultsTable.addResult(result);
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(thisReference,
							"File with given path doesn't exists!",
							"File not found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel startButtonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel langPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		fetchPane.add(langPane);
		fetchPane.add(startButtonPane);
		final JLabel langLabel = new JLabel("Language:");
		langLabel.setEnabled(false);
		langPane.add(langLabel);
		langPane.add(langComboBox);
		langComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Global.getInstance().setLang(
						Lang.getValueOf(langComboBox.getSelectedItem()
								.toString()));
			}
		});
		startButtonPane.add(startButton);

		filePathTextField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				boolean enabled = filePathTextField.getText().length() > 0;
				startButton.setEnabled(enabled);
				langComboBox.setEnabled(enabled);
				napiprojektCheckBox.setEnabled(enabled);
				opensubtitlesCheckBox.setEnabled(enabled);
				langLabel.setEnabled(enabled);

				Global.getInstance().setSelectedMovieFilePath(
						filePathTextField.getText());
			}
		});
	}

	private void initializeOptionsPane() {
		JPanel optionsPane = new JPanel(new GridLayout(1, 2, 20, 0));
		mainPane.add(optionsPane, BorderLayout.CENTER);
		setOptionsEnabled(false);

		JPanel charsetPane = new JPanel(new GridLayout(2, 1));
		optionsPane.add(charsetPane);
		charsetPane.add(charsetLabel);
		charsetPane.add(charsetComboBox);
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
		formatComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Global.getInstance().setFormat(
						Format.valueOf(formatComboBox.getSelectedItem()
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
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
		});
	}

}
