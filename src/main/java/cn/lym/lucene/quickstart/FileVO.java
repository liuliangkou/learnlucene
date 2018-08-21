package cn.lym.lucene.quickstart;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.io.Serializable;

public class FileVO implements Serializable {
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 修改时间
     */
    private long modified;
    /**
     * 文件大小
     */
    private long size;
    /**
     * 文件内容
     */
    private String contents;


    public FileVO(Document document) {
        for (IndexableField field : document.getFields()) {
            try {
                switch (FieldNames.valueOf(field.name())) {
                    case NAME:
                        this.name = field.stringValue();
                        break;
                    case PATH:
                        this.path = field.stringValue();
                        break;
                    case MODIFIED:
                        this.modified = field.numericValue().longValue();
                        break;
                    case SIZE:
                        this.size = field.numericValue().longValue();
                        break;
                    case CONTENTS:
                        this.contents = field.stringValue();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getModified() {
        return modified;
    }

    public long getSize() {
        return size;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "FileVO{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", modified=" + modified +
                ", size=" + size +
                ", contents='" + contents + '\'' +
                '}';
    }
}