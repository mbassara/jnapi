package pl.mbassara.jnapi.core.services.opensubtitles;

import java.util.ArrayList;

public class Value {

    private String type;
    private Object value;

    public Value(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        String result = "";
        if (type.equals("struct")) {
            for (Member member : (ArrayList<Member>) value)
                result += member + "\n";
        } else
            result = value.toString();

        return result;
    }
}
