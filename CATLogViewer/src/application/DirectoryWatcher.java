package application;
import com.log.LogEnum;
import com.log.Logger;
import com.parser.utils.Sleep;
import com.sun.nio.file.SensitivityWatchEventModifier;

import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Watches dir if creates or deletes a .csv.
 * @author alopez
 *
 */
public class DirectoryWatcher extends Task<AppController>
{
	private final static long TIMER = 500;
	private File rootFolder = new File(AppController.INPUT_CSV_PATH);
	private WatchService watcher;
	private ExecutorService executor;
	private AppController controller;

	public DirectoryWatcher(AppController mainController)
	{
		this.controller = mainController;
	}

	public AppController call() throws Exception
	{
		watcher = FileSystems.getDefault().newWatchService();
		executor = Executors.newSingleThreadExecutor();
		startRecursiveWatcher();
		return controller;
	}

	@Override
	protected void cancelled()
	{
		super.cancelled();
		cleanup();
	}

	@Override
	protected void failed()
	{
		super.failed();
		cleanup();
	}

	@Override
	protected void succeeded()
	{
		
		super.succeeded();
	}

	public void cleanup()
	{
		try
		{
			watcher.close();
		}
		catch (IOException e)
		{
			Logger.log(LogEnum.ERROR, "Error closing watcher service " + e.toString());
		}
		executor.shutdown();
	}

	private void startRecursiveWatcher() throws IOException
	{
		Logger.log(LogEnum.INFO, "Starting Recursive Watcher");

		final Map<WatchKey, Path> keys = new HashMap<>();

		Consumer<Path> register = p -> {
			if (!p.toFile().exists() || !p.toFile().isDirectory())
			{
				throw new RuntimeException("folder " + p + " does not exist or is not a directory");
			}
			try 
			{
				Files.walkFileTree(p, new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException 
					{
						Logger.log(LogEnum.INFO,"registering " + dir + " in watcher service");
						WatchKey watchKey = dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE}, SensitivityWatchEventModifier.HIGH);
						keys.put(watchKey, dir);
						return FileVisitResult.CONTINUE;
					}
				});
			}
			catch (IOException e)
			{
				throw new RuntimeException("Error registering path " + p);
			}
		};

		register.accept(rootFolder.toPath());

		executor.submit(() -> {
			while (true) {
				final WatchKey key;
				try
				{
					key = watcher.take(); // wait for a key to be available
				}
				catch (InterruptedException ex)
				{
					return;
				}

				final Path dir = keys.get(key);
				if (dir == null) {
					System.err.println("WatchKey " + key + " not recognized!");
					continue;
				}

				// ENTRY_CREATE
				key.pollEvents().stream()
				.filter(e -> (e.kind() != OVERFLOW ))
				.map(e -> ((WatchEvent<Path>) e).context())
				.forEach(p -> {
					final Path absPath = dir.resolve(p);
					if (absPath.toFile().isDirectory())
					{
						register.accept(absPath);
					}
					else
					{
						// TODO: abstract out with a template method, interface, override ?
						final File f = absPath.toFile();
						if (!f.exists()
								&& f.getName().endsWith(".csv")
								&& controller.inputComboBox.getItems().remove(f.getPath()))
						{
							Logger.log(LogEnum.INFO,"Deleted file " + f.getAbsolutePath());
						}
						else if (f.exists()
								&& f.getName().endsWith(".csv")
								&& controller.inputComboBox.getItems().add(f.getPath()))
						{
							Logger.log(LogEnum.INFO,"Created new file " + f.getAbsolutePath());
						}

					}
				});

				// IMPORTANT: The key must be reset after processed
				boolean valid = key.reset(); 
				if (!valid) {
					break;
				}
				new Sleep (TIMER, this.getClass().getSimpleName());
			}
		});
	}


}

