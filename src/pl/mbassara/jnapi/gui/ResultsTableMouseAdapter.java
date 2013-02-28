package pl.mbassara.jnapi.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import pl.mbassara.jnapi.Global;
import pl.mbassara.jnapi.model.parsers.UnsupportedSubtitlesFormatException;
import pl.mbassara.jnapi.services.SubtitlesResult;
import pl.mbassara.jnapi.services.napiprojekt.NapiResult;
import pl.mbassara.jnapi.services.napiprojekt.NapiprojektMovieInfoPanel;
import pl.mbassara.jnapi.services.opensubtitles.OpensubtitlesMovieInfoPanel;
import pl.mbassara.jnapi.services.opensubtitles.ResponseStruct;

public class ResultsTableMouseAdapter extends MouseAdapter {

	private final ResultsTable table;
	private final int[] selectedRowIndex = new int[1];
	private final JFrame parentFrame;

	public ResultsTableMouseAdapter(JFrame parentFrame, ResultsTable table) {
		this.table = table;
		this.parentFrame = parentFrame;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		selectedRowIndex[0] = table.rowAtPoint(e.getPoint());
		if (selectedRowIndex[0] >= 0
				&& selectedRowIndex[0] < table.getRowCount()) {
			table.setRowSelectionInterval(selectedRowIndex[0],
					selectedRowIndex[0]);
		} else {
			table.clearSelection();

		}

		if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
			JPopupMenu menu = new JPopupMenu("menu");

			JMenuItem item = new JMenuItem("Save");
			item.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			item.addActionListener(saveActionListener);
			menu.add(item);

			item = new JMenuItem("Save As");
			item.addActionListener(saveAsActionListener);
			menu.add(item);

			item = new JMenuItem("Properties");
			item.addActionListener(propertiesActionListener);
			menu.add(item);

			menu.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getClickCount() == 2) {
			saveSubtitles();
		}
	}

	private void saveSubtitles() {
		String proposedPath = Global.getInstance().getSelectedMovieFilePath();
		proposedPath = proposedPath.substring(0, proposedPath.lastIndexOf("."))
				+ ".txt";

		saveSubtitlesAs(proposedPath);
	}

	private void saveSubtitlesAs(String destinationFilePath) {
		File destinationFile = new File(destinationFilePath);
		if (destinationFile.exists()) {
			int option = JOptionPane.showConfirmDialog(table,
					"Are you sure you want to override file:\n"
							+ destinationFilePath, "File exists",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (option == JOptionPane.NO_OPTION)
				return;
		}

		SubtitlesResult subtitlesResult = ((ResultsTableModel) table.getModel())
				.getResultAt(selectedRowIndex[0]);

		try {
			boolean isSaved = subtitlesResult.saveSubtitles(destinationFile,
					Global.getInstance().getFormat(), Global.getInstance()
							.getSubtitlesCharset().toString());

			if (isSaved)
				JOptionPane.showMessageDialog(table, "Subtitles saved as: "
						+ destinationFile.getName(), "Subtitles saved",
						JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(table,
						"Subtitles cannot be saved.", "Can't save",
						JOptionPane.WARNING_MESSAGE);
		} catch (UnsupportedSubtitlesFormatException e1) {
			Global.getInstance().getLogger().warning(e1.toString());
			e1.printStackTrace();
			JOptionPane
					.showMessageDialog(
							table,
							"Downloaded subtitles are in unsupported format and therefore they cannot be saved.",
							"Unsupported format", JOptionPane.ERROR_MESSAGE);
		}

	}

	private ActionListener saveActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveSubtitles();
		}
	};

	private ActionListener saveAsActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String proposedPath = Global.getInstance()
					.getSelectedMovieFilePath();
			proposedPath = proposedPath.substring(0,
					proposedPath.lastIndexOf("."))
					+ ".txt";

			JFileChooser chooser = new JFileChooser(Global.getInstance()
					.getLastUsedDirectory());
			chooser.setSelectedFile(new File(proposedPath));
			chooser.setDialogTitle("Save subtitles");
			chooser.setMultiSelectionEnabled(false);
			int option = chooser.showSaveDialog(table);

			if (option != JFileChooser.APPROVE_OPTION)
				return;

			Global.getInstance().setLastUsedDirectory(
					chooser.getSelectedFile().getParentFile());

			saveSubtitlesAs(chooser.getSelectedFile().getAbsolutePath());

		}
	};

	private ActionListener propertiesActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			final Object rawResult = ((ResultsTableModel) table.getModel())
					.getResultAt(selectedRowIndex[0]).getRawResult();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JDialog dialog = null;
					if (rawResult instanceof NapiResult) {
						dialog = new JDialog(parentFrame,
								"Napiprojekt movie info", true);
						dialog.setContentPane(NapiprojektMovieInfoPanel
								.getInstance((NapiResult) rawResult));
					}
					if (rawResult instanceof ResponseStruct) {
						dialog = new JDialog(parentFrame,
								"Opensubtitles.org movie info", true);
						dialog.setContentPane(OpensubtitlesMovieInfoPanel
								.getInstance((ResponseStruct) rawResult));
					}
					if (dialog != null) {
						dialog.pack();
						dialog.setResizable(false);
						dialog.setLocationByPlatform(true);
						dialog.setIconImage(Global.getInstance().getIcon());
						dialog.setVisible(true);
					}
				}
			});
		}
	};
}
