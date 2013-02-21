package pl.mbassara.jnapi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import pl.mbassara.jnapi.services.FileHelper;
import pl.mbassara.jnapi.services.napiprojekt.NapiResult;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt.Lang;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt.Mode;

public class NapiprojektMovieInfoPanel extends JPanel {

	private static final long serialVersionUID = 5792176431005929181L;
	private final int HEIGHT = 250;
	private final int WIDTH = 500;

	private ImagePanel coverPanel;
	private JPanel infoPanel = new JPanel(new BorderLayout());
	private JPanel leftInfoPanel = new JPanel(new GridLayout(9, 1));
	private JPanel rightInfoPanel = new JPanel(new GridLayout(9, 1));
	private JPanel southPanel = new JPanel(new BorderLayout());
	private JPanel votesPanel = new JPanel(new GridLayout(1, 4));

	private JLabel titleLabel = new JLabel();
	private JLabel yearLabel = new JLabel();
	private JLabel countryLabel = new JLabel();
	private JLabel genreLabel = new JLabel();
	private JLabel directionLabel = new JLabel();
	private JLabel screenplayLabel = new JLabel();
	private JLabel musicLabel = new JLabel();
	private JLabel ratingLabel = new JLabel();
	private JLabel votesLabel = new JLabel();
	private LinkLabel filmWebLinkLabel = new LinkLabel();

	private NapiprojektMovieInfoPanel(byte[] imageData, String title,
			String year, String country, String genre, String direction,
			String screenplay, String music, String rating, String votes,
			String filmWebUrl) {

		setLayout(new BorderLayout());

		coverPanel = new ImagePanel();

		setSize(WIDTH, HEIGHT);
		leftInfoPanel.setPreferredSize(new Dimension(120, HEIGHT));
		rightInfoPanel.setPreferredSize(new Dimension(200, HEIGHT));

		leftInfoPanel.add(new Label());
		rightInfoPanel.add(new Label());
		leftInfoPanel.add(new JLabel("Title:  ", JLabel.RIGHT));
		rightInfoPanel.add(titleLabel);
		leftInfoPanel.add(new JLabel("Year:  ", JLabel.RIGHT));
		rightInfoPanel.add(yearLabel);
		leftInfoPanel.add(new JLabel("Country:  ", JLabel.RIGHT));
		rightInfoPanel.add(countryLabel);
		leftInfoPanel.add(new JLabel("Genre:  ", JLabel.RIGHT));
		rightInfoPanel.add(genreLabel);
		leftInfoPanel.add(new JLabel("Direction:  ", JLabel.RIGHT));
		rightInfoPanel.add(directionLabel);
		leftInfoPanel.add(new JLabel("Screenplay:  ", JLabel.RIGHT));
		rightInfoPanel.add(screenplayLabel);
		leftInfoPanel.add(new JLabel("Music:  ", JLabel.RIGHT));
		rightInfoPanel.add(musicLabel);
		leftInfoPanel.add(new Label());
		rightInfoPanel.add(new Label());

		add(coverPanel, BorderLayout.WEST);
		add(infoPanel, BorderLayout.EAST);
		infoPanel.add(leftInfoPanel, BorderLayout.WEST);
		infoPanel.add(rightInfoPanel, BorderLayout.EAST);

		infoPanel.add(southPanel, BorderLayout.SOUTH);

		votesPanel.add(new JLabel("Rating:  ", JLabel.RIGHT));
		votesPanel.add(ratingLabel);
		votesPanel.add(new JLabel("Votes:  ", JLabel.RIGHT));
		votesPanel.add(votesLabel);

		southPanel.add(votesPanel, BorderLayout.NORTH);
		southPanel.add(filmWebLinkLabel, BorderLayout.SOUTH);

		coverPanel.setImage(imageData);

		titleLabel.setText("<html>" + title + "</html>");
		yearLabel.setText("<html>" + year + "</html>");
		countryLabel.setText("<html>" + country + "</html>");
		genreLabel.setText("<html>" + genre + "</html>");
		directionLabel.setText("<html>" + direction + "</html>");
		screenplayLabel.setText("<html>" + screenplay + "</html>");
		musicLabel.setText("<html>" + music + "</html>");
		ratingLabel.setText(rating);
		votesLabel.setText(votes);

		try {
			filmWebLinkLabel.setTarget(new URI(filmWebUrl));
			filmWebLinkLabel
					.setText("See the site of this movie on filmweb.pl");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static NapiprojektMovieInfoPanel getInstance(NapiResult result) {
		if (result == null || !result.isCoverStatus()) {
			try {
				InputStream stream = NapiprojektMovieInfoPanel.class
						.getClassLoader().getResourceAsStream("no-image.png");

				byte[] buff = new byte[20000];

				int len = stream.read(buff);

				byte[] imageData = Arrays.copyOf(buff, len);

				return new NapiprojektMovieInfoPanel(imageData, "", "", "", "",
						"", "", "", "", "", "");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		return new NapiprojektMovieInfoPanel(
				FileHelper.base64ToByteArray(result.getCoverAsciiBin()),
				result.getTitle(), result.getYear(), result.getEnCountry(),
				result.getEnGenre(), result.getDirector(),
				result.getScreenPlay(), result.getMusic(), result.getRating()
						+ "", result.getVotes() + "", result.getFilmweb());
	}

	public void setContent(NapiResult result) {
		if (result == null || !result.isCoverStatus())
			return;

		setContent(FileHelper.base64ToByteArray(result.getCoverAsciiBin()),
				result.getTitle(), result.getYear(), result.getEnCountry(),
				result.getEnGenre(), result.getDirector(),
				result.getScreenPlay(), result.getMusic(), result.getRating()
						+ "", result.getVotes() + "", result.getFilmweb());
	}

	public void setContent(byte[] imageData, String title, String year,
			String country, String genre, String direction, String screenplay,
			String music, String rating, String votes, String filmWebUrl) {

		coverPanel.setImage(imageData);

		titleLabel.setText("<html>" + title + "</html>");
		yearLabel.setText("<html>" + year + "</html>");
		countryLabel.setText("<html>" + country + "</html>");
		genreLabel.setText("<html>" + genre + "</html>");
		directionLabel.setText("<html>" + direction + "</html>");
		screenplayLabel.setText("<html>" + screenplay + "</html>");
		musicLabel.setText("<html>" + music + "</html>");
		ratingLabel.setText(rating);
		votesLabel.setText(votes);

		try {
			filmWebLinkLabel.setTarget(new URI(filmWebUrl));
			filmWebLinkLabel
					.setText("See the site of this movie on filmweb.pl");
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
								"/media/maciek/Media/Maciek/Videos/Total Recall {2012} DVDRIP. Jaybob/Total Recall {2012} DVDRIP. Jaybob.avi"),
						Mode.SUBS_COVER, Lang.PL);
		JFrame frame = new JFrame();
		NapiprojektMovieInfoPanel infoPanel;
		frame.add(infoPanel = NapiprojektMovieInfoPanel.getInstance(null));
		frame.setVisible(true);
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		infoPanel.setContent(result);
	}
}
