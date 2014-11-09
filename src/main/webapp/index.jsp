<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Area51 Short URLs</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="a51.css" />

    </head>
    <body>
        <div id="main-outer">
            <div id="main-body">
                <div id="main-content">
                    <h1>Welcome to the Area51 Short URL Service</h1>
                    <div class="left">
                        <img src="//area51.onl/images/area51logo.png" title="Area51" alt="Area51"/>
                    </div>
                    <div class="right">
                        <a href="http//www.worldipv6launch.org"><img src="//area51.onl/images/ipv6launch.png" title="IPv6 enabled" alt="IPv6 enabled"/></a>
                    </div>
                    <p class="clear">
                        This is the dedicated short url service for <a href="//area51.onl/">Area51</a> projects.
                    </p>
                    <h2>FAQ</h2>
                    <h3>URL Format</h3>
                    <p>
                        All url's on this service begin with http://area51.onl/ and followed by one or more digits or letters
                        forming the short URL ID. Note: This ID is case insensitive.
                    </p>
                    <h3>Why not use an existing third party service like <a href="//bit.ly/">bit.ly</a></h3>
                    <p>
                        I used to use bit.ly however I wanted an easy way to generate my own pragmatically and to ensure
                        that I'm supporting IPv6 which all new projects fully support as standard.
                    </p>
                    <h3>Is this service available to non Area51 projects?</h3>
                    <p>No it is not. This is partly due to spam on my original short url service from a few years ago.</p>
                    <p>This may change but don't hold your breath!</p>
                    <h3>Is the source code available?</h3>
                    <p>The source is available over on <a href="/1h">GitHub</a>.</p>
                </div>
            </div>
            <div id="footer">
                <div id="footer-outer">
                    <div id="footer-inner">
                        <div id="footer-left">
                            <a href="//a51.li/">a51.li</a>
                            &copy;2014<c:if test="${requestScope.year>2014}">${requestScope.year}</c:if>
                            Peter Mount, All Rights Reserved. Content &copy; the respected publishers.
                        </div>
                        <div id="footer-right">Page generated ${requestScope.pageGenerated}</div>
                        <div id="footer-center"></div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
