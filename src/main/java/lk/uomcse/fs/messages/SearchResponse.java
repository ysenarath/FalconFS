package lk.uomcse.fs.messages;

import lk.uomcse.fs.models.Node;

import java.util.List;

// length SEROK no_files IP port hops filename1 filename2 ... ...
public class SearchResponse {
    private int fileCount;

    private Node node;

    private int hops;

    private List<String> filenames;
}
