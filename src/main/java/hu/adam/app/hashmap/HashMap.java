package hu.adam.app.hashmap;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashMap<K,V>{
    private int bucketSize = 16;

    // This holds all the data. Its a primitive array where every element is a Linked List.
    // They Linked List holds elements of type KeyValue
    private LinkedList<KeyValue<K, V>>[] elements = new LinkedList[bucketSize];

    public void put(K key, V value) {
        putWithoutResizing(key, value);

        // If the key already exists throw an error.
        // Make a new instance of the KeyValue class, fill it with the key, value parameters, then add it to the list.

        resizeIfNeeded();
    }

    private void putWithoutResizing(K key, V value) {
        // find out which position of the primitive array to use:
        int position = Math.abs(getHash(key)%elements.length);
        LinkedList<KeyValue<K, V>> list = getListOfPosition(key);

        if(list == null){
            list = new LinkedList<>();
            list.add(new KeyValue<K,V>(key,value));
            elements[position] = list;
        } else{
            AtomicBoolean listContains = new AtomicBoolean(false);
            list.forEach( keyValue ->{
                if(keyValue.key == key){
                    listContains.getAndSet(true);
                    keyValue.value = value;
                }
            });

            if(!listContains.get()){
                KeyValue<K,V> kvKeyValue = new KeyValue<>(key,value);
                list.add(kvKeyValue);
            }

            elements[position] = list;
        }
    }

    //returns value
    public V getValue(K key){
        // 1. Calculate the hash of the key. This defines which element to get from the "elements" array
        // 2. Find in the List in this position the KeyValue element that has this key, then return its value.
        //    If none of the items in the list has this key throw error.
        LinkedList<KeyValue<K,V>> list = getListOfPosition(key);

        return list.stream()
                .filter(keyValue -> keyValue.key.equals(key))
                .map(kvKeyValue -> kvKeyValue.value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("key not found"));

    }

    private LinkedList<KeyValue<K,V>> getListOfPosition(K key){
        return elements[Math.abs(getHash(key)%elements.length)];
    }

    public void clearAll(){
        elements = new LinkedList[bucketSize];
    };

    public void delete(K key){
        LinkedList<KeyValue<K,V>> list = getListOfPosition(key);

        elements[Math.abs(getHash(key)%elements.length)] =
                list.stream()
                .filter(kvKeyValue -> !kvKeyValue.key.equals(key))
                .collect(Collectors.toCollection(LinkedList::new));
    };

    public boolean contains(K key){
        LinkedList<KeyValue<K,V>> list = getListOfPosition(key);
        KeyValue<K, V> keyValue = list.stream()
                .filter(kvKeyValue -> kvKeyValue.key.equals(key))
                .findFirst()
                .orElse(null);

        return keyValue != null;
    }

    public int size(){
        return Stream.of(elements)
                    .filter(Objects::nonNull)
                    .mapToInt(LinkedList::size)
                    .sum();
    }

    private int getHash(K key) {
        // This function converts somehow the key to an integer between 0 and bucketSize
        // In C# GetHashCode(), in Java hashCode() is a function of Object, so all non-primitive types
        // can easily be converted to an integer.
        return key.hashCode();
    }

    private List<KeyValue<K,V>> getAllElements(){
        List<KeyValue<K,V>> allElements = new LinkedList<>();


        Stream.of(elements)
                .filter(Objects::nonNull)
                .forEach(allElements::addAll);

        return allElements;
    }

    private void resizeIfNeeded(){
    // If it holds more elements than bucketSize * 2, destroy and recreate it
    // with the double size of the elements array.
    // if it holds less elements than bucketSize / 2, destroy and recreate it
    // with half size of the elements array.

        System.out.println(bucketSize);

        if (this.size() >= bucketSize *2){
            this.bucketSize = this.bucketSize*2;
            resize();
        } else if(this.size() <= bucketSize/2){
            this.bucketSize = this.bucketSize/2;
            resize();
        }

    }

    private void resize() {
        LinkedList<KeyValue<K,V>>[] resizedElements = new LinkedList[this.bucketSize];
        List<KeyValue<K,V>> keyValues = getAllElements();
        this.elements = resizedElements;
        keyValues.forEach(kvKeyValue -> this.putWithoutResizing(kvKeyValue.key,kvKeyValue.value));
    }
}
