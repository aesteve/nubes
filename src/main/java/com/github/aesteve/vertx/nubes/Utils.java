package com.github.aesteve.vertx.nubes;

import java.util.*;

public class Utils extends io.vertx.ext.web.impl.Utils{
    //implemented getSortedAcceptableMimeTypes from older versions
    public static List<String> getSortedAcceptableMimeTypes(String acceptHeader) {
        // accept anything when accept is not present
        if (acceptHeader == null) {
            return Collections.emptyList();
        }

        // parse
        String[] items = acceptHeader.split(" *, *");
        // sort on quality
        Arrays.sort(items, ACCEPT_X_COMPARATOR);

        List<String> list = new ArrayList<>(items.length);

        for (String item : items) {
            // find any ; e.g.: "application/json;q=0.8"
            int space = item.indexOf(';');

            if (space != -1) {
                list.add(item.substring(0, space));
            } else {
                list.add(item);
            }
        }

        return list;
    }


    private static final Comparator<String> ACCEPT_X_COMPARATOR = new Comparator<String>() {
        float getQuality(String s) {
            if (s == null) {
                return 0;
            }

            String[] params = s.split(" *; *");
            for (int i = 1; i < params.length; i++) {
                String[] q = params[1].split(" *= *");
                if ("q".equals(q[0])) {
                    return Float.parseFloat(q[1]);
                }
            }
            return 1;
        }

        @Override
        public int compare(String o1, String o2) {
            float f1 = getQuality(o1);
            float f2 = getQuality(o2);
            if (f1 < f2) {
                return 1;
            }
            if (f1 > f2) {
                return -1;
            }
            return 0;
        }
    };

}
