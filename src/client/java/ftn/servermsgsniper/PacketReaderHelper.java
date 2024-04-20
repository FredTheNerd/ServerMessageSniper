package ftn.servermsgsniper;

import net.minecraft.text.*;
import net.minecraft.client.MinecraftClient;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static ftn.servermsgsniper.CustomClickEventHandler.process;
import static net.minecraft.text.ClickEvent.Action.*;

public class PacketReaderHelper {

    private static final ArrayList<Predicate<String>> auto = new ArrayList<>(); // regex of toString
    private static final HashMap<Predicate<String>, ClickEvent> option = new HashMap<>(); // translate tags, clickEvent to insert
    private static final ArrayList<Predicate<String>> ignore = new ArrayList<>();

    public static List<Text> processClickEvents(Text current){

        List<Text> out = new ArrayList<>();
        ClickEventLists events = getClickEvents(current, new ClickEventLists());
        String tmpStr = current.toString();
        for(Predicate<String> x : auto){
            if(x.test(tmpStr)){
                for(ClickEvent y: events.list){
                    System.out.println(y);
                    process(y);
                }
                return out;
            }
        }
        // non-auto
        System.out.println(events.list.size());
        if(events.list.size() == 1){
            MinecraftClient.getInstance().getToastManager().add(new AskToast(events.list.get(0)));
            System.out.println("popup");
        }
        else{
            while(events.injects-- > 0)
                out.add(MutableText.of(PlainTextContent.of(events.list.get(events.injects).getValue())).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(20 << 16 + 210 << 8 + 40)).withClickEvent(events.list.get(events.injects))));
        }
        return out;
    }

    public static ClickEventLists getClickEvents(Text current, ClickEventLists found){
        ClickEvent event = current.getStyle().getClickEvent();
        if(event != null && !checkIgnoreList(event))
            found.list.add(event);
        if(current.getContent() instanceof TranslatableTextContent content){

            // Test and add injects
            for(Map.Entry<Predicate<String>, ClickEvent> x : option.entrySet()){
                if(x.getKey().test(content.getKey())){
                    found.list.add(0, x.getValue());
                    found.injects++;
                }
            }

            // Recursion
            for(Object x : content.getArgs()){
                if(x instanceof Text y){
                    getClickEvents(y, found);
                }
            }

        }
        // Recursion john
        for(Text x : current.getSiblings()){
            getClickEvents(x, found);
        }
        return found;
    }

    private static boolean checkIgnoreList(ClickEvent event){
        if(event.getAction() == OPEN_FILE)
            return true;
        for(Predicate<String> x: ignore){
            if(x.test(event.getValue())){
                return true;
            }
        }
        return false;
    }

    static {
        // TODO: data driven
        auto.add((str) -> (Pattern.compile("chat\\.type\\.advancement\\..*").asPredicate().test(str) && !str.contains(MinecraftClient.getInstance().getSession().getUsername())));
        auto.add((str) -> (Pattern.compile("death\\..*").asPredicate().test(str) && !str.contains(MinecraftClient.getInstance().getSession().getUsername())));
        option.put(Pattern.compile("chat\\.type\\.advancement\\..*").asPredicate(), new ClickEvent(RUN_COMMAND, "gg"));
        option.put(Pattern.compile("death\\..*").asPredicate(), new ClickEvent(RUN_COMMAND, "L"));
        ignore.add(Pattern.compile("/tell .*").asPredicate());
    }
}