package com.github.kaaz.emily.discordobjects.wrappers;

import com.github.kaaz.emily.discordobjects.exception.ErrorWrapper;
import com.github.kaaz.emily.service.services.MemoryManagementService;
import com.github.kaaz.emily.util.EmoticonHelper;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Made by nija123098 on 3/4/2017.
 */
public class Reaction {// should not be saved
    private static final Map<IReaction, Reaction> MAP = new MemoryManagementService.ManagedMap<>(150000);
    public static Reaction getReaction(IReaction iReaction){
        return MAP.computeIfAbsent(iReaction, r -> new Reaction(iReaction));
    }
    static List<Reaction> getReactions(List<IReaction> reactions){
        List<Reaction> reacts = new ArrayList<>(reactions.size());
        reactions.forEach(iMessage -> reacts.add(getReaction(iMessage)));
        return reacts;
    }
    private IReaction reaction;
    private String name;
    private Reaction(IReaction reaction) {
        this.reaction = reaction;
    }

    public IReaction reaction() {
        return this.reaction;
    }

    public int getCount() {
        return reaction.getCount();
    }

    public List<User> getUsers() {
        return User.getUsers(ErrorWrapper.wrap((ErrorWrapper.Request<List<IUser>>) () -> reaction().getUsers()));
    }

    public Shard getShard() {
        return Shard.getShard(reaction().getShard());
    }

    public boolean getUserReacted(User user) {
        return reaction().getUserReacted(user.user());
    }

    public boolean getClientReacted() {
        return reaction().getClientReacted();
    }

    public String getChars(){
        return reaction().toString();
    }

    public String getName() {
        if (this.name == null){
            this.name = EmoticonHelper.getName(reaction().toString());
        }
        return this.name;
    }
}
