<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <html>
  <body>
  <h2 align="center"><xsl:value-of select="TournamentStandings/TournamentName"/></h2>
  <h4 align="center"><xsl:value-of select="TournamentStandings/DateTime"/></h4>
    <table border="1" align="center">
      <tr bgcolor="#9acd32">
        <th>Pl</th>
        <th>Name</th>
        <th>Rank</th>
        <th>Co</th>
        <th>Club</th>
        <th>NbW</th>

        <xsl:call-template name="for.loop">
            <xsl:with-param name="i">1</xsl:with-param>
            <xsl:with-param name="count"><xsl:value-of select="TournamentStandings/NumberOfRounds" /></xsl:with-param>
        </xsl:call-template>

        <xsl:if test="TournamentStandings/NumberOfCriteria &gt; 0">
            <th><xsl:value-of select="TournamentStandings/Criteria/@crit1"/></th>
            <xsl:if test="TournamentStandings/NumberOfCriteria &gt; 1">
                <th><xsl:value-of select="TournamentStandings/Criteria/@crit2"/></th>
                <xsl:if test="TournamentStandings/NumberOfCriteria &gt; 2">
                    <th><xsl:value-of select="TournamentStandings/Criteria/@crit3"/></th>
                    <xsl:if test="TournamentStandings/NumberOfCriteria &gt; 3">
                        <th><xsl:value-of select="TournamentStandings/Criteria/@crit4"/></th>
                    </xsl:if>
                </xsl:if>
            </xsl:if>
        </xsl:if>

      </tr>
      <xsl:for-each select="TournamentStandings/Players/Player">
      <tr>
        <td><xsl:value-of select="@pl"/></td>
        <td><xsl:value-of select="@name" /><xsl:text> </xsl:text><xsl:value-of select="@firstName"/></td>
        <td><xsl:value-of select="@rank"/></td>
        <td><xsl:value-of select="@country"/></td>
        <td><xsl:value-of select="@club"/></td>
        <td align="center"><xsl:value-of select="Score/@nbw"/></td>
        <xsl:for-each select="Rounds/Round">
            <td><xsl:value-of select="@result"/></td>
        </xsl:for-each>
        <xsl:if test="/TournamentStandings/NumberOfCriteria &gt; 0">
            <td><xsl:value-of select="Score/@crit1"/></td>
            <xsl:if test="/TournamentStandings/NumberOfCriteria &gt; 1">
                <td><xsl:value-of select="Score/@crit2"/></td>
                <xsl:if test="/TournamentStandings/NumberOfCriteria &gt; 2">
                    <td><xsl:value-of select="Score/@crit3"/></td>
                    <xsl:if test="/TournamentStandings/NumberOfCriteria &gt; 3">
                        <td><xsl:value-of select="Score/@crit4"/></td>
                    </xsl:if>
                </xsl:if>
            </xsl:if>
        </xsl:if>

      </tr>
      </xsl:for-each>
    </table>
    <h4 align="center"><xsl:value-of select="TournamentStandings/Program"/></h4>

  </body>
  </html>
</xsl:template>



<xsl:template name="for.loop">
   <xsl:param name="i"      />
   <xsl:param name="count"  />

   <!-- A loop to insert Round headers -->
   <xsl:if test="$i &lt;= $count">
      <th>R<xsl:value-of select="$i" /></th>
   </xsl:if>

   <!-- RepeatTheLoopUntilFinished-->
   <xsl:if test="$i &lt;= $count">
      <xsl:call-template name="for.loop">
          <xsl:with-param name="i">
              <xsl:value-of select="$i + 1"/>
          </xsl:with-param>
          <xsl:with-param name="count">
              <xsl:value-of select="$count"/>
          </xsl:with-param>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

</xsl:stylesheet>

