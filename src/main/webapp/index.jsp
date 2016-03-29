<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8" %>
<html>
<head>

    <%--<script src="//cdn.bootcss.com/bootstrap-fileinput/4.3.1/js/fileinput.min.js"></script>--%>
    <meta charset="utf-8">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <link href="//cdn.bootcss.com/bootstrap-fileinput/4.3.1/css/fileinput.min.css" rel="stylesheet">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <%--<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>--%>
    <script src="//cdn.bootcss.com/bootstrap-fileinput/4.3.1/js/fileinput.js"></script>
    <script src="//cdn.bootcss.com/bootstrap-fileinput/4.3.1/js/fileinput_locale_zh.min.js"></script>

    <script>
        //初始化fileinput控件（第一次初始化）
        function initFileInput(ctrlName, uploadUrl) {
            var control = $('#' + ctrlName);

            control.fileinput({
                language: 'zh', //设置语言
                uploadUrl: uploadUrl, //上传的地址
                allowedFileExtensions : ['jpg', 'png','gif'],//接收的文件后缀
                showUpload: true, //是否显示上传按钮
                showCaption: true,//是否显示标题
                browseClass: "btn btn-primary", //按钮样式
                previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
            });
        }

        $(function(){
            $("#input-1a").on("fileuploaded", function (event, data, previewId, index) {
//            $("#myModal").modal("hide");
                var data = data.response;
                if (data == undefined) {
                    toastr.error('文件格式类型不正确');
                    return;
                }
                alert(data.result);
                //1.初始化表格

            });
        });



    </script>
</head>
<body>
<h2>Hello World!</h2>
<label class="control-label">Select File</label>
<input id="input-1a" type="file" class="file" data-show-preview="false">
<script>
    initFileInput("input-1a", "/test.do?subtime=1&method=doTest&param=1");
//    initFileInput("input-1a", "/TestServlet");

</script>
</body>
</html>
