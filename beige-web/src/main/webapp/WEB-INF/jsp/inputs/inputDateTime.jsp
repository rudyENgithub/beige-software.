<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<tr>
  <td>
    <label for="${entity.getClass().simpleName}.${fieldName}">${srvI18n.getMsg(fieldName)}</label>
  </td>
  <td>
    <c:set var="required" value=""/>
    <c:if test="${srvOrm.tablesMap[classEntity.canonicalName].fieldsMap[fieldName].definition.contains('not null')}">
      <c:set var="required" value="required"/>
    </c:if>
    <input type="datetime-local" ${autofocus} ${required} name="${entity.getClass().simpleName}.${fieldName}" value="${utlJsp.dateTimeToIso8601(entity[fieldName])}" onchange="inputHasBeenChanged(this);"/> 
    <c:set var="autofocus" value="" scope="request"/>
  </td>
</tr>
