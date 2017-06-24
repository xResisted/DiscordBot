package com.github.kaaz.emily.util;

import com.github.kaaz.emily.launcher.BotConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Made by nija123098 on 6/7/2017.
 */
public class YTDLHelper {
    public static boolean download(String url, String id, String format){
        if (!URLHelper.isValid(url)) return false;
        String location = BotConfig.AUDIO_PATH + id;
        List<String> commands = new ArrayList<>();
        ProcessBuilder builder = new ProcessBuilder();
        commands.add(BotConfig.YT_DL_PATH);
        commands.add("--no-check-certificate");
        commands.add("--extract-audio");//-x
        commands.add("--audio-format");
        commands.add(format);
        commands.add("--audio-quality");
        commands.add("0");
        commands.add("--prefer-ffmpeg");
        commands.add("--max-filesize");
        commands.add("128m");
        commands.add("--exec");
        commands.add("mv {} " + BotConfig.AUDIO_PATH);//-hide_banner -i input.m4a -c:a copy
        commands.add("--output");
        commands.add(location + ".%(ext)s");
        commands.add(url);
        builder.command(commands);
        builder.redirectErrorStream(true);
        boolean ret = true;
        try {
            File file = new File(location + "." + format);
            file.getParentFile().mkdirs();
            //file.createNewFile();
            Process process = builder.start();
            new Gobler(process.getInputStream(), System.out).start();
            new Gobler(process.getErrorStream(), System.err).start();
            if (!process.waitFor(2, TimeUnit.MINUTES)){
                if (file.exists()) file.delete();
                ret = false;
            }
            process.destroy();
        } catch (IOException | InterruptedException e) {
            Log.log("Error while downloading", e);
            ret = false;
        } finally {
            File malformed = new File(location + ".%(ext)s");
            if (malformed.exists()) malformed.delete();
        }
        return ret;
    }
    public static class Gobler extends Thread {
        private InputStream stream;
        private PrintStream printStream;
        private Gobler(InputStream stream, PrintStream printStream) {
            this.stream = stream;
            this.printStream = printStream;
        }
        @Override
        public void run(){
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String in;
            try {
                while ((in = reader.readLine()) != null){
                    this.printStream.println(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}