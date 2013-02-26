package pl.mbassara.jnapi.services.opensubtitles;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import pl.mbassara.jnapi.gui.LinkLabel;
import pl.mbassara.jnapi.services.Lang;

public class OpensubtitlesMovieInfoPanel extends JPanel {

	private static final long serialVersionUID = -5459894947192054817L;

	private final int WIDTH = 350;

	private JPanel infoPanel = new JPanel(new GridLayout(2, 1));

	private JPanel movieInfoPanel = new JPanel(new BorderLayout());
	private JPanel leftMovieInfoPanel = new JPanel(new GridLayout(8, 1));
	private JPanel rightMovieInfoPanel = new JPanel(new GridLayout(8, 1));

	private JPanel subsInfoPanel = new JPanel(new BorderLayout());
	private JPanel leftSubsInfoPanel = new JPanel(new GridLayout(8, 1));
	private JPanel rightSubsInfoPanel = new JPanel(new GridLayout(8, 1));

	private JPanel directDownloadLinkPanel = new JPanel();

	private JLabel movieNameLabel = new JLabel();
	private JLabel movieReleaseNameLabel = new JLabel();
	private JLabel movieYearLabel = new JLabel();
	private JLabel movieImdbRatingLabel = new JLabel();
	private JLabel seriesSeasonLabel = new JLabel();
	private JLabel seriesEpisodeLabel = new JLabel();

	private JLabel subFormatLabel = new JLabel();
	private JLabel languageNameLabel = new JLabel();
	private JLabel cdLabel = new JLabel();
	private JLabel movieSizeLabel = new JLabel();
	private JLabel subAddDateLabel = new JLabel();
	private JLabel subDownloadsCntLabel = new JLabel();
	private JTextField subAuthorCommentTextField = new JTextField();

	private LinkLabel imbdLinkLabel = new LinkLabel();
	private LinkLabel directDownloadLinkLabel = new LinkLabel();

