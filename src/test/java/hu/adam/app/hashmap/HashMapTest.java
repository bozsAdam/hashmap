package hu.adam.app.hashmap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

class HashMapTest {

    private HashMap<String,Integer> hashMap;

    @BeforeEach
    void doBefore(){
         hashMap= new HashMap<>();
    }

    @AfterEach
    void doAfter(){
        hashMap = null;
    }

    @org.junit.jupiter.api.Test
    void putTest() {
        int originalSize = hashMap.size();
        hashMap.put("test",123);
        Assertions.assertEquals(originalSize+1,hashMap.size());
    }

    @org.junit.jupiter.api.Test
    void getValueTest() {
        hashMap.put("test",123);
        Assertions.assertEquals(123,hashMap.getValue("test"));
    }

    @org.junit.jupiter.api.Test
    void clearAllTest() {
        hashMap.put("test",123);
        hashMap.put("test1",123);
        hashMap.put("test2",123);
        hashMap.put("test3",123);
        hashMap.clearAll();
        Assertions.assertEquals(0,hashMap.size());
    }

    @org.junit.jupiter.api.Test
    void deleteTest() {
        hashMap.put("test",123);

        hashMap.delete("test");

        Assertions.assertFalse(hashMap.contains("test"));
    }
}