package com.umair_adil.utils;

import java.io.File;

/**
 * Created by Umair_Adil on 23/07/2016.
 */
public class Utils {

    public static Boolean isSupportedFormat(File f) {
        String ext = getFileExtension(f);
        if (ext == null) return false;
        try {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    public static String getFileExtension(File f) {
        int i = f.getName().lastIndexOf('.');
        if (i > 0) {
            return f.getName().substring(i + 1);
        } else
            return null;
    }

    public enum SupportedFileFormat {
        AAC("aac"),
        MP4("mp4");

        private String filesuffix;

        SupportedFileFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }
}
