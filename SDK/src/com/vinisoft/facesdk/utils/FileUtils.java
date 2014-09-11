package com.vinisoft.facesdk.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.content.Context;
import android.content.res.AssetManager;

public class FileUtils {
	public static void writeFile(String fileName, String data, boolean isAppend) {
		try {
			File file = new File(fileName);
			File folder = file.getParentFile();

			if (!folder.exists()) {
				folder.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
				LogUtils.d("writeFile - !file.exists()");
			}

			BufferedWriter buf = new BufferedWriter(new FileWriter(file, isAppend));
			buf.append(data);
			buf.newLine();
			buf.close();
		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public static void moveFile(String srcPath, String dstPath) {
		try {
			File srcFile = new File(srcPath);
			File dstFile = new File(dstPath);
			File dstFolder = dstFile.getParentFile();

			if (!dstFolder.exists()) {
				dstFolder.mkdirs();
			}

			FileInputStream input = new FileInputStream(srcFile);
			FileOutputStream output = new FileOutputStream(dstFile);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = input.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
			input.close();
			input = null;

			output.flush();
			output.close();
			output = null;

			srcFile.delete();

		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public static void clearFileContent(String path) {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(path));
			writer.print("");
			writer.close();
		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public static void deleteRecursive(File fileOrDirectory) {
		try {
			if (fileOrDirectory.isDirectory())
				for (File child : fileOrDirectory.listFiles()) {
					deleteRecursive(child);
				}
			fileOrDirectory.delete();
		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public static void deleteRecursive(String path) {
		try {
			File mFile = new File(path);

			if (mFile.isDirectory())
				for (File child : mFile.listFiles()) {
					deleteRecursive(child);
				}
			mFile.delete();
		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static void assertToFile(Context context, String assertFile, String dstFile) {
		try {
			File fileDst = new File(dstFile);
			File parent = fileDst.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}

			if (!fileDst.exists() || fileDst.length() <= 0) {
				if (fileDst.createNewFile()) {
					AssetManager assetManager = context.getAssets();
					InputStream in = assetManager.open(assertFile);
					OutputStream out = new FileOutputStream(fileDst);
					copyFile(in, out);
				}
			}

		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public static void assertToFile(Context context, String assertFile, File dstFile) {
		try {
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open(assertFile);
			OutputStream out = new FileOutputStream(dstFile);
			copyFile(in, out);

		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}
}
