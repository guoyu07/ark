<%@ page session="true" contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<jp:mondrianQuery id="query01" jdbcDriver="org.postgresql.Driver" jdbcUrl="jdbc:postgresql:biogenix?user=postgres&password=postgres" catalogUri="/WEB-INF/queries/OLAPSchema.xml">
select
  {[Infection types].[All types]} on columns,
  {[Patient].[All countries]} on rows
from Infection 
</jp:mondrianQuery>
<c:set var="title01" scope="session">
Infection summary</c:set>