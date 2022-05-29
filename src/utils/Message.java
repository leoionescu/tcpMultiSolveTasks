package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    private String title;
    private List<String> args = new ArrayList<>();

    public Message(String title, List<String> args) {
        this.title = title;
        this.args = args;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", args=" + args +
                '}';
    }
}
