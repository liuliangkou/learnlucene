package cn.lym.lucene.quickstart;

import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

public class FileSpiderTest {
    private static FileSpider fileSpider;

    @BeforeClass
    public static void setUp() throws Exception {
        fileSpider = new FileSpider(Paths.get("F:\\files_index"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        fileSpider.close();
    }

    @Test
    public void indexFiles() throws Exception {
        fileSpider.indexFiles(Paths.get("F:\\lucene-7.4.0"));
    }

    @Test
    public void search() throws Exception {
        Query query = null;
        //查询所有文档，类似数据库的WHERE 1=1.
        query = new MatchAllDocsQuery();
        //查询类型为“jar”的文档，类似数据库的WHERE type='jar'.
        query = new TermQuery(new Term(FieldNames.TYPE.toString(), "jar"));
        //查询大小在1M~2M之间的文档，类似数据库的WHERE size BETWEEN XXX AND YYY.
        query = LongPoint.newRangeQuery(FieldNames.SIZE.toString(), 1024 * 1024, 2 * 1024 * 1024);
        //查询内容包含“lucene”的文档，类似数据库的WHERE contents LIKE '%lucene%'.
        query = new TermQuery(new Term(FieldNames.CONTENTS.toString(), "lucene"));

        SearchResult searchResult = fileSpider.search(query, 1);
        System.out.println(searchResult);
    }

    @Test
    public void delete() throws Exception {
        Query query = new TermQuery(new Term(FieldNames.TYPE.toString(), "jar"));
        fileSpider.delete(query);
        SearchResult searchResult = fileSpider.search(query, 1);
        System.out.println(searchResult);
    }

    @Test
    public void update() throws Exception {
        fileSpider.update(Paths.get("F:\\lucene-7.4.0\\README.txt"));
        Query query = new TermQuery(new Term(FieldNames.CONTENTS.toString(), "liuliangkou"));
        SearchResult searchResult = fileSpider.search(query, 1);
        System.out.println(searchResult);
    }
}