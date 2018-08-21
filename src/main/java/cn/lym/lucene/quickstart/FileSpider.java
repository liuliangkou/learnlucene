package cn.lym.lucene.quickstart;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileSpider {
    private Directory directory;
    private IndexWriter indexWriter;
    private DirectoryReader indexReader;

    public FileSpider(Path docsPath) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig();
        this.directory = FSDirectory.open(docsPath);
        this.indexWriter = new IndexWriter(directory, config);
    }

    public void indexFiles(Path indexPath) throws IOException {
        this.indexFiles(indexPath, false);
    }

    private void indexFiles(Path indexPath, boolean deleteFirst) throws IOException {
        if (Files.isDirectory(indexPath)) {
            Files.walkFileTree(indexPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        indexFile(file, deleteFirst);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexFile(indexPath, deleteFirst);
        }
        this.indexWriter.commit();
    }

    public long delete(Query query) throws IOException {
        return this.indexWriter.deleteDocuments(query);
    }

    /**
     * 查询接口
     *
     * @param query   查询条件
     * @param maxDocs 最大返回结果数
     * @return
     * @throws IOException
     */
    public SearchResult search(Query query, int maxDocs) throws IOException {
        if (this.indexReader == null) {
            this.indexReader = DirectoryReader.open(this.directory);
        } else {
            this.indexReader = DirectoryReader.openIfChanged(this.indexReader);
        }

        IndexSearcher searcher = new IndexSearcher(this.indexReader);
        TopDocs topDocs = searcher.search(query, maxDocs);
        int totalHits = (int) topDocs.totalHits;
        SearchResult searchResult = new SearchResult(totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = this.indexReader.document(scoreDoc.doc);
            searchResult.add(document);
        }
        return searchResult;
    }

    public void update(Path filePath) throws IOException {
        this.indexFiles(filePath, true);
    }

    private void indexFile(Path file) throws IOException {
        this.indexFile(file, false);
    }

    /**
     * 将文件添加到索引中。
     *
     * @param file
     * @param deleteFirst 是否先从索引中删除。update索引时，此参数为true。
     * @throws IOException
     */
    private void indexFile(Path file, boolean deleteFirst) throws IOException {
        if (deleteFirst) {
            this.indexWriter.deleteDocuments(new Term(FieldNames.PATH.toString(), file.toString()));
        }

        System.out.println("indexing " + file.toString());
        Document document = new Document();
        // 文件名
        document.add(new StringField(FieldNames.NAME.toString(), file.toFile().getName(), Field.Store.YES));
        // 文件路径
        document.add(new StringField(FieldNames.PATH.toString(), file.toString(), Field.Store.YES));
        // 修改时间
        document.add(new LongPoint(FieldNames.MODIFIED.toString(), Files.getLastModifiedTime(file).toMillis()));
//        document.add(new StoredField(FieldNames.MODIFIED.toString(), Files.getLastModifiedTime(file).toMillis()));
        // 文件大小
        document.add(new LongPoint(FieldNames.SIZE.toString(), Files.size(file)));
//        document.add(new StoredField(FieldNames.SIZE.toString(), Files.size(file)));
        // 文件类型
        String type = getType(file);
        document.add(new StringField(FieldNames.TYPE.toString(), type, Field.Store.YES));
        // 内容
        if (isTextFile(type)) {
            document.add(new TextField(FieldNames.CONTENTS.toString(), new FileReader(file.toFile())));
        }
        this.indexWriter.addDocument(document);
    }

    /**
     * 判断是否是文本类型的文件。
     *
     * @param type
     * @return
     */
    private boolean isTextFile(String type) {
        return "java".equals(type) || "txt".equals(type) || "html".equals(type);
    }

    /**
     * 获得文件类型
     *
     * @param file
     * @return
     */
    private String getType(Path file) {
        String pathname = file.toString().toLowerCase();
        int index = pathname.lastIndexOf(".");
        if (index != -1) {
            return pathname.substring(index + 1);
        }
        return pathname;
    }

    /**
     * 关闭资源
     *
     * @throws IOException
     */
    public void close() throws IOException {
        IOUtils.closeQuietly(this.indexReader, this.indexWriter, this.directory);
    }
}
