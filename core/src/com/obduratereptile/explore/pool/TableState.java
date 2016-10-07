package com.obduratereptile.explore.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;

/**
 * Created by Marc on 10/4/2016.
 */
public class TableState {
    public String name = ""; // the title of this setup
    public ArrayMap<String, Matrix4> balls;
    public String note = ""; // directions for this setup
    // TODO: add shot/cuestick setup

    public TableState() {
        balls = new ArrayMap<String, Matrix4>();
    }

    public TableState(String name, String note) {
        balls = new ArrayMap<String, Matrix4>();
        this.name = name;
        this.note = note;
    }

    public int put(String id, Matrix4 transform) {
        return balls.put(id, transform);
    }

    public Matrix4 get(String key) {
        return balls.get(key);
    }

    public void clear() {
        balls.clear();
    }

    public void save(String filename) {
        Json js = new Json();
        js.toJson(this, Gdx.files.internal(filename));
    }

    static public TableState load(String filename) {
        Json js = new Json();
        return js.fromJson(TableState.class, Gdx.files.internal(filename));
    }
}
