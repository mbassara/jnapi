package pl.mbassara.napi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import pl.mbassara.napi.utils.NapiFileHelper;
import pl.mbassara.napi.utils.NapiResult;
import pl.mbassara.napi.utils.Napiprojekt;
import pl.mbassara.napi.utils.Napiprojekt.Mode;

public class MovieInfoPanel extends JPanel {

	private static final long serialVersionUID = 5792176431005929181L;

	public MovieInfoPanel(NapiResult result) {
		NapiResult napiResult = result;
		setLayout(new BorderLayout());

		if (!napiResult.isCoverStatus())
			return;

		ImagePanel coverPanel = new ImagePanel(
				NapiFileHelper.base64ToByteArray(napiResult.getCoverAsciiBin()));

		JPanel leftInfoPanel = new JPanel(new GridLayout(8, 1));
		leftInfoPanel.setPreferredSize(new Dimension(120, 600));
		JPanel rightInfoPanel = new JPanel(new GridLayout(8, 1));
		rightInfoPanel.setPreferredSize(new Dimension(200, 600));

		leftInfoPanel.add(new JLabel("Title:  ", JLabel.RIGHT));
		rightInfoPanel
				.add(new JLabel("<html>" + result.getTitle() + "</html>"));
		leftInfoPanel.add(new JLabel("Year:  ", JLabel.RIGHT));
		rightInfoPanel.add(new JLabel("<html>" + result.getYear() + "</html>"));
		leftInfoPanel.add(new JLabel("Country:  ", JLabel.RIGHT));
		rightInfoPanel.add(new JLabel("<html>" + result.getEnCountry()
				+ "</html>"));
		leftInfoPanel.add(new JLabel("Genre:  ", JLabel.RIGHT));
		rightInfoPanel.add(new JLabel("<html>" + result.getEnGenre()
				+ "</html>"));
		leftInfoPanel.add(new JLabel("Direction:  ", JLabel.RIGHT));
		rightInfoPanel.add(new JLabel("<html>" + result.getDirector()
				+ "</html>"));
		leftInfoPanel.add(new JLabel("Screenplay:  ", JLabel.RIGHT));
		rightInfoPanel.add(new JLabel("<html>" + result.getScreenPlay()
				+ "</html>"));
		leftInfoPanel.add(new JLabel("Cinematography:  ", JLabel.RIGHT));
		rightInfoPanel.add(new JLabel("<html>" + result.getCinematography()
				+ "</html>"));
		leftInfoPanel.add(new JLabel("Music:  ", JLabel.RIGHT));
		rightInfoPanel
				.add(new JLabel("<html>" + result.getMusic() + "</html>"));

		add(coverPanel, BorderLayout.NORTH);
		add(leftInfoPanel, BorderLayout.WEST);
		add(rightInfoPanel, BorderLayout.EAST);

		JPanel southPanel = new JPanel(new BorderLayout(20, 10));
		add(southPanel, BorderLayout.SOUTH);

		JPanel votesPanel = new JPanel(new GridLayout(1, 4));
		votesPanel.add(new JLabel("Rating:  ", JLabel.RIGHT));
		votesPanel.add(new JLabel(result.getRating() / 100.0 + ""));
		votesPanel.add(new JLabel("Votes:  ", JLabel.RIGHT));
		votesPanel.add(new JLabel(result.getVotes() + ""));

		southPanel.add(votesPanel, BorderLayout.NORTH);
		try {
			southPanel.add(new LinkLabel(new URI(result.getFilmweb()),
					"See the site of this movie on filmweb.pl"),
					BorderLayout.SOUTH);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			URISyntaxException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		NapiResult result = Napiprojekt
				.request(
						new File(
								"F:\\Maciek\\Videos\\Star Wars Episode I  1999 720p BRRip [A Release-Lounge H264]\\Star Wars Episode I  1999 720p BRRip [A Release-Lounge H264].avi"),
						Mode.SUBS_COVER);
		JFrame frame = new JFrame();
		frame.add(new MovieInfoPanel(result));
		frame.setVisible(true);
		frame.setSize(350, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
