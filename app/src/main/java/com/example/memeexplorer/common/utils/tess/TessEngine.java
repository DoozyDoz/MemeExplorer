package com.example.memeexplorer.common.utils.tess;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.memeexplorer.MemeExplorerApplication;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class TessEngine {

    static final String TAG = "DBG_" + TessEngine.class.getName();

    private TessEngine(){
    }

    public static TessEngine Generate() {
        return new TessEngine();
    }

//    public String detectText(Bitmap bitmap) {
    public String detectText(String imgPath) {
        Log.d(TAG, "Initialization of TessBaseApi");
        TessDataManager.initTessTrainedData(MemeExplorerApplication.sAppContext);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String path = TessDataManager.getTesseractFolder();
        Log.d(TAG, "Tess folder: " + path);
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng");
        // 白名单
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        // 黑名单
//        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?");
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO); // only one
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD); // only one
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_ONLY); // only one
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK); // only four
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK_VERT_TEXT); // only four but gibberish
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR); // only seven but gibberish
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_COLUMN); // only one
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE); // only four gibberish
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_WORD); // only nine gibberish
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT); // only two
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT_OSD);// only two spaced
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_CIRCLE_WORD); // only nine gibberish
//        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_RAW_LINE); // only nine gibberish
        Log.d(TAG, "Ended initialization of TessEngine");
        Log.d(TAG, "Running inspection on bitmap");
//        tessBaseAPI.setImage(bitmap);
        tessBaseAPI.setImage(new File(imgPath));
//        String inspection = tessBaseAPI.getHOCRText(0);
//        String inspection = tessBaseAPI.getBoxText(0);
        String inspection = tessBaseAPI.getUTF8Text();

        Log.d(TAG, "Confidence values: " + tessBaseAPI.meanConfidence());
        Log.d(TAG, "text_0cr: " + inspection);
        tessBaseAPI.recycle();
//        tessBaseAPI.end();
        System.gc();
        return inspection;
    }

}
