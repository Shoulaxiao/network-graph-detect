<%--
  Created by IntelliJ IDEA.
  User: shoulaxiao
  Date: 20-5-13
  Time: 上午9:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div class="row">
    <div class="col-md-8">.
        <form action="/api/data/launch" method="post" enctype="multipart/form-data">
            file1:<input type="file" name="files"/>
            <input type="submit" value="上传"/>
        </form>
    </div>
</div>
</body>
</html>
