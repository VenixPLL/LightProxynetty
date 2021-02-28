package pl.venixpll.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class LogUtil {

    public static final Logger logger = Logger.getLogger("Minecraft");
    private static PrintWriter printWriter;

    public static String getFileNameForLog() {
        String file;
        final Date date = new Date();
        date.setTime(System.currentTimeMillis());
        final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH-mm");

        file = sdf.format(date);
        int index = 1;
        while (new File(file).exists()) {
            file += "=" + index;
            index++;
        }
        return file + ".log";
    }

    public static final String humanReadableByteCount(long bytes, boolean si) {
        final int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static final String convertTime(final long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static final String fixColor(final String raw) {
        return raw.replace("&", "§");
    }

    public static void setupLogging(File file) throws Exception {
        if (file != null) {
            if (!file.exists()) file.createNewFile();
            printWriter = new PrintWriter(file);
        }
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {

            private final SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");

            @Override
            public String format(final LogRecord record) {

                final String level = record.getLevel() == Level.SEVERE ? "ERROR" : record.getLevel() == Level.WARNING ? "WARN" : "INFO";

                return date.format(record.getMillis()) + " [" + level + "/" + Thread.currentThread().getName() + "] " + formatMessage(record);
            }
        });

        logger.addHandler(handler);
        logger.setUseParentHandlers(false);

        System.setOut(new PrintStream(new StreamFormatter(logger, Level.INFO, file), true));
        System.setErr(new PrintStream(new StreamFormatter(logger, Level.SEVERE, file), true));
    }

    public static void printMessage(final String message, final Object... obj) {
        System.out.println(String.format(message, obj));
    }

    public static void writeLog(String log) throws IOException {
        final Date date = new Date();
        date.setTime(System.currentTimeMillis());
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss ");
        printWriter.println(sdf.format(date) + log);
        printWriter.flush();
    }
}

class StreamFormatter extends ByteArrayOutputStream {

    private final Logger logger;
    private final Level level;
    private final File file;

    public StreamFormatter(final Logger logger, final Level level, File file) {
        this.logger = logger;
        this.level = level;
        this.file = file;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            final String record = this.toString();
            super.reset();

            if (record.length() > 0 && !record.equals(System.lineSeparator())) {
                logger.log(level, record);
                if (file != null)
                    LogUtil.writeLog(record);
            }
        }
    }
}
