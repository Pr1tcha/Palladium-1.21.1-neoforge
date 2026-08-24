package net.threetag.palladium.util.property;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.log.AddonPackLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandFunctionProperty extends PalladiumProperty<CommandFunctionProperty.CommandFunctionParsing> {

    public CommandFunctionProperty(String key) {
        super(key);
    }

    @Override
    public CommandFunctionParsing fromJSON(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new CommandFunctionParsing(fixCmd(jsonElement.getAsString()));
        } else {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                lines.add(fixCmd(jsonArray.get(i).getAsString()));
            }
            return new CommandFunctionParsing(lines);
        }
    }

    private String fixCmd(String cmd) {
        while (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }

        return cmd;
    }

    @Override
    public JsonElement toJSON(CommandFunctionParsing value) {
        var array = new JsonArray();
        for (String line : value.getLines()) {
            array.add(line);
        }
        return array;
    }

    @Override
    public CommandFunctionParsing fromNBT(Tag tag, CommandFunctionParsing defaultValue) {
        if (tag instanceof ListTag listTag) {
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < listTag.size(); i++) {
                lines.add(listTag.getString(i));
            }
            return new CommandFunctionParsing(lines);
        }
        return defaultValue;
    }

    @Override
    public Tag toNBT(CommandFunctionParsing value) {
        ListTag listTag = new ListTag();
        for (String s : value.getLines()) {
            listTag.add(StringTag.valueOf(s));
        }
        return listTag;
    }

    @Override
    public CommandFunctionParsing fromBuffer(FriendlyByteBuf buf) {
        return new CommandFunctionParsing(buf.readList(FriendlyByteBuf::readUtf));
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, Object value) {
        buf.writeCollection(((CommandFunctionParsing) value).getLines(), FriendlyByteBuf::writeUtf);
    }

    @Override
    public String getString(CommandFunctionParsing value) {
        return value == null ? null : Arrays.toString(value.getLines().toArray());
    }

    public static class CommandFunctionParsing {

        private final List<String> lines;
        private CommandFunction<CommandSourceStack> commandFunction;
        private boolean error = false;

        public CommandFunctionParsing(List<String> lines) {
            this.lines = lines;
        }

        public CommandFunctionParsing(String line) {
            this.lines = Collections.singletonList(line);
        }

        public CommandFunction<CommandSourceStack> getCommandFunction(MinecraftServer server) {
            if (this.error) {
                return null;
            }

            if (this.commandFunction == null) {
                try {
                    CommandSourceStack commandSourceStack = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, 2, "", CommonComponents.EMPTY, server, null);
                    this.commandFunction = CommandFunction.fromLines(Palladium.id("parsed"), server.getCommands().getDispatcher(), commandSourceStack, this.lines);
                } catch (Exception e) {
                    AddonPackLog.error(e.getMessage());
                    this.error = true;
                }
            }
            return this.commandFunction;
        }

        public List<String> getLines() {
            return this.lines;
        }
    }

}
