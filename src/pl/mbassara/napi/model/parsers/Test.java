package pl.mbassara.napi.model.parsers;


public class Test {
	public static void main(String[] args) {
		// String line = "{743}{814}- Jaki gatunek?|- Nie wiem.";
		// System.out.println(line.matches("\\{\\d+\\}\\{\\d+\\}.*"));
		//
		// int timeFrom = Integer.parseInt(line.substring(1,
		// line.indexOf("}")));
		// line = line.substring(line.indexOf("}") + 1);
		// int timeTo = Integer.parseInt(line.substring(1, line.indexOf("}")));
		// line = line.substring(line.indexOf("}") + 1);
		//
		// System.out.println(timeFrom);
		// System.out.println(timeTo);
		// System.out.println(line);

		// String path =
		// "F:\\Maciek\\Videos\\The Girl With The Dragon Tattoo {2011} DVDRIP. Jaybob\\The Girl With The Dragon Tattoo {2011} DVDRIP. Jaybob.avi";
		// String path2 =
		// "F:\\Maciek\\Videos\\The Girl With The Dragon Tattoo {2011} DVDRIP. Jaybob\\1.txt";
		//
		// try {
		// Subtitles subtitles;
		// subtitles = new MicroDVDParser().parse(new File(path), "UTF-8",
		// 23.976);
		// subtitles.save(TYPE.MicroDVD, new File(path2), "ISO-8859-2");
		// } catch (WrongSubtitlesFormatException e) {
		// System.err.println("Wrong line: " + e.getWrongLine());
		// e.printStackTrace();
		// }

		// for (String name : Charset.availableCharsets().keySet())
		// System.out.println(name);

		// MediaInfo mediaInfo = new MediaInfo();
		// mediaInfo.open(new File(path));
		// System.out.println(mediaInfo.get(StreamKind.Video, 0, "FrameRate"));

		System.out.println(Double.parseDouble(""));
	}
}
