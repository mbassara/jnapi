package pl.mbassara.jnapi.services.napiprojekt;

import java.util.Date;

public class NapiResult {

	private boolean status = false;
	private String hash;
	private String subsHash;
	private int filesize;
	private String author;
	private String uploader;
	private Date uploadDate;
	private String subsAsciiBin;

	private String movieHash;
	private boolean coverStatus = false;
	private String title;
	private String year;
	private String plCountry;
	private String enCountry;
	private String plGenre;
	private String enGenre;
	private String director;
	private String screenPlay;
	private String cinematography;
	private String music;
	private boolean tvSeries;

	private String filmweb;
	private int rating;
	private int votes;

	private String coverAsciiBin;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSubsHash() {
		return subsHash;
	}

	public void setSubsHash(String subsHash) {
		this.subsHash = subsHash;
	}

	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getSubsAsciiBin() {
		return subsAsciiBin;
	}

	public void setSubsAsciiBin(String subsAsciiBin) {
		this.subsAsciiBin = subsAsciiBin;
	}

	public String getMovieHash() {
		return movieHash;
	}

	public void setMovieHash(String movieHash) {
		this.movieHash = movieHash;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPlGenre() {
		return plGenre;
	}

	public void setPlGenre(String plGenre) {
		this.plGenre = plGenre;
	}

	public String getEnGenre() {
		return enGenre;
	}

	public void setEnGenre(String enGenre) {
		this.enGenre = enGenre;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getScreenPlay() {
		return screenPlay;
	}

	public void setScreenPlay(String screenPlay) {
		this.screenPlay = screenPlay;
	}

	public String getCinematography() {
		return cinematography;
	}

	public void setCinematography(String cinematography) {
		this.cinematography = cinematography;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public boolean isTvSeries() {
		return tvSeries;
	}

	public void setTvSeries(boolean tvSeries) {
		this.tvSeries = tvSeries;
	}

	public String getFilmweb() {
		return filmweb;
	}

	public void setFilmweb(String filmweb) {
		this.filmweb = filmweb;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getCoverAsciiBin() {
		return coverAsciiBin;
	}

	public void setCoverAsciiBin(String coverAsciiBin) {
		this.coverAsciiBin = coverAsciiBin;
	}

	public String getEnCountry() {
		return enCountry;
	}

	public void setEnCountry(String enCountry) {
		this.enCountry = enCountry;
	}

	public String getPlCountry() {
		return plCountry;
	}

	public void setPlCountry(String plCountry) {
		this.plCountry = plCountry;
	}

	public boolean isCoverStatus() {
		return coverStatus;
	}

	public void setCoverStatus(boolean coverStatus) {
		this.coverStatus = coverStatus;
	}

}
