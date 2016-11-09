package com.obduratereptile.explore.Mapper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

/**
 * Created by Marc on 11/8/2016.
 */

public class MyTiledMap extends TiledMap {
    public TextureRegion [][] tiles;
    public int tileWidth;
    public int tileHeight;

    public MyTiledMap(Texture t, int w, int h, int tilewidth, int tileheight, int hexsidelength) {
        super();

        tileWidth = tilewidth;
        tileHeight = tileheight;
        tiles = TextureRegion.split(t, tilewidth, tileheight);

        MapProperties props = getProperties();
        props.put("width", w);
        props.put("height", h);
        props.put("tileheight", tileheight);
        props.put("tilewidth", tilewidth);
        props.put("staggeraxis", "y");
        props.put("staggerindex", "odd");
        props.put("orientation", "hexagonal");
        props.put("hexsidelength", hexsidelength);

        generateRandomMap(w, h);
    }

    public void generateRandomMap(int w, int h) {
        MapLayers layers = getLayers();

        for (int l=0; l<1; l++) {
            TiledMapTileLayer layer = new TiledMapTileLayer(w, h, tileWidth, tileHeight);
            for (int x=0; x<w; x++) {
                for (int y=0; y<h; y++) {
                    int ty = (int)(Math.random() * tiles.length);
                    int tx = (int)(Math.random() * tiles[ty].length);
                    Cell cell = new Cell();
                    cell.setTile(new StaticTiledMapTile(tiles[ty][tx]));
                    layer.setCell(x, y, cell);
                }
            }
            layers.add(layer);
        }
    }
}
