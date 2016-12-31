package cn.tf.es;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2016/3/7 0007.
 */
@Service
public class Index {
    @Test
    public void createIndex() throws Exception {
        List<Doc> arrayList = new ArrayList<Doc>();
        File file = new File("C:\\Users\\asus\\Desktop\\doc1.txt");
        List<String> list = FileUtils.readLines(file,"UTF8");
        for(String line : list){
            Doc Doc = new Doc();
            String[] split = line.split("\t");
            System.out.print(split[0]);
            int parseInt = Integer.parseInt(split[0].trim());
            Doc.setId(parseInt);
            Doc.setTitle(split[1]);
            Doc.setAuthor(split[2]);
            Doc.setDescribe(split[3]);
            Doc.setContent(split[3]);
            arrayList.add(Doc);
        }
        HbaseUtils hbaseUtils = new HbaseUtils();
        for (Doc Doc : arrayList) {
            try {
                //把数据插入hbase
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_TITLE, Doc.getTitle());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_AUTHOR, Doc.getAuthor());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_DESCRIBE, Doc.getDescribe());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_CONTENT, Doc.getContent());
                //把数据插入es
                Esutil.addIndex("tfjt","doc", Doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void insert(String dataPath,String indexPath) throws Exception{
        List<Doc> arrayList = new ArrayList<Doc>();
        File file = new File("C:\\Users\\asus\\Desktop\\article.txt");
        List<String> list = FileUtils.readLines(file);
        for(String line : list){
            Doc Doc = new Doc();
            String[] split = line.split("\t");
            int parseInt = Integer.parseInt(split[0].trim());
            Doc.setId(parseInt);
            Doc.setTitle(split[1]);
            Doc.setAuthor(split[2]);
            Doc.setDescribe(split[3]);
            Doc.setContent(split[3]);
            arrayList.add(Doc);
        }
        HbaseUtils hbaseUtils = new HbaseUtils();
        for (Doc Doc : arrayList) {
            try {
                //把数据插入hbase
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_TITLE, Doc.getTitle());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_AUTHOR, Doc.getAuthor());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_DESCRIBE, Doc.getDescribe());
                hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_CONTENT, Doc.getContent());
                //把数据插入es
                Esutil.addIndex("tfjt","doc", Doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
