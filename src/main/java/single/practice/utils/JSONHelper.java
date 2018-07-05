package single.practice.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huawei.devcloud.server.domain.Response;

import java.io.IOException;
import java.util.*;


public abstract class JSONHelper {



    //请求driver 时使用，对于空属性，不作序列化
    private static ObjectMapper jsonMapper = new ObjectMapper();


    //仅用于输出response 使用，对于null 属性使用空字符串代替
    private static ObjectMapper tojsonStrMapper = new ObjectMapper();

    static {
        // 配置忽视未知属性
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        // 配置接受空字符串作为null对象
        jsonMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
        // 配置接受单个值的数组
        jsonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);

        // 配置不输出null 值
        jsonMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        tojsonStrMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        tojsonStrMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS,true);
        tojsonStrMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString("");
            }
        });
    }



    //公用类不应该有public 的构造方法
    private JSONHelper(){

    }

    /**
     * 将JSONObjec对象转换成Map集合
     *
     * @see JSONHelper#
     * @param json
     * @return
     */
    public static Map<String, Object> reflect(JsonNode json) {
        return json2Map(json);
    }


    /**
     * 将JSONObjec对象转换成Map集合
     *
     * @see
     */
    public  static String jsonGetValueAsStr(JsonNode jsonNode, String key){
        if(jsonNode == null || key==null){
            return  null;
        }
        if(jsonNode.has(key)){
            return jsonNode.get(key).asText();
        }
        return null;
    }

    public  static Map<String,Object> jsonGetValueAsMap(JsonNode jsonNode, String key){
        if(jsonNode == null || key==null){
            return  null;
        }
        if(jsonNode.has(key)){
            JsonNode value = jsonNode.get(key);
            return jsonMapper.convertValue(value, new TypeReference<Map<String,Object>>() {});
        }else {
            return  new HashMap<>();
        }
    }

    public static List jsonGetValueAsList(JsonNode jsonNode, String key){
        if(jsonNode == null || key==null){
            return  null;
        }
        if(jsonNode.has(key)){
            JsonNode value = jsonNode.get(key);
            return jsonMapper.convertValue(value, List.class);
        }else {
            return  new ArrayList<>();
        }
    }


    public static JsonNode parseJson(String jsonStr) throws IOException {
        return jsonMapper.readTree(jsonStr);
    }

    /**
     * 不输出为null的值
     * @param obj
     * @return
     */
    public static String toJsonStr(Object obj)  {
        try {
            return jsonMapper.writeValueAsString(obj);
        }catch (IOException e){
            throw  new RuntimeException(e);
        }
    }

    /**
     * 把null值统一转换为空字符串
     * @param obj
     * @return
     * @throws IOException
     */
    public static String toJsonStrWithEmptyStr(Object obj)  {
        try {
            return tojsonStrMapper.writeValueAsString(obj);
        }catch (IOException e){
            throw  new RuntimeException(e);
        }
    }

    public static  <T> T convertValueWithEmptyStr(Object fromValue, Class<T> toValueType) {
        return tojsonStrMapper.convertValue(fromValue,toValueType);
    }

    public static  <T> T convertValueWithEmptyStr(Object fromValue, TypeReference toValueTypeRef)  {
        return tojsonStrMapper.convertValue(fromValue,toValueTypeRef);
    }


    public static Map<String,Object> toMap(JsonNode jsonNode){
        if(jsonNode !=null &&!jsonNode.isNull()) {
            return jsonMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
        }else{
            return  new HashMap<>();
        }
    }

    public static Map<String,Object> toMap(String jsonStr) throws IOException {
        return jsonMapper.readValue(jsonStr,new TypeReference<Map<String,Object>> (){});
    }

    public static List toList(String jsonStr,TypeReference typeReference ) throws IOException {
        if(jsonStr==null||jsonStr.isEmpty()){
            return new ArrayList();
        }
        return jsonMapper.readValue(jsonStr,typeReference);
    }

    public static JsonNode convertValueToJsonNode(Object obj){
        return jsonMapper.convertValue(obj,JsonNode.class);
    }


    public static  <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return jsonMapper.convertValue(fromValue,toValueType);
    }

    public static  <T> T convertValue(Object fromValue, TypeReference toValueTypeRef)  {
        return jsonMapper.convertValue(fromValue,toValueTypeRef);
    }

    public static Map<String, Object> json2Map(JsonNode jsonObject) {
        Map<String, Object> resultMap = new HashMap<>();
        Iterator<String> iterator = jsonObject.fieldNames();

        while (iterator.hasNext()) {
            String key =  iterator.next();
            JsonNode value = jsonObject.get(key);
            if(value==null){
                return resultMap;
            }
            if(value.isValueNode()){
                resultMap.put(key,parseValue(value));
            }else if (value.isArray()){
                resultMap.put(key,json2List(value));
            }else{
                Map<String,Object> obj = json2Map(value);
                resultMap.put(key,obj);
            }
        }
        return resultMap;
    }

    public static List<Object> json2List(JsonNode jsonArray) {
        List<Object> list = new ArrayList<Object>();
        Iterator<JsonNode> it = jsonArray.iterator();
        while( it.hasNext()){
            Map<String,Object> child = json2Map(it.next());
            if (child.keySet().size() == 1 && child.keySet().contains("")){
                list.add(child.get(""));
            }else{
                list.add(child);
            }
        }
        return list;
    }

    private static Object parseValue(JsonNode valueNode){
        if(valueNode.isTextual()){
            return valueNode.asText();
        }else if(valueNode.isInt()){
            return valueNode.asInt();
        }else if(valueNode.isBigInteger() || valueNode.isLong()){
            return valueNode.asLong();
        }else if(valueNode.isFloatingPointNumber() || valueNode.isDouble()){
            return valueNode.asDouble();
        }else if(valueNode.isBoolean()){
            return valueNode.asBoolean();
        }
        return valueNode.asText();
    }

    public static JsonNode createJsonNode(){
        return jsonMapper.createObjectNode();
    }

    public static ObjectNode createObjectNode(){
        return jsonMapper.createObjectNode();
    }

    public static ArrayNode createArrayNode(){
        return jsonMapper.createArrayNode();
    }


    public static String response2String(Response response){
        ObjectNode reslut = JSONHelper.convertValue(response, ObjectNode.class);
        if(reslut.path("error").isNull()){
            reslut.remove("error");
        }

        return toJsonStrWithEmptyStr(reslut);
    }


}


