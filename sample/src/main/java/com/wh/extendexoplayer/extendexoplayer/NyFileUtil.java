package com.wh.extendexoplayer.extendexoplayer;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NyFileUtil {
    private static final String TAG = "mFileUtil";
    public static final String VIDEO_EXT = "webm_mov_mp4_mpg_mpeg_mts_m2ts_avi_vob_m3u8_flv_mpa_mkv";
    public static final String SPECIAL_EXT = "mts_m2ts_avi";
    public static final String IMAGE_EXT = "jpg_cr2_rw2_tif_gif_bmp_tiff_png_pic_pct_mdi";
    public static final String AUDIO_EXT = "mp3_aac_mka_adp_au_m2a_m3a_oga_snd_m4a";
    public static final String DOCUMENT_EXT = "txt_doc_docx__pdf_ppt_pptx_xls_xlsx_epub";
    public static final String MUPDF_EXT = "pdf_epub_png_jpg_bmp_tiff_gif_svg_cbz_cbr_xps";
    public static final String MEDIA_EXT = VIDEO_EXT + IMAGE_EXT + AUDIO_EXT;

    /*
            MIME_TYPES.put("cgm", "image/cgm");
        MIME_TYPES.put("btif", "image/prs.btif");
        MIME_TYPES.put("dwg", "image/vnd.dwg");
        MIME_TYPES.put("dxf", "image/vnd.dxf");
        MIME_TYPES.put("fbs", "image/vnd.fastbidsheet");
        MIME_TYPES.put("fpx", "image/vnd.fpx");
        MIME_TYPES.put("fst", "image/vnd.fst");
        MIME_TYPES.put("mdi", "image/vnd.ms-mdi");
        MIME_TYPES.put("npx", "image/vnd.net-fpx");
        MIME_TYPES.put("xif", "image/vnd.xiff");
        MIME_TYPES.put("pct", "image/x-pict");
        MIME_TYPES.put("pic", "image/x-pict");

        MIME_TYPES.put("adp", "audio/adpcm");
        MIME_TYPES.put("au", "audio/basic");
        MIME_TYPES.put("snd", "audio/basic");
        MIME_TYPES.put("m2a", "audio/mpeg");
        MIME_TYPES.put("m3a", "audio/mpeg");
        MIME_TYPES.put("oga", "audio/ogg");
        MIME_TYPES.put("spx", "audio/ogg");
        MIME_TYPES.put("aac", "audio/x-aac");
        MIME_TYPES.put("mka", "audio/x-matroska");

        MIME_TYPES.put("jpgv", "video/jpeg");
        MIME_TYPES.put("jpgm", "video/jpm");
        MIME_TYPES.put("jpm", "video/jpm");
        MIME_TYPES.put("mj2", "video/mj2");
        MIME_TYPES.put("mjp2", "video/mj2");
        MIME_TYPES.put("mpa", "video/mpeg");
        MIME_TYPES.put("ogv", "video/ogg");
        MIME_TYPES.put("flv", "video/x-flv");
        MIME_TYPES.put("mkv", "video/x-matroska");
     */

    public static String StringAfter(String str, String substr){
        return str.substring(str.indexOf(substr) + substr.length());
    }

    public static String removespace(String inStr) {
        return inStr.replaceAll(" ", "");
    }

    public static String getTypeFromName(String name) {
        final int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = name.substring(lastDot + 1).toLowerCase();
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }

    public static boolean isVideo(String url) {
        String lExt = getFileExtension(url);
        if (lExt != null) return (VIDEO_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean isSpecialMedia(String url) {
        String lExt = getFileExtension(url);
        if (lExt != null) return (SPECIAL_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean isVideoFile(String path) {
        try {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        } catch (Throwable e) {
            Log.e("ExceptionInIsVideo", "isVideoFile: " + e);
            return false;
        }
    }


    public static boolean isImage(String url) {
        if (url == null) return false;
        String lExt = getFileExtension(url);
        if (lExt != null) return (IMAGE_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean isAudio(String url) {
        String lExt = getFileExtension(url);
        if (lExt != null) return (AUDIO_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean isMedia(String url) {
        String lExt = getFileExtension(url);
        if (lExt != null) return (MEDIA_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean isDocument(String url) {
        String lExt = getFileExtension(url);
        if (lExt != null) return (DOCUMENT_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean isMuPdf(String url) {
        String lExt = getFileExtension(url);
        if (lExt != null) return (MUPDF_EXT.contains(lExt.toLowerCase()));
        else return false;
    }

    public static boolean hasFile(File file) {
        return file != null
                && file.exists()
                && file.lastModified() > 0
                && file.length() > 0;
    }

    public static boolean hasFile(String url) {
        if (isOnline(url)) return false;
        File file = new File(url);
        return hasFile(file);
    }




//------------------------------------------------------

    /**
     * <p>Converts the given Structured Access Framework Uri (<code>"content:…"</code>) into an
     * input/output url that can be used in FFmpeg and FFprobe commands.
     *
     * <p>Requires API Level >= 19. On older API levels it returns an empty url.
     *
     * @return input/output url that can be passed to FFmpegKit or FFprobeKit
     */
    private static String getSafParameter(final Context context, final Uri uri, final String openMode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.i(TAG, String.format("getSafParameter is not supported on API Level %d", Build.VERSION.SDK_INT));
            return "";
        }
        SparseArray<ParcelFileDescriptor> pfdMap = new SparseArray<>();
        String displayName = "unknown";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Failed to get %s column for %s.%s", DocumentsContract.Document.COLUMN_DISPLAY_NAME, uri.toString(), "Exceptions.getStackTraceString(t)"));
        }


        int fd = -1;
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, openMode);
            fd = parcelFileDescriptor.getFd();
            pfdMap.put(fd, parcelFileDescriptor);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Failed to obtain %s parcelFileDescriptor for %s.%s", openMode, uri.toString(), "Exceptions.getStackTraceString(t)"));
        }

        // workaround for https://issuetracker.google.com/issues/162440528: ANDROID_CREATE_DOCUMENT generating file names like "transcode.mp3 (2)"
        if (displayName.lastIndexOf('.') > 0 && displayName.lastIndexOf(' ') > displayName.lastIndexOf('.')) {
            String extension = displayName.substring(displayName.lastIndexOf('.'), displayName.lastIndexOf(' '));
            displayName += extension;
        }
        // spaces can break argument list parsing, see https://github.com/alexcohn/mobile-ffmpeg/pull/1#issuecomment-688643836
        final char NBSP = (char) 0xa0;
        return "saf:" + fd + "/" + displayName.replace(' ', NBSP);
    }

    /**
     * <p>Converts the given Structured Access Framework Uri (<code>"content:…"</code>) into an
     * input url that can be used in FFmpeg and FFprobe commands.
     *
     * <p>Requires API Level &ge; 19. On older API levels it returns an empty url.
     *
     * @param context application context
     * @param uri     saf uri
     * @return input url that can be passed to FFmpegKit or FFprobeKit
     */
    public static String getSafParameterForRead(final Context context, final Uri uri) {
        return getSafParameter(context, uri, "r");
    }

    /**
     * <p>Converts the given Structured Access Framework Uri (<code>"content:…"</code>) into an
     * output url that can be used in FFmpeg and FFprobe commands.
     *
     * <p>Requires API Level &ge; 19. On older API levels it returns an empty url.
     *
     * @param context application context
     * @param uri     saf uri
     * @return output url that can be passed to FFmpegKit or FFprobeKit
     */
    public static String getSafParameterForWrite(final Context context, final Uri uri) {
        return getSafParameter(context, uri, "w");
    }

    /**
     * Called by saf_wrapper from native library to close a parcel file descriptor.
     *
     * @param "fd" parcel file descriptor created for a saf uri
     *             <p>
     *             private static void closeParcelFileDescriptor(final int fd) {
     *             try {
     *             ParcelFileDescriptor pfd = pfdMap.get(fd);
     *             if (pfd != null) {
     *             pfd.close();
     *             pfdMap.delete(fd);
     *             }
     *             } catch (final Throwable t) {
     *             android.util.Log.e(TAG, String.format("Failed to close file descriptor: %d.%s", fd, Exceptions.getStackTraceString(t)));
     *             }
     *             }
     */

    // https://stackoverflow.com/questions/12285469/get-path-from-filedescriptor-in-java

    /* @param context 上下文
     * @param uri     待解析的 Uri
     * @return 真实路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String url = null;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    url = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                url = getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                url = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (uri.getScheme() != null) {
            // MediaStore (and general)
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri)) url = uri.getLastPathSegment();
                else url = getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                url = uri.getPath();
            }
        } else url = uri.toString();

        if (url == null) url = uri.toString();

        if (url.contains("%")) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToNext()) {
                String result = cursor.getString(0);
                //TODO most important
                return TextUtils.isEmpty(result) ? result : getRealPathFromURI(context, uri);
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        //TODO
        return uri.toString();
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

 /*   public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        //TODO
        return uri.toString();
    }*/

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isOnline(Uri uri) {
        String url = uri.toString().toLowerCase();
        return isOnline(url);
    }

    public static String RemoveRedundant(String url) {
        // remove redudant strings before http link
        if (url.contains("http")) url = url.substring(url.indexOf("http"));
        else if (url.contains("Http")) url = url.substring(url.indexOf("Http"));
        else if (url.contains("HTTP")) url = url.substring(url.indexOf("HTTP"));
        if (url.contains("复制")) url = url.substring(0, url.indexOf("复制"));
        if (url.contains("Copy")) url = url.substring(0, url.indexOf("Copy"));
        if (url.contains("或者")) url = url.substring(0, url.indexOf("或者"));
        if (url.contains("or use")) url = url.substring(0, url.indexOf("or use"));
        //  Log.e("reduction------------:", url);
        return url.trim();
    }

    public static Boolean isLocal(String fileName) {
        return !isOnline(fileName);
    }


    public static boolean isCasheable(Uri uri) {
        String url = uri.toString().toLowerCase();
        return (!url.contains("m3u8") && isOnline(uri));
    }


    public static String getFileExtension(String fileName) {
        String ext = null;
        if (fileName != null && fileName.lastIndexOf('.') != -1)
            ext = fileName.substring(fileName.lastIndexOf('.') + 1);
        return ext;
    }

    public static String getLastSegmentFromString(String path) {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path.contains("/")) {
            path = path.substring(path.lastIndexOf('/') + 1);
        }
        return path;
    }

    public static String getFileNameWithoutExtFromPath(String path) {

        path = NyFileUtil.getLastSegmentFromString(path);
        if (path.contains(".")) {
            path = path.substring(0, path.indexOf('.'));
        }
        return path;
    }


    public static String getParentPath(String path) {
        if (path.contains("/")) {
            path = path.substring(0, path.lastIndexOf('/') + 1);
        }
        // Log.e(TAG,"parent path"+ path);
        return path;
    }

    public static void DeleteAllFilesinDir(File parentDir) {
        List<String> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty()) {
            File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            } else {
                file.delete();
            }
        }
    }


    public static void DeleteBakFilesinDir(File parentDir) {
        List<String> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty()) {
            File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            } else if (file.getName().endsWith(".bak") ||
                    //   (file.getName().endsWith(".json") && (daysAfterFileCreation(file) > 2))){
                    file.getName().endsWith(".json")) {
                file.delete();
            }
        }
    }

    private static int daysAfterFileCreation(File file) {
        return (getCurrentJulianDay() - convertDateTOJulianDay(new Date(file.lastModified())));
    }

    private static int dayMoreinDate(Date date1, Date date2) {
        return convertDateTOJulianDay(date1) - convertDateTOJulianDay(date2);
    }

    public static int getCurrentJulianDay() {
        return convertDateTOJulianDay(new Date());
    }


    private static int convertDateTOJulianDay(Date date) {
        //  SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        //   String filename = formatter.format(now);
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDate();
        return (1461 * (year + 4800 + (month - 14) / 12)) / 4
                + (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12
                - (3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 + day - 32075;
    }


    public static void copyFileStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }

    public static boolean copyStreamToFile(InputStream in, File dest) {
        OutputStream os = null;
        boolean success = false;
        try {
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[50 * 1024 * 1024];
            int length;
            Log.e("mFilleUtil", "transfer in process...");
            while ((length = in.read(buffer)) > 0) {
                os.write(buffer, 0, length);
                Log.e("mFilleUtil", "transfered" + length);
            }

        } catch (IOException e) {
            Log.e("tag", "Failed to copy files:", e);
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                Log.e("tag", "Failed to close in:", e);
            }
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                    success = true;
                }
            } catch (IOException e) {
                Log.e("tag", "Failed to close out:", e);
            }
        }
        return success;
    }


    public static void copyFile(File source, File dest) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (IOException e) {
            Log.e("tag", "Failed to copy files:", e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                Log.e("tag", "Failed to close in:", e);
            }
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                Log.e("tag", "Failed to close out:", e);
            }
        }
    }

    public static void copyFiletoDir(String fileInPath, String dir) {
        File fileIn = new File(fileInPath);
        String fileName = NyFileUtil.getLastSegmentFromString(fileInPath);
        File fileOut = new File(dir, fileName);
        copyFile(fileIn, fileOut);
    }


    public static String timedFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHMM", Locale.ENGLISH);
        Date now = new Date();
        String filename = formatter.format(now) + ".mp4";
        return filename;
    }

    /**
     * Deletes the specified diretory and any files and directories in it
     * recursively.
     *
     * @param dir The directory to remove.
     * @throws IOException If the directory could not be removed.
     */
    public static void deleteDir(File dir)
            throws IOException {
        if (!dir.isDirectory()) {
            throw new IOException("Not a directory " + dir);
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new IOException("Unable to delete file" + file);
                }
            }
        }

        dir.delete();
    }



    public static boolean isOnline(String path) {
       // Log.e(TAG, " sSpecialSource " + path);
        return path.toLowerCase().contains("http://")
                || path.contains("https://")
                || path.contains("asset://")
                || path.contains("ftp://")
                || path.contains("mms:")
                || path.contains("rtmp:")
                || path.contains("rtsp")
                || path.contains("smb://")
                || path.contains("gdrive:")
                || path.contains("box:")
                || path.contains("dropbox:")
                || path.contains("onedrive:");
    }
}