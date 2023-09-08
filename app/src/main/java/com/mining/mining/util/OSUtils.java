package com.mining.mining.util;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSUtils {
    private static final String TAG = "Rom";

    /**
     * The constant ROM_MIUI.
     */
    public static final String ROM_MIUI = "MIUI";
    /**
     * The constant ROM_EMUI.
     */
    public static final String ROM_EMUI = "EMUI";
    /**
     * The constant ROM_FLYME.
     */
    public static final String ROM_FLYME = "FLYME";
    /**
     * The constant ROM_OPPO.
     */
    public static final String ROM_OPPO = "OPPO";
    /**
     * The constant ROM_SMARTISAN.
     */
    public static final String ROM_SMARTISAN = "SMARTISAN";
    /**
     * The constant ROM_VIVO.
     */
    public static final String ROM_VIVO = "VIVO";
    /**
     * The constant ROM_QIKU.
     */
    public static final String ROM_QIKU = "QIKU";

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;
    private static String sVersion;

    /**
     * Is emui boolean.
     *
     * @return the boolean
     */
    public static boolean isEmui() {
        return check(ROM_EMUI);
    }

    /**
     * Is miui boolean.
     *
     * @return the boolean
     */
    public static boolean isMiui() {
        return check(ROM_MIUI);
    }

    /**
     * Is vivo boolean.
     *
     * @return the boolean
     */
    public static boolean isVivo() {
        return check(ROM_VIVO);
    }

    /**
     * Is oppo boolean.
     *
     * @return the boolean
     */
    public static boolean isOppo() {
        return check(ROM_OPPO);
    }

    /**
     * Is flyme boolean.
     *
     * @return the boolean
     */
    public static boolean isFlyme() {
        return check(ROM_FLYME);
    }

    /**
     * Is 360 boolean.
     *
     * @return the boolean
     */
    public static boolean is360() {
        return check(ROM_QIKU) || check("360");
    }

    /**
     * Is smartisan boolean.
     *
     * @return the boolean
     */
    public static boolean isSmartisan() {
        return check(ROM_SMARTISAN);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    /**
     * Check boolean.
     *
     * @param rom the rom
     * @return the boolean
     */
    public static boolean check(String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }

        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    /**
     * Gets prop.
     *
     * @param name the name
     * @return the prop
     */
    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}
