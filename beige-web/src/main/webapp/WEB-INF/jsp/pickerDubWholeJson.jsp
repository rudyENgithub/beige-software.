<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="fltOrdPrefix" value="fltordPD" scope="request"/>
<c:set var="namePlace" value="pickersPlaceDub" scope="request"/>
<c:set var="nameRendererList" value="pickerDubJson" scope="request"/>
<c:set var="prefixFilterOrderForm" value="${namePlace}${param.nameEntity}" scope="request"/>
<c:import url="/WEB-INF/jsp/pickerWhole.jsp" varReader="rdEntities" charEncoding="UTF-8">
{"multiTargetResponse":
  [{"nameTarget": "${namePlace}${param.nameEntity}", "content": "${utlJsp.toJsonStringAndClose(rdEntities)}",
    "nameTargetParent": "${namePlace}", "javascript": "${utlJsp.toJsonString(javascript)}"}]
}
</c:import>
