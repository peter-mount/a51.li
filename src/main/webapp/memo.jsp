<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>
            <c:choose>
                <c:when test="${empty memo.title}">Untitled memo</c:when>
                <c:otherwise>${memo.title}</c:otherwise>
            </c:choose>
        </title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="//a51.li/a51.css" />

        <%-- Place this tag in your head or just before your close body tag. --%>
        <%--
        <script src="https://apis.google.com/js/platform.js" async defer></script>
        --%>
    </head>
    <body>
        <%--
        <div id="fb-root"></div>
        <script>(function (d, s, id) {
                var js, fjs = d.getElementsByTagName(s)[0];
                if (d.getElementById(id))
                    return;
                js = d.createElement(s);
                js.id = id;
                js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.0";
                fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));</script>
        --%>
        <div id="main-outer">
            <div id="main-body">
                <div id="main-content">
                    <div class="memo">
                        <div>
                            <div class="memologo">
                                <c:choose>
                                    <c:when test="${empty user.homepage or not user.enabled}">
                                        <img src="${user.logo}" title="${user.username}" alt="${user.username}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${user.homepage}">
                                            <img src="${user.logo}" title="${user.username}" alt="${user.username}"/>
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="memotitle">
                                <h1>
                                    <c:choose>
                                        <c:when test="${empty memo.title}">Untitled memo</c:when>
                                        <c:otherwise>${memo.title}</c:otherwise>
                                    </c:choose>
                                </h1>
                                <c:if test="${memo.shareable}">
                                    <div class="memometa">
                                        Posted by
                                        <c:choose>
                                            <c:when test="${empty user.homepage or not user.enabled}">${user.username}</c:when>
                                            <c:otherwise><a href="${user.homepage}">${user.username}</a></c:otherwise>
                                        </c:choose>
                                        on <fmt:formatDate type="both" value="${url.timestamp}" dateStyle="long" timeStyle="short"/>
                                    </div>
                                    <%--
                                    <div class="memometa">&nbsp;</div>
                                    <div class="memometa">
                                        Viewed
                                        ${count.lastWeek} times in last 7 days,
                                        ${count.lastMonth} this month,
                                        ${count.total} times in total.
                                    </div>
                                    --%>
                                    <c:if test="${not empty memo.expires}">
                                        <div class="memometa">&nbsp;</div>
                                        <div class="memometa">This memo will self destruct on <fmt:formatDate type="both" value="${memo.expires}" dateStyle="long" timeStyle="short"/></div>
                                    </c:if>
                                </c:if>
                            </div>
                            <div class="clear"></div>
                        </div>
                        <div class="share">
                            <c:if test="${memo.shareable}">
                                <a href="https://twitter.com/share" class="twitter-share-button" data-via="ProjectArea51" data-related="peter_mount">Tweet</a>
                                <script>
                                    !function (d, s, id) {
                                        var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/.test(d.location) ? 'http' : 'https';
                                        if (!d.getElementById(id)) {
                                            js = d.createElement(s);
                                            js.id = id;
                                            js.src = p + '://platform.twitter.com/widgets.js';
                                            fjs.parentNode.insertBefore(js, fjs);
                                        }
                                    }(document, 'script', 'twitter-wjs');
                                </script>
                                <%--
                                Url: <a href="${url.url}">${url.url}</a>
                                    <div class="g-plus" data-action="share"></div>
                                    <div class="fb-share-button" data-href="${url.url}"></div>
                                --%>
                            </c:if>
                        </div>
                        <div class="memotext ${memo.memoType.cssClass}">${memo.text}</div>
                    </div>
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
