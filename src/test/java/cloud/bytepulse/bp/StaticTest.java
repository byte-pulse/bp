package cloud.bytepulse.bp;

import cloud.bytepulse.bp.common.utils.json.JsonArr;
import cloud.bytepulse.bp.common.utils.json.JsonObj;
import cloud.bytepulse.bp.common.utils.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-15 21:45
 */
public class StaticTest {


    @Test
    public void JsonUtilsTest() {
        String jsonText = """
                {
                  "name": "Tom",
                  "age": 30,
                  "height": 1.85,
                  "isStudent": false,
                  "scores": [100, 98, 85],
                  "address": {
                    "street": "123 Main St",
                    "city": "Shanghai",
                    "zip": "200000"
                  },
                  "friends": [
                    {
                      "name": "Alice",
                      "age": 28,
                      "hobbies": ["reading", "swimming"]
                    },
                    {
                      "name": "Bob",
                      "age": 32,
                      "hobbies": ["gaming", "running"]
                    }
                  ],
                  "misc": [true, null, 42, {"key": "value"}, [1,2,3]]
                }
                """;
        JsonObj jsonObj = JsonUtils.parseJsonObj(jsonText);
        System.out.println(jsonObj);
        System.out.println(jsonObj.toPrettyString());

        // 获取基本数据
        System.out.println(jsonObj.getString("name"));
        System.out.println(jsonObj.getInt("age"));
        System.out.println(jsonObj.getDouble("height"));
        System.out.println(jsonObj.getBoolean("isStudent"));

        // 获取数组
        JsonArr scores = jsonObj.getJsonArr("scores");
        for (JsonNode jsonNode : scores) {
            System.out.println(jsonNode.asInt());
        }

        System.out.println(scores);
        System.out.println(scores.toPrettyString());

        // 获取对象
        JsonObj address = jsonObj.getJsonObj("address");
        System.out.println(address);
        System.out.println(address.getString("street"));

        // 获取对象数组
        JsonArr friends = jsonObj.getJsonArr("friends");
        for (int i = 0; i < friends.size(); i++) {
            JsonObj friend = friends.getJsonObj(i);
            System.out.println(friend);
            System.out.println(friend.getString("name"));
            System.out.println(friend.getInt("age"));
            JsonArr hobbies = friend.getJsonArr("hobbies");
            for (int j = 0; j < hobbies.size(); j++) {
                System.out.println(hobbies.getString(j));
            }
        }

        // 获取混合数据
        JsonArr misc = jsonObj.getJsonArr("misc");
        for (JsonNode jsonNode : misc) {
            System.out.println(JsonUtils.parseValue(jsonNode));
        }
    }


    @Test
    public void JsonArrTest() {
        JsonArr jsonNodes = new JsonArr();
        jsonNodes.append(1);
        jsonNodes.append(true);
        jsonNodes.append("hello");
        jsonNodes.append(new JsonObj().set("name", "Tom").set("age", 30));
        System.out.println(jsonNodes.toString());
        System.out.println(jsonNodes.toPrettyString());
    }

    @Test
    public void JsonObjTest() {
        JsonObj jsonObj = new JsonObj();
        jsonObj.set("name", "Tom");
        jsonObj.set("age", 30);
        jsonObj.set("height", 1.85)
                .set("isStudent", false)
                .set("scores", new JsonArr().append(100).append(98).append(85))
                .set("address", new JsonObj().set("street", "123 Main St").set("city", "Shanghai").set("zip", "200000"));
        System.out.println(jsonObj.toString());
        System.out.println(jsonObj.toPrettyString());
    }
}
