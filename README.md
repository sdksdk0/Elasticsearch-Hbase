# es
elasticsearch+hbase海量数据查询,支持千万数据秒回查询

博客地址：http://blog.csdn.net/sdksdk0/article/details/53966430

一、ElasticSearch和Hbase
ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。 Elasticsearch的性能是solr的50倍。

HBase – Hadoop Database，是一个高可靠性、高性能、面向列、可伸缩、
实时读写的分布式数据库
– 利用Hadoop HDFS作为其文件存储系统,利用Hadoop MapReduce来处理
HBase中的海量数据,利用Zookeeper作为其分布式协同服务
– 主要用来存储非结构化和半结构化的松散数据（列存 NoSQL 数据库）


二、需求分析&服务器环境设置
主要是做一个文章的搜索。有文章标题、作者、摘要、内容四个主要信息。效果图如下：这里样式我就没怎么设置了。。。。想要好看一点的可以自己加css。


服务器：
在3台centos7中部署，主机名为node1-node3.安装好ElasticSearch并配置好集群，
1.     解压
2.     修改config/elasticsearch.yml    (注意要顶格写，冒号后面要加一个空格)
a)      Cluster.name: tf   (同一集群要一样)
b)      Node.name： node-1  (同一集群要不一样)
c)       Network.Host: 192.168.44.137  这里不能写127.0.0.1
3.     解压安装kibana
4.     再congfig目录下的kibana.yml中修改elasticsearch.url
5.     安装插件
Step 1: Install Marvel into Elasticsearch:
bin/plugin install license
bin/plugin install marvel-agent
Step 2: Install Marvel into Kibana
bin/kibana plugin --install elasticsearch/marvel/latest
Step 3: Start Elasticsearch and Kibana
bin/elasticsearch
bin/kibana
 

启动好elasticsearch集群后，
然后启动zookeeper、hdfs、hbase。zkService.sh start  、start-all.sh、start-hbase.sh。
接下来就是剩下编码步骤了。



三、编码开发
1、首先在IntelliJ IDEA中新建一个maven工程，加入如下依赖。
[html] view plain copy print?
<dependencies>  
        <dependency>  
            <groupId>junit</groupId>  
            <artifactId>junit</artifactId>  
            <version>4.9</version>  
        </dependency>  
  
  
        <!-- spring 3.2 -->  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-context</artifactId>  
            <version>3.2.0.RELEASE</version>  
        </dependency>  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-orm</artifactId>  
            <version>3.2.0.RELEASE</version>  
        </dependency>  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-aspects</artifactId>  
            <version>3.2.0.RELEASE</version>  
        </dependency>  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-web</artifactId>  
            <version>3.2.0.RELEASE</version>  
        </dependency>  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-webmvc</artifactId>  
            <version>3.2.0.RELEASE</version>  
        </dependency>  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-test</artifactId>  
            <version>3.2.0.RELEASE</version>  
        </dependency>  
  
        <!-- JSTL -->  
        <dependency>  
            <groupId>jstl</groupId>  
            <artifactId>jstl</artifactId>  
            <version>1.2</version>  
        </dependency>  
        <dependency>  
            <groupId>taglibs</groupId>  
            <artifactId>standard</artifactId>  
            <version>1.1.2</version>  
        </dependency>  
        <!-- slf4j -->  
        <dependency>  
            <groupId>org.slf4j</groupId>  
            <artifactId>slf4j-api</artifactId>  
            <version>1.7.10</version>  
        </dependency>  
        <dependency>  
            <groupId>org.slf4j</groupId>  
            <artifactId>slf4j-log4j12</artifactId>  
            <version>1.7.10</version>  
        </dependency>  
  
        <!-- elasticsearch -->  
        <dependency>  
            <groupId>org.elasticsearch</groupId>  
            <artifactId>elasticsearch</artifactId>  
            <version>2.2.0</version>  
        </dependency>  
  
        <!-- habse -->  
        <dependency>  
            <groupId>org.apache.hbase</groupId>  
            <artifactId>hbase-client</artifactId>  
            <version>1.1.3</version>  
            <exclusions>  
                <exclusion>  
                    <groupId>com.google.guava</groupId>  
                    <artifactId>guava</artifactId>  
                </exclusion>  
            </exclusions>  
        </dependency>  
  
  
    </dependencies>  

2、Dao层
[java] view plain copy print?
private Integer id;  
private String title;  
  
private String describe;  
  
private String content;  
  
private String author;  

实现其getter/setter方法。

