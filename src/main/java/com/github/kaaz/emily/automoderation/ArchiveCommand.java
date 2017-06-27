package com.github.kaaz.emily.automoderation;

import com.github.kaaz.emily.command.AbstractCommand;
import com.github.kaaz.emily.command.ModuleLevel;
import com.github.kaaz.emily.command.annotations.Argument;
import com.github.kaaz.emily.command.annotations.Command;
import com.github.kaaz.emily.discordobjects.helpers.MessageMaker;
import com.github.kaaz.emily.discordobjects.wrappers.Channel;
import com.github.kaaz.emily.discordobjects.wrappers.Message;
import com.github.kaaz.emily.exeption.DevelopmentException;
import com.github.kaaz.emily.util.FileHelper;
import com.github.kaaz.emily.util.Time;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 6/6/2017.
 */
public class ArchiveCommand extends AbstractCommand {
    public ArchiveCommand() {
        super("archive", ModuleLevel.ADMINISTRATIVE, null, null, "Archives all data to a txt file");
    }
    @Command
    public void command(@Argument Time time, MessageMaker maker, Channel channel){
        List<Message> messages = channel.getMessagesTo(time == null ? 0 : System.currentTimeMillis() - time.timeUntil());
        List<String> strings = new ArrayList<>(messages.size());
        messages.forEach(message -> {
            String content = message.getContent();
            while (content.contains("\n")){
                content = content.replace("\n", "     ");
            }
            strings.add(message.getCreationDate().format(DateTimeFormatter.ISO_DATE_TIME).replace("T", " | ") + " " + message.getAuthor().getNameAndDiscrim() + ": " + content);
        });
        try {
            File file = FileHelper.getTempFile("archive", "txt");
            Files.write(Paths.get(file.getPath()), Lists.reverse(strings));
            maker.withFile(file);
        } catch (IOException e) {
            throw new DevelopmentException(e);
        }
    }
}
