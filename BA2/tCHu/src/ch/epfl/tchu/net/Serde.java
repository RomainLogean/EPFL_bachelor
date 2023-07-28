package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Romain Logean (327230)
 * @author Shuli JIA (316620)
 * @param <T>: static object
 */
public interface Serde<T> {

    /**
     *
     * @param object: object we want to turn into String
     * @return the string of the object
     */
    String serialize(T object);

    /**
     *
     * @param s: string message that we want to change into object of type C
     * @return the object of type C
     */
    T deserialize(String s);

    /**
     *
     * @param toSerialize: the function that is to serialize
     * @param toDeserialize: the function that is to deserialize
     * @return the serialized or deserialized function
     */
    static <T> Serde<T> of(Function<T, String> toSerialize, Function<String, T> toDeserialize) {
        return new Serde<>() {
            @Override
            public String serialize(T object) {
                return toSerialize.apply(object);
            }

            @Override
            public T deserialize(String s) {
                return toDeserialize.apply(s);
            }
        };
    }

    /**
     *
     * @param list: the list to serialize
     * @return the serialized list
     */
    static <T> Serde<T> oneOf(List<T> list) {
        return Serde.of(
                t -> (String.valueOf(list.indexOf(t))),
                s -> list.get(Integer.parseInt(s))
        );
    }

    /**
     *
     * @param serde: a serde
     * @param separator: String separator
     * @return a serde capable of serializing or deserializing lists of
     * serialized or deserialized values
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return Serde.of(object -> {
            List<String> serialList = new ArrayList<>();
            for (T t: object) {
                serialList.add(serde.serialize(t));
            }
            return String.join(separator, serialList);
        },s-> {
            String[] sToList = s.split(Pattern.quote(separator), -1);
            List<T> deserList = new ArrayList<>();
            if (s.length() == 0){
                return deserList;
            }
            for (String st: sToList) {
                deserList.add(serde.deserialize(st));
            }
            return deserList;
        });
    }

    /**
     *
     * @param serde: a serde
     * @param separator: String separator
     * @return a serde capable of serializing or deserializing sorted bags of
     * serialized or deserialized values
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        Serde<List<T>> serdeList = Serde.listOf(serde,separator);
        return Serde.of(object->{
            List<T> objects = object.toList();
            return serdeList.serialize(objects);
        },s->{
            List<T> desserlist = serdeList.deserialize(s);
            return SortedBag.of(desserlist);
        });
    }
}
