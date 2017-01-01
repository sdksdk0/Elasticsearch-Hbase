package cn.tf.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class EsController {
	static final Logger logger = LoggerFactory.getLogger(EsController.class);
	HbaseUtils hbaseUtils = new HbaseUtils();
	@Autowired
	private Index index;
	public static String path = "D:/index";
	@RequestMapping("/create.do")
	public String createIndex() throws Exception {
		index.createIndex();
		return "/create.jsp";
	}

	@RequestMapping("/search.do")
	public String serachArticle(Model model,
			@RequestParam(value="keyWords",required = false) String keyWords,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize){
		try {
			keyWords = new String(keyWords.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Map<String,Object> map = new HashMap<String, Object>();
		int count = 0;
		try {
			map = Esutil.search(keyWords,"tfjt","doc",(pageNum-1)*pageSize, pageSize);
			count = Integer.parseInt(((Long) map.get("count")).toString());
		} catch (Exception e) {
			logger.error("查询索引错误!{}",e);
			e.printStackTrace();
		}
		PageUtil<Map<String, Object>> page = new PageUtil<Map<String, Object>>(String.valueOf(pageNum),String.valueOf(pageSize),count);
		List<Map<String, Object>> articleList = (List<Map<String, Object>>)map.get("dataList");
		page.setList(articleList);
		model.addAttribute("total",count);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("page",page);
		model.addAttribute("kw",keyWords);
		return "index.jsp";
	}

	
	
	
	

	

	
	/**
	 * 查看文章详细信息
	 * @return
	 */
	@RequestMapping("/detailDocById/{id}.do")
	public String detailArticleById(@PathVariable(value="id") String id, Model modelMap) throws IOException {
		//这里用的查询是直接从hbase中查询一条字符串出来做拆分封装，这里要求protobuffer
		Doc doc = hbaseUtils.get(hbaseUtils.TABLE_NAME, id);
		doc.setAuthor(new String(doc.getAuthor().getBytes("gbk"),"UTF-8"));
		doc.setTitle(new String(doc.getTitle().getBytes("gbk"),"UTF-8"));
		doc.setContent(new String(doc.getContent().getBytes("gbk"),"UTF-8"));
		doc.setDescribe(new String(doc.getDescribe().getBytes("gbk"),"UTF-8"));
		modelMap.addAttribute("Doc",doc);
		return "/detail.jsp";
	}
	
	
	
}
