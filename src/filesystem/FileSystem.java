package filesystem;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private final Directory root;
    public FileSystem() {
        root = new Directory("/", null);
    }

    private List<Entry> resolve(String path) {
        assert path.startsWith("/");
        String[] components = path.substring(1).split("/");
        List<Entry> entries = new ArrayList<Entry>(components.length + 1);
        entries.add(root);

        Entry entry = root;
        for (String component : components) {
            if (entry == null || !(entry instanceof Directory)) {
                throw new IllegalArgumentException("invalid path:" + path);
            }
            if (!component.isEmpty()) {
                entry = ((Directory) entry).getChild(component);
                entries.add(entry);
            }
        }
        return entries;
    }

    public void mkdir(String path) {
        List<Entry> entries = resolve(path);
        if (entries.get(entries.size() - 1) != null) {
            throw new IllegalArgumentException("Directory already exists: " + path);
        }
        String[] components = path.split("/");
        final String dirName = components[components.length - 1];
        final Directory parent = (Directory) entries.get(entries.size() - 2);
        Directory newDir = new Directory(dirName, parent);
        parent.addEntry(newDir);
    }

    public void createFiles(String path) {
        assert !path.endsWith("/");
        List<Entry> entries = resolve(path);
        if (entries.get(entries.size() - 1) != null) {
            throw new IllegalArgumentException("File already exists: " + path);
        }
        final String fileName = path.substring(path.lastIndexOf("/") + 1);
        final Directory parent = (Directory) entries.get(entries.size() - 2);
        File file = new File(fileName, parent, 0);
        parent.addEntry(file);
    }

    public void delete(String path) {
        List<Entry> entries = resolve(path);
        if (entries.get(entries.size() - 1) == null) {
            throw new IllegalArgumentException("File/Directory not found: " + path);
        }
        Directory parent = (Directory) entries.get(entries.size() - 2);
        Entry toDelete = entries.get(entries.size() - 1);
        parent.deleteEntry(toDelete);
    }

    public Entry[] list(String path, boolean showFullAddress) {
        System.out.println("Listing " + path );
        List<Entry> entries = resolve(path);
        if (entries.get(entries.size() - 1) == null) {
            throw new IllegalArgumentException("Path not found: " + path);
        }
        if (entries.get(entries.size() - 1) instanceof File) {
            return null;
        }
        Directory parent = (Directory) entries.get(entries.size() - 1);
        Entry[] ret = new Entry[parent.contents.size()];
        int i = 0;
        for (Entry e : parent.getContents()) {
            ret[i++] = e;
            if (!showFullAddress) {
                System.out.println(e.name);
            } else {
                if (!path.endsWith("/"))
                    System.out.println(path + "/"+ e.name);
                else
                    System.out.println(path + e.name);
            }
        }
        return ret;
    }

    public int count() {
        return root.numberOfFiles();
    }

    public static void main(String[] args) {
        FileSystem test = new FileSystem();

        System.out.println("1 Test mkdir");
        for (int i = 0; i < 3; i++) {
            test.mkdir("/level" + 0 + "_" + i);
            String prefix = "/level" + 0 + "_" + i;
            for (int j = 0; j < 3; j ++) {
                test.mkdir(prefix + "/level" + 1 + "_" +  i + "_" + j);
            }
        }

        test.list("/", true);
        System.out.println();
        test.list("/level0_0", true);
        System.out.println();
        test.list("/level0_1", true);
        System.out.println("Total number of items : " + test.count());
        System.out.println();

        System.out.println("2 Test file creation");
        test.createFiles("/level0_file1");
        test.createFiles("/level0_0/level1_0_file1");

        test.list("/", true);
        System.out.println();
        test.list("/level0_0", true);
        System.out.println();
        test.list("/level0_1", true);
        System.out.println("Total number of items : " + test.count());
        System.out.println();

        System.out.println("3 Test file/directory deletion");
        test.delete("/level0_0");
        test.delete("/level0_file1");

        test.list("/", true);
        System.out.println();
        test.list("/level0_1", true);
        System.out.println("Total number of items : " + test.count());
    }
}
