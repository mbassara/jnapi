package pl.mbassara.jnapi.gui;

import javax.swing.JFrame;
import javax.swing.JTable;

import pl.mbassara.jnapi.services.SubtitlesResult;

public class ResultsTable extends JTable {

	private static final long serialVersionUID = 7478035175403314625L;
	private ResultsTableModel model = new ResultsTableModel();

	public ResultsTable(JFrame parentFrame) {
		setModel(model);
		getColumn("Release name").setPreferredWidth(350);
		getColumn("Provider").setPreferredWidth(150);

		addMouseListener(new ResultsTableMouseAdapter(parentFrame, this));
	}

	public void addResult(SubtitlesResult result) {
		model.addResult(result);
	}

	public void removeResult(SubtitlesResult result) {
		model.removeResult(result);
	}

	public void clear() {
		model.clear();
	}

}
