package com.haswalk.matting.utils;

import java.util.ArrayList;
import java.util.List;

public class Intersection {

    public static <T> List<T> exec(List<T> first, List<T> second) {
        List<T> temp = new ArrayList<>(first);
        temp.removeAll(second);
        List<T> result = new ArrayList<>(first);
        result.removeAll(temp);
        return result;
    }

}
