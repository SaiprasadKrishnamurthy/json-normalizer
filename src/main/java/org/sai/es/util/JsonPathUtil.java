package org.sai.es.util;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.*;

public class JsonPathUtil {
    public static void main(String[] args) throws Exception {

        DualHashBidiMap<String, Set<Object>> primaryKeySecondaryValuesMap = new DualHashBidiMap<>();

        Set<Object> s = primaryKeySecondaryValuesMap.compute("a", (k, v) -> (v == null) ? new LinkedHashSet<>() : v);
        s.add("1");

        System.out.println(primaryKeySecondaryValuesMap.getKey(s));

//        String s = FileUtils.readFileToString(new File("a.json"));
//        List<String> value = JsonPath.read(s, "$._shards.total");
//        System.out.println(value);
        /*RestTemplate rs = new RestTemplate();
        String[] carrierCodes = {"BA", "AI", "EK"};
        String template = FileUtils.readFileToString(new File("b.json"));
        Random r = new Random();
        long currDate = System.currentTimeMillis();
        long nDaysAgo = 60;
        long daysAgo = currDate;

        for (int i = 0; i < nDaysAgo; i++) {
            for (int j = 0; j < 10; j++) {
                String carrierCode = carrierCodes[r.nextInt(carrierCodes.length)];
                String t = template;
                t = t.replace("__serviceCode", carrierCode);
                t = t.replace("__service", carrierCode + "" + r.nextInt(100));
                t = t.replace("__totalPaxCountAnomalies", r.nextInt(100) + "");
                t = t.replace("__paxDocNoAnomalies", r.nextInt(100) + "");
                t = t.replace("__paxIdentityInconsistencies", r.nextInt(100) + "");
                t = t.replace("__timestamp", daysAgo + "");
                System.out.println(t);
                rs.put("http://localhost:9200/bar/bar/" + i, Serializer.fromJsonString(t, Map.class));
            }
            daysAgo = daysAgo - (1000 * 60 * 60 * 24); // prev day.
        }*/
    }
}