3、数据准备
在桌面新建一个doc1.txt文档，用于把我们需要查询的数据写入到里面，这里我只准备了5条数据。中间用tab键隔开。



4、在hbase中建立表。表名师doc，列族是cf。

public static void main(String[] args) throws Exception {
      HbaseUtils hbase = new HbaseUtils();
      //创建一张表
	hbase.createTable("doc","cf");
}

/**
 * 创建一张表
 * @param tableName
 * @param column
 * @throws Exception
 */
public void createTable(String tableName, String column) throws Exception {
   if(admin.tableExists(TableName.valueOf(tableName))){
      System.out.println(tableName+"表已经存在！");
   }else{
      HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
      tableDesc.addFamily(new HColumnDescriptor(column.getBytes()));
      admin.createTable(tableDesc);
      System.out.println(tableName+"表创建成功！");
   }
}


5、导入索引。这一步的时候确保你的hdfs和hbase以及elasticsearch是处于开启状态。
[java] view plain copy print?
@Test  
  public void createIndex() throws Exception {  
      List<Doc> arrayList = new ArrayList<Doc>();  
      File file = new File("C:\\Users\\asus\\Desktop\\doc1.txt");  
      List<String> list = FileUtils.readLines(file,"UTF8");  
      for(String line : list){  
          Doc Doc = new Doc();  
          String[] split = line.split("\t");  
          System.out.print(split[0]);  
          int parseInt = Integer.parseInt(split[0].trim());  
          Doc.setId(parseInt);  
          Doc.setTitle(split[1]);  
          Doc.setAuthor(split[2]);  
          Doc.setDescribe(split[3]);  
          Doc.setContent(split[3]);  
          arrayList.add(Doc);  
      }  
      HbaseUtils hbaseUtils = new HbaseUtils();  
      for (Doc Doc : arrayList) {  
          try {  
              //把数据插入hbase  
              hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_TITLE, Doc.getTitle());  
              hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_AUTHOR, Doc.getAuthor());  
              hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_DESCRIBE, Doc.getDescribe());  
              hbaseUtils.put(hbaseUtils.TABLE_NAME, Doc.getId()+"", hbaseUtils.COLUMNFAMILY_1, hbaseUtils.COLUMNFAMILY_1_CONTENT, Doc.getContent());  
              //把数据插入es  
              Esutil.addIndex("tfjt","doc", Doc);  
          } catch (Exception e) {  
              e.printStackTrace();  
          }  
      }  
  }  

数据导入成功之后可以在服务器上通过命令查看一下：
curl -XGET http://node1:9200/tfjt/_search



7、搜索。
在这里新建了一个工具类Esutil.java,主要用于处理搜索的。注意，我们默认的elasticsearch是9200端口的，这里数据传输用的是9300，不要写成9200了，然后就是集群名字为tf，也就是前面配置的集群名。还有就是主机名node1-node3,这里不能写ip地址，如果是本地测试的话，你需要在你的window下面配置hosts文件。

[java] view plain copy print?
public class Esutil {  
    public static Client client = null;  
  
        /** 
         * 获取客户端 
         * @return 
         */  
        public static  Client getClient() {  
            if(client!=null){  
                return client;  
            }  
            Settings settings = Settings.settingsBuilder().put("cluster.name", "tf").build();  
            try {  
                client = TransportClient.builder().settings(settings).build()  
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node1"), 9300))  
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node2"), 9300))  
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node3"), 9300));  
            } catch (UnknownHostException e) {  
                e.printStackTrace();  
            }  
            return client;  
        }  
      
      
      
      
    public static String addIndex(String index,String type,Doc Doc){  
        HashMap<String, Object> hashMap = new HashMap<String, Object>();  
        hashMap.put("id", Doc.getId());  
        hashMap.put("title", Doc.getTitle());  
        hashMap.put("describe", Doc.getDescribe());  
        hashMap.put("author", Doc.getAuthor());  
          
        IndexResponse response = getClient().prepareIndex(index, type).setSource(hashMap).execute().actionGet();  
        return response.getId();  
    }  
      
      
    public static Map<String, Object> search(String key,String index,String type,int start,int row){  
        SearchRequestBuilder builder = getClient().prepareSearch(index);  
        builder.setTypes(type);  
        builder.setFrom(start);  
        builder.setSize(row);  
        //设置高亮字段名称  
        builder.addHighlightedField("title");  
        builder.addHighlightedField("describe");  
        //设置高亮前缀  
        builder.setHighlighterPreTags("<font color='red' >");  
        //设置高亮后缀  
        builder.setHighlighterPostTags("</font>");  
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);  
        if(StringUtils.isNotBlank(key)){  
//          builder.setQuery(QueryBuilders.termQuery("title",key));  
            builder.setQuery(QueryBuilders.multiMatchQuery(key, "title","describe"));  
        }  
        builder.setExplain(true);  
        SearchResponse searchResponse = builder.get();  
          
