package com.dangdang.config.service.file.protocol;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;

import com.dangdang.config.service.exception.InvalidFileException;
import com.dangdang.config.service.file.FileChangeEventListener;
import com.dangdang.config.service.file.FileConfigGroup;
import com.dangdang.config.service.file.FileLocation;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class LocalFileProtocol implements Protocol {

	private WatchService watcher;

	@Override
	public final byte[] read(FileLocation location) throws InvalidFileException {
		try {
			Path path = getPath(location);
			if (!Files.isReadable(path)) {
				throw new InvalidFileException("The file is not readable.");
			}
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw new InvalidFileException(e);
		}
	}

	@Override
	public final void watch(FileLocation location, FileConfigGroup fileConfigGroup) throws InvalidFileException {
		// Register file change listener
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Path path = getPath(location);

			path.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			new Thread(new FileChangeEventListener(watcher, fileConfigGroup, path)).start();
		} catch (IOException e) {
			throw new InvalidFileException(e);
		}
	}

	protected abstract Path getPath(FileLocation location) throws InvalidFileException;

	@Override
	public void close() throws IOException {
		if (watcher != null) {
			watcher.close();
		}
	}

}
