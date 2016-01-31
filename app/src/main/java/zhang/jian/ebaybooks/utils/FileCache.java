package zhang.jian.ebaybooks.utils;

import android.content.Context;

import java.io.File;
import java.net.URLEncoder;


public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        //use internal storage instead
        cacheDir = new File(context.getFilesDir(), "TTImages_cache");
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        //String filename = String.valueOf(url.hashCode());
        String filename = URLEncoder.encode(url);
        File file = new File(cacheDir, filename);
        return file;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File file : files)
            file.delete();
    }
}