        SearchHits hits = searchResponse.getHits();  
        long total = hits.getTotalHits();  
        Map<String, Object> map = new HashMap<String,Object>();  
        SearchHit[] hits2 = hits.getHits();  
        map.put("count", total);  
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
        for (SearchHit searchHit : hits2) {  
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();  
            HighlightField highlightField = highlightFields.get("title");  
            Map<String, Object> source = searchHit.getSource();  
            if(highlightField!=null){  
                Text[] fragments = highlightField.fragments();  
                String name = "";  
                for (Text text : fragments) {  
                    name+=text;  
                }  
                source.put("title", name);  
            }  
            HighlightField highlightField2 = highlightFields.get("describe");  
            if(highlightField2!=null){  
                Text[] fragments = highlightField2.fragments();  
                String describe = "";  
                for (Text text : fragments) {  
                    describe+=text;  
                }  
                source.put("describe", describe);  
            }  
            list.add(source);  
        }  
        map.put("dataList", list);  
        return map;  
    }  
  
//  public static void main(String[] args) {  
//      Map<String, Object> search = Esutil.search("hbase", "tfjt", "doc", 0, 10);  
//      List<Map<String, Object>> list = (List<Map<String, Object>>) search.get("dataList");  
//  }  
}  


8、使用spring控制层处理
在里面的spring配置这里就不说了，代码文末提供。
[java] view plain copy print?
@RequestMapping("/search.do")  
public String serachArticle(Model model,  
        @RequestParam(value="keyWords",required = false) String keyWords,  
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,  
        @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize){  
    try {  
        keyWords = new String(keyWords.getBytes("ISO-8859-1"),"UTF-8");  
    } catch (UnsupportedEncodingException e) {  
        e.printStackTrace();  
    }  
    Map<String,Object> map = new HashMap<String, Object>();  
    int count = 0;  
    try {  
        map = Esutil.search(keyWords,"tfjt","doc",(pageNum-1)*pageSize, pageSize);  
        count = Integer.parseInt(((Long) map.get("count")).toString());  
    } catch (Exception e) {  
        logger.error("查询索引错误!{}",e);  
        e.printStackTrace();  
    }  
    PageUtil<Map<String, Object>> page = new PageUtil<Map<String, Object>>(String.valueOf(pageNum),String.valueOf(pageSize),count);  
    List<Map<String, Object>> articleList = (List<Map<String, Object>>)map.get("dataList");  
    page.setList(articleList);  
    model.addAttribute("total",count);  
    model.addAttribute("pageNum",pageNum);  
    model.addAttribute("page",page);  
    model.addAttribute("kw",keyWords);  
    return "index.jsp";  
}  


9、页面

[java] view plain copy print?
<center>  
<form action="search.do" method="get">  
  <input type="text" name="keyWords" />  
  <input type="submit" value="百度一下">  
  <input type="hidden" value="1" name="pageNum">  
</form>  
<c:if test="${! empty page.list }">  
<h3>百度为您找到相关结果约${total}个</h3>  
<c:forEach items="${page.list}" var="bean">  
  <a href="/es/detailDocById/${bean.id}.do">${bean.title}</a>  
  <br/>  
  <br/>  
  <span>${bean.describe}</span>  
  <br/>  
  <br/>  
</c:forEach>  
  
<c:if test="${page.hasPrevious }">  
  <a href="search.do?pageNum=${page.previousPageNum }&keyWords=${kw}"> 上一页</a>  
</c:if>  
<c:forEach begin="${page.everyPageStart }" end="${page.everyPageEnd }" var="n">  
  <a href="search.do?pageNum=${n }&keyWords=${kw}"> ${n }</a>     
</c:forEach>  
  
<c:if test="${page.hasNext }">  
  <a href="search.do?pageNum=${page.nextPageNum }&keyWords=${kw}"> 下一页</a>  
</c:if>  
</c:if>  
</center>  

10、项目发布
在IntelliJ IDEA 中配置好常用的项目，这里发布名Application context名字为es，当然你也可以自定义设置。





最终效果如下：搜索COS会得到结果，速度非常快。
