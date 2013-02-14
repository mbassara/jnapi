package pl.mbassara.jnapi.gui;

import java.awt.Dimension;
import java.awt.Label;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import pl.mbassara.jnapi.services.napiprojekt.NapiResult;
import pl.mbassara.jnapi.services.opensubtitles.ResponseStruct;

public class ResultsPanel extends JPanel {

	private static final long serialVersionUID = 7478035175403314625L;
	private ArrayList<String> resultsList;
	
	public ResultsPanel(){
		setPreferredSize(new Dimension(getPreferredSize().width, 100));
		resultsList = new ArrayList<String>();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	public void addResult(NapiResult result){
		resultsList.add(result.getTitle());
		add(new Label(result.getTitle()));
		doLayout();
	}
	
	public void addResult(ResponseStruct result){
		String tmp = result.getFieldsForName("").get(0).getValue();
		resultsList.add(tmp);
		add(new Label(tmp));
		doLayout();
	}

}
