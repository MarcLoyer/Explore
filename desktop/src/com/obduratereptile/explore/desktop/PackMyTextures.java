package com.obduratereptile.explore.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by Marc on 9/15/2016.
 */
public class PackMyTextures {

    public static void main (String[] arg) {
        // run directory should be set to android/assets in the run config
        String assetsPath = "C:/Users/Marc/Documents/AssetDevelopment/Explore/Images";
        String atlasPath = "atlas";

        TexturePacker.Settings packSettings = new TexturePacker.Settings();
        packSettings.pot = true;
        packSettings.maxWidth = 4096;
        packSettings.maxWidth = 4096;

        // I tried all the different filters, So far the best is MipMapLinearLinear or MipMap
        packSettings.filterMin = Texture.TextureFilter.MipMap;
        packSettings.filterMag = Texture.TextureFilter.MipMap;
        packSettings.flattenPaths = true;
        packSettings.combineSubdirectories = true;

        TexturePacker.processIfModified(packSettings, assetsPath, atlasPath, "textures.pack");
    }
}
