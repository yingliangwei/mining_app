package com.mining.mining.download.interFace;

import java.io.File;

/**
 * @Description TODO
 * @Package com.fallenangel.app.Download.InterFace
 * @Author Angel
 * @Date 02-04-2022 周五 23:48
 */
public interface OnDownload {
    void onProgressChange(int current, long max);

    void onSuccess(File file);

    void error(Exception e);
}
