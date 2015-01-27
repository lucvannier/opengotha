<?xml version='1.0' encoding='ISO-8859-1'?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
                         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">

<helpset version="2.0">
	<title>GothaHelp</title>
	<maps>
		<homeID>Starting OpenGotha</homeID>
		<mapref location="map.xml"/>
	</maps>
	<view mergetype="javax.help.AppendMerge">
		<name>TOC</name>
		<label>Table of Contents</label>
		<type>javax.help.TOCView</type>
		<data>toc.xml</data>
	</view>
	<view mergetype="javax.help.AppendMerge">
		<name>Index</name>
		<label>Index</label>
		<type>javax.help.IndexView</type>
		<data>index.xml</data>
	</view>
        <presentation default="true" displayviews="true" displayviewimages="false">
            <name>MainWindow</name>
            <size width="800" height="500" />
            <location x="10" y="10" />
            <title>OpenGotha help</title>
            <image>gotha.icon</image>
        </presentation>

</helpset>
