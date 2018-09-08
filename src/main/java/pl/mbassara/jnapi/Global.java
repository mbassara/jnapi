package pl.mbassara.jnapi;

import pl.mbassara.jnapi.gui.SubtitlesCharset;
import pl.mbassara.jnapi.logs.FileLogHandler;
import pl.mbassara.jnapi.services.Lang;
import pl.mbassara.jnapi.subtitles.Subtitles.Format;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Global {

    private static Global instance = null;

    private static final Logger logger = Logger.getLogger("Jnapi_main_logger");
    private static Handler handler = null;

    private File configFile;

    private Global() {

        try {
            icon = ImageIO.read(getClass().getClassLoader()
                    .getResourceAsStream("icon.png"));

            if (System.getProperty("os.name").toLowerCase().contains("win"))
                configFile = new File(System.getenv("APPDATA") + File.separator
                        + "JNapi" + File.separator + "config.dat");
            else
                configFile = new File(System.getProperty("user.home")
                        + File.separator + ".JNapi" + File.separator
                        + "config.dat");

            configFile.getParentFile().mkdirs();

            if (configFile.exists()) {
                BufferedReader input = new BufferedReader(new FileReader(
                        configFile));

                String line;
                while ((line = input.readLine()) != null) {
                    if (line.matches("LANG:\\t\\t.+;"))
                        lang = Lang.getValueOf(line.substring(7,
                                line.lastIndexOf(";")));
                    if (line.matches("FORMAT:\\t\\t.+;"))
                        format = Format.valueOf(line.substring(9,
                                line.lastIndexOf(";")));
                    if (line.matches("CHARSET:\\t.+;"))
                        subtitlesCharset = SubtitlesCharset.getValueOf(line
                                .substring(9, line.lastIndexOf(";")));
                    if (line.matches("LAST_DIR:\\t.+;")) {
                        File tmp = new File(line.substring(10,
                                line.lastIndexOf(";")));
                        if (tmp.exists())
                            lastUsedDirectory = tmp;
                    }
                }

                input.close();
            } else {
                FileWriter output = new FileWriter(configFile);
                output.write("LANG:\t\tNULL;\nFORMAT:\t\tNULL;\nCHARSET:\tNULL;\nLAST_DIR:\tNULL;");
                output.close();

                setLang(Lang.PL);
                setFormat(Format.MicroDVD);
                setSubtitlesCharset(SubtitlesCharset.Windows);
                setLastUsedDirectory(new File(System.getProperty("user.home")));
            }

        } catch (IOException e) {
            logger.warning(e.toString());
            e.printStackTrace();
        }
    }

    public static Global getInstance() {
        if (instance == null)
            instance = new Global();

        return instance;
    }

    private Lang lang;
    private Format format;
    private SubtitlesCharset subtitlesCharset;
    private File lastUsedDirectory;
    private String selectedMovieFilePath = "";
    private Image icon;

    public Lang getLang() {
        return lang;
    }

    public Format getFormat() {
        return format;
    }

    public SubtitlesCharset getSubtitlesCharset() {
        return subtitlesCharset;
    }

    public File getLastUsedDirectory() {
        return lastUsedDirectory;
    }

    public String getSelectedMovieFilePath() {
        return selectedMovieFilePath;
    }

    public Image getIcon() {
        return icon;
    }

    public void setLang(Lang lang) {
        updateConfig(lang);
        this.lang = lang;
    }

    public void setFormat(Format format) {
        updateConfig(format);
        this.format = format;
    }

    public void setSubtitlesCharset(SubtitlesCharset subtitlesCharset) {
        updateConfig(subtitlesCharset);
        this.subtitlesCharset = subtitlesCharset;
    }

    public void setLastUsedDirectory(File lastUsedDirectory) {
        updateConfig(lastUsedDirectory);
        this.lastUsedDirectory = lastUsedDirectory;
    }

    public void setSelectedMovieFilePath(String selectedMovieFilePath) {
        this.selectedMovieFilePath = selectedMovieFilePath;
    }

    private void updateConfig(Object value) {
        try {
            BufferedReader input = new BufferedReader(
                    new FileReader(configFile));

            String line, config = "";
            while ((line = input.readLine()) != null) {
                if (value instanceof Lang && line.matches("LANG:\\t\\t.+;"))
                    line = "LANG:\t\t" + value.toString() + ";";
                if (value instanceof Format && line.matches("FORMAT:\\t\\t.+;"))
                    line = "FORMAT:\t\t" + value.toString() + ";";
                if (value instanceof SubtitlesCharset
                        && line.matches("CHARSET:\\t.+;"))
                    line = "CHARSET:\t" + value.toString() + ";";
                if (value instanceof File && ((File) value).isDirectory()
                        && line.matches("LAST_DIR:\\t.+;"))
                    line = "LAST_DIR:\t" + ((File) value).getCanonicalPath()
                            + ";";

                config += line + "\n";
            }

            input.close();

            FileWriter output = new FileWriter(configFile);
            output.write(config);
            output.close();

        } catch (IOException e) {
            logger.warning(e.toString());
            e.printStackTrace();
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isFileLogEnabled() {
        return handler != null;
    }

    public void setFileLogEnabled(boolean enabled) {
        if (enabled) {
            handler = new FileLogHandler("logs.log", true);
            logger.addHandler(handler);
        } else if (handler != null) {
            handler.flush();
            handler.close();
            logger.removeHandler(handler);
            handler = null;
        }
    }

}
