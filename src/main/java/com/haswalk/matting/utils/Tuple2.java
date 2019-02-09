package com.haswalk.matting.utils;

public class Tuple2<F, S> {
    private F _1;
    private S _2;

    public Tuple2(F f, S s) {
        this._1 = f;
        this._2 = s;
    }

    public F _1() {
        return _1;
    }

    public S _2() {
        return _2;
    }
}
