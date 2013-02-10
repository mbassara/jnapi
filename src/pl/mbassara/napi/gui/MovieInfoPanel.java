package pl.mbassara.napi.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import pl.mbassara.napi.utils.NapiFileHelper;
import pl.mbassara.napi.utils.NapiResult;
import pl.mbassara.napi.utils.Napiprojekt;
import pl.mbassara.napi.utils.Napiprojekt.Mode;

public class MovieInfoPanel extends JPanel {

	private static final long serialVersionUID = 5792176431005929181L;
	private NapiResult napiResult;
	private ImagePanel coverPanel;
	private JPanel votesPanel = new JPanel(new FlowLayout());

	public MovieInfoPanel(NapiResult result) {
		napiResult = result;
		setLayout(new BorderLayout());

		if (!napiResult.isCoverStatus())
			return;

		coverPanel = new ImagePanel(NapiFileHelper.base64ToByteArray(napiResult
				.getCoverAsciiBin()));
		add(coverPanel, BorderLayout.NORTH);
		add(votesPanel, BorderLayout.SOUTH);
	}

	public static void main(String[] args) throws FileNotFoundException {
		NapiResult result = Napiprojekt
				.request(
						new File(
								"F:\\Maciek\\Videos\\Star Wars Episode I  1999 720p BRRip [A Release-Lounge H264]\\Star Wars Episode I  1999 720p BRRip [A Release-Lounge H264].avi"),
						Mode.SUBS_COVER);
		JFrame frame = new JFrame();
		frame.add(new MovieInfoPanel(result));
		frame.setVisible(true);
		frame.setSize(150, 200);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
