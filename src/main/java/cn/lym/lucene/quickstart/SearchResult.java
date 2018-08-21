package cn.lym.lucene.quickstart;

import org.apache.lucene.document.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResult implements Serializable {
    private int totalHits;
    private List<FileVO> files;

    public SearchResult(int totalHits) {
        this.totalHits = totalHits;
        this.files = new ArrayList<>(totalHits);
    }

    public void add(Document document) {
        this.files.add(new FileVO(document));
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<FileVO> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "totalHits=" + totalHits +
                ", files=" + files +
                '}';
    }
}