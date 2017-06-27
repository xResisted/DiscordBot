package com.github.kaaz.emily.fun.meme;

import com.github.kaaz.emily.command.AbstractCommand;
import com.github.kaaz.emily.command.annotations.Command;
import com.github.kaaz.emily.discordobjects.helpers.MessageMaker;
import com.github.kaaz.emily.launcher.Reference;
import com.github.kaaz.emily.util.FormatHelper;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Made by nija123098 on 6/4/2017.
 */
public class MemeTypesCommand extends AbstractCommand {
    static final Set<String> MEME_TYPES = new ConcurrentHashSet<>();
    public MemeTypesCommand() {
        super(MemeCommand.class, "types", null, null, "list", "List of all valid types");
        loadMemeTypes();
    }
    @Command
    public static void command(MessageMaker maker){
        if (MEME_TYPES.isEmpty()) loadMemeTypes();
        maker.appendRaw(FormatHelper.makeTable(new ArrayList<>(MEME_TYPES)));
    }
    static void loadMemeTypes() {
        try {
            Document document = Jsoup.connect("https://memegen.link/").userAgent(Reference.USER_AGENT).get();
            if (document != null) {
                Elements fmls = document.select(".js-meme-selector option");
                if (!fmls.isEmpty()) {
                    for (Element fml : fmls) MEME_TYPES.add(fml.val().toLowerCase());
                }
            }
        } catch (IOException e) {e.printStackTrace();}
    }
}
