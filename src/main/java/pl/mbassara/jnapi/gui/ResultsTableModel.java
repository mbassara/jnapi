package pl.mbassara.jnapi.gui;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringUtils;
import pl.mbassara.jnapi.services.SubtitlesResult;

public class ResultsTableModel implements TableModel {

	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	private ArrayList<SubtitlesResult> elements = new ArrayList<SubtitlesResult>();

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return (columnIndex == 0 ? SubtitlesResult.class : String.class);
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return (columnIndex == 0 ? "Release name" : "Provider");
	}

	@Override
	public int getRowCount() {
		return elements.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SubtitlesResult element = elements.get(rowIndex);
		String value = (columnIndex == 0 ? element.getMovieReleaseName()
				: element.getProviderName());

		return StringUtils.defaultIfEmpty(value, "").trim();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return;
	}

	public void addResult(SubtitlesResult result) {
		elements.add(result);
		for (TableModelListener listener : listeners)
			listener.tableChanged(new TableModelEvent(this));
	}

	public void removeResult(SubtitlesResult result) {
		elements.remove(result);
		for (TableModelListener listener : listeners)
			listener.tableChanged(new TableModelEvent(this));
	}

	public void clear() {
		elements.clear();
		for (TableModelListener listener : listeners)
			listener.tableChanged(new TableModelEvent(this));
	}

	public SubtitlesResult getResultAt(int index) {
		return elements.get(index);
	}
}
