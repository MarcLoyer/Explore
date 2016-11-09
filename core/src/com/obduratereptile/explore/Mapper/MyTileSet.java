package com.obduratereptile.explore.Mapper;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by Marc on 11/8/2016.
 */
public class MyTileSet {
    public TextureRegion image;
    public String name;
    public ArrayMap<String, Object> properties;

    public MyTileSet(TextureRegion image, String name) {
        this.image = image;
        this.name = name;
        properties = new ArrayMap<String, Object>();
    }

    public TiledMapTileLayer.Cell createCell() {
        TiledMapTileLayer.Cell c = new TiledMapTileLayer.Cell();
        c.setTile(new StaticTiledMapTile(image));
        return c;
    }
}
