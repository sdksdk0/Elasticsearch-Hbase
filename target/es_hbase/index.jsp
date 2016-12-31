<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false"%>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <base href="<%=basePath%>">

  <title>My JSP 'index.jsp' starting page</title>
  <meta http-equiv="pragma" content="no-cache">
  <meta http-equiv="cache-control" content="no-cache">
  <meta http-equiv="expires" content="0">
  <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
  <meta http-equiv="description" content="This is my page">

</head>

<body>
<form action="search.do" method="get">
  <input type="text" name="keyWords" />
  <input type="submit" value="千度一下">
  <input type="hidden" value="1" name="pageNum">
</form>
<c:if test="${! empty page.list }">
<h3>千度为您找到相关结果约${total}个</h3>
<c:forEach items="${page.list}" var="bean">
  <a href="/detailDocById/${bean.id}.do">${bean.title}</a>
  <br/>
  <br/>
  <span>${bean.describe}</span>
  <br/>
  <br/>
</c:forEach>

<c:if test="${page.hasPrevious }">
  <a href="search.do?pageNum=${page.previousPageNum }&keyWords=${kw}"> 上一页</a>
</c:if>
<c:forEach begin="${page.everyPageStart }" end="${page.everyPageEnd }" var="n">
  <a href="search.do?pageNum=${n }&keyWords=${kw}"> ${n }</a> &nbsp;&nbsp;
</c:forEach>

<c:if test="${page.hasNext }">
  <a href="search.do?pageNum=${page.nextPageNum }&keyWords=${kw}"> 下一页</a>
</c:if>
</c:if>

</body>
</html>