	private OpensubtitlesMovieInfoPanel() {

		setLayout(new BorderLayout());

		add(infoPanel, BorderLayout.CENTER);
		add(directDownloadLinkPanel, BorderLayout.SOUTH);

		infoPanel.add(movieInfoPanel);
		infoPanel.add(subsInfoPanel);

		leftMovieInfoPanel.setPreferredSize(new Dimension(WIDTH / 3, 160));
		rightMovieInfoPanel.setPreferredSize(new Dimension(WIDTH * 2 / 3, 160));

		leftSubsInfoPanel.setPreferredSize(new Dimension(WIDTH / 3, 160));
		rightSubsInfoPanel.setPreferredSize(new Dimension(WIDTH * 2 / 3, 160));

		movieInfoPanel.add(leftMovieInfoPanel, BorderLayout.WEST);
		movieInfoPanel.add(rightMovieInfoPanel, BorderLayout.CENTER);

		leftMovieInfoPanel.add(new JLabel());
		rightMovieInfoPanel.add(new JLabel());
		leftMovieInfoPanel.add(new JLabel("Title:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(movieNameLabel);
		leftMovieInfoPanel.add(new JLabel("Release:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(movieReleaseNameLabel);
		leftMovieInfoPanel.add(new JLabel("Year:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(movieYearLabel);
		leftMovieInfoPanel.add(new JLabel("Rating:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(movieImdbRatingLabel);
		leftMovieInfoPanel.add(new JLabel("Season:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(seriesSeasonLabel);
		leftMovieInfoPanel.add(new JLabel("Episode:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(seriesEpisodeLabel);
		leftMovieInfoPanel.add(new JLabel("Imdb:  ", JLabel.RIGHT));
		rightMovieInfoPanel.add(imbdLinkLabel);

		subsInfoPanel.add(leftSubsInfoPanel, BorderLayout.WEST);
		subsInfoPanel.add(rightSubsInfoPanel, BorderLayout.CENTER);

		leftSubsInfoPanel.add(new JLabel());
		rightSubsInfoPanel.add(new JLabel());
		leftSubsInfoPanel.add(new JLabel("Format:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(subFormatLabel);
		leftSubsInfoPanel.add(new JLabel("Language:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(languageNameLabel);
		leftSubsInfoPanel.add(new JLabel("CD:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(cdLabel);
		leftSubsInfoPanel.add(new JLabel("Movie size:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(movieSizeLabel);
		leftSubsInfoPanel.add(new JLabel("Created:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(subAddDateLabel);
		leftSubsInfoPanel.add(new JLabel("Comment:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(subDownloadsCntLabel);
		leftSubsInfoPanel.add(new JLabel("Downloads:  ", JLabel.RIGHT));
		rightSubsInfoPanel.add(subAuthorCommentTextField);

		subAuthorCommentTextField.setEditable(false);
		subAuthorCommentTextField.setBorder(BorderFactory.createEmptyBorder());

		directDownloadLinkPanel.add(directDownloadLinkLabel);
	}

	public static OpensubtitlesMovieInfoPanel getInstance() {
		return getInstance(null);
	}

	public static OpensubtitlesMovieInfoPanel getInstance(
			ResponseStruct response) {
		OpensubtitlesMovieInfoPanel instance = new OpensubtitlesMovieInfoPanel();
		instance.setContent(response);

		return instance;
	}

	public void setContent(ResponseStruct response) {
		if (response == null) {
			setContent("", "", "", "", "", "", "", "", "", "", "", "", "", "",
					"", "");
		} else
			setContent(
					response.getFieldsForName("MovieName").get(0).getValue(),
					response.getFieldsForName("MovieReleaseName").get(0)
							.getValue(),
					response.getFieldsForName("MovieYear").get(0).getValue(),
					response.getFieldsForName("MovieImdbRating").get(0)
							.getValue(),
					response.getFieldsForName("SeriesSeason").get(0).getValue(),
					response.getFieldsForName("SeriesEpisode").get(0)
							.getValue(),
					response.getFieldsForName("IDMovieImdb").get(0).getValue(),
					response.getFieldsForName("SubFormat").get(0).getValue(),
					response.getFieldsForName("LanguageName").get(0).getValue(),
					response.getFieldsForName("SubActualCD").get(0).getValue(),
					response.getFieldsForName("SubSumCD").get(0).getValue(),
					response.getFieldsForName("MovieByteSize").get(0)
							.getValue(), response
							.getFieldsForName("SubAddDate").get(0).getValue(),
					response.getFieldsForName("SubDownloadsCnt").get(0)
							.getValue(),
					response.getFieldsForName("SubAuthorComment").get(0)
							.getValue(),
					response.getFieldsForName("ZipDownloadLink").get(0)
							.getValue());
	}

	public void setContent(String movieName, String movieReleaseName,
			String movieYear, String movieImdbRating, String seriesSeason,
			String seriesEpisode, String iDMovieImdb, String subFormat,
			String languageName, String subActualCD, String subSumCD,
			String movieByteSize, String subAddDate, String subDownloadsCnt,
			String subAuthorComment, String zipDownloadLink) {

		movieNameLabel.setText("<html>" + movieName + "</html>");
		movieReleaseNameLabel.setText("<html>" + movieReleaseName + "</html>");
		movieYearLabel.setText("<html>" + movieYear + "</html>");
		movieImdbRatingLabel.setText("<html>" + movieImdbRating + "</html>");
		seriesSeasonLabel.setText("<html>" + seriesSeason + "</html>");
		seriesEpisodeLabel.setText("<html>" + seriesEpisode + "</html>");

		subFormatLabel.setText("<html>" + subFormat + "</html>");
		languageNameLabel.setText("<html>" + languageName + "</html>");
		cdLabel.setText("<html>" + subActualCD + "/" + subSumCD + "</html>");
		if (movieByteSize != null && movieByteSize.length() > 0)
			movieSizeLabel.setText("<html>"
					+ (Integer.parseInt(movieByteSize) / 1024 / 1024) + " MB"
					+ "</html>");
		subAddDateLabel.setText("<html>" + subAddDate + "</html>");
		subDownloadsCntLabel.setText("<html>" + subDownloadsCnt + "</html>");
		subAuthorCommentTextField.setText(subAuthorComment);

		try {
			if (iDMovieImdb != null && iDMovieImdb.length() > 0) {
				imbdLinkLabel.setHorizontalAlignment(LinkLabel.LEFT);
				imbdLinkLabel.setTarget(new URI("http://www.imdb.com/title/tt"
						+ iDMovieImdb + "/"));
				imbdLinkLabel.setText("<html>" + "http://www.imdb.com/title/tt"
						+ iDMovieImdb + "</html>");
			}

			if (zipDownloadLink != null && zipDownloadLink.length() > 0) {
				directDownloadLinkLabel.setTarget(new URI(zipDownloadLink));
				directDownloadLinkLabel.setText("<html>"
						+ "Direct download from opensubtitles.org (zip file)"
						+ "</html>");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		for (Component component : getComponents()) {
			component.setEnabled(enabled);
			System.out.println(component);
		}

		super.setEnabled(enabled);
	}

	public static void main(String[] args) throws FileNotFoundException,
			URISyntaxException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// NapiResult result = Napiprojekt
		// .request(
		// new File(
		// "/media/maciek/Media/Maciek/Videos/Total Recall {2012} DVDRIP. Jaybob/Total Recall {2012} DVDRIP. Jaybob.avi"),
		// Mode.SUBS_COVER, Lang.PL);

		String token = OpenSubtitles.logIn();
		ResponseStruct response = OpenSubtitles
				.searchSubtitles(
						token,
						new File(
								"F:\\Maciek\\Videos\\Black Swan {2010} DVDRIP. Jaybob\\Black Swan {2010} DVDRIP. Jaybob .avi"),
						Lang.PL).getSubResponseStructs().get(0);
		OpenSubtitles.logOut(token);
		// System.out.println(response);

		JFrame frame = new JFrame();
		OpensubtitlesMovieInfoPanel infoPanel;
		frame.add(infoPanel = OpensubtitlesMovieInfoPanel.getInstance(null));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		infoPanel.setContent(response);
		frame.pack();
	}
}
